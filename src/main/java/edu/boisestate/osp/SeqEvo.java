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
import java.util.stream.Stream;
import edu.boisestate.osp.domainbasedencodednetwork.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 *
 * @author mtobi
 */
public class SeqEvo {
    final static String version = "2.0";
    final static int NUMBERTHREADS = Runtime.getRuntime().availableProcessors();
    
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
    final int NDPG; // New Daughters Per Generation
    final static String NDPG_LABEL = "NDPG";
    final static String NDPG_DEFAULT = "1";
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
        this.NDPG = Integer.parseInt(parameters.getOrDefault(NDPG_LABEL,NDPG_DEFAULT));
        usedParameters.put(NDPG_LABEL,String.valueOf(this.NDPG));
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
        final FactoryDomainBasedEncodedNetwork factory = new FactoryDomainBasedEncodedNetwork(coder, fixedDomains, oligomerDomains, initialVariableDomains);
        
        // Scoring stuff
        final IScorer scorer = new DeltaWScorer(fixedDomains, oligomerDomains, initialVariableDomains, INTRASB, INTRASLC, INTERSB, INTERSLC, SWX);
        
        // Validator stuff
        final IValidator validator = new Validator(coder, MAXAA, MAXCC, MAXGG, MAXTT);
        
        // Work supervisor
        final MutationSupervisor mutationSupervisor = new MutationSupervisor(NUMBERTHREADS,factory,scorer,validator);
        
        IDomainBasedEncodedNetwork gen0 = factory.getNewNetwork(initialVariableDomains);
        if (!validator.isValidNetwork(gen0)) {
            System.out.println("Initial network invalid. Replacing with random sequences.");
            gen0 = factory.getType1Mutation(gen0,validator);
        }
        if (!validator.isValidNetwork(gen0)){
            System.out.println("Failed to identify valid network.");
            System.exit(0);
        }
        
        IDomainBasedEncodedScoredNetwork scoredGen0 = scorer.getScored(gen0);
        System.out.println("initialScore = "+scoredGen0.getScore());
        
        double optStartTime = System.currentTimeMillis(); // start timer for optimization runtime.
        
        // optimize
        //IDomainBasedEncodedScoredNetwork finalGen = cycle1(scoredGen0, scorer, workSupervisor, NL, CPL, NMPC, GPC, NDPG);
        
        Type1CycleReport report = (new Type1Cycle(scoredGen0, mutationSupervisor, scorer, NL, CPL, NMPC, GPC, NDPG)).call();
        IDomainBasedEncodedScoredNetwork finalGen = report.fittest;
        
        System.out.println("fittestScore = "+finalGen.getScore());
        
        //calculate runtime.
        double optEndTime   = System.currentTimeMillis(); // record evolutionary cycle endtime
        double elapsedTime = optEndTime-optStartTime;
        int H = (int)((elapsedTime/1000) / (60 *60)); // Hours
        int M = (int)(((elapsedTime/1000) / 60) % 60 ); // Minutes
        int S = (int)((elapsedTime/1000) % 60 );   // Seconds
        String elapsedTimeString = ( H + " h " + M + " m " + S + " s ");
        
        System.out.println("total time: "+ elapsedTimeString);
        
        mutationSupervisor.close();
        
