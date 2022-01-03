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
package edu.boisestate.osp.structure;

import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.sequence.Sequence;

/**
 *
 * @author mtobi
 */
public abstract class SimpleSecondaryStructure {
    /**
     * @return Returns one of the sequences which form the structure.
     */
    abstract Sequence getSequence1();
    
    /**
     * @return Returns the other sequence which forms the structure.
     */
    abstract Sequence getSequence2();
    
    /**
     * @return Returns the subsequence of bases within sequence1 which are involved in the structure; 
     */
    abstract LinearSequence getDuplexSequence1();
    
    /**
     * @return Returns the subsequence of bases within sequence2 which are involved in the structure; 
     */
    abstract LinearSequence getDuplexSequence2();
    
    /**
     * @return Returns the number of bases involved in the structure.
     */
    abstract int getStructureLength();
    
    /**
     * 
     * @return Returns the position of the 5' most base of sequence1 involved in the structure relative to the 5' most base of sequence1.; 
     */
    abstract int getSequence1Base1Index();
    
    /**
     * 
     * @return Returns the position of the 5' most base of sequence2 involved in the structure relative to the 5' most base of sequence2.; 
     */
    abstract int getSequence2Base1Index();
}
