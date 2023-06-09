/*
 * The MIT License
 *
 * Copyright 2023 mtobi.
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
package edu.boisestate.osp.domainbasedencodednetwork;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class DeltaWScorer implements IScorer{
    final int intraSLC;
    final int intraSB;
    final int interSLC;
    final int interSB;
    final int maxLength;
    final int swx;
    
    final Map<Integer,int[]> knownRanges;
    
    final Map<Integer,BigInteger> knownIntraScores;
    final Map<Integer,BigInteger> knownInterScores;

    final BigInteger baselineO;
    final BigInteger baselineN;
    final BigInteger baselineW;
    
    final int NUMBERTHREADS;
    final ScoringSupervisor ss;
    
    public DeltaWScorer(Map<String,String> fixedDomains, Map<String,String[]> oligomerDomains, Map<String,String> variableDomains, int intraSB, int intraSLC, int interSB, int interSLC, int swx, int numberThreads){
        knownIntraScores = new ConcurrentHashMap<>();
        knownInterScores = new ConcurrentHashMap<>();
        knownRanges = new ConcurrentHashMap<>();
        
        this.intraSB = intraSB;
        this.intraSLC = intraSLC;
        this.interSB = interSB;
        this.interSLC = interSLC;
        this.swx = swx;

        Map<String,int[]> efd = encode(fixedDomains); // encoded fixed domains
        Map<String,int[]> uevd = getUniquelyEncodedDomains(variableDomains); // uniequely encoded initial variable domains
        Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, oligomerDomains);
        int[][] ueoArray = ueo.values().stream().toArray(i->new int[i][]);
        
        maxLength = Arrays.stream(ueoArray).mapToInt(x -> x.length).max().getAsInt();
        
        baselineO = calculateO(ueoArray);
        baselineN = calculateN(ueoArray);
        baselineW = calculateW(baselineO,baselineN);
        
        NUMBERTHREADS = numberThreads;
        ss = new ScoringSupervisor(NUMBERTHREADS);
    }
    
    private class InnerNetwork implements IDomainBasedEncodedScoredNetwork{
        final IDomainBasedEncodedNetwork unscoredNetwork;
        final String score;
        final Object scorer = DeltaWScorer.this;
        
        InnerNetwork(IDomainBasedEncodedNetwork unscoredNetwork, String score){
            this.unscoredNetwork = unscoredNetwork;
            this.score = score;
        }
        
        @Override
        public String getScore() {
            return score;
        }
        
        @Override
        public Object getScorer() {
            return scorer;
        }

        @Override
        public int[][] getFixedDomainEncodedSequences() {
            return unscoredNetwork.getFixedDomainEncodedSequences();
        }

        @Override
        public Map<String, Integer> getFixedDomainIndices() {
            return unscoredNetwork.getFixedDomainIndices();
        }

        @Override
        public String[] getFixedDomainNames() {
            return unscoredNetwork.getFixedDomainNames();
        }

        @Override
        public String[] getFixedDomainSequences() {
            return unscoredNetwork.getFixedDomainSequences();
        }

        @Override
        public Map<String, Integer> getOligomerIndices() {
            return unscoredNetwork.getOligomerIndices();
        }

        @Override
        public String[][] getOligomerDomains() {
            return unscoredNetwork.getOligomerDomains();
        }

        @Override
        public String[] getOligomerNames() {
            return unscoredNetwork.getOligomerNames();
        }

        @Override
        public String[] getOligomerSequences() {
            return unscoredNetwork.getOligomerSequences();
        }

        @Override
        public int[][] getOligomerSequencesEncoded() {
            return unscoredNetwork.getOligomerSequencesEncoded();
        }
        
        @Override
        public boolean[][] getOligomerBaseIsVariableArray(){
            return unscoredNetwork.getOligomerBaseIsVariableArray();
        }

        @Override
        public Map<String, Integer> getVariableDomainIndices() {
            return unscoredNetwork.getVariableDomainIndices();
        }

        @Override
        public String[] getVariableDomainNames() {
            return unscoredNetwork.getVariableDomainNames();
        }

        @Override
        public String[] getVariableDomainSequences() {
            return unscoredNetwork.getVariableDomainSequences();
        }

        @Override
        public int[][] getVariableDomainSequencesEncoded() {
            return unscoredNetwork.getVariableDomainSequencesEncoded();
        }

        @Override
        public Map<Integer, Map<Integer, int[]>> getVariableDomainToOligomerCoordinates() {
            return unscoredNetwork.getVariableDomainToOligomerCoordinates();
        }

        @Override
        public Map<Integer, Map<Integer, int[]>> getVariableDomainComplementToOligomerCoordinates() {
            return unscoredNetwork.getVariableDomainComplementToOligomerCoordinates();
        }
        
        @Override
        public Map<Integer, Set<Integer>> getVariableDomainToOligomerIndices() {
            return unscoredNetwork.getVariableDomainToOligomerIndices();
        }
        
        @Override
        public Map<Integer,int[][]> getVariableDomainToOligomerCombinations(){
            return unscoredNetwork.getVariableDomainToOligomerCombinations();
        }
        
    }
    
    @Override
    public int compareFitness(IDomainBasedEncodedScoredNetwork network1, IDomainBasedEncodedScoredNetwork network2){
        BigInteger score1;
        if(network1.getScorer() == DeltaWScorer.this){
            score1 = new BigInteger(network1.getScore());
        } else {
           score1 = calculateW(network1.getOligomerSequencesEncoded()).subtract(baselineW);
        }
        
        BigInteger score2;
        if(network2.getScorer() == DeltaWScorer.this){
            score2 = new BigInteger(network2.getScore());
        } else {
           score2 = calculateW(network2.getOligomerSequencesEncoded()).subtract(baselineW);
        }
       
       return -score1.compareTo(score2);
    }
    
    /**
    * Returns a scored version of the given network;
    * @param network
    * @return
    */
    @Override
   public IDomainBasedEncodedScoredNetwork getScored(IDomainBasedEncodedNetwork network){
       String score = getScoreString(network);
       return new InnerNetwork(network,score);
   }

   /**
    * Returns a version of the given network scored using this scorer;
    * @param network
    * @return
    */
    @Override
   public IDomainBasedEncodedScoredNetwork getScored(IDomainBasedEncodedScoredNetwork network){
       String score = getScoreString(network);
       return new InnerNetwork(network,score);
   }

   /**
    * Returns a scored version of the given network.
    * @param previousNetwork The prior network
    * @param newNetwork The new network which has had one variable domain updated.
    * @param updatedDomainIndex The domain index of the variable domain which was updated.
    * @return
    */
    @Override
    public IDomainBasedEncodedScoredNetwork getScored(IDomainBasedEncodedScoredNetwork previousNetwork, IDomainBasedEncodedNetwork newNetwork, int updatedDomainIndex){
        String score = ss.getScoreString(DeltaWScorer.this, previousNetwork, newNetwork, updatedDomainIndex);
        return new InnerNetwork(newNetwork,score);

    }

    @Override
    public String getScoreLabel(){
        return "Wx";
    }
    
    /**
     * Returns a human-readable string for describing the units of this score.
     * @return
     */
    @Override
    public String getScoreUnits(){
        return "fitness points";
    }
    
    private String getScoreString(IDomainBasedEncodedNetwork network){
        BigInteger W = calculateW(network.getOligomerSequencesEncoded());
        BigInteger deltaW = W.subtract(baselineW);

        return deltaW.toString();
   }
   
    private String getScoreString(IDomainBasedEncodedScoredNetwork previousNetwork, IDomainBasedEncodedNetwork newNetwork, int updatedVariableDomainIndex){
        BigInteger deltaW;
       
        if(previousNetwork.getScorer() == DeltaWScorer.this){
            BigInteger newPartialW = calculateAffectedW(newNetwork, updatedVariableDomainIndex);
            BigInteger oldPartialW = calculateAffectedW(previousNetwork, updatedVariableDomainIndex);
            BigInteger oldDeltaW = new BigInteger(previousNetwork.getScore());
            deltaW = oldDeltaW.subtract(oldPartialW).add(newPartialW);
        } else {
           deltaW = calculateW(newNetwork.getOligomerSequencesEncoded()).subtract(baselineW);
        }
       
       return deltaW.toString();
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

    private BigInteger calculateO (int[][] encodedOligomers){
        Map<Integer,Integer> lengthCounts = new HashMap<>();
        //for each alignment

        int[] encodedOligomer;
        int[] S1;
        int S1length;
        int b1Max;
        int structureLength;
        int b1;
        int b2;
        int length;
        Integer singleCount = 1;
        // for each oligomer
        for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
            encodedOligomer = encodedOligomers[i];
            S1 = encodedOligomer;
            S1length = S1.length;
            b1Max = S1length-1;

            for (int j : IntStream.range(0,S1length).toArray()){
                structureLength = 0;
                b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                length = S1length/2;
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
                                lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            b1 = 0;
                            structureLength = 0;
                    } else {b1++;}

                    if( b2 == 0) 
                    {
                            if (structureLength >= intraSLC)
                            {
                                lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
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
                            lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                        }
                        structureLength =0;
                    }
                }

                //if the loop ended with an active structure, record it.
                if (structureLength >= intraSLC)
                {
                    lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                };
            }
        }


        BigInteger retScore = BigInteger.valueOf(0);
        for(Map.Entry<Integer,Integer> entry: lengthCounts.entrySet()){
            length = entry.getKey();
            int counts = entry.getValue();
            BigInteger lengthScore = knownIntraScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, intraSLC, intraSB));
            retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
        }

        return retScore;
    }

    private BigInteger calculateN (int[][] encodedOligomers){
        Map<Integer,Integer> lengthCounts = new HashMap<>();
        int[][] oligomers = encodedOligomers;
        //for each alignment
        
        int[] S1Bases;
        int S1length;
        int b1Max;
        int[] S2Bases;
        int S2length;
        int b2Max;
        int structureLength;
        int b1;
        int b2;
        Integer singleCount = Integer.valueOf(1);

        // for the first oligomer in each combination
        for(int i1: IntStream.range(0,oligomers.length).toArray()){
            S1Bases = oligomers[i1];
            S1length = S1Bases.length;		
            b1Max = S1length-1;
            // for the second oligomer in each combination
            for (int i2 = i1; i2 < oligomers.length; i2++){
                S2Bases = oligomers[i2];
                S2length = S2Bases.length;
                b2Max = S2length-1;

                for( int j = 0; j <S2length; j++ ){
                    structureLength = 0;
                    b1 = 0; // index of base on the top strand;
                    b2 = (b2Max + j ) % (b2Max+1);// index of base on the bottom strand;

                    // consider the first base pair.
                    if (S1Bases[b1] == -S2Bases[b2]){
                            structureLength = 1;
                    };

                    while (b1 < b1Max){
                            b1++;

                            if( b2 == 0){

                                    if (structureLength >= interSLC){
                                        lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                    }
                                    b2 = b2Max;
                                    structureLength = 0;

                            } else {b2--;};

                            //if the bases are complementary, increase structure length, else record structure;
                            if (S1Bases[b1] == -S2Bases[b2])
                            {
                                    structureLength++;
                            } else
                            {
                                    if (structureLength >= interSLC)
                                    {
                                        lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                    }
                                    structureLength = 0;
                            };
                    };
                    //if the loop ended with an active structure, record it.
                    if (structureLength >= interSLC)
                    {
                        lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    };
                }
            }
        }

        BigInteger retScore = BigInteger.valueOf(0);
        for(Map.Entry<Integer,Integer> entry: lengthCounts.entrySet()){
            int length = entry.getKey();
            int counts = entry.getValue();
            BigInteger lengthScore = knownInterScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, interSLC, interSB));
            retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
        }

        return retScore;
    }

    private BigInteger calculateW (int[][] encodedOligomers){
        BigInteger O = calculateO(encodedOligomers);
        BigInteger N = calculateN(encodedOligomers);
        return O.multiply(BigInteger.valueOf(swx)).add(N);
    }
    
    private BigInteger calculateW (BigInteger O, BigInteger N){
        return O.multiply(BigInteger.valueOf(swx)).add(N);
    }

    private BigInteger calculateAffectedO (IDomainBasedEncodedNetwork network, int updatedVariableDomainIndex){
        int[] lengthCounts = new int[maxLength+1];
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        Set<Integer> affectedOligomerIndices = network.getVariableDomainToOligomerIndices().get(updatedVariableDomainIndex);
        //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
        
        int[] encodedOligomer;
        int[] S1;
        int S1length;
        int b1Max;
        int structureLength;
        int b1;
        int b2;
        //int j;

        // for each oligomer
        for(Integer i : affectedOligomerIndices){
            encodedOligomer = encodedOligomers[i];
            S1 = encodedOligomer;
            S1length = S1.length;
            b1Max = S1length-1;
            //j=0;

            for( int j : knownRanges.computeIfAbsent(S1length,x->IntStream.range(0,x).toArray())){
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
                            lengthCounts[structureLength]++;
                            //lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                        }
                        b1 = 0;
                        structureLength = 0;
                    } else {b1++;}

                    if( b2 == 0) 
                    {
                        if (structureLength >= intraSLC)
                        {
                            lengthCounts[structureLength]++;
                            //lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
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
                            lengthCounts[structureLength]++;
                            //lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                        }
                        structureLength =0;
                    }
                }

                //if the loop ended with an active structure, record it.
                if (structureLength >= intraSLC)
                {
                    lengthCounts[structureLength]++;
                    //lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                }
            };
        }

        BigInteger retScore = BigInteger.valueOf(0);
        for(int i : knownRanges.computeIfAbsent(lengthCounts.length,x->IntStream.range(0,x).toArray())){
            int length = i;
            int counts = lengthCounts[i];
            if (counts >0){
                BigInteger lengthScore = knownIntraScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, intraSLC, intraSB));
                retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
            }
        }

        return retScore;
    }

    private BigInteger calculateAffectedN (IDomainBasedEncodedNetwork network, int updatedVariableDomainIndex){
        //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
        
        int[] lengthCounts = new int[maxLength+1];
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        int[][] aoc = network.getVariableDomainToOligomerCombinations().get(updatedVariableDomainIndex);
        
        int[] S1Bases;
        int[] S2Bases;
        int S1length;
        int b1Max;
        int S2length;
        int b2Max;
        int structureLength;
        int b1;
        int b2;
        
        // for each oligomer combination
        for( int i : knownRanges.computeIfAbsent(aoc[0].length,x->IntStream.range(0,x).toArray())){
            S1Bases = encodedOligomers[aoc[0][i]];
            S2Bases = encodedOligomers[aoc[1][i]];
            S1length = S1Bases.length;		
            b1Max = S1length-1;
            S2length = S2Bases.length;
            b2Max = S2length-1;
            for (int j : knownRanges.computeIfAbsent(S2length,x->IntStream.range(0,x).toArray())){
                structureLength = 0;
                b1 = 0; // index of base on the top strand;
                b2 = (b2Max + j) % (S2length);// index of base on the bottom strand;
                
                // for each base in the stretch.
                do{
                    //are the current bases complementary?
                    if (S1Bases[b1] + S2Bases[b2] == 0){
                        structureLength++;
                    } else {
                        if (structureLength >= interSLC){
                            lengthCounts[structureLength]++;
                            //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            //lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                        }
                        structureLength = 0;
                    }
                    
                    //increment
                    b1++;
                    if(b2 == 0){
                        if (structureLength >= interSLC){
                            lengthCounts[structureLength]++;
                            //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            //lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                        }
                        b2 = b2Max;
                        structureLength = 0;
                    } else {b2--;}
                } while (b1 <= b1Max);

                //if the loop ended with an active structure, record it.
                if (structureLength >= interSLC){
                    lengthCounts[structureLength]++;
                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    //lengthCounts.computeIfAbsent(structureLength,(x)->new AtomicInteger(0)).incrementAndGet();
                }
            }
        }

        //System.out.println("Inter Oligomer Structures:");
        BigInteger retScore = BigInteger.valueOf(0);
        for(int i : knownRanges.computeIfAbsent(lengthCounts.length,x->IntStream.range(0,x).toArray())){
            int length = i;
            int counts = lengthCounts[i];
            if (counts >0){
                //System.out.println(length+", "+counts);
                BigInteger lengthScore = knownInterScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, interSLC, interSB));
                retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
            }
        }
        return retScore;
    }

    private BigInteger calculateAffectedW (IDomainBasedEncodedNetwork network, int updatedVariableDomainIndex){
        BigInteger O = calculateAffectedO(network,updatedVariableDomainIndex);
        BigInteger N = calculateAffectedN(network,updatedVariableDomainIndex);
        return O.multiply(BigInteger.valueOf(swx)).add(N);
    }
    
    private static Map<String,int[]> getUniquelyEncodedDomains(Map<String,String> domains){
        Map<String, int[]> uniqueDomains = new HashMap<>();
        int currentBase = 2; 
        for ( Map.Entry<String,String> entry : domains.entrySet()){
            int length = entry.getValue().trim().length();
            int[] newV = new int[length];
            for (int i = 0; i < length; i++){
                currentBase++;
                newV[i] = currentBase;
            }
            uniqueDomains.put(entry.getKey(), newV);
        }
        return uniqueDomains;
    }
    
    private static Map<String,int[]> assembleEncodedOligomers(Map<String,int[]> fixedDomains, Map<String,int[]> variableDomains, Map<String,String[]> oligomerDomains){
        Map<String,int[]> encodedOligomers = new HashMap<>();

        //for each oligomer
        for (Map.Entry<String,String[]> entry : oligomerDomains.entrySet()){
            String oligomer = entry.getKey();
            String[] domainStrings = entry.getValue();
            ArrayList<int[]> encodedSequences = new ArrayList<>();
            
            int totalLength = 0;
            for( String domain : domainStrings){
                //if domain is a complement
                if (domain.startsWith("c.")){
                    String compName = domain.substring(2);
                    int[] domainSequence = fixedDomains.get(compName);
                    if (domainSequence == null ) domainSequence = variableDomains.get(compName);
                    if (domainSequence == null ) {
                        System.out.println("Could not find complement of domain "+ domain +"." );
                        System.exit(0);
                    }
                    int[] complementSequence = getComplement(domainSequence);
                    encodedSequences.add(complementSequence);
                    totalLength += complementSequence.length;
                } else {
                    int[] domainSequence = fixedDomains.get(domain);
                    if (domainSequence == null ) domainSequence = variableDomains.get(domain);
                    if (domainSequence == null ) {
                        System.out.println("Could not find domain "+ domain +"." );
                        System.exit(0);
                    }
                    encodedSequences.add(domainSequence);
                    totalLength += domainSequence.length;
                }
            }
            int[] encodedOligomer = new int[totalLength];
            int nextBase = 0;
            for (int[] sequence : encodedSequences){
                System.arraycopy(sequence, 0, encodedOligomer, nextBase, sequence.length);
                nextBase += sequence.length;
            }
            encodedOligomers.put(oligomer,encodedOligomer);
        }
        return encodedOligomers;
    }
    
    private static int[] getComplement(int[] encodedSequence){
        int[] retSequence = new int[encodedSequence.length];
        IntStream.range(0,encodedSequence.length).forEach(i-> retSequence[i] = -encodedSequence[encodedSequence.length-i-1]);
        return retSequence;
    }
    
    private static String[][] getCombinations(Set<String> oligomers){
        ArrayList<String> CO1 = new ArrayList<>(); //Combination Oligomer-1
        ArrayList<String> CO2 = new ArrayList<>(); // Combination Oligomer-2
        
        String[] oligomerArray = oligomers.toArray(new String[0]);
                
        for (int i = 0; i < oligomerArray.length;i++){
            for (int j = i; j < oligomerArray.length;j++){
                CO1.add(oligomerArray[i]);
                CO2.add(oligomerArray[j]);
            }
        }
        
        String[][] retArray = {CO1.toArray(new String[0]),CO2.toArray(new String[0])};
        return retArray;
    }
    
    private static BigInteger calculateUniqueDuplexPoints (int length, int slc, int base){
        BigInteger score = BigInteger.valueOf(0);
        
        int numberOfStructures = 1;
        for (int i = length; i >= slc; i--){
            score = score.add(BigInteger.valueOf(base).pow(i).multiply(BigInteger.valueOf(numberOfStructures)));
            numberOfStructures++;
        }
        
        return score;
    }
    
    private static Map<String,int[]> encode(Map<String,String> sequences){
        Map<String,int[]> encoded = new HashMap<>();
        sequences.forEach((k,v)-> {
                char[] b = v.toCharArray();
                int[] e = new int[b.length];
                IntStream.range(0, b.length).forEach(i->e[i]=encode(b[i]));
                encoded.put(k, e);
            }
        );
        return encoded;
    }
    
    private static int encode(char c){
        switch (c){
            case 'a':
            case 'A':
                return -2;
            case 'c':
            case 'C':
                return -1;
            case 'g':
            case 'G':
                return 1;
            case 't':
            case 'T':
                return 2;
            default: 
                System.out.println("Error: Base \"" + c + "\" not recognized." );
                System.exit(0);
                return 0;
        }
    }
    
    static private class ScoringSupervisor{
        ExecutorService es;
        int numberThreads;
        
        ScoringSupervisor(int numberThreads){
            es = Executors.newFixedThreadPool(numberThreads);
            this.numberThreads = numberThreads;
            Runtime.getRuntime().addShutdownHook( new Thread(){
                @Override
                public void run(){
                    es.shutdownNow();
                }
            });
        }
        
        String getScoreString(DeltaWScorer scorer, IDomainBasedEncodedScoredNetwork previousNetwork, IDomainBasedEncodedNetwork newNetwork, int updatedVariableDomainIndex){
            BigInteger deltaW;
       
            if(previousNetwork.getScorer() == scorer){
                int[][] aoc = previousNetwork.getVariableDomainToOligomerCombinations().get(updatedVariableDomainIndex);
                
                // calculate old partial O
                BigInteger oldPartialO = scorer.calculateAffectedO(previousNetwork, updatedVariableDomainIndex);
                
                // start calculation of old partial N
                Callable[] oldSubN = new Callable[numberThreads];
                Future<BigInteger>[] oldFutures = new Future[numberThreads];
                int combPerThread = (aoc[0].length+numberThreads-1)/numberThreads; // Math.ceil of aoc[0].length / numberThreads
                for(int i=0; (i < numberThreads ) && (i < aoc[0].length) ; i++){
                    int firstIndex = i*combPerThread;
                    int lastIndex = Math.min(firstIndex+combPerThread, aoc[0].length);
                    oldSubN[i] = new NCalculator(previousNetwork, scorer, updatedVariableDomainIndex, firstIndex, lastIndex);
                    oldFutures[i] = es.submit(oldSubN[i]);
                }
                
                // calculate new parital O
                BigInteger newPartialO = scorer.calculateAffectedO(newNetwork, updatedVariableDomainIndex);
                
                // start calculation of new partial N
                Callable[] newSubN = new Callable[numberThreads];
                Future<BigInteger>[] newFutures = new Future[numberThreads];
                for(int i=0; (i < numberThreads ) && (i < aoc[0].length) ; i++){
                    int firstIndex = i*combPerThread;
                    int lastIndex = Math.min(firstIndex+combPerThread, aoc[0].length);
                    newSubN[i] = new NCalculator(newNetwork, scorer, updatedVariableDomainIndex, firstIndex, lastIndex);
                    newFutures[i] = es.submit(newSubN[i]);
                }
                
                // finish calculation of old partial N
                BigInteger oldPartialN = BigInteger.valueOf(0);
                for(int i=0; (i < numberThreads ) && (i < aoc[0].length) ; i++){
                    try {
                        oldPartialN = oldPartialN.add(oldFutures[i].get());
                    } catch (Exception e) {System.out.println(e.getMessage());}
                }
                
                BigInteger newPartialN = BigInteger.valueOf(0);
                for(int i=0; (i < numberThreads ) && (i < aoc[0].length) ; i++){
                    try {
                        newPartialN = newPartialN.add(newFutures[i].get());
                    } catch (Exception e) {System.out.println(e.getMessage());}
                }
                
                // calculate partialW
                BigInteger oldPartialW = oldPartialO.multiply(BigInteger.valueOf(scorer.swx)).add(oldPartialN);
                BigInteger newPartialW = newPartialO.multiply(BigInteger.valueOf(scorer.swx)).add(newPartialN);
                BigInteger oldDeltaW = new BigInteger(previousNetwork.getScore());
                deltaW = oldDeltaW.subtract(oldPartialW).add(newPartialW);
            } else {
               deltaW = scorer.calculateW(newNetwork.getOligomerSequencesEncoded()).subtract(scorer.baselineW);
            }
            
            String retString = deltaW.toString();
            return retString;
         }
        
        static private class NCalculator implements Callable{
            final DeltaWScorer scorer;
            final int firstIndex;
            final int lastIndex;
            final IDomainBasedEncodedNetwork network;
            final int updatedDomainIndex; 
            //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
            
            NCalculator(IDomainBasedEncodedNetwork network, DeltaWScorer scorer, int updatedDomainIndex, int firstIndex, int lastIndex){
                this.firstIndex = firstIndex;
                this.lastIndex = lastIndex;
                this.network = network;
                this.scorer = scorer;
                this.updatedDomainIndex = updatedDomainIndex;
            }
        
            public BigInteger call(){
                int[] lengthCounts = new int[scorer.maxLength+1];
                int[][] encodedOligomers = network.getOligomerSequencesEncoded();
                int[][] aoc = network.getVariableDomainToOligomerCombinations().get(updatedDomainIndex);

                int[] S1Bases;
                int[] S2Bases;
                int S1length;
                int b1Max;
                int S2length;
                int b2Max;
                int structureLength;
                int b1;
                int b2;

                // for each oligomer combination
                for( int k : scorer.knownRanges.computeIfAbsent(lastIndex-firstIndex,x->IntStream.range(0,x).toArray())){
                    int i = k+firstIndex;
                    S1Bases = encodedOligomers[aoc[0][i]];
                    S2Bases = encodedOligomers[aoc[1][i]];
                    S1length = S1Bases.length;		
                    b1Max = S1length-1;
                    S2length = S2Bases.length;
                    b2Max = S2length-1;
                    for (int j : scorer.knownRanges.computeIfAbsent(S2length,x->IntStream.range(0,x).toArray())){
                        structureLength = 0;
                        b1 = 0; // index of base on the top strand;
                        b2 = (b2Max + j) % (S2length);// index of base on the bottom strand;

                        do{
                            if (S1Bases[b1] + S2Bases[b2] == 0){
                                structureLength++;
                            } else {
                                if (structureLength >= scorer.interSLC){
                                    lengthCounts[structureLength]++;
                                }
                                structureLength = 0;
                            }

                            b1++;
                            if(b2 == 0){
                                if (structureLength >= scorer.interSLC){
                                    lengthCounts[structureLength]++;
                                }
                                b2 = b2Max;
                                structureLength = 0;
                            } else {b2--;}
                        } while (b1 <= b1Max);

                        //if the loop ended with an active structure, record it.
                        if (structureLength >= scorer.interSLC){
                            lengthCounts[structureLength]++;
                        }
                    }
                }

                BigInteger retScore = BigInteger.valueOf(0);
                for(int i : scorer.knownRanges.computeIfAbsent(lengthCounts.length,x->IntStream.range(0,x).toArray())){
                    int length = i;
                    int counts = lengthCounts[i];
                    if (counts >0){
                        BigInteger lengthScore = scorer.knownInterScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, scorer.interSLC, scorer.interSB));
                        retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
                    }
                }
                return retScore;
            }
        }
    }
        
}
