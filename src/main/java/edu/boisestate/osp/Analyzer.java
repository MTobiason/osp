/*
 * The MIT License
 *
 * Copyright 2023 mtobi.
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

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 *
 * @author mtobi
 */
public class Analyzer{
    
    // parameter labels
    public final static String INTER_SB_LABEL = "interSB";
    public final static String INTER_SLC_LABEL = "interSLC";
    public final static String INTRA_SB_LABEL = "intraSB";
    public final static String INTRA_SLC_LABEL = "intraSLC";
    public final static String SWX_LABEL = "scoringWeightX";
    
    // property labels
    public final static String BASELINE_N_LABEL = "baselineN";
    public final static String BASELINE_O_LABEL = "baselineO";
    public final static String BASELINE_W_LABEL = "baselineW";
    public final static String DELTA_N_LABEL = "deltaN";
    public final static String DELTA_O_LABEL = "deltaO";
    public final static String DELTA_W_LABEL = "deltaW";
    public final static String N_LABEL = "N";
    public final static String O_LABEL = "O";
    public final static String W_LABEL = "W";

    final static String PACA_LABEL = "Profile_All_Complete_Intra"; // Profile all complete intra
    final static String PACE_LABEL = "Profile_All_Complete_Inter"; // Profile all complete inter
    final static String PAUA_LABEL = "Profile_All_Unique_Intra"; // Profile all Unique intra
    final static String PAUE_LABEL = "Profile_All_Unique_Inter"; // Profile all Unique inter

    final static String PBCA_LABEL = "Profile_Baseline_Complete_Intra"; // Profile baseline complete intra
    final static String PBCE_LABEL = "Profile_Baseline_Complete_Inter"; // Profile baseline complete inter
    final static String PBUA_LABEL = "Profile_Baseline_Unique_Intra"; // Profile baseline Unique intra
    final static String PBUE_LABEL = "Profile_Baseline_Unique_Inter"; // Profile baseline Unique inter

    final static String PDCA_LABEL = "Profile_Delta_Complete_Intra"; // Profile baseline complete intra
    final static String PDCE_LABEL = "Profile_Delta_Complete_Inter"; // Profile baseline complete inter
    final static String PDUA_LABEL = "Profile_Delta_Unique_Intra"; // Profile baseline Unique intra
    final static String PDUE_LABEL = "Profile_Delta_Unique_Inter"; // Profile baseline Unique inter
    
