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
package edu.boisestate.osp.util.properties;

import edu.boisestate.osp.sequence.Base;
import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.DesignPropertyReport;
import edu.boisestate.osp.util.GenericDesignPropertyReport;
import edu.boisestate.osp.util.GenericStrandPair;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.seqevo.IDomainDesign;

/**
 *
 * @author mtobi
 */
public class NetworkFitnessScore implements DesignProperty{
    final static String propertyName;
    final int base;
    final Map<Integer,BigInteger> lengthToScore;
    static final CircularCircularStrandCombinations ccp = new CircularCircularStrandCombinations();
    static final CircularLinearStrandCombinations clp = new CircularLinearStrandCombinations();
    static final LinearCircularStrandCombinations lcp = new LinearCircularStrandCombinations();
    static final LinearLinearStrandCombinations llp = new LinearLinearStrandCombinations();
    
    NetworkFitnessScore(int base){
        this.base = base;
        this.propertyName = "Network Fitness Score with base = "+base;
        lengthToScore = new ConcurrentSkipListMap<>();
    }

    @Override
    public int compareProperty(IDomainDesign design1, IDomainDesign design2) {
        String value1String = design1.getPropertyValue(this);
        String value2String = design2.getPropertyValue(this);
        BigInteger value1 = new BigInteger(value1String);
        BigInteger value2 = new BigInteger(value2String);
        return value1.compareTo(value2);
    }

    @Override
    public DesignPropertyReport calculateReport(IDomainDesign design) {
        return GenericDesignPropertyReport.getNew(propertyName,getValue(design));
    }

    @Override
    public String getValue(IDomainDesign design) {
        Set<GenericStrandPair> cc = GenericStrandPair.setFromString(design.getPropertyValue(ccp));
        Set<GenericStrandPair> cl = GenericStrandPair.setFromString(design.getPropertyValue(clp));
        Set<GenericStrandPair> lc = GenericStrandPair.setFromString(design.getPropertyValue(lcp));
        Set<GenericStrandPair> ll = GenericStrandPair.setFromString(design.getPropertyValue(llp));
        
        //make or get property objects
        
        //calculate the 4 score groups.
        
        BigInteger linearLinearScore;
        BigInteger linearCircularScore;
        BigInteger circularCircularScore;
        BigInteger CircularCircularScore;
    }
    
    private BigInteger getScoreFromLength(int length){
        if(length <= 0) throw new RuntimeException("No Network Fitness Score Exists for length "+length+".");
        if(length == 1) return BigInteger.valueOf(base);
        if(length == 2) return (BigInteger.valueOf(base).pow(2)).add(BigInteger.valueOf(base).multiply(BigInteger.valueOf(2)));
        BigInteger retValue = lengthToScore.computeIfAbsent(length,value->{
            BigInteger tempValue = BigInteger.ZERO;
            for(int i = length; i >0; i--){
                tempValue = tempValue.add(BigInteger.valueOf(length-i+1).multiply(BigInteger.valueOf(base).pow(length)));
            }
            return tempValue;
        });
        return retValue;
    }
    
    abstract class NetworkFitnessStrandStrandScore implements DesignProperty{
        final int base;
        final String propertyName;
        final String strandName1;
        final String strandName2;
        
        NetworkFitnessStrandStrandScore(NetworkFitnessScore parent, String strandName1, String strandName2){
            this.base = parent.base;
            this.strandName1 = strandName1;
            this.strandName2 = strandName2;
            this.propertyName = parent.propertyName+"."+strandName1+","+strandName2;
        }

        @Override
        public int compareProperty(IDomainDesign design1, IDomainDesign design2) {
            String value1String = design1.getPropertyValue(this);
            String value2String = design2.getPropertyValue(this);
            BigInteger value1 = new BigInteger(value1String);
            BigInteger value2 = new BigInteger(value2String);
            return value1.compareTo(value2);
        }

        @Override
        public DesignPropertyReport calculateReport(IDomainDesign design) {
            return GenericDesignPropertyReport.getNew(propertyName,getValue(design));
        }
    }
    
