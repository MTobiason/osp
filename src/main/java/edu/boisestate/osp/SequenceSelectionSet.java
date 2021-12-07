package osap;
import osap.SequenceSelection;
import osap.Sequence;
import java.util.HashMap;
import java.util.ArrayList;
/**
 * SequuenceSelectionSet objects represent a group of selections.
 *
 *
 * @author Tobiason
 * @version 2021-02-10
*/

public class SequenceSelectionSet
{
	private SequenceSelection[] selections;
	private HashMap<String,ArrayList<SequenceSelection>> selectionMap;
	
	/**
	* Create a SequenceSelectionSet from the SequenceSelection array ISS. 
	*
	* @param ISS Abbreviation of Incoming-Sequence-Selections.
	*/
	
	public SequenceSelectionSet(SequenceSelection[] ISS)
	{
		selections = ISS.clone();
		String tempName;
		ArrayList<SequenceSelection> tempAL;
		
		for (SequenceSelection i : selections)
		{
			tempName = SequenceSelection.getSequenceName(i);
			tempAL = selectionMap.get(tempName);
			if(tempAL == null)
			{
				tempAL = new ArrayList<SequenceSelection>(2);
				tempAL.add(i);
				selectionMap.put(tempName,tempAL);
			}
			else
			{
				tempAL.add(i);
			}
		}
	}
	
	/**
	* Returns an array containing all the selections.
	* 
	* @param ISSS Abbreviation of Incoming-Sequence-Selection-Set.
	* @param IBI Abbreviation of Incoming-Base-Index.
	*/
	
	public static SequenceSelection[] getSelections(SequenceSelectionSet ISSS)
	{
		return ISSS.selections.clone();
	}
	
	/**
	* Returns the array of SequenceSelections for sequence name ISN in SequenceSelectionSet ISSS.
	*
	* @param ISSS Abbreviation of Incoming-Sequence-Selection-Set.
	* @param ISN Abbreviation of Incoming-Sequence-Name
	*/
	public static SequenceSelection[] getSelection(SequenceSelectionSet ISSS, String ISN)
	{
		return ISSS.selectionMap.get(ISN).toArray(new SequenceSelection[0]);
	}
}