    final static String DAUA_LABEL = "Duplexes_All_Unique_Intra"; // List of all unique intra-oligomer duplexes.
    final static String DAUE_LABEL = "Duplexes_All_Unique_Inter"; // List of all unique intra-oligomer duplexes.
    final static String DBUA_LABEL = "Duplexes_Baseline_Unique_Intra"; // List of baseline unique intra-oligomer duplexes.
    final static String DBUE_LABEL = "Duplexes_Baseline_Unique_Inter"; // List of baseline unique intra-oligomer duplexes.
    
    
    final static Map<String,IntegerParameter> availableParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); {
        availableParameters.put(INTER_SB_LABEL, new IntegerParameter("Inter-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex.", INTER_SB_LABEL,0,Integer.MAX_VALUE));
        availableParameters.put(INTER_SLC_LABEL, new IntegerParameter("Inter-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores.", INTER_SLC_LABEL,1,Integer.MAX_VALUE));
        availableParameters.put(INTRA_SB_LABEL, new IntegerParameter("Intra-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex.", INTRA_SB_LABEL,0,Integer.MAX_VALUE));
        availableParameters.put(INTRA_SLC_LABEL, new IntegerParameter("Intra-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores.", INTRA_SLC_LABEL,1,Integer.MAX_VALUE));
        availableParameters.put(SWX_LABEL, new IntegerParameter("W will be calculated as O times this value plus N.", SWX_LABEL,0,Integer.MAX_VALUE));
    }
    
    final static Map<String,Property> availableProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);{
        availableProperties.put(BASELINE_N_LABEL, new Property("Network Fitness Score resulting from necessary duplexes.", BASELINE_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL,PBUE_LABEL}));
        availableProperties.put(BASELINE_O_LABEL, new Property("Oligomer Fitness Score resulting from necessary duplexes.", BASELINE_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL,PBUA_LABEL}));
        availableProperties.put(BASELINE_W_LABEL, new Property("Weighted Fitness Score resulting from necessary duplexes..", BASELINE_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL,PBUA_LABEL,PBUE_LABEL}));
        
        availableProperties.put(DELTA_N_LABEL, new Property("Network Fitness Score resulting from unnecessary duplexes.", DELTA_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL, DELTA_N_LABEL, N_LABEL}));
        availableProperties.put(DELTA_O_LABEL, new Property("Oligomer Fitness Score resulting from unnecessary duplexes.", DELTA_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL,DELTA_O_LABEL, O_LABEL}));
        availableProperties.put(DELTA_W_LABEL, new Property("Weighted Fitness Score resulting from unnecessary duplexes.", DELTA_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL,DELTA_N_LABEL,DELTA_O_LABEL,DELTA_W_LABEL,N_LABEL,O_LABEL,W_LABEL,PACE_LABEL,PACA_LABEL,PBUA_LABEL,PBUE_LABEL}));
        
        availableProperties.put(N_LABEL, new Property("Network Fitness Score.", N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{N_LABEL,PAUE_LABEL}));
        availableProperties.put(O_LABEL, new Property("Oligomer Fitness Score.", O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{O_LABEL,PAUA_LABEL}));
        availableProperties.put(W_LABEL, new Property("Weighted Fitness Score.", W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{N_LABEL,O_LABEL,W_LABEL,PACE_LABEL,PACA_LABEL}));
        
        availableProperties.put(PACA_LABEL, new Property("Profile of the length-counts for all intra-oligomer duplexes.", PACA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PACA_LABEL,PAUA_LABEL}));
        availableProperties.put(PACE_LABEL, new Property("Profile of the length-counts for all inter-oligomer duplexes.", PACE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PACE_LABEL,PAUE_LABEL}));
        availableProperties.put(PAUA_LABEL, new Property("Profile of the length-counts for the most prominent intra-oligomer duplexes.", PAUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PAUA_LABEL}));
        availableProperties.put(PAUE_LABEL, new Property("Profile of the length-counts for the most prominent inter-oligomer duplexes.", PAUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PAUE_LABEL}));
        
        availableProperties.put(PBCA_LABEL, new Property("Profile of the length-counts for baseline intra-oligomer duplexes.", PBCA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PBCA_LABEL,PBUA_LABEL}));
        availableProperties.put(PBCE_LABEL, new Property("Profile of the length-counts for baseline inter-oligomer duplexes.", PBCE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PBCE_LABEL,PBUE_LABEL}));
        availableProperties.put(PBUA_LABEL, new Property("Profile of the length-counts for the most prominent baseline intra-oligomer duplexes.", PBUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PBUA_LABEL}));
        availableProperties.put(PBUE_LABEL, new Property("Profile of the length-counts for the most prominent baseline inter-oligomer duplexes.", PBUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PBUE_LABEL}));
        
        availableProperties.put(PDCA_LABEL, new Property("Profile of the length-counts for unnecessary intra-oligomer duplexes.", PDCA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PDCA_LABEL,PDUA_LABEL,PAUA_LABEL,PBUA_LABEL}));
        availableProperties.put(PDCE_LABEL, new Property("Profile of the length-counts for unnecessary inter-oligomer duplexes.", PDCE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PDCE_LABEL,PDUE_LABEL,PAUE_LABEL,PBUE_LABEL}));
        availableProperties.put(PDUA_LABEL, new Property("Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes.", PDUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PDUA_LABEL,PAUA_LABEL,PBUA_LABEL}));
        availableProperties.put(PDUE_LABEL, new Property("Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes.", PDUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PDUE_LABEL,PAUE_LABEL,PBUE_LABEL}));
        
        availableProperties.put(DAUA_LABEL, new Property("List of all intra-oligomer duplexes.", DAUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DAUA_LABEL}));
        availableProperties.put(DAUE_LABEL, new Property("List of all inter-oligomer duplexes.", DAUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DAUE_LABEL}));
        availableProperties.put(DBUA_LABEL, new Property("List of baseline intra-oligomer duplexes.", DBUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DBUA_LABEL}));
        availableProperties.put(DBUE_LABEL, new Property("List of baseline inter-oligomer duplexes.", DBUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DBUE_LABEL}));
    }

    final int MAXTHREADS;
    final int MAXTHREADSPERNETWORK;
    final AnalysisSupervisor as;
    
    public Analyzer(int maxThreads, int maxThreadsPerNetwork){
        
        MAXTHREADS = maxThreads;
        MAXTHREADSPERNETWORK = maxThreadsPerNetwork;
        as = new AnalysisSupervisor(MAXTHREADS,MAXTHREADSPERNETWORK);
    }
    
    public static class Request{
        IDomainBasedEncodedNetwork network;
        Collection<String> requestedProperties;
        Map<String,String> parameters;
    
        public Request(IDomainBasedEncodedNetwork network, Collection<String> requestedProperties, Map<String,String> parameters){
            this.network = network;
            this.requestedProperties = requestedProperties;
            this.parameters = parameters;
        }
    }
    
    public Report analyze(Request r){
        Collection<String> neededProperties = new HashSet<>();
        Collection<String> neededParameters = new HashSet<>();
        
        for (String propertyLabel : r.requestedProperties){
            Property p = availableProperties.get(propertyLabel);
            if (p == null) {
                System.err.println("Property "+ propertyLabel + " not supported by analyzer.");
                System.exit(1);
            }
            for (String label : p.getNeededParameters()){
                neededParameters.add(label);
            }
            for (String label : p.getNeededProperties()){
                neededProperties.add(label);
            }
        }
        
        Map<String,String> usedParameters = new HashMap<>();
        for (String parameterLabel : neededParameters){
            Parameter p = availableParameters.get(parameterLabel);
            if (p == null) {
                System.err.println("Parameter "+ parameterLabel + " not supported by analyzer.");
                System.exit(1);
            }
            String value = r.parameters.get(parameterLabel);
            if (value == null) {
                System.err.println("Parameter "+ parameterLabel + " required for analysis.");
                System.exit(1);
            }
            usedParameters.put(parameterLabel,value);
        }
        
        BigInteger baselineN = null;
        BigInteger baselineO = null;
        BigInteger baselineW = null;
        BigInteger deltaN = null;
        BigInteger deltaO = null;
        BigInteger deltaW = null;
        BigInteger N = null;
        BigInteger O = null;
        BigInteger W = null;
        Map<Integer,Integer> paca = null;
        Map<Integer,Integer> pace = null;
        Map<Integer,Integer> paua  = null;
        Map<Integer,Integer> paue  = null;
        Map<Integer,Integer> pbca = null;
        Map<Integer,Integer> pbce = null;
        Map<Integer,Integer> pbua = null;
        Map<Integer,Integer> pbue = null;
        Map<Integer,Integer> pdca = null;
        Map<Integer,Integer> pdce = null;
        Map<Integer,Integer> pdua = null;
        Map<Integer,Integer> pdue = null;
                
        Map<String,String> calculatedPropertyValues = new HashMap<>();
        
        // if baseline intra duplexes is required calculate it.
        if (neededProperties.contains(PBUA_LABEL)){
            pbua = as.getBaselineIntraDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbua.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PBUA_LABEL, valueString.toString());
        }
        
        // if baseline inter duplexes is required calculate it.
        if (neededProperties.contains(PBUE_LABEL)){
            pbue = as.getBaselineInterDuplexCount(r.network, Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbue.entrySet()){
                valueString.append(entry.getKey()).append(" ").append(entry.getValue()).append(System.lineSeparator());
            }
            calculatedPropertyValues.put(PBUE_LABEL, valueString.toString());
        }
        
        // if baselineN is required calculate it.
        if (neededProperties.contains(BASELINE_N_LABEL)){
            baselineN = as.getN(pbue, Integer.valueOf(usedParameters.get(INTER_SB_LABEL)), Integer.valueOf(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(BASELINE_N_LABEL, baselineN.toString());
        }
        
        // if baeslineO is required calculate it.
        if (neededProperties.contains(BASELINE_O_LABEL)){
            baselineO = as.getO(pbua, Integer.valueOf(usedParameters.get(INTRA_SB_LABEL)), Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(BASELINE_O_LABEL, baselineO.toString());
        }
        
        // if baselineW is required calculate it.
        if (neededProperties.contains(BASELINE_W_LABEL)){
            baselineW = baselineO.multiply(new BigInteger(usedParameters.get(SWX_LABEL))).add(baselineN);
            calculatedPropertyValues.put(BASELINE_W_LABEL, baselineW.toString());
        }
        
        // if intraduplexes is required, calculate it.
        if (neededProperties.contains(PAUA_LABEL)){
            paua = as.getIntraDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : paua.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PAUA_LABEL, valueString.toString());
        }
        
        // if interduplexes is required, calculate it.
        if (neededProperties.contains(PAUE_LABEL)){
            paue = as.getInterDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTER_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : paue.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PAUE_LABEL, valueString.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(N_LABEL)){
            N = as.getN(paue, Integer.parseInt(usedParameters.get(INTER_SB_LABEL)), Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(N_LABEL, N.toString());
        }
        
        // if O is required calculate it.
        if (neededProperties.contains(O_LABEL)){
            O = as.getO(paua, Integer.parseInt(usedParameters.get(INTRA_SB_LABEL)), Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(O_LABEL, O.toString());
        }
        
        // if W is required calculate it.
        if (neededProperties.contains(W_LABEL)){
            W = O.multiply(new BigInteger(usedParameters.get(SWX_LABEL))).add(N);
            calculatedPropertyValues.put(W_LABEL, W.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(DELTA_N_LABEL)){
            deltaN = N.subtract(baselineN);
            calculatedPropertyValues.put(DELTA_N_LABEL, deltaN.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(DELTA_O_LABEL)){
            deltaO = O.subtract(baselineO);
            calculatedPropertyValues.put(DELTA_O_LABEL, deltaO.toString());
        }
        
        // if N is required calculate it.
        if (neededProperties.contains(DELTA_W_LABEL)){
            deltaW = W.subtract(baselineW);
            calculatedPropertyValues.put(DELTA_W_LABEL, deltaW.toString());
        }
        
        //PACA
        if (neededProperties.contains(PACA_LABEL)){
            paca = getCompleteProfile(paua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : paca.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PACA_LABEL, valueString.toString());
        }
        
        //PACE
        if (neededProperties.contains(PACE_LABEL)){
            pace = getCompleteProfile(paue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pace.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PACE_LABEL, valueString.toString());
        }
        
        //PBCA
        if (neededProperties.contains(PBCA_LABEL)){
            pbca = getCompleteProfile(pbua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbca.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PBCA_LABEL, valueString.toString());
        }
        
        //PBCE
        if (neededProperties.contains(PBCE_LABEL)){
            pbce = getCompleteProfile(pbue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbce.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PBCE_LABEL, valueString.toString());
        }
        
        //PDUA
        if (neededProperties.contains(PDUA_LABEL)){
            pdua = getProfileDifference(paua,pbua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdua.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PDUA_LABEL, valueString.toString());
        }
        
        //PDUE
        if (neededProperties.contains(PDUE_LABEL)){
            pdue = getProfileDifference(paue,pbue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdue.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PDUE_LABEL, valueString.toString());
        }
        
        //PDCA
        if (neededProperties.contains(PDCA_LABEL)){
            pdca = getCompleteProfile(pdua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdca.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PDCA_LABEL, valueString.toString());
        }
        
        //PDCE
        if (neededProperties.contains(PDCE_LABEL)){
            pdce = getCompleteProfile(pdue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdce.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(PDCE_LABEL, valueString.toString());
        }
        
        //DAUA
        Duplexes daua = null;
        if (neededProperties.contains(DAUA_LABEL)){
            daua = as.getIntraDuplexes(r.network,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(DAUA_LABEL, daua.toString());
        }
        
        //DAUE
        Duplexes daue = null;
        if (neededProperties.contains(DAUE_LABEL)){
            daue = as.getInterDuplexes(r.network,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(DAUE_LABEL, daue.toString());
        }
        
        //DBUA
        Duplexes dbua = null;
        if (neededProperties.contains(DBUA_LABEL)){
            dbua = as.getBaselineIntraDuplexes(r.network,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(DBUA_LABEL, dbua.toString());
        }
        
        //DBUE
        Duplexes dbue = null;
        if (neededProperties.contains(DBUE_LABEL)){
            dbue = as.getBaselineInterDuplexes(r.network,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(DBUE_LABEL, dbue.toString());
        }
        
        Map<String,String> requestedPropertyValues = new HashMap<>();
        for (String property : r.requestedProperties){
            requestedPropertyValues.put(property,calculatedPropertyValues.get(property));
        }
        
        Map<String,String> necessaryPropertyValues = new HashMap<>();
        for (String property : neededProperties){
            if (!requestedPropertyValues.containsKey(property)){
            necessaryPropertyValues.put(property,calculatedPropertyValues.get(property));
            }
        }
        
        return new Report(necessaryPropertyValues, requestedPropertyValues, usedParameters);
    }
    
    public static class Report{
        Map<String,String> neededProperties;
        Map<String,String> requestedProperties;
        Map<String,String> usedParameters;
        
        Report(Map<String,String> necessaryProperties,Map<String,String> requestedProperties, Map<String,String> usedParameters){
            this.neededProperties = necessaryProperties;
            this.requestedProperties = requestedProperties;
            this.usedParameters = usedParameters;
        }
        
        public Map<String,String> getNecessaryPropertyValues(){
            return neededProperties;
        }
        
        public Map<String,String> getRequestedPropertyValues(){
            return requestedProperties;
        }
        
        public Map<String,String> getUsedParameters(){
            return usedParameters;
        }
    }
    
    static private class AnalysisSupervisor{
        final ExecutorService es;
        final int maxThreads;
        final int maxThreadsPerNetwork;
        
        final Map<Integer,int[]> knownRanges;
        final Map<Integer,Combination[]> knownCombos; // map connecting variable domain index to affected combinations;
        final Map<Integer,Map<Integer,BasePair[][]>> knownBasePairs;

        final Map<Integer,BigInteger> knownIntraScores;
        final Map<Integer,BigInteger> knownInterScores;
        
        AnalysisSupervisor(int maxThreads, int maxThreadsPerNetwork){
            this.maxThreads = maxThreads;
            this.maxThreadsPerNetwork = maxThreadsPerNetwork;
            es = Executors.newFixedThreadPool(maxThreads);
            
            knownIntraScores = new ConcurrentHashMap<>();
            knownInterScores = new ConcurrentHashMap<>();
            knownRanges = new ConcurrentHashMap<>();
            knownBasePairs = new ConcurrentHashMap<>();
            knownCombos = new ConcurrentHashMap<>();
        }
        
        Map<Integer,Integer> getBaselineInterDuplexCount( IDomainBasedEncodedNetwork network, int interSLC){
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            //ueoSequences = ueo.values().stream().toArray(i->new int[i][]);
            
            int[][] encodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                encodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial N
            Future<int[]>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC));
            }

            // Add up length Counts;
            Map<Integer,Integer> ret = new HashMap<>();
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    int[] lengthCounts = futures[i].get();
                    for(int j = 0 ; j<lengthCounts.length;j++){
                        int counts = lengthCounts[j];
                        if (counts>0){
                            ret.merge(j,counts,(x,y)->x+y);
                        }
                    }
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            
            if (ret.size() == 0){
                ret.put(interSLC, 0);
            }
            
            return ret;
        }
        
        Map<Integer,Integer> getBaselineIntraDuplexCount( IDomainBasedEncodedNetwork network, int intraSLC){
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            //ueoSequences = ueo.values().stream().toArray(i->new int[i][]);
            
            int[][] encodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                encodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            int maxLength = Arrays.stream(encodedOligomers).mapToInt(oligomer -> oligomer.length).max().getAsInt();
            int[] lengthCounts = new int[maxLength+1];
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
            //for each alignment

            int[] encodedOligomer;
            int[] S1;
            int S1length;
            int b1Max;
            int structureLength;
            int b1;
            int b2;
            int length;
            Integer singleCount = 1;
            // for each oligomer
            for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
                encodedOligomer = encodedOligomers[i];
                S1 = encodedOligomer;
                S1length = S1.length;
                b1Max = S1length-1;

                for (int j : IntStream.range(0,S1length).toArray()){
                    structureLength = 0;
                    b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                    b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                    length = S1length/2;
                    if(S1length % 2 == 0 && j%2 == 1)
                    {
                        length = length -1;
                    }

                    if(S1[b1] + S1[b2] ==0)
                    {
                        structureLength = 1;
                    }

                    //For every base-pair in the reference position
                    for ( int k =1; k < length; k++)
                    {
                        if( b1 == b1Max) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b1 = 0;
                                structureLength = 0;
                        } else {b1++;}

                        if( b2 == 0) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b2 = b1Max;
                                structureLength = 0;
                        } else {b2--;}

                        if(S1[b1]+S1[b2]==0)
                        {
                                structureLength++;
                        }
                        else
                        {
                            if (structureLength >= intraSLC)
                            {
                                
                                lengthCounts[structureLength]++;
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            structureLength =0;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (structureLength >= intraSLC)
                    {
                        
                        lengthCounts[structureLength]++;
                        //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    };
                }
            }
            Map<Integer,Integer> ret = new HashMap<>();
            for(int i : IntStream.range(0,lengthCounts.length).toArray()){
                int duplexLength = i;
                int counts = lengthCounts[i];
                if (counts >0){
                    ret.put(duplexLength,counts);
                }
            }
            if (ret.size() == 0){
                ret.put(intraSLC, 0);
            }
            return ret;
        }
        
        Duplexes getBaselineInterDuplexes( IDomainBasedEncodedNetwork network, int interSLC){
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            int[][] baselineEncodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                baselineEncodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial
            Future<Duplexes>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new InterDuplexesRequest( baselineEncodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new InterDuplexesRequest( baselineEncodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC));
            }

            // Collect duplexes.
            Duplexes ret = new Duplexes();
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    Duplexes duplexes = futures[i].get();
                    ret = new Duplexes(ret,duplexes);
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            
            return ret;
        }
        
        Duplexes getBaselineIntraDuplexes( IDomainBasedEncodedNetwork network, int intraSLC){
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            int[][] baselineEncodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                baselineEncodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            
            //int[] lengthCounts = new int[maxLength+1];
            Duplexes duplexes = new Duplexes();
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
            //for each alignment

            int S1length;
            int b1Max;
            int encodedStructureLength;
            int b1;
            int lastB1=0;
            int b2;
            int lastB2=0;
            int length;
            Integer singleCount = 1;
            // for each oligomer
            for(int i : IntStream.range(0,baselineEncodedOligomers.length).toArray()){
                S1length = baselineEncodedOligomers[i].length;
                b1Max = S1length-1;

                for (int j : IntStream.range(0,S1length).toArray()){
                    encodedStructureLength = 0;
                    b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                    b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                    length = S1length/2;
                    if(S1length % 2 == 0 && j%2 == 1)
                    {
                        length = length -1;
                    }

//                    if(baselineEncodedOligomers[i][b1] + baselineEncodedOligomers[i][b2] ==0){
//                        encodedStructureLength = 1;
//                    }

                    //For every base-pair in the reference position
                    for ( int k =0; k < length; k++)
                    {
                        //compare current base-pair. 
                        if (baselineEncodedOligomers[i][b1]+baselineEncodedOligomers[i][b2]==0){
                            encodedStructureLength++;
                        } else {
                            if ( encodedStructureLength >= intraSLC){
                                duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
                            }
                            encodedStructureLength = 0;
                        }
                        
                        //itterate after.
                        if( b1 == b1Max){
                            if (encodedStructureLength >= intraSLC){
                                    duplexes.addDuplex(i,b1-encodedStructureLength+1, i, b2,encodedStructureLength);
                            }
                            lastB1 = b1;
                            b1 = 0;
                            encodedStructureLength = 0;
                        } else {
                            lastB1 = b1;
                            b1++;
                        }

                        if( b2 == 0){ 
                            if (encodedStructureLength >= intraSLC){
                                duplexes.addDuplex(i,b1-encodedStructureLength+1, i, b2,encodedStructureLength);
                            }
                            lastB2 = b2;
                            b2 = b1Max;
                            encodedStructureLength = 0;
                        } else {
                            lastB2 = b2;
                            b2--;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (encodedStructureLength >= intraSLC)
                    {
                        if (encodedStructureLength >= intraSLC){
                            duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
                        }
                    };
                }
            }
            
            return duplexes;
        }
        
        Map<Integer,Integer> getInterDuplexCount(IDomainBasedEncodedNetwork network, int interSLC){
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial N
            Future<int[]>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new CountInterDuplexRequest(encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC));
            }

            // Add up length Counts;
            Map<Integer,Integer> ret = new HashMap<>();
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    int[] lengthCounts = futures[i].get();
                    for(int j = 0 ; j<lengthCounts.length;j++){
                        int counts = lengthCounts[j];
                        if (counts>0){
                            ret.merge(j,counts,(x,y)->x+y);
                        }
                    }
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            if (ret.size() == 0){
                ret.put(interSLC, 0);
            }
            
            return ret;
        }
        
        Map<Integer,Integer> getIntraDuplexCount(IDomainBasedEncodedNetwork network, int intraSLC){
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            int maxLength = Arrays.stream(encodedOligomers).mapToInt(oligomer -> oligomer.length).max().getAsInt();
            int[] lengthCounts = new int[maxLength+1];
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
            //for each alignment

            int[] encodedOligomer;
            int[] S1;
            int S1length;
            int b1Max;
            int structureLength;
            int b1;
            int b2;
            int length;
            Integer singleCount = 1;
            // for each oligomer
            for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
                encodedOligomer = encodedOligomers[i];
                S1 = encodedOligomer;
                S1length = S1.length;
                b1Max = S1length-1;

                for (int j : IntStream.range(0,S1length).toArray()){
                    structureLength = 0;
                    b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                    b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                    length = S1length/2;
                    if(S1length % 2 == 0 && j%2 == 1)
                    {
                        length = length -1;
                    }

                    if(S1[b1] + S1[b2] ==0)
                    {
                        structureLength = 1;
                    }

                    //For every base-pair in the reference position
                    for ( int k =1; k < length; k++)
                    {
                        if( b1 == b1Max) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b1 = 0;
                                structureLength = 0;
                        } else {b1++;}

                        if( b2 == 0) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b2 = b1Max;
                                structureLength = 0;
                        } else {b2--;}

                        if(S1[b1]+S1[b2]==0)
                        {
                                structureLength++;
                        }
                        else
                        {
                            if (structureLength >= intraSLC)
                            {
                                
                                lengthCounts[structureLength]++;
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            structureLength =0;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (structureLength >= intraSLC)
                    {
                        
                        lengthCounts[structureLength]++;
                        //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    };
                }
            }
            Map<Integer,Integer> ret = new HashMap<>();
            for(int i : IntStream.range(0,lengthCounts.length).toArray()){
                int duplexLength = i;
                int counts = lengthCounts[i];
                if (counts >0){
                    ret.put(duplexLength,counts);
                }
            }
            if (ret.size() == 0){
                ret.put(intraSLC, 0);
            }
            return ret;
        }
        
        Duplexes getInterDuplexes(IDomainBasedEncodedNetwork network, int interSLC){
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial
            Future<Duplexes>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new InterDuplexesRequest(encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new InterDuplexesRequest(encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC));
            }

            // Collect duplexes.
            Duplexes ret = new Duplexes();
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    Duplexes duplexes = futures[i].get();
                    ret = new Duplexes(ret,duplexes);
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            
            return ret;
        }
        
        Duplexes getIntraDuplexes(IDomainBasedEncodedNetwork network, int intraSLC){
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            int maxLength = Arrays.stream(encodedOligomers).mapToInt(oligomer -> oligomer.length).max().getAsInt();
            //int[] lengthCounts = new int[maxLength+1];
            Duplexes duplexes = new Duplexes();
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
            //for each alignment

            int[] encodedOligomer;
            int[] S1;
            int S1length;
            int b1Max;
            int structureLength;
            int b1;
            int b1Last;
            int b2;
            int b2Last;
            int length;
            Integer singleCount = 1;
            // for each oligomer
            for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
                encodedOligomer = encodedOligomers[i];
                S1 = encodedOligomer;
                S1length = S1.length;
                b1Max = S1length-1;

                for (int j : IntStream.range(0,S1length).toArray()){
                    structureLength = 0;
                    b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                    b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                    length = S1length/2;
                    if(S1length % 2 == 0 && j%2 == 1)
                    {
                        length = length -1;
                    }

                    if(S1[b1] + S1[b2] ==0)
                    {
                        structureLength = 1;
                    }

                    //For every base-pair in the reference position
                    for ( int k =1; k < length; k++)
                    {
                        if( b1 == b1Max) 
                        {
                            if (structureLength >= intraSLC)
                            {
                                duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                                //lengthCounts[structureLength]++;
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            b1Last = b1;
                            b1 = 0;
                            structureLength = 0;
                        } else {
                            b1Last = b1;
                            b1++;
                        }

                        if( b2 == 0) 
                        {
                            if (structureLength >= intraSLC)
                            {
                                duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                                //lengthCounts[structureLength]++;
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            b2Last = b2;
                            b2 = b1Max;
                            structureLength = 0;
                        } else {
                            b2Last = b2;
                            b2--;
                        }

                        if(S1[b1]+S1[b2]==0)
                        {
                                structureLength++;
                        }
                        else
                        {
                            if (structureLength >= intraSLC)
                            {
                                duplexes.addDuplex(i,b1Last-structureLength+1, i, b2Last,structureLength);
                                //lengthCounts[structureLength]++;
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            structureLength =0;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (structureLength >= intraSLC)
                    {
                        duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                        //lengthCounts[structureLength]++;
                        //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    };
                }
            }
            
            return duplexes;
        }
        
        LargestDuplexes getLargestDeltaInterDuplexes(IDomainBasedEncodedNetwork network, int interSLC, int numberDuplexes){
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            int[][] baselineEncodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                baselineEncodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial
            Future<LargestDuplexes>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new LargestDeltaInterDuplexesRequest(baselineEncodedOligomers, encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC, numberDuplexes));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new LargestDeltaInterDuplexesRequest(baselineEncodedOligomers, encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC, numberDuplexes));
            }

            // Collect duplexes.
            LargestDuplexes ret = new LargestDuplexes(numberDuplexes);
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    LargestDuplexes duplexes = futures[i].get();
                    ret = new LargestDuplexes(ret,duplexes);
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            
            return ret;
        }
        
        LargestDuplexes getLargestDeltaIntraDuplexes(IDomainBasedEncodedNetwork network, int intraSLC, int numberDuplexes){
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            int[][] baselineEncodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                baselineEncodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            int maxLength = Arrays.stream(encodedOligomers).mapToInt(oligomer -> oligomer.length).max().getAsInt();
            //int[] lengthCounts = new int[maxLength+1];
            LargestDuplexes duplexes = new LargestDuplexes(numberDuplexes);
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
            //for each alignment

            int S1length;
            int b1Max;
            int baselineStructureLength;
            int encodedStructureLength;
            int b1;
            int b2;
            int length;
            Integer singleCount = 1;
            // for each oligomer
            for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
                S1length = encodedOligomers[i].length;
                b1Max = S1length-1;

                for (int j : IntStream.range(0,S1length).toArray()){
                    baselineStructureLength = 0;
                    encodedStructureLength = 0;
                    b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                    b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                    length = S1length/2;
                    if(S1length % 2 == 0 && j%2 == 1)
                    {
                        length = length -1;
                    }

                    if(encodedOligomers[i][b1] + encodedOligomers[i][b2] ==0){
                        encodedStructureLength = 1;
                    }
                    
                    if(baselineEncodedOligomers[i][b1] + baselineEncodedOligomers[i][b2] ==0){
                        baselineStructureLength = 1;
                    }

                    //For every base-pair in the reference position
                    for ( int k =0; k < length; k++)
                    {
                        //compare current base-pair. 
                        if (encodedOligomers[i][b1]+encodedOligomers[i][b2]==0){
                            encodedStructureLength++;
                            if (baselineEncodedOligomers[i][b1]+baselineEncodedOligomers[i][b2]==0){
                                baselineStructureLength++;
                            }
                        } else {
                            if ( encodedStructureLength >= intraSLC){
                                if(encodedStructureLength > baselineStructureLength){
                                    duplexes.addDuplex(i,b1-encodedStructureLength+1, i, b2,encodedStructureLength);
                                }
                            }
                            encodedStructureLength = 0;
                            baselineStructureLength = 0;
                        }
                        
                        //itterate after.
                        if( b1 == b1Max){
                            if (encodedStructureLength >= intraSLC){
                                if(encodedStructureLength > baselineStructureLength){
                                    duplexes.addDuplex(i,b1-encodedStructureLength+1, i, b2,encodedStructureLength);
                                }
                            }
                            b1 = 0;
                            encodedStructureLength = 0;
                            baselineStructureLength = 0;
                        } else {b1++;}

                        if( b2 == 0){
                                if (encodedStructureLength >= intraSLC){
                                    if(encodedStructureLength > baselineStructureLength){
                                        duplexes.addDuplex(i,b1-encodedStructureLength+1, i, b2,encodedStructureLength);
                                    }
                                }
                                b2 = b1Max;
                                encodedStructureLength = 0;
                                baselineStructureLength = 0;
                        } else {b2--;}
                    }

                    //if the loop ended with an active structure, record it.
                    if (encodedStructureLength >= intraSLC)
                    {
                        if (encodedStructureLength >= intraSLC){
                            if(encodedStructureLength > baselineStructureLength){
                                duplexes.addDuplex(i,b1-1-encodedStructureLength+1, i, b2+1,encodedStructureLength);
                            }
                        }
                    };
                }
            }
            
            return duplexes;
        }
        
        LargestDuplexes getLargestInterDuplexes(IDomainBasedEncodedNetwork network, int interSLC, int numberDuplexes){
            Combination[] combos = getCombos(AnalysisSupervisor.this, network);
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            
            int threadsPerNetwork = maxThreadsPerNetwork;
            int comboPerThread = (combos.length+threadsPerNetwork-1)/threadsPerNetwork;

            ArrayList<Integer> lastIndexes = new ArrayList<>();
            for(int i=0; (i < threadsPerNetwork ) && (i*comboPerThread < combos.length) ; i++){
                int firstIndex = i*comboPerThread;
                Integer lastIndex = Math.min(firstIndex+comboPerThread, combos.length);
                lastIndexes.add(lastIndex);
            }
            Integer[] lastIndexesArray = lastIndexes.toArray(x->new Integer[x]);
            int[] indexList = IntStream.range(1,lastIndexesArray.length).toArray();

            // start calculation of new partial
            Future<LargestDuplexes>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new LargestInterDuplexesRequest(encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC, numberDuplexes));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new LargestInterDuplexesRequest(encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC, numberDuplexes));
            }

            // Collect duplexes.
            LargestDuplexes ret = new LargestDuplexes(numberDuplexes);
            for (int i = 0; i < lastIndexesArray.length; i++ ){
                try{
                    LargestDuplexes duplexes = futures[i].get();
                    ret = new LargestDuplexes(ret,duplexes);
                } catch (Exception e) {System.err.println(e.getMessage());}
            }
            
            return ret;
        }
        
        LargestDuplexes getLargestIntraDuplexes(IDomainBasedEncodedNetwork network, int intraSLC, int numberDuplexes){
            int[][] encodedOligomers = network.getOligomerSequencesEncoded();
            int maxLength = Arrays.stream(encodedOligomers).mapToInt(oligomer -> oligomer.length).max().getAsInt();
            //int[] lengthCounts = new int[maxLength+1];
            LargestDuplexes duplexes = new LargestDuplexes(numberDuplexes);
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
            //for each alignment

            int[] encodedOligomer;
            int[] S1;
            int S1length;
            int b1Max;
            int structureLength;
            int b1;
            int b2;
            int length;
            Integer singleCount = 1;
            // for each oligomer
            for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
                encodedOligomer = encodedOligomers[i];
                S1 = encodedOligomer;
                S1length = S1.length;
                b1Max = S1length-1;

                for (int j : IntStream.range(0,S1length).toArray()){
                    structureLength = 0;
                    b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
                    b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;

                    length = S1length/2;
                    if(S1length % 2 == 0 && j%2 == 1)
                    {
                        length = length -1;
                    }

                    if(S1[b1] + S1[b2] ==0)
                    {
                        structureLength = 1;
                    }

                    //For every base-pair in the reference position
                    for ( int k =1; k < length; k++)
                    {
                        if( b1 == b1Max) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                                    //lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b1 = 0;
                                structureLength = 0;
                        } else {b1++;}

                        if( b2 == 0) 
                        {
                                if (structureLength >= intraSLC)
                                {
                                    duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                                    //lengthCounts[structureLength]++;
                                    //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                                }
                                b2 = b1Max;
                                structureLength = 0;
                        } else {b2--;}

                        if(S1[b1]+S1[b2]==0)
                        {
                                structureLength++;
                        }
                        else
                        {
                            if (structureLength >= intraSLC)
                            {
                                duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                                //lengthCounts[structureLength]++;
                                //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                            }
                            structureLength =0;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (structureLength >= intraSLC)
                    {
                        duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                        //lengthCounts[structureLength]++;
                        //lengthCounts.merge(structureLength,singleCount,(x,y)->x+y);
                    };
                }
            }
            
            return duplexes;
        }
        
        BigInteger getN (Map<Integer,Integer> uniqueLengthCounts, int interSB, int interSLC){
            BigInteger retScore = BigInteger.valueOf(0);
            for(Map.Entry<Integer,Integer> entry: uniqueLengthCounts.entrySet()){
                int length = entry.getKey();
                int counts = entry.getValue();
                BigInteger lengthScore = knownInterScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, interSLC, interSB));
                retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
            }
            return retScore;
        }
        
        BigInteger getO (Map<Integer,Integer> uniqueLengthCounts, int intraSB, int intraSLC){
            BigInteger retScore = BigInteger.valueOf(0);
            for(Map.Entry<Integer,Integer> entry: uniqueLengthCounts.entrySet()){
                int length = entry.getKey();
                int counts = entry.getValue();
                BigInteger lengthScore = knownIntraScores.computeIfAbsent(length, (x)->calculateUniqueDuplexPoints(x, intraSLC, intraSB));
                retScore = retScore.add(lengthScore.multiply(BigInteger.valueOf(counts)));
            }
            return retScore;
        }
        
        static private class Worker implements Runnable{
            LinkedBlockingQueue<Runnable> queue;
            Worker(LinkedBlockingQueue<Runnable> queue){
                this.queue = queue;
            }
            public void run(){
                while (!Thread.currentThread().isInterrupted()){
                    try{
                        Runnable r = queue.take();
                        synchronized (r) {
                            r.run();
                            r.notify();
                        }
                    } catch (Exception e) {System.err.println(e);}
                }
            }
        }
        
        static private class CountInterDuplexRequest implements Callable<int[]>{
            final Combination[] combinations;
            final int firstIndex;
            final int lastIndex;
            final int[][] encodedOligomers;
            final AnalysisSupervisor as;
            final int interSLC;
            //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
            
            CountInterDuplexRequest(int[][] encodedOligomers, AnalysisSupervisor analysisSupervisor, Combination[] combinations, int firstIndex, int lastIndex, int interSLC){
                this.combinations = combinations;
                this.firstIndex = firstIndex;
                this.lastIndex = lastIndex;
                this.encodedOligomers = encodedOligomers;
                this.as = analysisSupervisor;
                this.interSLC = interSLC;
            }
            
            public int[] call(){
                int maxLength =0;
                for(int i =firstIndex; i < lastIndex; i++){
                    if (encodedOligomers[combinations[i].indexO1].length > maxLength){
                        maxLength = encodedOligomers[combinations[i].indexO1].length;
                    }
                }
                int[] lengthCounts = new int[maxLength+1];

                int indexS1;
                int indexS2;
                int structureLength;
                BasePair[][] allBP = new BasePair[0][];
                Combination currentCombo;

                // for each oligomer combination
                for(int i =firstIndex; i < lastIndex; i++){
                    currentCombo = combinations[i];
                    indexS1 = currentCombo.indexO1;
                    indexS2 = currentCombo.indexO2;
                    allBP = currentCombo.allBP;
                    
                    // for each stretch of base pairs.
                    for (BasePair[] bps : allBP){
                        structureLength=0;
                        // for each base-pair in the stretch.
                        for(BasePair bp : bps){
                            if(encodedOligomers[indexS1][bp.index1]+encodedOligomers[indexS2][bp.index2] == 0){
                                structureLength++;
                            } else {
                                if (structureLength >= interSLC){
                                    
                                    lengthCounts[structureLength]++;
                                }
                                structureLength = 0;
                            }
                        }
                        if (structureLength >= interSLC){
                            lengthCounts[structureLength]++;
                        }
                    }
                }
                return lengthCounts;
            }
        }
        
        static private class InterDuplexesRequest implements Callable<Duplexes>{
            final Combination[] combinations;
            final int firstIndex;
            final int lastIndex;
            final int[][] encodedOligomers;
            final AnalysisSupervisor as;
            final int interSLC;
            //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
            
            InterDuplexesRequest(int[][] encodedOligomers, AnalysisSupervisor analysisSupervisor, Combination[] combinations, int firstIndex, int lastIndex, int interSLC){
                this.combinations = combinations;
                this.firstIndex = firstIndex;
                this.lastIndex = lastIndex;
                this.encodedOligomers = encodedOligomers;
                this.as = analysisSupervisor;
                this.interSLC = interSLC;
            }
            
            public Duplexes call(){
                int maxLength =0;
                for(int i =firstIndex; i < lastIndex; i++){
                    if (encodedOligomers[combinations[i].indexO1].length > maxLength){
                        maxLength = encodedOligomers[combinations[i].indexO1].length;
                    }
                }
                //int[] lengthCounts = new int[maxLength+1];
                Duplexes duplexes = new Duplexes();

                int indexS1;
                int indexS2;
                int structureLength;
                BasePair[][] allBP = new BasePair[0][];
                BasePair lastBP = null;
                Combination currentCombo;

                // for each oligomer combination
                for(int i =firstIndex; i < lastIndex; i++){
                    currentCombo = combinations[i];
                    indexS1 = currentCombo.indexO1;
                    indexS2 = currentCombo.indexO2;
                    allBP = currentCombo.allBP;
                    
                    // for each stretch of base pairs.
                    for (BasePair[] bps : allBP){
                        structureLength=0;
                        // for each base-pair in the stretch.
                        for(BasePair bp : bps){
                            if(encodedOligomers[indexS1][bp.index1]+encodedOligomers[indexS2][bp.index2] == 0){
                                structureLength++;
                            } else {
                                if (structureLength >= interSLC){
                                    duplexes.addDuplex(indexS1,lastBP.index1-structureLength+1, indexS2, lastBP.index2,structureLength);
                                    //lengthCounts[structureLength]++;
                                }
                                structureLength = 0;
                            }
                            lastBP = bp;
                        }
                        if (structureLength >= interSLC){
                            duplexes.addDuplex(indexS1,lastBP.index1-structureLength+1, indexS2, lastBP.index2,structureLength);
                            //lengthCounts[structureLength]++;
                        }
                    }
                }
                return duplexes;
            }
        }
        
        static private class LargestInterDuplexesRequest implements Callable<LargestDuplexes>{
            final Combination[] combinations;
            final int firstIndex;
            final int lastIndex;
            final int[][] encodedOligomers;
            final AnalysisSupervisor as;
            final int interSLC;
            final int numberDuplexes;
            //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
            
            LargestInterDuplexesRequest(int[][] encodedOligomers, AnalysisSupervisor analysisSupervisor, Combination[] combinations, int firstIndex, int lastIndex, int interSLC, int numberDuplexes){
                this.combinations = combinations;
                this.firstIndex = firstIndex;
                this.lastIndex = lastIndex;
                this.encodedOligomers = encodedOligomers;
                this.as = analysisSupervisor;
                this.interSLC = interSLC;
                this.numberDuplexes = numberDuplexes;
            }
            
            public LargestDuplexes call(){
                int maxLength =0;
                for(int i =firstIndex; i < lastIndex; i++){
                    if (encodedOligomers[combinations[i].indexO1].length > maxLength){
                        maxLength = encodedOligomers[combinations[i].indexO1].length;
                    }
                }
                //int[] lengthCounts = new int[maxLength+1];
                LargestDuplexes duplexes = new LargestDuplexes(numberDuplexes);

                int indexS1;
                int indexS2;
                int structureLength;
                BasePair[][] allBP = new BasePair[0][];
                Combination currentCombo;

                // for each oligomer combination
                for(int i =firstIndex; i < lastIndex; i++){
                    currentCombo = combinations[i];
                    indexS1 = currentCombo.indexO1;
                    indexS2 = currentCombo.indexO2;
                    allBP = currentCombo.allBP;
                    
                    // for each stretch of base pairs.
                    for (BasePair[] bps : allBP){
                        structureLength=0;
                        // for each base-pair in the stretch.
                        for(BasePair bp : bps){
                            if(encodedOligomers[indexS1][bp.index1]+encodedOligomers[indexS2][bp.index2] == 0){
                                structureLength++;
                            } else {
                                if (structureLength >= interSLC){
                                    duplexes.addDuplex(indexS1,bp.index1-structureLength+1, indexS2, bp.index2,structureLength);
                                    //lengthCounts[structureLength]++;
                                }
                                structureLength = 0;
                            }
                        }
                        if (structureLength >= interSLC){
                            duplexes.addDuplex(indexS1,bps[bps.length-1].index1-structureLength+1, indexS2, bps[bps.length-1].index2,structureLength);
                            //lengthCounts[structureLength]++;
                        }
                    }
                }
                return duplexes;
            }
        }
        
        static private class LargestDeltaInterDuplexesRequest implements Callable<LargestDuplexes>{
            final Combination[] combinations;
            final int firstIndex;
            final int lastIndex;
            final int[][] baselineEncodedOligomers;
            final int[][] encodedOligomers;
            final AnalysisSupervisor as;
            final int interSLC;
            final int numberDuplexes;
            //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
            
            LargestDeltaInterDuplexesRequest(int[][] baselineEncodedOligomers, int[][] encodedOligomers, AnalysisSupervisor analysisSupervisor, Combination[] combinations, int firstIndex, int lastIndex, int interSLC, int numberDuplexes){
                this.baselineEncodedOligomers = baselineEncodedOligomers;
                this.combinations = combinations;
                this.firstIndex = firstIndex;
                this.lastIndex = lastIndex;
                this.encodedOligomers = encodedOligomers;
                this.as = analysisSupervisor;
                this.interSLC = interSLC;
                this.numberDuplexes = numberDuplexes;
            }
            
            public LargestDuplexes call(){
                int maxLength =0;
                for(int i =firstIndex; i < lastIndex; i++){
                    if (encodedOligomers[combinations[i].indexO1].length > maxLength){
                        maxLength = encodedOligomers[combinations[i].indexO1].length;
                    }
                }
                //int[] lengthCounts = new int[maxLength+1];
                LargestDuplexes duplexes = new LargestDuplexes(numberDuplexes);

                int indexS1;
                int indexS2;
                int baselineComparison;
                int encodedComparison;
                int baselineStructureLength;
                int encodedStructureLength;
                BasePair[][] allBP = null;
                Combination currentCombo;

                // for each oligomer combination
                for(int i =firstIndex; i < lastIndex; i++){
                    currentCombo = combinations[i];
                    indexS1 = currentCombo.indexO1;
                    indexS2 = currentCombo.indexO2;
                    allBP = currentCombo.allBP;
                    
                    // for each stretch of base pairs.
                    for (BasePair[] bps : allBP){
                        baselineStructureLength = 0;
                        encodedStructureLength = 0;
                        // for each base-pair in the stretch.
                        for(BasePair bp : bps){
                            baselineComparison = baselineEncodedOligomers[indexS1][bp.index1]+baselineEncodedOligomers[indexS2][bp.index2];
                            encodedComparison = encodedOligomers[indexS1][bp.index1]+encodedOligomers[indexS2][bp.index2];
                            if(encodedComparison ==0){
                                encodedStructureLength++;
                                if(baselineComparison ==0){
                                    baselineStructureLength++;
                                } else {
                                    baselineStructureLength = 0;
                                }
                            } else {
                                if (encodedStructureLength >= interSLC){
                                    if (encodedStructureLength != baselineStructureLength){
                                        duplexes.addDuplex(indexS1,bp.index1-encodedStructureLength+1, indexS2, bp.index2,encodedStructureLength);
                                    }
                                    //lengthCounts[structureLength]++;
                                }
                                encodedStructureLength = 0;
                                baselineStructureLength = 0;
                            }
                        }
                        if (encodedStructureLength >= interSLC){
                            if (encodedStructureLength != baselineStructureLength){
                                duplexes.addDuplex(indexS1,bps[bps.length-1].index1-encodedStructureLength+1, indexS2, bps[bps.length-1].index2,encodedStructureLength);
                            }
                            //lengthCounts[structureLength]++;
                        }
                    }
                }
                return duplexes;
            }
        }
    }
    
    static private class BasePair{
        final int index1;
        final int index2;
        BasePair(int index1, int index2){
            this.index1 = index1;
            this.index2 = index2;
        }
    }

    // returns an n x m array of base pairs.
    // each n represents a longest possible duplex, aka base-alignment.
    // each m represents a pase pair in the alignment.
    static private BasePair[][] getKnownBasePairs(AnalysisSupervisor as, int oligomer1Length, int oligomer2Length){
        Map<Integer,BasePair[][]> firstMap = as.knownBasePairs.computeIfAbsent(oligomer1Length,x->new ConcurrentHashMap<Integer,BasePair[][]>());
        BasePair[][] bps = firstMap.computeIfAbsent(oligomer2Length,x->calculateBasePairs(as,oligomer1Length,oligomer2Length));
        return bps;
    }

    // returns an n x m array of base pairs.
    // each n represents a longest possible duplex, aka base-alignment.
    // each m represents a pase pair in the alignment.
    // oligomer1Length must be larger than or equal to oligomer2Length
    static private BasePair[][] calculateBasePairs(AnalysisSupervisor as, int oligomer1Length, int oligomer2Length){
        int S1length;
        int b1Max;
        int S2length;
        int b2Max;
        int b1;
        int b2;
        ArrayList<BasePair[]> duplexList = new ArrayList<>();
        ArrayList<BasePair> bpList = new ArrayList<>();

        // for each oligomer combination
        S1length = oligomer1Length;		
        b1Max = S1length-1;
        S2length = oligomer2Length;
        b2Max = S2length-1;
        //for each oligomer alignment
        for (int j : as.knownRanges.computeIfAbsent(S2length,x->IntStream.range(0,x).toArray())){
            b1 = 0; // index of base on the top strand;
            b2 = (b2Max + j) % (S2length);// index of base on the bottom strand;

            do{
                // add a base pair to the array.
                bpList.add(new BasePair(b1,b2));

                //advance to the next base
                b1++;
                if(b2 == 0){
                    BasePair[] copiedArray = Arrays.copyOf(bpList.toArray(x->new BasePair[x]),bpList.size());
                    duplexList.add(copiedArray);
                    bpList.clear();
                    b2 = b2Max;
                } else {b2--;}
            } while (b1 <= b1Max);

            // if the loop ended with a duplex.
            if(bpList.size() > 0){
                BasePair[] copiedArray = Arrays.copyOf(bpList.toArray(x->new BasePair[x]),bpList.size());
                duplexList.add(copiedArray);
                bpList.clear();
            }
        }

        BasePair[][] ret = duplexList.toArray(x->new BasePair[x][]);
        return ret;
    }

    static private Combination[] getCombos(AnalysisSupervisor as, IDomainBasedEncodedNetwork network){
        int[][] encodedOligomers = network.getOligomerSequencesEncoded();
        Combination[] ret = new Combination[(encodedOligomers.length+1)*(encodedOligomers.length)/2];
        int retIndex =0;
        for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
            int indexO1 = i;
            for(int j : IntStream.range(i,encodedOligomers.length).toArray()){
                int indexO2 = j;
                if( encodedOligomers[indexO1].length < encodedOligomers[indexO2].length){
                    BasePair[][] allBP = getKnownBasePairs(as,encodedOligomers[indexO2].length, encodedOligomers[indexO1].length);
                    ret[retIndex] = new Combination(indexO2, indexO1, allBP);
                } else {
                    BasePair[][] allBP = getKnownBasePairs(as,encodedOligomers[indexO1].length, encodedOligomers[indexO2].length);
                    ret[retIndex] = new Combination(indexO1, indexO2, allBP);
                }
                retIndex++;
            }
        }

        return ret;
    }

    private static class Combination{
        int indexO1;
        int indexO2;
        BasePair[][] allBP;
        Combination(int indexO1, int indexO2, BasePair[][] allBP){
            this.indexO1 = indexO1;
            this.indexO2 = indexO2;
            this.allBP = allBP;
        }
    }
    
    private static BigInteger calculateUniqueDuplexPoints (int length, int slc, int base){
        BigInteger score = BigInteger.valueOf(0);
        
        int numberOfStructures = 1;
        for (int i = length; i >= slc; i--){
            score = score.add(BigInteger.valueOf(base).pow(i).multiply(BigInteger.valueOf(numberOfStructures)));
            numberOfStructures++;
        }
        
        return score;
    }
    
    private static Map<String,int[]> encode(String[] names, String[] sequences){
        Map<String,int[]> encoded = new HashMap<>();
    for(int i : IntStream.range(0, names.length).toArray()){
            char[] b = sequences[i].toCharArray();
            int[] e = IntStream.range(0, b.length).map(x->encode(b[x])).toArray();
            encoded.put(names[i], e);
        }
        return encoded;
    }
    
    private static int encode(char c){
        switch (c){
            case 'a':
            case 'A':
                return -2;
            case 'c':
            case 'C':
                return -1;
            case 'g':
            case 'G':
                return 1;
            case 't':
            case 'T':
                return 2;
            default: 
                System.out.println("Error: Base \"" + c + "\" not recognized." );
                System.exit(0);
                return 0;
        }
    }
    
    private static Map<String,int[]> assembleEncodedOligomers(Map<String,int[]> fixedDomains, Map<String,int[]> variableDomains, String[] oligomerNames, String[][] oligomerDomains){
        Map<String,int[]> encodedOligomers = new HashMap<>();

        //for each oligomer
        for (int i : IntStream.range(0, oligomerNames.length).toArray()){
            String oligomer = oligomerNames[i];
            String[] domainStrings = oligomerDomains[i];
            ArrayList<int[]> encodedSequences = new ArrayList<>();
            
            int totalLength = 0;
            for( String domain : domainStrings){
                //if domain is a complement
                if (domain.startsWith("c.")){
                    String compName = domain.substring(2);
                    int[] domainSequence = fixedDomains.get(compName);
                    if (domainSequence == null ) domainSequence = variableDomains.get(compName);
                    if (domainSequence == null ) {
                        System.out.println("Could not find complement of domain "+ domain +"." );
                        System.exit(0);
                    }
                    int[] complementSequence = getComplement(domainSequence);
                    encodedSequences.add(complementSequence);
                    totalLength += complementSequence.length;
                } else {
                    int[] domainSequence = fixedDomains.get(domain);
                    if (domainSequence == null ) domainSequence = variableDomains.get(domain);
                    if (domainSequence == null ) {
                        System.out.println("Could not find domain "+ domain +"." );
                        System.exit(0);
                    }
                    encodedSequences.add(domainSequence);
                    totalLength += domainSequence.length;
                }
            }
            int[] encodedOligomer = new int[totalLength];
            int nextBase = 0;
            for (int[] sequence : encodedSequences){
                System.arraycopy(sequence, 0, encodedOligomer, nextBase, sequence.length);
                nextBase += sequence.length;
            }
            encodedOligomers.put(oligomer,encodedOligomer);
        }
        return encodedOligomers;
    }
    
    private static int[] getComplement(int[] encodedSequence){
        int[] retSequence = new int[encodedSequence.length];
        IntStream.range(0,encodedSequence.length).forEach(i-> retSequence[i] = -encodedSequence[encodedSequence.length-i-1]);
        return retSequence;
    }
    
    private static Map<String,int[]> getUniquelyEncodedDomains(String[] names, String[] sequences){
        Map<String, int[]> uniqueDomains = new HashMap<>();
        int currentBase = 3; 
        for(int i : IntStream.range(0, names.length).toArray()){
            int length = sequences[i].trim().length();
            int[] newV = IntStream.range(currentBase,currentBase+length).toArray();
            currentBase = currentBase+length;
            uniqueDomains.put(names[i], newV);
        }
        return uniqueDomains;
    }
    
    private static Map<Integer,Integer> getCompleteProfile( Map<Integer,Integer> uniqueProfile, int SLC){
        Map<Integer,Integer> ret = new HashMap<>();
        for(Map.Entry<Integer,Integer> entry : uniqueProfile.entrySet()){
            int length = entry.getKey();
            int count = entry.getValue();
            for(int i = 0; (i<length) && (length-i >= SLC); i++){
                ret.merge(length-i,count*(i+1),(x,y)->x+y);
            }
        }
        return ret;
    }
    
    private static Map<Integer,Integer> getProfileDifference(Map<Integer,Integer> uniqueProfile1, Map<Integer,Integer> uniqueProfile2, int SLC){
        Map<Integer,Integer> ret = new HashMap<>(uniqueProfile1);
        for(Map.Entry<Integer,Integer> entry : uniqueProfile2.entrySet()){
            int length = entry.getKey();
            int count = entry.getValue();
            int newValue = ret.getOrDefault(length,0)-count;
            if ((newValue == 0) && (length == SLC)){
                ret.remove(length);
            } else {
                ret.put(length, newValue);
            }
            
        }
        return ret;
    }
    
    private class Property{
        String label;
        String description;
        String[] neededParameters;
        String[] neededProperties;
        
        // defaultRequested = is requested by default.
        Property (String description, String label, String[] neededParameters, String[] neededProperties){
            this.label = label;
            this.description = description;
            this.neededParameters = neededParameters;
            this.neededProperties = neededProperties;
        }
        
        public String getDescription(){
            return description;
        }
        
        public String getLabel(){
            return label;
        }
        
        public String[] getNeededParameters(){
            return neededParameters;
        }
        
        public String[] getNeededProperties(){
            return neededProperties;
        }
        
        public void printLine(PrintStream PS, String value){
            PS.println();
            PS.print(label+" "+value);
        }
    }
    
    private interface Parameter{
        public String getDescription();
        public String getLabel();
        boolean isValid(String value);
    }
    
    private class IntegerParameter implements Parameter{
        String description;
        String label;
        int minValue;
        int maxValue;
        
        IntegerParameter (String description, String label, int minValue, int maxValue){
            this.label = label;
            this.description = description;
            this.minValue = minValue;
            this.maxValue = maxValue;
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
    
    static private class Duplexes{
        ArrayList<Integer> indexesO1;
        ArrayList<Integer> indexesO1B1;
        ArrayList<Integer> indexesO2;
        ArrayList<Integer> indexesO2B1;
        ArrayList<Integer> lengths;
        Duplexes(){
            this.indexesO1 = new ArrayList<>();
            this.indexesO1B1 = new ArrayList<>();
            this.indexesO2 = new ArrayList<>();
            this.indexesO2B1 = new ArrayList<>();
            this.lengths = new ArrayList<>();
        }
        
        Duplexes(Duplexes d1, Duplexes d2){
            this.indexesO1 = new ArrayList<>(d1.indexesO1);
            this.indexesO1.addAll(d2.indexesO1);
            this.indexesO1B1 = new ArrayList<>(d1.indexesO1B1);
            this.indexesO1B1.addAll(d2.indexesO1B1);
            this.indexesO2 = new ArrayList<>(d1.indexesO2);
            this.indexesO2.addAll(d2.indexesO2);
            this.indexesO2B1 = new ArrayList<>(d1.indexesO2B1);
            this.indexesO2B1.addAll(d2.indexesO2B1);
            this.lengths = new ArrayList<>(d1.lengths);
            this.lengths.addAll(d2.lengths);
        }
        
        void addDuplex( int indexO1, int indexO1B1, int indexO2, int indexO2B1, int length){
            indexesO1.add(indexO1);
            indexesO2.add(indexO2);
            indexesO1B1.add(indexO1B1);
            indexesO2B1.add(indexO2B1);
            lengths.add(length);
        }
        
        @Override
        public String toString(){
            StringBuilder valueString = new StringBuilder();
            valueString.append("indexO1 indexO1B1 indexO2 indexO2B1 base-pairs");
            
            Iterator<Integer> i0 = lengths.iterator();
            Iterator<Integer> i1 = indexesO1.iterator();
            Iterator<Integer> i2 = indexesO2.iterator();
            Iterator<Integer> i3 = indexesO1B1.iterator();
            Iterator<Integer> i4 = indexesO2B1.iterator();
            
            int skips = 1;
            while (i0.hasNext()){
                Integer length = i0.next();
                Integer indexO1 = i1.next();
                Integer indexO2 = i2.next();
                Integer indexO1B1 = i3.next();
                Integer indexO2B1 = i4.next();
                valueString.append(System.lineSeparator()+indexO1+" "+indexO1B1+" "+indexO2+" "+indexO2B1+" "+length);
            }
            return valueString.toString();
        }
        
    }
    
    private static class LargestDuplexes{
        int capacity;
        int minLength;
        LinkedList<Integer> indexesO1;
        LinkedList<Integer> indexesO1B1;
        LinkedList<Integer> indexesO2;
        LinkedList<Integer> indexesO2B1;
        LinkedList<Integer> lengths;
        
        LargestDuplexes(int capacity){
            this.capacity = capacity;
            this.indexesO1 = new LinkedList<>();
            this.indexesO1B1 = new LinkedList<>();
            this.indexesO2 = new LinkedList<>();
            this.indexesO2B1 = new LinkedList<>();
            this.lengths = new LinkedList<>();
            this.minLength = 0;
        }
        
        LargestDuplexes(LargestDuplexes d1, LargestDuplexes d2){
            this.capacity = Math.min(d1.capacity, d2.capacity);
            this.indexesO1 = new LinkedList<>(d1.indexesO1);
            this.indexesO1B1 = new LinkedList<>(d1.indexesO1B1);
            this.indexesO2 = new LinkedList<>(d1.indexesO2);
            this.indexesO2B1 = new LinkedList<>(d1.indexesO2B1);
            this.lengths = new LinkedList<>(d1.lengths);
            // add each member of d2.
            for(int i=0; i < d2.lengths.size(); i++){
                this.addDuplex(d2.indexesO1.get(i), d2.indexesO1B1.get(i),d2.indexesO2.get(i), d2.indexesO2B1.get(i),d2.lengths.get(i));
            }
        }
        
        void addDuplex( int indexO1, int indexO1B1, int indexO2, int indexO2B1, int length){
            if (length <= minLength){
                return;
            } else {
                int firstLesserIndex = lengths.size(); 
                for(int i : IntStream.range(0, lengths.size()).toArray()){
                    if (lengths.get(i) < length){
                        firstLesserIndex = i;
                        break;
                    }
                }
                indexesO1.add(indexO1,firstLesserIndex);
                indexesO2.add(indexO2,firstLesserIndex);
                indexesO2B1.add(indexO2B1, firstLesserIndex);
                indexesO1B1.add(indexO1B1, firstLesserIndex);
                lengths.add(length, firstLesserIndex);
                    
                if (lengths.size() > capacity){
                    indexesO1.removeLast();
                    indexesO2.removeLast();
                    indexesO1B1.removeLast();
                    indexesO2B1.removeLast();
                    lengths.removeLast();
                    minLength = lengths.getLast();
                } else {
                    minLength = 0;
                }
            }
        }
        
        @Override
        public String toString(){
            StringBuilder valueString = new StringBuilder();
            valueString.append("indexO1 indexO1B1 indexO2 indexO2B1 base-pairs");
            
            Iterator<Integer> i0 = lengths.iterator();
            Iterator<Integer> i1 = indexesO1.iterator();
            Iterator<Integer> i2 = indexesO2.iterator();
            Iterator<Integer> i3 = indexesO1B1.iterator();
            Iterator<Integer> i4 = indexesO2B1.iterator();
            
            while (i0.hasNext()){
                Integer length = i0.next();
                Integer indexO1 = i1.next();
                Integer indexO2 = i2.next();
                Integer indexO1B1 = i3.next();
                Integer indexO2B1 = i4.next();
                valueString.append(System.lineSeparator()+indexO1+" "+indexO1B1+" "+indexO2+" "+indexO2B1+" "+length);
            }
            return valueString.toString();
        }
    }
}
