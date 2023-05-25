/*
 * The MIT License
 *
 * Copyright 2021 mtobi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.boisestate.osp;

import java.util.Map;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author mtobi
 */
public class SeqEvo {
    
    // file paths
    final static String PFP_LABEL = "PFP"; //parameters File Path
    final static String PFP_DEFAULT = "in_parameters.txt";
    
    final static String RFP_LABEL = "RFP"; // report-file-path
    final static String RFP_DEFAULT = "out_se_report.txt";
    final static String FDFP_LABEL = "FDFP"; // fixed-domains-file-path
    final static String FDFP_DEFAULT = "in_fixedDomains.txt";
    final static String VDFP_LABEL = "VDFP"; // variable-domains-file-path
    final static String VDFP_DEFAULT = "in_variableDomains.txt";
    final static String OFP_LABEL = "ODFP"; // oligomers-file-path
    final static String OFP_DEFAULT = "in_oligomers.txt";
    
    // mutation parameters
    final static String MAXAA_LABEL = "maxAA";
    final static String MAXAA_DEFAULT = "6";
    final static String MAXCC_LABEL = "maxCC";
    final static String MAXCC_DEFAULT = "3";
    final static String MAXGG_LABEL = "maxGG";
    final static String MAXGG_DEFAULT = "3";
    final static String MAXTT_LABEL = "maxTT";
    final static String MAXTT_DEFAULT = "6";
    
    // optimization parameters
    final static String CPL_LABEL = "CPL";
    final static String CPL_DEFAULT = "100000";
    final static String GPC_LABEL = "GPC";
    final static String GPC_DEFAULT = "1";
    final static String NDPM_LABEL = "NDPM";
    final static String NDPM_DEFAULT = "1";
    final static String NL_LABEL = "NL";
    final static String NL_DEFAULT = "8";
    final static String NMPC_LABEL = "NMPC";
    final static String NMPC_DEFAULT = "2";
    
    //Scoring parameters
    final static String FS_LABEL = "FS"; // Fitness-Score
    final static String FS_DEFAULT = "Wx";
    final static String SWX_LABEL = "scoringWeightX";
    final static String SWX_DEFAULT = "10000";
    final static String intraSLC_LABEL = "intraSLC";
    final static String intraSLC_DEFAULT = "1";
    final static String intraSB_LABEL = "intraSB";
    final static String intraSB_DEFAULT = "10";
    final static String interSLC_LABEL = "interSLC";
    final static String interSLC_DEFAULT = "1";
    final static String interSB_LABEL = "interSB";
    final static String interSB_DEFAULT = "10";
    
    final Map<String,String> parameters;
    final Map<String,String> usedParameters;
    final Map<String,String> fixedDomains;
    final Map<String,String> variableDomains;
    final Map<String,String[]> oligomerDomains;
    
    final int CPL;
    final int GPC;
    final int NDPM;
    final int NL;
    final int NMPC;
    
    final NetworkFactory nf;
    final Mutator m;
    
    SeqEvo(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
        usedParameters = new TreeMap<>();
        this.parameters = parameters;
        this.fixedDomains = fixedDomains;
        this.variableDomains = variableDomains;
        this.oligomerDomains = oligomerDomains;
        
        // optimization parameters
        CPL = Integer.valueOf(parameters.getOrDefault(CPL_LABEL,CPL_DEFAULT));
        usedParameters.put(CPL_LABEL,String.valueOf(CPL));
        GPC = Integer.valueOf(parameters.getOrDefault(GPC_LABEL,GPC_DEFAULT));
        usedParameters.put(GPC_LABEL,String.valueOf(GPC));
        NDPM = Integer.valueOf(parameters.getOrDefault(NDPM_LABEL,NDPM_DEFAULT));
        usedParameters.put(NDPM_LABEL,String.valueOf(NDPM));
        NMPC = Integer.valueOf(parameters.getOrDefault(NMPC_LABEL,NMPC_DEFAULT));
        usedParameters.put(NMPC_LABEL,String.valueOf(NMPC));
        NL = Integer.valueOf(parameters.getOrDefault(NL_LABEL,NL_DEFAULT));
        usedParameters.put(NL_LABEL,String.valueOf(NL));
        
        nf = new NetworkFactory(parameters, fixedDomains, variableDomains, oligomerDomains);
        usedParameters.putAll(nf.getUsedParameters());
        
        m = new Mutator(parameters, fixedDomains, variableDomains, oligomerDomains);
        usedParameters.putAll(m.getUsedParameters());
    }
    