        Report r = new Report(usedParameters,scoredGen0,finalGen,startTime,elapsedTimeString);
        return r;
    }
    
    static private IDomainBasedEncodedScoredNetwork cycle1(IDomainBasedEncodedScoredNetwork network, IScorer scorer, MutationSupervisor workSupervisor, int NL, int CPL, int NMPC, int GPC, int NDPG){
        //generate mutated networks.
        IDomainBasedEncodedScoredNetwork[] beforeSubCycles = Stream.concat(Stream.of(network),Arrays.stream(workSupervisor.getType1Mutation(network,NL-1))).toArray(x -> new IDomainBasedEncodedScoredNetwork[x]);
        IDomainBasedEncodedScoredNetwork[] afterSubCycles = Arrays.stream(beforeSubCycles).parallel()
            .map(n->{
            IDomainBasedEncodedScoredNetwork subCycleBest = n;
            for(int i =0; i < CPL; i++){
                subCycleBest = cycle2(subCycleBest, scorer, workSupervisor ,NMPC, GPC, NDPG);
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
    
    static private IDomainBasedEncodedScoredNetwork cycle2( IDomainBasedEncodedScoredNetwork network, IScorer scorer, MutationSupervisor workSupervisor, int NMPC, int GPC, int NDPG){
        //generate mutated networks.
        IDomainBasedEncodedScoredNetwork[] beforeSubCycles = Stream.concat(Stream.of(network),Arrays.stream(workSupervisor.getType2Mutation(network,NMPC))).toArray(x->new IDomainBasedEncodedScoredNetwork[x]);
        IDomainBasedEncodedScoredNetwork[] afterSubCycles = Arrays.stream(beforeSubCycles).parallel()
                .map(n->{
                IDomainBasedEncodedScoredNetwork subCycleBest = n;
                for(int i =0; i < GPC; i++){
                    subCycleBest = cycle3(subCycleBest,scorer,workSupervisor,NDPG);
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
    
    static private IDomainBasedEncodedScoredNetwork cycle3(IDomainBasedEncodedScoredNetwork network, IScorer scorer, MutationSupervisor workSupervisor, int NDPG){
        //generate mutated networks.
        IDomainBasedEncodedScoredNetwork[] newNetworks = Arrays.stream(workSupervisor.getType3Mutation(network,NDPG)).toArray(x->new IDomainBasedEncodedScoredNetwork[x]);
        
        // compare scores.
        IDomainBasedEncodedScoredNetwork fittest = network;
        for(IDomainBasedEncodedScoredNetwork n : newNetworks){
            if(scorer.compareFitness(n,fittest) >= 0){
                fittest = n;
            }
        }
        return fittest;
    }
        	
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
    
    static private class MutationSupervisor {
        private final ExecutorService service;
        final FactoryDomainBasedEncodedNetwork factory;
        final IScorer scorer;
        final IValidator validator;
        
        MutationSupervisor(int numberThreads, FactoryDomainBasedEncodedNetwork factory,IScorer scorer, IValidator validator){
            service = Executors.newFixedThreadPool(numberThreads);            
            this.factory = factory;
            this.scorer = scorer;
            this.validator = validator;
        }
        
        public IDomainBasedEncodedScoredNetwork getType1Mutation(IDomainBasedEncodedScoredNetwork network){
            Type1MutationThread toQueue = new Type1MutationThread(network);
            Future<IDomainBasedEncodedScoredNetwork> result = service.submit(toQueue);
            IDomainBasedEncodedScoredNetwork ret;
            try{
                ret = result.get();
            } catch (Exception e){
                System.out.println("Exception during type 1 mutation.");
                System.out.println(e.getMessage());
                ret = network;
            }
            return ret;
        }
        
        public IDomainBasedEncodedScoredNetwork[] getType1Mutation(IDomainBasedEncodedScoredNetwork network, int numberOfMutations){
            Future<IDomainBasedEncodedScoredNetwork>[] results = new Future[numberOfMutations];
            for(int i =0; i < numberOfMutations; i++){
                Type1MutationThread toQueue = new Type1MutationThread(network);
                results[i] = service.submit(toQueue);
            }
            
            IDomainBasedEncodedScoredNetwork[] ret = new IDomainBasedEncodedScoredNetwork[numberOfMutations];
            for(int i =0; i < numberOfMutations; i++){
                try{
                    ret[i] = results[i].get();
                } catch (Exception e){
                    System.out.println("Exception during type 3 mutation.");
                    System.out.println(e.getMessage());
                    ret[i] = network;
                }
            }
            return ret;
        }
        
        class Type1MutationThread implements Callable{
            final IDomainBasedEncodedScoredNetwork network;
            
            Type1MutationThread(IDomainBasedEncodedScoredNetwork network){
                this.network = network;
            }
            
            @Override
            public IDomainBasedEncodedScoredNetwork call(){
                return factory.getType1Mutation(network, scorer, validator);
            }
        }
        
        public IDomainBasedEncodedScoredNetwork getType2Mutation(IDomainBasedEncodedScoredNetwork network){
            Type2MutationThread toQueue = new Type2MutationThread(network);
            Future<IDomainBasedEncodedScoredNetwork> result = service.submit(toQueue);
            IDomainBasedEncodedScoredNetwork ret;
            try{
                ret = result.get();
            } catch (Exception e){
                System.out.println("Exception during type 2 mutation.");
                System.out.println(e.getMessage());
                ret = network;
            }
            return ret;
        }
        
        public IDomainBasedEncodedScoredNetwork[] getType2Mutation(IDomainBasedEncodedScoredNetwork network, int numberOfMutations){
            Future<IDomainBasedEncodedScoredNetwork>[] results = new Future[numberOfMutations];
            for(int i =0; i < numberOfMutations; i++){
                Type2MutationThread toQueue = new Type2MutationThread(network);
                results[i] = service.submit(toQueue);
            }
            
            IDomainBasedEncodedScoredNetwork[] ret = new IDomainBasedEncodedScoredNetwork[numberOfMutations];
            for(int i =0; i < numberOfMutations; i++){
                try{
                    ret[i] = results[i].get();
                } catch (Exception e){
                    System.out.println("Exception during type 3 mutation.");
                    System.out.println(e.getMessage());
                    ret[i] = network;
                }
            }
            return ret;
        }
        
        class Type2MutationThread implements Callable{
            final IDomainBasedEncodedScoredNetwork network;
            
            Type2MutationThread(IDomainBasedEncodedScoredNetwork network){
                this.network = network;
            }
            
            @Override
            public IDomainBasedEncodedScoredNetwork call(){
                return factory.getType2Mutation(network, scorer, validator);
            }
        }
        
        public IDomainBasedEncodedScoredNetwork getType3Mutation(IDomainBasedEncodedScoredNetwork network){
            Type3MutationThread toQueue = new Type3MutationThread(network);
            Future<IDomainBasedEncodedScoredNetwork> result = service.submit(toQueue);
            IDomainBasedEncodedScoredNetwork ret;
            try{
                ret = result.get();
            } catch (Exception e){
                System.out.println("Exception during type 3 mutation.");
                System.out.println(e.getMessage());
                ret = network;
            }
            return ret;
        }
        
        public IDomainBasedEncodedScoredNetwork[] getType3Mutation(IDomainBasedEncodedScoredNetwork network, int numberOfMutations){
            Future<IDomainBasedEncodedScoredNetwork>[] results = new Future[numberOfMutations];
            for(int i =0; i < numberOfMutations; i++){
                Type3MutationThread toQueue = new Type3MutationThread(network);
                results[i] = service.submit(toQueue);
            }
            
            IDomainBasedEncodedScoredNetwork[] ret = new IDomainBasedEncodedScoredNetwork[numberOfMutations];
            for(int i =0; i < numberOfMutations; i++){
                try{
                    ret[i] = results[i].get();
                } catch (Exception e){
                    System.out.println("Exception during type 3 mutation.");
                    System.out.println(e.getMessage());
                    ret[i] = network;
                }
            }
            return ret;
        }
        
        class Type3MutationThread implements Callable{
            final IDomainBasedEncodedScoredNetwork network;
            
            Type3MutationThread(IDomainBasedEncodedScoredNetwork network){
                this.network = network;
            }
            
            @Override
            public IDomainBasedEncodedScoredNetwork call(){
                return factory.getType3Mutation(network, scorer, validator);
            }
        }
        
        public void close(){
            service.shutdownNow();
        }
    }
    
    
    static private class Type1Cycle implements Callable{
        final MutationSupervisor mutationSupervisor;
        final IScorer scorer;
        
        final IDomainBasedEncodedScoredNetwork initialNetwork;
        final int NL;
        final int CPL;
        final int NMPC;
        final int GPC;
        final int NDPG;
        
        Type2Cycle[] subCycles;
        
        Type1Cycle (IDomainBasedEncodedScoredNetwork initialNetwork, MutationSupervisor mutationSupervisor, IScorer scorer, int NL, int CPL,int NMPC,int GPC,int NDPG){
            this.initialNetwork = initialNetwork;
            this.mutationSupervisor=mutationSupervisor;
            this.scorer = scorer;
            this.NL = NL;
            this.CPL = CPL;
            this.NMPC = NMPC;
            this.GPC = GPC;
            this.NDPG = NDPG;
        }
        
        @Override
        public Type1CycleReport call(){
            final ExecutorService es = Executors.newCachedThreadPool();
            final String[][] fittestScores = new String[NL][];
            
            //generate mutated networks.
            IDomainBasedEncodedScoredNetwork[] NewLineageMothers = mutationSupervisor.getType1Mutation(initialNetwork,NL-1);
            subCycles = new Type2Cycle[NL];
            
            Future<Type2CycleReport>[] futures = new Future[NL];
            subCycles[0] = new Type2Cycle(initialNetwork,mutationSupervisor,es,scorer,CPL,NMPC,GPC,NDPG);
            futures[0] = es.submit(subCycles[0]);
            
            for(int i = 0; i < NL-1; i++){
                subCycles[i+1] = new Type2Cycle(NewLineageMothers[i],mutationSupervisor,es,scorer,CPL,NMPC,GPC,NDPG);
                futures[i+1] = es.submit(subCycles[i+1]);
            }
            
            Type2CycleReport[] reports = new Type2CycleReport[NL];
            IDomainBasedEncodedScoredNetwork[] fittestLineageMothers = new IDomainBasedEncodedScoredNetwork[NL];
            
            try{
                for(int i = 0; i < NL; i++){
                    reports[i]= futures[i].get();
                    fittestLineageMothers[i] = reports[i].fittest;
                }
            }catch(Exception e){System.out.print(e.getMessage());}
            
            IDomainBasedEncodedScoredNetwork fittest = fittestLineageMothers[0];
            for(int i = 1; i < NL; i++){
                if(scorer.compareFitness(fittestLineageMothers[i],fittest)>=0){
                    fittest = fittestLineageMothers[i];
                }
            }
            
            for(int i = 0; i < NL; i++){
                fittestScores[i] = reports[i].fittestScores;
            }
            
            es.shutdownNow();
            
            Type1CycleReport ret = new Type1CycleReport(fittest,fittestScores,fittestLineageMothers);
            return ret;
        }
    }
    static public class Type1CycleReport{
        IDomainBasedEncodedScoredNetwork fittest;
        IDomainBasedEncodedScoredNetwork[] fittestLineageMothers;
        String[][] fittestScores;
        Type1CycleReport(IDomainBasedEncodedScoredNetwork fittest, String[][] fittestScores, IDomainBasedEncodedScoredNetwork[] fittestLineageMothers){
            this.fittest = fittest;
            this.fittestScores = fittestScores;
            this.fittestLineageMothers = fittestLineageMothers;
        }
    }
    
    static private class Type2Cycle implements Callable{
        final ExecutorService es;
        final MutationSupervisor mutationSupervisor;
        final IScorer scorer;
        
        final IDomainBasedEncodedScoredNetwork initialNetwork;
        final int CPL;
        final int NMPC;
        final int GPC;
        final int NDPG;
        
        Type2Cycle ( IDomainBasedEncodedScoredNetwork initialNetwork, MutationSupervisor mutationSupervisor, ExecutorService executorService, IScorer scorer, int CPL,int NMPC,int GPC,int NDPG){
            this.es = executorService;
            this.initialNetwork = initialNetwork;
            this.mutationSupervisor=mutationSupervisor;
            this.scorer = scorer;
            this.CPL = CPL;
            this.NMPC = NMPC;
            this.GPC = GPC;
            this.NDPG = NDPG;
        }
        
        @Override
        public Type2CycleReport call(){
            ArrayList<String> fittestScores = new ArrayList<>();
            int cycleIndex=0;
            IDomainBasedEncodedScoredNetwork currentFittest = initialNetwork;
            IDomainBasedEncodedScoredNetwork[] subCycleMothers = new IDomainBasedEncodedScoredNetwork[NMPC+1];
            Type3Cycle[] subCycles = new Type3Cycle[NMPC+1];
            subCycles[0] = new Type3Cycle(initialNetwork,mutationSupervisor,es,scorer,GPC,NDPG);
            for(int i = 0; i < NMPC; i++){
                subCycles[i+1] = new Type3Cycle(initialNetwork,mutationSupervisor,es,scorer,GPC,NDPG);
            }
            Future<Type3CycleReport>[] futures = new Future[NMPC+1];
            IDomainBasedEncodedScoredNetwork[] subCycleFittest = new IDomainBasedEncodedScoredNetwork[NMPC+1];
            String[] fittestSubScores;
            int fittestIndex;
            
            do{
                IDomainBasedEncodedScoredNetwork[] newCycleMothers = mutationSupervisor.getType2Mutation(currentFittest,NMPC);
                
                subCycleMothers[0] = currentFittest;
                for(int i = 1; i < NMPC+1; i++){
                    subCycleMothers[i] = newCycleMothers[i-1];
                }

                for(int i = 0; i < NMPC+1; i++){
                    subCycles[i].updateState(subCycleMothers[i]);
                    futures[i] = es.submit(subCycles[i]);
                }
                
                try{
                    for(int i=0; i < NMPC+1; i++){
                        subCycleFittest[i] = futures[i].get().fittest;
                    }
                } catch (Exception e){}
                
                fittestIndex =0;
                for(int i =0; i < NMPC+1;i++){
                    if (scorer.compareFitness(subCycleFittest[i], subCycleFittest[fittestIndex])>=0){
                        fittestIndex = i;
                    }
                }
                
                currentFittest = subCycleFittest[fittestIndex];
                try{
                    fittestScores.add(subCycleMothers[fittestIndex].getScore());
                    fittestSubScores = futures[fittestIndex].get().fittestScores;
                    for(String score: fittestSubScores){
                        fittestScores.add(score);
                    }
                } catch (Exception e){System.out.println(e.getMessage());}
                
                cycleIndex++;
            } while (cycleIndex < CPL);
            
            Type2CycleReport ret = new Type2CycleReport(currentFittest, fittestScores.toArray(new String[0]));
            return ret;
        }
        
    }
    
    static public class Type2CycleReport{
        IDomainBasedEncodedScoredNetwork fittest;
        String[] fittestScores;
        Type2CycleReport(IDomainBasedEncodedScoredNetwork fittest, String[] fittestScores){
            this.fittest = fittest;
            this.fittestScores = fittestScores;
        }
    }
    
    static private class Type3Cycle implements Callable{
        final ExecutorService es;
        final MutationSupervisor mutationSupervisor;
        final IScorer scorer;
        
        IDomainBasedEncodedScoredNetwork initialNetwork;
        final int GPC;
        final int NDPG;
        
        Type3Cycle ( IDomainBasedEncodedScoredNetwork initialNetwork, MutationSupervisor mutationSupervisor, ExecutorService executorService, IScorer scorer, int GPC,int NDPG){
            this.es = executorService;
            this.initialNetwork = initialNetwork;
            this.mutationSupervisor=mutationSupervisor;
            this.scorer = scorer;
            this.GPC = GPC;
            this.NDPG = NDPG;
        }
        
        public void updateState(IDomainBasedEncodedScoredNetwork initialNetwork){
            this.initialNetwork = initialNetwork;
        }
        
        @Override
        public Type3CycleReport call(){
            String[] fittestScores = new String[GPC];
            int generationIndex = 0;
            IDomainBasedEncodedScoredNetwork currentFittest = initialNetwork;
            
            do{
                IDomainBasedEncodedScoredNetwork[] newDaughters = mutationSupervisor.getType3Mutation(currentFittest,NDPG);

                for(int i =0; i < NDPG;i++){
                    if (scorer.compareFitness(newDaughters[i], currentFittest)>=0){
                        currentFittest = newDaughters[i];
                    }
                }
                fittestScores[generationIndex] = currentFittest.getScore();
                
                generationIndex++;
            } while (generationIndex < GPC);
            
            Type3CycleReport ret = new Type3CycleReport(currentFittest, fittestScores);
            return ret;
        }
        
    }
    
    static public class Type3CycleReport{
        IDomainBasedEncodedScoredNetwork fittest;
        String[] fittestScores;
        Type3CycleReport(IDomainBasedEncodedScoredNetwork fittest, String[] fittestScores){
            this.fittest = fittest;
            this.fittestScores = fittestScores;
        }
    }
}
