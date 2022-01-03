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
package edu.boisestate.osp.optimizer;

import edu.boisestate.osp.design.Design;
import edu.boisestate.osp.design.DesignValidator;
import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.DesignOptimizer;
import edu.boisestate.osp.design.DesignOptimizerReport;
import edu.boisestate.osp.seqevo.ISeqEvoAnalyzer;
import edu.boisestate.osp.seqevo.ISeqEvoDesign;
import edu.boisestate.osp.seqevo.ISeqEvoOptimizationReport;
import edu.boisestate.osp.seqevo.ISeqEvoOptimizer;
import edu.boisestate.osp.seqevo.ISeqEvoValidator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class SeqEvoOptimizer implements ISeqEvoOptimizer {

    final static String NL_DEFAULT = "8";
    final static String NMPC_DEFAULT ="2";
    final static String NDPM_DEFAULT = "1";
    final static String CPL_DEFAULT = "1000";
    final static String GPC_DEFAULT = "1";
    
    SeqEvoOptimizer(){
    }
    
    @Override
    public OptimizationReport optimize(Map<String,String> parameters, ISeqEvoAnalyzer analyzer, ISeqEvoDesign initialDesign, ISeqEvoValidator validator){
        
        double startTime = System.currentTimeMillis(); // start timer for runtime.
        OptimizationReport report = new OptimizationReport();
        Map<String,String> usedParameters = new TreeMap<>();
        
        final int cpl = Integer.valueOf(parameters.getOrDefault("Optimizer.CPL",CPL_DEFAULT));
        usedParameters.put("Optimizer.CPL",String.valueOf(cpl));
        final int gpc = Integer.valueOf(parameters.getOrDefault("Optimizer.GPC",GPC_DEFAULT));
        usedParameters.put("Optimizer.GPC",String.valueOf(gpc));
        final int ndpm = Integer.valueOf(parameters.getOrDefault("Optimizer.NDPM",NDPM_DEFAULT));
        usedParameters.put("Optimizer.NDPM",String.valueOf(ndpm));
        final int nmpc = Integer.valueOf(parameters.getOrDefault("Optimizer.NMPC",NMPC_DEFAULT));
        usedParameters.put("Optimizer.NMPC",String.valueOf(nmpc));
        final int nl = Integer.valueOf(parameters.getOrDefault("Optimizer.NL",NL_DEFAULT));
        usedParameters.put("Optimizer.NL",String.valueOf(nl));
		
        //initialize Design Arrays.
        ISeqEvoDesign[] lineageMothers = new ISeqEvoDesign[nl];
        ISeqEvoDesign[][] cycleMothers = new ISeqEvoDesign[nl][nmpc];
        ISeqEvoDesign[][][] cycleDaughters = new ISeqEvoDesign[nl][nmpc][ndpm];
        
        //initialize lineage mothers.
        lineageMothers[0] = initialDesign;
                
        for (int i =  1; i< nl; i++){
            lineageMothers[i] = getType1Mutation(initialDesign,validator);
        }
        
        //begin heuristic process
        
        //for each cycle
        for( int cycle =0; cycle< cpl; cycle++){
            //for each lineage
            for( int i = 0; i< nl; i++){
                //for each cycle mother
                for( int j = 0; j < nmpc; j++){
                    if(j == 0){
                        cycleMothers[i][0] = lineageMothers[i];
                    }
                    else{
                        cycleMothers[i][j] = getType2Mutation(lineageMothers[i],validator);
                    }
                    //for each generation in the cycle
                    for (int generation =0; generation < gpc; generation++){
                        //mutate and score k daughters
                        for (int k = 0; k< ndpm; k++){
                                cycleDaughters[i][j][k] = getType3Mutation(cycleMothers[i][j],validator);
                        }
                        
                        //for each of the k daughters, compare scores.
                        for (int k =0; k < ndpm; k++){
                            //compare daughter, mother fitness
                            if(target.compareProperty(cycleDaughters[i][j][k],cycleMothers[i][j]) >= 0){
                                    //daughter replaces mother.
                                    cycleMothers[i][j] = cycleDaughters[i][j][k];
                            }
                        }
                    }
                }
                //for each of the cycle mothers
                for (int j = 0; j < nmpc; j++){
                    // if cycle mother is fitter than the lineage mother,
                    if(target.compareProperty(cycleMothers[i][j],lineageMothers[i]) >=0){
                        //cycle mother replaces lineage mother
                        lineageMothers[i] = cycleMothers[i][j];
                    }
                }
            }
        }
        
        //find the most fit design.
        Design fittestDesign = initialDesign;
        
        //for each lineage
        for(int i =0; i< nl; i++){
            //if the lineage mother is at least as fit as the most fit design.
            if(target.compareProperty(lineageMothers[i],fittestDesign) >= 0){
                //it replaces the current most fit design.
                fittestDesign = lineageMothers[i];
            }
        }
        
        //calculate runtime.
        double endTime   = System.currentTimeMillis(); // record evolutionary cycle endtime
        double elapsedTime = endTime-startTime;
        int H = (int)((elapsedTime/1000) / (60 *60)); // Hours
        int M = (int)(((elapsedTime/1000) / 60) % 60 ); // Minutes
        int S = (int)((elapsedTime/1000) % 60 );   // Seconds
        String totalTime = ( H + " h " + M + " m " + S + " s ");
        
        OptimizerReport or = new OptimizerReport();
        or.reportFinalDesign = fittestDesign;
        or.runtimeString = totalTime;
        
        return or;
    }
    
    public static DesignOptimizer newFromParameters(int cpl, int gpc, int ndpm, int nl, int nmpc, DesignValidator validator){
        return new SeqEvoOptimizer(cpl, gpc, ndpm, nl, nmpc, validator);
    }
    
    public static SeqEvoOptimizer getNew(){
        return new SeqEvoOptimizer();
    }
    
    class OptimizerReport implements DesignOptimizerReport{
        
        Design reportFinalDesign;
        String  runtimeString;
        
        OptimizerReport(){
        }
        
        @Override
        public Design getOptimizedDesign() {
            return reportFinalDesign;
        }
    }
    
    private Design getType1Mutation(Design design, DesignValidator validator){
        return design;
    }
    
    private Design getType2Mutation(Design design, DesignValidator validator){
        return design;
    }
    
    private Design getType3Mutation(Design design, DesignValidator validator){
        return design;
    }
    
    class OptimizationReport implements ISeqEvoOptimizationReport{
    
    }
}
