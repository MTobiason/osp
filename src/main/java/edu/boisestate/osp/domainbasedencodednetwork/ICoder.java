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
package edu.boisestate.osp.domainbasedencodednetwork;

/**
 * This interface describes an object which produces a valid encoding or 
 * decoding of a given DNA sequence. These encodings obey the following rules.
 * (1) An integer value exists for each base in the sequence. (2) unassigned 
 * bases have a value of 0. (3) The sum of an encoded base and it's complement 
 * is zero.
 *
 *
 * @author mtobi
 */

public interface ICoder {
   
   /**
    * Decodes a given integer back into a base-sequences. 
    * base.
    * @param encodedBase
    * @return
    */
   char decode(int encodedBase);
   
   /**
    * Decodes a given encoding back to a string of base-sequences. 
    * base.
    * @param encodedSequence
    * @return
    */
   String decode(int[] encodedSequence);

   /**
    * Decodes a given encoding back to a string of base-sequences. 
    * base.
    * @param encodedSequences
    * @return
    */
   String[] decode(int[][] encodedSequences);
    
    /**
    * Encodes a given char into an integer value. 
    * base.
    * @param base 
    * @return
    */
   int encode(char base);
   
   /**
    * Generates an array of integers encoding the given sequences. Encoding must 
    * be an integer value for each base. A value of zero indicates a blank
    * base.
    * @param sequence
    * @return
    */
   int[] encode(String sequence);
   
    /**
    * Returns a "jagged" [n][m] array of integers encoding the given sequences. 
    * The nth sub-array contains an array encoding the nth sequence of the 
    * provided Sequences array. 
    * 
    * base.
    * @param sequences
    * @return
    */
   int[][] encode(String[] sequences);
   
   /**
    * Returns an integer array containing the complement of the given 
    * encoded sequence.
    * @param encodedSequence
    * @return
    */
   int[] getComplement(int[] encodedSequence);
}
