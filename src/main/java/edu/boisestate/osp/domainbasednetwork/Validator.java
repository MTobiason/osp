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

import edu.boisestate.osp.util;
import java.util.Map;

/**
 *
 * @author mtobi
 */
public class Validator implements EncodedDomainBasedNetworkFactory.IValidator{
    private boolean checkEdges(Map<String,int[]> encodedOligomers, String domainName){
        //check validity everywhere the domain occurs. (just on the edges, everything else is already checked.)
        int length = dl.get(domainName);
        for(Map.Entry<String,int[]> entry : dtom.get(domainName).entrySet()){
            String oligomer = entry.getKey();
            int[] lefts = entry.getValue();
            for (int coord : lefts){
                if (!util.checkForStretches(encodedOligomers.get(oligomer),coord,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
                if (!util.checkForStretches(encodedOligomers.get(oligomer),coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
            }
        }

        //check validity everywhere the complement occurs.
        for(Map.Entry<String,int[]> entry : ctom.get(domainName).entrySet()){
            String oligomer = entry.getKey();
            int[] lefts = entry.getValue();
            for (int coord : lefts){
                if (!util.checkForStretches(encodedOligomers.get(oligomer),coord,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
                if (!util.checkForStretches(encodedOligomers.get(oligomer),coord+length-1,maxAA,maxCC,maxGG,maxTT)){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(Map<String,int[]> encodedOligomers, Map<String,int[]> variableDomains){

        for(Map.Entry<String,int[]> entry : variableDomains.entrySet()){
            String dn = entry.getKey();
            int[] es = entry.getValue();
            if (!util.checkForStretches(es,maxAA,maxCC,maxGG,maxTT)) return false;
            if (!checkEdges(encodedOligomers,dn)) return false;
        }

        return true;
    }

    
}
