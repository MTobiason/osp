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
import java.util.HashMap;
import edu.boisestate.osp.util;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeMap;

/**
 *
 * @author mtobi
 */
public class DevPro {
    final static String VERSION = "2.0";
    final int MAX_THREADS;
    final int MAX_THREADS_PER_NETWORK;
    
    // parameters stuff
    final static String PFP_LABEL = "PFP"; //parameters File Path
    final static String PFP_DEFAULT = "dp_in_parameters.txt";
    
    // input files
    final static String FDFP_LABEL = "FDFP"; // fixed-domains-file-path
    final static String FDFP_DEFAULT = "dp_in_domains_fixed.txt";
    final static String VDFP_LABEL = "VDFP"; // variable-domains-file-path
    final static String VDFP_DEFAULT = "dp_in_domains_variable.txt";
    final static String ODFP_LABEL = "ODFP"; // oligomers-file-path
    final static String ODFP_DEFAULT = "dp_in_oligomer_domains.txt";
    
    // output files
    final static String ORFP_LABEL = "ORFP"; // Output Report File Path
    final static String ORFP_DEFAULT = "dp_out_report.txt";
    final static String OOSFP_LABEL = "OOFP"; // Output Oligomer Sequences File Path
    final static String OOSFP_DEFAULT = "dp_out_oligomers.txt"; //
    final static String ORPFP_LABEL = "OPFP"; // Output Requested Properties File Path
    final static String ORPFP_DEFAULT = "dp_out_properties.txt";
    
    final static String BASELINE_INTER_DUPLEX_COUNT_DEFAULT = "true";
    final static String BASELINE_INTRA_DUPLEX_COUNT_DEFAULT = "true";
    final static String BASELINE_INTER_DUPLEX_COUNT_LABEL = "baselineUniqueInterDuplexCount";
    final static String BASELINE_INTRA_DUPLEX_COUNT_LABEL = "baselineUniqueIntraDuplexCount";
    final static String BASELINE_N_DEFAULT = "true";
    final static String BASELINE_N_LABEL = "baselineN";
    final static String BASELINE_O_DEFAULT = "true";
    final static String BASELINE_O_LABEL = "baselineO";
    final static String BASELINE_W_DEFAULT = "true";
    final static String BASELINE_W_LABEL = "baselineW";
    final static String DELTA_N_DEFAULT = "true";
    final static String DELTA_N_LABEL = "deltaN";
    final static String DELTA_O_DEFAULT = "true";
    final static String DELTA_O_LABEL = "deltaO";
    final static String DELTA_W_DEFAULT = "true";
    final static String DELTA_W_LABEL = "deltaW";
    final static String N_DEFAULT = "true";
    final static String N_LABEL = "N";
    final static String O_DEFAULT = "true";
    final static String O_LABEL = "O";    
    final static String W_DEFAULT = "true";
    final static String W_LABEL = "W";
    final static String INTER_DUPLEX_COUNT_DEFAULT = "true";
    final static String INTER_DUPLEX_COUNT_LABEL = "interDuplexUniqueCount";
    final static String INTRA_DUPLEX_COUNT_DEFAULT = "true";
    final static String INTRA_DUPLEX_COUNT_LABEL = "intraDuplexUniqueCount";

    final static String INTER_SB_LABEL = "interSB";
    final static String INTER_SB_DEFAULT = "10";
    final static String INTER_SLC_LABEL = "interSLC";
    final static String INTER_SLC_DEFAULT = "1";
    final static String INTRA_SB_LABEL = "intraSB";
    final static String INTRA_SB_DEFAULT = "10";
    final static String INTRA_SLC_LABEL = "intraSLC";
    final static String INTRA_SLC_DEFAULT = "1";
    final static String SWX_LABEL = "scoringWeightX";
    final static String SWX_DEFAULT = "10000";
    
    final Analyzer analyzer;
    
    public DevPro(int maxThreads, int maxThreadsPerNetwork){
        this.MAX_THREADS = maxThreads;
        this.MAX_THREADS_PER_NETWORK = maxThreadsPerNetwork;
        this.analyzer = new Analyzer(this.MAX_THREADS, this.MAX_THREADS_PER_NETWORK);
    }
    
    public static class Request{
        IDomainBasedEncodedNetwork network;
        Map<String,String> parameters;
        Collection<String> requestedProperties;
        
        Request(IDomainBasedEncodedNetwork network, Collection<String> requestedProperties, Map<String,String> parameters){
            this.requestedProperties = requestedProperties;
            this.network = network;
            this.parameters = parameters;
        }
    }
    
