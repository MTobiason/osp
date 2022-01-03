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
package edu.boisestate.osp.seqevo;

import edu.boisestate.osp.sequence.Base;
import edu.boisestate.osp.sequence.CircularSequence;
import edu.boisestate.osp.sequence.LinearSequence;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class SeqEvoValidator implements ISeqEvoValidator {
    Map<Base,Integer> maxAcceptable;
    
    SeqEvoValidator(Map<Base,Integer> maxAcceptable){
        this.maxAcceptable = new TreeMap<>(maxAcceptable);
    }
    
    public static SeqEvoValidator getNew(Map<Base,Integer> maxAcceptable){
        return new SeqEvoValidator(maxAcceptable);
    }

    @Override
    public boolean isValid(ISeqEvoDesign design) {
        Map<String,LinearSequence> lin = design.getLinear();
        Map<String,CircularSequence> circ = design.getCircular();
        
        for(LinearSequence x: lin.values()){
            if(!isValid(x)) return false;
        }
        for(CircularSequence x: circ.values()){
            if(!isValid(x)) return false;
        }
        return true;
    }

    @Override
    public boolean isValid(LinearSequence sequence) {
        Base[] bases = sequence.getBases();
        Base lastBase = bases[0];
        int length = 1;
        int limit = maxAcceptable.get(lastBase);
        for(int i = 1; i < bases.length;i++){
            if(bases[i].equals(lastBase)) {
                length++;
                if (length > limit) return false;
            }
            else{
                lastBase = bases[i];
                length = 1;
                limit = maxAcceptable.get(lastBase);
            }
        }
        return true;
    }
    
    public boolean isValid(CircularSequence sequence){
        Base[] bases = sequence.getBases();
        Base lastBase = bases[0];
        int length = 1;
        int limit = maxAcceptable.get(lastBase);
        for(int i = 1; i < bases.length;i++){
            if(bases[i].equals(lastBase)) {
                length++;
                if (length > limit) return false;
            }
            else{
                lastBase = bases[i];
                length = 1;
                limit = maxAcceptable.get(lastBase);
            }
        }
        return true;
    }
}
