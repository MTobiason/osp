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
package edu.boisestate.osp.seqevo;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class SeqEvoParameterManager {
    Map<String,String> params = new TreeMap<>();
    
    public SeqEvoParameterManager(String filePath){
        try{
            File file = new File(filePath);
            Scanner scanner1 = new Scanner(file);
            while(scanner1.hasNextLine()){ // for each line of input file, until end of file
                String lineText = scanner1.nextLine();
                Scanner scanner2 = new Scanner(lineText);
                scanner2.useDelimiter(",");
                
                if(!lineText.startsWith("//") && scanner2.hasNext()){
                    String parameterName = scanner2.next();
                    String parameterValue = scanner2.next();
                    params.put(parameterName, parameterValue);    
                }
                scanner2.close();
            }  
            scanner1.close();
        }
        catch (Exception e){
            System.out.println("Error while importing parameters.");
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    public Optional<BigInteger> getBigintValue(String name){
        String value = params.get(name);
        if(value == null) {return Optional.empty();}
        else {return Optional.of(new BigInteger(value));}
    }
    
    public Optional<Integer> getIntValue(String name){
        String value = params.get(name);
        if(value == null) {return Optional.empty();}
        else {return Optional.of(Integer.valueOf(value));}
    }
    
    public Optional<String> getStringValue(String name){
        String value = params.get(name);
        if(value == null) {return Optional.empty();}
        else {return Optional.of(value);}
    }
    
    public String getStringValue(String name, String defaultValue){
        String value = params.get(name);
        if(value == null) {return defaultValue;}
        else return value;
    }
    
    public Integer getIntValue(String name, int defaultValue){
        String value = params.get(name);
        if(value == null) {return defaultValue;}
        else return Integer.valueOf(value);
    }
    
    public void printParameters(){
        params.forEach((x,y)->System.out.println(x+" -> "+y));
    }
}
