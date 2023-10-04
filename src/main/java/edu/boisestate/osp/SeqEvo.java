/*
 * Copyright (c) 2019 Boise State University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.boisestate.osp;

import edu.boisestate.osp.validators.IValidator;
import edu.boisestate.osp.validators.Validator;
import edu.boisestate.osp.scorers.IScorer;
import edu.boisestate.osp.scorers.DeltaWScorer;
import edu.boisestate.osp.coders.ICoder;
import edu.boisestate.osp.coders.Coder;
import edu.boisestate.osp.networks.FactoryDomainBasedEncodedNetwork;
import edu.boisestate.osp.networks.IDomainBasedEncodedNetwork;
import edu.boisestate.osp.networks.IDomainBasedEncodedScoredNetwork;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class SeqEvo {
    final static String VERSION = "2.0";
    final static int NUMBERTHREADS = Runtime.getRuntime().availableProcessors();
    
    //Scoring parameters
    final static String FS_DEFAULT = "Wx";
    final static String FS_LABEL = "FITNESS_SCORE"; // Fitness-Score
    final static String[] FS_VALUES = new String[] {"Wx"};
    final static String SWX_LABEL = "scoringWeightX";
    final static String SWX_DEFAULT = "10000";
    final static String INTRA_SLC_LABEL = "intraSLC";
    final static String INTRA_SLC_DEFAULT = "1";
    final static String INTRA_SB_LABEL = "intraSB";
    final static String INTRA_SB_DEFAULT = "10";
    final static String INTER_SLC_LABEL = "interSLC";
    final static String INTER_SLC_DEFAULT = "1";
    final static String INTER_SB_LABEL = "interSB";
    final static String INTER_SB_DEFAULT = "10";
    
    // mutation parameters
    final static String MAX_AA_LABEL = "maxAA";
    final static String MAX_AA_DEFAULT = "6";
    final static String MAX_CC_LABEL = "maxCC";
    final static String MAX_CC_DEFAULT = "3";
    final static String MAX_GG_LABEL = "maxGG";
    final static String MAX_GG_DEFAULT = "3";
    final static String MAX_TT_LABEL = "maxTT";
    final static String MAX_TT_DEFAULT = "6";
    
    // optimization parameters
    final static String CPL_LABEL = "CPL";
    final static String CPL_DEFAULT = "100000";
    final static String GPC_LABEL = "GPC";
    final static String GPC_DEFAULT = "1";
    final static String NDPG_LABEL = "NDPG";
    final static String NDPG_DEFAULT = "1";
    final static String NL_LABEL = "NL";
    final static String NL_DEFAULT = "8";
    final static String NMPC_LABEL = "NMPC";
    final static String NMPC_DEFAULT = "2";
    
    final static ArrayList<Parameter> scoringParameters = new ArrayList<>();
    static {
        scoringParameters.add(new StringParameter( FS_DEFAULT, "Fitness score to optimize. The only currently accepted value is Wx.",FS_LABEL,FS_VALUES));
        scoringParameters.add(new IntegerParameter( INTER_SB_DEFAULT, "Inter-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex. Must be an integer greater than or equal to 0 and less than "+Integer.MAX_VALUE+".", INTER_SB_LABEL, 0, Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( INTER_SLC_DEFAULT, "Inter-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", INTER_SLC_LABEL, 1, Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( INTRA_SB_DEFAULT, "Intra-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex. Must be an integer greater than or equal to 0 and less than "+Integer.MAX_VALUE+".", INTRA_SB_LABEL, 0, Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( INTRA_SLC_DEFAULT, "Intra-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", INTRA_SLC_LABEL, 1, Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( SWX_DEFAULT, "W will be calculated as O times this value plus N. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", SWX_LABEL, 0, Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( MAX_AA_DEFAULT, "Maximum number of consecutive adenosine bases. Any stretch of bases greater than this number will make a network invalid. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", MAX_AA_LABEL,1,Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( MAX_CC_DEFAULT, "Maximum number of consecutive cytosine bases. Any stretch of bases greater than this number will make a network invalid. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", MAX_CC_LABEL,1,Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( MAX_GG_DEFAULT, "Maximum number of consecutive guanine bases. Any stretch of bases greater than this number will make a network invalid. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", MAX_GG_LABEL,1,Integer.MAX_VALUE));
        scoringParameters.add(new IntegerParameter( MAX_TT_DEFAULT, "Maximum number of consecutive thymine bases. Any stretch of bases greater than this number will make a network invalid. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", MAX_TT_LABEL,1,Integer.MAX_VALUE));
    
    }
    
    final static ArrayList<Parameter> heuristicParameters = new ArrayList<>();
    static {
        heuristicParameters.add(new IntegerParameter( CPL_DEFAULT, "Cycles-Per-Lineage. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", CPL_LABEL,1,Integer.MAX_VALUE));
        heuristicParameters.add(new IntegerParameter( GPC_DEFAULT, "Generations-Per-Cycle. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", GPC_LABEL,1,Integer.MAX_VALUE));
        heuristicParameters.add(new IntegerParameter( NDPG_DEFAULT, "New-Daughters-Per-Generation. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", NDPG_LABEL,1,Integer.MAX_VALUE));
        heuristicParameters.add(new IntegerParameter( NL_DEFAULT, "Number-of-Lineages. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", NL_LABEL,1,Integer.MAX_VALUE));
        heuristicParameters.add(new IntegerParameter( NMPC_DEFAULT, "New-Mothers-Per-Cycle. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", NMPC_LABEL,1,Integer.MAX_VALUE));
    }
    
    final static ArrayList<Parameter> availableParameters = new ArrayList<>();
    static {
        availableParameters.addAll(scoringParameters);
        availableParameters.addAll(heuristicParameters);
    }
    
    final static Map<String,Parameter> labelToParameterMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); 
    static { for (Parameter p : availableParameters) labelToParameterMap.put(p.getLabel(), p);}
    
    public SeqEvo(){
    }
    
    public static class Request{
        Map<String,String> fixedDomains;
        Map<String,String> initialVariableDomains;
        Map<String,String[]> oligomerDomains;
        Map<String,String>  parameters;
        PrintStream streamForUpdates;
        
        Request(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> initialVariableDomains, Map<String,String[]> oligomerDomains, PrintStream streamForUpdates){
            this.fixedDomains = fixedDomains;
            this.initialVariableDomains= initialVariableDomains;
            this.oligomerDomains = oligomerDomains;
            this.parameters = parseParameters(parameters);
            this.streamForUpdates = streamForUpdates;
        }
        
        Request(Map<String,String> parameters, Map<String,String> fixedDomains, Map<String,String> initialVariableDomains, Map<String,String[]> oligomerDomains){
            this.fixedDomains = fixedDomains;
            this.initialVariableDomains= initialVariableDomains;
            this.oligomerDomains = oligomerDomains;
            this.streamForUpdates = null;
        }
        
        private Map<String,String> parseParameters(Map<String,String> incomingParam){
            Map<String,String> retParam = new HashMap<String,String>();
            // for each incoming parameter.
            for (Map.Entry<String,String> entry : incomingParam.entrySet()){
                String label = entry.getKey();
                String value = entry.getValue();
                Parameter p = labelToParameterMap.get(label);
                if (p != null){
                    if (p.isValid(value)){
                        retParam.put(label, value);
                    } else {
                        System.out.println("Value "+value+" is not valid for parameter "+ label);
                        System.exit(1);
                    }
                }
            }
            
            return retParam;
        }
    }
    
    public Report run(Request request){
        final String startTimeString = new Date().toString();
        double startTime = System.currentTimeMillis(); // start timer for optimization runtime.
        
        Map<String,String> parameters = request.parameters;
        Map<String,String> usedParameters = new HashMap<>();
        
        // scorer stuff
        int SWX = Integer.parseInt(parameters.getOrDefault(SWX_LABEL,SWX_DEFAULT));
        usedParameters.put(SWX_LABEL,String.valueOf(SWX));
        int INTRASLC = Integer.parseInt(parameters.getOrDefault(INTRA_SLC_LABEL,INTRA_SLC_DEFAULT));
        usedParameters.put(INTRA_SLC_LABEL,String.valueOf(INTRASLC));
        int INTRASB = Integer.parseInt(parameters.getOrDefault(INTRA_SB_LABEL,INTRA_SB_DEFAULT));
        usedParameters.put(INTRA_SB_LABEL,String.valueOf(INTRASB));
        int INTERSLC = Integer.parseInt(parameters.getOrDefault(INTER_SLC_LABEL,INTER_SLC_DEFAULT));
        usedParameters.put(INTER_SLC_LABEL,String.valueOf(INTERSLC));
        int INTERSB = Integer.parseInt(parameters.getOrDefault(INTER_SB_LABEL,INTER_SB_DEFAULT));
        usedParameters.put(INTER_SB_LABEL,String.valueOf(INTERSB));
        
        // validator stuff
        int MAXAA = Integer.parseInt(parameters.getOrDefault(MAX_AA_LABEL, MAX_AA_DEFAULT));
        usedParameters.put(MAX_AA_LABEL,String.valueOf(MAXAA));
        int MAXCC = Integer.parseInt(parameters.getOrDefault(MAX_CC_LABEL, MAX_CC_DEFAULT));
        usedParameters.put(MAX_CC_LABEL,String.valueOf(MAXCC));
        int MAXGG = Integer.parseInt(parameters.getOrDefault(MAX_GG_LABEL, MAX_GG_DEFAULT));
        usedParameters.put(MAX_GG_LABEL,String.valueOf(MAXGG));
        int MAXTT = Integer.parseInt(parameters.getOrDefault(MAX_TT_LABEL, MAX_TT_DEFAULT));
        usedParameters.put(MAX_TT_LABEL,String.valueOf(MAXTT));
        
        // optimization stuff
        int CPL = Integer.parseInt(parameters.getOrDefault(CPL_LABEL,CPL_DEFAULT));
        usedParameters.put(CPL_LABEL,String.valueOf(CPL));
        int GPC = Integer.parseInt(parameters.getOrDefault(GPC_LABEL,GPC_DEFAULT));
        usedParameters.put(GPC_LABEL,String.valueOf(GPC));
        int NDPG = Integer.parseInt(parameters.getOrDefault(NDPG_LABEL,NDPG_DEFAULT));
        usedParameters.put(NDPG_LABEL,String.valueOf(NDPG));
        int NMPC = Integer.parseInt(parameters.getOrDefault(NMPC_LABEL,NMPC_DEFAULT));
        usedParameters.put(NMPC_LABEL,String.valueOf(NMPC));
        int NL = Integer.parseInt(parameters.getOrDefault(NL_LABEL,NL_DEFAULT));
        usedParameters.put(NL_LABEL,String.valueOf(NL));
        
        // coder stuff
        final ICoder coder = new Coder();
        
        // factory stuff
        final FactoryDomainBasedEncodedNetwork factory = new FactoryDomainBasedEncodedNetwork(coder, request.fixedDomains, request.oligomerDomains, request.initialVariableDomains);
        
        // Scoring stuff
        final IScorer scorer = new DeltaWScorer(request.fixedDomains, request.oligomerDomains, request.initialVariableDomains, INTRASB, INTRASLC, INTERSB, INTERSLC, SWX, NUMBERTHREADS, 4);
        String scoreLabel = scorer.getScoreLabel();
        String scoreUnits = scorer.getScoreUnits();
        
        // Validator stuff
        final IValidator validator = new Validator(coder, MAXAA, MAXCC, MAXGG, MAXTT);
        
        // Work supervisor
        final MutationSupervisor mutationSupervisor = new MutationSupervisor(NUMBERTHREADS,factory,scorer,validator);
        
        IDomainBasedEncodedNetwork gen0 = factory.getNewNetwork(request.initialVariableDomains);
        if (!validator.isValidNetwork(gen0)) {
            request.streamForUpdates.println("Initial network invalid. Replacing with random sequences.");
            gen0 = factory.getType1Mutation(gen0,validator);
        }
        if (!validator.isValidNetwork(gen0)){
            System.out.println("Failed to identify valid network.");
            System.exit(0);
        }
        
        IDomainBasedEncodedScoredNetwork scoredGen0 = scorer.getScored(gen0);
        
        double optStartTime = System.currentTimeMillis(); // start timer for optimization runtime.
        
        // optimize
        OptimizationSupervisor os = new OptimizationSupervisor( mutationSupervisor, scorer, NL, CPL, NMPC, GPC, NDPG, request.streamForUpdates);
        OptimizationSupervisor.OptimizerReport report = os.optimize(scoredGen0);
        
        IDomainBasedEncodedScoredNetwork finalGen = report.fittest;
        String[][] lineageFittestScores = report.lineageFittestScores;
        
        //calculate runtime.
        double optEndTime   = System.currentTimeMillis(); // record evolutionary cycle endtime
        double optimizationTimeSeconds = (optEndTime-optStartTime)/1000;
        int H = (int)(optimizationTimeSeconds / (60 *60)); // Hours
        int M = (int)((optimizationTimeSeconds / 60) % 60 ); // Minutes
        int S = (int)(optimizationTimeSeconds % 60 );   // Seconds
        String optimizationTimeString = ( H + " h " + M + " m " + S + " s ");
        
        double endTime = System.currentTimeMillis(); // record evolutionary cycle endtime
        double totalTimeSeconds = (endTime-startTime)/1000;
        H = (int)(totalTimeSeconds / (60 *60)); // Hours
        M = (int)((totalTimeSeconds / 60) % 60 ); // Minutes
        S = (int)(totalTimeSeconds % 60 );   // Seconds
        String totalTimeString = ( H + " h " + M + " m " + S + " s ");
        
        mutationSupervisor.close();
        
        Report r = new Report(usedParameters,scoredGen0,finalGen,startTimeString,optimizationTimeString,totalTimeString,optimizationTimeSeconds,totalTimeSeconds,lineageFittestScores,scoreLabel, scoreUnits);
        return r;
    }
    
    static public class Report {
        public final Map<String,String> usedParameters;
        public final IDomainBasedEncodedScoredNetwork initialNetwork;
        public final IDomainBasedEncodedScoredNetwork finalNetwork;
        public final String startTime;
        public final String optimizationTime;
        public final String totalTime;
        public final double optimizationTimeSeconds;
        public final double totalTimeSeconds;
        public final String version = SeqEvo.VERSION;
        public final String[][] lineageFittestScores;
        public final String scoreLabel;
        public final String scoreUnits;
        
        Report(Map<String,String> usedParameters, IDomainBasedEncodedScoredNetwork initialNetwork, IDomainBasedEncodedScoredNetwork finalNetwork, String startTime, String optimizationTime, String totalTime, double optimizationTimeSeconds, double totalTimeSeconds, String[][] lineageFittestScores, String scoreLabel, String scoreUnits){
            this.usedParameters = usedParameters;
            this.initialNetwork = initialNetwork;
            this.finalNetwork = finalNetwork;
            this.startTime = startTime;
            this.optimizationTime = optimizationTime;
            this.totalTime = totalTime;
            this.optimizationTimeSeconds = optimizationTimeSeconds;
            this.totalTimeSeconds = totalTimeSeconds;
            this.lineageFittestScores = lineageFittestScores;
            this.scoreLabel = scoreLabel;
            this.scoreUnits = scoreUnits;
        }
    }
    
    public static void main(String[] args){
        SeqEvo s = new SeqEvo();
        Map<String,String> usedParameters = new HashMap<>();
        
        final String PFP_LABEL = "PFP"; //parameters File Path
        final String PFP_DEFAULT = "se_parameters.txt";
        final String EXAMPLE_PARAMETERS_FILE_DEFAULT = "se_parameters_example.txt";

        // input files
        final String FDFP_LABEL = "IN_FIXED_DOMAINS"; // fixed-domains-file-path
        final String FDFP_DEFAULT = "se_in_domains_fixed.txt";
        final String VDFP_LABEL = "IN_VARIABLE_DOMAINS"; // variable-domains-file-path
        final String VDFP_DEFAULT = "se_in_domains_variable.txt";
        final String ODFP_LABEL = "IN_OLIGOMERS"; // oligomers-file-path
        final String ODFP_DEFAULT = "se_in_oligomers.txt";

        // output files
        final String OUTPUT_DIRECTORY_DEFAULT = "output" + File.separator;
        final String OUTPUT_DIRECTORY_LABEL = "OUT_DIRECTORY";
        final String FILE_REPORT_LABEL = "OUT_FILE_REPORT"; // Output Report File Path
        final String FILE_REPORT_DEFAULT = "se_out_report.txt";
        final String FILE_FINAL_DOMAINS_VARIABLE_LABEL = "OUT_FILE_DOMAINS_VARIABLE"; // Output Variable Domains File Path
        final String FILE_FINAL_DOMAINS_VARIABLE_DEFAULT = "se_out_domains_variable.txt";
        final String FILE_FINAL_OLIGOMER_SEQUENCES_LABEL = "OUT_FILE_OLIGOMERS"; // Output Oligomer Sequences File Path
        final String FILE_FINAL_OLIGOMER_SEQUENCES_DEFAULT = "se_out_oligomers.txt"; //
        final String FILE_SCORE_TRAJECTORY_LABEL = "OUT_FILE_SCORES"; // Output Score Trajectories File Path
        final String FILE_SCORE_TRAJECTORY_DEFAULT = "se_out_score_trajectories.csv";
        final String FILE_LOG_SCORE_TRAJECTORY_LABEL = "OUT_FILE_LOG_SCORES"; // Output Score Trajectories File Path
        final String FILE_LOG_SCORE_TRAJECTORY_DEFAULT = "se_out_score_trajectories_log.csv";
        
        ArrayList<InputFileParameter> inputFileParameters = new ArrayList<>();
        {
            inputFileParameters.add(new InputFileParameter( FDFP_LABEL, "Text file listing the fixed domains for the network. Each line should contain a single domain formated as DOMAIN-NAME <tab> BASE-SEQUENCE. Acceptable bases are A/T/C/G. ",FDFP_DEFAULT));
            inputFileParameters.add(new InputFileParameter( VDFP_LABEL, "Text file listing the variable domains for the network. Each line should contain a single domain formated as DOMAIN-NAME <tab> BASE-SEQUENCE. Acceptable bases are A/T/C/G. ",VDFP_DEFAULT));
            inputFileParameters.add(new InputFileParameter( ODFP_LABEL, "Text file listing the oligomers for the network. Each line should contain a single oligomer formated as an OLIGOMER-NAME, <tab>, then a list of domain-names or domain-name complements. Complements are denoted by prepending c. to a domain name. Convention is to list the 5' most domain first. ",ODFP_DEFAULT));
        }
        
        ArrayList<Parameter> inputParameters = new ArrayList<>();
        {
            inputParameters.addAll(inputFileParameters);
        }
        
        ArrayList<OutputFileParameter> outputFileParameters = new ArrayList<>();
        {
            outputFileParameters.add(new ReportFileParameter( FILE_REPORT_DEFAULT, "Text file detailing key results and parameters used. Value must be either false or end with .txt", FILE_REPORT_LABEL, usedParameters));
            outputFileParameters.add(new VariableDomainsFileParameter( FILE_FINAL_DOMAINS_VARIABLE_DEFAULT, "Text file listing the base-sequence of the variable domains following optimization. Value must be either false or end with .txt", FILE_FINAL_DOMAINS_VARIABLE_LABEL));
            outputFileParameters.add(new FinalOligomersFileParameter( FILE_FINAL_OLIGOMER_SEQUENCES_DEFAULT, "Text file listing the base-sequence of the oligomers following optimization. Value must be either false or end with .txt", FILE_FINAL_OLIGOMER_SEQUENCES_LABEL));
            outputFileParameters.add(new ScoresFileParameter( FILE_SCORE_TRAJECTORY_DEFAULT, "Text file listing the scores of the networks in each generation. Value must be either false or end with .csv", FILE_SCORE_TRAJECTORY_LABEL));
            outputFileParameters.add(new LogScoresFileParameter( FILE_LOG_SCORE_TRAJECTORY_DEFAULT, "Text file listing the scores of the networks in logarithmically distributed generations. Value must be either false or end with .csv", FILE_LOG_SCORE_TRAJECTORY_LABEL));
        }
        ArrayList<Parameter> outputParameters = new ArrayList<>();
        {
            outputParameters.add(new OutputDirectoryParameter(OUTPUT_DIRECTORY_DEFAULT, "Directory where output files will be created. ", OUTPUT_DIRECTORY_LABEL ));
            outputParameters.addAll(outputFileParameters);
        }
        
        ArrayList<Parameter> allAvailableParameters = new ArrayList<>(availableParameters);
        {
            allAvailableParameters.addAll(inputFileParameters);
            allAvailableParameters.addAll(outputParameters);
        }
        
        String PFP = PFP_DEFAULT;
        if (args.length > 0)
        {
            if (args[0].equals("-h") || args[0].equals("--help")) // Print explanation of acceptable arguments.
            {
                System.out.println("SeqEvo version "+ VERSION);
                System.out.println("Usage: SeqEvo <Parameter File Path>");
                System.out.println("If no parameter file path is provided, the default value of "+ PFP_DEFAULT + " will be used.");
                System.out.println("SeqEvo -h or --help will print this help message.");
                System.out.println("SeqEvo -ep or --exampleParameters will create an example parameter file.");
                System.exit(0);
            }
            
            if (args[0].equals("-ep") || args[0].equals("--exampleParameters")){
                try{
                    PrintStream PS = new PrintStream(EXAMPLE_PARAMETERS_FILE_DEFAULT);
                    PS.println("// Format: parameter label <tab> default value");
                    PS.println();
                    PS.println("// ****************");
                    PS.println("// Input Parameters");
                    PS.println("// ****************");
                            
                    for (Parameter p : inputFileParameters){
                        PS.println();
                        PS.println("//\t"+p.getLabel()+" - " +p.getDescription());
                        PS.println(p.getLabel()+"\t"+p.getDefault());
                    }
                    
                    PS.println();
                    PS.println("// ********************");
                    PS.println("// Heuristic Parameters");
                    PS.println("// ********************");
                            
                    for (Parameter p : heuristicParameters){
                        PS.println();
                        PS.println("//\t"+p.getLabel()+" - " +p.getDescription());
                        PS.println(p.getLabel()+"\t"+p.getDefault());
                    }
                    
                    PS.println();
                    PS.println("// ******************");
                    PS.println("// Scoring Parameters");
                    PS.println("// ******************");
                            
                    for (Parameter p : scoringParameters){
                        PS.println();
                        PS.println("//\t"+p.getLabel()+" - " +p.getDescription());
                        PS.println(p.getLabel()+"\t"+p.getDefault());
                    }
                    
                    PS.println();
                    PS.println("// *****************");
                    PS.println("// Output Parameters");
                    PS.println("// *****************");
                            
                    for (Parameter p : outputFileParameters){
                        PS.println();
                        PS.println("//\t"+p.getLabel()+" - " +p.getDescription());
                        PS.println(p.getLabel()+"\t"+p.getDefault());
                    }
                    
                    PS.close();
                    System.out.println("\""+EXAMPLE_PARAMETERS_FILE_DEFAULT+"\" file containing default parameters created.");
                }catch (Exception e){
                    System.err.println("Error creating example parameters file.");
                    System.err.println(e.getMessage());
                }
                System.exit(0);
            }
            
            
            PFP = args[0]; // accept the next argument as the parameter file
            System.out.println("Using Parameter File Path: " + PFP); 
        }
        
        // Read parameters file.
        usedParameters.put(PFP_LABEL, PFP);
        final Map<String,String> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        System.out.println("Importing parameters file: "+ PFP);
        parameters.putAll(util.importPairFromTxt(PFP));
        
        for (Parameter p : allAvailableParameters){
            String value = parameters.get(p.getLabel());
            if (value != null){
                if (p.isValid(value)){
                    usedParameters.put(p.getLabel(), value);
                } else {
                    System.out.println("Value "+value+" is not valid for parameter "+ p.getLabel());
                    System.exit(1);
                }
            } else {
                usedParameters.put(p.getLabel(),p.getDefault());
            }
        }
        
        // Read fixed domains file.
        final String FDFP = usedParameters.get(FDFP_LABEL);
        System.out.println("Importing fixed domains file: "+ FDFP);
        final Map<String,String> fixedDomains = util.importPairFromTxt(FDFP);

        // Read variable domains file.
        final String VDFP = usedParameters.get(VDFP_LABEL);
        System.out.println("Importing variable domains file: "+ VDFP);
        final Map<String,String> initialVariableDomains = util.importPairFromTxt(VDFP);

        // Read oligomer domains file.
        final String OFP = usedParameters.get(ODFP_LABEL);
        System.out.println("Importing oligomers file: "+ OFP);
        final Map<String,String[]> oligomerDomains = util.importListFromTxt(OFP);
        
        Request request = new Request(usedParameters, fixedDomains, initialVariableDomains, oligomerDomains, System.out);
        System.out.println("Beginning search.");
        Report report = s.run(request);
        
        // export output files.
        String outputDirectory = usedParameters.get(OUTPUT_DIRECTORY_LABEL);
        for (OutputFileParameter p : outputFileParameters){
            String fp = usedParameters.get(p.getLabel());
            if (!fp.equalsIgnoreCase("false")){
                try{
                    Files.createDirectories(Paths.get(outputDirectory));
                    PrintStream PS = new PrintStream( outputDirectory + fp);
                    p.printFile(PS, report);
                    PS.close();
                } catch (Exception e){
                    System.err.println("Error while exporting "+p.getLabel()+" file.");
                    System.err.println(e.getMessage());
                }
            }
        }
        
        System.out.println("Initial "+report.scoreLabel+" ("+report.scoreUnits+"): "+report.initialNetwork.getScore());
        System.out.println("Fittest "+report.scoreLabel+" ("+report.scoreUnits+"): "+report.finalNetwork.getScore());
        
        System.out.println("Optimization time: "+ report.optimizationTime);
        System.out.println("Total time: " + report.totalTime);
        
        System.exit(0);
    }
    
    private static void printReport (PrintStream ps, Report report, Map<String,String> otherUsedParameters){
        Map<String,String> allUsedParameters = new HashMap<>(otherUsedParameters);
        allUsedParameters.putAll(report.usedParameters);

        ps.println("Report generated by SeqEvo.");
        ps.println("Program version: "+report.version);
        ps.println("Start time: "+report.startTime);
        ps.println("Optimization time: "+report.optimizationTime);
        ps.println("Total time: "+report.totalTime);

        ps.println();
        ps.println("**************");
        ps.println("Fitness Scores");
        ps.println("**************");
        ps.println();
        ps.println("Initial network "+report.scoreLabel+" ("+report.scoreUnits+"): " +report.initialNetwork.getScore());
        ps.println("Fittest network "+report.scoreLabel+" ("+report.scoreUnits+"): " +report.finalNetwork.getScore());
        
        // print used parameters.
        ps.println();
        ps.println("***************");
        ps.println("Used Parameters");
        ps.println("***************");
        ps.println();

        Map<String,String> sortedUsedParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedUsedParameters.putAll(allUsedParameters);
        for(Map.Entry<String,String> entry : sortedUsedParameters.entrySet()){
            ps.println(entry.getKey()+ " " + entry.getValue());
        }

        // print initial network
        ps.println();
        ps.println("***************");
        ps.println("Initial Network");
        ps.println("***************");
        ps.println();

        Map<String,Integer> sortedFixedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedFixedDomains.putAll(report.initialNetwork.getFixedDomainIndices());
        ps.println("Fixed Domains:");
        ps.println("--------------");
        for(Map.Entry<String,Integer> entry : sortedFixedDomains.entrySet()){
            ps.println(report.initialNetwork.getFixedDomainNames()[entry.getValue()]+ " " + report.initialNetwork.getFixedDomainSequences()[entry.getValue()]);
        }

        Map<String,Integer> sortedVariableDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedVariableDomains.putAll(report.initialNetwork.getVariableDomainIndices());
        ps.println();
        ps.println("Variable Domains:");
        ps.println("-----------------");
        for(Map.Entry<String,Integer> entry : sortedVariableDomains.entrySet()){
            ps.println(report.initialNetwork.getVariableDomainNames()[entry.getValue()]+ " " + report.initialNetwork.getVariableDomainSequences()[entry.getValue()]);
        }

        Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedOligomers.putAll(report.initialNetwork.getOligomerIndices());
        ps.println();
        ps.println("Oligomer Domains:");
        ps.println("-------------------");
        for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
            ps.print(report.initialNetwork.getOligomerNames()[entry.getValue()]);
            for(String domain : report.initialNetwork.getOligomerDomains()[entry.getValue()]){
                ps.print(" "+ domain);
            }
            ps.println();
        }

        ps.println();
        ps.println("Oligomer Sequences:");
        ps.println("-------------------");
        for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
            ps.println(report.initialNetwork.getOligomerNames()[entry.getValue()]+ " " + report.initialNetwork.getOligomerSequences()[entry.getValue()]);
        }

        ps.println();
        ps.println("*************");
        ps.println("Final Network");
        ps.println("*************");
        ps.println();

        sortedFixedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedFixedDomains.putAll(report.finalNetwork.getFixedDomainIndices());
        ps.println("Fixed Domains:");
        ps.println("--------------");
        for(Map.Entry<String,Integer> entry : sortedFixedDomains.entrySet()){
            ps.println(report.finalNetwork.getFixedDomainNames()[entry.getValue()]+ " " + report.finalNetwork.getFixedDomainSequences()[entry.getValue()]);
        }

        sortedVariableDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedVariableDomains.putAll(report.finalNetwork.getVariableDomainIndices());
        ps.println();
        ps.println("Variable Domains:");
        ps.println("-----------------");
        for(Map.Entry<String,Integer> entry : sortedVariableDomains.entrySet()){
            ps.println(report.finalNetwork.getVariableDomainNames()[entry.getValue()]+ " " + report.finalNetwork.getVariableDomainSequences()[entry.getValue()]);
        }

        sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedOligomers.putAll(report.finalNetwork.getOligomerIndices());
        ps.println();
        ps.println("Oligomer Domains:");
        ps.println("-------------------");
        for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
            ps.print(report.finalNetwork.getOligomerNames()[entry.getValue()]);
            for(String domain : report.finalNetwork.getOligomerDomains()[entry.getValue()]){
                ps.print(" "+ domain);
            }
            ps.println();
        }

        ps.println();
        ps.println("Oligomer Sequences:");
        ps.println("-------------------");
        for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
            ps.println(report.finalNetwork.getOligomerNames()[entry.getValue()]+ " " + report.finalNetwork.getOligomerSequences()[entry.getValue()]);
        }
        
    }
    
    private static void printFinalVariableDomains (PrintStream ps, Report report){
        Map<String,Integer> sortedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedDomains.putAll(report.finalNetwork.getVariableDomainIndices());

        String[] vds = report.finalNetwork.getVariableDomainSequences();
        for(Map.Entry<String,Integer> entry: sortedDomains.entrySet()){
            ps.println(entry.getKey()+ " " + vds[entry.getValue()]);
        }
    }
    
    private static void printFinalOligomers (PrintStream ps, Report report){
        Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedOligomers.putAll(report.finalNetwork.getOligomerIndices());
        String[] os = report.finalNetwork.getOligomerSequences();
        for(Map.Entry<String,Integer> entry: sortedOligomers.entrySet()){
            ps.println(entry.getKey()+ " " + os[entry.getValue()]);
        }
    }
    
    private static void printScoreTrajectory (PrintStream ps, Report report){
        String[][] scores = report.lineageFittestScores;
        int[] lineageIndexes = IntStream.range(0,scores.length).toArray();
        ps.print("Generation Number");
            for (int j : lineageIndexes){
                ps.print(",Lineage "+j+" ("+report.scoreLabel+" - "+report.scoreUnits+")");
            }
            ps.println();
        for(int i : IntStream.range(0,scores[0].length).toArray()){
            ps.print(i+1);
            for (int j : lineageIndexes){
                ps.print(","+scores[j][i]);
            }
            ps.println();
        }
    }
    
    private static void printLogScoreTrajectory (PrintStream ps, Report report){
        String[][] scores = report.lineageFittestScores;
        int[] lineageIndexes = IntStream.range(0,scores.length).toArray();
        ps.print("Generation Number");
            for (int j : lineageIndexes){
                ps.print(",Lineage "+j+" ("+report.scoreLabel+" - "+report.scoreUnits+")");
            }
            ps.println();
        for(int i = 1; i < scores[0].length; i = i*2){
            ps.print(i);
            for (int j : lineageIndexes){
                ps.print(","+scores[j][i-1]);
            }
            ps.println();
        }
    }
    
    static private class MutationSupervisor {
        private final ExecutorService service;
        final FactoryDomainBasedEncodedNetwork factory;
        final IScorer scorer;
        final IValidator validator;
        
        MutationSupervisor(int numberThreads, FactoryDomainBasedEncodedNetwork factory, IScorer scorer, IValidator validator){
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
                    System.out.println("Exception during type 1 mutation.");
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
                    System.out.println("Exception during type 2 mutation.");
                    System.out.println(e.toString());
                    e.printStackTrace();
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
    
    static private class OptimizationSupervisor{
        final ExecutorService es;
        final MutationSupervisor mutationSupervisor;
        final IScorer scorer;
        final PrintStream out;
        
        final int cyclesPerUpdate;
        final int totalCycles;
        
        final int NL;
        final int CPL;
        final int NMPC;
        final int GPC;
        final int NDPG;
        
        OptimizationSupervisor ( MutationSupervisor mutationSupervisor, IScorer scorer, int NL, int CPL,int NMPC,int GPC,int NDPG, PrintStream streamForUpdates){
            this.es = Executors.newCachedThreadPool();
            this.mutationSupervisor=mutationSupervisor;
            this.scorer = scorer;
            this.out = streamForUpdates;
            this.NL = NL;
            this.CPL = CPL;
            this.NMPC = NMPC;
            this.GPC = GPC;
            this.NDPG = NDPG;
            this.totalCycles = CPL*NL;
            this.cyclesPerUpdate = Math.max(this.totalCycles/100,1);
        }
        
        public OptimizerReport optimize(IDomainBasedEncodedScoredNetwork initialNetwork){
            final AtomicInteger completedCycles = new AtomicInteger(0);
            double startTime = System.currentTimeMillis();
            final String[][] fittestScores = new String[NL][];
            Cycle2Request[] subCycleRequests = new Cycle2Request[NL];
            subCycleRequests[0] = new Cycle2Request(initialNetwork,completedCycles,startTime);
            Future<Type2CycleReport>[] futures = new Future[NL];
            futures[0] = es.submit(subCycleRequests[0]);
            
            //generate mutated networks.
            IDomainBasedEncodedScoredNetwork[] newLineageMothers = mutationSupervisor.getType1Mutation(initialNetwork,NL-1);
            
            for(int i = 1; i < NL; i++){
                subCycleRequests[i] = new Cycle2Request(newLineageMothers[i-1],completedCycles,startTime);
                futures[i] = es.submit(subCycleRequests[i]);
            }
            
            Type2CycleReport[] reports = new Type2CycleReport[NL];
            IDomainBasedEncodedScoredNetwork[] fittestLineageMothers = new IDomainBasedEncodedScoredNetwork[NL];
            
            try{
                for(int i = 0; i < NL; i++){
                    reports[i] = futures[i].get();
                    fittestLineageMothers[i] = reports[i].fittest;
                }
            } catch(Exception e){System.out.print(e.getMessage());}
            
            IDomainBasedEncodedScoredNetwork fittest = fittestLineageMothers[0];
            for(int i = 1; i < NL; i++){
                if(scorer.compareFitness(fittestLineageMothers[i],fittest) >= 0){
                    fittest = fittestLineageMothers[i];
                }
            }
            
            ArrayList<String>[] s = new ArrayList[NL];
            s[0] = new ArrayList<String>(Arrays.asList(reports[0].fittestScores));
            s[0].add(0,initialNetwork.getScore());
            fittestScores[0] = s[0].toArray(new String[0]);
            for(int i = 1; i < NL; i++){
                s[i] = new ArrayList<String>(Arrays.asList(reports[i].fittestScores));
                s[i].add(0,newLineageMothers[i-1].getScore());
                fittestScores[i] = s[i].toArray(new String[0]);
            }
            
            es.shutdownNow();
            
            OptimizerReport ret = new OptimizerReport(fittest,fittestScores,fittestLineageMothers);
            return ret;
        }
        
        static public class OptimizerReport{
            IDomainBasedEncodedScoredNetwork fittest;
            IDomainBasedEncodedScoredNetwork[] fittestLineageMothers;
            String[][] lineageFittestScores;
            OptimizerReport(IDomainBasedEncodedScoredNetwork fittest, String[][] lineageFittestScores, IDomainBasedEncodedScoredNetwork[] fittestLineageMothers){
                this.fittest = fittest;
                this.lineageFittestScores = lineageFittestScores;
                this.fittestLineageMothers = fittestLineageMothers;
            }
        }
        private class Cycle2Request implements Callable<Type2CycleReport>{
            final IDomainBasedEncodedScoredNetwork initialNetwork;
            final double startTime;
            final AtomicInteger completedCycles;

            Cycle2Request ( IDomainBasedEncodedScoredNetwork initialNetwork, AtomicInteger completedCycles, double startTime){
                this.initialNetwork = initialNetwork;
                this.startTime = startTime;
                this.completedCycles = completedCycles;
            }

            @Override
            public Type2CycleReport call(){
                ArrayList<String> fittestScores = new ArrayList<>();
                int cycleIndex=0;
                IDomainBasedEncodedScoredNetwork currentFittest = initialNetwork;
                IDomainBasedEncodedScoredNetwork[] subCycleMothers = new IDomainBasedEncodedScoredNetwork[NMPC+1];
                Type3CycleRequest[] subCycleRequests = new Type3CycleRequest[NMPC+1];
                subCycleRequests[0] = new Type3CycleRequest(initialNetwork);
                for(int i = 0; i < NMPC; i++){
                    subCycleRequests[i+1] = new Type3CycleRequest(initialNetwork);
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
                        subCycleRequests[i].updateState(subCycleMothers[i]);
                        futures[i] = es.submit(subCycleRequests[i]);
                    }

                    try{
                        for(int i=0; i < NMPC+1; i++){
                            subCycleFittest[i] = futures[i].get().fittest;
                        }
                    } catch (Exception e){System.out.println(e.getMessage());}

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
                    
                    int finishedCycles = completedCycles.incrementAndGet();
                    
                    if (out != null && finishedCycles%cyclesPerUpdate==0){
                        double elapsedTime = System.currentTimeMillis()-startTime;
                        double fractionComplete = (((double)finishedCycles)/((double)totalCycles));
                        int percentComplete = (int) (fractionComplete*100);
                        double remainingTime = ((elapsedTime/fractionComplete)*(1-fractionComplete));
                        int h = (int)((remainingTime/1000)/(60*60));
                        int m = (int)(((remainingTime/1000)/60)%60);
                        int s = (int)((remainingTime/1000)%60);
                        out.println(percentComplete + "% completed; " + "Estimated time remaining: "+ h + " h " + m + " m " + s + " s ");
                    }
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
        
        private class Type3CycleRequest implements Callable{
            IDomainBasedEncodedScoredNetwork initialNetwork;

            Type3CycleRequest ( IDomainBasedEncodedScoredNetwork initialNetwork){
                this.initialNetwork = initialNetwork;
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
    
    private interface Parameter{
        public String getDefault();
        public String getDescription();
        public String getLabel();
        boolean isValid(String value);
    }
    
    private static class IntegerParameter implements Parameter{
        String description;
        String defaultValue;
        String label;
        int minValue;
        int maxValue;
        
        IntegerParameter ( String defaultValue, String description, String label, int minValue, int maxValue){
            this.label = label;
            this.description = description;
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
        
        @Override
        public String getDefault(){
            return defaultValue;
        }
        
        @Override
        public String getDescription(){
            return description;
        }
        
        @Override
        public String getLabel(){
            return label;
        }
        
        @Override
        public boolean isValid(String value){
            int v = Integer.parseInt(value);
            if (v < minValue) return false;
            if (v > maxValue) return false;
            return true;
        }
    }
    
    private static class StringParameter implements Parameter{
        String[] validValues;
        String defaultValue;
        String description;
        String label;
        
        StringParameter (String defaultValue, String description, String label, String[] validValues){
            this.label = label;
            this.description = description;
            this.defaultValue = defaultValue;
            this.validValues = validValues;
        }
        
        @Override
        public String getDefault(){
            return defaultValue;
        }
        
        @Override
        public String getDescription(){
            return description;
        }
        
        @Override
        public String getLabel(){
            return label;
        }
        
        @Override
        public boolean isValid(String value){
            for(String s : validValues){
                if (value.equals(s)) return true;
            }
            return false;
        }
    }
    
    private static class InputFileParameter implements Parameter{
        String defaultFilePath;
        String description;
        String label;
        
        InputFileParameter(String label, String description, String defaultFilePath){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
        }
        
        @Override
        public String getDefault() {
            return defaultFilePath;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        @Override
        public boolean isValid(String value) {
            return value.endsWith(".txt");
        }
    }
    
    private static class OutputDirectoryParameter implements Parameter{
        String defaultFilePath;
        String description;
        String label;
        
        OutputDirectoryParameter(String defaultFilePath, String description, String label){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
        }
        
        @Override
        public String getDefault() {
            return defaultFilePath;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        @Override
        public boolean isValid(String value) {
            return true;
        }
    }
    
    private static interface OutputFileParameter extends Parameter{
        void printFile(PrintStream PS, Report report);
    }
    
    private static class ReportFileParameter implements OutputFileParameter{
        String defaultFilePath;
        String description;
        String label;
        Map<String,String> otherUsedParameters;
        
        ReportFileParameter(String defaultFilePath, String description, String label, Map<String,String> otherUsedParameters){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
            this.otherUsedParameters = otherUsedParameters;
        }
        
        @Override
        public String getDefault() {
            return defaultFilePath;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        @Override
        public boolean isValid(String value) {
            if (value.equalsIgnoreCase("false")) return true;
            return value.endsWith(".txt");
        }
        
        @Override
        public void printFile(PrintStream PS, Report report){
            printReport (PS, report, otherUsedParameters);
        }
    }
    
    private static class VariableDomainsFileParameter implements OutputFileParameter{
        String defaultFilePath;
        String description;
        String label;
        
        VariableDomainsFileParameter(String defaultFilePath, String description, String label){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
        }
        
        @Override
        public String getDefault() {
            return defaultFilePath;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        @Override
        public boolean isValid(String value) {
            if (value.equalsIgnoreCase("false")) return true;
            return value.endsWith(".txt");
        }
        
        @Override
        public void printFile(PrintStream PS, Report report){
            printFinalVariableDomains(PS, report);
        }
    }
    
    private static class FinalOligomersFileParameter implements OutputFileParameter{
        String defaultFilePath;
        String description;
        String label;
        
        FinalOligomersFileParameter(String defaultFilePath, String description, String label){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
        }
        
        @Override
        public String getDefault() {
            return defaultFilePath;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        @Override
        public boolean isValid(String value) {
            if (value.equalsIgnoreCase("false")) return true;
            return value.endsWith(".txt");
        }
        
        @Override
        public void printFile(PrintStream PS, Report report){
            printFinalOligomers(PS, report);
        }
    }
    
    private static class ScoresFileParameter implements OutputFileParameter{
        String defaultFilePath;
        String description;
        String label;
        
        ScoresFileParameter(String defaultFilePath, String description, String label){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
        }
        
        @Override
        public String getDefault() {
            return defaultFilePath;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        @Override
        public boolean isValid(String value) {
            if (value.equalsIgnoreCase("false")) return true;
            return value.endsWith(".txt");
        }
        
        @Override
        public void printFile(PrintStream PS, Report report){
            printScoreTrajectory(PS, report);
        }
    }
    
    private static class LogScoresFileParameter implements OutputFileParameter{
        String defaultFilePath;
        String description;
        String label;
        
        LogScoresFileParameter(String defaultFilePath, String description, String label){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
        }
        
        @Override
        public String getDefault() {
            return defaultFilePath;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        @Override
        public boolean isValid(String value) {
            if (value.equalsIgnoreCase("false")) return true;
            return value.endsWith(".txt");
        }
        
        @Override
        public void printFile(PrintStream PS, Report report){
            printLogScoreTrajectory(PS, report);
        }
    }
    
}
