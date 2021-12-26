package edu.boisestate.osp.utils;

import edu.boisestate.osp.utils.SeqEvo.SeqEvoParameterManager;
import java.util.Optional;

public class OrigamiEvolver {
    
    //default Parameters
    final static String defaultPFP = "oe.parameters.txt"; // default parameters file path
    final static int defaultNL = 8;
    final static int defaultNMPC =2;
    final static int defaultNDPM = 1;
    final static int defaultCPL = 1000;
    final static int defaultGPC = 1;
        
    final int NL; //Number-of-Lineages
    final int NMPC; // Number-of-Mothers-Per-Cycle
    final int NDPM; // Number-of-Daughters-Per-Mother
    final int CPL; // Number-of-Cycles-Per-Lineage
    final int GPC; // Number-of-Generations-Per-Cycle
    
    OrigamiEvolver(String IPFP){
        
        SeqEvoParameterManager pm = new SeqEvoParameterManager(IPFP);
        
        //get parameters
        Optional<Integer> INL = pm.getIntValue("NL");
        if(INL.isPresent()) {NL = INL.get();}
        else {NL = defaultNL;}
        
        Optional<Integer> INMPC = pm.getIntValue("NMPC");
        if(INMPC.isPresent()) {NMPC = INMPC.get();}
        else {NMPC = defaultNMPC;}
        
        Optional<Integer> INDPM = pm.getIntValue("NDPM");
        if(INDPM.isPresent()) {NDPM = INDPM.get();}
        else {NDPM = defaultNDPM;}
        
        Optional<Integer> ICPL = pm.getIntValue("CPL");
        if(ICPL.isPresent()) {CPL = ICPL.get();}
        else {CPL = defaultCPL;}
        
        Optional<Integer> IGPC = pm.getIntValue("GPC");
        if(IGPC.isPresent()) {GPC = IGPC.get();}
        else {GPC = defaultCPL;}
        
    }

    public static void main(String[] args) {
        
        String PFP = defaultPFP;
        
        // read arguments
        if (args.length > 0)
        {
            if (args[0].equals("-h") || args[0].equals("--help")) // Print explanation of acceptable arguments.
            {
                    System.out.println("Usage: OrigamiEvolver <Parameter File Path>");
                    System.out.println("Default Parameter File Path: " + defaultPFP);
                    System.exit(0);
            }

            else{
                    PFP = args[0]; // accept the next argument as the parameter file
                    System.out.println("Using Parameter File Path: " + PFP); 
            }
        }
        
        OrigamiEvolver oe = new OrigamiEvolver(PFP);
        
        //import design
        //String dfp = params.getDFP(); //design file path
        //DomainLevelDesign design = new Design(dfp); //design file path
        

        //import generation 0
        //StrandSet gen0 = new StrandSet("in.FixedDomains.txt","in.VariableDomains.txt","in.Design.txt");
        //StrandSet validGen0 = StrandSet.getValidatedStrandSet(gen0
        
        double startTime = System.currentTimeMillis(); // start timer for runtime.
        
/*

        //initialize the StrandSet objects.
        StrandSet[] lineageMothers = new StrandSet[NL];
        StrandSet[][] cycleMothers = new StrandSet[NL][NMPC];
        StrandSet[][][] cycleDaughters = new StrandSet[NL][NMPC][NDPM];

        //start evolutionary cycles

        for (int i =  0; i< NL; i++)
        {
                if (i == 0)
                {
                        lineageMothers[i] = validGen0;
                        System.out.println("validGen0 validity is "+ StrandSet.isValid(validGen0)); 
                }
                else{
                        lineageMothers[i] = StrandSet.getMutation(validGen0,1);
                }
        }

        for( int cycle =0; cycle< CPL; cycle++)
        {
                for( int i = 0; i< NL; i++)
                {
                        for( int j = 0; j < NMPC; j++) // for each cycle mother
                        {
                                //StrandSet.clone(lineageMothers[i],cycleMothers[i][j]);

                                if(j == 0)
                                {
                                        cycleMothers[i][j] = lineageMothers[i];
                                        //StrandSet.mutate(cycleMothers[i][j],2);
                                }
                                else
                                {
                                        cycleMothers[i][j] = StrandSet.getMutation(lineageMothers[i],2);
                                }
                                for (int generation =0; generation < GPC; generation++)
                                {
                                        //for the k daughters
                                        for (int k = 0; k< NDPM; k++)
                                        {
                                                cycleDaughters[i][j][k] = StrandSet.getMutation(cycleMothers[i][j],3);
                                                //StrandSet.clone(cycleMothers[i][j],cycleDaughters[i][j][k]);
                                                //StrandSet.mutate(cycleDaughters[i][j][k],3);
                                        }
                                        //for each of the k daughters, collect scores.
                                        for (int k =0; k < NDPM; k++) //for each daughter
                                        {
                                                if(StrandSet.getScore(cycleDaughters[i][j][k]).compareTo(StrandSet.getScore(cycleMothers[i][j])) < 0)
                                                {
                                                        //swap mother and daughter.
                                                        StrandSet temp = cycleDaughters[i][j][k];
                                                        cycleDaughters[i][j][k] = cycleMothers[i][j];
                                                        cycleMothers[i][j] = temp;
                                                }
                                        }
                                }
                        }
                        //for the cycle mothers, collect scores
                        for (int j = 0; j < NMPC; j++)
                        {
                                if(StrandSet.getScore(cycleMothers[i][j]).compareTo(StrandSet.getScore(lineageMothers[i])) < 0)
                                {
                                        //swap lineage mother and cycle mother.
                                        StrandSet temp = cycleMothers[i][j];
                                        cycleMothers[i][j] = lineageMothers[i];
                                        lineageMothers[i] = temp;
                                }
                        }
                        //System.out.println("Cycle Completed");

                }

                System.out.println("Cycle " +(cycle+1) + " completed.");
        };

        StrandSet fittestSystem = validGen0;	
        for(int i =0; i< NL; i++)
        {
                if(StrandSet.getScore(lineageMothers[i]).compareTo(StrandSet.getScore(fittestSystem)) < 0)
                {
                        fittestSystem = lineageMothers[i];
                }
        }

        double endTime   = System.currentTimeMillis(); // record evolutionary cycle endtime
        double elapsedTime = endTime-startTime;
        int H = (int)((elapsedTime/1000) / (60 *60)); // Hours
        int M = (int)(((elapsedTime/1000) / 60) % 60 ); // Minutes
        int S = (int)((elapsedTime/1000) % 60 );   // Seconds
        String totalTime = ( H + " h " + M + " m " + S + " s ");

        try {
            FileWriter filewriter = new FileWriter( "se.out.report.txt" ); //initiate the export for initial lineage mothers
            PrintWriter PW = new PrintWriter( filewriter);
            StrandSet.export(validGen0,PW);
            StrandSet.export(fittestSystem,PW);
            PW.close();
        }
        catch (Exception e){
            System.out.println("Error while exporting report :: ");
            System.out.println(e.getMessage());
        }

        System.out.println("Runtime of Evolutionary Process: " + totalTime);
        System.out.println();
*/
    }
}
