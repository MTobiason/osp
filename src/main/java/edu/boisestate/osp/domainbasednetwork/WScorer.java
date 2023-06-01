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
package edu.boisestate.osp.domainbasednetwork;

import edu.boisestate.osp.SeqEvo;
import edu.boisestate.osp.util;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class WScorer {
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

    public String getUpdatedScore(SeqEvo.Network existingNetwork, Map<String, int[]> newVariableDomains, Map<String, int[]> newOligomers, String updatedDomain) {
        BigInteger previousW = new BigInteger(existingNetwork.getScore());

        Map<String,int[]> oldOligomers = existingNetwork.getEncodedOligomerSequences();
        BigInteger oldW = calculateAffectedW(oldOligomers, updatedDomain);

        BigInteger newW = calculateAffectedW(newOligomers, updatedDomain);

        String ret = previousW.subtract(oldW).add(newW).toString();
        return ret;
    }
}
