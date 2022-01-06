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

import edu.boisestate.osp.seqevo.IImporter;
import edu.boisestate.osp.sequence.Base;
import edu.boisestate.osp.sequence.CircularSequence;
import edu.boisestate.osp.sequence.LinearSequence;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class SeqEvoImporter implements IImporter{
    static public SeqEvoImporter getNew(){
        return new SeqEvoImporter();
    }
    
    public Map<String,String> importParametersFromTxt(String filePath){
        Map<String,String> parameters = new TreeMap<>();
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
                    parameters.put(parameterName, parameterValue);    
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
        return parameters;
    }
    
    public Map<String,LinearSequence> importLinearSequencesFromTxt(String filePath, Base[] allBases){
        Map<String,LinearSequence> newSequences = new TreeMap<String,LinearSequence>();
        Map<Character,Base> charToBaseMap = new TreeMap<>();

        for(Base x: allBases){
            charToBaseMap.put(x.getChar(),x);
        }

        LinearSequence tempSequence;

        try{
            File file = new File(filePath);
            Scanner scanner1 = new Scanner(file);
            while( scanner1.hasNextLine()) // for each line of input file, until end of file
            {
                String lineText = scanner1.nextLine();
                Scanner scanner2 = new Scanner(lineText);
                scanner2.useDelimiter(",");
                if( !lineText.startsWith("//") && scanner2.hasNext())
                {
                    String key = scanner2.next(); //record the sequence name
                    String value = scanner2.next().toUpperCase(); //record the sequence.
                    Base[] sequence = new Base[value.length()];

                    //for each character in the sequence
                    for(int i =0; i < value.length(); i++){
                        char currentChar = value.charAt(i);
                        Base x = charToBaseMap.get(currentChar);
                        if(x == null) throw new RuntimeException("Base '"+currentChar+"' in sequence '"+key+"' is not valid.");
                        sequence[i] = x;
                    }
                    newSequences.put(key,GenericLinearSequence.newFromBases(sequence));
                }
                scanner2.close();
            }  
            scanner1.close();
        }
        catch (Exception e)
        {
                System.out.println("Error while importing sequences from '"+ filePath + "'.");
                System.out.println(e.getMessage());
                System.exit(0);
        }
        return newSequences;
    }
    
    public Map<String,CircularSequence> importCircularSequencesFromTxt(String filePath, Base[] allBases){
        Map<String,CircularSequence> newSequences = new TreeMap<>();
        Map<Character,Base> charToBaseMap = new TreeMap<>();

        for(Base x: allBases){
            charToBaseMap.put(x.getChar(),x);
        }

        LinearSequence tempSequence;

        try{
            File file = new File(filePath);
            Scanner scanner1 = new Scanner(file);
            while( scanner1.hasNextLine()) // for each line of input file, until end of file
            {
                String lineText = scanner1.nextLine();
                Scanner scanner2 = new Scanner(lineText);
                scanner2.useDelimiter(",");
                if( !lineText.startsWith("//") && scanner2.hasNext())
                {
                    String key = scanner2.next(); //record the sequence name
                    String value = scanner2.next().toUpperCase(); //record the sequence.
                    Base[] sequence = new Base[value.length()];

                    //for each character in the sequence
                    for(int i =0; i < value.length(); i++){
                        char currentChar = value.charAt(i);
                        Base x = charToBaseMap.get(currentChar);
                        if(x == null) throw new RuntimeException("Base '"+currentChar+"' in sequence '"+key+"' is not valid.");
                        sequence[i] = x;
                    }
                    newSequences.put(key,GenericCircularSequence.newFromBases(sequence));
                }
                scanner2.close();
            }  
            scanner1.close();
        }
        catch (Exception e)
        {
                System.out.println("Error while importing sequences from '"+ filePath + "'.");
                System.out.println(e.getMessage());
                System.exit(0);
        }
        return newSequences;
    }

    public Map<String,String[]> importStrandDomainsFromTxt(String filePath){
        Map<String,String[]> newDomains = new TreeMap<>();
        try{
            File file = new File(filePath);
            Scanner scanner1 = new Scanner(file);

            while( scanner1.hasNextLine()){
                String lineText = scanner1.nextLine();
                Scanner scanner2 = new Scanner(lineText);
                scanner2.useDelimiter(",");

                if( !lineText.startsWith("//") && scanner2.hasNext()){
                    String key = scanner2.next(); //record the domain name
                    ArrayList<String> domains = new ArrayList<>();

                    while(scanner2.hasNext()){
                        String value = scanner2.next(); //record the domain.
                        domains.add(value);
                    }
                    newDomains.put(key,domains.toArray(new String[0]));
                }
                scanner2.close();
            }  
            scanner1.close();
        }
        catch (Exception e)
        {
                System.out.println("Error while importing domains from '"+ filePath + "'.");
                System.out.println(e.getMessage());
                System.exit(0);
        }
        return newDomains;
    }
}