    public SeqEvoReport optimize(){
        double startTime = System.currentTimeMillis(); // start timer for runtime.
                
        Network gen0 = getInitialNetwork();
        	
        //initialize IDomainDesign Arrays.
        Network[] lineageMothers = new Network[NL];
        Network[][] cycleMothers = new Network[NL][NMPC];
        Network[][][] cycleDaughters = new Network[NL][NMPC][NDPM];
        
        //initialize lineage mothers.
        lineageMothers[0] = gen0;
        
        for (int i =  1; i< NL; i++){
            lineageMothers[i] = getType1Mutation(gen0);
        }
        
        //begin heuristic process
        
        //for each cycle
        for( int cycle =0; cycle< CPL; cycle++){
            //for each lineage
            for( int i = 0; i< NL; i++){
                //for each cycle mother
                for( int j = 0; j < NMPC; j++){
                    if(j == 0){
                        cycleMothers[i][0] = lineageMothers[i];
                    }
                    else{
                        cycleMothers[i][j] = getType2Mutation(lineageMothers[i]);
                    }
                    //for each generation in the cycle
                    for (int generation =0; generation < GPC; generation++){
                        //mutate and score k daughters
                        for (int k = 0; k< NDPM; k++){
                                cycleDaughters[i][j][k] = getType3Mutation(cycleMothers[i][j]);
                        }
                        
                        //for each of the k daughters, compare scores.
                        for (int k =0; k < NDPM; k++){
                            // if daughter is more or equally fit, daughter replaces mother
                            if (compare(cycleDaughters[i][j][k],cycleMothers[i][j]) <=0){
                                cycleMothers[i][j] = cycleDaughters[i][j][k];
                            }
                        }
                    }
                }
                //for each of the cycle mothers
                for (int j = 0; j < NMPC; j++){
                    //if cycle mother is more or equally fit, cycle mother replaces lineage mother.
                    if ( compare(cycleMothers[i][j],lineageMothers[i]) <= 0){
                        lineageMothers[i] = cycleMothers[i][j];
                    }
                }
            }
        }
        
        //find the most fit design.
        Network fittestDesign = gen0;
        
        //for each lineage
        for(int i =0; i< NL; i++){
            //if the lineage mother is at least as fit as the most fit design.
            if (compare(lineageMothers[i],fittestDesign)<=0){
                //it replaces the current most fit design.
                fittestDesign = lineageMothers[i];
            }
        }
        
        //calculate runtime.
        double endTime   = System.currentTimeMillis(); // record evolutionary cycle endtime
        double elapsedTime = endTime-startTime;
        int H = (int)((elapsedTime/1000) / (60 *60)); // Hours
        int M = (int)(((elapsedTime/1000) / 60) % 60 ); // Minutes
        int S = (int)((elapsedTime/1000) % 60 );   // Seconds
        String totalTime = ( H + " h " + M + " m " + S + " s ");
        
        return new SeqEvoReport();
    }
    
    // returns 1 if n1 is more fit, 0 if fitness is equal, or -1 if n1 is less fit.
    private int compare(Network n1, Network n2){                
        BigInteger score1 = n1.getScore();
        BigInteger score2 = n2.getScore();
        return score1.compareTo(score2);
    }
    
    private Network getInitialNetwork(){
        return nf.getInitialNetwork();
    }
    
    private BigInteger getScore(Network n1){
        return n1.getScore();
    }
    
