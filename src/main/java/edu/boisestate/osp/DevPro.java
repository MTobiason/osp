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

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    final static String FDFP_LABEL = "fixed_domains_file"; // fixed-domains-file-path
    final static String FDFP_DEFAULT = "dp_in_domains_fixed.txt";
    final static String VDFP_LABEL = "variable_domains_file"; // variable-domains-file-path
    final static String VDFP_DEFAULT = "dp_in_domains_variable.txt";
    final static String ODFP_LABEL = "oligomer_domains_file"; // oligomers-file-path
    final static String ODFP_DEFAULT = "dp_in_oligomer_domains.txt";
    
    // output files
    final static String OUTPUT_DIRECTORY_DEFAULT = "DevPro-Out" + File.separator;
    final static String OUTPUT_DIRECTORY_LABEL = "outputDirectory";
    final static String ORFP_LABEL = "ORFP"; // Output Report File Path
    final static String ORFP_DEFAULT = "dp_out_report.txt";
    final static String OOSFP_LABEL = "OOFP"; // Output Oligomer Sequences File Path
    final static String OOSFP_DEFAULT = "dp_out_oligomers.txt"; //
    final static String ORPFP_LABEL = "OPFP"; // Output Requested Properties File Path
    final static String ORPFP_DEFAULT = "dp_out_properties.txt";
    
    final static String PACA_FILE_LABEL = "Profile_All_Complete_Intra_File"; // Profile all complete intra
    final static String PACE_FILE_LABEL = "Profile_All_Complete_Inter_File"; // Profile all complete inter
    final static String PAUA_FILE_LABEL = "Profile_All_Unique_Intra_File"; // Profile all Unique intra
    final static String PAUE_FILE_LABEL = "Profile_All_Unique_Inter_File"; // Profile all Unique inter
    final static String PACA_LABEL = "Profile_All_Complete_Intra"; // Profile all complete intra
    final static String PACE_LABEL = "Profile_All_Complete_Inter"; // Profile all complete inter
    final static String PAUA_LABEL = "Profile_All_Unique_Intra"; // Profile all Unique intra
    final static String PAUE_LABEL = "Profile_All_Unique_Inter"; // Profile all Unique inter
    
    final static String PBCA_FILE_LABEL = "Profile_Baseline_Complete_Intra_File"; // Profile all complete intra
    final static String PBCE_FILE_LABEL = "Profile_Baseline_Complete_Inter_File"; // Profile all complete inter
    final static String PBUA_FILE_LABEL = "Profile_Baseline_Unique_Intra_File"; // Profile all Unique intra
    final static String PBUE_FILE_LABEL = "Profile_Baseline_Unique_Inter_File"; // Profile all Unique inter
    final static String PBCA_LABEL = "Profile_Baseline_Complete_Intra"; // Profile baseline complete intra
    final static String PBCE_LABEL = "Profile_Baseline_Complete_Inter"; // Profile baseline complete inter
    final static String PBUA_LABEL = "Profile_Baseline_Unique_Intra"; // Profile baseline Unique intra
    final static String PBUE_LABEL = "Profile_Baseline_Unique_Inter"; // Profile baseline Unique inter
    
    final static String PDCA_FILE_LABEL = "Profile_Delta_Complete_Intra_File"; // Profile all complete intra
    final static String PDCE_FILE_LABEL = "Profile_Delta_Complete_Inter_File"; // Profile all complete inter
    final static String PDUA_FILE_LABEL = "Profile_Delta_Unique_Intra_File"; // Profile all Unique intra
    final static String PDUE_FILE_LABEL = "Profile_Delta_Unique_Inter_File"; // Profile all Unique inter
    final static String PDCA_LABEL = "Profile_Delta_Complete_Intra"; // Profile baseline complete intra
    final static String PDCE_LABEL = "Profile_Delta_Complete_Inter"; // Profile baseline complete inter
    final static String PDUA_LABEL = "Profile_Delta_Unique_Intra"; // Profile baseline Unique intra
    final static String PDUE_LABEL = "Profile_Delta_Unique_Inter"; // Profile baseline Unique inter
    
    final static String DAUA_FILE_LABEL = "Duplexes_All_Unique_Intra_File";
    final static String DAUA_LABEL = "Duplexes_All_Unique_Intra"; // List of all unique intra-oligomer duplexes.
    final static String DAUE_FILE_LABEL = "Duplexes_All_Unique_Inter_File"; 
    final static String DAUE_LABEL = "Duplexes_All_Unique_Inter"; // List of all unique intra-oligomer duplexes.
    final static String DBUA_FILE_LABEL = "Duplexes_Baseline_Unique_Intra_File"; 
    final static String DBUA_LABEL = "Duplexes_Baseline_Unique_Intra"; // List of baseline unique intra-oligomer duplexes.
    final static String DBUE_FILE_LABEL = "Duplexes_Baseline_Unique_Inter_File"; 
    final static String DBUE_LABEL = "Duplexes_Baseline_Unique_Inter"; // List of baseline unique intra-oligomer duplexes.
    final static String DDUA_FILE_LABEL = "Duplexes_Delta_Unique_Intra_File"; 
    final static String DDUA_LABEL = "Duplexes_Delta_Unique_Intra"; // List of baseline unique intra-oligomer duplexes.
    final static String DDUE_FILE_LABEL = "Duplexes_Delta_Unique_Inter_File"; 
    final static String DDUE_LABEL = "Duplexes_Delta_Unique_Inter"; // List of baseline unique intra-oligomer duplexes.
    
    final static String LDAUA_FILE_LABEL = "Largest_Duplexes_All_Unique_Intra_File";
    final static String LDAUA_LABEL = "Largest_Duplexes_All_Unique_Intra"; // List of all unique intra-oligomer duplexes.
    final static String LDAUE_FILE_LABEL = "Largest_Duplexes_All_Unique_Inter_File"; 
    final static String LDAUE_LABEL = "Largest_Duplexes_All_Unique_Inter"; // List of all unique intra-oligomer duplexes.
    final static String LDBUA_FILE_LABEL = "Largest_Duplexes_Baseline_Unique_Intra_File"; 
    final static String LDBUA_LABEL = "Largest_Duplexes_Baseline_Unique_Intra"; // List of baseline unique intra-oligomer duplexes.
    final static String LDBUE_FILE_LABEL = "Largest_Duplexes_Baseline_Unique_Inter_File"; 
    final static String LDBUE_LABEL = "Largest_Duplexes_Baseline_Unique_Inter"; // List of baseline unique intra-oligomer duplexes.
    final static String LDDUA_FILE_LABEL = "Largest_Duplexes_Delta_Unique_Intra_File"; 
    final static String LDDUA_LABEL = "Largest_Duplexes_Delta_Unique_Intra"; // List of baseline unique intra-oligomer duplexes.
    final static String LDDUE_FILE_LABEL = "Largest_Duplexes_Delta_Unique_Inter_File"; 
    final static String LDDUE_LABEL = "Largest_Duplexes_Delta_Unique_Inter"; // List of baseline unique intra-oligomer duplexes.

    final static String BASELINE_N_LABEL = "baselineN";
    final static String BASELINE_O_LABEL = "baselineO";
    final static String BASELINE_W_LABEL = "baselineW";
    final static String DELTA_N_LABEL = "deltaN";
    final static String DELTA_O_LABEL = "deltaO";
    final static String DELTA_W_LABEL = "deltaW";
    final static String N_LABEL = "N";
    final static String O_LABEL = "O";    
    final static String W_LABEL = "W";

    final static String INTER_SB_LABEL = "interSB";
    final static String INTER_SLC_LABEL = "interSLC";
    final static String INTRA_SB_LABEL = "intraSB";
    final static String INTRA_SLC_LABEL = "intraSLC";
    final static String SWX_LABEL = "scoringWeightX";
    public final static String NUMBER_LARGEST_DUPLEXES_LABEL = "numberLargestDuplexes";
    
    final Analyzer analyzer;
    
    final static Map<String,IntegerParameter> availableParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); {
        availableParameters.put(INTER_SB_LABEL, new IntegerParameter(Analyzer.INTER_SB_LABEL, "Inter-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex.", INTER_SB_LABEL,"10",0,Integer.MAX_VALUE));
        availableParameters.put(INTER_SLC_LABEL, new IntegerParameter(Analyzer.INTER_SLC_LABEL, "Inter-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores.", INTER_SLC_LABEL,"1",1,Integer.MAX_VALUE));
        availableParameters.put(INTRA_SB_LABEL, new IntegerParameter(Analyzer.INTRA_SB_LABEL, "Intra-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex.", INTRA_SB_LABEL,"10",0,Integer.MAX_VALUE));
        availableParameters.put(INTRA_SLC_LABEL, new IntegerParameter(Analyzer.INTRA_SLC_LABEL, "Intra-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores.", INTRA_SLC_LABEL,"1",1,Integer.MAX_VALUE));
        availableParameters.put(NUMBER_LARGEST_DUPLEXES_LABEL, new IntegerParameter(Analyzer.NUMBER_LARGEST_DUPLEXES_LABEL, "Maximum number of duplexes to include when listing largest-duplexes.", NUMBER_LARGEST_DUPLEXES_LABEL,"1000",1,Integer.MAX_VALUE));
        availableParameters.put(SWX_LABEL, new IntegerParameter(Analyzer.SWX_LABEL, "W will be calculated as O times this value plus N.", SWX_LABEL,"10000",0,Integer.MAX_VALUE));
    }
    
    final static Map<String,Property> availableProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);{
        availableProperties.put(BASELINE_N_LABEL, new Property( Analyzer.BASELINE_N_LABEL, "Network Fitness Score resulting from necessary duplexes.", "true", BASELINE_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL}));
        availableProperties.put(BASELINE_O_LABEL, new Property( Analyzer.BASELINE_O_LABEL, "Oligomer Fitness Score resulting from necessary duplexes.", "true", BASELINE_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL}));
        availableProperties.put(BASELINE_W_LABEL, new Property( Analyzer.BASELINE_W_LABEL, "Weighted Fitness Score resulting from necessary duplexes..", "true", BASELINE_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL}));
        
        availableProperties.put(DELTA_N_LABEL, new Property( Analyzer.DELTA_N_LABEL, "Network Fitness Score resulting from unnecessary duplexes.", "true", DELTA_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL, DELTA_N_LABEL, N_LABEL}));
        availableProperties.put(DELTA_O_LABEL, new Property( Analyzer.DELTA_O_LABEL, "Oligomer Fitness Score resulting from unnecessary duplexes.", "true", DELTA_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL,DELTA_O_LABEL, O_LABEL}));
        availableProperties.put(DELTA_W_LABEL, new Property( Analyzer.DELTA_W_LABEL, "Weighted Fitness Score resulting from unnecessary duplexes.", "true", DELTA_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL,DELTA_N_LABEL,DELTA_O_LABEL,DELTA_W_LABEL,N_LABEL,O_LABEL,W_LABEL}));
        
        availableProperties.put(N_LABEL, new Property( Analyzer.N_LABEL, "Network Fitness Score.", "true", N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{N_LABEL}));
        availableProperties.put(O_LABEL, new Property( Analyzer.O_LABEL, "Oligomer Fitness Score.", "true", O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{O_LABEL}));
        availableProperties.put(W_LABEL, new Property( Analyzer.W_LABEL, "Weighted Fitness Score.", "true", W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{N_LABEL,O_LABEL,W_LABEL}));
        
        availableProperties.put(PACA_LABEL, new ProfileProperty( Analyzer.PACA_LABEL, "Profile of the length-counts for all intra-oligomer duplexes.", "true", PACA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PACA_LABEL}));
        availableProperties.put(PACE_LABEL, new ProfileProperty( Analyzer.PACE_LABEL, "Profile of the length-counts for all inter-oligomer duplexes.", "true", PACE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PACE_LABEL}));
        availableProperties.put(PAUA_LABEL, new ProfileProperty( Analyzer.PAUA_LABEL, "Profile of the length-counts for the most prominent intra-oligomer duplexes.", "true", PAUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PAUA_LABEL}));
        availableProperties.put(PAUE_LABEL, new ProfileProperty( Analyzer.PAUE_LABEL, "Profile of the length-counts for the most prominent inter-oligomer duplexes.", "true", PAUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PAUE_LABEL}));
        
        availableProperties.put(PBCA_LABEL, new ProfileProperty( Analyzer.PBCA_LABEL, "Profile of the length-counts for baseline intra-oligomer duplexes.", "true", PBCA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PBCA_LABEL}));
        availableProperties.put(PBCE_LABEL, new ProfileProperty( Analyzer.PBCE_LABEL, "Profile of the length-counts for baseline inter-oligomer duplexes.", "true", PBCE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PBCE_LABEL}));
        availableProperties.put(PBUA_LABEL, new ProfileProperty( Analyzer.PBUA_LABEL, "Profile of the length-counts for the most prominent baseline intra-oligomer duplexes.", "true", PBUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PBUA_LABEL}));
        availableProperties.put(PBUE_LABEL, new ProfileProperty( Analyzer.PBUE_LABEL, "Profile of the length-counts for the most prominent baseline inter-oligomer duplexes.", "true", PBUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PBUE_LABEL}));
        
        availableProperties.put(PDCA_LABEL, new ProfileProperty( Analyzer.PDCA_LABEL, "Profile of the length-counts for unnecessary intra-oligomer duplexes.", "true", PDCA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PDCA_LABEL}));
        availableProperties.put(PDCE_LABEL, new ProfileProperty( Analyzer.PDCE_LABEL, "Profile of the length-counts for unnecessary inter-oligomer duplexes.", "true", PDCE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PDCE_LABEL}));
        availableProperties.put(PDUA_LABEL, new ProfileProperty( Analyzer.PDUA_LABEL, "Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes.", "true", PDUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{PDUA_LABEL}));
        availableProperties.put(PDUE_LABEL, new ProfileProperty( Analyzer.PDUE_LABEL, "Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes.", "true", PDUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{PDUE_LABEL}));
        
        availableProperties.put(DAUA_LABEL, new DuplexProperty(Analyzer.DAUA_LABEL,"List of all intra-oligomer duplexes.", "true", DAUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DAUA_LABEL}));
        availableProperties.put(DAUE_LABEL, new DuplexProperty(Analyzer.DAUE_LABEL,"List of all inter-oligomer duplexes.", "true", DAUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DAUE_LABEL}));
        availableProperties.put(DBUA_LABEL, new DuplexProperty(Analyzer.DBUA_LABEL,"List of baseline intra-oligomer duplexes.", "true", DBUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DBUA_LABEL}));
        availableProperties.put(DBUE_LABEL, new DuplexProperty(Analyzer.DBUE_LABEL,"List of baseline inter-oligomer duplexes.", "true", DBUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DBUE_LABEL}));
        availableProperties.put(DDUA_LABEL, new DuplexProperty(Analyzer.DDUA_LABEL,"List of delta intra-oligomer duplexes.", "true", DDUA_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{DDUA_LABEL}));
        availableProperties.put(DDUE_LABEL, new DuplexProperty(Analyzer.DDUE_LABEL,"List of delta inter-oligomer duplexes.", "true", DDUE_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{DDUE_LABEL}));
        
        availableProperties.put(LDAUA_LABEL, new DuplexProperty(Analyzer.LDAUA_LABEL,"List of the largest intra-oligomer duplexes.", "true", LDAUA_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LDAUA_LABEL}));
        availableProperties.put(LDAUE_LABEL, new DuplexProperty(Analyzer.LDAUE_LABEL,"List of the largest inter-oligomer duplexes.", "true", LDAUE_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LDAUE_LABEL}));
        availableProperties.put(LDBUA_LABEL, new DuplexProperty(Analyzer.LDBUA_LABEL,"List of the largest baseline intra-oligomer duplexes.", "true", LDBUA_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LDBUA_LABEL}));
        availableProperties.put(LDBUE_LABEL, new DuplexProperty(Analyzer.LDBUE_LABEL,"List of the largest baseline inter-oligomer duplexes.", "true", LDBUE_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LDBUE_LABEL}));
        availableProperties.put(LDDUA_LABEL, new DuplexProperty(Analyzer.LDDUA_LABEL,"List of the largest delta intra-oligomer duplexes.", "true", LDDUA_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LDDUA_LABEL}));
        availableProperties.put(LDDUE_LABEL, new DuplexProperty(Analyzer.LDDUE_LABEL,"List of the largest delta inter-oligomer duplexes.", "true", LDDUE_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{LDDUE_LABEL}));
        
    }
    
    final static Map<String,OutputFile> availableOutputFiles = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);{
        availableOutputFiles.put(PACA_FILE_LABEL, new ProfileFile("Profile_All_Complete_Intra.csv", "Profile of the length-counts for all intra-oligomer duplexes.", PACA_FILE_LABEL, PACA_LABEL, "true"));
        availableOutputFiles.put(PACE_FILE_LABEL, new ProfileFile("Profile_All_Complete_Inter.csv", "Profile of the length-counts for all inter-oligomer duplexes.", PAUA_FILE_LABEL, PACE_LABEL, "true"));
        availableOutputFiles.put(PAUA_FILE_LABEL, new ProfileFile("Profile_All_Unique_Intra.csv", "Profile of the length-counts for the most prominent intra-oligomer duplexes.", PAUA_FILE_LABEL, PAUA_LABEL, "true"));
        availableOutputFiles.put(PAUE_FILE_LABEL, new ProfileFile("Profile_All_Unique_Inter.csv", "Profile of the length-counts for the most prominent inter-oligomer duplexes.", PAUE_FILE_LABEL, PAUE_LABEL, "true"));
        
        availableOutputFiles.put(PBCA_FILE_LABEL, new ProfileFile("Profile_Baseline_Complete_Intra.csv", "Profile of the length-counts for baseline intra-oligomer duplexes.", PBCA_FILE_LABEL, PBCA_LABEL, "true"));
        availableOutputFiles.put(PBCE_FILE_LABEL, new ProfileFile("Profile_Baseline_Complete_Inter.csv", "Profile of the length-counts for baseline inter-oligomer duplexes.", PBUA_FILE_LABEL, PBCE_LABEL, "true"));
        availableOutputFiles.put(PBUA_FILE_LABEL, new ProfileFile("Profile_Baseline_Unique_Intra.csv", "Profile of the length-counts for the most prominent baseline intra-oligomer duplexes.", PBUA_FILE_LABEL, PBUA_LABEL, "true"));
        availableOutputFiles.put(PBUE_FILE_LABEL, new ProfileFile("Profile_Baseline_Unique_Inter.csv", "Profile of the length-counts for the most prominent baseline inter-oligomer duplexes.", PBUE_FILE_LABEL, PBUE_LABEL, "true"));
        
        availableOutputFiles.put(PDCA_FILE_LABEL, new ProfileFile("Profile_Delta_Complete_Intra.csv", "Profile of the length-counts for unnecessary intra-oligomer duplexes.", PDCA_FILE_LABEL, PDCA_LABEL, "true"));
        availableOutputFiles.put(PDCE_FILE_LABEL, new ProfileFile("Profile_Delta_Complete_Inter.csv", "Profile of the length-counts for unnecessary inter-oligomer duplexes.", PDUA_FILE_LABEL, PDCE_LABEL, "true"));
        availableOutputFiles.put(PDUA_FILE_LABEL, new ProfileFile("Profile_Delta_Unique_Intra.csv", "Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes.", PDUA_FILE_LABEL, PDUA_LABEL, "true"));
        availableOutputFiles.put(PDUE_FILE_LABEL, new ProfileFile("Profile_Delta_Unique_Inter.csv", "Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes.", PDUE_FILE_LABEL, PDUE_LABEL, "true"));
        
        availableOutputFiles.put(DAUA_FILE_LABEL, new DuplexesFile("Duplexes_All_Unique_Intra.csv", "List of all unique intra-oligomer duplexes.", DAUA_FILE_LABEL, DAUA_LABEL, "true"));
        availableOutputFiles.put(DAUE_FILE_LABEL, new DuplexesFile("Duplexes_All_Unique_Inter.csv", "List of all unique inter-oligomer duplexes.", DAUE_FILE_LABEL, DAUE_LABEL, "true"));
        availableOutputFiles.put(DBUA_FILE_LABEL, new DuplexesFile("Duplexes_Baseline_Unique_Intra.csv", "List of baseline unique intra-oligomer duplexes.", DBUA_FILE_LABEL, DBUA_LABEL, "true"));
        availableOutputFiles.put(DBUE_FILE_LABEL, new DuplexesFile("Duplexes_Baseline_Unique_Inter.csv", "List of baseline unique inter-oligomer duplexes.", DBUE_FILE_LABEL, DBUE_LABEL, "true"));
        availableOutputFiles.put(DDUA_FILE_LABEL, new DuplexesFile("Duplexes_Delta_Unique_Intra.csv", "List of delta unique intra-oligomer duplexes.", DDUA_FILE_LABEL, DDUA_LABEL, "true"));
        availableOutputFiles.put(DDUE_FILE_LABEL, new DuplexesFile("Duplexes_Delta_Unique_Inter.csv", "List of delta unique inter-oligomer duplexes.", DDUE_FILE_LABEL, DDUE_LABEL, "true"));
        
        availableOutputFiles.put(LDAUA_FILE_LABEL, new DuplexesFile("Largest_Duplexes_All_Unique_Intra.csv", "List of the largest unique intra-oligomer duplexes.", LDAUA_FILE_LABEL, LDAUA_LABEL, "true"));
        availableOutputFiles.put(LDAUE_FILE_LABEL, new DuplexesFile("Largest_Duplexes_All_Unique_Inter.csv", "List of all unique inter-oligomer duplexes.", LDAUE_FILE_LABEL, LDAUE_LABEL, "true"));
        availableOutputFiles.put(LDBUA_FILE_LABEL, new DuplexesFile("Largest_Duplexes_Baseline_Unique_Intra.csv", "List of baseline unique intra-oligomer duplexes.", LDBUA_FILE_LABEL, LDBUA_LABEL, "true"));
        availableOutputFiles.put(LDBUE_FILE_LABEL, new DuplexesFile("Largest_Duplexes_Baseline_Unique_Inter.csv", "List of baseline unique inter-oligomer duplexes.", LDBUE_FILE_LABEL, LDBUE_LABEL, "true"));
        availableOutputFiles.put(LDDUA_FILE_LABEL, new DuplexesFile("Largest_Duplexes_Delta_Unique_Intra.csv", "List of delta unique intra-oligomer duplexes.", LDDUA_FILE_LABEL, LDDUA_LABEL, "true"));
        availableOutputFiles.put(LDDUE_FILE_LABEL, new DuplexesFile("Largest_Duplexes_Delta_Unique_Inter.csv", "List of delta unique inter-oligomer duplexes.", LDDUE_FILE_LABEL, LDDUE_LABEL, "true"));
        
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
            Property p = availableProperties.get(propertyLabel);
            if (p == null){
                System.err.println("Property "+propertyLabel+" not supported by DevPro.");
                System.exit(1);
            }
            propertiesToRequest.add(p.getAnalyzerLabel());
            for (String paramLabel : p.getNeededParameters()){
                Parameter param = availableParameters.get(paramLabel);
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
        
        Map<String,String> necessaryPropertyValues = report.getNecessaryPropertyValues();
        Map<String,String> requestedPropertyValues = report.getRequestedPropertyValues();
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
    
//    private void exportReport(Report r, Map<String,String> otherUsedParameters){
//        Map<String,String> allUsedParameters = new HashMap<>(otherUsedParameters);
//        allUsedParameters.putAll(r.usedParameters);
//        IDomainBasedEncodedNetwork network = r.network;
//
//        // Export Report file
//        String ORFP = allUsedParameters.get(ORFP_LABEL);
//        if (!ORFP.equalsIgnoreCase("False")){
//            try{
//                String outputDirectory = allUsedParameters.get(OUTPUT_DIRECTORY_LABEL);
//                Files.createDirectories(Paths.get(outputDirectory));
//                PrintStream PS = new PrintStream( outputDirectory+ORFP );
//
//                PS.println("Report generated by DevPro.");
//                PS.println("Program version: "+r.version);
//                PS.println("Start date: "+r.startDate);
//                PS.print("Elapsed time during analysis: ");
//                        
//                int H = (int)(Double.parseDouble(r.totalTimeSeconds) / (60 *60)); // Hours
//                int M = (int)((Double.parseDouble(r.totalTimeSeconds) / 60) % 60 ); // Minutes
//                int S = (int)(Double.parseDouble(r.totalTimeSeconds) % 60 );   // Seconds
//                PS.println(H + " h " + M + " m " + S + " s ");
//
//                // print used parameters.
//                PS.println();
//                PS.println("***************");
//                PS.println("Used Parameters");
//                PS.println("***************");
//                PS.println();
//
//                Map<String,String> sortedUsedParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedUsedParameters.putAll(allUsedParameters);
//                for(Map.Entry<String,String> entry : sortedUsedParameters.entrySet()){
//                    PS.println(entry.getKey()+ " " + entry.getValue());
//                }
//
//                // print requested properties
//                PS.println();
//                PS.println("********************");
//                PS.println("Requested Properties");
//                PS.println("********************");
//                PS.println();
//                
//                Map<String,String> sortedRequestedProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedRequestedProperties.putAll(r.requestedProperties);
//                for(Map.Entry<String,String> entry : sortedRequestedProperties.entrySet()){
//                    switch (entry.getKey()){
//                        case Analyzer.PACA_LABEL:
//                                printProfile(PS,PACA_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.PACE_LABEL:
//                            printProfile(PS,PACE_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.PAUA_LABEL:
//                            printProfile(PS,PAUA_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.PAUE_LABEL:
//                            printProfile(PS,PAUE_LABEL, entry.getValue());
//                            break;
//                            
//                        default:
//                            PS.println(entry.getKey()+" "+ entry.getValue());
//                    }
//                }
//                
//                PS.println();
//                PS.println("**************************");
//                PS.println("Other Necessary Properties");
//                PS.println("**************************");
//                PS.println();
//                
//                Map<String,String> sortedNecessaryProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedNecessaryProperties.putAll(r.necessaryProperties);
//                for(Map.Entry<String,String> entry : sortedNecessaryProperties.entrySet()){
//                    switch (entry.getKey()){
//                        case Analyzer.PACA_LABEL:
//                                printProfile(PS,PACA_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.PACE_LABEL:
//                            printProfile(PS,PACE_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.PAUA_LABEL:
//                            printProfile(PS,PAUA_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.PAUE_LABEL:
//                            printProfile(PS,PAUE_LABEL, entry.getValue());
//                            break;
//                            
//                        default:
//                            PS.println(entry.getKey()+" "+ entry.getValue());
//                    }
//                }
//
//                // print network information
//                PS.println();
//                PS.println("*******************");
//                PS.println("Network Information");
//                PS.println("*******************");
//                PS.println();
//
//                Map<String,Integer> sortedFixedDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedFixedDomains.putAll(r.network.getFixedDomainIndices());
//                PS.println("Fixed Domains:");
//                PS.println("--------------");
//                for(Map.Entry<String,Integer> entry : sortedFixedDomains.entrySet()){
//                    PS.println(r.network.getFixedDomainNames()[entry.getValue()]+ " " + r.network.getFixedDomainSequences()[entry.getValue()]);
//                }
//
//                Map<String,Integer> sortedVariableDomains = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedVariableDomains.putAll(r.network.getVariableDomainIndices());
//                PS.println();
//                PS.println("Variable Domains:");
//                PS.println("-----------------");
//                for(Map.Entry<String,Integer> entry : sortedVariableDomains.entrySet()){
//                    PS.println(r.network.getVariableDomainNames()[entry.getValue()]+ " " + r.network.getVariableDomainSequences()[entry.getValue()]);
//                }
//
//                Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedOligomers.putAll(r.network.getOligomerIndices());
//                PS.println();
//                PS.println("Oligomer Domains:");
//                PS.println("-------------------");
//                for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
//                    PS.print(r.network.getOligomerNames()[entry.getValue()]);
//                    for(String domain : r.network.getOligomerDomains()[entry.getValue()]){
//                        PS.print(" "+ domain);
//                    }
//                    PS.println();
//                }
//
//                PS.println();
//                PS.println("Oligomer Sequences:");
//                PS.println("-------------------");
//                for(Map.Entry<String,Integer> entry : sortedOligomers.entrySet()){
//                    PS.println(r.network.getOligomerNames()[entry.getValue()]+ " " + r.network.getOligomerSequences()[entry.getValue()]);
//                }
//                
//                PS.close();
//            } catch (Exception e){
//                System.err.println("Error while exporting report file.");
//                System.err.println(e.getMessage());
//            }
//        }
//
//        // Export oligomer sequences from final network
//        String OOSFP = allUsedParameters.get(OOSFP_LABEL);
//        if (!OOSFP.equalsIgnoreCase("False")){
//            try {
//                String outputDirectory = allUsedParameters.get(OUTPUT_DIRECTORY_LABEL);
//                Files.createDirectories(Paths.get(outputDirectory));
//                PrintStream PS = new PrintStream( outputDirectory+OOSFP );
//
//                Map<String,Integer> sortedOligomers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedOligomers.putAll(r.network.getOligomerIndices());
//                String[] os = network.getOligomerSequences();
//                for(Map.Entry<String,Integer> entry: sortedOligomers.entrySet()){
//                    PS.println(entry.getKey()+ " " + os[entry.getValue()]);
//                }
//                PS.close();
//            } catch (Exception e) {
//                System.err.println("Error while exporting OOS file.");
//                System.err.println(e.getMessage());
//            }
//        }
//
//        // Export requested properties file.
//        String ORPFP = allUsedParameters.get(ORPFP_LABEL);
//        if (!ORPFP.equalsIgnoreCase("False")){
//            try{
//                String outputDirectory = allUsedParameters.get(OUTPUT_DIRECTORY_LABEL);
//                Files.createDirectories(Paths.get(outputDirectory));
//                PrintStream PS = new PrintStream( outputDirectory+ORPFP );
//
//                Map<String,String> sortedProperties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//                sortedProperties.putAll(r.requestedProperties);
//                for(Map.Entry<String,String> entry : sortedProperties.entrySet()){
//                    switch (entry.getKey()){
//                        case Analyzer.BASELINE_INTER_DUPLEX_COUNT_LABEL:
//                            printProfile(PS,BASELINE_INTER_DUPLEX_COUNT_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.BASELINE_INTRA_DUPLEX_COUNT_LABEL:
//                            printProfile(PS,BASELINE_INTRA_DUPLEX_COUNT_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.INTER_DUPLEX_COUNT_LABEL:
//                            printProfile(PS,INTER_DUPLEX_COUNT_LABEL, entry.getValue());
//                            break;
//                            
//                        case Analyzer.INTRA_DUPLEX_COUNT_LABEL:
//                            printProfile(PS,INTRA_DUPLEX_COUNT_LABEL, entry.getValue());
//                            break;
//                            
//                        default:
//                            PS.println(entry.getKey()+" "+ entry.getValue());
//                    }
//                }
//            } catch (Exception e) {
//                System.err.println("Error while exporting ORP file.");
//                System.err.println(e.getMessage());
//            }
//        }
//        
//        String PACA = allUsedParameters.get(PACA_LABEL);
//        if (!PACA.equalsIgnoreCase("False")){
//            try{
//                String outputDirectory = allUsedParameters.get(OUTPUT_DIRECTORY_LABEL);
//                Files.createDirectories(Paths.get(outputDirectory));
//                PrintStream PS = new PrintStream( outputDirectory+PACA );
//                printProfileCSV(PS,PACA_LABEL, r.requestedProperties.get(Analyzer.PACA_LABEL));
//                PS.close();
//            } catch (Exception e) {
//                System.err.println("Error while exporting "+PACA_LABEL+" file.");
//                System.err.println(e.getMessage());
//            }
//        }
//        
//        String PACE = allUsedParameters.get(PACE_LABEL);
//        if (!PACE.equalsIgnoreCase("False")){
//            try{
//                String outputDirectory = allUsedParameters.get(OUTPUT_DIRECTORY_LABEL);
//                Files.createDirectories(Paths.get(outputDirectory));
//                PrintStream PS = new PrintStream( outputDirectory+PACE );
//                printProfileCSV(PS,PACE_LABEL, r.requestedProperties.get(Analyzer.PACE_LABEL));
//                PS.close();
//            } catch (Exception e) {
//                System.err.println("Error while exporting "+PACE_LABEL+" file.");
//                System.err.println(e.getMessage());
//            }
//        }
//        
//        String PAUA = allUsedParameters.get(PAUA_LABEL);
//        if (!PAUA.equalsIgnoreCase("False")){
//            try{
//                String outputDirectory = allUsedParameters.get(OUTPUT_DIRECTORY_LABEL);
//                Files.createDirectories(Paths.get(outputDirectory));
//                PrintStream PS = new PrintStream( outputDirectory+PAUA );
//                printProfileCSV(PS,PAUA_LABEL, r.requestedProperties.get(Analyzer.PAUA_LABEL));
//                PS.close();
//            } catch (Exception e) {
//                System.err.println("Error while exporting "+PAUA_LABEL+" file.");
//                System.err.println(e.getMessage());
//            }
//        }
//        
//        String PAUE = allUsedParameters.get(PAUE_LABEL);
//        if (!PAUE.equalsIgnoreCase("False")){
//            try{
//                String outputDirectory = allUsedParameters.get(OUTPUT_DIRECTORY_LABEL);
//                Files.createDirectories(Paths.get(outputDirectory));
//                PrintStream PS = new PrintStream( outputDirectory+PAUE );
//                printProfileCSV(PS,PAUE_LABEL, r.requestedProperties.get(Analyzer.PAUE_LABEL));
//                PS.close();
//            } catch (Exception e) {
//                System.err.println("Error while exporting "+PAUE_LABEL+" file.");
//                System.err.println(e.getMessage());
//            }
//        }
//    }
    
    public static void main(String[] args){
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        DevPro dp = new DevPro(availableProcessors,availableProcessors);
        
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
        
        Collection<String> propertiesToRequest = new HashSet<>();
        Map<String,String> parametersToProvide = new HashMap<>();
        
        // for each of the available properties. Is it requested?
        for(Map.Entry<String,Property> entry: availableProperties.entrySet()){
            String propLabel = entry.getKey();
            Property p = entry.getValue();
            boolean active = Boolean.parseBoolean(parameters.getOrDefault(propLabel,p.isDefault()));
            usedParameters.put(propLabel,String.valueOf(active));
            if (active) {
                propertiesToRequest.add(propLabel);
                for(String paramLabel : p.neededParameters){
                    Parameter param = availableParameters.get(paramLabel);
                    String paramValue = parameters.getOrDefault(paramLabel,param.getDefault());
                    if (!param.isValid(paramValue)){
                        System.err.println("Value "+paramValue+" is not valid for parameter "+ paramLabel);
                        System.exit(1);
                    }
                    usedParameters.put(paramLabel,paramValue);
                    parametersToProvide.put(paramLabel,paramValue);
                }
            }
        }
        
        // for each available output file. If requested, request needed properties.
        for(Map.Entry<String,OutputFile> entry : availableOutputFiles.entrySet()){
            String fileLabel = entry.getKey();
            OutputFile of = entry.getValue();
            String fp = parameters.get(fileLabel);
            if (fp == null){
                if (Boolean.parseBoolean(of.isDefault())){
                    fp = of.getDefaultFilePath();
                } else {
                    fp = "false";
                }
            }
            usedParameters.put(fileLabel, fp);
            if(!fp.equalsIgnoreCase("false")){
                if (!of.isValid(fp)){
                    System.err.println("File path "+ fp + " invalid for file "+ fileLabel);
                    System.exit(1);
                }
                String[] props = of.getNeededProperties();
                for( String prop: props){
                    Property p = availableProperties.get(prop);
                    if (p == null){
                        System.err.println("Property "+ prop + " is not supported by DevPro");
                        System.exit(1);
                    }
                    propertiesToRequest.add(prop);
                    for(String paramLabel : p.neededParameters){
                        Parameter param = availableParameters.get(paramLabel);
                        String paramValue = parameters.getOrDefault(paramLabel,param.getDefault());
                        if (!param.isValid(paramValue)){
                            System.err.println("Value "+paramValue+" is not valid for parameter "+ paramLabel);
                            System.exit(1);
                        }
                        usedParameters.put(paramLabel,paramValue);
                        parametersToProvide.put(paramLabel,paramValue);
                    }
                }
            }
        }
        
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
        
        // make network object
        final ICoder coder = new Coder();
        final FactoryDomainBasedEncodedNetwork factory = new FactoryDomainBasedEncodedNetwork(coder, fixedDomains, oligomerDomains, variableDomains);
        final IDomainBasedEncodedNetwork network = factory.getNewNetwork(variableDomains);
        
        // add properties to request.
        
        //output stuff
        final String outputDirectory = parameters.getOrDefault(OUTPUT_DIRECTORY_LABEL, OUTPUT_DIRECTORY_DEFAULT);
        usedParameters.put(OUTPUT_DIRECTORY_LABEL,outputDirectory);
        final String ORFP = parameters.getOrDefault(ORFP_LABEL, ORFP_DEFAULT);
        usedParameters.put(ORFP_LABEL,ORFP);
        final String OOSFP = parameters.getOrDefault(OOSFP_LABEL, OOSFP_DEFAULT);
        usedParameters.put(OOSFP_LABEL,OOSFP);
        
        Request request = new Request(network, propertiesToRequest, parametersToProvide);
        Report report = dp.analyze(request);
        
        // print report file.
        try{
            Files.createDirectories(Paths.get(outputDirectory));
            PrintStream PS = new PrintStream( outputDirectory+usedParameters.get(ORFP_LABEL));
            printReport(PS, report, usedParameters);
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
        for(Map.Entry<String,OutputFile> entry :  availableOutputFiles.entrySet()){
            String fileLabel = entry.getKey();
            OutputFile of = entry.getValue();
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
    
    private void printProfile(PrintStream ps, String label ,String lengthCounts){
        ps.print(label);
        ps.print(" (base-pairs, counts):");
        String[] splitStrings = lengthCounts.split(System.lineSeparator());
        Map<Integer,Integer> sortedValues = new TreeMap<>();
        for(String value: splitStrings){
            String[] splitValues = value.split(" ");
            sortedValues.put(Integer.parseInt(splitValues[0]),Integer.parseInt(splitValues[1]));
        }
        for(Map.Entry<Integer,Integer> entry :sortedValues.entrySet()){
            ps.print(" ("+entry.getKey()+", "+entry.getValue()+")");
        }
        ps.println();
    }
    
    private void printProfileCSV(PrintStream ps, String lengthCounts){
        ps.print("Size(base-pairs),Count");
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
    
    
//    private interface Parameter{
//        String getLabel();
//        String getDefaultValue();
//        String getDescription();
//        
//        boolean isValid(String value);
//        void print(PrintStream PS);
//    }
    
//    private interface AnalyzerParameter extends Parameter{
//        String getAnalyzerLabel();
//    }
//    
//    private interface AnalyzerProperty extends Parameter{
//        String getAnalyzerLabel();
//        String[] getNeededAnalyzerParameters();
//        
//        void printLine(PrintStream PS, String value);
//    }
//    
//    private interface OutputFile extends Parameter{
//        String getFilePath();
//        String[] getNeededProperties();
//        
//        void printFile(PrintStream PS, String value);
//    }
    
    private class Property{
        String analyzerLabel;
        String description;
        String isDefault;
        String label;
        String[] neededParameters;
        String[] neededProperties;
        
        // defaultRequested = is requested by default.
        Property (String analyzerLabel, String description, String isDefault, String label, String[] neededParameters, String[] neededProperties){
            this.label = label;
            this.analyzerLabel = analyzerLabel;
            this.description = description;
            this.isDefault = isDefault;
            this.neededParameters = neededParameters;
            this.neededProperties = neededProperties;
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
            PS.print(label+" "+value);
        }
    }
    
    private class ProfileProperty extends Property{
        
        public ProfileProperty(String analyzerLabel, String description, String isDefault, String label, String[] neededParameters, String[] neededProperties) {
            super(analyzerLabel, description, isDefault, label, neededParameters, neededProperties);
        }
        
        public void printLine(PrintStream PS, String value){
            PS.println();
            PS.print(label+" (base-pairs, counts):");
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
    
    private class DuplexProperty extends Property{
        
        public DuplexProperty(String analyzerLabel, String description, String isDefault, String label, String[] neededParameters, String[] neededProperties) {
            super(analyzerLabel, description, isDefault, label, neededParameters, neededProperties);
        }
        
        @Override
        public void printLine(PrintStream PS, String value){
            PS.println();
            PS.print(label+" (base-pairs indexO1 indexO1B1 indexO2 indexO2B1):");
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
        public String getAnalyzerLabel();
        public String getDefault();
        public String getDescription();
        public String getLabel();
        boolean isValid(String value);
    }
    
    private class IntegerParameter implements Parameter{
        String analyzerLabel;
        String description;
        String defaultValue;
        String label;
        int minValue;
        int maxValue;
        
        IntegerParameter (String analyzerLabel, String description, String label, String defaultValue, int minValue, int maxValue){
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
    
    private interface OutputFile{
        public String getDefaultFilePath();
        public String getDescription();
        public String getLabel();
        public String[] getNeededProperties();
        public String isDefault();
        boolean isValid(String value);
        void printFile(PrintStream PS, Report report);
    }
    
    private class ProfileFile implements OutputFile{
        String defaultFilePath;
        String description;
        String label;
        String neededProperty;
        String isDefault;
        
        ProfileFile(String defaultFilePath, String description, String label, String neededProperty, String isDefault){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
            this.neededProperty = neededProperty;
            this.isDefault = isDefault;
        }
        public String getDefaultFilePath() {
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
        
        public String isDefault(){
            return isDefault;
        }

        public boolean isValid(String value) {
            return value.endsWith(".csv");
        }
        
        public void printFile(PrintStream PS, Report report){
            printProfileCSV(PS,report.requestedProperties.get(neededProperty));
        }
    }
    
    private class DuplexesFile implements OutputFile{
        String defaultFilePath;
        String description;
        String label;
        String neededProperty;
        String isDefault;
        
        DuplexesFile(String defaultFilePath, String description, String label, String neededProperty, String isDefault){
            this.defaultFilePath = defaultFilePath;
            this.description = description;
            this.label = label;
            this.neededProperty = neededProperty;
            this.isDefault = isDefault;
        }
        public String getDefaultFilePath() {
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
        
        public String isDefault(){
            return isDefault;
        }

        public boolean isValid(String value) {
            return value.endsWith(".csv");
        }
        
        public void printFile(PrintStream PS, Report report){
            String[] oligomerNames = report.network.getOligomerNames();
            String[] oligomerSequences = report.network.getOligomerSequences();
            //PS.print("indexO1,indexO1B1,indexO2,indexO2B1,base-pairs");
            PS.print("Duplex size(base-pairs),Oligomer 1 name,Oligomer 1 sequence,Index of oligomer 1 first base,Oligomer 2 name,Oligomer 2 sequence,Index of oligomer 2 first base");
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
    
    private static void printReport(PrintStream PS, Report report, Map<String,String> usedParameters) {
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

        // print requested properties
        PS.println();
        PS.println();
        PS.println("********************");
        PS.println("Requested Properties");
        PS.println("********************");
        
        // for each known property. If it was requested, print it.
        for(Map.Entry<String,Property> entry : availableProperties.entrySet()){
            String propLabel = entry.getKey();
            Property p = entry.getValue();
            String value = report.requestedProperties.get(p.analyzerLabel);
            if (value != null) p.printLine(PS,value);
        }
        PS.println();
        PS.println();
        PS.println("**************************");
        PS.println("Other Necessary Properties");
        PS.println("**************************");
        
        // for each known property. If it was necessary, print it.
        for(Map.Entry<String,Property> entry : availableProperties.entrySet()){
            String propLabel = entry.getKey();
            Property p = entry.getValue();
            String value = report.necessaryProperties.get(p.analyzerLabel);
            if (value != null) p.printLine(PS,value);
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