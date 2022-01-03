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
package edu.boisestate.osp.utils;

import edu.boisestate.osp.sequence.Base;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.sequence.SequenceProperty;

/**
 *
 * @author mtobi
 */
public class GenericBaseSequence extends LinearSequence {
    final Base[] sequenceBases;
    final Base[] complementBases;
    final LinearSequence complementBaseSequence;
    final String sequenceString;
    final String complementString;
    
    final Map<SequenceProperty,String> propertyValues;
 
    GenericBaseSequence(Base[] sequence){
        sequenceBases = sequence.clone();
        complementBases = calculateComplementaryBases(sequenceBases);
        sequenceString = calculateString(sequenceBases);
        complementString = calculateString(complementBases);
        complementBaseSequence = new GenericBaseSequence(complementBases,sequenceBases,complementString,sequenceString,this);
        propertyValues = new ConcurrentHashMap<>();
    }
    
    GenericBaseSequence(Base[] bases, Base[] complementBases,String sequenceString, String complementString,GenericBaseSequence complementBaseSequence){
        this.sequenceBases = bases.clone();
        this.complementBases = complementBases.clone();
        this.sequenceString = sequenceString;
        this.complementString = complementString;
        this.complementBaseSequence = complementBaseSequence;
        propertyValues = new ConcurrentHashMap<>();
    }
    
    GenericBaseSequence(LinearSequence[] subSequences){
        int numberOfSubsequences = subSequences.length;
        
        Base[][] bases = new Base[numberOfSubsequences][];
        IntStream.range(0,numberOfSubsequences).forEach(i->{bases[i]=subSequences[i].getBases();});
        int totalBases = IntStream.range(0,numberOfSubsequences).map(i->bases[i].length).sum();
        
        sequenceBases = new Base[totalBases];
        int currentLength = 0;
        for(int i = 0; i< numberOfSubsequences; i++){
            System.arraycopy(bases[i],0,sequenceBases,currentLength, bases[i].length);
            currentLength += bases[i].length;
        }
        
        complementBases = calculateComplementaryBases(sequenceBases);
        sequenceString = calculateString(sequenceBases);
        complementString = calculateString(complementBases);
        complementBaseSequence = new GenericBaseSequence(complementBases,sequenceBases,complementString,sequenceString,this);
        propertyValues = new ConcurrentHashMap<>();
    }
    
    public static GenericBaseSequence newFromBases(Base[] sequence){
        return new GenericBaseSequence(sequence);
    }
    
    public static GenericBaseSequence newFromBaseSequences(LinearSequence[] sequences){
        return new GenericBaseSequence(sequences);
    }

    
    public LinearSequence getComplement() {
        return complementBaseSequence;
    }
    
    
    public Base[] getComplementBases() {
        return complementBases.clone();
    }

    
    public Base[] getBases() {
        return sequenceBases.clone();
    }
    
    public String getSequenceString(){
        return sequenceString;
    }

    
    public String getPropertyValue(SequenceProperty property) {
        return propertyValues.computeIfAbsent(property,x->x.calculateValue(this));
    }
    
    private static Base[] calculateComplementaryBases(Base[] sequence){
        Base[] newBases = new Base[sequence.length];
        IntStream.range(0,sequence.length).forEach(i->newBases[i]=sequence[i].getComplement());
        return newBases;
    }
    
    private static String calculateString(Base[] bases){
        char[] newSequence = new char[bases.length];
        IntStream.range(0,bases.length).forEach(i->newSequence[i]=bases[i].getChar());
        return String.valueOf(newSequence);
    }
}
