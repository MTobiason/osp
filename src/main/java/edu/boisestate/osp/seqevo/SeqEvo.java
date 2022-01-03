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

import edu.boisestate.osp.optimizer.SeqEvoOptimizer;
import edu.boisestate.osp.sequence.Base;
import edu.boisestate.osp.utils.GenericBase;
import edu.boisestate.osp.design.Design;
import edu.boisestate.osp.utils.DomainDesign;
import java.util.Map;
import edu.boisestate.osp.design.DesignValidator;
import edu.boisestate.osp.design.DesignOptimizer;
import edu.boisestate.osp.design.DesignOptimizerReport;
import edu.boisestate.osp.importer.SeqEvoImporter;
import edu.boisestate.osp.sequence.LinearSequence;
import java.math.BigInteger;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class SeqEvo {
    
    final static String FDFP_DEFAULT = "se.in.fixedDomainSequences.txt";
    final static String VDFP_DEFAULT = "se.in.variableDomainSequences.txt";
    final static String LSDFP_DEFAULT = "se.in.linearStrandDomains.txt";
    final static String CSDFP_DEFAULT = "se.in.circularStrandDomains.txt";
    
    final static BigInteger scoreWeight_DEFAULT = BigInteger.valueOf(10000);
    
    final static String NL_DEFAULT = "8";
    final static String NMPC_DEFAULT ="2";
    final static String NDPM_DEFAULT = "1";
    final static String CPL_DEFAULT = "1000";
    final static String GPC_DEFAULT = "1";
    
    final static String maxAA_DEFAULT = "6";
    final static String maxCC_DEFAULT = "3";
    final static String maxGG_DEFAULT = "3";
    final static String maxTT_DEFAULT = "6";
    
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
        ISeqEvoImporter importer = new SeqEvoImporter();
        Map<String,String> param =  importer.importParametersFromTxt(PFP);
        
        Base[] bases = {GenericBase.getNew('A','T'),GenericBase.getNew('C','G'),GenericBase.getNew('G','C'), GenericBase.getNew('T','A')};
        
        int maxAA = Integer.valueOf(param.getOrDefault("maxAA",maxAA_DEFAULT));
        int maxCC = Integer.valueOf(param.getOrDefault("maxCC",maxCC_DEFAULT));
        int maxGG = Integer.valueOf(param.getOrDefault("maxGG",maxGG_DEFAULT));
        int maxTT = Integer.valueOf(param.getOrDefault("maxTT",maxTT_DEFAULT));
        
        Map<Base,Integer> maxAcceptable = new TreeMap<>();
        maxAcceptable.put(bases[0],maxAA);
        maxAcceptable.put(bases[1],maxCC);
        maxAcceptable.put(bases[2],maxGG);
        maxAcceptable.put(bases[3],maxTT);
        
        ISeqEvoValidator validator = SeqEvoValidator.getNew(maxAcceptable);
        
        String fixedDomainsFilePath = param.getOrDefault("fixedDomainSequencesFilePath", FDFP_DEFAULT);
        Map<String,LinearSequence> fixedDomainSequences = importer.importLinearSequencesFromTxt(fixedDomainsFilePath,bases);
        
        String variableDomainsFilePath = param.getOrDefault("variableDomainSequencesFilePath",VDFP_DEFAULT);
        Map<String,LinearSequence> variableDomainSequences  = importer.importLinearSequencesFromTxt(variableDomainsFilePath,bases);
        
        String linearStrandDomainsFilePath = param.getOrDefault("linearStrandDomainsFilePath",LSDFP_DEFAULT);
        Map<String,String[]> linearStrandDomains = importer.importStrandDomainsFromTxt(linearStrandDomainsFilePath);
        
        String circularStrandDomainsFilePath = param.getOrDefault("circularStrandDomainsFilePath",CSDFP_DEFAULT);
        Map<String,String[]> circularStrandDomains = importer.importStrandDomainsFromTxt(circularStrandDomainsFilePath);
        
        ISeqEvoDesign initialDesign = DomainDesign.newFromParameters(fixedDomainSequences, variableDomainSequences, linearStrandDomains, circularStrandDomains);
        
        BigInteger scoreWeight = new BigInteger(pm.getStringValue("scoreWeight",scoreWeight_DEFAULT.toString()));
        DevPro devpro = DevProAnalyzer.getNew();
        
        DesignOptimizer optimizer = SeqEvoOptimizer.getNew();
        
        DesignOptimizerReport report = optimizer.optimize(params,initialDesign,score);
    }
}
