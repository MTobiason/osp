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

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author mtobi
 */
public class GenericStrandPair implements Comparable<GenericStrandPair>{
    public final String strand1;
    public final String strand2;

    GenericStrandPair(String strand1,String strand2){
        this.strand1 = strand1;
        this.strand2 = strand2;
    }

    @Override
    public int compareTo(GenericStrandPair other) {
        int value = strand1.compareTo(other.strand1);
        if(value ==0) value = strand2.compareTo(other.strand2);
        return value;
    }
    
    public static GenericStrandPair getNew(String strandName1, String strandName2){
        return new GenericStrandPair(strandName1,strandName2);
    }
    
    public static Set<GenericStrandPair> setFromString(String value){
        Set<GenericStrandPair> returnSet = new TreeSet<GenericStrandPair>();
        Scanner s1 = new Scanner(value);
        s1.useDelimiter(".");
        while(s1.hasNext()){
            Scanner s2 = new Scanner(s1.next());
            s2.useDelimiter(",");
            String strand1 = s2.next();
            String strand2 = s2.next();
            returnSet.add(new GenericStrandPair(strand1,strand2));
            s2.close();
        }
        s1.close();
        return returnSet;
    }
    
    public static String stringFromSet(Set<GenericStrandPair> set){
        StringBuilder sb = new StringBuilder();
        set.iterator().forEachRemaining(i->sb.append(i.strand1+","+i.strand2+"."));
        return sb.toString();
    }
}
