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
package edu.boisestate.osp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class Analyzer{
    public final static String BASELINE_INTER_DUPLEX_COUNT_LABEL = "baselineInterDuplexCount";
    public final static String BASELINE_INTRA_DUPLEX_COUNT_LABEL = "baselineIntraDuplexCount";
    public final static String BASELINE_N_LABEL = "baselineN";
    public final static String BASELINE_O_LABEL = "baselineO";
    public final static String BASELINE_W_LABEL = "baselineW";
    public final static String DELTA_N_LABEL = "deltaN";
    public final static String DELTA_O_LABEL = "deltaO";
    public final static String DELTA_W_LABEL = "deltaW";
    public final static String N_LABEL = "N";
    public final static String O_LABEL = "O";
    public final static String W_LABEL = "W";
    public final static String INTER_DUPLEX_COUNT_LABEL = "interDuplexCount";
    public final static String INTRA_DUPLEX_COUNT_LABEL = "intraDuplexCount";
    
    public final static String INTER_SB_LABEL = "interSB";
    final static String INTER_SB_DEFAULT = "10";
    public final static String INTER_SLC_LABEL = "interSLC";
    final static String INTER_SLC_DEFAULT = "1";
    public final static String INTRA_SB_LABEL = "intraSB";
    final static String INTRA_SB_DEFAULT = "10";
    public final static String INTRA_SLC_LABEL = "intraSLC";
    final static String INTRA_SLC_DEFAULT = "1";
    public final static String SWX_LABEL = "scoringWeightX";
    final static String SWX_DEFAULT = "10000";

    final int MAXTHREADS;
    final int MAXTHREADSPERNETWORK;
    final AnalysisSupervisor as;
    
    public Analyzer(int maxThreads, int maxThreadsPerNetwork){
        //Map<String,int[]> efd = encode(fixedDomains); // encoded fixed domains
        //Map<String,int[]> uevd = getUniquelyEncodedDomains(variableDomains); // uniequely encoded initial variable domains
        //Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, oligomerDomains);
        //ueoSequences = ueo.values().stream().toArray(i->new int[i][]);
        
        //baselineO = calculateO(ueoSequences);
        //baselineN = calculateN(ueoSequences);
        //baselineW = calculateW(baselineO,baselineN);
        
        MAXTHREADS = maxThreads;
        MAXTHREADSPERNETWORK = maxThreadsPerNetwork;
        as = new AnalysisSupervisor(MAXTHREADS,MAXTHREADSPERNETWORK);
    }
    
    public static class Request{
        IDomainBasedEncodedNetwork network;
        Collection<String> requestedProperties;
        Map<String,String> parameters;
    
        public Request(IDomainBasedEncodedNetwork network, Collection<String> requestedProperties, Map<String,String> parameters){
            this.network = network;
            this.requestedProperties = requestedProperties;
            this.parameters = parameters;
        }
    }
    
    public Report analyze(Request r){
        Collection<String> neededProperties = new HashSet<>();
        Collection<String> neededParameters = new HashSet<>();
        for (String property : r.requestedProperties){
            switch (property){
                case BASELINE_INTRA_DUPLEX_COUNT_LABEL:
                    neededProperties.add(BASELINE_INTRA_DUPLEX_COUNT_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    break;
                    
                case BASELINE_INTER_DUPLEX_COUNT_LABEL:
                    neededProperties.add(BASELINE_INTER_DUPLEX_COUNT_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    break;
                    
                case BASELINE_N_LABEL:
                    neededProperties.add(BASELINE_INTER_DUPLEX_COUNT_LABEL);
                    neededProperties.add(BASELINE_N_LABEL);
                    neededParameters.add(INTER_SB_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    break;
                    
                case BASELINE_O_LABEL:
                    neededProperties.add(BASELINE_INTRA_DUPLEX_COUNT_LABEL);
                    neededProperties.add(BASELINE_O_LABEL);
                    neededParameters.add(INTRA_SB_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    break;
                    
                case BASELINE_W_LABEL:
                    neededProperties.add(BASELINE_INTER_DUPLEX_COUNT_LABEL);
                    neededProperties.add(BASELINE_INTRA_DUPLEX_COUNT_LABEL);
                    neededProperties.add(BASELINE_N_LABEL);
                    neededProperties.add(BASELINE_O_LABEL);
                    neededProperties.add(BASELINE_W_LABEL);
                    neededParameters.add(INTER_SB_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    neededParameters.add(INTRA_SB_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    neededParameters.add(SWX_LABEL);
                    break;
                    
                case DELTA_N_LABEL:
                    neededProperties.add(BASELINE_INTER_DUPLEX_COUNT_LABEL);
                    neededProperties.add(INTER_DUPLEX_COUNT_LABEL);
                    neededProperties.add(DELTA_N_LABEL);
                    neededProperties.add(N_LABEL);
                    neededProperties.add(BASELINE_N_LABEL);
                    neededParameters.add(INTER_SB_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    break;
                    
                case DELTA_O_LABEL:
                    neededProperties.add(BASELINE_INTRA_DUPLEX_COUNT_LABEL);
                    neededProperties.add(INTRA_DUPLEX_COUNT_LABEL);
                    neededProperties.add(DELTA_O_LABEL);
                    neededProperties.add(O_LABEL);
                    neededProperties.add(BASELINE_O_LABEL);
                    neededParameters.add(INTRA_SB_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    break;
                    
                case DELTA_W_LABEL:
                    neededProperties.add(BASELINE_INTER_DUPLEX_COUNT_LABEL);
                    neededProperties.add(BASELINE_INTRA_DUPLEX_COUNT_LABEL);
                    neededProperties.add(DELTA_W_LABEL);
                    neededProperties.add(N_LABEL);
                    neededProperties.add(O_LABEL);
                    neededProperties.add(W_LABEL);
                    neededProperties.add(BASELINE_N_LABEL);
                    neededProperties.add(BASELINE_O_LABEL);
                    neededProperties.add(BASELINE_W_LABEL);
                    neededProperties.add(INTER_DUPLEX_COUNT_LABEL);
                    neededProperties.add(INTRA_DUPLEX_COUNT_LABEL);
                    neededParameters.add(INTER_SB_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    neededParameters.add(INTRA_SB_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    neededParameters.add(SWX_LABEL);
                    break;
                    
                case N_LABEL:
                    neededProperties.add(N_LABEL);
                    neededParameters.add(INTER_SB_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    break;
                    
                case O_LABEL:
                    neededProperties.add(O_LABEL);
                    neededParameters.add(INTRA_SB_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    break;
                    
                case W_LABEL:
                    neededProperties.add(N_LABEL);
                    neededProperties.add(O_LABEL);
                    neededProperties.add(W_LABEL);
                    neededParameters.add(INTER_SB_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    neededParameters.add(INTRA_SB_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    neededParameters.add(SWX_LABEL);
                    break;
                    
                case INTRA_DUPLEX_COUNT_LABEL:
                    neededProperties.add(INTRA_DUPLEX_COUNT_LABEL);
                    neededParameters.add(INTRA_SLC_LABEL);
                    break;
                    
                case INTER_DUPLEX_COUNT_LABEL:
                    neededProperties.add(INTER_DUPLEX_COUNT_LABEL);
                    neededParameters.add(INTER_SLC_LABEL);
                    break;
                    
                default:
                    System.err.println("Property "+ property + " not supported by analyzer.");
                    System.exit(1);
            }
        }
        
        Map<String,String> usedParameters = new HashMap<>();
        for (String parameter : neededParameters){
            switch (parameter){
                case (INTRA_SB_LABEL):{
                    String value = r.parameters.getOrDefault(INTRA_SB_LABEL,INTRA_SB_DEFAULT);
                    usedParameters.put(INTRA_SB_LABEL,value);
                    }
                    break;   
                    
                case (INTRA_SLC_LABEL):{
                    String value = r.parameters.getOrDefault(INTRA_SLC_LABEL,INTRA_SLC_DEFAULT);
                    usedParameters.put(INTRA_SLC_LABEL,value);
                    }
                    break;
                    
                case (INTER_SB_LABEL):{
                    String value = r.parameters.getOrDefault(INTER_SB_LABEL,INTER_SB_DEFAULT);
                    usedParameters.put(INTER_SB_LABEL,value);
                    }
                    break;  
                    
                case (INTER_SLC_LABEL):{
                    String value = r.parameters.getOrDefault(INTER_SLC_LABEL,INTER_SLC_DEFAULT);
                    usedParameters.put(INTER_SLC_LABEL,value);
                    }
                    break;
                    
                case (SWX_LABEL):{
                    String value = r.parameters.getOrDefault(SWX_LABEL,SWX_DEFAULT);
                    usedParameters.put(SWX_LABEL,value);
                    }
                    break;
                    
                default:
                    System.err.println("Parameter "+ parameter + " not supported by analyzer");
                    System.exit(1);
            }
        }
        
        Map<Integer,Integer> baselineIntraDuplexLengthCount = null;
        Map<Integer,Integer> baselineInterDuplexLengthCount = null;
        Map<Integer,Integer> intraDuplexLengthCount = null;
        Map<Integer,Integer> interDuplexLengthCount = null;
        BigInteger baselineN = null;
        BigInteger baselineO = null;
        BigInteger baselineW = null;
        BigInteger deltaN = null;
        BigInteger deltaO = null;
        BigInteger deltaW = null;
        BigInteger N = null;
        BigInteger O = null;
        BigInteger W = null;
                
        
        Map<String,String> calculatedPropertyValues = new HashMap<>();
        
        // if baseline inter duplexes is required calculate it.
        if (neededProperties.contains(BASELINE_INTER_DUPLEX_COUNT_LABEL)){
            baselineInterDuplexLengthCount = as.getBaselineInterDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTER_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : baselineInterDuplexLengthCount.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(BASELINE_INTER_DUPLEX_COUNT_LABEL, valueString.toString());
        }
        
        // if baseline intra duplexes is required calculate it.
        if (neededProperties.contains(BASELINE_INTRA_DUPLEX_COUNT_LABEL)){
            baselineIntraDuplexLengthCount = as.getBaselineIntraDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : baselineIntraDuplexLengthCount.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(BASELINE_INTRA_DUPLEX_COUNT_LABEL, valueString.toString());
        }
        
        // if baselineN is required calculate it.
        if (neededProperties.contains(BASELINE_N_LABEL)){
            baselineN = as.getN(baselineInterDuplexLengthCount, Integer.valueOf(usedParameters.get(INTER_SB_LABEL)), Integer.valueOf(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(BASELINE_N_LABEL, baselineN.toString());
        }
        
        // if baeslineO is required calculate it.
        if (neededProperties.contains(BASELINE_O_LABEL)){
            baselineO = as.getO(baselineIntraDuplexLengthCount, Integer.valueOf(usedParameters.get(INTRA_SB_LABEL)), Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(BASELINE_O_LABEL, baselineO.toString());
        }
        
        // if baselineW is required calculate it.
        if (neededProperties.contains(BASELINE_W_LABEL)){
            baselineW = baselineO.multiply(new BigInteger(usedParameters.get(SWX_LABEL))).add(baselineN);
            calculatedPropertyValues.put(BASELINE_W_LABEL, baselineW.toString());
        }
        
        // if intraduplexes is required, calculate it.
        if (neededProperties.contains(INTRA_DUPLEX_COUNT_LABEL)){
            intraDuplexLengthCount = as.getIntraDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : intraDuplexLengthCount.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(INTRA_DUPLEX_COUNT_LABEL, valueString.toString());
        }
        
        // if interduplexes is required, calculate it.
        if (neededProperties.contains(INTER_DUPLEX_COUNT_LABEL)){
            interDuplexLengthCount = as.getInterDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTER_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : interDuplexLengthCount.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(INTER_DUPLEX_COUNT_LABEL, valueString.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(N_LABEL)){
            N = as.getN(interDuplexLengthCount, Integer.parseInt(usedParameters.get(INTER_SB_LABEL)), Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(N_LABEL, N.toString());
        }
        
        // if O is required calculate it.
        if (neededProperties.contains(O_LABEL)){
            O = as.getO(intraDuplexLengthCount, Integer.parseInt(usedParameters.get(INTRA_SB_LABEL)), Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(O_LABEL, O.toString());
        }
        
        // if W is required calculate it.
        if (neededProperties.contains(W_LABEL)){
            W = O.multiply(new BigInteger(usedParameters.get(SWX_LABEL))).add(N);
            calculatedPropertyValues.put(W_LABEL, W.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(DELTA_N_LABEL)){
            deltaN = N.subtract(baselineN);
            calculatedPropertyValues.put(DELTA_N_LABEL, deltaN.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(DELTA_O_LABEL)){
            deltaO = O.subtract(baselineO);
            calculatedPropertyValues.put(DELTA_O_LABEL, deltaO.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(DELTA_W_LABEL)){
            deltaW = W.subtract(baselineW);
            calculatedPropertyValues.put(DELTA_W_LABEL, deltaW.toString());
        }
        
        Map<String,String> necessaryPropertyValues = new HashMap<>();
        for (String property : neededProperties){
            necessaryPropertyValues.put(property,calculatedPropertyValues.get(property));
        }
        
        Map<String,String> requestedPropertyValues = new HashMap<>();
        for (String property : r.requestedProperties){
            requestedPropertyValues.put(property,calculatedPropertyValues.get(property));
        }
        
        return new Report(necessaryPropertyValues, requestedPropertyValues, usedParameters);
    }
    
    public static class Report{
        Map<String,String> neededProperties;
        Map<String,String> requestedProperties;
        Map<String,String> usedParameters;
        
        Report(Map<String,String> necessaryProperties,Map<String,String> requestedProperties, Map<String,String> usedParameters){
            this.neededProperties = necessaryProperties;
            this.requestedProperties = requestedProperties;
            this.usedParameters = usedParameters;
        }
        
        public Map<String,String> getNecessaryPropertyValues(){
            return neededProperties;
        }
        
        public Map<String,String> getRequestedPropertyValues(){
            return requestedProperties;
        }
        
        public Map<String,String> getUsedParameters(){
            return usedParameters;
        }
    }
    
    /*

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
    
*/
    
    static private class AnalysisSupervisor{
        final ExecutorService es;
        final int maxThreads;
        final int maxThreadsPerNetwork;
        
        final Map<Integer,int[]> knownRanges;
        final Map<Integer,Combination[]> knownCombos; // map connecting variable domain index to affected combinations;
        final Map<Integer,Map<Integer,BasePair[][]>> knownBasePairs;

        final Map<Integer,BigInteger> knownIntraScores;
        final Map<Integer,BigInteger> knownInterScores;
        
        AnalysisSupervisor(int maxThreads, int maxThreadsPerNetwork){
            this.maxThreads = maxThreads;
            this.maxThreadsPerNetwork = maxThreadsPerNetwork;
            es = Executors.newFixedThreadPool(maxThreads);
            
            knownIntraScores = new ConcurrentHashMap<>();
            knownInterScores = new ConcurrentHashMap<>();
            knownRanges = new ConcurrentHashMap<>();
            knownBasePairs = new ConcurrentHashMap<>();
            knownCombos = new ConcurrentHashMap<>();
        }
        
        Map<Integer,Integer> getBaselineInterDuplexCount(IDomainBasedEncodedNetwork network, int interSLC){
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            //ueoSequences = ueo.values().stream().toArray(i->new int[i][]);
            
            int[][] encodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                encodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial N
            Future<int[]>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC));
            }

            // Add up length Counts;
            Map<Integer,Integer> ret = new HashMap<>();
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    int[] lengthCounts = futures[i].get();
                    for(int j = 0 ; j<lengthCounts.length;j++){
                        int counts = lengthCounts[j];
                        if (counts>0){
                            ret.merge(j,counts,(x,y)->x+y);
                        }
                    }
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            
            if (ret.size() == 0){
                ret.put(1, 0);
            }
            
            return ret;
        }
        
        Map<Integer,Integer> getBaselineIntraDuplexCount(IDomainBasedEncodedNetwork network, int intraSLC){
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            //ueoSequences = ueo.values().stream().toArray(i->new int[i][]);
            
            int[][] encodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                encodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            int maxLength = Arrays.stream(encodedOligomers).mapToInt(oligomer -> oligomer.length).max().getAsInt();
            int[] lengthCounts = new int[maxLength+1];
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
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
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b1 = 0;
                                structureLength = 0;
                        } else {b1++;}

                        if( b2 == 0) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
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
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            structureLength =0;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (structureLength >= intraSLC)
                    {
                        
                        lengthCounts[structureLength]++;
                        //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    };
                }
            }
            Map<Integer,Integer> ret = new HashMap<>();
            for(int i : IntStream.range(0,lengthCounts.length).toArray()){
                int duplexLength = i;
                int counts = lengthCounts[i];
                if (counts >0){
                    ret.put(duplexLength,counts);
                }
            }
            if (ret.size() == 0){
                ret.put(1, 0);
            }
            return ret;
        }
        
        Map<Integer,Integer> getInterDuplexCount(IDomainBasedEncodedNetwork network, int interSLC){
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial N
            Future<int[]>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC));
            }

            // Add up length Counts;
            Map<Integer,Integer> ret = new HashMap<>();
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    int[] lengthCounts = futures[i].get();
                    for(int j = 0 ; j<lengthCounts.length;j++){
                        int counts = lengthCounts[j];
                        if (counts>0){
                            ret.merge(j,counts,(x,y)->x+y);
                        }
                    }
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            if (ret.size() == 0){
                ret.put(1, 0);
            }
            
            return ret;
        }
        
        Map<Integer,Integer> getIntraDuplexCount(IDomainBasedEncodedNetwork network, int intraSLC){
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            int maxLength = Arrays.stream(encodedOligomers).mapToInt(oligomer -> oligomer.length).max().getAsInt();
            int[] lengthCounts = new int[maxLength+1];
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
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
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b1 = 0;
                                structureLength = 0;
                        } else {b1++;}

                        if( b2 == 0) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
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
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            structureLength =0;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (structureLength >= intraSLC)
                    {
                        
                        lengthCounts[structureLength]++;
                        //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    };
                }
            }
            Map<Integer,Integer> ret = new HashMap<>();
            for(int i : IntStream.range(0,lengthCounts.length).toArray()){
                int duplexLength = i;
                int counts = lengthCounts[i];
                if (counts >0){
                    ret.put(duplexLength,counts);
                }
            }
            if (ret.size() == 0){
                ret.put(1, 0);
            }
            return ret;
        }
        
        BigInteger getN (Map<Integer,Integer> lengthCounts, int interSB, int interSLC){
            BigInteger retScore = BigInteger.valueOf(0);
            for(Map.Entry<Integer,Integer> entry: lengthCounts.entrySet()){
                int length = entry.getKey();
                int counts = entry.getValue();
                BigInteger lengthScore = knownInterScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, interSLC, interSB));
                retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
            }
            return retScore;
        }
        
        BigInteger getO (Map<Integer,Integer> lengthCounts, int intraSB, int intraSLC){
            BigInteger retScore = BigInteger.valueOf(0);
            for(Map.Entry<Integer,Integer> entry: lengthCounts.entrySet()){
                int length = entry.getKey();
                int counts = entry.getValue();
                BigInteger lengthScore = knownIntraScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, intraSLC, intraSB));
                retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
            }
            return retScore;
        }
        
        static private class Worker implements Runnable{
            LinkedBlockingQueue<Runnable> queue;
            Worker(LinkedBlockingQueue<Runnable> queue){
                this.queue = queue;
            }
            public void run(){
                while (!Thread.currentThread().isInterrupted()){
                    try{
                        Runnable r = queue.take();
                        synchronized (r) {
                            r.run();
                            r.notify();
                        }
                    } catch (Exception e) {System.err.println(e);}
                }
            }
        }
        
        static private class CountInterDuplexRequest implements Callable<int[]>{
            final Combination[] combinations;
            final int firstIndex;
            final int lastIndex;
            final int[][] encodedOligomers;
            final AnalysisSupervisor as;
            final int interSLC;
            //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
            
            CountInterDuplexRequest(int[][] encodedOligomers, AnalysisSupervisor analysisSupervisor, Combination[] combinations, int firstIndex, int lastIndex, int interSLC){
                this.combinations = combinations;
                this.firstIndex = firstIndex;
                this.lastIndex = lastIndex;
                this.encodedOligomers = encodedOligomers;
                this.as = analysisSupervisor;
                this.interSLC = interSLC;
            }
            
            public int[] call(){
                int maxLength =0;
                for(int i =firstIndex; i < lastIndex; i++){
                    if (encodedOligomers[combinations[i].indexO1].length > maxLength){
                        maxLength = encodedOligomers[combinations[i].indexO1].length;
                    }
                }
                int[] lengthCounts = new int[maxLength+1];

                int indexS1;
                int indexS2;
                int structureLength;
                BasePair[][] allBP = new BasePair[0][];
                Combination currentCombo;

                // for each oligomer combination
                for(int i =firstIndex; i < lastIndex; i++){
                    currentCombo = combinations[i];
                    indexS1 = currentCombo.indexO1;
                    indexS2 = currentCombo.indexO2;
                    allBP = currentCombo.allBP;
                    
                    // for each stretch of base pairs.
                    for (BasePair[] bps : allBP){
                        structureLength=0;
                        // for each base-pair in the stretch.
                        for(BasePair bp : bps){
                            if(encodedOligomers[indexS1][bp.index1]+encodedOligomers[indexS2][bp.index2] == 0){
                                structureLength++;
                            } else {
                                if (structureLength >= interSLC){
                                    lengthCounts[structureLength]++;
                                }
                                structureLength = 0;
                            }
                        }
                        if (structureLength >= interSLC){
                            lengthCounts[structureLength]++;
                        }
                    }
                }
                return lengthCounts;
            }
        }
    }
    
    static private class BasePair{
        final int index1;
        final int index2;
        BasePair(int index1, int index2){
            this.index1 = index1;
            this.index2 = index2;
        }
    }

    // returns an n x m array of base pairs.
    // each n represents a longest possible duplex, aka base-alignment.
    // each m represents a pase pair in the alignment.
    static private BasePair[][] getKnownBasePairs(AnalysisSupervisor as, int oligomer1Length, int oligomer2Length){
        Map<Integer,BasePair[][]> firstMap = as.knownBasePairs.computeIfAbsent(oligomer1Length,x->new ConcurrentHashMap<Integer,BasePair[][]>());
        BasePair[][] bps = firstMap.computeIfAbsent(oligomer2Length,x->calculateBasePairs(as,oligomer1Length,oligomer2Length));
        return bps;
    }

    // returns an n x m array of base pairs.
    // each n represents a longest possible duplex, aka base-alignment.
    // each m represents a pase pair in the alignment.
    // oligomer1Length must be larger than or equal to oligomer2Length
    static private BasePair[][] calculateBasePairs(AnalysisSupervisor as, int oligomer1Length, int oligomer2Length){
        int S1length;
        int b1Max;
        int S2length;
        int b2Max;
        int b1;
        int b2;
        ArrayList<BasePair[]> duplexList = new ArrayList<>();
        ArrayList<BasePair> bpList = new ArrayList<>();

        // for each oligomer combination
        S1length = oligomer1Length;		
        b1Max = S1length-1;
        S2length = oligomer2Length;
        b2Max = S2length-1;
        //for each oligomer alignment
        for (int j : as.knownRanges.computeIfAbsent(S2length,x->IntStream.range(0,x).toArray())){
            b1 = 0; // index of base on the top strand;
            b2 = (b2Max + j) % (S2length);// index of base on the bottom strand;

            do{
                // add a base pair to the array.
                bpList.add(new BasePair(b1,b2));

                //advance to the next base
                b1++;
                if(b2 == 0){
                    BasePair[] copiedArray = Arrays.copyOf(bpList.toArray(x->new BasePair[x]),bpList.size());
                    duplexList.add(copiedArray);
                    bpList.clear();
                    b2 = b2Max;
                } else {b2--;}
            } while (b1 <= b1Max);

            // if the loop ended with a duplex.
            if(bpList.size() > 0){
                BasePair[] copiedArray = Arrays.copyOf(bpList.toArray(x->new BasePair[x]),bpList.size());
                duplexList.add(copiedArray);
                bpList.clear();
            }
        }

        BasePair[][] ret = duplexList.toArray(x->new BasePair[x][]);
        return ret;
    }

    static private Combination[] getCombos(AnalysisSupervisor as, IDomainBasedEncodedNetwork network){
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        Combination[] ret = new Combination[(encodedOligomers.length+1)*(encodedOligomers.length)/2];
        int retIndex =0;
        for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
            int indexO1 = i;
            for(int j : IntStream.range(i,encodedOligomers.length).toArray()){
                int indexO2 = j;
                if( encodedOligomers[indexO1].length < encodedOligomers[indexO2].length){
                    BasePair[][] allBP = getKnownBasePairs(as,encodedOligomers[indexO2].length, encodedOligomers[indexO1].length);
                    ret[retIndex] = new Combination(indexO2, indexO1, allBP);
                } else {
                    BasePair[][] allBP = getKnownBasePairs(as,encodedOligomers[indexO1].length, encodedOligomers[indexO2].length);
                    ret[retIndex] = new Combination(indexO1, indexO2, allBP);
                }
                retIndex++;
            }
        }

        return ret;
    }

    private static class Combination{
        int indexO1;
        int indexO2;
        BasePair[][] allBP;
        Combination(int indexO1, int indexO2, BasePair[][] allBP){
            this.indexO1 = indexO1;
            this.indexO2 = indexO2;
            this.allBP = allBP;
        }
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
    
    private static Map<String,int[]> encode(String[] names, String[] sequences){
        Map<String,int[]> encoded = new HashMap<>();
    for(int i : IntStream.range(0, names.length).toArray()){
            char[] b = sequences[i].toCharArray();
            int[] e = IntStream.range(0, b.length).map(x->encode(b[x])).toArray();
            encoded.put(names[i], e);
        }
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
    
    private static Map<String,int[]> assembleEncodedOligomers(Map<String,int[]> fixedDomains, Map<String,int[]> variableDomains, String[] oligomerNames, String[][] oligomerDomains){
        Map<String,int[]> encodedOligomers = new HashMap<>();

        //for each oligomer
        for (int i : IntStream.range(0, oligomerNames.length).toArray()){
            String oligomer = oligomerNames[i];
            String[] domainStrings = oligomerDomains[i];
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
    
    private static Map<String,int[]> getUniquelyEncodedDomains(String[] names, String[] sequences){
        Map<String, int[]> uniqueDomains = new HashMap<>();
        int currentBase = 3; 
        for(int i : IntStream.range(0, names.length).toArray()){
            int length = sequences[i].trim().length();
            int[] newV = IntStream.range(currentBase,currentBase+length).toArray();
            currentBase = currentBase+length;
            uniqueDomains.put(names[i], newV);
        }
        return uniqueDomains;
    }
}
