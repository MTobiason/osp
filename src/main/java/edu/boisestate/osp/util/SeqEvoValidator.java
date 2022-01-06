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
package edu.boisestate.osp.util;

import edu.boisestate.osp.seqevo.IDomainDesign;
import edu.boisestate.osp.seqevo.IValidator;
import edu.boisestate.osp.sequence.Base;
import edu.boisestate.osp.sequence.CircularSequence;
import edu.boisestate.osp.sequence.LinearSequence;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class SeqEvoValidator implements IValidator{
    final static String maxAA_DEFAULT = "6";
    final static String maxCC_DEFAULT = "3";
    final static String maxGG_DEFAULT = "3";
    final static String maxTT_DEFAULT = "6";
    
    final Map<Character,Integer> maxAcceptable;
    final Map<String,String> usedParameters;

    SeqEvoValidator(Map<String,String> parameters){
        usedParameters = new TreeMap<>();
        
        int maxAA = Integer.valueOf(parameters.getOrDefault("maxAA",maxAA_DEFAULT));
        usedParameters.put("maxAA",String.valueOf(maxAA));
        int maxCC = Integer.valueOf(parameters.getOrDefault("maxCC",maxCC_DEFAULT));
        usedParameters.put("maxCC",String.valueOf(maxCC));
        int maxGG = Integer.valueOf(parameters.getOrDefault("maxGG",maxGG_DEFAULT));
        usedParameters.put("maxGG",String.valueOf(maxGG));
        int maxTT = Integer.valueOf(parameters.getOrDefault("maxTT",maxTT_DEFAULT));
        usedParameters.put("maxTT",String.valueOf(maxTT));

        maxAcceptable = Map.of('A',maxAA,'C',maxCC,'G',maxGG,'T',maxTT);
    }
    
    public static SeqEvoValidator newFromParameters(Map<String,String> parameters){
        return new SeqEvoValidator(parameters);
    }
    
    public Map<String,String> getUsedParameters(){
        return new TreeMap<>(usedParameters);
    }

    public boolean isValid(LinearSequence sequence) {
        Base[] bases = sequence.getBases();
        Base lastBase = bases[0];
        int length = 1;
        int limit = maxAcceptable.get(lastBase.getChar());
        for(int i = 1; i < bases.length;i++){
            if(bases[i].equals(lastBase)) {
                length++;
                if (length > limit) return false;
            }
            else{
                lastBase = bases[i];
                length = 1;
                limit = maxAcceptable.get(lastBase.getChar());
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
