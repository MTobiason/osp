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

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class Coder implements ICoder{
    
    Coder(){
    }
    
    @Override
    public char decode(int e){
        switch (e){
            case -2:
                return 'A';
            case -1:
                return 'C';
            case 0:
                return 'X';
            case 1:
                return 'G';
            case 2:
                return 'T';
            default: 
                System.out.println("Error: encoded base \"" + e + "\" not recognized." );
                System.exit(0);
                return 0;
        }
    }
    
    @Override
    public String decode(int[] encodedSequence){
        StringBuilder sb = new StringBuilder();
        for(int i : encodedSequence){
            sb.append(decode(i));
        }
        return sb.toString();
    }
    
    @Override
    public String[] decode(int[][] encodedSequences){
        String[] decoded = new String[encodedSequences.length];
        for(int i : IntStream.range(0,encodedSequences.length).toArray()){
            decoded[i] = decode(encodedSequences[i]);
        }
        return decoded;
    }
    
    @Override
    public int encode(char c){
        switch (c){
            case 'a':
            case 'A':
                return -2;
            case 'c':
            case 'C':
                return -1;
            case 'g':
            case 'G':
                return 1;
            case 't':
            case 'T':
                return 2;
            default: 
                System.out.println("Error: Base \"" + c + "\" not recognized." );
                System.exit(0);
                return 0;
        }
    }
    
    @Override
    public int[] encode(String sequence){
        
        char[] b = sequence.toCharArray();
        int[] encoded = IntStream.range(0, b.length).map(j-> encode(b[j])).toArray();
        
        return encoded;
    }
    
    @Override
    public int[][] encode(String[] sequences){
        int[][] encoded = Arrays.stream(sequences).map(sequence-> sequence.chars().map(j-> encode((char)j)).toArray()).toArray(x->new int[x][]);
        return encoded;
    }
    
    @Override
    public int[] getComplement(int[] encodedSequence){
        int[] retSequence = IntStream.range(0,encodedSequence.length).map(i-> -encodedSequence[encodedSequence.length-i-1]).toArray();
        return retSequence;
    }
}
