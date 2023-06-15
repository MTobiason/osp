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

import java.util.Map;
import java.util.Set;

/**
 *
 * @author mtobi
 */
interface IDomainBasedEncodedNetwork {
    // fixed domain stuff
    /**
     * Returns an n x m array of integers where the n'th array represents the 
     * n'th fixed domain domain and the m'th element specifies the m'th base in 
     * that domain.
     * @return
     */
    int[][] getFixedDomainEncodedSequences();
    /**
     * Returns a map specifying the location of each fixed domain in the encoded
     * fixed domains array.
     * @return
     */
    Map<String,Integer> getFixedDomainIndices();
    String[] getFixedDomainNames();
    String[] getFixedDomainSequences();
    
    // oligomer stuff
    
    /**
     * Returns a map specifying the location of each oligomer in the encoded
     * oligomers array.
     * @return
     */
    Map<String,Integer> getOligomerIndices();
    
    /**
     * Returns an n x m array where each n'th array represents an oligomer and
     * each m'th element represents the name of a domain or domain complement.
     * @return
     */
    String[][] getOligomerDomains();
    String[] getOligomerNames();
    String[] getOligomerSequences();
    /**
     * Returns an n x m array of integers where the n'th array represents the 
     * n'th oligomer and the m'th element specifies the m'th base on that 
     * oligomer.
     * @return
     */
    int[][] getOligomerSequencesEncoded();
    
    /**
     * Returns a 2D array with the same dimensions as getOligomerSequencesEncoded
     * Each boolean in this array indicates if the base at the matching location
     * is either fixed (false) or variable (true).
     * @return
     */
    boolean[][] getOligomerBaseIsVariableArray();
    
    // variable domain stuff
    /**
     * Returns a map specifying the location of each variable domain in the encoded
     * fixed domains array.
     * @return
     */
    Map<String,Integer> getVariableDomainIndices();
    String[] getVariableDomainNames();
    String[] getVariableDomainSequences();
    /**
     * Returns an n x m array of integers where the n'th array represents the 
     * n'th variable domain domain and the m'th element specifies the m'th base 
     * in that domain.
     * @return
     */
    int[][] getVariableDomainSequencesEncoded();
    
    /**
     * Returns a map which connects a variable domain index to a second map.
     * The second map connects an oligomer index to an integer array.
     * Each integer in the integer array indicates a base-index where the domain
     * starts. 
     * @return
     */
    Map<Integer,Map<Integer,int[]>> getVariableDomainToOligomerCoordinates();
    
    /**
     * Returns a map which connects a variable domain index to a second map.
     * The second map connects an oligomer index to an integer array.
     * Each integer in the integer array indicates a base-index where the 
     * binding complement of the domain starts.
     * @return
     */
    Map<Integer,Map<Integer,int[]>> getVariableDomainComplementToOligomerCoordinates();
    
    /**
     * 
     * Returns a map which connects a variable domain index to a set of values.
     * The set of value indicates the indices of the oligomers on which this 
     * domain or this domains complement occurs.
     * @return
     */
    Map<Integer,Set<Integer>> getVariableDomainToOligomerIndices();
    
    /**
     * 
     * Returns a map which connects a variable domain index to an int array.
     * The int array is 2 x m. These arrays together store m pairs of oligomer
     * indices representing the unique combinations associated with a given 
     * variable domain.
     * @return
     */
    Map<Integer,int[][]> getVariableDomainToOligomerCombinations();
}
