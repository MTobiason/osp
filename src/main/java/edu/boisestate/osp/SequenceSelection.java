package osap;
import osap.SequenceSelection;
import osap.Sequence;
import java.util.Arrays;
/**
 * SequuenceSelection objects represent a selection of one or more bases within a Sequence.
 *
 *
 * @author Tobiason
 * @version 2021-02-09
*/

public class SequenceSelection
{
	private boolean[] baseSelected;
	private String sequenceName;
	
	/**
	* Create a SequenceSelection for bases in sequence IS. 
	*
	* @param IS Abbreviation of Incoming Sequence.
	* @param IBI Incoming-Base-Indexes. The bases to be selection. An integer array of values between 0 and (1 - the length of IS). 
	*/
	
	public SequenceSelection(Sequence IS, int[] IBI)
	{
		baseSelected = new boolean[Sequence.getLength(IS)];
		
		Arrays.fill(baseSelected,Boolean.FALSE);
		for( int i : IBI)
		{
			baseSelected[i] = Boolean.TRUE;
		}

		sequenceName = Sequence.getName(IS);
	}
	
	/**
	* Returns true if the base at index IBI is selected;
	* 
	* @param ISS Abbreviation of Incoming-Sequence-Selection.
	* @param IBI Abbreviation of Incoming-Base-Index.
	*/
	
	public static boolean isSelected(SequenceSelection ISS, int IBI)
	{
		return ISS.baseSelected[IBI];
	}
	
	/**
	* Returns true if the base at index IBI is selected;
	* 
	* @param ISS Abbreviation of Incoming-Sequence-Selection.
	* @param IBI Abbreviation of Incoming-Base-Index.
	*/
	
	public static String getSequenceName(SequenceSelection ISS)
	{
		return ISS.sequenceName;
	}
}