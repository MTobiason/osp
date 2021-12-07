import java.io.* ;
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.IntStream;
import osap.SequenceSet;
import dsap.ParameterSet;
import dsap.StrandSet;

/**
 * Generates oligomer-set sequences with reduced non-target structures
 *
 *
 * @author Tobiason
 * @version 2.0
 * @date 2019-11-14
*/

public class SeqEvo 
{
	private static final Date runDate = new Date();
	private static final String version = new String("2.0");
	private static String PFP = "se.parameters.txt"; //default value for parameters file path
	
	public static void main (String[] args) throws Exception // args: Incoming-Arguments
	{
		// **************************************
		// Check arguments for recognized options 
		// **************************************
		
		for(int i = 0; i < args.length; i++)
		{                 
			if (args[i].equals("-v") || args[i].equals("--version") )
			{
				System.out.println("Sequence-Evolver Version: " + version);
				System.exit(0);
			}
			
			if (args[i].equals("-h") || args[i].equals("--help")) // Print explanation of acceptable arguments.
			{
				System.out.println("Recognized arguments:");
				System.out.println();
				System.out.println(""+"-p [filepath] or --parameters [filepath]");
				System.out.println("\t"+":: Set parameters file to [filepath]"); 
				System.out.println("\t"+":: Default parameters file is: '"+ PFP +"'");
				System.out.println();
				System.out.println(""+"-v or --version");
				System.out.println("\t"+":: Print software version"); 
				System.out.println();
				System.out.println(""+"-h or --help");
				System.out.println("\t"+":: Print this help message"); 
				System.out.println();
				System.exit(0);
			}
			
			if (args[i].equals("-p"))
			{
				PFP = args[i+1]; // accept the next argument as the parameter file
				System.out.println("Using Parameters file: " + PFP); 
			}
		}

		System.out.println("**************************************************");  
		System.out.println("                 Sequence-Evolver                 ");      
		System.out.println("    Software for identifying fit DNA sequences    ");     
		System.out.println("          (c) Boise State University 2019         ");       
		System.out.println("**************************************************"); 
		System.out.println();
		
		//import parameters
		ParameterSet.importParameters(PFP); 
		
		
		System.out.println("Testing Alignments");
		SequenceSet.testAlignments();
		
		//import generation 0
		StrandSet gen0 = new StrandSet("in.FixedDomains.txt","in.VariableDomains.txt","in.Design.txt");
		StrandSet validGen0 = StrandSet.getValidatedStrandSet(gen0);
		
		//get parameters
		int NL = ParameterSet.getNL();
		int NMPC = ParameterSet.getNMPC();
		int NDPM = ParameterSet.getNDPM();
		int CPL = ParameterSet.getCPL();
		int GPC = ParameterSet.getGPC();
		
		double startTime = System.currentTimeMillis(); // start timer for runtime.
		
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
		
		
		FileWriter filewriter = new FileWriter( "se.out.report.txt" ); //initiate the export for initial lineage mothers
		PrintWriter PW = new PrintWriter( filewriter);
		StrandSet.export(validGen0,PW);
		StrandSet.export(fittestSystem,PW);
		PW.close();
		
		System.out.println("Runtime of Evolutionary Process: " + totalTime);
		System.out.println();
	}
}