    private Network getType1Mutation(Network n1){
        Map<String,int[]> mvd = m.type1Mutation(n1.getEncodedVariableDomains());
        Network retNet = nf.getUpdated(n1,mvd);
        return retNet;
    }
    
    private Network getType2Mutation(Network n1){
        System.out.println("Type 2 mutation not yet active.");
        return n1;
    }
    
    private Network getType3Mutation(Network n1){
        System.out.println("Type 3 mutation not yet active.");
        return n1;
    }
    
    class Mutator{
        Map<String,String> usedParameters;
        String[] dsb;
        
        final int maxAA;
        final int maxCC;
        final int maxGG;
        final int maxTT;
        
        
        Mutator(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
            maxAA = Integer.valueOf(parameters.getOrDefault(MAXAA_LABEL, MAXAA_DEFAULT));
            usedParameters.put(MAXAA_LABEL,String.valueOf(maxAA));
            
            maxCC = Integer.valueOf(parameters.getOrDefault(MAXCC_LABEL, MAXCC_DEFAULT));
            usedParameters.put(MAXCC_LABEL,String.valueOf(maxCC));
            
            maxGG = Integer.valueOf(parameters.getOrDefault(MAXGG_LABEL, MAXGG_DEFAULT));
            usedParameters.put(MAXGG_LABEL,String.valueOf(maxGG));
            
            maxTT = Integer.valueOf(parameters.getOrDefault(MAXTT_LABEL, MAXTT_DEFAULT));
            usedParameters.put(MAXTT_LABEL,String.valueOf(maxTT));
            
            dsb = getDomainSelectionBag(variableDomains);
            
        }
        
        public Map<String,int[]> getType3Mutation(Map<String,int[]> encodedVariableDomains){
            Map<String,int[]> retDomains = new TreeMap<>();
            retDomains.putAll(encodedVariableDomains);
            Random rnd = ThreadLocalRandom.current();
            String selectedDomain = dsb[rnd.nextInt(dsb.length)];
            int[] selectedSequence = encodedVariableDomains.get(selectedDomain);
            int length = selectedSequence.length;
            int[] newSequence = new int[length];
            
            // mutate the sequence by swapping 2 bases.
            int attempts = 0;
            while (attempts < 100 && Arrays.equals(selectedSequence,newSequence)){
                System.arraycopy(selectedSequence, 0, newSequence, 0, length);
                int i1 = rnd.nextInt(newSequence.length);
                int i2 = rnd.nextInt(newSequence.length);
                while (i2 == i1) i2 = rnd.nextInt(newSequence.length);
                int a = newSequence[i2];
                newSequence[i2] = newSequence[i1];
                newSequence[i1] = a;
                retDomains.put(selectedDomain, newSequence);
            }
            
            return retDomains;
        }
        
        // This mutation selects a random sub-sequence and moves it to a new position.
        public Map<String,int[]> getType2Mutation(Map<String,int[]> encodedVariableDomains){
            
            Random rnd = ThreadLocalRandom.current();
            String selectedDomain = dsb[rnd.nextInt(dsb.length)];
            int[] selectedSequence = encodedVariableDomains.get(selectedDomain);
            int length = selectedSequence.length;
            int[] newSequence = new int[length]; 
            
            //try up to 100 times
            int attempts = 0;
            while (attempts < 100 && Arrays.equals(selectedSequence,newSequence)){
                System.arraycopy(selectedSequence, 0, newSequence, 0, length);
                // transpose the domain.
                Integer[] tempArray = Arrays.stream( selectedSequence ).boxed().toArray( Integer[]::new );

                ArrayList<Integer> targetDomain = new ArrayList<Integer>(Arrays.asList(tempArray));
                int b1 = rnd.nextInt(length);
                int b2 = rnd.nextInt(length);

                List<Integer> removedStretch = new ArrayList<Integer> (targetDomain.subList(Math.min(b1,b2), Math.max(b1,b2)));
                targetDomain.subList(Math.min(b1,b2), Math.max(b1,b2)).clear();

                if (b1 < b2) {Collections.reverse(removedStretch);}

                int b3 = rnd.nextInt(length - (Math.max(b1,b2)-Math.min(b1,b2)));

                targetDomain.addAll(b3, removedStretch);
                
                for(int i = 0; i < length; i++)
                {
                   newSequence[i] = targetDomain.get(i).intValue();
                }
                
                attempts++;
            }
            
            Map<String,int[]> retDomains = new TreeMap<>();
            retDomains.putAll(encodedVariableDomains);
            retDomains.put(selectedDomain,newSequence);
            
            return retDomains;
        }
        
