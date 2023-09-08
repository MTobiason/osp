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
    final static String PFP_DEFAULT = "dp_parameters.txt";
    final static String EXAMPLE_PARAMETERS_FILE_DEFAULT = "dp_parameters_example.txt";
    
    // input files
    final static String FDFP_LABEL = "fixed_domains_file"; // fixed-domains-file-path
    final static String FDFP_DEFAULT = "dp_in_domains_fixed.txt";
    final static String VDFP_LABEL = "variable_domains_file"; // variable-domains-file-path
    final static String VDFP_DEFAULT = "dp_in_domains_variable.txt";
    final static String ODFP_LABEL = "oligomer_domains_file"; // oligomers-file-path
    final static String ODFP_DEFAULT = "dp_in_oligomers.txt";
    
    // output files
    final static String OUTPUT_DIRECTORY_DEFAULT = "Output" + File.separator;
    final static String OUTPUT_DIRECTORY_LABEL = "outputDirectory";
    
    final static String REPORT_FILE_DEFAULT = "dp_out_report.txt";
    final static String REPORT_FILE_LABEL = "ORFP";
    
    final static String OLIGOMERS_FILE_DEFAULT = "dp_out_oligomers.txt"; //
    final static String OLIGOMERS_FILE_LABEL = "OOFP";
    
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
    
    final static String AC_FILE_DEFAULT = "false";
    final static String AC_FILE_LABEL = "File_Intra_Counts";
    final static String AC_LABEL = "Intra_Counts";
    
    final static String ALD_FILE_DEFAULT = "false";
    final static String ALD_FILE_LABEL = "File_Intra_Largest_Details";
    final static String ALD_LABEL = "Intra_Largest_Details";
    
    final static String APC_FILE_DEFAULT = "false";
    final static String APC_FILE_LABEL = "File_Intra_Prominent_Counts";
    final static String APC_LABEL = "Intra_Prominent_Counts";
    
    final static String APD_FILE_DEFAULT = "false";
    final static String APD_FILE_LABEL = "File_Intra_Prominent_Details";
    final static String APD_LABEL = "Intra_Prominent_Details";
    
    final static String EC_FILE_DEFAULT = "false";
    final static String EC_FILE_LABEL = "File_Inter_Counts";
    final static String EC_LABEL = "Inter_Counts";
    
    final static String ELD_FILE_DEFAULT = "false";
    final static String ELD_FILE_LABEL = "File_Inter_Largest_Details"; 
    final static String ELD_LABEL = "Inter_Largest_Details";
    
    final static String EPC_FILE_DEFAULT = "false";
    final static String EPC_FILE_LABEL = "File_Inter_Prominent_Counts";
    final static String EPC_LABEL = "Inter_Prominent_Counts";
    
    final static String EPD_FILE_DEFAULT = "false";
    final static String EPD_FILE_LABEL = "File_Inter_Prominent_Details"; 
    final static String EPD_LABEL = "Inter_Prominent_Details";
    
    final static String NAC_FILE_DEFAULT = "dp_out_Necessary_Intra_Counts.csv";
    final static String NAC_FILE_LABEL = "File_Necessary_Intra_Counts";
    final static String NAC_LABEL = "Necessary_Intra_Counts";
    
    final static String NALD_FILE_DEFAULT = "false";
    final static String NALD_FILE_LABEL = "File_Necessary_Intra_Largest_Details";
    final static String NALD_LABEL = "Necessary_Intra_Largest_Details";
    
    final static String NAPC_FILE_DEFAULT = "dp_out_Necessary_Intra_Prominent_Counts.csv";
    final static String NAPC_FILE_LABEL = "File_Necessary_Intra_Prominent_Counts";
    final static String NAPC_LABEL = "Necessary_Intra_Prominent_Counts";
    
    final static String NAPD_FILE_DEFAULT = "dp_out_Necessary_Intra_Prominent_Details.csv";
    final static String NAPD_FILE_LABEL = "File_Necessary_Intra_Prominent_Details";  
    final static String NAPD_LABEL = "Necessary_Intra_Prominent_Details";
    
    final static String NEC_FILE_DEFAULT = "dp_out_Necessary_Inter_Counts.csv";
    final static String NEC_FILE_LABEL = "File_Necessary_Inter_Counts";
    final static String NEC_LABEL = "Necessary_Inter_Counts";
    
    final static String NELD_FILE_DEFAULT = "false";
    final static String NELD_FILE_LABEL = "File_Necessary_Inter_Largest_Details"; 
    final static String NELD_LABEL = "Necessary_Inter_Largest_Details"; 
    
    final static String NEPC_FILE_DEFAULT = "dp_out_Necessary_Inter_Prominent_Counts.csv";
    final static String NEPC_FILE_LABEL = "File_Necessary_Inter_Prominent_Counts";
    final static String NEPC_LABEL = "Necessary_Inter_Prominent_Counts";
    
    final static String NEPD_FILE_DEFAULT = "dp_out_Necessary_Inter_Prominent_Details.csv";
    final static String NEPD_FILE_LABEL = "File_Necessary_Inter_Prominent_Details"; 
    final static String NEPD_LABEL = "Necessary_Inter_Prominent_Details";
    
    final static String UAC_FILE_DEFAULT = "dp_out_Unnecessary_Intra_Counts.csv";
    final static String UAC_FILE_LABEL = "File_Unnecessary_Intra_Counts"; // Profile all complete intra
    final static String UAC_LABEL = "Unnecessary_Intra_Counts"; // Profile baseline complete intra
        
    final static String UALD_FILE_DEFAULT = "dp_out_Unnecessary_Intra_Largest_Details.csv"; 
    final static String UALD_FILE_LABEL = "File_Unnecessary_Intra_Largest_Details"; 
    final static String UALD_LABEL = "Unnecessary_Intra_Largest_Details";
    
    final static String UAPC_FILE_DEFAULT = "dp_out_Unnecessary_Intra_Prominent_Counts.csv";
    final static String UAPC_FILE_LABEL = "File_Unnecessary_Intra_Prominent_Counts"; // Profile all Unique intra
    final static String UAPC_LABEL = "Unnecessary_Intra_Prominent_Counts"; // Profile baseline Unique intra
    
    final static String UAPD_FILE_DEFAULT = "false";
    final static String UAPD_FILE_LABEL = "File_Unnecessary_Intra_Prominent_Details"; 
    final static String UAPD_LABEL = "Unnecessary_Intra_Prominent_Details";
    
    final static String UEC_FILE_DEFAULT = "dp_out_Unnecessary_Inter_Counts.csv";
    final static String UEC_FILE_LABEL = "File_Unnecessary_Inter_Counts"; // Profile all complete inter
    final static String UEC_LABEL = "Unnecessary_Inter_Counts"; // Profile baseline complete inter
    
    final static String UELD_FILE_DEFAULT = "dp_out_Unnecessary_Inter_Largest_Details.csv"; 
    final static String UELD_FILE_LABEL = "File_Unnecessary_Inter_Largest_Details"; 
    final static String UELD_LABEL = "Unnecessary_Inter_Largest_Details";
    
    final static String UEPC_FILE_DEFAULT = "dp_out_Unnecessary_Inter_Prominent_Counts.csv";
    final static String UEPC_FILE_LABEL = "File_Unnecessary_Inter_Prominent_Counts"; // Profile all Unique inter
    final static String UEPC_LABEL = "Unnecessary_Inter_Prominent_Counts"; // Profile baseline Unique inter
    
    final static String UEPD_FILE_DEFAULT = "false";
    final static String UEPD_FILE_LABEL = "File_Unnecessary_Inter_Prominent_Details"; 
    final static String UEPD_LABEL = "Unnecessary_Intra_Prominent_Details";
    
    // Score labels
    final static String BASELINE_N_LABEL = "baselineN";
    final static String BASELINE_O_LABEL = "baselineO";
    final static String BASELINE_W_LABEL = "baselineW";
    final static String DELTA_N_LABEL = "deltaN";
    final static String DELTA_O_LABEL = "deltaO";
    final static String DELTA_W_LABEL = "deltaW";
    final static String N_LABEL = "N";
    final static String O_LABEL = "O";    
    final static String W_LABEL = "W";

    // parameter labels
    final static String INTER_SB_LABEL = "interSB";
    final static String INTER_SLC_LABEL = "interSLC";
    final static String INTRA_SB_LABEL = "intraSB";
    final static String INTRA_SLC_LABEL = "intraSLC";
    final static String SWX_LABEL = "scoringWeightX";
    public final static String NUMBER_LARGEST_DUPLEXES_LABEL = "numberLargestDuplexes";
    
    final Analyzer analyzer;
    
    final static ArrayList<Parameter> availableParameters = new ArrayList<>();
    static {
        availableParameters.add(new IntegerParameter(Analyzer.INTER_SB_LABEL, "Inter-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex. Must be an integer greater than or equal to 0 and less than "+Integer.MAX_VALUE+".", INTER_SB_LABEL,"10",0,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter(Analyzer.INTER_SLC_LABEL, "Inter-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", INTER_SLC_LABEL,"1",1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter(Analyzer.INTRA_SB_LABEL, "Intra-oligomer duplexes will contribute points to N equalt to this value raised to the length of the duplex. Must be an integer greater than or equal to 0 and less than "+Integer.MAX_VALUE+".", INTRA_SB_LABEL,"10",0,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter(Analyzer.INTRA_SLC_LABEL, "Intra-oligomer duplexes with base-pairs less than this value do not contribute to profiles or scores. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", INTRA_SLC_LABEL,"1",1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter(Analyzer.NUMBER_LARGEST_DUPLEXES_LABEL, "Maximum number of duplexes to include when listing largest-duplexes. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", NUMBER_LARGEST_DUPLEXES_LABEL,"1000",1,Integer.MAX_VALUE));
        availableParameters.add(new IntegerParameter(Analyzer.SWX_LABEL, "W will be calculated as O times this value plus N. Must be an integer greater than or equal to 1 and less than "+Integer.MAX_VALUE+".", SWX_LABEL,"10000",0,Integer.MAX_VALUE));
    }
    
    final static Map<String,Parameter> labelToParameterMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); 
    static {
        for (Parameter p : availableParameters){
            labelToParameterMap.put(p.getLabel(), p);
        }
    }
    
    final static ArrayList<Property> availableProperties = new ArrayList<>();
    static {
        availableProperties.add(new Property( Analyzer.BASELINE_N_LABEL, "Network Fitness Score resulting from necessary duplexes. Must be true or false. A value of true will request this property.", "true", BASELINE_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL}));
        availableProperties.add(new Property( Analyzer.BASELINE_O_LABEL, "Oligomer Fitness Score resulting from necessary duplexes. Must be true or false. A value of true will request this property.", "true", BASELINE_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL}));
        availableProperties.add(new Property( Analyzer.BASELINE_W_LABEL, "Weighted Fitness Score resulting from necessary duplexes. Must be true or false. A value of true will request this property.", "true", BASELINE_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL}));
        
        availableProperties.add(new Property( Analyzer.DELTA_N_LABEL, "Network Fitness Score resulting from unnecessary duplexes. Must be true or false. A value of true will request this property.", "true", DELTA_N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{BASELINE_N_LABEL, DELTA_N_LABEL, N_LABEL}));
        availableProperties.add(new Property( Analyzer.DELTA_O_LABEL, "Oligomer Fitness Score resulting from unnecessary duplexes. Must be true or false. A value of true will request this property.", "true", DELTA_O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{BASELINE_O_LABEL,DELTA_O_LABEL, O_LABEL}));
        availableProperties.add(new Property( Analyzer.DELTA_W_LABEL, "Weighted Fitness Score resulting from unnecessary duplexes. Must be true or false. A value of true will request this property.", "true", DELTA_W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{BASELINE_N_LABEL,BASELINE_O_LABEL,BASELINE_W_LABEL,DELTA_N_LABEL,DELTA_O_LABEL,DELTA_W_LABEL,N_LABEL,O_LABEL,W_LABEL}));
        
        availableProperties.add(new Property( Analyzer.N_LABEL, "Network Fitness Score. Must be true or false. A value of true will request this property.", "true", N_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL}, new String[]{N_LABEL}));
        availableProperties.add(new Property( Analyzer.O_LABEL, "Oligomer Fitness Score. Must be true or false. A value of true will request this property.", "true", O_LABEL, new String[] {INTRA_SB_LABEL,INTRA_SLC_LABEL}, new String[]{O_LABEL}));
        availableProperties.add(new Property( Analyzer.W_LABEL, "Weighted Fitness Score. Must be true or false. A value of true will request this property.", "true", W_LABEL, new String[] {INTER_SB_LABEL,INTER_SLC_LABEL,INTRA_SB_LABEL,INTRA_SLC_LABEL,SWX_LABEL}, new String[]{N_LABEL,O_LABEL,W_LABEL}));
        
        availableProperties.add(new ProfileProperty( Analyzer.AC_LABEL, "Profile of the length-counts for all intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", AC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{AC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.ALD_LABEL,"List of the largest intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", ALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{ALD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.APC_LABEL, "Profile of the length-counts for the most prominent intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", APC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{APC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.APD_LABEL,"List of all intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", APD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{APD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.EC_LABEL, "Profile of the length-counts for all inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", EC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{EC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.ELD_LABEL,"List of the largest inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", ELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{ELD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.EPC_LABEL, "Profile of the length-counts for the most prominent inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", EPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{EPC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.EPD_LABEL,"List of all inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", EPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{EPD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.NAC_LABEL, "Profile of the length-counts for baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NAC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{NAC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.NALD_LABEL,"List of the largest baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{NALD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.NAPC_LABEL, "Profile of the length-counts for the most prominent baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NAPC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{NAPC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.NAPD_LABEL,"List of baseline intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NAPD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{NAPD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.NEC_LABEL, "Profile of the length-counts for baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NEC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{NEC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.NELD_LABEL,"List of the largest baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{NELD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.NEPC_LABEL, "Profile of the length-counts for the most prominent baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NEPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{NEPC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.NEPD_LABEL,"List of baseline inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", NEPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{NEPD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.UAC_LABEL, "Profile of the length-counts for unnecessary intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UAC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{UAC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.UALD_LABEL,"List of the largest delta intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UALD_LABEL, new String[] {INTRA_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{UALD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.UAPC_LABEL, "Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UAPC_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{UAPC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.UAPD_LABEL,"List of delta intra-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UAPD_LABEL, new String[] {INTRA_SLC_LABEL}, new String[]{UAPD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.UEC_LABEL, "Profile of the length-counts for unnecessary inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UEC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{UEC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.UELD_LABEL,"List of the largest delta inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UELD_LABEL, new String[] {INTER_SLC_LABEL,NUMBER_LARGEST_DUPLEXES_LABEL}, new String[]{UELD_LABEL}));
        availableProperties.add(new ProfileProperty( Analyzer.UEPC_LABEL, "Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UEPC_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{UEPC_LABEL}));
        availableProperties.add(new DuplexProperty(Analyzer.UEPD_LABEL,"List of delta inter-oligomer duplexes. Must be true or false. A value of true will request this property.", "false", UEPD_LABEL, new String[] {INTER_SLC_LABEL}, new String[]{UEPD_LABEL}));
    }
    
    final static Map<String,Property> labelToPropertyMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    static {
        for (Property p : availableProperties){
            labelToPropertyMap.put(p.getLabel(), p);
        }
    }
    
    final static Map<String,Property> analyzerLabelToPropertyMap= new HashMap<>();
    static {
        for(Map.Entry<String,Property> entry : labelToPropertyMap.entrySet()){
            analyzerLabelToPropertyMap.put(entry.getValue().analyzerLabel,entry.getValue());
        }
    }
    
    final static ArrayList<OutputFile> availableOutputFiles = new ArrayList<>();
    static {
        availableOutputFiles.add(new ProfileFile(AC_FILE_DEFAULT, "Profile of the length-counts for all intra-oligomer duplexes. Must be either a file name ending with .csv or false.", AC_FILE_LABEL, AC_LABEL));
        availableOutputFiles.add(new DuplexesFile(ALD_FILE_DEFAULT, "List of the largest unique intra-oligomer duplexes. Must be either a file name ending with .csv or false.", ALD_FILE_LABEL, ALD_LABEL));
        availableOutputFiles.add(new ProfileFile(APC_FILE_DEFAULT, "Profile of the length-counts for the most prominent intra-oligomer duplexes.", APC_FILE_LABEL, APC_LABEL));
        availableOutputFiles.add(new DuplexesFile(APD_FILE_DEFAULT, "List of all prominent intra-oligomer duplexes. Must be either a file name ending with .csv or false.", APD_FILE_LABEL, APD_LABEL));
        availableOutputFiles.add(new ProfileFile(EC_FILE_DEFAULT, "Profile of the length-counts for all inter-oligomer duplexes. Must be either a file name ending with .csv or false.", EC_FILE_LABEL, EC_LABEL));
        availableOutputFiles.add(new DuplexesFile(ELD_FILE_DEFAULT, "List of all unique inter-oligomer duplexes. Must be either a file name ending with .csv or false.", ELD_FILE_LABEL, ELD_LABEL));
        availableOutputFiles.add(new ProfileFile(EPC_FILE_DEFAULT, "Profile of the length-counts for the most prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", EPC_FILE_LABEL, EPC_LABEL));
        availableOutputFiles.add(new DuplexesFile(EPD_FILE_DEFAULT, "List of all prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", EPD_FILE_LABEL, EPD_LABEL));
        availableOutputFiles.add(new ProfileFile(NAC_FILE_DEFAULT, "Profile of the length-counts for necessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", NAC_FILE_LABEL, NAC_LABEL));
        availableOutputFiles.add(new DuplexesFile(NALD_FILE_DEFAULT, "List of baseline unique intra-oligomer duplexes. Must be either a file name ending with .csv or false.", NALD_FILE_LABEL, NALD_LABEL));
        availableOutputFiles.add(new ProfileFile(NAPC_FILE_DEFAULT, "Profile of the length-counts for the most prominent necessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", NAPC_FILE_LABEL, NAPC_LABEL));
        availableOutputFiles.add(new DuplexesFile(NAPD_FILE_DEFAULT, "List of necessary prominent intra-oligomer duplexes. Must be either a file name ending with .csv or false.", NAPD_FILE_LABEL, NAPD_LABEL));
        availableOutputFiles.add(new ProfileFile(NEC_FILE_DEFAULT, "Profile of the length-counts for necessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", NEC_FILE_LABEL, NEC_LABEL));
        availableOutputFiles.add(new DuplexesFile(NELD_FILE_DEFAULT, "List of baseline unique inter-oligomer duplexes. Must be either a file name ending with .csv or false.", NELD_FILE_LABEL, NELD_LABEL));
        availableOutputFiles.add(new ProfileFile(NEPC_FILE_DEFAULT, "Profile of the length-counts for the most prominent necessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", NEPC_FILE_LABEL, NEPC_LABEL));
        availableOutputFiles.add(new DuplexesFile(NEPD_FILE_DEFAULT, "List of necessary prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", NEPD_FILE_LABEL, NEPD_LABEL));
        availableOutputFiles.add(new ProfileFile(UAC_FILE_DEFAULT, "Profile of the length-counts for unnecessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", UAC_FILE_LABEL, UAC_LABEL));
        availableOutputFiles.add(new DuplexesFile(UALD_FILE_DEFAULT, "List of delta unique intra-oligomer duplexes. Must be either a file name ending with .csv or false.", UALD_FILE_LABEL, UALD_LABEL));
        availableOutputFiles.add(new ProfileFile(UAPC_FILE_DEFAULT, "Profile of the length-counts for the most prominent unnecessary intra-oligomer duplexes. Must be either a file name ending with .csv or false.", UAPC_FILE_LABEL, UAPC_LABEL));
        availableOutputFiles.add(new DuplexesFile(UAPD_FILE_DEFAULT, "List of unnecessary prominent intra-oligomer duplexes. Must be either a file name ending with .csv or false.", UAPD_FILE_LABEL, UAPD_LABEL));
        availableOutputFiles.add(new ProfileFile(UEC_FILE_DEFAULT, "Profile of the length-counts for unnecessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", UEC_FILE_LABEL, UEC_LABEL));
        availableOutputFiles.add(new DuplexesFile(UELD_FILE_DEFAULT, "List of delta unique inter-oligomer duplexes. Must be either a file name ending with .csv or false.", UELD_FILE_LABEL, UELD_LABEL));
        availableOutputFiles.add(new ProfileFile(UEPC_FILE_DEFAULT, "Profile of the length-counts for the most prominent unnecessary inter-oligomer duplexes. Must be either a file name ending with .csv or false.", UEPC_FILE_LABEL, UEPC_LABEL));
        availableOutputFiles.add(new DuplexesFile(UEPD_FILE_DEFAULT, "List of unnecessary prominent inter-oligomer duplexes. Must be either a file name ending with .csv or false.", UEPD_FILE_LABEL, UEPD_LABEL));
    }
    
    final static Map<String,OutputFile> labelToOutputFileMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    static {
        for (OutputFile o : availableOutputFiles){
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
            Property p = labelToPropertyMap.get(propertyLabel);
            if (p == null){
                System.err.println("Property "+propertyLabel+" not supported by DevPro.");
                System.exit(1);
            }
            propertiesToRequest.add(p.getAnalyzerLabel());
            for (String paramLabel : p.getNeededParameters()){
                Parameter param = labelToParameterMap.get(paramLabel);
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
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        DevPro dp = new DevPro(availableProcessors,availableProcessors);
        
        Map<String,String> usedParameters = new HashMap<>();
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
                    PS.println("// Format: parameter label <tab> default value <tab> parameter description");
                    PS.println();
                    PS.println("// Numerical parameters");
                    for (Parameter p : availableParameters){
                        PS.println(p.getLabel()+"\t"+p.getDefault()+ "\t// "+p.getDescription());
                    }
                    PS.println();
                    PS.println("// Requestable properties");
                    for (Property p : availableProperties){
                        PS.println(p.getLabel()+"\t"+p.isDefault()+ "\t// "+p.getDescription());
                    }
                    
                    PS.println();
                    PS.println("// Requestable output files");
                    for (OutputFile o : availableOutputFiles){
                        PS.println(o.getLabel()+"\t"+o.getDefaultFilePath()+ "\t// "+o.getDescription());
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
        final Map<String,String> parameters = util.importPairFromTxt(PFP);
        
        Collection<String> propertiesToRequest = new HashSet<>();
        Map<String,String> parametersToProvide = new HashMap<>();
        
        // for each available output file. If requested, request needed properties.
        for(Map.Entry<String,OutputFile> entry : labelToOutputFileMap.entrySet()){
            String fileLabel = entry.getKey();
            OutputFile of = entry.getValue();
            String fp = parameters.get(fileLabel);
            if (fp == null){
                fp = of.getDefaultFilePath();
            }
            usedParameters.put(fileLabel, fp);
            if(!fp.equalsIgnoreCase("false")){
                if (!of.isValid(fp)){
                    System.err.println("File path "+ fp + " invalid for file "+ fileLabel);
                    System.exit(1);
                }
                String[] props = of.getNeededProperties();
                for( String prop: props){
                    Property p = labelToPropertyMap.get(prop);
                    if (p == null){
                        System.err.println("Property "+ prop + " is not supported by DevPro");
                        System.exit(1);
                    }
                    propertiesToRequest.add(prop);
                    for(String paramLabel : p.neededParameters){
                        Parameter param = labelToParameterMap.get(paramLabel);
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
        
        // for each of the available properties. Is it requested?
        for(Map.Entry<String,Property> entry: labelToPropertyMap.entrySet()){
            String propLabel = entry.getKey();
            Property p = entry.getValue();
            boolean active = Boolean.parseBoolean(parameters.getOrDefault(propLabel,p.isDefault()));
            usedParameters.put(propLabel,String.valueOf(active));
            if (active) {
                propertiesToRequest.add(propLabel);
                for(String paramLabel : p.neededParameters){
                    Parameter param = labelToParameterMap.get(paramLabel);
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
        final String ORFP = parameters.getOrDefault(REPORT_FILE_LABEL, REPORT_FILE_DEFAULT);
        usedParameters.put(REPORT_FILE_LABEL,ORFP);
        final String OOSFP = parameters.getOrDefault(OLIGOMERS_FILE_LABEL, OLIGOMERS_FILE_DEFAULT);
        usedParameters.put(OLIGOMERS_FILE_LABEL,OOSFP);
        
        Request request = new Request(network, propertiesToRequest, parametersToProvide);
        Report report = dp.analyze(request);
        
        // print report file.
        try{
            Files.createDirectories(Paths.get(outputDirectory));
            PrintStream PS = new PrintStream( outputDirectory+usedParameters.get(REPORT_FILE_LABEL));
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
        for(Map.Entry<String,OutputFile> entry :  labelToOutputFileMap.entrySet()){
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
    
    private static void printProfileCSV(PrintStream ps, String lengthCounts){
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
    
    private static class Property{
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
    
    private static class ProfileProperty extends Property{
        
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
    
    private static class DuplexProperty extends Property{
        
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
    
    private static class IntegerParameter implements Parameter{
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
    
    private static interface OutputFile{
        public String getDefaultFilePath();
        public String getDescription();
        public String getLabel();
        public String[] getNeededProperties();
        boolean isValid(String value);
        void printFile(PrintStream PS, Report report);
    }
    
    private static class ProfileFile implements OutputFile{
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
        
        public boolean isValid(String value) {
            return value.endsWith(".csv");
        }
        
        public void printFile(PrintStream PS, Report report){
            printProfileCSV(PS,report.requestedProperties.get(neededProperty));
        }
    }
    
    private static class DuplexesFile implements OutputFile{
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
        public String getDefaultFilePath() {
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

        // print requested properties
        PS.println();
        PS.println();
        PS.println("********************");
        PS.println("Requested Properties");
        PS.println("********************");
        
        // for each known property. If it was requested, print it.
        for(Map.Entry<String,Property> entry : labelToPropertyMap.entrySet()){
            String propLabel = entry.getKey();
            Property p = entry.getValue();
            String value = report.requestedProperties.get(p.label);
            if (value != null) p.printLine(PS,value);
        }
        
        PS.println();
        PS.println();
        PS.println("***********************");
        PS.println("Other Needed Properties");
        PS.println("***********************");
        
        // for each known property. If it was necessary, print it.
        for(Map.Entry<String,Property> entry : labelToPropertyMap.entrySet()){
            String propLabel = entry.getKey();
            Property p = entry.getValue();
            String value = report.necessaryProperties.get(p.label);
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