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

/**
 *
 * @author mtobi
 */
public class GenericBaseFactory {
    
    public GenericBaseFactory(){
    }
    
    static public Base getNewBase(char baseName,char complementName){
        return new GenericBase(baseName, complementName);
    }
    
    static class GenericBase extends Base{
        final char baseName;
        final char complementName;
        final Base complement;

        GenericBase(char baseName, char complementName){
            this.baseName = baseName;
            this.complementName = complementName; 
            complement = new GenericBase(complementName,baseName,this);
        }

        GenericBase(char baseName, char complementName, GenericBaseFactory complementaryBase){
            this.baseName = baseName;
            this.complementName = complementName; 
            complement = complementaryBase;
        }
        
        public char getChar(){
        return baseName;
        }

        public boolean isComplementary(Base base){
            return base.toString().equals(complementName);
        }

        public Base getComplement(){
            return complement;
        }
    }
}
