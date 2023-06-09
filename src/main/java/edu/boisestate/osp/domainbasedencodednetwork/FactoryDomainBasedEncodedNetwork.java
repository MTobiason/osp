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

import edu.boisestate.osp.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class FactoryDomainBasedEncodedNetwork {
    final ICoder coder;
    
    final int fixedDomainCount;
    final Map<String,Integer> fixedDomainIndices;
    final int[] fixedDomainLengths;
    final String[] fixedDomainNames;
    final String[] fixedDomainSequences; // fixed-domains
    final int[][] fixedDomainEncodedSequences; // encoded-fixed-domains
    
    final int variableDomainCount;
    final Map<String,Integer> variableDomainIndices;
    final int[] variableDomainLengths;
    final String[] variableDomainNames;
    final String[] variableDomainInitialSequences;
    
    final int oligomerCount;
    final String[][]  oligomerDomains; // oligomer-domains
    final Map<String,Integer> oligomerIndices;
    final String[] oligomerNames;
    final boolean[][] oligomerBaseIsVariableArray;
    
    final int[][] firstPartialEncodedOligomerSequences; // blank-encoded-oligomer-sequences

    // variables for mutating networks.
    final int[] domainSelectionBag; // domain-selection-bag
    final Map<Integer,Map<Integer,int[]>> vdtoc; // variable domain to oligomer coordinates map
    final Map<Integer,Map<Integer,int[]>> vdctoc; // variable domain complement to oligomer coordinates map
    
    final Map<Integer,Set<Integer>> vdto;
    final Map<Integer,int[][]> vdCombo;

    // Creates a factory for creating networks of a given design
    public FactoryDomainBasedEncodedNetwork (ICoder coder, Map<String,String> fixedDomains, Map<String,String[]> oligomerDomains, Map<String,String> variableDomains){
        this.coder = coder;
        
        this.fixedDomainIndices = new HashMap<>();
        Integer index = 0;
        for (String domain: fixedDomains.keySet()){
            this.fixedDomainIndices.put(domain,index);
            index++;
        }
        fixedDomainCount = index;
        
        this.fixedDomainNames = new String[fixedDomainCount];
        for (Map.Entry<String,Integer> entry:  fixedDomainIndices.entrySet()){
            this.fixedDomainNames[entry.getValue()] = entry.getKey();
        }
        
        this.variableDomainIndices = new HashMap<>();
        index = 0;
        for (String domain: variableDomains.keySet()){
            this.variableDomainIndices.put(domain,index);
            index++;
        }
        variableDomainCount = index;
        
        this.variableDomainNames = new String[variableDomainCount];
        for (Map.Entry<String,Integer> entry:  variableDomainIndices.entrySet()){
            this.variableDomainNames[entry.getValue()] = entry.getKey();
        }
        
        this.fixedDomainSequences = new String[fixedDomainCount];
        for (Map.Entry<String,Integer> entry:  fixedDomainIndices.entrySet()){
            this.fixedDomainSequences[entry.getValue()] = fixedDomains.get(entry.getKey());
        }
        
        this.fixedDomainEncodedSequences = coder.encode(this.fixedDomainSequences);
        
        this.oligomerIndices = new HashMap<>();
        index = 0;
        for (String domain: oligomerDomains.keySet()){
            this.oligomerIndices.put(domain,index);
            index++;
        }
        oligomerCount = index;
        
        this.oligomerNames = new String[oligomerCount];
        for (Map.Entry<String,Integer> entry:  oligomerIndices.entrySet()){
            this.oligomerNames[entry.getValue()] = entry.getKey();
        }
        
        this.oligomerDomains = new String[oligomerCount][];
        for (Map.Entry<String,Integer> entry:  oligomerIndices.entrySet()){
            this.oligomerDomains[entry.getValue()] = oligomerDomains.get(entry.getKey());
        }
        
        this.variableDomainInitialSequences = new String[variableDomainCount];
        for (Map.Entry<String,Integer> entry:  variableDomainIndices.entrySet()){
            this.variableDomainInitialSequences[entry.getValue()] = variableDomains.get(entry.getKey());
        }

        this.domainSelectionBag = getDomainSelectionBag(variableDomains);
        this.fixedDomainLengths = util.getDomainLengths(this.fixedDomainSequences);
        this.variableDomainLengths = util.getDomainLengths(this.variableDomainInitialSequences);
        this.vdto = getVariableDomainToOligomers();
        this.vdtoc = getVariableDomainToOligomerCoordinates();
        this.vdctoc = getVariableDomainComplementToOligomerCoordinates();
        this.firstPartialEncodedOligomerSequences = assembleFirstPartialSolution();
        this.vdCombo = getDomainToOligomerCombinationsMap(variableDomains,oligomerDomains,vdto);
        this.oligomerBaseIsVariableArray = getBaseIsVariableArray(this.firstPartialEncodedOligomerSequences);
    }
    
    private class InnerNetwork implements IDomainBasedEncodedNetwork{
                
        String[] variableDomains; // variable-domains
        int[][] encodedVariableDomains; // encoded-variable-domains
        String[] oligomerSequences; // oligomer-sequences
        int[][] encodedOligomers; // encoded-oligomer-sequences

        InnerNetwork(){
        }
        
        InnerNetwork(int[][] encodedVariableDomains, int[][] encodedOligomers){
            this.encodedVariableDomains = encodedVariableDomains;
            this.encodedOligomers = encodedOligomers;
        }
        
        @Override
        public int[][] getFixedDomainEncodedSequences() {
            return fixedDomainEncodedSequences;
        }
        
        @Override
        public Map<String,Integer> getFixedDomainIndices(){
            return fixedDomainIndices;
        }
        
        @Override
        public String[] getFixedDomainNames() {
            return fixedDomainNames;
        }
        
        @Override
        public String[] getFixedDomainSequences() {
            return fixedDomainSequences;
        }
        
        @Override
        public String[][] getOligomerDomains() {
            return oligomerDomains;
        }
        
        @Override
        public Map<String,Integer> getOligomerIndices(){
            return oligomerIndices;
        }
        
        @Override
        public String[] getOligomerNames() {
            return oligomerNames;
        }
        
        @Override
        public String[] getOligomerSequences() {
            if (oligomerSequences == null) oligomerSequences = coder.decode(encodedOligomers);
            return oligomerSequences;
        }
        
        @Override
        public int[][] getOligomerSequencesEncoded() {
            return encodedOligomers;
        }
        
        @Override
        public boolean[][] getOligomerBaseIsVariableArray(){
            return oligomerBaseIsVariableArray;
        }
        
        @Override
        public Map<String,Integer> getVariableDomainIndices(){
            return variableDomainIndices;
        }
        
        @Override
        public String[] getVariableDomainNames() {
            return variableDomainNames;
        }
        
        @Override
        public String[]  getVariableDomainSequences() {
            if (variableDomains == null) variableDomains = coder.decode(encodedVariableDomains);
            return variableDomains;
        }

        @Override
        public int[][] getVariableDomainSequencesEncoded() {
            return encodedVariableDomains;
        }
        
        @Override
        public Map<Integer,Map<Integer,int[]>> getVariableDomainToOligomerCoordinates(){
            return vdtoc;
        }
        
        @Override
        public Map<Integer,Map<Integer,int[]>> getVariableDomainComplementToOligomerCoordinates(){
            return vdctoc;
        }
        
        @Override
        public Map<Integer,Set<Integer>> getVariableDomainToOligomerIndices(){
            return vdto;
        }
        
        @Override
        public Map<Integer,int[][]> getVariableDomainToOligomerCombinations(){
            return vdCombo;
        }

    }
    
    private int[] getDomainSelectionBag(Map<String,String> initialVariableDomains){
        ArrayList<Integer> tempBag = new ArrayList<>();

        //for each variable domain
        // add a number of elements equal to the base length to the domain selection bag.
        for (Map.Entry<String,String> entry : initialVariableDomains.entrySet()){
            Integer domainIndex = variableDomainIndices.get(entry.getKey());
            int length = entry.getValue().trim().length();
            for(int i = 0; i < length; i++){
                tempBag.add(domainIndex);
            }
       }
        
        int[] ret = tempBag.stream().mapToInt(i->i).toArray();
        return ret;
    }
    
    // returns a new network based on a given fixedDomains, oligomerDomains, and variable domains. Network validity is not considered.
    public IDomainBasedEncodedNetwork getNewNetwork(Map<String,String> variableDomains){
        String[] vd = new String[variableDomainCount];
        for (Map.Entry<String,Integer> entry :  variableDomainIndices.entrySet()){
            vd[entry.getValue()] = variableDomains.get(entry.getKey());
        }
        
        int[][] evd = coder.encode(vd);
        int[][] eos = placeDomains(firstPartialEncodedOligomerSequences, IntStream.range(0,variableDomainCount).toArray(), evd);

        InnerNetwork retNetwork = new InnerNetwork(evd,eos);
        return retNetwork;
    }
    
    // returns a new network based on a given fixedDomains, oligomerDomains, and variable domains. If the given network is invalid according to validator, a random valid network is instead returned.
    public IDomainBasedEncodedScoredNetwork getNewNetwork( Map<String,String> variableDomains, IScorer scorer, IValidator validator){
        String[] vd = new String[variableDomainCount];
        for (Map.Entry<String,Integer> entry :  variableDomainIndices.entrySet()){
            vd[entry.getValue()] = variableDomains.get(entry.getKey());
        }
        
        int[][] evd = coder.encode(vd);
        int[][] eos = placeDomains(firstPartialEncodedOligomerSequences, IntStream.range(0,variableDomainCount).toArray(), evd);

        IDomainBasedEncodedScoredNetwork retNet = scorer.getScored(new InnerNetwork(evd,eos));
        
        if (!validator.isValidNetwork(retNet)) {
            System.out.println("Initial oligomers invalid. Replacing with randomly seeded oligomers.");
            retNet = getType1Mutation(retNet,scorer,validator);
        }
        return retNet;
    }
    
    private static int[] getType1Mutation(int[] encodedSequence){
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
    
    public IDomainBasedEncodedNetwork getType1Mutation(IDomainBasedEncodedNetwork existingNetwork, IValidator validator){
        final int[][] oldEVD = existingNetwork.getVariableDomainSequencesEncoded();
        final int[][] oldEOS = existingNetwork.getOligomerSequencesEncoded();
        int[][] bevd = util.getBlankEncodedSequences(variableDomainLengths);
        
        InnerNetwork partialNetwork = new InnerNetwork(bevd,firstPartialEncodedOligomerSequences);
        int[][] newEVD;
        int[][] newEOS;
        int seedings = 0;
        boolean valid = false;
        // attempt to seed all domains
        do {
            newEOS = Arrays.copyOf(firstPartialEncodedOligomerSequences,firstPartialEncodedOligomerSequences.length);
            newEVD = Arrays.copyOf(bevd,bevd.length);
            partialNetwork.encodedVariableDomains = newEVD;
            partialNetwork.encodedOligomers = newEOS;
            int[] domainSelectionBag = IntStream.range(0,variableDomainCount).toArray();

            Random rnd = ThreadLocalRandom.current();
            // for each domain in the domain selection bag.
            currentSeeding:
            for (int i = domainSelectionBag.length-1; i >= 0; i--){

                // select a domain to be mutated and remove it from the bag.
                int k = rnd.nextInt(i + 1);
                int a = domainSelectionBag[k];
                domainSelectionBag[k] = domainSelectionBag[i];
                domainSelectionBag[i] = a;

                int selectedDomainIndex = domainSelectionBag[i];
                final int[] oldDomain = oldEVD[selectedDomainIndex];

                //attempt to mutate this domain 100 times;
                int attempts2 = 0;
                int[] newDomain;
                // attempt to randomize the domain
                do {
                    newDomain = getType1Mutation(oldDomain);
                    newEVD[selectedDomainIndex] = newDomain;
                    newEOS = placeDomain(newEOS,selectedDomainIndex,newDomain);
                    partialNetwork.encodedVariableDomains = newEVD;
                    partialNetwork.encodedOligomers = newEOS;
                    valid = validator.isValidPartialNetwork(partialNetwork,selectedDomainIndex);
                    attempts2++;
                } while (attempts2 < 1000 && (!valid));

                if(!valid) break currentSeeding;
            }
            seedings++;
        } while (seedings < 1000 && !valid);

        if(!valid){
            System.out.println("Failed to identify a valid network during random seeding.");
            System.exit(0);
        }

        return partialNetwork;
    }
    
    public IDomainBasedEncodedScoredNetwork getType1Mutation(IDomainBasedEncodedScoredNetwork existingNetwork, IScorer scorer, IValidator validator){
        final int[][] oldEVD = existingNetwork.getVariableDomainSequencesEncoded();
        final int[][] oldEOS = existingNetwork.getOligomerSequencesEncoded();
        int[][] bevd = util.getBlankEncodedSequences(variableDomainLengths);
        
        InnerNetwork partialNetwork = new InnerNetwork(bevd,firstPartialEncodedOligomerSequences);
        int[][] newEVD;
        int[][] newEOS;
        int seedings = 0;
        boolean valid = false;
        // attempt to seed all domains
        do {
            newEOS = Arrays.copyOf(firstPartialEncodedOligomerSequences,firstPartialEncodedOligomerSequences.length);
            newEVD = Arrays.copyOf(bevd,bevd.length);
            partialNetwork.encodedVariableDomains = newEVD;
            partialNetwork.encodedOligomers = newEOS;
            int[] domainSelectionBag = IntStream.range(0,variableDomainCount).toArray();

            Random rnd = ThreadLocalRandom.current();
            // for each domain in the domain selection bag.
            currentSeeding:
            for (int i = domainSelectionBag.length-1; i >= 0; i--){

                // select a domain to be mutated and remove it from the bag.
                int k = rnd.nextInt(i + 1);
                int a = domainSelectionBag[k];
                domainSelectionBag[k] = domainSelectionBag[i];
                domainSelectionBag[i] = a;

                int selectedDomainIndex = domainSelectionBag[i];
                final int[] oldDomain = oldEVD[selectedDomainIndex];

                //attempt to mutate this domain 100 times;
                int attempts2 = 0;
                int[] newDomain;
                // attempt to randomize the domain
                do {
                    newDomain = getType1Mutation(oldDomain);
                    newEVD[selectedDomainIndex] = newDomain;
                    newEOS = placeDomain(newEOS,selectedDomainIndex,newDomain);
                    partialNetwork.encodedVariableDomains = newEVD;
                    partialNetwork.encodedOligomers = newEOS;
                    valid = validator.isValidPartialNetwork(partialNetwork,selectedDomainIndex);
                    attempts2++;
                } while (attempts2 < 1000 && (!valid));

                if(!valid) break currentSeeding;
            }
            seedings++;
        } while (seedings < 1000 && !valid);

        if(!valid){
            System.out.println("Failed to identify a valid network during random seeding.");
            System.exit(0);
        }

        IDomainBasedEncodedScoredNetwork retNet = scorer.getScored(partialNetwork);
        return retNet;
    }

    private static int[] getType2Mutation(int[] encodedSequence){
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
    
    public IDomainBasedEncodedScoredNetwork getType2Mutation(IDomainBasedEncodedScoredNetwork existingNetwork, IScorer scorer, IValidator validator){
        int[][] oldEVD = existingNetwork.getVariableDomainSequencesEncoded();
        int[][] newEVD = Arrays.copyOf(oldEVD,oldEVD.length);
        int[][] oldEOS = existingNetwork.getOligomerSequencesEncoded();
        int[][] newEOS = Arrays.copyOf(oldEOS,oldEOS.length);

        //Select a domain for mutation.
        Random rnd = ThreadLocalRandom.current();
        int sdi = domainSelectionBag[rnd.nextInt(domainSelectionBag.length)]; // selceted Domain Index
        int[] oldDomain = newEVD[sdi];
        
        InnerNetwork newNetwork = new InnerNetwork(newEVD,newEOS);
        
        int attempts1 = 0;
        int[] newDomain;
        boolean valid = false;
        do {
            newDomain = getType2Mutation(oldDomain);
            newEVD[sdi] = newDomain;
            newEOS = placeDomain(newEOS,sdi,newDomain);
            newNetwork.encodedOligomers = newEOS;
            newNetwork.encodedVariableDomains = newEVD;
            valid = validator.isValidNetwork(newNetwork,sdi);
            attempts1++;
        } while (attempts1<1000 && (!valid || Arrays.equals(oldDomain,newDomain)));

        if (!valid) {
            return existingNetwork;
        }
        
        IDomainBasedEncodedScoredNetwork retNet = scorer.getScored(existingNetwork,newNetwork,sdi);
        return retNet;
    }

    private static int[] getType3Mutation(int[] encodedSequence){
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
    
    public IDomainBasedEncodedScoredNetwork getType3Mutation(IDomainBasedEncodedScoredNetwork existingNetwork, IScorer scorer, IValidator validator){
        int[][] oldEVD = existingNetwork.getVariableDomainSequencesEncoded();
        int[][] newEVD = Arrays.copyOf(oldEVD,oldEVD.length);
        int[][] oldEOS = existingNetwork.getOligomerSequencesEncoded();
        int[][] newEOS = Arrays.copyOf(oldEOS,oldEOS.length);

        //Select a domain for mutation.
        Random rnd = ThreadLocalRandom.current();
        int sdi = domainSelectionBag[rnd.nextInt(domainSelectionBag.length)]; // selceted Domain Index
        int[] oldDomain = newEVD[sdi];
        
        InnerNetwork newNetwork = new InnerNetwork(newEVD,newEOS);
        
        int attempts1 = 0;
        int[] newDomain;
        boolean valid = false;
        do {
            newDomain = getType3Mutation(oldDomain);
            newEVD[sdi] = newDomain;
            newEOS = placeDomain(newEOS,sdi,newDomain);
            newNetwork.encodedOligomers = newEOS;
            newNetwork.encodedVariableDomains = newEVD;
            valid = validator.isValidNetwork(newNetwork,sdi);
            attempts1++;
        } while (attempts1<1000 && (!valid || Arrays.equals(oldDomain,newDomain)));

        if (!valid) {
            return existingNetwork;
        }
        
        IDomainBasedEncodedScoredNetwork retNet = scorer.getScored(existingNetwork,newNetwork,sdi);
        return retNet;
    }
    
    // creates encoded oligomers which have all variable domains placed, but
    // zeros for variable domains.
    private int[][] assembleFirstPartialSolution(){
        int[][] encodedOligomers = new int[oligomerCount][];
        
        int[][] blankEncodedVariableDomains = util.getBlankEncodedSequences(variableDomainLengths);
        String[] domainStrings;
        String currentDomain;

        //for each oligomer
        for (int i : IntStream.range(0, oligomerCount).toArray()){
            domainStrings = oligomerDomains[i];
            ArrayList<int[]> encodedSequenceChunks = new ArrayList<>();
            
            int totalLength = 0;
            int[] domainSequence;
            int[] compSequence;
            String domainName;
            
            //for each domain in the oligomer
            for (String domainString : domainStrings) {
                currentDomain = domainString;
                if (currentDomain.startsWith("c.")){
                    domainName = currentDomain.substring(2);
                    Integer compIndex = fixedDomainIndices.get(domainName);
                    // if the domain is a fixed domain.
                    if (compIndex != null ) {
                        domainSequence = fixedDomainEncodedSequences[compIndex];
                        compSequence = coder.getComplement(domainSequence);
                    } else {
                        compIndex = variableDomainIndices.get(domainName);
                        if (compIndex != null){
                            domainSequence = blankEncodedVariableDomains[compIndex];
                            compSequence = domainSequence;
                        } else {
                            compSequence = new int[0];
                        }
                    }
                    if (compIndex == null ) {
                        System.out.println("Could not find complement of domain "+ domainName +"." );
                        System.exit(0);
                    }
                    encodedSequenceChunks.add(compSequence);
                    totalLength += compSequence.length;
                } else {
                    Integer domainIndex = fixedDomainIndices.get(currentDomain);
                    if (domainIndex != null ) {
                        domainSequence = fixedDomainEncodedSequences[domainIndex];
                    } else {
                        domainIndex = variableDomainIndices.get(currentDomain);
                        if (domainIndex != null){
                            domainSequence = blankEncodedVariableDomains[domainIndex];
                        } else {
                            domainSequence = new int[0];
                        }
                    }
                    if (domainIndex == null ) {
                        System.out.println("Could not find domain "+ currentDomain +"." );
                        System.exit(0);
                        domainSequence = new int[0];
                    }
                    encodedSequenceChunks.add(domainSequence);
                    totalLength += domainSequence.length;
                }
            }
            int[] encodedOligomer = new int[totalLength];
            int nextBase = 0;
            for (int[] sequenceChunks : encodedSequenceChunks){
                System.arraycopy(sequenceChunks, 0, encodedOligomer, nextBase, sequenceChunks.length);
                nextBase += sequenceChunks.length;
            }
            encodedOligomers[i] = encodedOligomer;
        }
        return encodedOligomers;
    }
    
    private boolean[][] getBaseIsVariableArray(int[][] assembledPartialSolution){
        boolean[][] retArray = new boolean[assembledPartialSolution.length][];
        
        for(int i=0; i < assembledPartialSolution.length; i++){
            boolean[] newArray = new boolean[assembledPartialSolution[i].length];
            for (int j=0; j < assembledPartialSolution[i].length; j++){
                if (assembledPartialSolution[i][j]== 0){
                    newArray[j]=true;
                } else{
                    newArray[j]=false;
                }
            }
            retArray[i] = newArray;
        }
        return retArray;
    }


    private Map<Integer,Map<Integer,int[]>> getVariableDomainComplementToOligomerCoordinates (){
        Map<Integer,Map<Integer,int[]>> retDCTOM = new ConcurrentHashMap<>();

        //initialize an entry for every domain;
        for (int i : IntStream.range(0,variableDomainCount).toArray()){
            retDCTOM.put(i,new ConcurrentHashMap<Integer,int[]>());
        }

        //for each oligomer
            // for each domain in the oligomer
                // add an entry for this association.
        int currentBase;
        String domainName;
        //for each oligomer
        for (int i : IntStream.range(0,oligomerDomains.length).toArray()){
            currentBase =0; 
            
            // for each domain in the oligomer
            for (int j : IntStream.range(0,oligomerDomains[i].length).toArray()){
                domainName = oligomerDomains[i][j];
                if (domainName.startsWith("c.")){
                    String compName = domainName.substring(2);
                    Integer compIndex = fixedDomainIndices.get(compName);
                    int domainLength;
                    if (compIndex != null) {
                        domainLength = fixedDomainLengths[compIndex];
                    } else {
                        compIndex = variableDomainIndices.get(compName);
                    }
                    if (compIndex != null) {
                        domainLength = variableDomainLengths[compIndex];
                        Map<Integer,int[]> oligomerToLocations = retDCTOM.get(compIndex);
                        int[] startIndexes = oligomerToLocations.get(i);
                        if (startIndexes == null){
                            startIndexes = new int[1];
                            startIndexes[0] = currentBase;
                        } else {
                            startIndexes = Arrays.copyOf(startIndexes,startIndexes.length+1);
                            startIndexes[startIndexes.length]=currentBase;
                        }
                        oligomerToLocations.put(i,startIndexes);
                    } else {
                        System.out.println("Failed to find domain "+ domainName);
                        System.exit(0);
                        domainLength = 0;
                    }
                    currentBase += domainLength;
                } else {
                    Integer domainIndex = variableDomainIndices.get(domainName);
                    int domainLength;
                    if (domainIndex != null){
                        domainLength = variableDomainLengths[domainIndex];
                    } else {
                        domainIndex = fixedDomainIndices.get(domainName);
                        if( domainIndex != null){
                            domainLength = fixedDomainLengths[domainIndex];
                        } else {
                            System.out.println("Failed to find domain "+ domainName);
                            System.exit(0);
                            domainLength = 0;
                        } 
                    }
                    currentBase += domainLength;
                }
            }
        }
        return retDCTOM;        
    }
    
    private Map<Integer,Set<Integer>> getVariableDomainToOligomers (){
        Map<Integer,Set<Integer>> retMap = new ConcurrentHashMap<>();

        //initialize an entry for every domain;
        for (int i : IntStream.range(0,variableDomainCount).toArray()){
            retMap.put(i,new HashSet<Integer>());
        }

        //for each oligomer
            // for each domain in the oligomer
                // add an entry for this association.
        String domainName;
        //for each oligomer
        for (int i : IntStream.range(0,oligomerDomains.length).toArray()){
            
            // for each domain in the oligomer
            for (int j : IntStream.range(0,oligomerDomains[i].length).toArray()){
                domainName = oligomerDomains[i][j];
                if (domainName.startsWith("c.")){
                    // if the domain is a complement.
                    String compName = domainName.substring(2);
                    Integer compIndex = variableDomainIndices.get(compName);
                    if (compIndex != null){
                        // if the domain is variable.
                        retMap.get(compIndex).add(i);
                    }
                } else {
                    // if the domain is not a complement.
                    Integer domainIndex = variableDomainIndices.get(domainName);
                    if (domainIndex != null){
                        // if the domain is variable.
                        retMap.get(domainIndex).add(i);
                    }
                }
            }
        }
        return retMap;        
    }
    
    private Map<Integer,Map<Integer,int[]>> getVariableDomainToOligomerCoordinates (){
        Map<Integer,Map<Integer,int[]>> retDTOM = new ConcurrentHashMap<>();

        //initialize an entry for every domain;
        for (int i : IntStream.range(0,variableDomainCount).toArray()){
            retDTOM.put(i,new ConcurrentHashMap<Integer,int[]>());
        }

        //for each oligomer
            // for each domain in the oligomer
                // add an entry for this association.
        int currentBase;
        String domainName;
        //for each oligomer
        for (int i : IntStream.range(0,oligomerDomains.length).toArray()){
            currentBase =0; 
            
            // for each domain in the oligomer
            for (int j : IntStream.range(0,oligomerDomains[i].length).toArray()){
                domainName = oligomerDomains[i][j];
                if (domainName.startsWith("c.")){
                    String compName = domainName.substring(2);
                    Integer compIndex = fixedDomainIndices.get(compName);
                    int domainLength;
                    if (compIndex != null) {
                        domainLength = fixedDomainLengths[compIndex];
                    } else {
                        compIndex = variableDomainIndices.get(compName);
                    }
                    if (compIndex != null) {
                        domainLength = variableDomainLengths[compIndex];
                    } else {
                        System.out.println("Failed to find domain "+ domainName);
                        System.exit(0);
                        domainLength = 0;
                    }
                    currentBase += domainLength;
                } else {
                    Integer domainIndex = variableDomainIndices.get(domainName);
                    int domainLength;
                    if (domainIndex != null){
                        Map<Integer,int[]> oligomerToLocations = retDTOM.get(domainIndex);
                        int[] startIndexes = oligomerToLocations.get(i);
                        if (startIndexes == null){
                            startIndexes = new int[1];
                            startIndexes[0] = currentBase;
                        } else {
                            startIndexes = Arrays.copyOf(startIndexes,startIndexes.length+1);
                            startIndexes[startIndexes.length]=currentBase;
                        }
                        oligomerToLocations.put(i,startIndexes);
                        domainLength = variableDomainLengths[domainIndex];
                    } else {
                        domainIndex = fixedDomainIndices.get(domainName);
                        if( domainIndex != null){
                            domainLength = fixedDomainLengths[domainIndex];
                        } else {
                            System.out.println("Failed to find domain "+ domainName);
                            System.exit(0);
                            domainLength = 0;
                        } 
                    }
                    currentBase += domainLength;
                }
            }
        }
        return retDTOM;        
    }
    
    //returns encoded oligomers with the passed domain added.
    private int[][] placeDomain(int[][] encodedOligomerSequences, int domainIndex, int[] domainSequence){
        int[][] ret = Arrays.copyOf(encodedOligomerSequences,encodedOligomerSequences.length);
                
        //for every oligomer the domain occurs on
        for(Map.Entry<Integer,int[]> oligomerCoords : vdtoc.get(domainIndex).entrySet()){
            Integer oligomerIndex = oligomerCoords.getKey();
            int[] startIndexes = oligomerCoords.getValue();

            // create a new copy of the oligomer
            int[] oldOligomer = ret[oligomerIndex];
            int[] newOligomer = Arrays.copyOf(oldOligomer, oldOligomer.length); 

            //for every occurrence on the oligomer.
            for (int k : startIndexes){
                //copy the new domain into the new oligomer copy.
                System.arraycopy(domainSequence,0,newOligomer,k,domainSequence.length);
            }

            // replace the oligomer in the EOS.
            ret[oligomerIndex]= newOligomer;
        }
        
        //for every oligomer the domain occurs on
        int[] compSequence = coder.getComplement(domainSequence);
        for(Map.Entry<Integer,int[]> oligomerCoords : vdctoc.get(domainIndex).entrySet()){
            Integer oligomerIndex = oligomerCoords.getKey();
            int[] startIndexes = oligomerCoords.getValue();

            // create a new copy of the oligomer
            int[] oldOligomer = ret[oligomerIndex];
            int[] newOligomer = Arrays.copyOf(oldOligomer, oldOligomer.length); 

            //for every occurrence on the oligomer.
            for (int k : startIndexes){
                //copy the new domain into the new oligomer copy.
                System.arraycopy(compSequence,0,newOligomer,k,compSequence.length);
            }

            // replace the oligomer in the EOS.
            ret[oligomerIndex]= newOligomer;
        }
        return ret;
    }
    
    //returns encoded oligomers with the passed domains added.
    private int[][] placeDomains(int[][] encodedOligomerSequences, int[] domainIndices, int[][] domainSequences){

        int[][] ret = new int[encodedOligomerSequences.length][];    
        
        // deep copy the encoded oligomer sequences.
        for (int i : IntStream.range(0,encodedOligomerSequences.length).toArray()){
            int[] oldSequence = encodedOligomerSequences[i];
            ret[i] = Arrays.copyOf(oldSequence,oldSequence.length);
        }
        
        //for each domain
        for(int i : IntStream.range(0,domainIndices.length).toArray()){
            int domainIndex = domainIndices[i];
            int[] domainSequence = domainSequences[i];
            
            //for every oligomer the domain occurs on
            for(Map.Entry<Integer,int[]> oligomerCoords : vdtoc.get(domainIndex).entrySet()){
                Integer oligomerIndex = oligomerCoords.getKey();
                int[] startIndexes = oligomerCoords.getValue();
                int[] newOligomer = ret[oligomerIndex];

                //for every occurrence on the oligomer.
                for (int k : startIndexes){
                    //copy the new domain into the new oligomer copy.
                    System.arraycopy(domainSequence,0,newOligomer,k,domainSequence.length);
                }
            }

            //for every oligomer the domain complement occurs on
            int[] compSequence = coder.getComplement(domainSequence);
            for(Map.Entry<Integer,int[]> oligomerCoords : vdctoc.get(domainIndex).entrySet()){
                Integer oligomerIndex = oligomerCoords.getKey();
                int[] startIndexes = oligomerCoords.getValue();
                int[] newOligomer = ret[oligomerIndex];

                //for every occurrence on the oligomer.
                for (int k : startIndexes){
                    //copy the new domain into the new oligomer copy.
                    System.arraycopy(compSequence,0,newOligomer,k,compSequence.length);
                }
            }
        }
        return ret;
    }
    
    private Map<Integer,int[][]> getDomainToOligomerCombinationsMap (Map<String,String> variableDomains, Map<String,String[]> oligomerDomains, Map<Integer,Set<Integer>> variableDomainsToOligomers){
        Map<Integer,int[][]> ret = new ConcurrentHashMap<>(); //Domains To Oligomers Map

        //for each domain
            // make a 2D array of strings to store the first and second oligomer.
            //for all oligomers the domain affects.
                // put the combinations of these oligomers into the combinations list.
            // for each oligomer the domain affects.
                // for every other oligomer.
                    // if the oligomer has not been modified.
                    // add it to the list of combinations.
        
        for (Integer variableDomainIndex : IntStream.range(0, variableDomainCount).toArray()){
            ArrayList<Integer> firstOligomers = new ArrayList<>();
            ArrayList<Integer> secondOligomers = new ArrayList<>();
            Set<Integer> vdto = variableDomainsToOligomers.get(variableDomainIndex);
            Integer[] affectedOligomersArray = vdto.toArray(new Integer[0]);
            //add all combinations of these oligomers.
            for (int i = 0; i < affectedOligomersArray.length; i++){
                for (int j = i; j < affectedOligomersArray.length; j++){
                    int index1 = affectedOligomersArray[i];
                    int index2 = affectedOligomersArray[j];
                    if(firstPartialEncodedOligomerSequences[index1].length >= firstPartialEncodedOligomerSequences[index2].length){
                        firstOligomers.add(index1);
                        secondOligomers.add(index2);
                    } else {
                        firstOligomers.add(index2);
                        secondOligomers.add(index1);
                    }
                }
            }
            
            // add other combinations if they have not yet been added.
            for (int i = 0; i < affectedOligomersArray.length; i++){
                for (int j : IntStream.range(0,oligomerCount).toArray()){
                    if (!vdto.contains(j)){
                        int index1 = affectedOligomersArray[i];
                        int index2 = j;
                        if(firstPartialEncodedOligomerSequences[index1].length >= firstPartialEncodedOligomerSequences[index2].length){
                            firstOligomers.add(index1);
                            secondOligomers.add(index2);
                        } else {
                            firstOligomers.add(index2);
                            secondOligomers.add(index1);
                        }
                    }
                }
            }

            int[][] value = new int[2][];
            value[0] = firstOligomers.stream().mapToInt(x->x).toArray();
            value[1] = secondOligomers.stream().mapToInt(x->x).toArray();
            ret.put(variableDomainIndex, value);
        }

        return ret;
    }

}
