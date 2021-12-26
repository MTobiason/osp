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
package edu.boisestate.osp.utils.SeqEvo;

import edu.boisestate.osp.utils.GenericBaseSequence;
import edu.boisestate.osp.utils.GenericBase;
import edu.boisestate.osp.Base;
import edu.boisestate.osp.design.Design;
import edu.boisestate.osp.utils.DomainDesign;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import edu.boisestate.osp.design.DesignValidator;
import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.DesignOptimizer;
import edu.boisestate.osp.design.DesignOptimizerReport;
import edu.boisestate.osp.BaseSequence;

/**
 *
 * @author mtobi
 */
public class SeqEvo {
    
    final static String FDFP_DEFAULT = "se.in.fixedDomainSequences.txt";
    final static String VDFP_DEFAULT = "se.in.variableDomainSequences.txt";
    final static String LSDFP_DEFAULT = "se.in.linearStrandDomains.txt";
    final static String CSDFP_DEFAULT = "se.in.circularStrandDomains.txt";
    
    final static int NL_DEFAULT = 8;
    final static int NMPC_DEFAULT =2;
    final static int NDPM_DEFAULT = 1;
    final static int CPL_DEFAULT = 1000;
    final static int GPC_DEFAULT = 1;
    
    public static void main(String[] args){
        String PFP = "se.parameters.txt";
        
        if (args.length > 0)
        {
            if (args[0].equals("-h") || args[0].equals("--help")) // Print explanation of acceptable arguments.
            {
                    System.out.println("Usage: OrigamiEvolver <Parameter File Path>");
                    System.out.println("Default Parameter File Path: " + PFP);
                    System.exit(0);
            }

            else{
                    PFP = args[0]; // accept the next argument as the parameter file
                    System.out.println("Using Parameter File Path: " + PFP); 
            }
        }
        
        SeqEvoParameterManager pm = new SeqEvoParameterManager(PFP);
        
        Base[] bases = {GenericBase.getNew('A','T'),GenericBase.getNew('C','G'),GenericBase.getNew('G','C'), GenericBase.getNew('T','A')};
        
        String fixedDomainsFilePath = pm.getStringValue("fixedDomainSequencesFilePath", FDFP_DEFAULT);
        Map<String,BaseSequence> fixedDomainSequences = importSequences(fixedDomainsFilePath,bases);
        
        String variableDomainsFilePath = pm.getStringValue("variableDomainSequencesFilePath",VDFP_DEFAULT);
        Map<String,BaseSequence> variableDomainSequences  = importSequences(variableDomainsFilePath,bases);
        
        String linearStrandDomainsFilePath = pm.getStringValue("linearStrandDomainsFilePath",LSDFP_DEFAULT);
        Map<String,String[]> linearStrandDomains = importStrandDomains(linearStrandDomainsFilePath);
        
        String circularStrandDomainsFilePath = pm.getStringValue("circularStrandDomainsFilePath",CSDFP_DEFAULT);
        Map<String,String[]> circularStrandDomains = importStrandDomains(circularStrandDomainsFilePath);
        
        Design initialDesign = DomainDesign.newFromParameters(fixedDomainSequences, variableDomainSequences, linearStrandDomains, circularStrandDomains);
        
        DesignProperty score = SeqEvoScore.newFromFile(PFP);
        DesignValidator validator = SeqEvoValidator.newFromFile(PFP);
        
        int cpl = pm.getIntValue("CPL",CPL_DEFAULT);
        int gpc =  pm.getIntValue("GPC",GPC_DEFAULT);
        int ndpm =  pm.getIntValue("NDPM",NDPM_DEFAULT);
        int nl =  pm.getIntValue("NL",NL_DEFAULT);
        int nmpc =  pm.getIntValue("NMPC",NMPC_DEFAULT);
        DesignOptimizer optimizer = SeqEvoOptimizer.newFromParameters(cpl,gpc,ndpm,nl,nmpc,validator);
        
        DesignOptimizerReport report = optimizer.optimize(initialDesign,score);
    }
    
    private static Map<String,BaseSequence> importSequences(String filePath, Base[] allBases){
        Map<String,BaseSequence> newSequences = new TreeMap<String,BaseSequence>();
        Map<Character,Base> charToBaseMap = new TreeMap<>();
        
        for(Base x: allBases){
            charToBaseMap.put(x.getChar(),x);
        }
                
        BaseSequence tempSequence;
        
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
                    Base[] sequence = new GenericBase[value.length()];
                    
                    //for each character in the sequence
                    for(int i =0; i < value.length(); i++){
                        char currentChar = value.charAt(i);
                        Base x = charToBaseMap.get(currentChar);
                        if(x == null) throw new RuntimeException("Base '"+currentChar+"' in sequence '"+key+"' is not valid.");
                        sequence[i] = x;
                    }
                    newSequences.put(key,GenericBaseSequence.newFromBases(sequence));
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
    
    private static Map<String,String[]> importStrandDomains(String filePath){
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
