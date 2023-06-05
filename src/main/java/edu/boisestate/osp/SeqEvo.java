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

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import edu.boisestate.osp.domainbasedencodednetwork.IDomainBasedEncodedScoredNetwork;

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
    final static String FDFP_DEFAULT = "in_domains_fixed.txt";
    final static String VDFP_LABEL = "VDFP"; // variable-domains-file-path
    final static String VDFP_DEFAULT = "in_domains_variable.txt";
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
    
    public SeqEvo(String parametersFilePath, String fixedDomainsFilePath, String variableDomainsFilePath, String oligomerDomainsFilePath){
        usedParameters = new HashMap<>();
        
        // Read parameters file.
        String PFP = parametersFilePath;
        usedParameters.put(PFP_LABEL, parametersFilePath);
        parameters = util.importPairFromTxt(PFP);
        
        // Read fixed domains file.
        String FDFP = fixedDomainsFilePath;
        usedParameters.put(FDFP_LABEL,FDFP);
        fixedDomains = util.importPairFromTxt(FDFP);

        // Read variable domains file.
        String VDFP = variableDomainsFilePath;
        usedParameters.put(VDFP_LABEL, VDFP);
        variableDomains = util.importPairFromTxt(VDFP);

        // Read oligomer domains file.
        String OFP = oligomerDomainsFilePath;
        usedParameters.put(OFP_LABEL, OFP);
        oligomerDomains = util.importListFromTxt(OFP);
        
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
    }
    
    /**
     * Creates a new instance of the SeqEvo optimizer for optimizing the provided network.
     * @param parameters 
     * @param fixedDomains
     * @param variableDomains
     * @param oligomerDomains
     */
    
    public SeqEvo(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
        usedParameters = new HashMap<>();
        this.parameters = parameters;
        this.fixedDomains = fixedDomains;
        this.variableDomains = variableDomains;
        this.oligomerDomains = oligomerDomains;
        
        // optimization parameters
        CPL = Integer.parseInt(parameters.getOrDefault(CPL_LABEL,CPL_DEFAULT));
        usedParameters.put(CPL_LABEL,String.valueOf(CPL));
        GPC = Integer.parseInt(parameters.getOrDefault(GPC_LABEL,GPC_DEFAULT));
        usedParameters.put(GPC_LABEL,String.valueOf(GPC));
        NDPM = Integer.parseInt(parameters.getOrDefault(NDPM_LABEL,NDPM_DEFAULT));
        usedParameters.put(NDPM_LABEL,String.valueOf(NDPM));
        NMPC = Integer.parseInt(parameters.getOrDefault(NMPC_LABEL,NMPC_DEFAULT));
        usedParameters.put(NMPC_LABEL,String.valueOf(NMPC));
        NL = Integer.parseInt(parameters.getOrDefault(NL_LABEL,NL_DEFAULT));
        usedParameters.put(NL_LABEL,String.valueOf(NL));
        
        nf = new NetworkFactory(parameters, fixedDomains, variableDomains, oligomerDomains);
        usedParameters.putAll(nf.getUsedParameters());
    }
    
    public SeqEvoReport optimize(){
        double startTime = System.currentTimeMillis(); // start timer for runtime.
                
        Network gen0 = getInitialNetwork();
        System.out.println("initialScore = "+gen0.getScore());
        
        	
//        //initialize IDomainDesign Arrays.
//        Network[] lineageMothers = new Network[NL];
//        Network[][] cycleMothers = new Network[NL][NMPC];
//        Network[][][] cycleDaughters = new Network[NL][NMPC][NDPM];
        
        //initialize lineage mothers.
        //lineageMothers = Stream.concat(Stream.of(gen0),Stream.generate(()->getType1Mutation(gen0)).limit(NL-1).parallel()).toArray((x)->new Network[x]);
        //lineageMothers[0] = gen0;
        
        //begin heuristic process
        
        /*
        //for each cycle
        for( int cycle =0; cycle< CPL; cycle++){
            
            //create mothers
            //for GPC iterations, iterate mothers.
            
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
        Network fittest = gen0;
        
        //for each lineage
        for(int i =0; i< NL; i++){
            //if the lineage mother is at least as fit as the most fit design.
            if (compare(lineageMothers[i],fittest)<=0){
                //it replaces the current most fit design.
                fittest = lineageMothers[i];
            }
        }
        */
        
        // compare scores.
        Network fittest = cycle1(gen0);
        
        
        System.out.println("fittestScore = "+fittest.getScore());
        
        //calculate runtime.
        double endTime   = System.currentTimeMillis(); // record evolutionary cycle endtime
        double elapsedTime = endTime-startTime;
        int H = (int)((elapsedTime/1000) / (60 *60)); // Hours
        int M = (int)(((elapsedTime/1000) / 60) % 60 ); // Minutes
        int S = (int)((elapsedTime/1000) % 60 );   // Seconds
        String totalTime = ( H + " h " + M + " m " + S + " s ");
        
        System.out.println("total time: "+ totalTime);
        export(fittest, "out_domains_variable.txt", "out_oligomers.txt");
        
        return new SeqEvoReport();
    }
    
    private Network cycle1(Network network){
        //generate mutated networks.
        Network[] beforeSubCycles = Stream.concat(Stream.of(network),Stream.generate(()->getType1Mutation(network)).limit(NL-1).parallel()).toArray(x -> new Network[x]);
        Network[] afterSubCycles = Arrays.stream(beforeSubCycles).parallel()
            .map(n->{
            Network subCycleBest = n;
            for(int i =0; i < CPL; i++){
                subCycleBest = cycle2(subCycleBest);
            }
            return subCycleBest;
        }).toArray(i->new Network[i]);
        
        // compare scores.
        Network fittest = network;
        for(Network n : afterSubCycles){
            if(compare(n,fittest) <=0){
                fittest = n;
            }
        }
        return fittest;
    }
    
    private Network cycle2(Network network){
        //generate mutated networks.
        Network[] beforeSubCycles = Stream.concat(Stream.of(network),Stream.generate(()->getType2Mutation(network)).limit(NMPC)).toArray(x->new Network[x]);
        Network[] afterSubCycles = Arrays.stream(beforeSubCycles)
                .map(n->{
                Network subCycleBest = n;
                for(int i =0; i < GPC; i++){
                    subCycleBest = cycle3(subCycleBest);
                }
                return subCycleBest;
            }).toArray(i->new Network[i]);
        
        // compare scores.
        Network fittest = network;
        for(Network n : afterSubCycles){
            if(compare(n,fittest) <=0){
                fittest = n;
            }
        }
        return fittest;
    }
    
    private Network cycle3(Network network){
        //generate mutated networks.
        Network[] newNetworks = Stream.generate(()->getType3Mutation(network)).limit(NDPM).toArray(i->new Network[i]);
        
        // compare scores.
        Network fittest = network;
        for(Network n : newNetworks){
            if(compare(n,fittest) <=0){
                fittest = n;
            }
        }
        return fittest;
    }
    
    // returns 1 if n1 is more fit, 0 if fitness is equal, or -1 if n1 is less fit.
    private int compare(Network n1, Network n2){                
        BigInteger score1 = new BigInteger(n1.getScore());
        BigInteger score2 = new BigInteger(n2.getScore());
        return score1.compareTo(score2);
    }
    
    private void export(Network n, String variableDomainsFile, String oligomerSequencesFile){
        try{
            FileWriter FW1 = new FileWriter( variableDomainsFile );
            PrintWriter PW1 = new PrintWriter( FW1);
            for(Map.Entry<String,String> entry : n.getVariableDomains().entrySet()){
                PW1.println(entry.getKey()+ " " + entry.getValue());
            }
            PW1.close();
            
            FileWriter FW2 = new FileWriter( variableDomainsFile );
            PrintWriter PW2 = new PrintWriter( FW2);
            for(Map.Entry<String,String> entry : n.getVariableDomains().entrySet()){
                PW2.println(entry.getKey()+ " " + entry.getValue());
            }
            PW2.close();
        } catch (Exception e) {
            System.out.println("Error while exporting network.");
            System.out.println(e.getMessage());
        }
    }
    
    private Network getInitialNetwork(){
        return nf.getInitialNetwork();
    }
    
    private BigInteger getScore(Network n1){
        return new BigInteger(n1.getScore());
    }
    
    private Network getType1Mutation(Network n1){
        return nf.getType1Mutation(n1);
    }
    
    private Network getType2Mutation(Network n1){
        return nf.getType2Mutation(n1);
    }
    
    private Network getType3Mutation(Network n1){
        return nf.getType3Mutation(n1);
    }
    
    class SeqEvoReport{
        
        SeqEvoReport(){
            
        }
    }
    
    private interface Network extends IDomainBasedEncodedScoredNetwork{
    }
    
    private class NetworkFactory{
        final Map<String,String> usedParameters;
        
        final Map<String,Integer> dl; //domain-lengths
        final Map<String,int[]> beos; // blank-encoded-oligomer-sequences
        final Map<String,int[]> bevd; // blank-encoded-variable-domains;
        final Map<String,String> fd; // fixed-domains
        final Map<String,int[]> efd; // encoded-fixed-domains
        final Map<String,String[]> od; // oligomer-domains
        
        // variables for mutating networks.
        final String[] dsb; // domain-selection-bag
        final Map<String,Map<String,int[]>> dtom; // domain-to-oligomer map
        final Map<String,Map<String,int[]>> ctom; // complement-to-oligomer map
        final int maxAA; // Maximum number of consecutive A's
        final int maxCC; // Maximum number of consecutive C's
        final int maxGG; // Maximum number of consecutive G's
        final int maxTT; // Maximum number of consecutive T's
        
        final WScorer s;
        
        NetworkFactory(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
            usedParameters = new HashMap<>();
            fd = fixedDomains;
            efd = util.encode(fd);
            od = oligomerDomains;
            
            dsb = getDomainSelectionBag(variableDomains);
            bevd = util.getBlankEncodedSequences(variableDomains);
            dl = getDomainLengths(fixedDomains,variableDomains);
            dtom = getDomainToOligomerMap(variableDomains, oligomerDomains);
            ctom = getComplementToOligomerMap(variableDomains, oligomerDomains);
            beos = util.assembleEncodedOligomers(efd, bevd, od);
            
            // initialize variables for mutating networks
            maxAA = Integer.parseInt(parameters.getOrDefault(MAXAA_LABEL, MAXAA_DEFAULT));
            usedParameters.put(MAXAA_LABEL,String.valueOf(maxAA));
            maxCC = Integer.parseInt(parameters.getOrDefault(MAXCC_LABEL, MAXCC_DEFAULT));
            usedParameters.put(MAXCC_LABEL,String.valueOf(maxCC));
            maxGG = Integer.parseInt(parameters.getOrDefault(MAXGG_LABEL, MAXGG_DEFAULT));
            usedParameters.put(MAXGG_LABEL,String.valueOf(maxGG));
            maxTT = Integer.parseInt(parameters.getOrDefault(MAXTT_LABEL, MAXTT_DEFAULT));
            usedParameters.put(MAXTT_LABEL,String.valueOf(maxTT));
            
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
        
        private boolean checkEdges(Map<String,int[]> encodedOligomers, String domainName){
            //check validity everywhere the domain occurs. (just on the edges, everything else is already checked.)
            int length = dl.get(domainName);
            for(Map.Entry<String,int[]> entry : dtom.get(domainName).entrySet()){
                String oligomer = entry.getKey();
                int[] lefts = entry.getValue();
                for (int coord : lefts){
                    if (!util.checkForStretches(encodedOligomers.get(oligomer),coord,maxAA,maxCC,maxGG,maxTT)){
                        return false;
                    }
                    if (!util.checkForStretches(encodedOligomers.get(oligomer),coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                        return false;
                    }
                }
            }

            //check validity everywhere the complement occurs.
            for(Map.Entry<String,int[]> entry : ctom.get(domainName).entrySet()){
                String oligomer = entry.getKey();
                int[] lefts = entry.getValue();
                for (int coord : lefts){
                    if (!util.checkForStretches(encodedOligomers.get(oligomer),coord,maxAA,maxCC,maxGG,maxTT)){
                        return false;
                    }
                    if (!util.checkForStretches(encodedOligomers.get(oligomer),coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                        return false;
                    }
                }
            }
            return true;
        }
        
        private boolean isValid(Map<String,int[]> encodedOligomers, Map<String,int[]> variableDomains){
           
            for(Map.Entry<String,int[]> entry : variableDomains.entrySet()){
                String dn = entry.getKey();
                int[] es = entry.getValue();
                if (!util.checkForStretches(es,maxAA,maxCC,maxGG,maxTT)) return false;
                if (!checkEdges(encodedOligomers,dn)) return false;
            }
            
            return true;
        }
        
        private Map<String,Integer> getDomainLengths(Map<String,String> fixedDomains, Map<String,String> variableDomains){
            Map<String,Integer> ret = new HashMap<>();
            for(Map.Entry<String,String> entry : fixedDomains.entrySet()){
                ret.put(entry.getKey(),entry.getValue().trim().length());
            }
            
            for(Map.Entry<String,String> entry : variableDomains.entrySet()){
                ret.put(entry.getKey(),entry.getValue().trim().length());
            }
            
            return ret;
        }
        
        private String[] getDomainSelectionBag(Map<String,String> initialVariableDomains){
            ArrayList<String> tempBag = new ArrayList<>();
            
            //for each variable domain
                // add a number of elements equal to the base length to the domain selection bag.
            for (Map.Entry<String,String> entry : initialVariableDomains.entrySet()){
                int length = entry.getValue().trim().length();
               for(int i = 0; i < length; i++){
                   tempBag.add(entry.getKey());
               }
           }
            return tempBag.toArray(new String[0]);
        }
        
        private Map<String,Map<String,int[]>> getDomainToOligomerMap (Map<String,String> variableDomains, Map<String,String[]> OligomerDomains){
            Map<String,Map<String,int[]>> retDTOM = new HashMap<>();
            
            //initialize an entry for every domain;
            for (Map.Entry<String,String> variableDomain : variableDomains.entrySet()){
                retDTOM.put(variableDomain.getKey(),new HashMap<String,int[]>());
            }
            
            //for each oligomer
                // for each domain in the oligomer
                    // add an entry for this association.
                    
            for (Map.Entry<String,String[]> entry1 : oligomerDomains.entrySet()){
                String oligomerName = entry1.getKey();
                String[] domainNames = entry1.getValue();
                int currentBase = 0;
                for(String domainName : domainNames){
                    if (domainName.startsWith("c.")){
                        String compName = domainName.substring(2);
                        int domainLength = dl.get(compName);
                        currentBase += domainLength;
                    } else {
                        if (variableDomains.containsKey(domainName)){
                            Map<String,int[]> oligomerToLocations = retDTOM.get(domainName);
                            int[] startIndexes = oligomerToLocations.get(oligomerName);
                            if (startIndexes == null){
                                startIndexes = new int[1];
                                startIndexes[0] = currentBase;
                            } else {
                                startIndexes = Arrays.copyOf(startIndexes,startIndexes.length+1);
                                startIndexes[startIndexes.length]=currentBase;
                            }
                            oligomerToLocations.put(oligomerName,startIndexes);
                        }
                    int domainLength = dl.get(domainName);
                    currentBase += domainLength;
                    }
                }
            }
            return retDTOM;        
        }
        
        private Map<String,Map<String,int[]>> getComplementToOligomerMap (Map<String,String> variableDomains, Map<String,String[]> OligomerDomains){
            Map<String,Map<String,int[]>> retCTOM = new HashMap<>();
            
            //initialize an entry for every domain;
            for (Map.Entry<String,String> variableDomain : variableDomains.entrySet()){
                retCTOM.put(variableDomain.getKey(),new HashMap<String,int[]>());
            }
            
            //for each oligomer
                // for each domain in the oligomer
                    // add an entry for this association.
                    
            for (Map.Entry<String,String[]> entry1 : oligomerDomains.entrySet()){
                String oligomerName = entry1.getKey();
                String[] domainNames = entry1.getValue();
                int currentBase = 0;
                for(String domainName : domainNames){
                    if (domainName.startsWith("c.")){
                        String compName = domainName.substring(2);
                        if (variableDomains.containsKey(compName)){
                            Map<String,int[]> oligomerToLocations = retCTOM.get(compName);
                            int[] startIndexes = oligomerToLocations.get(oligomerName);
                            if (startIndexes == null){
                                startIndexes = new int[1];
                                startIndexes[0] = currentBase;
                            } else {
                                startIndexes = Arrays.copyOf(startIndexes,startIndexes.length+1);
                                startIndexes[startIndexes.length] = currentBase;
                            }
                            oligomerToLocations.put(oligomerName,startIndexes);
                        }
                        int domainLength = dl.get(compName);
                        currentBase += domainLength;
                    } else {
                        int domainLength = dl.get(domainName);
                        currentBase += domainLength;
                    }
                }
            }
            return retCTOM;        
        }
        
        Network getInitialNetwork(){
            Map<String,int[]> ievd = util.encode(variableDomains);
            Map<String,int[]> ieos = util.assembleEncodedOligomers(efd, ievd, od);
            
            if (!isValid(ieos,ievd)) {
                System.out.println("Initial sequences invalid. Replacing with randomly seeded sequences.");
                ievd = getValidRandomizedVariableDomains(ievd);
                ieos = util.assembleEncodedOligomers(efd, ievd, od);
            }
            
            String score = s.getScore(ieos);
            
            InnerNetwork retNetwork = new InnerNetwork(ievd,ieos,score);
            return retNetwork;
        }
        
        private Map<String,int[]> getValidRandomizedVariableDomains(Map<String,int[]> variableDomains){
            final Map<String,int[]> oldEVD = variableDomains;
            Map<String,int[]> newEVD = new HashMap<>(oldEVD);
            Map<String,int[]> newEOS = new HashMap<>(beos);
            
            int seedings = 0;
            boolean valid = false;
            // attempt to seed all domains
            do {
                newEOS = new HashMap<>(beos);
                newEVD = new HashMap<>(oldEVD);
                String[] domainSelectionBag = Arrays.copyOf(newEVD.keySet().toArray(new String[0]),newEVD.size());
                            
                Random rnd = ThreadLocalRandom.current();
                // for each domain in the domain selection bag. (reverse)
                currentSeeding:
                for (int i = domainSelectionBag.length-1; i >= 0; i--){
                    
                    // select a domain to be mutated and remove it from the bag.
                    int k = rnd.nextInt(i + 1);
                    String a = domainSelectionBag[k];
                    domainSelectionBag[k] = domainSelectionBag[i];
                    domainSelectionBag[i] = a;

                    String domainName = domainSelectionBag[i];
                    final int[] oldDomain = oldEVD.get(domainName);

                    //attempt to mutate this domain 100 times;
                    int attempts2 = 0;
                    int[] newDomain = new int[oldDomain.length];
                    // attempt to randomize the domain
                    do {
                        newDomain = util.getType1Mutation(oldDomain);
                        valid = util.checkForStretches(newDomain,maxAA,maxCC,maxGG,maxTT);
                        if (valid) {
                            placeDomain(newEOS,domainName,newDomain);
                            valid = checkEdges(newEOS,domainName);
                        }
                        attempts2++;
                    } while (attempts2 < 1000 && (!valid));
                    
                    if(!valid) break currentSeeding;
                    newEVD.put(domainName, newDomain);
                }
                seedings++;
            } while (seedings < 1000 && !valid);
            
            if(!valid){
                System.out.println("Failed to identify a valid network during random seeding.");
                System.exit(0);
            }
            
            return newEVD;
        }
        
        Network getType1Mutation(Network existingNetwork){
            final Map<String,int[]> oldEVD = existingNetwork.getVariableDomainSequencesEncoded();
            
            Map<String,int[]> newEVD = getValidRandomizedVariableDomains(oldEVD);
            Map<String,int[]> newEOS = util.assembleEncodedOligomers(efd, newEVD, od);
            
            // calculate score.
            String score = s.getScore(newEOS);
            
            Network retNet = new InnerNetwork(newEVD, newEOS, score);
            return retNet;
        }
        
        Network getType2Mutation(Network existingNetwork){
            Map<String,int[]> newEVD = new HashMap<>(existingNetwork.getVariableDomainSequencesEncoded());
            Map<String,int[]> newEOS = new HashMap<>(existingNetwork.getEncodedOligomerSequences());
            
            //Select a domain for mutation.
            Random rnd = ThreadLocalRandom.current();
            String domainName = dsb[rnd.nextInt(dsb.length)];
            int[] oldDomain = newEVD.get(domainName);
            
            int attempts1 = 0;
            int[] newDomain;
            boolean valid = false;
            do {
                newDomain = util.getType2Mutation(oldDomain);
                valid = util.checkForStretches(newDomain, maxAA, maxCC, maxGG, maxTT);
                if (valid) {
                    newEVD.put(domainName, newDomain);
                    placeDomain(newEOS,domainName,newDomain);
                    valid = checkEdges(newEOS,domainName);
                }
                attempts1++;
            } while (attempts1<1000 && (!valid || Arrays.equals(oldDomain,newDomain)));
            
            if (!valid) {
                newDomain = oldDomain;
                placeDomain(newEOS,domainName,newDomain);
                valid = true;
            }
            newEVD.put(domainName, newDomain);
            
            // calculate score.
            String score = s.getUpdatedScore(existingNetwork, newEVD, newEOS, domainName);
            
            Network retNet = new InnerNetwork(newEVD, newEOS, score);
            return retNet;
        }
        
        Network getType3Mutation(Network existingNetwork){
            Map<String,int[]> newEVD = new HashMap<>(existingNetwork.getVariableDomainSequencesEncoded());
            Map<String,int[]> newEOS = new HashMap<>(existingNetwork.getEncodedOligomerSequences());
            
            //Select a domain for mutation.
            Random rnd = ThreadLocalRandom.current();
            String domainName = dsb[rnd.nextInt(dsb.length)];
            int[] oldDomain = newEVD.get(domainName);
            
            //attempt to mutate this domain 100 times;
            int attempts = 0;
            int[] newDomain = oldDomain;
            boolean valid = false;
            do {
                newDomain = util.getType3Mutation(oldDomain);
                valid = util.checkForStretches(newDomain, maxAA, maxCC, maxGG, maxTT);
                if (valid){
                    placeDomain(newEOS,domainName,newDomain);
                    valid = checkEdges(newEOS,domainName);
                }
                attempts++;
            } while (attempts<1000 && (!valid || Arrays.equals(oldDomain,newDomain)));
            
            if (!valid) {
                newDomain = oldDomain;
                placeDomain(newEOS,domainName,newDomain);
                valid = true;
            }
            
            newEVD.put(domainName, newDomain);
            
            String score = s.getUpdatedScore(existingNetwork, newEVD, newEOS, domainName);
            
            Network retNet = new InnerNetwork(newEVD, newEOS, score);
            return retNet;
        }
        
        Map<String,String> getUsedParameters(){
            return usedParameters;
        }
        
        //modifies the passed oligomer sequences by placing the domain.
        void placeDomain(Map<String,int[]> encodedOligomerSequences, String domainName, int[] domainSequence){
            
            //for every oligomer the domain occurs on
            for(Map.Entry<String,int[]> oligomerToCoords : dtom.get(domainName).entrySet()){
                String oligomerName = oligomerToCoords.getKey();
                int[] startIndexes = oligomerToCoords.getValue();
                
                // create a new copy of the oligomer
                int[] oldOligomer = encodedOligomerSequences.get(oligomerName);
                int[] newOligomer = Arrays.copyOf(oldOligomer, oldOligomer.length); 
                
                //for every occurrence on the oligomer.
                for (int k : startIndexes){
                    //copy the new domain into the new oligomer copy.
                    System.arraycopy(domainSequence,0,newOligomer,k,domainSequence.length);
                }
                
                // replace the oligomer in the EOS.
                encodedOligomerSequences.put(oligomerName,newOligomer);
            }
            
            // for every oligomer the domain complement occurs on
            int[] newComplement = util.getComplement(domainSequence);
            for(Map.Entry<String,int[]> oligomerToCoords : ctom.get(domainName).entrySet()){
                String oligomerName = oligomerToCoords.getKey();
                int[] startIndexes = oligomerToCoords.getValue();
                // create a new copy of the oligomer
                int[] oldOligomer = encodedOligomerSequences.get(oligomerName);
                int[] newOligomer = Arrays.copyOf(oldOligomer, oldOligomer.length); 
                
                //for every occurrence on the oligomer.
                for (int k : startIndexes){
                    //copy the new domain into the new oligomer copy.
                    System.arraycopy(newComplement,0,newOligomer,k,newComplement.length);
                }
                
                // replace the oligomer in the newEOS.
                encodedOligomerSequences.put(oligomerName,newOligomer);
            }
        }
        
        private class InnerNetwork implements Network{
            Map<String,String> vd; // variable-domains
            final Map<String,int[]> evd; // encoded-variable-domains
            Map<String,String> os; // oligomer-sequences
            final Map<String,int[]> eos; // encoded-oligomer-sequences
            final String score;
            
            InnerNetwork(Map<String,int[]> encodedVariableDomains, Map<String,int[]> encodedOligomerSequences, String score){
                this.evd = encodedVariableDomains;
                this.eos = encodedOligomerSequences;
                this.score = score;
            }

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
            public Map<String, int[]> getFixedDomainEncodedSequences() {
                return efd;
            }

            @Override
            public Map<String, int[]> getVariableDomainSequencesEncoded() {
                return evd;
            }

            @Override
            public Map<String, int[]> getEncodedOligomerSequences() {
                return eos;
            }
            
            public String getScore(){
                return score;
            }
        }
        
        class WScorer{
            final Map<String,String> usedParameters;

            final Map<String,String> fd; // fixed-domains
            final Map<String,String> ivd; // initial-variable-domains
            final Map<String,String[]> od; // oligomer-domains

            final Map<String,String[][]> dtoc;
            final Map<String,String[]> dto;

            final int intraSLC;
            final int intraSB;
            final int interSLC;
            final int interSB;
            final int swx;
            final String[][] oligomerCombinations;
            final Map<Integer,BigInteger> knownIntraScores;
            final Map<Integer,BigInteger> knownInterScores;

            final BigInteger baselineO;
            final BigInteger baselineN;
            final BigInteger baselineW;

            WScorer(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
                usedParameters = new HashMap<>();
                knownIntraScores = new ConcurrentHashMap<>();
                knownInterScores = new ConcurrentHashMap<>();

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

                dtoc = getDomainToOligomerCombinationsMap(variableDomains,oligomerDomains);
                dto = getDomainToOligomersMap(variableDomains,oligomerDomains);

                fd = fixedDomains;
                ivd = variableDomains;
                od = oligomerDomains;
                oligomerCombinations = util.getCombinations(od.keySet());

                Map<String,int[]> efd = util.encode(fd); //encoded fixed domains
                Map<String,int[]> eivd = util.encode(ivd); // encoded initial variable domains
                Map<String,int[]> uevd = util.getUniquelyEncodedVariableBases(eivd); // uniequely encoded initial variable domains
                Map<String,int[]> ueo = util.assembleEncodedOligomers(efd, uevd, od); // 

                //Map<Integer,Integer> baselineIntraUnique = util.countIntraOligomerUnique(ueo,intraSLC);
                baselineO = calculateO(ueo);

                //Map<Integer,Integer> baselineInterUnique = util.countInterOligomerUnique( ueo, oligomerCombinations, interSLC);
                baselineN = calculateN(ueo);

                baselineW = calculateW(ueo);
            }
            
            //returns an array of int[3][] where the first array is length of the possible duplex, array 2 is index of the left most base on o1, array 3 is the index of the left most base on 02
            private int[][] calculateInterAlignments (int S1length, int S2length){
                ArrayList<Integer> lengths = new ArrayList<>();
                ArrayList<Integer> o1b1 = new ArrayList<>();
                ArrayList<Integer> o2b1 = new ArrayList<>();
                
                // for the first oligomer in each combination	
                final int b1Max = S1length-1;
                final int b2Max = S2length-1;
                
                for( int j = 0; j <b2Max+1; j++ ){
                    int length = 1;
                    int b1 = 0; // index of base on the top strand;
                    int b2 = (b2Max + j ) % (b2Max+1);// index of base on the bottom strand;
                    
                    while (b1 < b1Max) {
                        if(b2 == 0){
                            lengths.add(length);
                            o1b1.add(b1-length+1);
                            o2b1.add(b2+length-1);
                            b2= b2Max+1;
                            length = 0;
                        }
                        
                        length++;
                        b1++;
                        b2--;
                    }
                    //if the loop ended with an active structure, record it.
                    lengths.add(length);
                    o1b1.add(b1-length+1);
                    o2b1.add(b2+length-1);
                }
                int[][] ret = new int[3][];
                ret[0] = lengths.stream().mapToInt(i->i).toArray();
                ret[1] = o1b1.stream().mapToInt(i->i).toArray();
                ret[2] = o2b1.stream().mapToInt(i->i).toArray();
                return ret;
            }
            
            private BigInteger calculateO (Map<String,int[]> encodedOligomers){
                Map<Integer,Integer> lengthCounts = new HashMap<>();
                //for each alignment
                
                // for each oligomer
                for(Map.Entry<String,int[]> entry : encodedOligomers.entrySet()){
                    String oligomerName = entry.getKey();
                    int[] encodedOligomer = entry.getValue();
                    int[] S1 = encodedOligomer;
                    int S1length = S1.length;
                    int b1Max = S1length-1;

                    for (int j : IntStream.range(0,S1length).toArray()){
                        int structureLength = 0;
                        int b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                        int b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                        int length = S1length/2;
                        if(S1length % 2 == 0 && j%2 == 1)
                        {
                            length = length -1;
                        }

                        if(S1[b1] + S1[b2] ==0)
                        {
                            structureLength = 1;
                        }

                        //For every base-pair in the reference position
                        for ( int k =1; k < length; k++)
                        {
                            if( b1 == b1Max) 
                            {
                                    if (structureLength >= intraSLC)
                                    {
                                        lengthCounts.merge(structureLength,1,Integer::sum);
                                    }
                                    b1 = 0;
                                    structureLength = 0;
                            } else {b1++;}

                            if( b2 == 0) 
                            {
                                    if (structureLength >= intraSLC)
                                    {
                                        lengthCounts.merge(structureLength,1,Integer::sum);
                                    }
                                    b2 = b1Max;
                                    structureLength = 0;
                            } else {b2--;}

                            if(S1[b1]+S1[b2]==0)
                            {
                                    structureLength++;
                            }
                            else
                            {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts.merge(structureLength,1,Integer::sum);
                                }
                                structureLength =0;
                            }
                        }

                        //if the loop ended with an active structure, record it.
                        if (structureLength >= intraSLC)
                        {
                            lengthCounts.merge(structureLength,1,Integer::sum);
                        };
                    }
                }
                
                
                BigInteger retScore = BigInteger.valueOf(0);
                for(Map.Entry<Integer,Integer> entry: lengthCounts.entrySet()){
                    int length = entry.getKey();
                    int counts = entry.getValue();
                    BigInteger lengthScore = knownIntraScores.computeIfAbsent(length, (x)->util.calculateUniqueDuplexPoints(x, intraSLC, intraSB));
                    retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
                }
                
                return retScore;
            }
            
            private BigInteger calculateN (Map<String,int[]> encodedOligomers){
                Map<Integer,Integer> lengthCounts = new HashMap<>();
                int[][] oligomers = encodedOligomers.values().toArray(new int[0][]);
                //for each alignment
                
                // for the first oligomer in each combination
                for(int i1 = 0; i1 < oligomers.length; i1++){
                    int[] S1Bases = oligomers[i1];
                    final int S1length = S1Bases.length;		
                    final int b1Max = S1length-1;
                    // for the second base in each combination
                    for (int i2 = i1; i2 < oligomers.length; i2++){
                        int[] S2Bases = oligomers[i2];
                        final int S2length = S2Bases.length;
                        final int b2Max = S2length-1;

                        for( int j = 0; j <b2Max+1; j++ ){
                            int structureLength = 0;
                            int b1 = 0; // index of base on the top strand;
                            int b2 = (b2Max + j ) % (b2Max+1);// index of base on the bottom strand;

                            // consider the first base pair.
                            if (S1Bases[b1]+S2Bases[b2]==0){
                                    structureLength = 1;
                            };

                            while (b1 < b1Max){
                                    b1++;

                                    if( b2 == 0){

                                            if (structureLength >= interSLC){
                                                lengthCounts.merge(structureLength,1,(x,y)->x+y);
                                            }
                                            b2 = b2Max;
                                            structureLength = 0;

                                    } else {b2--;};

                                    //if the bases are complementary, increase structure length, else record structure;
                                    if (S1Bases[b1]+S2Bases[b2]==0)
                                    {
                                            structureLength++;
                                    } else
                                    {
                                            if (structureLength >= interSLC)
                                            {
                                                lengthCounts.merge(structureLength,1,(x,y)->x+y);
                                            }
                                            structureLength = 0;
                                    };
                            };
                            //if the loop ended with an active structure, record it.
                            if (structureLength >= interSLC)
                            {
                                lengthCounts.merge(structureLength,1,(x,y)->x+y);
                            };
                        }
                    }
                }
                
                BigInteger retScore = BigInteger.valueOf(0);
                for(Map.Entry<Integer,Integer> entry: lengthCounts.entrySet()){
                    int length = entry.getKey();
                    int counts = entry.getValue();
                    BigInteger lengthScore = knownInterScores.computeIfAbsent(length, (x)->util.calculateUniqueDuplexPoints(x, interSLC, interSB));
                    retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
                }
                
                return retScore;
            }
            
            private BigInteger calculateW (Map<String,int[]> encodedOligomers){
                BigInteger O = calculateO(encodedOligomers);
                BigInteger N = calculateN(encodedOligomers);
                return O.multiply(BigInteger.valueOf(swx)).add(N);
            }
            
            private BigInteger calculateAffectedO (Map<String,int[]> encodedOligomers, String modifiedDomain){
                Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
                String[] ao = dto.get(modifiedDomain); //afffected oligomer
                
                AtomicInteger oneCounts = new AtomicInteger(0);
                int[] encodedOligomer;
                int[] S1;
                int S1length;
                int b1Max;
                int structureLength;
                int b1;
                int b2;
                //int j;
                
                // for each oligomer
                for(String o : ao){
                    String oligomerName = o;
                    encodedOligomer = encodedOligomers.get(o);
                    S1 = encodedOligomer;
                    S1length = S1.length;
                    b1Max = S1length-1;
                    //j=0;

                    for (int j : IntStream.range(0,S1length).toArray()) {
                        structureLength = 0;
                        b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                        b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                        int length = S1length/2;
                        if(S1length % 2 == 0 && j%2 == 1)
                        {
                            length = length -1;
                        }

                        if(S1[b1] + S1[b2] ==0)
                        {
                            structureLength = 1;
                        }

                        //For every base-pair in the reference position
                        for ( int k =1; k < length; k++)
                        {
                            if( b1 == b1Max) 
                            {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                                }
                                b1 = 0;
                                structureLength = 0;
                            } else {b1++;}

                            if( b2 == 0) 
                            {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                                }
                                b2 = b1Max;
                                structureLength = 0;
                            } else {b2--;}

                            if(S1[b1]+S1[b2]==0)
                            {
                                structureLength++;
                            }
                            else
                            {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                                }
                                structureLength =0;
                            }
                        }

                        //if the loop ended with an active structure, record it.
                        if (structureLength >= intraSLC)
                        {
                            lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                        }
                        j++;
                    };
                }
                
                BigInteger retScore = BigInteger.valueOf(0);
                for(Map.Entry<Integer,AtomicInteger> entry: lengthCounts.entrySet()){
                    int length = entry.getKey();
                    int counts = entry.getValue().get();
                    BigInteger lengthScore = knownIntraScores.computeIfAbsent(length, (x)->util.calculateUniqueDuplexPoints(x, intraSLC, intraSB));
                    retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
                }
                
                return retScore;
            }
            
            private BigInteger calculateAffectedN (Map<String,int[]> encodedOligomers, String modifiedDomain){
                Map<Integer,AtomicInteger> lengthCounts = new ConcurrentHashMap<>();
                
                String[][] aoc = dtoc.get(modifiedDomain); //afffected oligomer combinations
                
                //int S1length;
                //int b1Max;
                //int S2length;
                //int b2Max;
                //int structureLength;
                //int b1;
                //int b2;
                //int j;
                
                // for each oligomer combination
                IntStream.range(0,aoc[0].length).forEach(i->{
                //for(int i : IntStream.range(0,aoc[0].length).toArray()){
                    String oligomer1Name = aoc[0][i];
                    String oligomer2Name = aoc[1][i];
                    int[] S1Bases = encodedOligomers.get(oligomer1Name);
                    int[] S2Bases = encodedOligomers.get(oligomer2Name);
                    if (S1Bases.length < S2Bases.length){
                        int[] temp = S1Bases;
                        S1Bases = S2Bases;
                        S2Bases = temp;
                    }
                    int S1length = S1Bases.length;		
                    int b1Max = S1length-1;
                    int S2length = S2Bases.length;
                    int b2Max = S2length-1;
                    //j = 0;
                    for (int j : IntStream.range(0,S2length).toArray()){
                        int structureLength = 0;
                        int b1 = 0; // index of base on the top strand;
                        int b2 = (b2Max + j ) % (b2Max+1);// index of base on the bottom strand;

                        // consider the first base pair.
                        if (S1Bases[b1]+S2Bases[b2]==0){
                                structureLength = 1;
                        };

                        while (b1 < b1Max){
                            b1++;

                            if( b2 == 0){
                                if (structureLength >= interSLC){
                                    lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                                }
                                b2 = b2Max;
                                structureLength = 0;
                            } else {b2--;};

                            //if the bases are complementary, increase structure length, else record structure;
                            if (S1Bases[b1]+S2Bases[b2]==0){
                                structureLength++;
                            } else
                            {
                                if (structureLength >= interSLC){
                                    lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                                }
                                structureLength = 0;
                            };
                        };
                        //if the loop ended with an active structure, record it.
                        if (structureLength >= interSLC)
                        {
                            lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                        };
                        j++;
                    };
                });
                
                BigInteger retScore = BigInteger.valueOf(0);
                for(Map.Entry<Integer,AtomicInteger> entry: lengthCounts.entrySet()){
                    int length = entry.getKey();
                    int counts = entry.getValue().get();
                    BigInteger lengthScore = knownInterScores.computeIfAbsent(length, (x)->util.calculateUniqueDuplexPoints(x, interSLC, interSB));
                    retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
                }
                
                return retScore;
            }
            
            private BigInteger calculateAffectedW (Map<String,int[]> encodedOligomers, String modifiedDomain){
                BigInteger O = calculateAffectedO(encodedOligomers,modifiedDomain);
                BigInteger N = calculateAffectedN(encodedOligomers,modifiedDomain);
                return O.multiply(BigInteger.valueOf(swx)).add(N);
            }

            private Map<String,String[][]> getDomainToOligomerCombinationsMap (Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
                Map<String,Set<String>> DTOM = new HashMap<>(); //Domains To Oligomers Map

                //initialize an entry for every domain;
                for (Map.Entry<String,String> variableDomain : variableDomains.entrySet()){
                    DTOM.put(variableDomain.getKey(),new TreeSet<>());
                }

                //for each oligomer
                    // for each domain in the oligomer
                        // add an entry for this association.

                for (Map.Entry<String,String[]> entry1 : oligomerDomains.entrySet()){
                    String oligomerName = entry1.getKey();
                    String[] domainNames = entry1.getValue();
                    for(String domainName : domainNames){
                        if (domainName.startsWith("c.")){
                            String compName = domainName.substring(2);
                            if (variableDomains.containsKey(compName)){
                                Set<String> currentOligomers = DTOM.get(compName);
                                currentOligomers.add(oligomerName);
                            }
                        } else {
                            if (variableDomains.containsKey(domainName)){
                                Set<String> currentOligomers = DTOM.get(domainName);
                                currentOligomers.add(oligomerName);
                            }
                        }
                    }
                }

                //for each domain
                    // make a 2D array of strings to store the first and second oligomer.
                    //for all oligomers the domain affects.
                        // put the combinations of these oligomers into the combinations list.
                    // for each oligomer the domain affects.
                        // for every other oligomer.
                            // if the oligomer has not been modified.
                            // add it to the list of combinations.

                Set<String> allOligomers = oligomerDomains.keySet();
                String[] allOligomersArray = allOligomers.toArray(new String[0]);

                Map<String,String[][]> ret = new HashMap<>();

                for (String variableDomain : variableDomains.keySet()){
                    ArrayList<String> firstOligomers = new ArrayList<>();
                    ArrayList<String> secondOligomers = new ArrayList<>();

                    Set<String> affectedOligomers = DTOM.get(variableDomain);
                    String[] affectedOligomersArray = affectedOligomers.toArray(new String[0]);
                    for (int i = 0; i < affectedOligomersArray.length; i++){
                        for (int j = i; j < affectedOligomersArray.length; j++){
                            firstOligomers.add(affectedOligomersArray[i]);
                            secondOligomers.add(affectedOligomersArray[j]);
                        }
                    }

                    for (int i = 0; i < affectedOligomersArray.length; i++){
                        for (int j = 0; j < allOligomersArray.length; j++){
                            if (!affectedOligomers.contains(allOligomersArray[j])){
                                firstOligomers.add(affectedOligomersArray[i]);
                                secondOligomers.add(allOligomersArray[j]);
                            }
                        }
                    }
                    String[][] value = new String[2][];
                    value[0] = firstOligomers.toArray(new String[0]);
                    value[1] = secondOligomers.toArray(new String[0]);
                    ret.put(variableDomain, value);
                }

                return ret;
            }

            private Map<String,String[]> getDomainToOligomersMap (Map<String,String> variableDomains, Map<String,String[]> oligomerDomains){
                Map<String,Set<String>> DTOM = new HashMap<>(); //Domains To Oligomers Map

                //initialize an entry for every domain;
                for (Map.Entry<String,String> variableDomain : variableDomains.entrySet()){
                    DTOM.put(variableDomain.getKey(),new TreeSet<>());
                }

                //for each oligomer
                    // for each domain in the oligomer
                        // add an entry for this association.

                for (Map.Entry<String,String[]> entry1 : oligomerDomains.entrySet()){
                    String oligomerName = entry1.getKey();
                    String[] domainNames = entry1.getValue();
                    for(String domainName : domainNames){
                        if (domainName.startsWith("c.")){
                            String compName = domainName.substring(2);
                            if (variableDomains.containsKey(compName)){
                                Set<String> currentOligomers = DTOM.get(compName);
                                currentOligomers.add(oligomerName);
                            }
                        } else {
                            if (variableDomains.containsKey(domainName)){
                                Set<String> currentOligomers = DTOM.get(domainName);
                                currentOligomers.add(oligomerName);
                            }
                        }
                    }
                }

                Map<String,String[]> ret = new HashMap<>();
                for(Map.Entry<String,Set<String>> entry : DTOM.entrySet()){
                    ret.put(entry.getKey(),entry.getValue().toArray(new String[0]));
                }
                return ret;
            }

            public String getScore(Map<String,int[]> encodedOligomers){
                Map<String,String> retScores = new HashMap<>();

                //Map<Integer,Integer> intraUnique = util.countIntraOligomerUnique(encodedOligomers,intraSLC);
                //BigInteger O = calculateO(encodedOligomers);
                //Map<Integer,Integer> interUnique = util.countInterOligomerUnique(encodedOligomers, oligomerCombinations, interSLC);
                //BigInteger N = calculateN(encodedOligomers);
                
                BigInteger W = calculateW(encodedOligomers);
                BigInteger deltaW = W.subtract(baselineW);

                return deltaW.toString();
            }

            public String getUpdatedScore(Network existingNetwork, Map<String, int[]> newVariableDomains, Map<String, int[]> newOligomers, String updatedDomain) {
                BigInteger previousW = new BigInteger(existingNetwork.getScore());

                Map<String,int[]> oldOligomers = existingNetwork.getEncodedOligomerSequences();
                BigInteger oldW = calculateAffectedW(oldOligomers, updatedDomain);

                BigInteger newW = calculateAffectedW(newOligomers, updatedDomain);

                String ret = previousW.subtract(oldW).add(newW).toString();
                return ret;
            }

            public Map<String,String> getUsedParameters(){
                return usedParameters;
            }
        }
    }
    
    public static void main(String[] args){
        Map<String,String> usedParameters = new HashMap<>();
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
