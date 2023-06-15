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

import edu.boisestate.osp.util;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class Validator implements IValidator{
    final int maxAA;
    final int maxCC;
    final int maxGG;
    final int maxTT;
    
    final int encodedA;
    final int encodedC;
    final int encodedG;
    final int encodedT;
    
    Map<Integer,int[]> knownRanges = new ConcurrentHashMap<>();
    
    /**
     * Creates a validator which will return false if a given encoding contains 
     * a stretch of consecutive bases longer than the provided thresholds.
     * @param maxAA
     * @param maxCC
     * @param maxGG
     * @param maxTT
     */
    public Validator(ICoder coder, int maxAA, int maxCC, int maxGG, int maxTT){
        this.encodedA = coder.encode('A');
        this.encodedC = coder.encode('C');
        this.encodedG = coder.encode('G');
        this.encodedT = coder.encode('T');
        
        this.maxAA = maxAA;
        this.maxCC = maxCC;
        this.maxGG = maxGG;
        this.maxTT = maxTT;
    }
    
    // returns false if any sequence contains a stretch longer than the 
    // corresponding threshold OR a zero.
    private boolean isValid(int[] encodedSequence){
        int previousBase = 0;
        int currentRun = 1;
        for (int base : encodedSequence){
            if (base == 0) return false;
            if (base == previousBase){
                currentRun++;
                if (previousBase == encodedA){
                    if (currentRun > maxAA) return false;
                } else if (previousBase == encodedC){
                    if (currentRun > maxCC) return false;
                } else if (previousBase == encodedG){
                    if (currentRun > maxGG) return false;
                } else if (previousBase == encodedT){
                    if (currentRun > maxTT) return false;
                }
            } else {
                currentRun = 1;
            }
            previousBase = base;
        }
        return true;
    }
    
    // returns false if any sequence contains a stretch longer than the 
    // corresponding threshold OR a zero.
    private boolean isValid(int[][] encodedSequences, boolean[][] isVariableArray){
        int previousBase;
        int currentRun;
        int encodedSequence[];
        int base;
        boolean touchesVariable;
        for(int i : knownRanges.computeIfAbsent(encodedSequences.length, x->IntStream.range(0,x).toArray())){
            encodedSequence = encodedSequences[i];
            touchesVariable = false;
            previousBase = 0;
            currentRun = 1;
            
            for(int j : knownRanges.computeIfAbsent(encodedSequence.length, x->IntStream.range(0,x).toArray())){
                base = encodedSequence[j];
                if (base == 0) return false;
                if (base == previousBase){
                    touchesVariable = (touchesVariable || isVariableArray[i][j]);
                    currentRun++;
                    if (touchesVariable){
                        if (previousBase == encodedA){
                            if (currentRun > maxAA) return false;
                        } else if (previousBase == encodedC){
                            if (currentRun > maxCC) return false;
                        } else if (previousBase == encodedG){
                            if (currentRun > maxGG) return false;
                        } else if (previousBase == encodedT){
                            if (currentRun > maxTT) return false;
                        }
                    }
                } else {
                    touchesVariable = isVariableArray[i][j];
                    currentRun = 1;
                }
                previousBase = base;
            }
        }
        return true;
    }
    
    private boolean isValid(IDomainBasedEncodedNetwork network, int updatedDomainIndex){
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        int[] updatedDomainSequence = network.getVariableDomainSequencesEncoded()[updatedDomainIndex];
        
        // check sequence of the new domain.
        if (!isValid(updatedDomainSequence)) return false;
        
        // check edges of where the new domain was added.
        //check validity everywhere the domain occurs. (just on the edges, everything else is already checked.)
        int length = updatedDomainSequence.length;
        Map<Integer,int[]> oligomerIndices = network.getVariableDomainToOligomerCoordinates().get(updatedDomainIndex);
        for(Map.Entry<Integer,int[]> entry : oligomerIndices.entrySet()){
            Integer oligomerIndex = entry.getKey();
            int[] lefts = entry.getValue();
            for (int coord : lefts){
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
            }
        }
        
        oligomerIndices = network.getVariableDomainComplementToOligomerCoordinates().get(updatedDomainIndex);
        for(Map.Entry<Integer,int[]> entry : oligomerIndices.entrySet()){
            Integer oligomerIndex = entry.getKey();
            int[] lefts = entry.getValue();
            for (int coord : lefts){
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
            }
        }

        return true;
    }
    
    // returns false if any sequence contains any stretch longer than the 
    // corresponding threshold.
    private boolean isValidPartial(int[]encodedSequence){
        int previousBase = 0;
        int currentRun = 1;
        for (int base : encodedSequence){
            if (base == previousBase){
                currentRun++;
                if (previousBase == encodedA){
                    if (currentRun > maxAA) return false;
                } else if (previousBase == encodedC){
                    if (currentRun > maxCC) return false;
                } else if (previousBase == encodedG){
                    if (currentRun > maxGG) return false;
                } else if (previousBase == encodedT){
                    if (currentRun > maxTT) return false;
                }
            } else {
                currentRun = 1;
            }
            previousBase = base;
        }
        return true;
    }
    
    // returns false if any sequence contains any stretch longer than the 
    // corresponding threshold.
    private boolean isValidPartial(int[][] encodedSequences){
        int previousBase;
        int currentRun;
        for(int[] encodedSequence : encodedSequences){
            previousBase = 0;
            currentRun = 1;
            for (int base : encodedSequence){
                if (base == previousBase){
                    currentRun++;
                    if (previousBase == encodedA){
                        if (currentRun > maxAA) return false;
                    } else if (previousBase == encodedC){
                        if (currentRun > maxCC) return false;
                    } else if (previousBase == encodedG){
                        if (currentRun > maxGG) return false;
                    } else if (previousBase == encodedT){
                        if (currentRun > maxTT) return false;
                    }
                } else {
                    currentRun = 1;
                }
                previousBase = base;
            }
        }
        return true;
    }
    
    // returns false if any sequence contains any stretch longer than the 
    // corresponding threshold.
    private boolean isValidPartial(IDomainBasedEncodedNetwork network, int updatedDomainIndex){
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        int[] updatedDomainSequence = network.getVariableDomainSequencesEncoded()[updatedDomainIndex];
        
        // check sequence of the new domain.
        if (!isValidPartial(updatedDomainSequence)) return false;
        
        // check edges of where the new domain was added.
        //check validity everywhere the domain occurs. (just on the edges, everything else is already checked.)
        int length = updatedDomainSequence.length;
        Map<Integer,int[]> oligomerIndices = network.getVariableDomainToOligomerCoordinates().get(updatedDomainIndex);
        for(Map.Entry<Integer,int[]> entry : oligomerIndices.entrySet()){
            Integer oligomerIndex = entry.getKey();
            int[] lefts = entry.getValue();
            for (int coord : lefts){
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
            }
        }
        
        oligomerIndices = network.getVariableDomainComplementToOligomerCoordinates().get(updatedDomainIndex);
        for(Map.Entry<Integer,int[]> entry : oligomerIndices.entrySet()){
            Integer oligomerIndex = entry.getKey();
            int[] lefts = entry.getValue();
            for (int coord : lefts){
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
                if (!util.checkForStretches(encodedOligomers[oligomerIndex],coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns true if the network is valid and false otherwise. A valid network
     * contains no stretch of consecutive identical bases greater than the 
     * provided threshold. The exception to this rule is for stretches of such 
     * bases which do not touch a variable domain (i.e. are part of the fixed 
     * design of the network).
     * @param network
     * @return
     */
    @Override
    public boolean isValidNetwork(IDomainBasedEncodedNetwork network){
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        
        if (!isValid(encodedOligomers,network.getOligomerBaseIsVariableArray())) return false;

        return true;
    }
    
    /**
         * For a previously valid network, returns true if the network is still 
         * valid after domain updatedDomain has been updated and false 
         * otherwise.
         * @param network
         * @param updatedDomainIndex
         * @return
         */
    @Override
    public boolean isValidNetwork(IDomainBasedEncodedNetwork network, int updatedDomainIndex){
        return this.isValid(network,updatedDomainIndex);
    }

    /**
     * Returns true if the partial network is valid and false otherwise. A 
     * partial network may contain 0's in encoded sequences.
     * @param network
     * @return
     */
    @Override
    public boolean isValidPartialNetwork(IDomainBasedEncodedNetwork network){
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        
        if (!isValidPartial(encodedOligomers)) return false;

        return true;
    }
    
    /**
     * For a previously valid partial network, returns true if the network 
     * is still valid after domain updatedDomain has been updated and false 
     * otherwise.A partial network may contain 0's in encoded sequences.
     * @param network
     * @param updatedDomain
     * @return
     */
    @Override
    public boolean isValidPartialNetwork(IDomainBasedEncodedNetwork network, int updatedDomainIndex){
        return this.isValidPartial(network, updatedDomainIndex);
    }
    
}