        private String[] getDomainSelectionBag(Map<String,String> initialVariableDomains){
            ArrayList<String> tempBag = new ArrayList<>();
            
            //for each variable domain
                // add a number of elements equal to the base length to the domain selection bag.
            for (Map.Entry<String,String> entry : encodedVariableDomains.entrySet()){
                int length = entry.getValue().trim().length();
               for(int i = 0; i < length; i++){
                   tempBag.add(entry.getKey());
               }
           }
            
            return tempBag.toArray(new String[0]);
        }
        
        public Map<String,String> getUsedParameters(){
            return usedParameters;
        }
        
    }
    
    class SeqEvoReport{
        
        SeqEvoReport(){
            
        }
    }
    
    private interface Network {
        Map<String,String> getUsedParameters();
        Map<String,String> getFixedDomains(); // fixed-domain-sequences
        Map<String,String> getVariableDomains(); // variable-domain-sequences
        Map<String,String[]> getOligomerDomains(); //oligomer-domains
        Map<String,String> getOligomerSequences(); // oligomer-sequences
        
        Map<String,int[]> getEncodedFixedDomains();
        Map<String,int[]> getEncodedVariableDomains();
        Map<String,int[]> getEncodedOligomerSequences();
        
        BigInteger getScore();
    }
    
    private class NetworkFactory{
        final Map<String,String> usedParameters;
        
        final Map<String,String> fd; // fixed-domains
        final Map<String,int[]> efd; // encoded-fixed-domains
        final Map<String,String> ivd; // initial-variable-domains
        final Map<String,String[]> od; // oligomer-domains
        
        final Scorer s;
        
        NetworkFactory(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
            usedParameters = new TreeMap<>();
            fd = fixedDomains;
            efd = util.encode(fd);
            ivd = variableDomains;
            od = oligomerDomains;
            
            String FS = parameters.getOrDefault(FS_LABEL,FS_DEFAULT);
            usedParameters.put(FS_LABEL,FS);
            switch (FS){
                default:
                    System.out.println("Fitness score "+ FS + " not recognized.");
                    System.exit(0);
                case "N":
                    System.out.println("Fitness score "+ FS + " not yet supported.");
                    System.exit(0);
                case "O":
                    System.out.println("Fitness score "+ FS + " not yet supported.");
                    System.exit(0);
                case "Wx":
                    s = new WScorer(parameters,fixedDomains,variableDomains,oligomerDomains);
                    usedParameters.putAll(s.getUsedParameters());
                    break;
            }
        }
        
        Network getInitialNetwork(){
            innerNetwork retNetwork = new innerNetwork();
            retNetwork.evd = util.encode(ivd);
            retNetwork.eos = util.assembleEncodedOligomers(efd, retNetwork.evd, od);
            retNetwork.score = s.getScore(retNetwork.eos);
            return retNetwork;
        }
        
        Network getType1Mutation(Network network){
            return getInitialNetwork();
        }
        
        Network getType2Mutation(Network network){
            return getInitialNetwork();
        }
        
        Network getType3Mutation(Network network){
            return getInitialNetwork();
        }
        
        Map<String,String> getUsedParameters(){
            return usedParameters;
        }
        
