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
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class util {
    
    public static Map<String,int[]> assembleEncodedOligomers(Map<String,int[]> fixedDomains, Map<String,int[]> variableDomains, Map<String,String[]> oligomerDomains){
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
    
    public static BigInteger calculateUniqueDuplexPoints (int length, int slc, int base){
        BigInteger score = BigInteger.valueOf(0);
        
        int numberOfStructures = 1;
        for (int i = length; i >= slc; i--){
            score = score.add(BigInteger.valueOf(base).pow(i).multiply(BigInteger.valueOf(numberOfStructures)));
            numberOfStructures++;
        }
        
        return score;
    }
    
    //returns true if the base sequence is valid. returns false if the base sequence contains too long of a stretch.
    public static boolean checkForStretches(Map<String,int[]> encodedSequences, int maxAA, int maxCC, int maxGG, int maxTT){
        int previousBase = 0;
        int currentRun = 1;
        for(Map.Entry<String,int[]> entry : encodedSequences.entrySet()){
            int[] encodedSequence = entry.getValue();
            for (int base : encodedSequence){
                if (base == previousBase){
                    currentRun++;
                    switch (previousBase){
                        case -2:
                            if (currentRun > maxAA) return false;
                            break;
                        case -1:
                            if (currentRun > maxCC) return false;
                            break;
                        default:
                            break;
                        case 1:
                            if (currentRun > maxGG) return false;
                            break;
                        case 2:
                            if (currentRun > maxTT) return false;
                            break;
                    }
                } else {
                    currentRun = 1;
                }
                previousBase = base;
            }
        }
        return true;
    }
    
    //returns true if the base sequence is valid. returns false if the base sequence contains too long of a stretch.
    public static boolean checkForStretches(int[] encodedSequence, int maxAA, int maxCC, int maxGG, int maxTT){
        int previousBase = 0;
        int currentRun = 1;
        for (int base : encodedSequence){
            if (base == previousBase){
                currentRun++;
                switch (previousBase){
                    case -2:
                        if (currentRun > maxAA) return false;
                        break;
                    case -1:
                        if (currentRun > maxCC) return false;
                        break;
                    default:
                        break;
                    case 1:
                        if (currentRun > maxGG) return false;
                        break;
                    case 2:
                        if (currentRun > maxTT) return false;
                        break;
                }
            } else {
                currentRun = 1;
            }
            previousBase = base;
        }
        return true;
    }
    
    public static boolean checkForStretches(int[] encodedSequence, int baseIndex, int maxAA, int maxCC, int maxGG, int maxTT){
        int currentBase = encodedSequence[baseIndex];
        int limit = 0;
        switch (currentBase){
            case -2:
                limit = maxAA;
                break;
            case -1:
                limit = maxCC;
                break;
            default:
                break;
            case 1:
                limit = maxGG;
                break;
            case 2:
                limit = maxTT;
                break;
        }

        // starting at the base index. 
        // read left to find edge.
        int currentIndex = baseIndex-1;
        int currentRun = 1;
        while (currentIndex > 0 && encodedSequence[currentIndex] == currentBase){
            currentRun++;
            if (currentRun > limit) return false;
            currentIndex--;
        }
        
        // read right to find edge.
        currentIndex = baseIndex;
        while (currentIndex < encodedSequence.length && encodedSequence[currentIndex] == currentBase){
            currentRun++;
            if (currentRun > limit) return false;
            currentIndex++;
        }
        return true;
    }
    
    // Returns a Map object which connects an integer (representing length of duplex) to another integer (count of duplexes of this length.
    public static Map<Integer,Integer> countInterOligomerUnique(int[] es1, int[] es2, int slc){
        int[] S1Bases = es1;
        int[] S2Bases = es2;
        final int S1length = S1Bases.length;
        final int S2length = S2Bases.length;		
        final int b1Max = S1length-1;
        final int b2Max = S2length-1;
        Map<Integer,Integer> lengthCounts = new HashMap();
        
        for (int j : IntStream.range(0,b2Max+1).toArray()){
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

                                if (structureLength >= slc){
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
                                if (structureLength >= slc)
                                {
                                    lengthCounts.merge(structureLength,1,Integer::sum);
                                }
                                structureLength = 0;
                        };
                };
                //if the loop ended with an active structure, record it.
                if (structureLength >= slc)
                {
                    lengthCounts.merge(structureLength,1,Integer::sum);
                };
            }
        return lengthCounts;
    }
    
    public static Map<Integer,Integer> countInterOligomerUnique(Map<String,int[]> encodedOligomers, String[][] combinations, int slc){
        Map<Integer,Integer> ret = new HashMap<>();
        for (int v :IntStream.range(0,combinations[0].length).toArray()){
            String O1 = combinations[0][v];
            String O2 = combinations[1][v];
            int[] es1 = encodedOligomers.get(O1);
            int[] es2 = encodedOligomers.get(O2);
            for(Map.Entry<Integer,Integer> entry : countInterOligomerUnique(es1, es2, slc).entrySet()){
                ret.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return ret;
    }
        
    public static Map<Integer,Integer> countIntraOligomerUnique(int[] encodedOligomer, int slc){
        int[] S1 = encodedOligomer;
        int[] S2 = encodedOligomer;
        int S1length = S1.length;		
        int b1Max = S1length-1;
        Map<Integer,Integer> lengthCounts = new HashMap();
        
        for (int j : IntStream.range(0,S1length).toArray()){
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
                        if (structureLength >= slc)
                        {
                            lengthCounts.merge(structureLength,1,Integer::sum);
                        }
                        b1 = 0;
                        structureLength = 0;
                } else {b1++;}

                if( b2 == 0) 
                {
                        if (structureLength >= slc)
                        {
                            lengthCounts.merge(structureLength,1,Integer::sum);
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
                        if (structureLength >= slc)
                        {
                            lengthCounts.merge(structureLength,1,Integer::sum);
                        }
                        structureLength =0;
                }
            }

            //if the loop ended with an active structure, record it.
            if (structureLength >= slc)
            {
                lengthCounts.merge(structureLength,1,Integer::sum);
            };
        }
        return lengthCounts;
    }
    
    public static Map<Integer,Integer> countIntraOligomerUnique(Map<String,int[]> encodedOligomers, int slc){
        Map<Integer,Integer> ret = new HashMap<>();
        for (Map.Entry<String,int[]> entry : encodedOligomers.entrySet()){
            int[] encodedSequence = entry.getValue();
            for(Map.Entry<Integer,Integer> entry2 : countIntraOligomerUnique(encodedSequence, slc).entrySet()){
                ret.merge(entry2.getKey(), entry2.getValue(), Integer::sum);
            }   
        }
        return ret;
    }
    
    public static Map<Integer,Integer> countIntraOligomerUnique(Map<String,int[]> encodedOligomers, String[] oligomers, int slc){
        
        Map<Integer,Integer> retCount = Arrays.stream(oligomers)
                .parallel()
                .map(oligomer -> countIntraOligomerUnique(encodedOligomers.get(oligomer),slc))
                .collect(HashMap<Integer,Integer>::new, (response, element) -> element.forEach((k,v)->response.merge(k,v,Integer::sum)),(response1, response2) -> response2.forEach((k,v)->response1.merge(k,v,Integer::sum)))
                ;
        
        return retCount;
    }
    
    public static int[][] getBlankEncodedSequences(Map<String,String> encodedSequences){
        int[][] ret = new int[encodedSequences.size()][];
        
        
        for (Map.Entry<String,String> entry : encodedSequences.entrySet()){
            ret.put(entry.getKey(),new int[entry.getValue().trim().length()]);
        }
        
        return ret;
    }
    
    public static int[][] getBlankEncodedSequences(int[] sequenceLengths){
        int[][] ret = new int[sequenceLengths.length][];
        
        for (int i =0; i < sequenceLengths.length; i++){
            ret[i] = new int[sequenceLengths[i]];
        }
        
        return ret;
    }
    
    public static String[][] getCombinations(Set<String> oligomers){
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
    
    public static int[] getComplement(int[] encodedSequence){
        int[] retSequence = new int[encodedSequence.length];
        IntStream.range(0,encodedSequence.length).forEach(i-> retSequence[i] = -encodedSequence[encodedSequence.length-i-1]);
        return retSequence;
    }
    
    public static Map<String,int[]> getUniquelyEncodedVariableBases(Map<String,int[]> domains){
        Map<String, int[]> uniqueDomains = new HashMap<>();
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
    
    public static int[] getType3Mutation(int[] encodedSequence){
            Random rnd = ThreadLocalRandom.current();
            int length = encodedSequence.length;
            int[] newSequence = new int[length];
            
            System.arraycopy(encodedSequence, 0, newSequence, 0, length);
            int i1 = rnd.nextInt(newSequence.length);
            int i2 = rnd.nextInt(newSequence.length);
            while (i2 == i1) i2 = rnd.nextInt(newSequence.length);
            int a = newSequence[i2];
            newSequence[i2] = newSequence[i1];
            newSequence[i1] = a;
            
            return newSequence;
        }
    
    public static int[] getType2Mutation(int[] encodedSequence){
        Random rnd = ThreadLocalRandom.current();
        int length = encodedSequence.length;
        int[] ret = new int[length];
        int[] newSequence = Arrays.copyOf(encodedSequence, length);
        // select 2 bases as endpoints of the sub-sequence.
        int b1 = rnd.nextInt(length);
        int b2 = rnd.nextInt(length);
        while(b2 == b1) b2 = rnd.nextInt(length);
        
        if (b1 < b2){
            //left is b1.
            
            //remove this subsequence from the new sequence.
            System.arraycopy(encodedSequence, b2+1, newSequence, b1, length - (b2+1));
            int newLength = length - (b2-b1+1);
            
            //select a location to reinsert the subsequence
            int b3 = rnd.nextInt(newLength+1);
            
            // copy the subsequence back into the return sequence.
            //copy left. 
            if (b3 >0){
                System.arraycopy(newSequence,0,ret,0,b3);
            }
            
            //copy middle
            System.arraycopy(encodedSequence, b1, ret, b3, b2-b1+1);
            
            //copy right
            if (b3 < newLength){
                System.arraycopy(newSequence, b3, ret, b3 + b2-b1+1 , newLength-b3);
            }
            
        }
        if (b2 < b1){
            //left is b2, but reverse the sequence before re-inserting.
            
            //remove this subsequence from the new sequence.
            System.arraycopy(encodedSequence, b1+1, newSequence, b2, length - (b1+1));
            int newLength = length - (b1-b2+1);
            
            //select a location to reinsert the subsequence
            int b3 = rnd.nextInt(newLength+1);
            
            // copy the subsequence back into the return sequence.
            //copy left. 
            if (b3 >0){
                System.arraycopy(newSequence,0,ret,0,b3);
            }
            
            //reverse and copy middle
            for (int i = 0 ; i < b1-b2+1; i++){
                ret[b3+i] = encodedSequence[b1-i];
            }
            
            //copy right
            if (b3 < newLength){
                System.arraycopy(newSequence, b3, ret, b3 + b1-b2+1 , newLength-b3);
            }
        }
        
        return ret;
    }
    
    public static int[] getType1Mutation(int[] encodedSequence){
        Random rnd = ThreadLocalRandom.current();
        int length = encodedSequence.length;
        int[] newSequence = Arrays.copyOf(encodedSequence,encodedSequence.length);
        
        for (int i = length - 1; i > 0; i--) 
        {
           int k = rnd.nextInt(i + 1);
           int a = newSequence[k];
           newSequence[k] = newSequence[i];
           newSequence[i] = a;
        }
        
        return newSequence;
    }
    
    public static Map<String,String> decode(Map<String,int[]> sequences){
        Map<String,String> decoded = new HashMap<>();
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
        Map<String,int[]> encoded = new HashMap<>();
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
            
    public static Map<String,String> importPairFromTxt(String filePath){
        Map<String,String> parameters = new HashMap<>();
        try{
            File file = new File(filePath);
            Scanner scanner1 = new Scanner(file);
            while(scanner1.hasNextLine()){ // for each line of input file, until end of file
                String lineText = scanner1.nextLine();
                Scanner scanner2 = new Scanner(lineText);
                //scanner2.useDelimiter(" ");
                
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
       Map<String,String[]> importedList = new HashMap<>();
       try{
           File file = new File(filePath);
           Scanner scanner1 = new Scanner(file);

           while( scanner1.hasNextLine()){
               String lineText = scanner1.nextLine();
               Scanner scanner2 = new Scanner(lineText);
               //scanner2.useDelimiter(",");

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
