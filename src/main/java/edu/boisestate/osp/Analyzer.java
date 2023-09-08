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

import edu.boisestate.osp.networks.IDomainBasedEncodedNetwork;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    public final static String NUMBER_LARGEST_DUPLEXES_LABEL = "numberLargestDuplexes";
    
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
    
    //  File and property label naming convention is:
    //  Duplex category 1 (a label can have up to one letter from this set)
    //      (N) Necessary duplexes only
    //      (U) Unnecessary duplexes only
    //  Duplex category 2 (a label needs exactly one letter from this set)
    //      (A) Intra-Oligomer duplexes only
    //      (E) Inter-Oligomer duplexes only
    //  Duplex category 3 (a label can have up to one letter from this set)
    //      (L) Largest prominent duplexes only
    //      (P) Prominent duplexes only (i.e., those which are not part of a larger duplex)
    //  Property type (a label needs exactly one letter from this set)
    //      (D) Details of duplex location and base-sequence
    //      (C) Counts of the duplexes of each length

    final static String AC_LABEL = "Intra_Counts";
    final static String ALD_LABEL = "Intra_Largest_Details"; // List of largest unique intra-oligomer duplexes.
    final static String APC_LABEL = "Intra_Prominent_Counts";
    final static String APD_LABEL = "Intra_Prominent_Details"; // List of all unique intra-oligomer duplexes.
    final static String EC_LABEL = "Inter_Counts";
    final static String ELD_LABEL = "Inter_Largest_Details"; // List of largest unique intra-oligomer duplexes.
    final static String EPC_LABEL = "Inter_Prominent_Counts";
    final static String EPD_LABEL = "Inter_Prominent_Details"; // List of all unique intra-oligomer duplexes.
    final static String NAC_LABEL = "Necessary_Intra_Counts"; 
    final static String NALD_LABEL = "Necessary_Intra_Largest_Details"; // List of largest baseline unique intra-oligomer duplexes.
    final static String NAPC_LABEL = "Necessary_Intra_Prominent_Counts";
    final static String NAPD_LABEL = "Necessary_Intra_Prominent_Details"; // List of baseline unique intra-oligomer duplexes.
    final static String NEC_LABEL = "Necessary_Inter_Counts"; 
    final static String NELD_LABEL = "Necessary_Inter_Largest_Details"; // List of largest baseline unique intra-oligomer duplexes.
    final static String NEPC_LABEL = "Necessary_Inter_Prominent_Counts"; 
    final static String NEPD_LABEL = "Necessary_Inter_Prominent_Details"; // List of baseline unique intra-oligomer duplexes.
    final static String UAC_LABEL = "Unnecessary_Intra_Counts"; // Profile baseline complete intra
    final static String UALD_LABEL = "Unnecessary_Intra_Largest_Details"; // List of largest baseline unique intra-oligomer duplexes.
    final static String UAPC_LABEL = "Unnecessary_Intra_Prominent_Counts"; // Profile baseline Unique intra
    final static String UAPD_LABEL = "Unnecessary_Intra_Prominent_Details"; // List of baseline unique intra-oligomer duplexes.
    final static String UEC_LABEL = "Unnecessary_Inter_Counts"; // Profile baseline complete inter
    final static String UELD_LABEL = "Unnecessary_Inter_Largest_Details"; // List of largest baseline unique intra-oligomer duplexes.
    final static String UEPC_LABEL = "Unnecessary_Inter_Prominent_Counts"; // Profile baseline Unique inter
    final static String UEPD_LABEL = "Unnecessary_Inter_Prominent_Details"; // List of baseline unique intra-oligomer duplexes.
    
    final static ArrayList<Parameter> availableParameters = new ArrayList<>();
    static {
        availableParameters.add(new IntegerParameter("Inter-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex.", INTER_SB_LABEL,0,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter("Inter-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores.", INTER_SLC_LABEL,1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter("Intra-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex.", INTRA_SB_LABEL,0,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter("Intra-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores.", INTRA_SLC_LABEL,1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter("Maximum number of duplexes to include when listing largest-duplexes.", NUMBER_LARGEST_DUPLEXES_LABEL,1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter("W will be calculated as O times this value plus N.", SWX_LABEL,0,Integer.MAX_VALUE));
    }
    
    final static Map<String,Parameter> labelToParameterMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); 
    static {
        for (Parameter p : availableParameters){
            labelToParameterMap.put(p.getLabel(), p);
        }
    }
    
    final static ArrayList<Property> availableProperties = new ArrayList<>();
    static {
        availableProperties.add(new Property("Network Fitness Score resulting from necessary duplexes.", BASELINE_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL,NEPC_LABEL}));
        availableProperties.add(new Property("Oligomer Fitness Score resulting from necessary duplexes.", BASELINE_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL,NAPC_LABEL}));
        availableProperties.add(new Property("Weighted Fitness Score resulting from necessary duplexes..", BASELINE_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL,NAPC_LABEL,NEPC_LABEL}));
        availableProperties.add(new Property("Network Fitness Score resulting from unnecessary duplexes.", DELTA_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL, DELTA_N_LABEL, N_LABEL}));
        availableProperties.add(new Property("Oligomer Fitness Score resulting from unnecessary duplexes.", DELTA_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL,DELTA_O_LABEL, O_LABEL}));
        availableProperties.add(new Property("Weighted Fitness Score resulting from unnecessary duplexes.", DELTA_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL,DELTA_N_LABEL,DELTA_O_LABEL,DELTA_W_LABEL,N_LABEL,O_LABEL,W_LABEL,EC_LABEL,AC_LABEL,NAPC_LABEL,NEPC_LABEL}));
        availableProperties.add(new Property("Network Fitness Score.", N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{N_LABEL,EPC_LABEL}));
        availableProperties.add(new Property("Oligomer Fitness Score.", O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{O_LABEL,APC_LABEL}));
        availableProperties.add(new Property("Weighted Fitness Score.", W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{N_LABEL,O_LABEL,W_LABEL,EC_LABEL,AC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for all intra-oligomer duplexes.", AC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{AC_LABEL,APC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for all inter-oligomer duplexes.", EC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{EC_LABEL,EPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for the most prominent intra-oligomer duplexes.", APC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{APC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for the most prominent inter-oligomer duplexes.", EPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{EPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for necessary intra-oligomer duplexes.", NAC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{NAC_LABEL,NAPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for necessary inter-oligomer duplexes.", NEC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{NEC_LABEL,NEPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for the most prominent necessary intra-oligomer duplexes.", NAPC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{NAPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for the most prominent necessary inter-oligomer duplexes.", NEPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{NEPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for unnecessary intra-oligomer duplexes.", UAC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{UAC_LABEL,UAPC_LABEL,APC_LABEL,NAPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for unnecessary inter-oligomer duplexes.", UEC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{UEC_LABEL,UEPC_LABEL,EPC_LABEL,NEPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes.", UAPC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{UAPC_LABEL,APC_LABEL,NAPC_LABEL}));
        availableProperties.add(new Property("Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes.", UEPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{UEPC_LABEL,EPC_LABEL,NEPC_LABEL}));
        availableProperties.add(new Property("List of all intra-oligomer duplexes.", APD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{APD_LABEL}));
        availableProperties.add(new Property("List of all inter-oligomer duplexes.", EPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{EPD_LABEL}));
        availableProperties.add(new Property("List of necessary intra-oligomer duplexes.", NAPD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{NAPD_LABEL}));
        availableProperties.add(new Property("List of necessary inter-oligomer duplexes.", NEPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{NEPD_LABEL}));
        availableProperties.add(new Property("List of unnecessary intra-oligomer duplexes.", UAPD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{UAPD_LABEL}));
        availableProperties.add(new Property("List of unnecessary inter-oligomer duplexes.", UEPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{UEPD_LABEL}));
        availableProperties.add(new Property("List of the largest intra-oligomer duplexes.", ALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{ALD_LABEL}));
        availableProperties.add(new Property("List of the largest inter-oligomer duplexes.", ELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{ELD_LABEL}));
        availableProperties.add(new Property("List of the largest necessary intra-oligomer duplexes.", NALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{NALD_LABEL}));
        availableProperties.add(new Property("List of the largest necessary inter-oligomer duplexes.", NELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{NELD_LABEL}));
        availableProperties.add(new Property("List of the largest unnecessary intra-oligomer duplexes.", UALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{UALD_LABEL}));
        availableProperties.add(new Property("List of the largest unnecessary inter-oligomer duplexes.", UELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{UELD_LABEL}));        
}
    
    final static Map<String,Property> labelToPropertyMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    static {
        for (Property p : availableProperties){
            labelToPropertyMap.put(p.getLabel(), p);
        }
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
            Property p = labelToPropertyMap.get(propertyLabel);
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
            Parameter p = labelToParameterMap.get(parameterLabel);
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
        if (neededProperties.contains(NAPC_LABEL)){
            pbua = as.getBaselineIntraDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbua.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(NAPC_LABEL, valueString.toString());
        }
        
        // if baseline inter duplexes is required calculate it.
        if (neededProperties.contains(NEPC_LABEL)){
            pbue = as.getBaselineInterDuplexCount(r.network, Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbue.entrySet()){
                valueString.append(entry.getKey()).append(" ").append(entry.getValue()).append(System.lineSeparator());
            }
            calculatedPropertyValues.put(NEPC_LABEL, valueString.toString());
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
        if (neededProperties.contains(APC_LABEL)){
            paua = as.getIntraDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : paua.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(APC_LABEL, valueString.toString());
        }
        
        // if interduplexes is required, calculate it.
        if (neededProperties.contains(EPC_LABEL)){
            paue = as.getInterDuplexCount(r.network, Integer.valueOf(usedParameters.get(INTER_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : paue.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(EPC_LABEL, valueString.toString());
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
        if (neededProperties.contains(AC_LABEL)){
            paca = getCompleteProfile(paua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : paca.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(AC_LABEL, valueString.toString());
        }
        
        //PACE
        if (neededProperties.contains(EC_LABEL)){
            pace = getCompleteProfile(paue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pace.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(EC_LABEL, valueString.toString());
        }
        
        //PBCA
        if (neededProperties.contains(NAC_LABEL)){
            pbca = getCompleteProfile(pbua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbca.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(NAC_LABEL, valueString.toString());
        }
        
        //PBCE
        if (neededProperties.contains(NEC_LABEL)){
            pbce = getCompleteProfile(pbue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pbce.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(NEC_LABEL, valueString.toString());
        }
        
        //PDUA
        if (neededProperties.contains(UAPC_LABEL)){
            pdua = getProfileDifference(paua,pbua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdua.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(UAPC_LABEL, valueString.toString());
        }
        
        //PDUE
        if (neededProperties.contains(UEPC_LABEL)){
            pdue = getProfileDifference(paue,pbue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdue.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(UEPC_LABEL, valueString.toString());
        }
        
        //PDCA
        if (neededProperties.contains(UAC_LABEL)){
            pdca = getCompleteProfile(pdua,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdca.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(UAC_LABEL, valueString.toString());
        }
        
        //PDCE
        if (neededProperties.contains(UEC_LABEL)){
            pdce = getCompleteProfile(pdue,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            
            StringBuilder valueString = new StringBuilder();
            for(Map.Entry<Integer,Integer> entry : pdce.entrySet()){
                valueString.append(entry.getKey()+" "+entry.getValue()+System.lineSeparator());
            }
            calculatedPropertyValues.put(UEC_LABEL, valueString.toString());
        }
        
        //DAUA
        Duplexes daua = null;
        if (neededProperties.contains(APD_LABEL)){
            daua = as.getIntraDuplexes(r.network,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(APD_LABEL, daua.toString());
        }
        
        //DAUE
        Duplexes daue = null;
        if (neededProperties.contains(EPD_LABEL)){
            daue = as.getInterDuplexes(r.network,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(EPD_LABEL, daue.toString());
        }
        
        //DBUA
        Duplexes dbua = null;
        if (neededProperties.contains(NAPD_LABEL)){
            dbua = as.getBaselineIntraDuplexes(r.network,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(NAPD_LABEL, dbua.toString());
        }
        
        //DBUE
        Duplexes dbue = null;
        if (neededProperties.contains(NEPD_LABEL)){
            dbue = as.getBaselineInterDuplexes(r.network,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(NEPD_LABEL, dbue.toString());
        }
        
        //DDUA
        Duplexes ddua = null;
        if (neededProperties.contains(UAPD_LABEL)){
            ddua = as.getDeltaIntraDuplexes(r.network,Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)));
            calculatedPropertyValues.put(UAPD_LABEL, ddua.toString());
        }
        
        //DDUE
        Duplexes ddue = null;
        if (neededProperties.contains(UEPD_LABEL)){
            ddue = as.getDeltaInterDuplexes(r.network,Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)));
            calculatedPropertyValues.put(UEPD_LABEL, ddue.toString());
        }
        
        //LDAUA
        LargestDuplexes ldaua = null;
        if (neededProperties.contains(ALD_LABEL)){
            ldaua = as.getLargestIntraDuplexes(r.network, Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)), Integer.parseInt(usedParameters.get(NUMBER_LARGEST_DUPLEXES_LABEL)));
            calculatedPropertyValues.put(ALD_LABEL, ldaua.toString());
        }
        //LDAUE
        LargestDuplexes ldaue = null;
        if (neededProperties.contains(ELD_LABEL)){
            ldaue = as.getLargestInterDuplexes(r.network, Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)), Integer.parseInt(usedParameters.get(NUMBER_LARGEST_DUPLEXES_LABEL)));
            calculatedPropertyValues.put(ELD_LABEL, ldaue.toString());
        }
        
        //LDBUA
        LargestDuplexes ldbua = null;
        if (neededProperties.contains(NALD_LABEL)){
            ldbua = as.getLargestBaselineIntraDuplexes(r.network, Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)), Integer.parseInt(usedParameters.get(NUMBER_LARGEST_DUPLEXES_LABEL)));
            calculatedPropertyValues.put(NALD_LABEL, ldbua.toString());
        }
        //LDBUE
        LargestDuplexes ldbue = null;
        if (neededProperties.contains(NELD_LABEL)){
            ldbue = as.getLargestBaselineInterDuplexes(r.network, Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)), Integer.parseInt(usedParameters.get(NUMBER_LARGEST_DUPLEXES_LABEL)));
            calculatedPropertyValues.put(NELD_LABEL, ldbue.toString());
        }
        
        //LDDUA
        LargestDuplexes lddua = null;
        if (neededProperties.contains(UALD_LABEL)){
            lddua = as.getLargestDeltaIntraDuplexes(r.network, Integer.parseInt(usedParameters.get(INTRA_SLC_LABEL)), Integer.parseInt(usedParameters.get(NUMBER_LARGEST_DUPLEXES_LABEL)));
            calculatedPropertyValues.put(UALD_LABEL, lddua.toString());
        }
        //LDBUE
        LargestDuplexes lddue = null;
        if (neededProperties.contains(UELD_LABEL)){
            lddue = as.getLargestDeltaInterDuplexes(r.network, Integer.parseInt(usedParameters.get(INTER_SLC_LABEL)), Integer.parseInt(usedParameters.get(NUMBER_LARGEST_DUPLEXES_LABEL)));
            calculatedPropertyValues.put(UELD_LABEL, lddue.toString());
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
            
            Duplexes duplexes = new Duplexes();

            int S1length;
            int b1Max;
            int encodedStructureLength;
            int b1;
            int lastB1=0;
            int b2;
            int lastB2=0;
            int length;
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
                        duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
                    };
                }
            }
            
            return duplexes;
        }
        
        Duplexes getDeltaInterDuplexes(IDomainBasedEncodedNetwork network, int interSLC){
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
            Future<Duplexes>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new DeltaInterDuplexesRequest(baselineEncodedOligomers, encodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new DeltaInterDuplexesRequest(baselineEncodedOligomers, encodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC));
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
        
        Duplexes getDeltaIntraDuplexes(IDomainBasedEncodedNetwork network, int intraSLC){
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
            Duplexes duplexes = new Duplexes();
            //Map<Integer,Integer> lengthCounts = new HashMap<>();
            //for each alignment

            int S1length;
            int b1Max;
            int baselineStructureLength;
            int encodedStructureLength;
            int b1;
            int lastB1=0;
            int b2;
            int lastB2=0;
            int length;
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

//                    if(encodedOligomers[i][b1] + encodedOligomers[i][b2] ==0){
//                        encodedStructureLength = 1;
//                    }
                    
//                    if(baselineEncodedOligomers[i][b1] + baselineEncodedOligomers[i][b2] ==0){
//                        baselineStructureLength = 1;
//                    }

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
                                    duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
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
                            lastB1 = b1;
                            b1 = 0;
                            encodedStructureLength = 0;
                            baselineStructureLength = 0;
                        } else {
                            lastB1 = b1;
                            b1++;
                        }

                        if( b2 == 0){
                                if (encodedStructureLength >= intraSLC){
                                    if(encodedStructureLength > baselineStructureLength){
                                        duplexes.addDuplex(i,b1-encodedStructureLength+1, i, b2,encodedStructureLength);
                                    }
                                }
                                lastB2 = b2;
                                b2 = b1Max;
                                encodedStructureLength = 0;
                                baselineStructureLength = 0;
                        } else {
                            lastB2 = b2;
                            b2--;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (encodedStructureLength >= intraSLC)
                    {
                        if (encodedStructureLength >= intraSLC){
                            if(encodedStructureLength > baselineStructureLength){
                                duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
                            }
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
            int lastB1=0;
            int b2;
            int lastB2=0;
            int length;
            // for each oligomer
            for(int i : IntStream.range(0,encodedOligomers.length).toArray()){
                S1 = encodedOligomers[i];
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
                    
                    for ( int k =0; k < length; k++)
                    {
                        //compare current base-pair. 
                        if (encodedOligomers[i][b1]+encodedOligomers[i][b2]==0){
                            structureLength++;
                        } else {
                            if ( structureLength >= intraSLC){
                                duplexes.addDuplex(i,lastB1-structureLength+1, i, lastB2,structureLength);
                            }
                            structureLength = 0;
                        }
                        
                        //itterate after.
                        if( b1 == b1Max){
                            if (structureLength >= intraSLC){
                                    duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                            }
                            lastB1 = b1;
                            b1 = 0;
                            structureLength = 0;
                        } else {
                            lastB1 = b1;
                            b1++;
                        }

                        if( b2 == 0){ 
                            if (structureLength >= intraSLC){
                                duplexes.addDuplex(i,b1-structureLength+1, i, b2,structureLength);
                            }
                            lastB2 = b2;
                            b2 = b1Max;
                            structureLength = 0;
                        } else {
                            lastB2 = b2;
                            b2--;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (structureLength >= intraSLC)
                    {
                        if (structureLength >= intraSLC){
                            duplexes.addDuplex(i,lastB1-structureLength+1, i, lastB2,structureLength);
                        }
                    };
                }
            }
            
            return duplexes;
        }
        
        LargestDuplexes getLargestBaselineInterDuplexes(IDomainBasedEncodedNetwork network, int interSLC, int numberDuplexes){
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
            Future<LargestDuplexes>[] futures = new Future[lastIndexesArray.length];
            futures[0] = es.submit(new LargestInterDuplexesRequest(baselineEncodedOligomers, AnalysisSupervisor.this, combos, 0, lastIndexesArray[0], interSLC, numberDuplexes));
            for(int i:indexList){
                int firstIndex = lastIndexesArray[i-1];
                int lastIndex = lastIndexesArray[i];
                futures[i] = es.submit(new LargestInterDuplexesRequest(baselineEncodedOligomers, AnalysisSupervisor.this, combos, firstIndex, lastIndex, interSLC, numberDuplexes));
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
        
        LargestDuplexes getLargestBaselineIntraDuplexes(IDomainBasedEncodedNetwork network, int intraSLC, int numberDuplexes){
            Map<String,int[]> efd = encode(network.getFixedDomainNames(), network.getFixedDomainSequences()); // encoded fixed domains
            Map<String,int[]> uevd = getUniquelyEncodedDomains(network.getVariableDomainNames(), network.getVariableDomainSequences()); // uniequely encoded initial variable domains
            Map<String,int[]> ueo = assembleEncodedOligomers(efd, uevd, network.getOligomerNames(), network.getOligomerDomains());
            int[][] baselineEncodedOligomers = new int[ueo.size()][];
            for(Map.Entry<String,Integer> entry: network.getOligomerIndices().entrySet()){
                baselineEncodedOligomers[entry.getValue()] = ueo.get(entry.getKey());
            }
            int[][] encodedOligomers = baselineEncodedOligomers;
            
            LargestDuplexes duplexes = new LargestDuplexes(numberDuplexes);

            int S1length;
            int b1Max;
            int encodedStructureLength;
            int b1;
            int lastB1=0;
            int b2;
            int lastB2=0;
            int length;
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
                        duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
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
            int lastB1=0;
            int b2;
            int lastB2=0;
            int length;
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

//                    if(encodedOligomers[i][b1] + encodedOligomers[i][b2] ==0){
//                        encodedStructureLength = 1;
//                    }
                    
//                    if(baselineEncodedOligomers[i][b1] + baselineEncodedOligomers[i][b2] ==0){
//                        baselineStructureLength = 1;
//                    }

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
                                    duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
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
                            lastB1 = b1;
                            b1 = 0;
                            encodedStructureLength = 0;
                            baselineStructureLength = 0;
                        } else {
                            lastB1 = b1;
                            b1++;
                        }

                        if( b2 == 0){
                                if (encodedStructureLength >= intraSLC){
                                    if(encodedStructureLength > baselineStructureLength){
                                        duplexes.addDuplex(i,b1-encodedStructureLength+1, i, b2,encodedStructureLength);
                                    }
                                }
                                lastB2 = b2;
                                b2 = b1Max;
                                encodedStructureLength = 0;
                                baselineStructureLength = 0;
                        } else {
                            lastB2 = b2;
                            b2--;
                        }
                    }

                    //if the loop ended with an active structure, record it.
                    if (encodedStructureLength >= intraSLC)
                    {
                        if (encodedStructureLength >= intraSLC){
                            if(encodedStructureLength > baselineStructureLength){
                                duplexes.addDuplex(i,lastB1-encodedStructureLength+1, i, lastB2,encodedStructureLength);
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
            int lastB1 = 0;
            int b2;
            int lastB2 = 0;
            int length;
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
                                lastB1=b1;
                                b1 = 0;
                                structureLength = 0;
                        } else {
                            lastB1=b1;
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
                                lastB2 = b2;
                                b2 = b1Max;
                                structureLength = 0;
                        } else {
                            lastB2=b2;
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
                                duplexes.addDuplex(i,lastB1-structureLength+1, i, lastB2,structureLength);
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
        
        static private class DeltaInterDuplexesRequest implements Callable<Duplexes>{
            final Combination[] combinations;
            final int firstIndex;
            final int lastIndex;
            final int[][] baselineEncodedOligomers;
            final int[][] encodedOligomers;
            final AnalysisSupervisor as;
            final int interSLC;
            //Map<Integer,AtomicInteger> lengthCounts = new HashMap<>();
            
            DeltaInterDuplexesRequest(int[][] baselineEncodedOligomers, int[][] encodedOligomers, AnalysisSupervisor analysisSupervisor, Combination[] combinations, int firstIndex, int lastIndex, int interSLC){
                this.baselineEncodedOligomers = baselineEncodedOligomers;
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
                int baselineComparison;
                int encodedComparison;
                int baselineStructureLength;
                int encodedStructureLength;
                BasePair[][] allBP = null;
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
                                        duplexes.addDuplex(indexS1,lastBP.index1-encodedStructureLength+1, indexS2, lastBP.index2,encodedStructureLength);
                                    }
                                    //lengthCounts[structureLength]++;
                                }
                                encodedStructureLength = 0;
                                baselineStructureLength = 0;
                            }
                            lastBP = bp;
                        }
                        if (encodedStructureLength >= interSLC){
                            if (encodedStructureLength != baselineStructureLength){
                                duplexes.addDuplex(indexS1,lastBP.index1-encodedStructureLength+1, indexS2, lastBP.index2,encodedStructureLength);
                            }
                            //lengthCounts[structureLength]++;
                        }
                    }
                }
                return duplexes;
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
                BasePair[][] allBP = null;
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
                                        duplexes.addDuplex(indexS1,lastBP.index1-encodedStructureLength+1, indexS2, lastBP.index2,encodedStructureLength);
                                    }
                                    //lengthCounts[structureLength]++;
                                }
                                encodedStructureLength = 0;
                                baselineStructureLength = 0;
                            }
                            lastBP = bp;
                        }
                        if (encodedStructureLength >= interSLC){
                            if (encodedStructureLength != baselineStructureLength){
                                duplexes.addDuplex(indexS1,lastBP.index1-encodedStructureLength+1, indexS2, lastBP.index2,encodedStructureLength);
                            }
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
                //int[] lengthCounts = new int[maxLength+1];
                LargestDuplexes duplexes = new LargestDuplexes(numberDuplexes);

                int indexS1;
                int indexS2;
                int structureLength;
                BasePair[][] allBP = null;
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
            if (newValue == 0){
                if (length != SLC){
                    ret.remove(length);
                } else {
                    ret.put(length,newValue);
                }
            } else {
                ret.put(length, newValue);
            }
        }
        return ret;
    }
    
    static private class Property{
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
    
    static private interface Parameter{
        public String getDescription();
        public String getLabel();
        boolean isValid(String value);
    }
    
    private static class IntegerParameter implements Parameter{
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
    
    private static class Duplexes{
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
            valueString.append("base-pairs indexO1 indexO1B1 indexO2 indexO2B1");
            
            Iterator<Integer> i0 = lengths.iterator();
            Iterator<Integer> i1 = indexesO1.iterator();
            Iterator<Integer> i2 = indexesO1B1.iterator();
            Iterator<Integer> i3 = indexesO2.iterator();
            Iterator<Integer> i4 = indexesO2B1.iterator();
            
            while (i0.hasNext()){
                Integer length = i0.next();
                Integer indexO1 = i1.next();
                Integer indexO1B1 = i2.next();
                Integer indexO2 = i3.next();
                Integer indexO2B1 = i4.next();
                valueString.append(System.lineSeparator()+length+" "+indexO1+" "+indexO1B1+" "+indexO2+" "+indexO2B1);
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
            Iterator<Integer> i0 = d2.indexesO1.iterator();
            Iterator<Integer> i1 = d2.indexesO1B1.iterator();
            Iterator<Integer> i2 = d2.indexesO2.iterator();
            Iterator<Integer> i3 = d2.indexesO2B1.iterator();
            Iterator<Integer> i4 = d2.lengths.iterator();
            
            while (i0.hasNext()){
                this.addDuplex(i0.next(), i1.next(),i2.next(), i3.next(),i4.next());
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
                indexesO1.add(firstLesserIndex, indexO1);
                indexesO1B1.add(firstLesserIndex,indexO1B1);
                indexesO2.add(firstLesserIndex,indexO2);
                indexesO2B1.add(firstLesserIndex,indexO2B1);
                lengths.add(firstLesserIndex,length);
                    
                if (lengths.size() > capacity){
                    indexesO1.removeLast();
                    indexesO1B1.removeLast();
                    indexesO2.removeLast();
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
            valueString.append("base-pairs indexO1 indexO1B1 indexO2 indexO2B1");
            
            Iterator<Integer> i0 = lengths.iterator();
            Iterator<Integer> i1 = indexesO1.iterator();
            Iterator<Integer> i2 = indexesO1B1.iterator();
            Iterator<Integer> i3 = indexesO2.iterator();
            Iterator<Integer> i4 = indexesO2B1.iterator();
            
            while (i0.hasNext()){
                Integer length = i0.next();
                Integer indexO1 = i1.next();
                Integer indexO1B1 = i2.next();
                Integer indexO2 = i3.next();
                Integer indexO2B1 = i4.next();
                valueString.append(System.lineSeparator()+length+" "+indexO1+" "+indexO1B1+" "+indexO2+" "+indexO2B1);
            }
            return valueString.toString();
        }
    }
}
