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

import edu.boisestate.osp.design.Design;
import edu.boisestate.osp.design.DesignValidator;
import edu.boisestate.osp.design.DesignProperty;
import edu.boisestate.osp.design.DesignOptimizer;
import edu.boisestate.osp.design.DesignOptimizerReport;

/**
 *
 * @author mtobi
 */
public class SeqEvoOptimizer implements DesignOptimizer {
    
    final int cpl; // Number-of-Cycles-Per-Lineage
    final int gpc; // Number-of-Generations-Per-Cycle
    final int ndpm; // Number-of-Daughters-Per-Mother
    final int nl; //Number-of-Lineages
    final int nmpc; // Number-of-Mothers-Per-Cycle
    
    final DesignValidator validator;
    
    SeqEvoOptimizer(int cpl, int gpc, int ndpm, int nl, int nmpc, DesignValidator validator){
        this.cpl = cpl;
        this.gpc = gpc;
        this.ndpm = ndpm;
        this.nl = nl;
        this.nmpc = nmpc;

        this.validator = validator;
    }
    
    @Override
    public DesignOptimizerReport optimize(Design initialDesign, DesignProperty target){
        double startTime = System.currentTimeMillis(); // start timer for runtime.
		
        //initialize Design Arrays.
        Design[] lineageMothers = new Design[nl];
        Design[][] cycleMothers = new Design[nl][nmpc];
        Design[][][] cycleDaughters = new Design[nl][nmpc][ndpm];
        
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
}