        private class innerNetwork implements Network{
            Map<String,String> vd; // variable-domains
            Map<String,int[]> evd; // encoded-variable-domains
            Map<String,String> os; // oligomer-sequences
            Map<String,int[]> eos; // encoded-oligomer-sequences
            BigInteger score;

            @Override
            public Map<String, String> getUsedParameters() {
                return usedParameters;
            }

            @Override
            public Map<String, String> getFixedDomains() {
                return fd;
            }

            @Override
            public Map<String, String> getVariableDomains() {
                if (vd == null) vd = util.decode(evd);
                return vd;
            }

            @Override
            public Map<String, String[]> getOligomerDomains() {
                return od;
            }

            @Override
            public Map<String, String> getOligomerSequences() {
                if (os == null) os = util.decode(eos);
                return os;
            }

            @Override
            public Map<String, int[]> getEncodedFixedDomains() {
                return efd;
            }

            @Override
            public Map<String, int[]> getEncodedVariableDomains() {
                return evd;
            }

            @Override
            public Map<String, int[]> getEncodedOligomerSequences() {
                return eos;
            }
            
            public BigInteger getScore(){
                return score;
            }
        }
    }
    
    private interface Scorer{
        BigInteger getScore(Map<String,int[]> encodedOligomers);
        Map<String,String> getUsedParameters();
    }
    
    class WScorer implements Scorer{
        final Map<String,String> usedParameters;
        
        final Map<String,String> fd; // fixed-domains
        final Map<String,String> ivd; // initial-variable-domains
        final Map<String,String[]> od; // oligomer-domains
        
        final int intraSLC;
        final int intraSB;
        final int interSLC;
        final int interSB;
        final int swx;
        final Map<String,String> baselineScores;
        final String[][] oligomerCombinations;
        final Map<Integer,BigInteger> knownIntraScores;
        final Map<Integer,BigInteger> knownInterScores;
        
        final BigInteger baselineO;
        final BigInteger baselineN;
        final BigInteger baselineW;
        
        WScorer(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
            usedParameters = new TreeMap<>();
            baselineScores = new TreeMap<>();
            knownIntraScores = new ConcurrentSkipListMap<>();
            knownInterScores = new ConcurrentSkipListMap<>();
            
            swx = Integer.valueOf(parameters.getOrDefault(SWX_LABEL,SWX_DEFAULT));
            usedParameters.put(SWX_LABEL,String.valueOf(swx));
            
            intraSLC = Integer.valueOf(parameters.getOrDefault(intraSLC_LABEL,intraSLC_DEFAULT));
            usedParameters.put(intraSLC_LABEL,String.valueOf(intraSLC));
            
            intraSB = Integer.valueOf(parameters.getOrDefault(intraSB_LABEL,intraSB_DEFAULT));
            usedParameters.put(intraSB_LABEL,String.valueOf(intraSB));
            
            interSLC = Integer.valueOf(parameters.getOrDefault(interSLC_LABEL,interSLC_DEFAULT));
            usedParameters.put(interSLC_LABEL,String.valueOf(interSLC));
            
            interSB = Integer.valueOf(parameters.getOrDefault(interSB_LABEL,interSB_DEFAULT));
            usedParameters.put(interSB_LABEL,String.valueOf(interSB));
            
            fd = fixedDomains;
            ivd = variableDomains;
            od = oligomerDomains;
            oligomerCombinations = util.getCombinations(od.keySet());
            
            Map<String,int[]> efd = util.encode(fd); //encoded fixed domains
            Map<String,int[]> eivd = util.encode(ivd); // encoded initial variable domains
            Map<String,int[]> uevd = util.getUniquelyEncodedVariableBases(eivd); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = util.assembleEncodedOligomers(efd, uevd, od); // 
            
            Map<Integer,Integer> baselineIntraUnique = util.countIntraOligomerUnique(ueo,intraSLC);
            baselineO = calculateO(baselineIntraUnique);
                        
            Map<Integer,Integer> baselineInterUnique = util.countInterOligomerUnique( ueo, oligomerCombinations, interSLC);
            baselineN = calculateN(baselineInterUnique);
            
            baselineW = calculateW(baselineO,baselineN);
        }
        