    public Report analyze(Request r){
        final String startDate = new Date().toString();
        double startTime = System.currentTimeMillis(); // start timer for optimization runtime.
        
        Map<String,String> usedParameters = new HashMap<>();
        Collection<String> propertiesToRequest = new HashSet<>();
        Map<String,String> parametersToProvide = new HashMap<>();
        
        // for each requested property
        for(String property : r.requestedProperties){
            switch (property){
                case BASELINE_INTER_DUPLEX_COUNT_LABEL:
                    propertiesToRequest.add(analyzer.BASELINE_INTER_DUPLEX_COUNT_LABEL);
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    break;
                case BASELINE_INTRA_DUPLEX_COUNT_LABEL:
                    propertiesToRequest.add(analyzer.BASELINE_INTRA_DUPLEX_COUNT_LABEL);
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    break;
                case BASELINE_N_LABEL:
                    propertiesToRequest.add(analyzer.BASELINE_N_LABEL);
                    usedParameters.put(INTER_SB_LABEL,r.parameters.getOrDefault(INTER_SB_LABEL, INTER_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SB_LABEL, usedParameters.get(INTER_SB_LABEL));
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    break;
                case BASELINE_O_LABEL:
                    propertiesToRequest.add(analyzer.BASELINE_O_LABEL);
                    usedParameters.put(INTRA_SB_LABEL,r.parameters.getOrDefault(INTRA_SB_LABEL, INTRA_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SB_LABEL, usedParameters.get(INTRA_SB_LABEL));
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    break;
                case BASELINE_W_LABEL:
                    propertiesToRequest.add(analyzer.BASELINE_W_LABEL);
                    usedParameters.put(INTER_SB_LABEL,r.parameters.getOrDefault(INTER_SB_LABEL, INTER_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SB_LABEL, usedParameters.get(INTER_SB_LABEL));
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    usedParameters.put(INTRA_SB_LABEL,r.parameters.getOrDefault(INTRA_SB_LABEL, INTRA_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SB_LABEL, usedParameters.get(INTRA_SB_LABEL));
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    usedParameters.put(SWX_LABEL,r.parameters.getOrDefault(SWX_LABEL, SWX_DEFAULT));
                    parametersToProvide.put(analyzer.SWX_LABEL, usedParameters.get(SWX_LABEL));
                    break;
                case DELTA_N_LABEL:
                    propertiesToRequest.add(analyzer.DELTA_N_LABEL);
                    usedParameters.put(INTER_SB_LABEL,r.parameters.getOrDefault(INTER_SB_LABEL, INTER_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SB_LABEL, usedParameters.get(INTER_SB_LABEL));
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    break;
                case DELTA_O_LABEL:
                    propertiesToRequest.add(analyzer.DELTA_O_LABEL);
                    usedParameters.put(INTRA_SB_LABEL,r.parameters.getOrDefault(INTRA_SB_LABEL, INTRA_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SB_LABEL, usedParameters.get(INTRA_SB_LABEL));
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    break;
                case DELTA_W_LABEL:
                    propertiesToRequest.add(analyzer.DELTA_W_LABEL);
                    usedParameters.put(INTER_SB_LABEL,r.parameters.getOrDefault(INTER_SB_LABEL, INTER_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SB_LABEL, usedParameters.get(INTER_SB_LABEL));
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    usedParameters.put(INTRA_SB_LABEL,r.parameters.getOrDefault(INTRA_SB_LABEL, INTRA_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SB_LABEL, usedParameters.get(INTRA_SB_LABEL));
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    usedParameters.put(SWX_LABEL,r.parameters.getOrDefault(SWX_LABEL, SWX_DEFAULT));
                    parametersToProvide.put(analyzer.SWX_LABEL, usedParameters.get(SWX_LABEL));
                    break;
                case N_LABEL:
                    propertiesToRequest.add(analyzer.N_LABEL);
                    usedParameters.put(INTER_SB_LABEL,r.parameters.getOrDefault(INTER_SB_LABEL, INTER_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SB_LABEL, usedParameters.get(INTER_SB_LABEL));
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    break;
                case O_LABEL:
                    propertiesToRequest.add(analyzer.O_LABEL);
                    usedParameters.put(INTRA_SB_LABEL,r.parameters.getOrDefault(INTRA_SB_LABEL, INTRA_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SB_LABEL, usedParameters.get(INTRA_SB_LABEL));
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    break;
                case W_LABEL:
                    propertiesToRequest.add(analyzer.W_LABEL);
                    usedParameters.put(INTER_SB_LABEL,r.parameters.getOrDefault(INTER_SB_LABEL, INTER_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SB_LABEL, usedParameters.get(INTER_SB_LABEL));
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    usedParameters.put(INTRA_SB_LABEL,r.parameters.getOrDefault(INTRA_SB_LABEL, INTRA_SB_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SB_LABEL, usedParameters.get(INTRA_SB_LABEL));
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    usedParameters.put(SWX_LABEL,r.parameters.getOrDefault(SWX_LABEL, SWX_DEFAULT));
                    parametersToProvide.put(analyzer.SWX_LABEL, usedParameters.get(SWX_LABEL));
                    break;
                case INTER_DUPLEX_COUNT_LABEL:
                    propertiesToRequest.add(analyzer.INTER_DUPLEX_COUNT_LABEL);
                    usedParameters.put(INTER_SLC_LABEL,r.parameters.getOrDefault(INTER_SLC_LABEL, INTER_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTER_SLC_LABEL, usedParameters.get(INTER_SLC_LABEL));
                    break;
                case INTRA_DUPLEX_COUNT_LABEL:
                    propertiesToRequest.add(analyzer.INTRA_DUPLEX_COUNT_LABEL);
                    usedParameters.put(INTRA_SLC_LABEL,r.parameters.getOrDefault(INTRA_SLC_LABEL, INTRA_SLC_DEFAULT));
                    parametersToProvide.put(analyzer.INTRA_SLC_LABEL, usedParameters.get(INTRA_SLC_LABEL));
                    break;
                default:
                    System.out.println("Property "+property+" not supported.");
            }
        }
        
        Analyzer.Request ar1 = new Analyzer.Request(r.network, propertiesToRequest, parametersToProvide);
        Analyzer.Report ar2 = analyzer.analyze(ar1);
        
        Map<String,String> necessaryPropertyValues = ar2.getNecessaryPropertyValues();
        Map<String,String> requestedPropertyValues = ar2.getRequestedPropertyValues();
        Map<String,String> analyzerUsedParameters = ar2.getUsedParameters();
        
        double endTime = System.currentTimeMillis(); // record evolutionary cycle endtime
        String totalTimeSeconds = String.valueOf((endTime-startTime)/1000);
        
        Report ret = new Report(usedParameters,r.network,requestedPropertyValues, necessaryPropertyValues, startDate, totalTimeSeconds);
        return ret;
    }
    
    public static class Report {
        final Map<String,String> usedParameters;
        final Map<String,String> requestedProperties;
        final Map<String,String> necessaryProperties;
        final IDomainBasedEncodedNetwork network;
        final String startDate;
        final String totalTimeSeconds;
        final String version = DevPro.VERSION;
        
        Report(Map<String,String> usedParameters, IDomainBasedEncodedNetwork network, Map<String,String> requestedPropertyValues, Map<String,String> necessaryPropertyValues, String startDate, String totalTimeSeconds){
            this.usedParameters = usedParameters;
            this.requestedProperties = requestedPropertyValues;
            this.necessaryProperties = necessaryPropertyValues;
            this.network = network;
            this.startDate = startDate;
            this.totalTimeSeconds = totalTimeSeconds;
        }
    }
    
    private void exportToFile(Report r, Map<String,String> otherUsedParameters, String ORFP, String OOSFP, String ORPFP){
        Map<String,String> allUsedParameters = new HashMap<>(otherUsedParameters);
        allUsedParameters.putAll(r.usedParameters);
        IDomainBasedEncodedNetwork network = r.network;

        try{
            // Export Report file
            if (!ORFP.equalsIgnoreCase("False")){
                FileWriter FW = new FileWriter( ORFP );
                PrintWriter PW = new PrintWriter( FW );

                PW.println("Report generated by DevPro.");
                PW.println("Program version: "+r.version);
                PW.println("Start date: "+r.startDate);
                PW.print("Elapsed time during analysis: ");
                        
                int H = (int)(Double.parseDouble(r.totalTimeSeconds) / (60 *60)); // Hours
                int M = (int)((Double.parseDouble(r.totalTimeSeconds) / 60) % 60 ); // Minutes
                int S = (int)(Double.parseDouble(r.totalTimeSeconds) % 60 );   // Seconds
                PW.println(H + " h " + M + " m " + S + " s ");

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

                PW.println();
                PW.println("********************");
                PW.println("Requested Properties");
                PW.println("********************");
                PW.println();
                
                Map<String,String> sortedRequestedProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                sortedRequestedProperties.putAll(r.requestedProperties);
                for(Map.Entry<String,String> entry : sortedRequestedProperties.entrySet()){
                    switch (entry.getKey()){
                        case Analyzer.BASELINE_INTER_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(BASELINE_INTER_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        case Analyzer.BASELINE_INTRA_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(BASELINE_INTRA_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        case Analyzer.INTER_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(INTER_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        case Analyzer.INTRA_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(INTRA_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        default:
                            PW.println(entry.getKey()+" "+ entry.getValue());
                    }
                }
                
                PW.println();
                PW.println("********************");
                PW.println("Necessary Properties");
                PW.println("********************");
                PW.println();
                
                Map<String,String> sortedNecessaryProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                sortedNecessaryProperties.putAll(r.necessaryProperties);
                for(Map.Entry<String,String> entry : sortedNecessaryProperties.entrySet()){
                    switch (entry.getKey()){
                        case Analyzer.BASELINE_INTER_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(BASELINE_INTER_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        case Analyzer.BASELINE_INTRA_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(BASELINE_INTRA_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        case Analyzer.INTER_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(INTER_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        case Analyzer.INTRA_DUPLEX_COUNT_LABEL:{
                                PW.println("-----------------");
                                PW.println(INTRA_DUPLEX_COUNT_LABEL+":");
                                PW.println("(Length) (Counts)");
                                String[] splitStrings = entry.getValue().split(System.lineSeparator());
                                Map<Integer,Integer> sortedValues = new TreeMap<>();
                                for(String value: splitStrings){
                                    String[] splitValues = value.split(" ");
                                    sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
                                }
                                for(Map.Entry<Integer,Integer> entry2 :sortedValues.entrySet()){
                                    PW.println(entry2.getKey()+" "+entry2.getValue());
                                }
                                PW.println("-----------------");
                            }
                            break;
                            
                        default:
                            PW.println(entry.getKey()+" "+ entry.getValue());
                    }
                }

                // print network information
                PW.println();
                PW.println("*******************");
                PW.println("Network Information");
                PW.println("*******************");
                PW.println();

                Map<String,Integer> sortedFixedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                sortedFixedDomains.putAll(r.network.getFixedDomainIndices());
                PW.println("Fixed Domains:");
                PW.println("--------------");
                for(Map.Entry<String,Integer> entry : sortedFixedDomains.entrySet()){
                    PW.println(r.network.getFixedDomainNames()[entry.getValue()]+ " " + r.network.getFixedDomainSequences()[entry.getValue()]);
                }

                Map<String,Integer> sortedVariableDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                sortedVariableDomains.putAll(r.network.getVariableDomainIndices());
                PW.println();
                PW.println("Variable Domains:");
                PW.println("-----------------");
                for(Map.Entry<String,Integer> entry : sortedVariableDomains.entrySet()){
                    PW.println(r.network.getVariableDomainNames()[entry.getValue()]+ " " + r.network.getVariableDomainSequences()[entry.getValue()]);
                }

                Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                sortedOligomers.putAll(r.network.getOligomerIndices());
                PW.println();
                PW.println("Oligomer Domains:");
                PW.println("-------------------");
                for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
                    PW.print(r.network.getOligomerNames()[entry.getValue()]);
                    for(String domain : r.network.getOligomerDomains()[entry.getValue()]){
                        PW.print(" "+ domain);
                    }
                    PW.println();
                }

                PW.println();
                PW.println("Oligomer Sequences:");
                PW.println("-------------------");
                for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
                    PW.println(r.network.getOligomerNames()[entry.getValue()]+ " " + r.network.getOligomerSequences()[entry.getValue()]);
                }
                
                PW.close();
            }

            // Export oligomer sequences from final network
            if (!OOSFP.equalsIgnoreCase("False")){
                FileWriter FW = new FileWriter( OOSFP );
                PrintWriter PW = new PrintWriter( FW);

                Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                sortedOligomers.putAll(r.network.getOligomerIndices());
                String[] os = network.getOligomerSequences();
                for(Map.Entry<String,Integer> entry: sortedOligomers.entrySet()){
                    PW.println(entry.getKey()+ " " + os[entry.getValue()]);
                }
                PW.close();
            }

            // Export properties.
            if (!ORPFP.equalsIgnoreCase("False")){
                FileWriter FW = new FileWriter( ORPFP );
                PrintWriter PW = new PrintWriter( FW);

                Map<String,String> sortedProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                sortedProperties.putAll(r.requestedProperties);
                
                for(Map.Entry<String,String> entry : sortedProperties.entrySet()){
                    PW.println(entry.getKey()+ " "+entry.getValue());
                }
                PW.close();
            }

        } catch (Exception e) {
            System.out.println("Error while exporting network.");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    public static void main(String[] args){
        Map<String,String> usedParameters = new HashMap<>();
        String PFP = PFP_DEFAULT;
        if (args.length > 0)
        {
            if (args[0].equals("-h") || args[0].equals("--help")) // Print explanation of acceptable arguments.
            {
                    System.out.println("Usage: DevPro <Parameter File Path>");
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
        final Map<String,String> variableDomains = util.importPairFromTxt(VDFP);

        // Read oligomer domains file.
        final String OFP = parameters.getOrDefault(ODFP_LABEL,ODFP_DEFAULT);
        usedParameters.put(ODFP_LABEL, OFP);
        final Map<String,String[]> oligomerDomains = util.importListFromTxt(OFP);
        
        // output stuff
        final String ORFP = parameters.getOrDefault(ORFP_LABEL, ORFP_DEFAULT);
        usedParameters.put(ORFP_LABEL,ORFP);
        final String OOSFP = parameters.getOrDefault(OOSFP_LABEL, OOSFP_DEFAULT);
        usedParameters.put(OOSFP_LABEL,OOSFP);
        final String ORPFP = parameters.getOrDefault(ORPFP_LABEL, ORPFP_DEFAULT);
        usedParameters.put(ORPFP_LABEL,ORPFP);
        
        // make network object
        final ICoder coder = new Coder();
        final FactoryDomainBasedEncodedNetwork factory = new FactoryDomainBasedEncodedNetwork(coder, fixedDomains, oligomerDomains, variableDomains);
        final IDomainBasedEncodedNetwork network = factory.getNewNetwork(variableDomains);
        
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        DevPro dp = new DevPro(availableProcessors,availableProcessors);
        
        // add properties to request.
        Collection<String> propertiesToRequest = new HashSet<>();
        
        if(Boolean.parseBoolean(parameters.getOrDefault(BASELINE_INTER_DUPLEX_COUNT_LABEL,BASELINE_INTER_DUPLEX_COUNT_DEFAULT))){
            propertiesToRequest.add(BASELINE_INTER_DUPLEX_COUNT_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(BASELINE_INTRA_DUPLEX_COUNT_LABEL,BASELINE_INTRA_DUPLEX_COUNT_DEFAULT))){
            propertiesToRequest.add(BASELINE_INTRA_DUPLEX_COUNT_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(BASELINE_N_LABEL,BASELINE_N_DEFAULT))){
            propertiesToRequest.add(BASELINE_N_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(BASELINE_O_LABEL,BASELINE_O_DEFAULT))){
            propertiesToRequest.add(BASELINE_O_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(BASELINE_W_LABEL,BASELINE_W_DEFAULT))){
            propertiesToRequest.add(BASELINE_W_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(DELTA_N_LABEL,DELTA_N_DEFAULT))){
            propertiesToRequest.add(DELTA_N_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(DELTA_O_LABEL,DELTA_O_DEFAULT))){
            propertiesToRequest.add(DELTA_O_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(DELTA_W_LABEL,DELTA_W_DEFAULT))){
            propertiesToRequest.add(DELTA_W_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(N_LABEL,N_DEFAULT))){
            propertiesToRequest.add(N_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(O_LABEL,O_DEFAULT))){
            propertiesToRequest.add(O_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(W_LABEL,W_DEFAULT))){
            propertiesToRequest.add(W_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(INTER_DUPLEX_COUNT_LABEL,INTER_DUPLEX_COUNT_DEFAULT))){
            propertiesToRequest.add(INTER_DUPLEX_COUNT_LABEL);
        }
        if(Boolean.parseBoolean(parameters.getOrDefault(INTRA_DUPLEX_COUNT_LABEL,INTRA_DUPLEX_COUNT_DEFAULT))){
            propertiesToRequest.add(INTRA_DUPLEX_COUNT_LABEL);
        }
        
        Request request = new Request(network, propertiesToRequest, parameters);
        Report report = dp.analyze(request);
        
        dp.exportToFile(report,usedParameters,ORFP,OOSFP,ORPFP);
        System.exit(0);
    }
}