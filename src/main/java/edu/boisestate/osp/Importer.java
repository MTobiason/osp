package osap;
import osap.Base;
import osap.Sequence;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Calculates the fitness of an Oligo-set
 *
 *
 * @author Tobiason
 * @version 1.0
 * @date 2021-01-29
*/

public class Importer 
{
	public Importer()
	{
	}
	
	/**
	* Creates Sequence-Set by importing from .txt file IFP.
	* Each line of the file should have a sequence name, a space or tab seperator, and then a series of bases for the sequence. 
	* The sequence name may contain letters, numbers, "_", and/or "-". It may not contain spaces.
	* The base sequence may contains the letters A, T, C, or G. These represent Adensine, Thymine, Cytosine, and Guanine respectively.
	*
	* @param IFP Incoming-File-Path
	*/
	
	public static SequenceSet importSequenceSetFromTXT(String IFP)
	{
		ArrayList<Sequence> tempSequences = new ArrayList<Sequence>();
		Sequence tempSequence;
		
		try
		{
			File file = new File(IFP);
			Scanner scanner1 = new Scanner(file);
			
			while( scanner1.hasNextLine()) // for each line of input file, until end of file
			{
				String lineText = scanner1.nextLine();
				Scanner scanner2 = new Scanner(lineText);
				if( !lineText.startsWith("//") && scanner2.hasNext())
				{
					String key = scanner2.next(); //record the sequence name
					String value = scanner2.next().toUpperCase(); //record the sequence.
					tempSequence = new Sequence(key,value);
					tempSequences.add(tempSequence);
				}
				scanner2.close();
			}  
			scanner1.close();
		}
		catch (Exception e)
		{
			System.out.println("Error while importing Sequences from "+ IFP + " :: ");
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		SequenceSet newSequenceSet = new SequenceSet(tempSequences.toArray(new Sequence[0]));
		return newSequenceSet;	
	}
}