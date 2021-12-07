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
import java.util.stream.Collectors;

/**
 * Temporary code for calculating primer fitness. 
 *
 *
 * @author Tobiason
 * @date 2021-02-11
*/

public class PrimerFitnessCalculator 
{
	public static void main (String[] args) throws Exception // args: Incoming-Arguments
	{
		// **************************************
		// Check arguments for recognized options 
		// **************************************
		String oligoFilePath = "in.OligoSequences.txt";
		
		SequenceSet oligoSequenceSet = Importer.importSequenceSetFromTXT(oligoFilePath);
		Sequence[] oligoSequences = SequenceSet.getSequences(oligoSequenceSet);
		
		int numberSequences = oligoSequences.length;
		SequenceSelection[] leftPrimerSequenceSelections = new SequenceSelection[numberSequences]; 
		SequenceSelection[] rightPrimerSequenceSelections = new SequenceSelection[numberSequences]; 
		BigInteger[] leftPrimerSequenceScores = new BigInteger[numberSequences];
		BigInteger[] rightPrimerSequenceScores = new BigInteger[numberSequences];
		BigInteger[] oligoFitnessScores = new BigInteger[numberSequences];
		
		Analyzer analyzer = new Analyzer();
		
		System.out.println(Arrays.toString(IntStream.range(0,20).toArray()));
		System.out.println(Arrays.toString(IntStream.range(250-20,250).toArray()));
		
		
		for (int i = 0; i < numberSequences; i++)
		{
			int oligoLength = Sequence.getLength(oligoSequences[i]);
			leftPrimerSequenceSelections[i] = new SequenceSelection(oligoSequences[i], IntStream.range(0,20).toArray());
			rightPrimerSequenceSelections[i] = new SequenceSelection(oligoSequences[i], IntStream.range(oligoLength-21,oligoLength).toArray());
			oligoFitnessScores[i] = analyzer.getOligoFitnessScore(oligoSequences[i]);
			leftPrimerSequenceScores[i] = analyzer.getPartialOligoFitnessScore(oligoSequences[i],leftPrimerSequenceSelections[i]);
			rightPrimerSequenceScores[i] = analyzer.getPartialOligoFitnessScore(oligoSequences[i],rightPrimerSequenceSelections[i]);
		}
		
		for( int i =0; i < numberSequences; i++)
		{
			System.out.println(Sequence.getName(oligoSequences[i])+"," +Sequence.getSequenceString(oligoSequences[i])+", " + oligoFitnessScores[i] + ", "+ leftPrimerSequenceScores[i]+"," +rightPrimerSequenceScores[i]);
		}
		
		FileWriter filewriter = new FileWriter( "out.primerAnalysis.csv" ); //initiate the export for initial lineage mothers
		PrintWriter PW = new PrintWriter( filewriter);
		PW.println("Oligo Name,Base Sequence,O_Oligo,O_Oligo_Left-Primer,O_Oligo_Right-Primer");

		
		for( int i =0; i < numberSequences; i++)
		{
			PW.print(Sequence.getName(oligoSequences[i])+"," +Sequence.getSequenceString(oligoSequences[i])+","+ oligoFitnessScores[i] + ","+ leftPrimerSequenceScores[i] +"," +rightPrimerSequenceScores[i]);
			PW.println();
		}
		PW.close();
	}
}