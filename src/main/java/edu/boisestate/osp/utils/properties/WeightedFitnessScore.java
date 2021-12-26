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
import java.math.BigInteger;

/**
 *
 * @author mtobi
 */
public class WeightedFitnessScore implements DesignProperty{
    final String propertyName;
    final int base;
    final int x;
    
    final NetworkFitnessScore nfs;
    final OligoFitnessScore ofs;
    
    WeightedFitnessScore(int base, int x){
        this.base = base;
        this.x = x;
        nfs = NetworkFitnessScore.newFromParameters(base);
        ofs = OligoFitnessScore.newFromParameters(base);
        propertyName = ("Weighted Fitness Score. Base = " + base+", x = "+x);
    }
   
    public WeightedFitnessScore newFromParameters(int base, int x){
        return new WeightedFitnessScore(base, x);
    }

    @Override
    public int compareProperty(Design design1, Design design2) {
        String value1String = design1.getPropertyValue(this);
        String value2String = design2.getPropertyValue(this);
        BigInteger value1 = new BigInteger(value1String);
        BigInteger value2 = new BigInteger(value2String);
        return value1.compareTo(value2);
    }

    @Override
    public DesignPropertyReport calculateReport(Design design) {
        return new Report(propertyName,calculateValue(design));
    }

    @Override
    public String calculateValue(Design design) {
        String value1String = design.getPropertyValue(nfs);
        String value2String = design.getPropertyValue(ofs);
        BigInteger value1 = new BigInteger(value1String);
        BigInteger value2 = new BigInteger(value2String);
        BigInteger xBigInt = BigInteger.valueOf(x);
        return value1.add(value2.multiply(xBigInt)).toString();
    }
    
    class Report implements DesignPropertyReport{
        String name;
        String value;
        
        Report(String name, String value){
            this.name = name;
            this.value = value;
        }
        
        @Override
        public String getName(){
            return name;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
    
}
