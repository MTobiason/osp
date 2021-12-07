package osap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import osap.Sequence;

/**
 * The SequenceSet class is a class for storing, importing, printing
 * or exporting sets of sequences. It is intended to provide utility
 * storing both domain or strand sequences.
 *
 *
 *
 * @author Tobiason
 * @version 2021-02-08
*/

public class SequenceSet
{
	private Sequence[] sequences;
	private final HashMap<String,Integer> nameToSequenceIndex;
	
	/**
	* Create a new sequence set by replacing sequences in the set.
	*
	* @param ISS Abbreviation of Incoming-Sequence-Set
	* @param ISA Abbreviation of incoming-Sequence-Array
	*/
	
	public SequenceSet(SequenceSet ISS, Sequence[] ISA)
	{
		sequences = ISS.sequences.clone();
		nameToSequenceIndex = ISS.nameToSequenceIndex;
		
		//replace with new sequences.
		
		for (Sequence x : ISA)
		{
			sequences[nameToSequenceIndex.get(Sequence.getName(x))] = x;
		}			
	}

	/**
	* Create a new sequence set by replacing sequences in the set.
	*
	* @param ISS Abbreviation of Incoming-Sequence-Set
	* @param IS Abbreviation of incoming-Sequence
	*/
	
	public SequenceSet(SequenceSet ISS, Sequence ISA)
	{
		sequences = ISS.sequences.clone();
		nameToSequenceIndex = ISS.nameToSequenceIndex;
		
		//replace with new sequences.
		sequences[nameToSequenceIndex.get(Sequence.getName(ISA))] = ISA;
	}
	
	/**
	* Create a SequenceSet from Sequence Array ISA.
	* 
	* @param IS Abbreviation of Incoming-Sequence-Array
	*/
	public SequenceSet( Sequence[] ISA)
	{
		sequences = ISA.clone();
		nameToSequenceIndex = new HashMap<String,Integer>();
		for (int i = 0; i < ISA.length; i++)
		{
			nameToSequenceIndex.put(Sequence.getName(ISA[i]), i);
		}
	}
	
	/**
	* Returns the array of sequences stored in SequenceSet ISS
	*
	* @param ISS Abbreviation of Incoming-Sequence-Set.
	*/
	public static Sequence[] getSequences(SequenceSet ISS)
	{
		return ISS.sequences.clone();
	}
	
	public static Sequence getSequence(SequenceSet ISS, String ISN)
	{
		return ISS.sequences[ISS.nameToSequenceIndex.get(ISN)];
	}
	
}