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

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author mtobi
 */
public class util {
    
    public static Map<String,int[]> assembleEncodedOligomers(Map<String,int[]> fixedDomains, Map<String,int[]> variableDomains, Map<String,String[]> oligomerDomains){
        Map<String,int[]> encodedOligomers = new ConcurrentSkipListMap<>();        
        oligomerDomains.forEach((name,domains)->{
            ArrayList<int[]> encodedSubSequences = new ArrayList<>();
            int seqLength = 0;
            for(int i = 0; i < domains.length; i++){
                String domain = domains[i];
                if(domain.startsWith("c.") || domain.startsWith("C.")){
                    //is a complement
                    String complement = domain.substring(2);
                    int[] sequence = fixedDomains.get(complement);
                    if (sequence == null) sequence = variableDomains.get(complement);
                    if (sequence == null) {
                        System.out.println("Could not find complement of domain "+ domain +"." );
                        System.exit(0);
                    }
                    int[] complementSequence = new int[sequence.length];
                    for (int j = 0; j < sequence.length; j++){
                        complementSequence[j]=0-sequence[sequence.length-j-1];
                    }
                    encodedSubSequences.add(complementSequence);
                    seqLength += sequence.length;
                } else {
                    //is not a complement
                    int[] sequence = fixedDomains.get(domain);
                    if (sequence == null) sequence = variableDomains.get(domain);
                    if (sequence == null) {
                        System.out.println("Could not find domain "+ domain +"." );
                        System.exit(0);
                    }
                    encodedSubSequences.add(sequence);
                    seqLength += sequence.length;
                }
            }
            int[] encodedSequence = new int[seqLength];
            int currentBase =0;
            for(int i =0; i< encodedSubSequences.size(); i++){
                int[] currentSubSeq = encodedSubSequences.get(i);
                System.arraycopy(currentSubSeq, 0, encodedSequence, currentBase, currentSubSeq.length);
            }
            encodedOligomers.put(name,encodedSequence);
        });
        return encodedOligomers;
    }
    
    public static BigInteger calculateUniqueDuplexPoints (int length, int slc, int base){
        BigInteger score = BigInteger.valueOf(0);
        
        int numberOfStructures = 1;
        for (int i = length; i >= slc; i--){
            score = score.add(BigInteger.valueOf(base).pow(length).multiply(BigInteger.valueOf(numberOfStructures)));
            numberOfStructures++;
        }
        
        return score;
    }
    
    public static String[][] getCombinations(Set<String> oligomers){
        ArrayList<String> CO1 = new ArrayList<>(); //Combination Oligomer-1
        ArrayList<String> CO2 = new ArrayList<>(); // Combination Oligomer-2
        
        String[] oligomerArray = (String[]) oligomers.toArray();
                
        for (int i = 0; i < oligomerArray.length;i++){
            for (int j = i; j < oligomerArray.length;j++){
                CO1.add(oligomerArray[i]);
                CO2.add(oligomerArray[j]);
            }
        }
        
        String[][] retArray = {CO1.toArray(new String[0]),CO2.toArray(new String[0])};
        return retArray;
    }
    
    // Returns a Map object which connects an integer (representing length of duplex) to another integer (count of duplexes of this length.
    public static Map<Integer,Integer> countInterOligomerUnique(int[] es1, int[] es2, int slc){
        int[] S1Bases = es1;
        int[] S2Bases = es2;
        int S1length = S1Bases.length;
        int S2length = S2Bases.length;
        int minstructure = 1;				
        int b1Max = S1length-1;
        int b2Max = S2length-1;
        //Iterate through all reference positions
        Map<Integer,Integer> interOligoStructures = 
            IntStream.range(0,b2Max+1)
                .parallel()
                .mapToObj(j->{
                    Map<Integer,Integer> lengthCounts = new TreeMap();
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
                                    
                                        if (structureLength > slc){
                                            
                                            lengthCounts.merge(structureLength,1,Integer::sum);
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
                                        if (structureLength > slc)
                                        {
                                            lengthCounts.merge(structureLength,1,Integer::sum);
                                        }
                                        structureLength = 0;
                                };
                        };

                        //if the loop ended with an active structure, record it.
                        if (structureLength > slc)
                        {
                            lengthCounts.merge(structureLength,1,Integer::sum);
                        };

                        return lengthCounts;
                })
                .collect(TreeMap<Integer,Integer>::new, (response, element) -> element.forEach((k,v)->response.merge(k,v,Integer::sum)),(response1, response2) -> response2.forEach((k,v)->response1.merge(k,v,Integer::sum)))
                ;
        
