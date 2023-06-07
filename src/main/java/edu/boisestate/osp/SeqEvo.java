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

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import edu.boisestate.osp.domainbasedencodednetwork.*;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class SeqEvo {
    final static String version = "2.0";
    
    // parameters stuff
    final static String PFP_LABEL = "PFP"; //parameters File Path
    final static String PFP_DEFAULT = "in_parameters.txt";
    final Map<String,String> usedParameters;
    
    // input files
    final static String FDFP_LABEL = "FDFP"; // fixed-domains-file-path
    final static String FDFP_DEFAULT = "in_domains_fixed.txt";
    final static String VDFP_LABEL = "VDFP"; // variable-domains-file-path
    final static String VDFP_DEFAULT = "in_domains_variable.txt";
    final static String ODFP_LABEL = "ODFP"; // oligomers-file-path
    final static String ODFP_DEFAULT = "in_oligomer_domains.txt";
    
    // output files
    final static String ORFP_LABEL = "ORFP"; // Output Report File Path
    final static String ORFP_DEFAULT = "out_se_report.txt";
    final static String OVDFP_LABEL = "OVDFP"; // Output Variable Domains File Path
    final static String OVDFP_DEFAULT = "out_domains_variable.txt";
    final static String OOSFP_LABEL = "OOFP"; // Output Oligomer Sequences File Path
    final static String OOSFP_DEFAULT = "out_oligomers.txt"; //
    
    // mutation parameters
    final int MAXAA; // Max number of consecutive AA's
    final static String MAXAA_LABEL = "maxAA";
    final static String MAXAA_DEFAULT = "6";
    final int MAXCC; // Max number of consecutive AA's
    final static String MAXCC_LABEL = "maxCC";
    final static String MAXCC_DEFAULT = "3";
    final int MAXGG; // Max number of consecutive AA's
    final static String MAXGG_LABEL = "maxGG";
    final static String MAXGG_DEFAULT = "3";
    final int MAXTT; // Max number of consecutive AA's
    final static String MAXTT_LABEL = "maxTT";
    final static String MAXTT_DEFAULT = "6";
    
    // optimization parameters
    final int CPL; // Cycles Per Lineage
    final static String CPL_LABEL = "CPL";
    final static String CPL_DEFAULT = "100000";
    final int GPC; // Generations Per Cycle
    final static String GPC_LABEL = "GPC";
    final static String GPC_DEFAULT = "1";
    final int NDPM; // New Daughters Per Mother
    final static String NDPM_LABEL = "NDPM";
    final static String NDPM_DEFAULT = "1";
    final int NL; // Number of Lineages
    final static String NL_LABEL = "NL";
    final static String NL_DEFAULT = "8";
    final int NMPC; // New Mothers Per Cycle
    final static String NMPC_LABEL = "NMPC";
    final static String NMPC_DEFAULT = "2";
    
    //Scoring parameters
    final String FS = "Wx"; // Fitness Score
    final static String FS_LABEL = "FS"; // Fitness-Score
    final static String FS_DEFAULT = "Wx";
    final int SWX; // Scoring Weight X
    final static String SWX_LABEL = "scoringWeightX";
    final static String SWX_DEFAULT = "10000";
    final int INTRASLC;
    final static String INTRASLC_LABEL = "intraSLC";
    final static String INTRASLC_DEFAULT = "1";
    final int INTRASB;
    final static String INTRASB_LABEL = "intraSB";
    final static String INTRASB_DEFAULT = "10";
    final int INTERSLC;
    final static String INTERSLC_LABEL = "interSLC";
    final static String INTERSLC_DEFAULT = "1";
    final int INTERSB;
    final static String INTERSB_LABEL = "interSB";
    final static String INTERSB_DEFAULT = "10";
    
    public SeqEvo(Map<String,String> parameters){
        usedParameters = new HashMap<>();
        
        // scorer stuff
        this.SWX = Integer.valueOf(parameters.getOrDefault(SWX_LABEL,SWX_DEFAULT));
        usedParameters.put(SWX_LABEL,String.valueOf(this.SWX));
        this.INTRASLC = Integer.valueOf(parameters.getOrDefault(INTRASLC_LABEL,INTRASLC_DEFAULT));
        usedParameters.put(INTRASLC_LABEL,String.valueOf(this.INTRASLC));
        this.INTRASB = Integer.valueOf(parameters.getOrDefault(INTRASB_LABEL,INTRASB_DEFAULT));
        usedParameters.put(INTRASB_LABEL,String.valueOf(this.INTRASB));
        this.INTERSLC = Integer.valueOf(parameters.getOrDefault(INTERSLC_LABEL,INTERSLC_DEFAULT));
        usedParameters.put(INTERSLC_LABEL,String.valueOf(this.INTERSLC));
        this.INTERSB = Integer.valueOf(parameters.getOrDefault(INTERSB_LABEL,INTERSB_DEFAULT));
        usedParameters.put(INTERSB_LABEL,String.valueOf(this.INTERSB));
        
        // validator stuff
        this.MAXAA = Integer.parseInt(parameters.getOrDefault(MAXAA_LABEL, MAXAA_DEFAULT));
        usedParameters.put(MAXAA_LABEL,String.valueOf(this.MAXAA));
        this.MAXCC = Integer.parseInt(parameters.getOrDefault(MAXCC_LABEL, MAXCC_DEFAULT));
        usedParameters.put(MAXCC_LABEL,String.valueOf(this.MAXCC));
        this.MAXGG = Integer.parseInt(parameters.getOrDefault(MAXGG_LABEL, MAXGG_DEFAULT));
        usedParameters.put(MAXGG_LABEL,String.valueOf(this.MAXGG));
        this.MAXTT = Integer.parseInt(parameters.getOrDefault(MAXTT_LABEL, MAXTT_DEFAULT));
        usedParameters.put(MAXTT_LABEL,String.valueOf(this.MAXTT));
        
        // optimization stuff
        this.CPL = Integer.parseInt(parameters.getOrDefault(CPL_LABEL,CPL_DEFAULT));
        usedParameters.put(CPL_LABEL,String.valueOf(this.CPL));
        this.GPC = Integer.parseInt(parameters.getOrDefault(GPC_LABEL,GPC_DEFAULT));
        usedParameters.put(GPC_LABEL,String.valueOf(this.GPC));
        this.NDPM = Integer.parseInt(parameters.getOrDefault(NDPM_LABEL,NDPM_DEFAULT));
        usedParameters.put(NDPM_LABEL,String.valueOf(this.NDPM));
        this.NMPC = Integer.parseInt(parameters.getOrDefault(NMPC_LABEL,NMPC_DEFAULT));
        usedParameters.put(NMPC_LABEL,String.valueOf(this.NMPC));
        this.NL = Integer.parseInt(parameters.getOrDefault(NL_LABEL,NL_DEFAULT));
        usedParameters.put(NL_LABEL,String.valueOf(this.NL));
        
    }
    
    public static void main(String[] args){
        Map<String,String> usedParameters = new HashMap<>();
        String PFP = PFP_DEFAULT;
        if (args.length > 0)
        {
            if (args[0].equals("-h") || args[0].equals("--help")) // Print explanation of acceptable arguments.
            {
                    System.out.println("Usage: SeqEvo <Parameter File Path>");
                    System.out.println("Default Parameter File Path: " + PFP_DEFAULT);
                    System.exit(0);
            }

            else{
                    PFP = args[0]; // accept the next argument as the parameter file
                    System.out.println("Using Parameter File Path: " + PFP); 
            }
        }
        
        // Read parameters file.
        usedParameters.put(PFP_LABEL, PFP);
        final Map<String,String> parameters = util.importPairFromTxt(PFP);
        
        // Read fixed domains file.
        final String FDFP = parameters.getOrDefault(FDFP_LABEL,FDFP_DEFAULT);
        usedParameters.put(FDFP_LABEL,FDFP);
        final Map<String,String> fixedDomains = util.importPairFromTxt(FDFP);

        // Read variable domains file.
        final String VDFP = parameters.getOrDefault(VDFP_LABEL,VDFP_DEFAULT);
        usedParameters.put(VDFP_LABEL, VDFP);
        final Map<String,String> initialVariableDomains = util.importPairFromTxt(VDFP);

        // Read oligomer domains file.
        final String OFP = parameters.getOrDefault(ODFP_LABEL,ODFP_DEFAULT);
        usedParameters.put(ODFP_LABEL, OFP);
        final Map<String,String[]> oligomerDomains = util.importListFromTxt(OFP);
        
        
        // output stuff
        final String ORFP = parameters.getOrDefault(ORFP_LABEL, ORFP_DEFAULT);
        usedParameters.put(ORFP_LABEL,ORFP);
        final String OVDFP = parameters.getOrDefault(OVDFP_LABEL, OVDFP_DEFAULT);
        usedParameters.put(OVDFP_LABEL,OVDFP);
        final String OOSFP = parameters.getOrDefault(OOSFP_LABEL, OOSFP_DEFAULT);
        usedParameters.put(OOSFP_LABEL,OOSFP);
        
        SeqEvo s = new SeqEvo(parameters);
        
        Report report = s.run(fixedDomains,initialVariableDomains,oligomerDomains);
        
        report.exportToFile(usedParameters,report,ORFP,OVDFP,OOSFP);
    }
    
    public Report run(Map<String,String> fixedDomains, Map<String,String> initialVariableDomains, Map<String,String[]> oligomerDomains){
        final String startTime = new Date().toString();
        
        // coder stuff
        final ICoder coder = new Coder();
        
        // factory stuff
        final FactoryDomainBasedEncodedNetwork networkFactory = new FactoryDomainBasedEncodedNetwork(coder, fixedDomains, oligomerDomains, initialVariableDomains);
        
        // Scoring stuff
        final IScorer scorer = new DeltaWScorer(fixedDomains, oligomerDomains, initialVariableDomains, INTRASB, INTRASLC, INTERSB, INTERSLC, SWX);
        
        // Validator stuff
        final IValidator validator = new Validator(coder, MAXAA, MAXCC, MAXGG, MAXTT);
        
        IDomainBasedEncodedNetwork gen0 = networkFactory.getNewNetwork(initialVariableDomains);
        if (!validator.isValidNetwork(gen0)) {
            System.out.println("Initial network invalid. Replacing with random sequences.");
            gen0 = networkFactory.getType1Mutation(gen0,validator);
        }
        if (!validator.isValidNetwork(gen0)){
            System.out.println("Failed to identify valid network.");
            System.exit(0);
        }
        
        IDomainBasedEncodedScoredNetwork scoredGen0 = scorer.getScored(gen0);
        System.out.println("initialScore = "+scoredGen0.getScore());
        
        double optStartTime = System.currentTimeMillis(); // start timer for optimization runtime.
        
        // optimize
        IDomainBasedEncodedScoredNetwork finalGen = cycle1(networkFactory, scoredGen0, scorer, validator, NL, CPL, NMPC, GPC, NDPM);
        
        System.out.println("fittestScore = "+finalGen.getScore());
        
        //calculate runtime.
        double optEndTime   = System.currentTimeMillis(); // record evolutionary cycle endtime
        double elapsedTime = optEndTime-optStartTime;
        int H = (int)((elapsedTime/1000) / (60 *60)); // Hours
        int M = (int)(((elapsedTime/1000) / 60) % 60 ); // Minutes
        int S = (int)((elapsedTime/1000) % 60 );   // Seconds
        String elapsedTimeString = ( H + " h " + M + " m " + S + " s ");
        
        System.out.println("total time: "+ elapsedTimeString);
        
        Report r = new Report(usedParameters,scoredGen0,finalGen,startTime,elapsedTimeString);
        return r;
    }
    
    static private IDomainBasedEncodedScoredNetwork cycle1(FactoryDomainBasedEncodedNetwork networkFactory, IDomainBasedEncodedScoredNetwork network, IScorer scorer, IValidator validator, int NL, int CPL, int NMPC, int GPC, int NDPM){
        //generate mutated networks.
        IDomainBasedEncodedScoredNetwork[] beforeSubCycles = Stream.concat(Stream.of(network),Stream.generate(()->networkFactory.getType1Mutation(network,scorer,validator)).limit(NL-1).parallel()).toArray(x -> new IDomainBasedEncodedScoredNetwork[x]);
        IDomainBasedEncodedScoredNetwork[] afterSubCycles = Arrays.stream(beforeSubCycles).parallel()
            .map(n->{
            IDomainBasedEncodedScoredNetwork subCycleBest = n;
            for(int i =0; i < CPL; i++){
                subCycleBest = cycle2(networkFactory,subCycleBest, scorer, validator ,NMPC, GPC, NDPM);
            }
            return subCycleBest;
        }).toArray(i->new IDomainBasedEncodedScoredNetwork[i]);
        
        // compare scores.
        IDomainBasedEncodedScoredNetwork fittest = network;
        for(IDomainBasedEncodedScoredNetwork n : afterSubCycles){
            if(scorer.compareFitness(n,fittest) >= 0){
                fittest = n;
            }
        }
        return fittest;
    }
    
    static private IDomainBasedEncodedScoredNetwork cycle2(FactoryDomainBasedEncodedNetwork networkFactory, IDomainBasedEncodedScoredNetwork network, IScorer scorer, IValidator validator, int NMPC, int GPC, int NDPM){
        //generate mutated networks.
        IDomainBasedEncodedScoredNetwork[] beforeSubCycles = Stream.concat(Stream.of(network),Stream.generate(()->networkFactory.getType2Mutation(network,scorer,validator)).limit(NMPC)).toArray(x->new IDomainBasedEncodedScoredNetwork[x]);
        IDomainBasedEncodedScoredNetwork[] afterSubCycles = Arrays.stream(beforeSubCycles)
                .map(n->{
                IDomainBasedEncodedScoredNetwork subCycleBest = n;
                for(int i =0; i < GPC; i++){
                    subCycleBest = cycle3(networkFactory,subCycleBest,scorer,validator,NDPM);
                }
                return subCycleBest;
            }).toArray(i->new IDomainBasedEncodedScoredNetwork[i]);
        
        // compare scores.
        IDomainBasedEncodedScoredNetwork fittest = network;
        for(IDomainBasedEncodedScoredNetwork n : afterSubCycles){
            if(scorer.compareFitness(n,fittest) >= 0){
                fittest = n;
            }
        }
        return fittest;
    }
    
    static private IDomainBasedEncodedScoredNetwork cycle3(FactoryDomainBasedEncodedNetwork networkFactory, IDomainBasedEncodedScoredNetwork network, IScorer scorer, IValidator validator, int NDPM){
        //generate mutated networks.
        IDomainBasedEncodedScoredNetwork[] newNetworks = Stream.generate(()->networkFactory.getType3Mutation(network,scorer,validator)).limit(NDPM).toArray(i->new IDomainBasedEncodedScoredNetwork[i]);
        
        // compare scores.
        IDomainBasedEncodedScoredNetwork fittest = network;
        for(IDomainBasedEncodedScoredNetwork n : newNetworks){
            if(scorer.compareFitness(n,fittest) >= 0){
                fittest = n;
            }
        }
        return fittest;
    }
        	
//        //initialize IDomainDesign Arrays.
//        Network[] lineageMothers = new Network[NL];
//        Network[][] cycleMothers = new Network[NL][NMPC];
//        Network[][][] cycleDaughters = new Network[NL][NMPC][NDPM];
        
        //initialize lineage mothers.
        //lineageMothers = Stream.concat(Stream.of(gen0),Stream.generate(()->getType1Mutation(gen0)).limit(NL-1).parallel()).toArray((x)->new Network[x]);
        //lineageMothers[0] = gen0;
        
        //begin heuristic process
        
        /*
        //for each cycle
        for( int cycle =0; cycle< CPL; cycle++){
            
            //create mothers
            //for GPC iterations, iterate mothers.
            
            //for each lineage
            for( int i = 0; i< NL; i++){
                //for each cycle mother
                for( int j = 0; j < NMPC; j++){
                    if(j == 0){
                        cycleMothers[i][0] = lineageMothers[i];
                    }
                    else{
                        cycleMothers[i][j] = getType2Mutation(lineageMothers[i]);
                    }
                    //for each generation in the cycle
                    for (int generation =0; generation < GPC; generation++){
                        //mutate and score k daughters
                        for (int k = 0; k< NDPM; k++){
                                cycleDaughters[i][j][k] = getType3Mutation(cycleMothers[i][j]);
                        }
                        
                        //for each of the k daughters, compare scores.
                        for (int k =0; k < NDPM; k++){
                            // if daughter is more or equally fit, daughter replaces mother
                            if (compare(cycleDaughters[i][j][k],cycleMothers[i][j]) <=0){
                                cycleMothers[i][j] = cycleDaughters[i][j][k];
                            }
                        }
                    }
                }
                //for each of the cycle mothers
                for (int j = 0; j < NMPC; j++){
                    //if cycle mother is more or equally fit, cycle mother replaces lineage mother.
                    if ( compare(cycleMothers[i][j],lineageMothers[i]) <= 0){
                        lineageMothers[i] = cycleMothers[i][j];
                    }
                }
            }
        }
        
        //find the most fit design.
        Network fittest = gen0;
        
        //for each lineage
        for(int i =0; i< NL; i++){
            //if the lineage mother is at least as fit as the most fit design.
            if (compare(lineageMothers[i],fittest)<=0){
                //it replaces the current most fit design.
                fittest = lineageMothers[i];
            }
        }
        */
        
        // compare scores.
    
    static public class Report {
        Map<String,String> usedParameters;
        IDomainBasedEncodedScoredNetwork initialNetwork;
        IDomainBasedEncodedScoredNetwork finalNetwork;
        String startTime;
        String elapsedTime;
        String version = SeqEvo.version;
        
        Report(Map<String,String> usedParameters, IDomainBasedEncodedScoredNetwork initialNetwork, IDomainBasedEncodedScoredNetwork finalNetwork, String startTime, String elapsedTime){
            this.usedParameters = usedParameters;
            this.initialNetwork = initialNetwork;
            this.finalNetwork = finalNetwork;
            this.startTime = startTime;
            this.elapsedTime = elapsedTime;
        }
        
        private void exportToFile(Map<String,String> otherUsedParameters, Report report, String ORFP, String OVDFP, String OOSFP){
            Map<String,String> allUsedParameters = new HashMap<>(otherUsedParameters);
            allUsedParameters.putAll(report.usedParameters);

            IDomainBasedEncodedScoredNetwork finalNetwork = report.finalNetwork;

            try{
                // Export Report file
                if (!ORFP.equalsIgnoreCase("False")){
                    FileWriter FW = new FileWriter( ORFP );
                    PrintWriter PW = new PrintWriter( FW);

                    PW.println("Report generated by SeqEvo.");
                    PW.println("Program version: "+report.version);
                    PW.println("Start time: "+report.startTime);
                    PW.println("Elapsed time during optimization: "+report.elapsedTime);

                    // print used parameters.
                    PW.println();
                    PW.println("***************");
                    PW.println("Used Parameters");
                    PW.println("***************");
                    PW.println();

                    Map<String,String> sortedUsedParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedUsedParameters.putAll(allUsedParameters);
                    for(Map.Entry<String,String> entry : sortedUsedParameters.entrySet()){
                        PW.println(entry.getKey()+ " " + entry.getValue());
                    }

                    // print initial network
                    PW.println();
                    PW.println("***************");
                    PW.println("Initial Network");
                    PW.println("***************");
                    PW.println();

                    Map<String,Integer> sortedFixedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedFixedDomains.putAll(report.initialNetwork.getFixedDomainIndices());
                    PW.println("Fixed Domains:");
                    PW.println("--------------");
                    for(Map.Entry<String,Integer> entry : sortedFixedDomains.entrySet()){
                        PW.println(report.initialNetwork.getFixedDomainNames()[entry.getValue()]+ " " + report.initialNetwork.getFixedDomainSequences()[entry.getValue()]);
                    }

                    Map<String,Integer> sortedVariableDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedVariableDomains.putAll(report.initialNetwork.getVariableDomainIndices());
                    PW.println();
                    PW.println("Variable Domains:");
                    PW.println("-----------------");
                    for(Map.Entry<String,Integer> entry : sortedVariableDomains.entrySet()){
                        PW.println(report.initialNetwork.getVariableDomainNames()[entry.getValue()]+ " " + report.initialNetwork.getVariableDomainSequences()[entry.getValue()]);
                    }

                    Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedOligomers.putAll(report.initialNetwork.getOligomerIndices());
                    PW.println();
                    PW.println("Oligomer Domains:");
                    PW.println("-------------------");
                    for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
                        PW.print(report.initialNetwork.getOligomerNames()[entry.getValue()]);
                        for(String domain : report.initialNetwork.getOligomerDomains()[entry.getValue()]){
                            PW.print(" "+ domain);
                        }
                        PW.println();
                    }

                    PW.println();
                    PW.println("Oligomer Sequences:");
                    PW.println("-------------------");
                    for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
                        PW.println(report.initialNetwork.getOligomerNames()[entry.getValue()]+ " " + report.initialNetwork.getOligomerSequences()[entry.getValue()]);
                    }

                    PW.println();
                    PW.println("*************");
                    PW.println("Final Network");
                    PW.println("*************");
                    PW.println();

                    sortedFixedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedFixedDomains.putAll(report.finalNetwork.getFixedDomainIndices());
                    PW.println("Fixed Domains:");
                    PW.println("--------------");
                    for(Map.Entry<String,Integer> entry : sortedFixedDomains.entrySet()){
                        PW.println(report.finalNetwork.getFixedDomainNames()[entry.getValue()]+ " " + report.finalNetwork.getFixedDomainSequences()[entry.getValue()]);
                    }

                    sortedVariableDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedVariableDomains.putAll(report.finalNetwork.getVariableDomainIndices());
                    PW.println();
                    PW.println("Variable Domains:");
                    PW.println("-----------------");
                    for(Map.Entry<String,Integer> entry : sortedVariableDomains.entrySet()){
                        PW.println(report.finalNetwork.getVariableDomainNames()[entry.getValue()]+ " " + report.finalNetwork.getVariableDomainSequences()[entry.getValue()]);
                    }

                    sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedOligomers.putAll(report.finalNetwork.getOligomerIndices());
                    PW.println();
                    PW.println("Oligomer Domains:");
                    PW.println("-------------------");
                    for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
                        PW.print(report.finalNetwork.getOligomerNames()[entry.getValue()]);
                        for(String domain : report.finalNetwork.getOligomerDomains()[entry.getValue()]){
                            PW.print(" "+ domain);
                        }
                        PW.println();
                    }

                    PW.println();
                    PW.println("Oligomer Sequences:");
                    PW.println("-------------------");
                    for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
                        PW.println(report.finalNetwork.getOligomerNames()[entry.getValue()]+ " " + report.finalNetwork.getOligomerSequences()[entry.getValue()]);
                    }

                    // print initial network
                    PW.println();
                    PW.println("***************");
                    PW.println("Fitness Scores");
                    PW.println("***************");
                    PW.println();
                    PW.println("Initial score: " +report.initialNetwork.getScore());
                    PW.println("Final score: " +report.finalNetwork.getScore());

                    PW.close();
                }

                // Export variable domains from final network
                if (!OVDFP.equalsIgnoreCase("False")){
                    FileWriter FW = new FileWriter( OVDFP );
                    PrintWriter PW = new PrintWriter( FW);
                    
                    Map<String,Integer> sortedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedDomains.putAll(report.finalNetwork.getVariableDomainIndices());

                    String[] vds = finalNetwork.getVariableDomainSequences();
                    for(Map.Entry<String,Integer> entry: sortedDomains.entrySet()){
                        PW.println(entry.getKey()+ " " + vds[entry.getValue()]);
                    }
                    PW.close();
                }

                // Export oligomer sequences from final network
                if (!OOSFP.equalsIgnoreCase("False")){
                    FileWriter FW = new FileWriter( OOSFP );
                    PrintWriter PW = new PrintWriter( FW);
                    
                    Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    sortedOligomers.putAll(report.finalNetwork.getOligomerIndices());
                    String[] os = finalNetwork.getOligomerSequences();
                    for(Map.Entry<String,Integer> entry: sortedOligomers.entrySet()){
                        PW.println(entry.getKey()+ " " + os[entry.getValue()]);
                    }
                    PW.close();
                }
            } catch (Exception e) {
                System.out.println("Error while exporting network.");
                System.out.println(e.getMessage());
            }
        }
    }
}