        public Map<String,String> getUsedParameters(){
            return usedParameters;
        }
        
        public BigInteger getScore(Map<String,int[]> encodedOligomers){
            Map<String,String> retScores = new TreeMap<>();
            
            Map<Integer,Integer> intraUnique = util.countIntraOligomerUnique(encodedOligomers,intraSLC);
            BigInteger O = calculateO(intraUnique);
            Map<Integer,Integer> interUnique = util.countInterOligomerUnique(encodedOligomers, oligomerCombinations, interSLC);
            BigInteger N = calculateN(interUnique);
            BigInteger W = calculateW(O,N);
            
            return W;
        }
        
        private BigInteger calculateO (Map<Integer,Integer> intraUnique){
            BigInteger retScore = BigInteger.valueOf(0);
            for(Map.Entry<Integer,Integer> entry: intraUnique.entrySet()){
                int length = entry.getKey();
                BigInteger lengthScore = knownIntraScores.get(length);
                if (lengthScore == null) {
                    lengthScore = util.calculateUniqueDuplexPoints(length, intraSLC, intraSB);
                    knownIntraScores.put(length,lengthScore);
                }
                BigInteger localScore = lengthScore.multiply(BigInteger.valueOf(entry.getValue()));
                retScore.add(localScore);
            }
            return retScore;
        }
        
        private BigInteger calculateN (Map<Integer,Integer> interUnique){
            BigInteger retScore = BigInteger.valueOf(0);
            for(Map.Entry<Integer,Integer> entry: interUnique.entrySet()){
                int length = entry.getKey();
                BigInteger lengthScore = knownInterScores.get(length);
                if (lengthScore == null) {
                    lengthScore = util.calculateUniqueDuplexPoints(length, interSLC, interSB);
                    knownInterScores.put(length,lengthScore);
                }
                BigInteger localScore = lengthScore.multiply(BigInteger.valueOf(entry.getValue()));
                retScore.add(localScore);
            }
            return retScore;
        }
        
        private BigInteger calculateW (BigInteger O, BigInteger N){
            return O.multiply(BigInteger.valueOf(swx)).add(N);
        }
    }
    
    public static void main(String[] args){
        Map<String,String> usedParameters = new TreeMap<>();
        String PFP = PFP_DEFAULT;
        if (args.length > 0)
        {
            if (args[0].equals("-h") || args[0].equals("--help")) // Print explanation of acceptable arguments.
            {
                    System.out.println("Usage: SeqEvo <Parameter File Path>");
                    System.out.println("Default Parameter File Path: " + PFP_DEFAULT);
                    System.exit(0);
            }

            else{
                    PFP = args[0]; // accept the next argument as the parameter file
                    System.out.println("Using Parameter File Path: " + PFP); 
            }
        }
        
        // Read parameters file.
        usedParameters.put(PFP_LABEL, PFP);
        Map<String,String> parameters = util.importPairFromTxt(PFP);
        
        // Read fixed domains file.
        String FDFP = parameters.getOrDefault(FDFP_LABEL,FDFP_DEFAULT);
        usedParameters.put(FDFP_LABEL,FDFP);
        Map<String,String> fd = util.importPairFromTxt(FDFP);

        // Read variable domains file.
        String VDFP = parameters.getOrDefault(VDFP_LABEL,VDFP_DEFAULT);
        usedParameters.put(VDFP_LABEL, VDFP);
        Map<String,String> ivd = util.importPairFromTxt(VDFP);

        // Read oligomer domains file.
        String OFP = parameters.getOrDefault(OFP_LABEL,OFP_DEFAULT);
        usedParameters.put(OFP_LABEL, OFP);
        Map<String,String[]> od = util.importListFromTxt(OFP);
        
        SeqEvo s = new SeqEvo(parameters, fd, ivd, od);
                
        SeqEvoReport report = s.optimize();
    }
}
