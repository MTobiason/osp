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
package edu.boisestate.osp;

import edu.boisestate.osp.seqevo.IDomainDesign;
import edu.boisestate.osp.seqevo.IImporter;
import edu.boisestate.osp.seqevo.IOptimizer;
import edu.boisestate.osp.seqevo.IValidator;
import edu.boisestate.osp.sequence.Base;
import java.util.Map;
import edu.boisestate.osp.util.DevProAnalyzer;
import edu.boisestate.osp.util.GenericBase;
import edu.boisestate.osp.util.SeqEvoDomainDesign;
import edu.boisestate.osp.util.SeqEvoImporter;
import edu.boisestate.osp.util.SeqEvoOptimizer;
import edu.boisestate.osp.util.SeqEvoValidator;
import java.math.BigInteger;

/**
 *
 * @author mtobi
 */
public class SeqEvo implements IOptimizer{
    
    final static String CPL_DEFAULT = "1000";
    final static String GPC_DEFAULT = "1";
    final static String NDPM_DEFAULT = "1";
    final static String NL_DEFAULT = "8";
    final static String NMPC_DEFAULT ="2";
    
    final int CPL;
    final int GPC;
    final int NDPM;
    final int NL;
    final int NMPC;
    
    final static BigInteger scoreWeight_DEFAULT = BigInteger.valueOf(10000);
    
    final static Base[] bases = {GenericBase.getNew('A','T'),GenericBase.getNew('C','G'),GenericBase.getNew('G','C'), GenericBase.getNew('T','A')};
    
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
        
        IImporter importer = SeqEvoImporter.getNew();
        Map<String,String> params =  importer.importParametersFromTxt(PFP);
        
        IOptimizer optimizer = newFromParameters(params);
        
        BigInteger scoreWeight = new BigInteger(pm.getStringValue("scoreWeight",scoreWeight_DEFAULT.toString()));
        DevPro devpro = DevProAnalyzer.getNew();
        
        DesignOptimizer optimizer = SeqEvoOptimizer.getNew();
        
        Report report = optimizer.optimize(params,initialDesign,score);
        
        SeqEvo seqEvo = new SeqEvo(PFP);
        SeqEvoReport report = seqEvo.run();
        report.exportReport();
    }
    
    public SeqEvo newFromParameters(Map<String,String> parameters){
        
        IAnalyzer analyzer = DevPro.newFromParameters(parameters);
        IDomainDesign design = SeqEvoDomainDesign.newFromParameters(parameters);
        IMutator mutator = SeqEvoMutator.newFromParameters(parameters);
        IValidator validator = SeqEvoValidator.newFromParameters(parameters);
        
        return new SeqEvo(analyzer, design, mutator, validator);
    }
}
