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

import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.DesignPropertyReport;
import edu.boisestate.osp.util.GenericDesignPropertyReport;
import edu.boisestate.osp.util.GenericStrandPair;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.seqevo.IDomainDesign;

/**
 *
 * @author mtobi
 */
public class CircularCircularStrandCombinations implements DesignProperty {
    final String propertyName = "Linear-Linear Strand Combinations";
    
    CircularCircularStrandCombinations(){
    }

    @Override
    public int compareProperty(IDomainDesign design1, IDomainDesign design2) {
        String value1 = design1.getPropertyValue(this);
        String value2 = design2.getPropertyValue(this);
        return value1.compareTo(value2);
    }

    @Override
    public DesignPropertyReport calculateReport(IDomainDesign design) {
        return GenericDesignPropertyReport.getNew(propertyName,getValue(design));
    }

    @Override
    public String getValue(IDomainDesign design) {
        Map<String,LinearSequence> circularStrands = design.getCircularStrandSequences();
        
        String[] circularNames = circularStrands.keySet().toArray(new String[0]);
        
        Set<GenericStrandPair> circularCircularCombinations = new TreeSet<>();
        for(int i = 0; i < circularNames.length; i++){
            for(int j = i; j<circularNames.length;j++){
                String strand1 = circularNames[i];
                String strand2 = circularNames[j];
                if(strand1.compareTo(strand2)>0) {String strand3 = strand1; strand1=strand2; strand2= strand3;}
                circularCircularCombinations.add(GenericStrandPair.getNew(strand1,strand2));
            }
        }
        String returnString = GenericStrandPair.stringFromSet(circularCircularCombinations);
        return returnString;
    }
}
