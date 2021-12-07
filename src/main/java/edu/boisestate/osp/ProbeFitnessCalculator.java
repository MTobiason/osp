import java.io.* ;
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;

import osap.Importer;
import dsap.ParameterSet;
import osap.Sequence;
import osap.SequenceSet;
import osap.SequenceSelection;
import osap.SequenceSelectionSet;
import java.math.BigInteger;
import osap.Analyzer;
import java.util.stream.IntStream;

/**
 * Temporary code for calculating staple fitness. 
 *
 *
 * @author Tobiason
 * @date 2021-02-10
*/

public class ProbeFitnessCalculator 
{
	public static void main (String[] args) throws Exception // args: Incoming-Arguments
	{
		// **************************************
		// Check arguments for recognized options 
		// **************************************
		String stapleFilePath = "in.StapleSequences.txt";
		String probeFilePath = "in.ProbeSequences.txt";
		
		SequenceSet stapleSequenceSet = Importer.importSequenceSetFromTXT(stapleFilePath);
		Sequence[] stapleSequences = SequenceSet.getSequences(stapleSequenceSet);
		
		SequenceSet probeSequenceSet = Importer.importSequenceSetFromTXT(probeFilePath);
		Sequence[] probeSequences = SequenceSet.getSequences(probeSequenceSet);
		
		int numberSequences = stapleSequences.length;
		SequenceSelection[] probeSequenceSelections = new SequenceSelection[numberSequences]; 
		BigInteger[] probeSequenceScores = new BigInteger[numberSequences];
		BigInteger[] oligoFitnessScores = new BigInteger[numberSequences];
		BigInteger[] nSelfFitnessScores = new BigInteger[numberSequences];
		BigInteger[] nComplementFitnessScores = new BigInteger[numberSequences];
		
		int numberProbes = probeSequences.length;
		int probeLength = 9;
		
		BigInteger[][] probeNetworkScores = new BigInteger[numberSequences][numberProbes];
		
		Analyzer analyzer = new Analyzer();
		
		for (int i = 0; i < numberSequences; i++)
		{
			int stapleLength = Sequence.getLength(stapleSequences[i]);
			probeSequenceSelections[i] = new SequenceSelection(stapleSequences[i], IntStream.range(stapleLength-(probeLength+1),stapleLength).toArray());
			oligoFitnessScores[i] = analyzer.getOligoFitnessScore(stapleSequences[i]);
			probeSequenceScores[i] = analyzer.getPartialOligoFitnessScore(stapleSequences[i],probeSequenceSelections[i]);
			nSelfFitnessScores[i] = analyzer.getPairFitnessScore(stapleSequences[i],stapleSequences[i]);
			nComplementFitnessScores[i] = analyzer.getPairFitnessScore(stapleSequences[i],new Sequence("Complement",Sequence.getComplementString(stapleSequences[i])));
			for (int j = 0; j < numberProbes; j++)
			{
				probeNetworkScores[i][j] = Analyzer.getPairFitnessScore(stapleSequences[i],probeSequences[j]);
			}
		}
		
		for( int i =0; i < numberSequences; i++)
		{
			System.out.println(Sequence.getName(stapleSequences[i])+"," +Sequence.getSequenceString(stapleSequences[i])+", " + oligoFitnessScores[i] + ", "+ probeSequenceScores[i]);
		}
		
		FileWriter filewriter = new FileWriter( "out.probeAnalysis.csv" ); //initiate the export for initial lineage mothers
		PrintWriter PW = new PrintWriter( filewriter);
		PW.print("Sequence Name,Base Sequence,N_Self,N_Complement,O_Oligo,O_Oligo_Last-"+probeLength+"-Bases");
		for( Sequence i : probeSequences)
		{
			PW.print(",N_Oligo-Probe-"+Sequence.getName(i));
		}
		PW.println();
		
		for( int i =0; i < numberSequences; i++)
		{
			PW.print(Sequence.getName(stapleSequences[i])+"," +Sequence.getSequenceString(stapleSequences[i])+","+ nSelfFitnessScores[i] +","+ nComplementFitnessScores[i] +","+ oligoFitnessScores[i] + ","+ probeSequenceScores[i]);
			for (int j = 0; j < numberProbes; j++)
			{
				PW.print(","+probeNetworkScores[i][j]);
			}
			PW.println();
		}
		PW.close();
	}
}