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
package edu.boisestate.osp.utils.properties;

import edu.boisestate.osp.design.Design;
import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.DesignPropertyReport;
import edu.boisestate.osp.utils.GenericDesignPropertyReport;
import edu.boisestate.osp.utils.GenericStrandPair;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import edu.boisestate.osp.sequence.LinearSequence;

/**
 *
 * @author mtobi
 */
public class LinearLinearStrandCombinations implements DesignProperty {
    final String propertyName = "Linear-Linear Strand Combinations";
    
    LinearLinearStrandCombinations(){
    }

    @Override
    public int compareProperty(Design design1, Design design2) {
        String value1 = design1.getPropertyValue(this);
        String value2 = design2.getPropertyValue(this);
        return value1.compareTo(value2);
    }

    @Override
    public DesignPropertyReport calculateReport(Design design) {
        return GenericDesignPropertyReport.getNew(propertyName,getValue(design));
    }

    @Override
    public String getValue(Design design) {
        Map<String,LinearSequence> linearStrands = design.getLinearStrandSequences();
        
        String[] linearStrandNames = linearStrands.keySet().toArray(new String[0]);
        
        Set<GenericStrandPair> linearLinearCombinations = new TreeSet<>();
        for(int i = 0; i < linearStrandNames.length; i++){
            for(int j = i; j<linearStrandNames.length;j++){
                String strand1 = linearStrandNames[i];
                String strand2 = linearStrandNames[j];
                if(strand1.compareTo(strand2)>0) {String strand3 = strand1; strand1=strand2; strand2= strand3;}
                linearLinearCombinations.add(GenericStrandPair.getNew(strand1,strand2));
            }
        }
        String returnString = GenericStrandPair.stringFromSet(linearLinearCombinations);
        return returnString;
    }
}