        return interOligoStructures;
    }
    
    public static Map<Integer,Integer> countInterOligomerUnique(Map<String,int[]> encodedOligomers, String[][] combinations, int slc){
        Map<Integer,Integer> retCounts =  IntStream.range(0,combinations[0].length)
                .parallel()
                .mapToObj(v -> {
                    String O1 = combinations[0][v];
                    String O2 = combinations[1][v];
                    int[] es1 = encodedOligomers.get(O1);
                    int[] es2 = encodedOligomers.get(O2);
                    return countInterOligomerUnique(es1, es2, slc);
                    })
                .collect(TreeMap<Integer,Integer>::new, (response, element) -> element.forEach((k,v)->response.merge(k,v,Integer::sum)),(response1, response2) -> response2.forEach((k,v)->response1.merge(k,v,Integer::sum)))
                ;
        return retCounts;
    }
        
    public static Map<Integer,Integer> countIntraOligomerUnique(int[] encodedOligomer, int slc){
        int[] S1 = encodedOligomer;
        int[] S2 = encodedOligomer;
        int S1length = S1.length;
        int S2length = S2.length;
        int minstructure = 1;				
        int b1Max = S1length-1;

        // for every base in S1 (aka reference position)
        Map<Integer,Integer> retCount = IntStream.range(0,S1length)
            .parallel()
            .mapToObj(j->{
                Map<Integer,Integer> localCount = new TreeMap<>();
                int structureLength = 0;
                int b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                int b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                int length = S1length/2;
                if(S1length % 2 == 0 && j%2 == 1)
                {
                        length = length -1;
                }

                if(S1[b1] + S2[b2] ==0)
                {
                        structureLength = 1;
                }

                //For every base-pair in the reference position
                for ( int k =1; k < length; k++)
                {
                        if( b1 == b1Max) 
                        {
                                if (structureLength >= minstructure)
                                {
                                    localCount.merge(structureLength,1,Integer::sum);
                                }
                                b1 = 0;
                                structureLength = 0;
                        } else {b1++;}

                        if( b2 == 0) 
                        {
                                if (structureLength >= minstructure)
                                {
                                    localCount.merge(structureLength,1,Integer::sum);
                                }
                                b2 = b1Max;
                                structureLength = 0;
                        } else {b2--;}

                        if(S1[b1]+S2[b2]==0)
                        {
                                structureLength++;
                        }
                        else
                        {
                                if (structureLength >= minstructure)
                                {
                                    localCount.merge(structureLength,1,Integer::sum);
                                }
                                structureLength =0;
                        }
                }

                //if the loop ended with an active structure, record it.
                if (structureLength >= minstructure)
                {
                    localCount.merge(structureLength,1,Integer::sum);
                };
                return localCount;
            }).collect(TreeMap<Integer,Integer>::new, (response, element) -> element.forEach((k,v)->response.merge(k,v,Integer::sum)),(response1, response2) -> response2.forEach((k,v)->response1.merge(k,v,Integer::sum))
            );
        return retCount;
    }
    
    public static Map<Integer,Integer> countIntraOligomerUnique(Map<String,int[]> encodedOligomers, int slc){
        
        Map<Integer,Integer> retCount = encodedOligomers.values().stream()
                .parallel()
                .map(v -> countIntraOligomerUnique(v,slc))
                .collect(TreeMap<Integer,Integer>::new, (response, element) -> element.forEach((k,v)->response.merge(k,v,Integer::sum)),(response1, response2) -> response2.forEach((k,v)->response1.merge(k,v,Integer::sum)))
                ;
        
        return retCount;
    }
    
    public static Map<String,int[]> getUniquelyEncodedVariableBases(Map<String,int[]> domains){
        Map<String, int[]> uniqueDomains = new TreeMap<>();
        int currentBase = 2; 
        for ( Map.Entry<String,int[]> entry : domains.entrySet()){
            int length = entry.getValue().length;
            int[] newV = new int[length];
            for (int i = 0; i < length; i++){
                currentBase++;
                newV[i] = currentBase;
            }
            uniqueDomains.put(entry.getKey(), newV);
        }
        return uniqueDomains;
    }
    
    public static Map<String,String> decode(Map<String,int[]> sequences){
        Map<String,String> decoded = new TreeMap<>();
        sequences.forEach((k,v)-> {
                int[] e = v;
                char[] d = new char[e.length];
                IntStream.range(0, e.length).forEach(i->d[i]= decode(e[i]));
                decoded.put(k, String.valueOf(d));
            }
        );
        return decoded;
    }
    
    public static char decode(int e){
        switch (e){
            case -2:
                return 'A';
            case -1:
                return 'C';
            case 1:
                return 'G';
            case 2:
                return 'T';
            default: 
                System.out.println("Error: encoded base \"" + e + "\" not recognized." );
                System.exit(0);
                return 0;
        }
    }
    
    public static Map<String,int[]> encode(Map<String,String> sequences){
        Map<String,int[]> encoded = new TreeMap<>();
        sequences.forEach((k,v)-> {
                char[] b = v.toCharArray();
                int[] e = new int[b.length];
                IntStream.range(0, b.length).forEach(i->e[i]= encode(b[i]));
                encoded.put(k, e);
            }
        );
        return encoded;
    }
    
    public static int encode(char c){
        switch (c){
            case 'a':
            case 'A':
                return -2;
            case 't':
            case 'T':
                return 2;
            case 'c':
            case 'C':
                return -1;
            case 'g':
            case 'G':
                return 1;
            default: 
                System.out.println("Error: Base \"" + c + "\" not recognized." );
                System.exit(0);
                return 0;
        }
    }
            
    public static Map<String,String> importPairFromTxt(String filePath){
        Map<String,String> parameters = new TreeMap<>();
        try{
            File file = new File(filePath);
            Scanner scanner1 = new Scanner(file);
            while(scanner1.hasNextLine()){ // for each line of input file, until end of file
                String lineText = scanner1.nextLine();
                Scanner scanner2 = new Scanner(lineText);
                scanner2.useDelimiter(",");
                
                if(!lineText.startsWith("//") && scanner2.hasNext()){
                    String parameterName = scanner2.next();
                    String parameterValue = scanner2.next();
                    parameters.put(parameterName, parameterValue);    
                }
                scanner2.close();
            }  
            scanner1.close();
        }
        catch (Exception e){
            System.out.println("Error while importing from "+ filePath );
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return parameters;
    }
    
    public static Map<String,String[]> importListFromTxt(String filePath){
       Map<String,String[]> importedList = new TreeMap<>();
       try{
           File file = new File(filePath);
           Scanner scanner1 = new Scanner(file);

           while( scanner1.hasNextLine()){
               String lineText = scanner1.nextLine();
               Scanner scanner2 = new Scanner(lineText);
               scanner2.useDelimiter(",");

               if( !lineText.startsWith("//") && scanner2.hasNext()){
                   String key = scanner2.next(); //record the domain name
                   ArrayList<String> domains = new ArrayList<>();

                   while(scanner2.hasNext()){
                       String value = scanner2.next(); //record the domain.
                       domains.add(value);
                   }
                   importedList.put(key,domains.toArray(new String[0]));
               }
               scanner2.close();
           }  
           scanner1.close();
       }
       catch (Exception e)
       {
           System.out.println("Error while importing from "+ filePath );
           System.out.println(e.getMessage());
           System.exit(0);
       }
       return importedList;
    }
}
