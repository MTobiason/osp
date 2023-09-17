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

import edu.boisestate.osp.coders.ICoder;
import edu.boisestate.osp.coders.Coder;
import edu.boisestate.osp.networks.FactoryDomainBasedEncodedNetwork;
import edu.boisestate.osp.networks.IDomainBasedEncodedNetwork;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class DevPro {
    final static String VERSION = "2.0";
    final int MAX_THREADS;
    final int MAX_THREADS_PER_NETWORK;
    
    // parameters stuff
    final static String PFP_LABEL = "PFP"; //parameters File Path
    final static String PFP_DEFAULT = "dp_parameters.txt";
    final static String EXAMPLE_PARAMETERS_FILE_DEFAULT = "dp_parameters_example.txt";
    
    final static String NAMING_CONVENTION_DETAILS = ""+
        "// Output files and properties are assigned 3-character labels based on the following rules." +
        "\n// \t\tThe term necessary refers to duplexes which are implied by either the domain-level-design or by fixed base-sequences." +
        "\n// \t\tThe term unnecessary describes all other duplexes." +
        "\n// \t\tThe term prominent refers to duplexes which are not part of a larger duplex." +
        "\n// \t\tThe term largest refers to the duplexes with the most base-pairs." +
        "\n// \tCharacter 1:" +
        "\n// \t\t(C) Counts of each length" +
        "\n// \t\t(D) Details of duplex location and base-sequence" +
        "\n// \t\t(L) Details of duplex location and base-sequence for the largest prominent duplexes" +
        "\n// \t\t(P) Counts of each length for prominent duplexes" +
        "\n// \t\t(S) Size of the largest duplex in base-pairs" +
        "\n// \tCharacter 2:" +
        "\n// \t\t(N) Necessary duplexes only" +
        "\n// \t\t(U) Unnecessary duplexes only" +
        "\n// \t\t(X) Both necessary and unnecessary" +
        "\n// \tCharacter 3:" +
        "\n// \t\t(A) Intra-Oligomer duplexes only" +
        "\n// \t\t(E) Inter-Oligomer duplexes only" +
        "\n// For example, the counts of unnecessary intra-oligomer duplexes are reported in the PROP_CUA property and OUT_CUA file.";
    
    final static String CXA_FILE_DEFAULT = "false";
    final static String CXA_FILE_LABEL = "OUT_CXA";
    final static String CXA_PROP_LABEL = "PROP_CXA";
    final static String CXA_PROP_DESCRIPTION = "Profile of the length-counts for all intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String LXA_FILE_DEFAULT = "false";
    final static String LXA_FILE_LABEL = "OUT_LXA";
    final static String LXA_PROP_LABEL = "PROP_LXA";
    final static String LXA_PROP_DESCRIPTION  = "List of the largest intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String PXA_FILE_DEFAULT = "false";
    final static String PXA_FILE_LABEL = "OUT_PXA";
    final static String PXA_PROP_LABEL = "PROP_PXA";
    final static String PXA_PROP_DESCRIPTION  = "Profile of the length-counts for the most prominent intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String DXA_FILE_DEFAULT = "false";
    final static String DXA_FILE_LABEL = "OUT_DXA";
    final static String DXA_PROP_LABEL = "PROP_DXA";
    final static String DXA_PROP_DESCRIPTION  = "List of all intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String CXE_FILE_DEFAULT = "false";
    final static String CXE_FILE_LABEL = "OUT_CXE";
    final static String CXE_PROP_LABEL = "PROP_CXE";
    final static String CXE_PROP_DESCRIPTION  = "Profile of the length-counts for all inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String LXE_FILE_DEFAULT = "false";
    final static String LXE_FILE_LABEL = "OUT_LXE"; 
    final static String LXE_PROP_LABEL = "PROP_LXE";
    final static String LXE_PROP_DESCRIPTION = "List of the largest inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String PXE_FILE_DEFAULT = "false";
    final static String PXE_FILE_LABEL = "OUT_PXE";
    final static String PXE_PROP_LABEL = "PROP_PXE";
    final static String PXE_PROP_DESCRIPTION = "Profile of the length-counts for the most prominent inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String DXE_FILE_DEFAULT = "false";
    final static String DXE_FILE_LABEL = "OUT_DXE"; 
    final static String DXE_PROP_LABEL = "PROP_DXE";
    final static String DXE_PROP_DESCRIPTION = "List of all inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String CNA_FILE_DEFAULT = "dp_out_Counts_Necessary_Intra.csv";
    final static String CNA_FILE_LABEL = "OUT_CNA";
    final static String CNA_PROP_LABEL = "PROP_CNA";
    final static String CNA_PROP_DESCRIPTION = "Profile of the length-counts for baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String LNA_FILE_DEFAULT = "dp_out_Details_Necessary_Intra_Largest.csv";
    final static String LNA_FILE_LABEL = "OUT_LNA";
    final static String LNA_PROP_LABEL = "PROP_LNA";
    final static String LNA_PROP_DESCRIPTION = "List of the largest baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String PNA_FILE_DEFAULT = "false";
    final static String PNA_FILE_LABEL = "OUT_PNA";
    final static String PNA_PROP_LABEL = "PROP_PNA";
    final static String PNA_PROP_DESCRIPTION = "Profile of the length-counts for the most prominent baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String DNA_FILE_DEFAULT = "false";
    final static String DNA_FILE_LABEL = "OUT_DNA";  
    final static String DNA_PROP_LABEL = "PROP_DNA";
    final static String DNA_PROP_DESCRIPTION = "List of baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String CNE_FILE_DEFAULT = "dp_out_Counts_Necessary_Inter.csv";
    final static String CNE_FILE_LABEL = "OUT_CNE";
    final static String CNE_PROP_LABEL = "PROP_CNE";
    final static String CNE_PROP_DESCRIPTION = "Profile of the length-counts for baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String LNE_FILE_DEFAULT = "dp_out_Details_Necessary_Inter_Largest.csv";
    final static String LNE_FILE_LABEL = "OUT_LNE"; 
    final static String LNE_PROP_LABEL = "PROP_LNE"; 
    final static String LNE_PROP_DESCRIPTION = "List of the largest baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String PNE_FILE_DEFAULT = "false";
    final static String PNE_FILE_LABEL = "OUT_PNE";
    final static String PNE_PROP_LABEL = "PROP_PNE";
    final static String PNE_PROP_DESCRIPTION = "Profile of the length-counts for the most prominent baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String DNE_FILE_DEFAULT = "false";
    final static String DNE_FILE_LABEL = "OUT_DNE"; 
    final static String DNE_PROP_LABEL = "PROP_DNE";
    final static String DNE_PROP_DESCRIPTION = "List of baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String CUA_FILE_DEFAULT = "dp_out_Counts_Unnecessary_Intra.csv";
    final static String CUA_FILE_LABEL = "OUT_CUA";
    final static String CUA_PROP_LABEL = "PROP_CUA";
    final static String CUA_PROP_DESCRIPTION = "Profile of the length-counts for unnecessary intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
        
    final static String LUA_FILE_DEFAULT = "dp_out_Details_Unnecessary_Intra_Largest.csv"; 
    final static String LUA_FILE_LABEL = "OUT_LUA"; 
    final static String LUA_PROP_LABEL = "PROP_LUA";
    final static String LUA_PROP_DESCRIPTION = "List of the largest unnecessary intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String PUA_FILE_DEFAULT = "false";
    final static String PUA_FILE_LABEL = "OUT_PUA";
    final static String PUA_PROP_LABEL = "PROP_PUA";
    final static String PUA_PROP_DESCRIPTION = "Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String DUA_FILE_DEFAULT = "false";
    final static String DUA_FILE_LABEL = "OUT_DUA"; 
    final static String DUA_PROP_LABEL = "PROP_DUA";
    final static String DUA_PROP_DESCRIPTION = "List of unnecessary intra-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String CUE_FILE_DEFAULT = "dp_out_Counts_Unnecessary_Inter.csv";
    final static String CUE_FILE_LABEL = "OUT_CUE";
    final static String CUE_PROP_LABEL = "PROP_CUE";
    final static String CUE_PROP_DESCRIPTION = "Profile of the length-counts for unnecessary inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String LUE_FILE_DEFAULT = "dp_out_Details_Unnecessary_Inter_Largest.csv"; 
    final static String LUE_FILE_LABEL = "OUT_LUE"; 
    final static String LUE_PROP_LABEL = "PROP_LUE";
    final static String LUE_PROP_DESCRIPTION = "List of the largest unnecessary inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String PUE_FILE_DEFAULT = "false";
    final static String PUE_FILE_LABEL = "OUT_PUE";
    final static String PUE_PROP_LABEL = "PROP_PUE";
    final static String PUE_PROP_DESCRIPTION = "Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String DUE_FILE_DEFAULT = "false";
    final static String DUE_FILE_LABEL = "OUT_DUE"; 
    final static String DUE_PROP_LABEL = "PROP_DUE";
    final static String DUE_PROP_DESCRIPTION = "List of unnecessary inter-oligomer duplexes. Must be true or false. A value of true will request this property.";
    
    final static String SUA_PROP_LABEL = "PROP_SUA";
    final static String SUA_PROP_DESCRIPTION = "Size of the largest unnecessary intra-oligomer duplex. Must be true or false. A value of true will request this property.";
    
    final static String SUE_PROP_LABEL = "PROP_SUE";
    final static String SUE_PROP_DESCRIPTION = "Size of the largest unnecessary inter-oligomer duplex. Must be true or false. A value of true will request this property.";
    
    // Score labels
    final static String BASELINE_N_LABEL = "PROP_baselineN";
    final static String BASELINE_O_LABEL = "PROP_baselineO";
    final static String BASELINE_W_LABEL = "PROP_baselineW";
    final static String DELTA_N_LABEL = "PROP_deltaN";
    final static String DELTA_O_LABEL = "PROP_deltaO";
    final static String DELTA_W_LABEL = "PROP_deltaW";
    final static String N_LABEL = "PROP_N";
    final static String O_LABEL = "PROP_O";    
    final static String W_LABEL = "PROP_W";

    // parameter labels
    final static String INTER_SB_LABEL = "interSB";
    final static String INTER_SLC_LABEL = "interSLC";
    final static String INTRA_SB_LABEL = "intraSB";
    final static String INTRA_SLC_LABEL = "intraSLC";
    final static String SWX_LABEL = "scoringWeightX";
    public final static String NUMBER_LARGEST_DUPLEXES_LABEL = "numberLargestDuplexes";
    
    final Analyzer analyzer;
    
    final static ArrayList<AnalyzerParameter> availableParameters = new ArrayList<>();
    static {
        availableParameters.add(new IntegerAnalyzerParameter(INTER_SB_LABEL, "Inter-oligomer duplexes will contribute points to N equal to this value raised to the length of the duplex. Must be an integer greater than or equal to 0 and less than "+Integer.MAX_VALUE+".","10", Analyzer.INTER_SB_LABEL,0,Integer.MAX_VALUE));
        availableParameters.add(new IntegerAnalyzerParameter(INTER_SLC_LABEL, "Inter-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".","1", Analyzer.INTER_SLC_LABEL,1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerAnalyzerParameter(INTRA_SB_LABEL, "Intra-oligomer duplexes will contribute points to N equal to this value raised to the length of the duplex. Must be an integer greater than or equal to 0 and less than "+Integer.MAX_VALUE+".","10", Analyzer.INTRA_SB_LABEL,0,Integer.MAX_VALUE));
        availableParameters.add(new IntegerAnalyzerParameter(INTRA_SLC_LABEL, "Intra-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".","1", Analyzer.INTRA_SLC_LABEL,1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerAnalyzerParameter(NUMBER_LARGEST_DUPLEXES_LABEL, "Maximum number of duplexes to include when listing largest-duplexes. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".","1000", Analyzer.NUMBER_LARGEST_DUPLEXES_LABEL,1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerAnalyzerParameter(SWX_LABEL, "W will be calculated as O times this value plus N. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".","10000", Analyzer.SWX_LABEL,0,Integer.MAX_VALUE));
    }
    
    final static Map<String,AnalyzerParameter> labelToParameterMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); 
    static {
        for (AnalyzerParameter p : availableParameters){
            labelToParameterMap.put(p.getLabel(), p);
        }
    }
    
    final static ArrayList<Property> availableProperties = new ArrayList<>();
    static {
        availableProperties.add(new Property( BASELINE_N_LABEL, "(fitness points)",  "Network Fitness Score resulting from necessary duplexes. Must be true or false. A value of true will request this property.", "false", Analyzer.BASELINE_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL}));
        availableProperties.add(new Property( BASELINE_O_LABEL, "(fitness points)", "Oligomer Fitness Score resulting from necessary duplexes. Must be true or false. A value of true will request this property.", "false", Analyzer.BASELINE_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL}));
        availableProperties.add(new Property( BASELINE_W_LABEL, "(fitness points)", "Weighted Fitness Score resulting from necessary duplexes. Must be true or false. A value of true will request this property.", "false", Analyzer.BASELINE_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL}));
        
        availableProperties.add(new Property( DELTA_N_LABEL, "(fitness points)", "Network Fitness Score resulting from unnecessary duplexes. Must be true or false. A value of true will request this property.", "true", Analyzer.DELTA_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL, DELTA_N_LABEL, N_LABEL}));
        availableProperties.add(new Property( DELTA_O_LABEL, "(fitness points)", "Oligomer Fitness Score resulting from unnecessary duplexes. Must be true or false. A value of true will request this property.", "true", Analyzer.DELTA_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL,DELTA_O_LABEL, O_LABEL}));
        availableProperties.add(new Property( DELTA_W_LABEL, "(fitness points)", "Weighted Fitness Score resulting from unnecessary duplexes. Must be true or false. A value of true will request this property.", "true", Analyzer.DELTA_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL,DELTA_N_LABEL,DELTA_O_LABEL,DELTA_W_LABEL,N_LABEL,O_LABEL,W_LABEL}));
        
        availableProperties.add(new Property( N_LABEL, "(fitness points)", "Network Fitness Score. Must be true or false. A value of true will request this property.", "false", Analyzer.N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{N_LABEL}));
        availableProperties.add(new Property( O_LABEL, "(fitness points)", "Oligomer Fitness Score. Must be true or false. A value of true will request this property.", "false", Analyzer.O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{O_LABEL}));
        availableProperties.add(new Property( W_LABEL, "(fitness points)", "Weighted Fitness Score. Must be true or false. A value of true will request this property.", "false", Analyzer.W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{N_LABEL,O_LABEL,W_LABEL}));
        
        availableProperties.add(new ProfileProperty( CXA_PROP_LABEL, "(base-pairs, counts)", CXA_PROP_DESCRIPTION, "false", Analyzer.AC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{CXA_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( LXA_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , LXA_PROP_DESCRIPTION, "false", Analyzer.ALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LXA_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( PXA_PROP_LABEL, "(base-pairs, counts)", PXA_PROP_DESCRIPTION, "false", Analyzer.APC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PXA_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( DXA_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , DXA_PROP_DESCRIPTION, "false", Analyzer.APD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DXA_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( CXE_PROP_LABEL, "(base-pairs, counts)", CXE_PROP_DESCRIPTION, "false", Analyzer.EC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{CXE_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( LXE_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , LXE_PROP_DESCRIPTION, "false", Analyzer.ELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LXE_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( PXE_PROP_LABEL, "(base-pairs, counts)", PXE_PROP_DESCRIPTION, "false", Analyzer.EPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PXE_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( DXE_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , DXE_PROP_DESCRIPTION, "false", Analyzer.EPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DXE_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( CNA_PROP_LABEL, "(base-pairs, counts)", CNA_PROP_DESCRIPTION, "false", Analyzer.NAC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{CNA_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( LNA_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , LNA_PROP_DESCRIPTION, "false", Analyzer.NALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LNA_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( PNA_PROP_LABEL, "(base-pairs, counts)", PNA_PROP_DESCRIPTION, "false", Analyzer.NAPC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PNA_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( DNA_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , DNA_PROP_DESCRIPTION, "false", Analyzer.NAPD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DNA_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( CNE_PROP_LABEL, "(base-pairs, counts)", CNE_PROP_DESCRIPTION, "false", Analyzer.NEC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{CNE_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( LNE_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , LNE_PROP_DESCRIPTION, "false", Analyzer.NELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LNE_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( PNE_PROP_LABEL, "(base-pairs, counts)", PNE_PROP_DESCRIPTION, "false", Analyzer.NEPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PNE_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( DNE_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , DNE_PROP_DESCRIPTION, "false", Analyzer.NEPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DNE_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( CUA_PROP_LABEL, "(base-pairs, counts)", CUA_PROP_DESCRIPTION, "false", Analyzer.UAC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{CUA_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( LUA_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , LUA_PROP_DESCRIPTION, "false", Analyzer.UALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LUA_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( PUA_PROP_LABEL, "(base-pairs, counts)", PUA_PROP_DESCRIPTION, "false", Analyzer.UAPC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PUA_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( DUA_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , DUA_PROP_DESCRIPTION, "false", Analyzer.UAPD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DUA_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( CUE_PROP_LABEL, "(base-pairs, counts)", CUE_PROP_DESCRIPTION, "false", Analyzer.UEC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{CUE_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( LUE_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , LUE_PROP_DESCRIPTION, "false", Analyzer.UELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LUE_PROP_LABEL}));
        availableProperties.add(new ProfileProperty( PUE_PROP_LABEL, "(base-pairs, counts)", PUE_PROP_DESCRIPTION, "false", Analyzer.UEPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PUE_PROP_LABEL}));
        availableProperties.add(new DuplexProperty( DUE_PROP_LABEL,"(base-pairs indexO1 indexO1B1 indexO2 indexO2B1)" , DUE_PROP_DESCRIPTION, "false", Analyzer.UEPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DUE_PROP_LABEL}));
        availableProperties.add(new Property(SUA_PROP_LABEL, "(base-pairs)", SUA_PROP_DESCRIPTION, "true",Analyzer.LARGEST_UNNECESSARY_INTRA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[] {SUA_PROP_LABEL,PUA_PROP_LABEL}));
        availableProperties.add(new Property(SUE_PROP_LABEL, "(base-pairs)", SUE_PROP_DESCRIPTION, "true",Analyzer.LARGEST_UNNECESSARY_INTER_LABEL, new String[] {INTER_SLC_LABEL}, new String[] {SUE_PROP_LABEL,PUE_PROP_LABEL}));
    }
    
    final static Map<String,Property> labelToAvailablePropertyMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    static {
        for (Property p : availableProperties){
            labelToAvailablePropertyMap.put(p.getLabel(), p);
        }
    }
    
    final static Map<String,Property> analyzerLabelToPropertyMap= new HashMap<>();
    static {
        for(Map.Entry<String,Property> entry : labelToAvailablePropertyMap.entrySet()){
            analyzerLabelToPropertyMap.put(entry.getValue().analyzerLabel,entry.getValue());
        }
    }
    
    final static ArrayList<OutputFilePath> availableOutputFiles = new ArrayList<>();
    static {
        availableOutputFiles.add(new ProfileFile(CXA_FILE_DEFAULT, "Profile of the length-counts for all intra-oligomer duplexes. Must be either a file name ending with .csv or false.", CXA_FILE_LABEL, CXA_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(LXA_FILE_DEFAULT, "List of the largest unique intra-oligomer duplexes. Must be either a file name ending with .csv or false.", LXA_FILE_LABEL, LXA_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(PXA_FILE_DEFAULT, "Profile of the length-counts for the most prominent intra-oligomer duplexes.", PXA_FILE_LABEL, PXA_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(DXA_FILE_DEFAULT, "List of all prominent intra-oligomer duplexes. Must be either a file name ending with .csv or false.", DXA_FILE_LABEL, DXA_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(CXE_FILE_DEFAULT, "Profile of the length-counts for all inter-oligomer duplexes. Must be either a file name ending with .csv or false.", CXE_FILE_LABEL, CXE_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(LXE_FILE_DEFAULT, "List of all unique inter-oligomer duplexes. Must be either a file name ending with .csv or false.", LXE_FILE_LABEL, LXE_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(PXE_FILE_DEFAULT, "Profile of the length-counts for the most prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", PXE_FILE_LABEL, PXE_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(DXE_FILE_DEFAULT, "List of all prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", DXE_FILE_LABEL, DXE_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(CNA_FILE_DEFAULT, "Profile of the length-counts for necessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", CNA_FILE_LABEL, CNA_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(LNA_FILE_DEFAULT, "List of baseline unique intra-oligomer duplexes. Must be either a file name ending with .csv or false.", LNA_FILE_LABEL, LNA_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(PNA_FILE_DEFAULT, "Profile of the length-counts for the most prominent necessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", PNA_FILE_LABEL, PNA_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(DNA_FILE_DEFAULT, "List of necessary prominent intra-oligomer duplexes. Must be either a file name ending with .csv or false.", DNA_FILE_LABEL, DNA_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(CNE_FILE_DEFAULT, "Profile of the length-counts for necessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", CNE_FILE_LABEL, CNE_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(LNE_FILE_DEFAULT, "List of baseline unique inter-oligomer duplexes. Must be either a file name ending with .csv or false.", LNE_FILE_LABEL, LNE_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(PNE_FILE_DEFAULT, "Profile of the length-counts for the most prominent necessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", PNE_FILE_LABEL, PNE_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(DNE_FILE_DEFAULT, "List of necessary prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", DNE_FILE_LABEL, DNE_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(CUA_FILE_DEFAULT, "Profile of the length-counts for unnecessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", CUA_FILE_LABEL, CUA_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(LUA_FILE_DEFAULT, "List of delta unique intra-oligomer duplexes. Must be either a file name ending with .csv or false.", LUA_FILE_LABEL, LUA_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(PUA_FILE_DEFAULT, "Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", PUA_FILE_LABEL, PUA_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(DUA_FILE_DEFAULT, "List of unnecessary prominent intra-oligomer duplexes. Must be either a file name ending with .csv or false.", DUA_FILE_LABEL, DUA_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(CUE_FILE_DEFAULT, "Profile of the length-counts for unnecessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", CUE_FILE_LABEL, CUE_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(LUE_FILE_DEFAULT, "List of delta unique inter-oligomer duplexes. Must be either a file name ending with .csv or false.", LUE_FILE_LABEL, LUE_PROP_LABEL));
        availableOutputFiles.add(new ProfileFile(PUE_FILE_DEFAULT, "Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", PUE_FILE_LABEL, PUE_PROP_LABEL));
        availableOutputFiles.add(new DuplexesFile(DUE_FILE_DEFAULT, "List of unnecessary prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", DUE_FILE_LABEL, DUE_PROP_LABEL));
    }
    
    final static Map<String,OutputFilePath> labelToOutputFileMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    static {
        for (OutputFilePath o : availableOutputFiles){
            labelToOutputFileMap.put(o.getLabel(), o);
        }
    }
    
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
        
        // for each requested property, add the property and parameters.
        for(String propertyLabel : r.requestedProperties){
            Property p = labelToAvailablePropertyMap.get(propertyLabel);
            if (p == null){
                System.err.println("Property "+propertyLabel+" not supported by DevPro.");
                System.exit(1);
            }
            propertiesToRequest.add(p.getAnalyzerLabel());
            for (String paramLabel : p.getNeededParameters()){
                AnalyzerParameter param = labelToParameterMap.get(paramLabel);
                if (param == null){
                    System.err.println("Parameter "+paramLabel+" not supported by DevPro.");
                    System.exit(1);
                }
                String value = r.parameters.get(paramLabel);
                if (value == null){
                    value = param.getDefault();
                }
                if (!param.isValid(value)){
                    System.err.println("Value "+value+" is not a valid for parameter "+paramLabel+".");
                    System.exit(1);
                }
                parametersToProvide.put(param.getAnalyzerLabel(),value);
                usedParameters.put(paramLabel,value);
            }
        }
        
        Analyzer.Request request = new Analyzer.Request(r.network, propertiesToRequest, parametersToProvide);
        Analyzer.Report report = analyzer.analyze(request);
        
        Map<String,String> necessaryPropertyValues = new HashMap<>();
        for(Map.Entry<String,String> entry : report.getNecessaryPropertyValues().entrySet()){
            String label = analyzerLabelToPropertyMap.get(entry.getKey()).label;
            if (label!=null){
                necessaryPropertyValues.put(label, entry.getValue());
            }
        }
        
        Map<String,String> requestedPropertyValues = new HashMap<>();
        for(Map.Entry<String,String> entry : report.getRequestedPropertyValues().entrySet()){
            String label = analyzerLabelToPropertyMap.get(entry.getKey()).label;
            if (label!=null){
                requestedPropertyValues.put(label, entry.getValue());
            }
        }
        
        Map<String,String> analyzerUsedParameters = report.getUsedParameters();
        
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
    
    public static void main(String[] args){
        // input files
        String FDFP_LABEL = "IN_FIXED_DOMAINS"; // fixed-domains-file-path
        String FDFP_DEFAULT = "dp_in_domains_fixed.txt";
        String FDFP_DESCRIPTION = "File listing the fixed domains.";

        String VDFP_LABEL = "IN_VARIABLE_DOMAINS"; // variable-domains-file-path
        String VDFP_DEFAULT = "dp_in_domains_variable.txt";
        String VDFP_DESCRIPTION = "File listing the variable domains.";

        String ODFP_LABEL = "IN_OLIGOMERS"; // oligomers-file-path
        String ODFP_DEFAULT = "dp_in_oligomers.txt";
        String ODFP_DESCRIPTION = "File listing the domains on each oligomer.";

        // output files
        String OUTPUT_DIRECTORY_DEFAULT = "output" + File.separator;
        String OUTPUT_DIRECTORY_LABEL = "outputDirectory";
        String OUTPUT_DIRECTORY_DESCRIPTION = "Directory where all output files will be placed.";

        String REPORT_FILE_DEFAULT = "dp_out_report.txt";
        String REPORT_FILE_LABEL = "OUT_REPORT";
        String REPORT_FILE_DESCRIPTION = "File containing the results and runtime parameters.";

        String OLIGOMERS_FILE_DEFAULT = "false"; //
        String OLIGOMERS_FILE_LABEL = "OUT_OLIGOMERS";
    
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        DevPro dp = new DevPro(availableProcessors,availableProcessors);
        
        Map<String,String> usedParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        String PFP = PFP_DEFAULT;
        if (args.length > 0)
        {
            if (args[0].equals("-h") || args[0].equals("--help")) // Print explanation of acceptable arguments.
            {
                    System.out.println("Usage: DevPro <Parameter File Path>");
                    System.out.println("If no parameter file path is provided, the default value of "+ PFP_DEFAULT + " will be used.");
                    System.out.println("DevPro -h or --help will print this help message.");
                    System.out.println("DevPro -ep or --exampleParameters will create an example parameter file.");
                    System.exit(0);
            }
            
            if (args[0].equals("-ep") || args[0].equals("--exampleParameters")){
                try{
                    PrintStream PS = new PrintStream(EXAMPLE_PARAMETERS_FILE_DEFAULT);
                    PS.println("// Format: parameter label <tab> value ");
                    PS.println("// Lines starting with '//' are ignored by the program." );
                    PS.println("// For any parameter without a value specified, a defalut value will be used.");
                    PS.println();
                    
                    PS.println(NAMING_CONVENTION_DETAILS);
                    PS.println();
                    
                    PS.println("// *********************");
                    PS.println("// Input File Parameters");
                    PS.println("// *********************");
                    PS.println();
                    
                    PS.println("// " + FDFP_LABEL + " - " + FDFP_DESCRIPTION);
                    PS.println(FDFP_LABEL+"\t"+FDFP_DEFAULT);
                    PS.println();
                    PS.println("// " + VDFP_LABEL + " - " + VDFP_DESCRIPTION);
                    PS.println(VDFP_LABEL+"\t"+VDFP_DEFAULT);
                    PS.println();
                    PS.println("// " + ODFP_LABEL + " - " + ODFP_DESCRIPTION);
                    PS.println(ODFP_LABEL+"\t"+ODFP_DEFAULT);
                    PS.println();
                    
                    PS.println("// *******************");
                    PS.println("// Analysis Parameters");
                    PS.println("// *******************");
                    PS.println();
                    
                    for (AnalyzerParameter p : availableParameters){
                        PS.println("// " + p.getLabel() + " - " + p.getDescription());
                        PS.println(p.getLabel()+"\t"+p.getDefault());
                        PS.println();
                    }
                    
                    PS.println("// **********************");
                    PS.println("// Requestable properties");
                    PS.println("// **********************");
                    PS.println();
                    
                    for (Property p : availableProperties){
                        PS.println("// " + p.getLabel() + " - " + p.getDescription());
                        PS.println(p.getLabel()+"\t"+p.isDefault());
                        PS.println();
                    }
                    
                    PS.println("// **********************");
                    PS.println("// Output File Parameters");
                    PS.println("// **********************");
                    PS.println();
                    
                    PS.println("// " + OUTPUT_DIRECTORY_LABEL + " - " + OUTPUT_DIRECTORY_DESCRIPTION);
                    PS.println(OUTPUT_DIRECTORY_LABEL+"\t"+OUTPUT_DIRECTORY_DEFAULT);
                    PS.println();
                    
                    PS.println("// " + REPORT_FILE_LABEL + " - " + REPORT_FILE_DESCRIPTION);
                    PS.println(REPORT_FILE_LABEL+"\t"+REPORT_FILE_DEFAULT);
                    PS.println();
                    
                    for (OutputFilePath o : availableOutputFiles){
                        PS.println("// " + o.getLabel() + " - " + o.getDescription());
                        PS.println(o.getLabel()+"\t"+o.getDefault());
                        PS.println();
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
        final Map<String,String> providedParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        providedParameters.putAll(util.importPairFromTxt(PFP));
        
        // Read fixed domains file.
        final String FDFP = providedParameters.getOrDefault(FDFP_LABEL,FDFP_DEFAULT);
        usedParameters.put(FDFP_LABEL,FDFP);
        final Map<String,String> fixedDomains = util.importPairFromTxt(FDFP);

        // Read variable domains file.
        final String VDFP = providedParameters.getOrDefault(VDFP_LABEL,VDFP_DEFAULT);
        usedParameters.put(VDFP_LABEL, VDFP);
        final Map<String,String> variableDomains = util.importPairFromTxt(VDFP);

        // Read oligomer domains file.
        final String OFP = providedParameters.getOrDefault(ODFP_LABEL,ODFP_DEFAULT);
        usedParameters.put(ODFP_LABEL, OFP);
        final Map<String,String[]> oligomerDomains = util.importListFromTxt(OFP);
        
        // make network object
        final ICoder coder = new Coder();
        final FactoryDomainBasedEncodedNetwork factory = new FactoryDomainBasedEncodedNetwork(coder, fixedDomains, oligomerDomains, variableDomains);
        final IDomainBasedEncodedNetwork network = factory.getNewNetwork(variableDomains);
        
        Collection<String> requestedProperties = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Collection<String> necessaryProperties = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Collection<String> necessaryParameters = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        
        // for each available property. Was it requested? Add to necessary and requested.
        for(Map.Entry<String,Property> entry: labelToAvailablePropertyMap.entrySet()){
            String propLabel = entry.getKey();
            Property p = entry.getValue();
            String val = providedParameters.getOrDefault(propLabel,p.isDefault());
            if (!val.equalsIgnoreCase("TRUE") && !val.equalsIgnoreCase("FALSE") ){
                System.err.println("Value "+val+" is not valid for parameter "+ propLabel);
                System.exit(1);
            }
            boolean requested = Boolean.parseBoolean(val);
            usedParameters.put(propLabel,String.valueOf(requested));
            if (requested) {
                necessaryProperties.add(propLabel);
                requestedProperties.add(propLabel);
                // for each parameter required by this property
                for(String paramLabel : p.neededParameters){
                    necessaryParameters.add(paramLabel);
                }
                for(String subProp : p.getNeededProperties()){
                    necessaryProperties.add(subProp);
                }
            }
        }
        
        // for each available output file. Was it requested? Request necessary properties and parameters
        for(Map.Entry<String,OutputFilePath> entry : labelToOutputFileMap.entrySet()){
            String fileLabel = entry.getKey();
            OutputFilePath of = entry.getValue();
            String val = providedParameters.getOrDefault(fileLabel,of.getDefault());
            usedParameters.put(fileLabel, val);
            if(!val.equalsIgnoreCase("false")){
                if (!of.isValid(val)){
                    System.err.println("File path "+ val + " invalid for file "+ fileLabel);
                    System.exit(1);
                }
                // for all properties needed by for this file
                for( String prop: of.getNeededProperties()){
                    Property p = labelToAvailablePropertyMap.get(prop);
                    if (p == null){
                        System.err.println("Property "+ prop + " is not supported by DevPro");
                        System.exit(1);
                    }
                    necessaryProperties.add(prop);
                    for(String paramLabel : p.neededParameters){
                        AnalyzerParameter param = labelToParameterMap.get(paramLabel);
                        String paramValue = providedParameters.getOrDefault(paramLabel,param.getDefault());
                        if (!param.isValid(paramValue)){
                            System.err.println("Value "+paramValue+" is not valid for parameter "+ paramLabel);
                            System.exit(1);
                        }
                        necessaryParameters.add(paramLabel);
                    }
                }
            }
        }
        
        Map<String,String> parametersToProvide = new HashMap<>();
        
        // for each necessary parameter
        for(String param: necessaryParameters){
            // read from parameter file.
            AnalyzerParameter par = labelToParameterMap.get(param);
            String val = providedParameters.getOrDefault(param,par.getDefault());
            if (!par.isValid(val)){
                System.err.println("Parameter value "+ val + " invalid for "+ param);
                System.exit(1);
            }
            // record used value
            usedParameters.put(param, val);
            parametersToProvide.put(par.getAnalyzerLabel(),val);
        }
        
        Collection<String> propertiesToRequest = new HashSet<>();
        
        // add properties to request.
        for (String prop : necessaryProperties){
            Property p = labelToAvailablePropertyMap.get(prop);
            propertiesToRequest.add(prop);
        }
        
        //output stuff
        final String outputDirectory = providedParameters.getOrDefault(OUTPUT_DIRECTORY_LABEL, OUTPUT_DIRECTORY_DEFAULT);
        usedParameters.put(OUTPUT_DIRECTORY_LABEL,outputDirectory);
        final String ORFP = providedParameters.getOrDefault(REPORT_FILE_LABEL, REPORT_FILE_DEFAULT);
        usedParameters.put(REPORT_FILE_LABEL,ORFP);
        final String OOSFP = providedParameters.getOrDefault(OLIGOMERS_FILE_LABEL, OLIGOMERS_FILE_DEFAULT);
        usedParameters.put(OLIGOMERS_FILE_LABEL,OOSFP);
        
        Request request = new Request(network, necessaryProperties, parametersToProvide);
        Report report = dp.analyze(request);
        
        // print report file.
        try{
            Files.createDirectories(Paths.get(outputDirectory));
            PrintStream PS = new PrintStream( outputDirectory+usedParameters.get(REPORT_FILE_LABEL));
            printReport(PS, report, necessaryProperties, requestedProperties, usedParameters);
            PS.close();
        }catch (Exception e){
            System.err.println("Error while exporting report file.");
            System.err.println(e.getMessage());
        }
        
        // output oligomers file
        if (!OOSFP.equalsIgnoreCase("False")){
            try {
                PrintStream PS = new PrintStream( outputDirectory+OOSFP );
                printOligomerSequences(PS, report);
                PS.close();
            } catch (Exception e) {
                System.err.println("Error while exporting oligomers file.");
                System.err.println(e.getMessage());
            }
        }
        
        // output other files.
        // for each available output file. Print if active.
        for(Map.Entry<String,OutputFilePath> entry :  labelToOutputFileMap.entrySet()){
            String fileLabel = entry.getKey();
            OutputFilePath of = entry.getValue();
            String fp = usedParameters.get(fileLabel);
            if(!fp.equalsIgnoreCase("false")){
                try {
                    PrintStream PS = new PrintStream( outputDirectory + fp );
                    of.printFile(PS, report);
                    PS.close();
                } catch (Exception e) {
                    System.err.println("Error while exporting "+ fileLabel + " file.");
                    System.err.println(e.getMessage());
                }
            }
        }
        
        System.exit(0);
    }
    
    private static void printProfileCSV(PrintStream ps, String lengthCounts){
        ps.print("Size (base-pairs),Count");
        String[] splitStrings = lengthCounts.split(System.lineSeparator());
        Map<Integer,Integer> sortedValues = new TreeMap<>();
        for(String value: splitStrings){
            String[] splitValues = value.split(" ");
            sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
        }
        for(Map.Entry<Integer,Integer> entry :sortedValues.entrySet()){
            ps.println();
            ps.print(entry.getKey()+","+entry.getValue());
        }
    }
    
    private static class Property{
        String analyzerLabel;
        String description;
        String isDefault;
        String label;
        String[] neededParameters;
        String[] neededProperties;
        String units;
        
        // defaultRequested = is requested by default.
        Property (String label, String units, String description, String isDefault, String analyzerLabel, String[] neededParameters, String[] neededProperties){
            this.label = label;
            this.analyzerLabel = analyzerLabel;
            this.description = description;
            this.isDefault = isDefault;
            this.neededParameters = neededParameters;
            this.neededProperties = neededProperties;
            this.units = units;
        }
        
        public String getAnalyzerLabel(){
            return analyzerLabel;
        }
        
        public String getDescription(){
            return description;
        }
        
        public String isDefault(){
            return isDefault;
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
            PS.print(label+" "+units+": "+value);
        }
        
        public String getUnits(){
            return units;
        }
    }
    
    private static class ProfileProperty extends Property{
        
        public ProfileProperty(String label, String units, String description, String isDefault, String analyzerLabel, String[] neededParameters, String[] neededProperties) {
            super(label, units, description, isDefault, analyzerLabel, neededParameters, neededProperties);
        }
        
        public void printLine(PrintStream PS, String value){
            PS.println();
            PS.print(label+" "+units+": ");
            String[] splitStrings = value.split(System.lineSeparator());
            Map<Integer,Integer> sortedValues = new TreeMap<>();
            for(String v2: splitStrings){
                String[] splitValues = v2.split(" ");
                sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
            }
            for(Map.Entry<Integer,Integer> entry :sortedValues.entrySet()){
                PS.print(" ("+entry.getKey()+" "+entry.getValue()+")");
            }
        }
    }
    
    private static class DuplexProperty extends Property{
        
        public DuplexProperty(String label, String units, String description, String isDefault, String analyzerLabel, String[] neededParameters, String[] neededProperties) {
            super(label, units, description, isDefault, analyzerLabel, neededParameters, neededProperties);
        }
        
        @Override
        public void printLine(PrintStream PS, String value){
            PS.println();
            PS.print(label+" "+units+": ");
            String[] splitStrings = value.split(System.lineSeparator());
            Map<Integer,Integer> sortedValues = new TreeMap<>();
            for(String v2: splitStrings){
                if (!v2.equals("base-pairs indexO1 indexO1B1 indexO2 indexO2B1")){
                PS.print(" ("+v2+")");
                }
            }
        }
    }
    
    private interface Parameter{
        public String getDefault();
        public String getDescription();
        public String getLabel();
        boolean isValid(String value);
    }
    
    private interface AnalyzerParameter extends Parameter{
        public String getAnalyzerLabel();
    }
    
    private static class IntegerAnalyzerParameter implements AnalyzerParameter{
        String analyzerLabel;
        String description;
        String defaultValue;
        String label;
        int minValue;
        int maxValue;
        
        IntegerAnalyzerParameter (String label, String description, String defaultValue, String analyzerLabel, int minValue, int maxValue){
            this.label = label;
            this.analyzerLabel = analyzerLabel;
            this.description = description;
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
        
        @Override
        public String getAnalyzerLabel(){
            return analyzerLabel;
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
    
    private static interface OutputFilePath extends Parameter{
        public String[] getNeededProperties();
        void printFile(PrintStream PS, Report report);
    }
    
    private static class ProfileFile implements OutputFilePath{
        String defaultFilePath;
        String description;
        String label;
        String neededProperty;
        
        ProfileFile(String defaultFilePath, String description, String label, String neededProperty){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
            this.neededProperty = neededProperty;
        }
        
        public String getDefault() {
            return defaultFilePath;
        }

        public String getDescription() {
            return description;
        }

        public String getLabel() {
            return label;
        }

        public String[] getNeededProperties() {
            return new String[] {neededProperty};
        }
        
        public boolean isValid(String value) {
            return value.endsWith(".csv");
        }
        
        public void printFile(PrintStream PS, Report report){
            printProfileCSV(PS,report.requestedProperties.get(neededProperty));
        }
    }
    
    private static class DuplexesFile implements OutputFilePath{
        String defaultFilePath;
        String description;
        String label;
        String neededProperty;
        
        DuplexesFile(String defaultFilePath, String description, String label, String neededProperty){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
            this.neededProperty = neededProperty;
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
        public String[] getNeededProperties() {
            return new String[] {neededProperty};
        }

        @Override
        public boolean isValid(String value) {
            return value.endsWith(".csv");
        }
        
        @Override
        public void printFile(PrintStream PS, Report report){
            String[] oligomerNames = report.network.getOligomerNames();
            String[] oligomerSequences = report.network.getOligomerSequences();
            //PS.print("indexO1,indexO1B1,indexO2,indexO2B1,base-pairs");
            PS.print("Duplex size (base-pairs),Oligomer 1 name,Oligomer 1 sequence,Index of oligomer 1 first base,Oligomer 2 name,Oligomer 2 sequence,Index of oligomer 2 first base");
            String[] splitLines = report.requestedProperties.get(neededProperty).split(System.lineSeparator());
            for(String line: splitLines){
                if (!line.equals("base-pairs indexO1 indexO1B1 indexO2 indexO2B1")){
                    String[] splitSpaces = line.split(" ");
                    int length = Integer.parseInt(splitSpaces[0]);
                    int O1 = Integer.parseInt(splitSpaces[1]);
                    int O1B1 = Integer.parseInt(splitSpaces[2]);
                    int O2 = Integer.parseInt(splitSpaces[3]);
                    int O2B1 = Integer.parseInt(splitSpaces[4]);
                    
                    PS.println();
                    PS.print(length+",");
                    PS.print(oligomerNames[O1]+",");
                    PS.print(oligomerSequences[O1].substring(O1B1,O1B1+length)+",");
                    PS.print(O1B1+",");
                    PS.print(oligomerNames[O2]+",");
                    PS.print(oligomerSequences[O2].substring(O2B1,O2B1+length)+",");
                    PS.print(O2B1);
                }
            }
        }
    }
    
    private static void printReport(PrintStream PS, Report report, Collection<String> necessaryProperties, Collection<String> requestedProperties, Map<String,String> usedParameters) {
        Map<String,String> allUsedParameters = new HashMap<>(usedParameters);
        allUsedParameters.putAll(report.usedParameters);
        IDomainBasedEncodedNetwork network = report.network;

        PS.println("Report generated by DevPro.");
        PS.println("Program version: "+report.version);
        PS.println("Start date: "+report.startDate);
        PS.print("Elapsed time during analysis: ");

        int H = (int)(Double.parseDouble(report.totalTimeSeconds) / (60 *60)); // Hours
        int M = (int)((Double.parseDouble(report.totalTimeSeconds) / 60) % 60 ); // Minutes
        int S = (int)(Double.parseDouble(report.totalTimeSeconds) % 60 );   // Seconds
        PS.print(H + " h " + M + " m " + S + " s ");

        // print requested properties
        PS.println();
        PS.println();
        PS.println("********************");
        PS.println("Requested Properties");
        PS.println("********************");
        
        // for each known property. If it was requested, print it.
        for(String entry : requestedProperties){
            Property p = labelToAvailablePropertyMap.get(entry);
            String value = report.requestedProperties.get(entry);
            p.printLine(PS,value);
        }
        
        PS.println();
        PS.println();
        PS.println("***********************");
        PS.println("Other Needed Properties");
        PS.println("***********************");
        
        for(String entry : necessaryProperties){
            Property p = labelToAvailablePropertyMap.get(entry);
            if(!requestedProperties.contains(entry)){
                String value = report.requestedProperties.get(entry);
                p.printLine(PS,value);
            }
        }

        // print network information
        PS.println();
        PS.println();
        PS.println("*******************");
        PS.println("Network Information");
        PS.println("*******************");

        Map<String,Integer> sortedFixedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedFixedDomains.putAll(report.network.getFixedDomainIndices());
        PS.println();
        PS.println("Fixed Domains:");
        PS.print("--------------");
        for(Map.Entry<String,Integer> entry : sortedFixedDomains.entrySet()){
            PS.println();
            PS.print(report.network.getFixedDomainNames()[entry.getValue()]+ " " + report.network.getFixedDomainSequences()[entry.getValue()]);
        }

        Map<String,Integer> sortedVariableDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedVariableDomains.putAll(report.network.getVariableDomainIndices());
        PS.println();
        PS.println();
        PS.println("Variable Domains:");
        PS.print("-----------------");
        for(Map.Entry<String,Integer> entry : sortedVariableDomains.entrySet()){
            PS.println();
            PS.print(report.network.getVariableDomainNames()[entry.getValue()]+ " " + report.network.getVariableDomainSequences()[entry.getValue()]);
        }

        Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedOligomers.putAll(report.network.getOligomerIndices());
        PS.println();
        PS.println();
        PS.println("Oligomer Domains:");
        PS.print("-------------------");
        for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
            PS.println();
            PS.print(report.network.getOligomerNames()[entry.getValue()]);
            for(String domain : report.network.getOligomerDomains()[entry.getValue()]){
                PS.print(" "+ domain);
            }
        }

        PS.println();
        PS.println();
        PS.println("Oligomer Sequences:");
        PS.print("-------------------");
        for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
            PS.println();
            PS.print(report.network.getOligomerNames()[entry.getValue()]+ " " + report.network.getOligomerSequences()[entry.getValue()]);
        }
        
        // print used parameters.
        PS.println();
        PS.println();
        PS.println("***************");
        PS.println("Used Parameters");
        PS.println("***************");

        Map<String,String> sortedUsedParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedUsedParameters.putAll(allUsedParameters);
        for(Map.Entry<String,String> entry : sortedUsedParameters.entrySet()){
            PS.println();
            PS.print(entry.getKey()+ " " + entry.getValue());
        }
    }
    
    // Export oligomer sequences from final network
    private static void printOligomerSequences(PrintStream PS, Report report){
        Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedOligomers.putAll(report.network.getOligomerIndices());
        String[] os = report.network.getOligomerSequences();
        for(Map.Entry<String,Integer> entry: sortedOligomers.entrySet()){
            PS.println(entry.getKey()+ " " + os[entry.getValue()]);
        }
    }
}