    class NetworkFitnessLinearLinearStrandScore extends NetworkFitnessStrandStrandScore{
        @Override
        public String getValue(IDomainDesign design) {
            LinearSequence bs1 = design.getLinearStrandSequence(strandName1);
            LinearSequence bs2 = design.getLinearStrandSequence(strandName2);
            
            //calculate the length of all unique simple secondary structures using a linear search of all possible strand alignments.
            Base[] ba1 = bs1.getBases();
            Base[] ba2 = bs2.getBases();
            if(ba2.length>ba1.length) {Base[] ba3 = ba1;ba1=ba2;ba2=ba3;}
            
            BigInteger score = BigInteger.ZERO;
            
            final int ba1Length = ba1.length;
            final int ba2Length = ba2.length;
            //for each position where the 5' most base of bs2 can align with a base on ba1.
            for(int i = 0; i < ba2Length; i++){
                int length =0;
                int ba2Index = i; // + 1 because it will be decreased in first iteration.
                int ba1Index = 0; // - 1 beacuse it will be increased in first iteration.
                //for each base in ba1
                while(ba1Index < ba1Length){
                    //if the bases are complementary, increase current length;
                    if(ba1[ba1Index].isComplementary(ba2[ba2Index])) length++;
                    //if the bases are not complementary, record the structure and reset length
                    else if (length >0) {score.add(getScoreFromLength(length));length=0;}
                    
                    //increment bases
                    ba1Index++;
                    ba2Index--;
                    //if we are past the end of ba2;
                    if(ba2Index == -1){
                        //since ba2 is linear, record the current structure.
                        if (length > 0) {score.add(getScoreFromLength(length));length = 0;}
                        //start over at the begining of ba2;
                        ba2Index = ba2Length-1;
                    }
                }
                //if the search ends on a structure, record it.
                if(length>0) score.add(getScoreFromLength(length));
            }
            return score.toString();
        }
    }
    
    class NetworkFitnessLinearCircularStrandScore extends NetworkFitnessStrandStrandScore{
        @Override
        public String getValue(IDomainDesign design) {
            
        }
    }
    
    class NetworkFitnessCircularCircularStrandScore extends NetworkFitnessStrandStrandScore{
        @Override
        public String getValue(IDomainDesign design) {
            LinearSequence bs1 = design.getCircularStrandSequence(strandName1);
            LinearSequence bs2 = design.getCircularStrandSequence(strandName2);
            
            //calculate the length of all unique simple secondary structures using a linear search of all possible strand alignments.
            Base[] ba1 = bs1.getBases();
            Base[] ba2 = bs2.getBases();
            if(ba2.length>ba1.length) {Base[] ba3 = ba1;ba1=ba2;ba2=ba3;}
            
            BigInteger score = BigInteger.ZERO;
            
            final int ba1Length = ba1.length;
            final int ba2Length = ba2.length;
            //for each position where the 5' most base of bs2 can align with a base on ba1.
            for(int i = 0; i < ba2Length; i++){
                int length =0;
                int ba2Index = i; // + 1 because it will be decreased in first iteration.
                int ba1Index = 0; // - 1 beacuse it will be increased in first iteration.
                
                //Since ba1 is circular, if the first bases are complementary find the start of this structure.
                if(ba1[ba1Index].isComplementary(ba2[ba2Index])){
                    int reverseSearchIndex1=ba1Index;
                    int reverseSearchIndex2=ba2Index;
                    length= -1;
                    
                    while(length < ba2Length && ba1[reverseSearchIndex1].isComplementary(ba2[reverseSearchIndex2])){
                        length++;
                        reverseSearchIndex1--;
                        if(reverseSearchIndex1 == -1) reverseSearchIndex1=ba1Length-1;
                        reverseSearchIndex2++;
                        if(reverseSearchIndex2 == ba2Length) reverseSearchIndex2=0;
                    }
                }
                
                //for each base in ba1
                while(ba1Index < ba1Length){
                    //if the bases are complementary, increase current length;
                    if(ba1[ba1Index].isComplementary(ba2[ba2Index])) {
                        //if length of the structure is already equal to the shorter sequence, record without incrementing or clearing.
                        if(length == ba2Length) score.add(getScoreFromLength(length));
                        //otherwise increment the length;
                        else length++;
                    }
                    
                    //if the bases are not complementary, record the structure and reset length.
                    else if (length >0) {score.add(getScoreFromLength(length));length =0;}
                    
                    //increment bases
                    ba1Index++;
                    ba2Index--;
                    //if we are past the end of ba2;
                    if(ba2Index == -1){
                        //since ba2 is circular, reset ba2 without recording the current structure.
                        ba2Index = ba2Length-1;
                    }
                }
                //if the search ends on a structure.
                if(length>0) {
                    //if the structure does not continue to the start, record it.
                    if(!ba1[0].isComplementary(ba2[ba2Index])) score.add(getScoreFromLength(length));
                    //if the structure continues to the start, but is already max length record it also.
                    else if (length == ba2Length) score.add(getScoreFromLength(length));
                }
            }
            return score.toString();
        }
    }
    
    class NetworkFitnessCircularLinearStrandScore extends NetworkFitnessStrandStrandScore{
        @Override
        public String getValue(IDomainDesign design) {
            
        }
    }
    
}
