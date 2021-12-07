package osap;
import osap.Base;


/**
 * Immutable class representing a given DNA sequence.
 *
 *
 *
 * @author Tobiason
 * @version 2021-02-08
*/

public class Sequence
{
	
	private Base[] sequenceBases;
	private String sequenceString;
	private String complementString;
	private String name;
	
	/**
	 * Creates a new Sequence object from a given sequence name and a array of bases.
	 * 
	 * @param IN Abbreviation of Incoming-Name.
	 * @param IB Abbrevaition of Incoming-Bases.
	 
	*/
	
	public Sequence(String IN, Base[] IB)
	{
		name = IN;		
		sequenceBases = IB.clone();
	}
	
	/**
	 * Creates a new Sequence object from a given sequence name and a string of bases.
	 * 
	 * @param IN Abbreviation of Incoming-Name.
	 * @param IS Abbrevaition of Incoming-Sequence.
	 
	*/
	
	public Sequence(String IN, String IS)
	{
		name = IN;
		sequenceString = IS;
		sequenceBases = new Base[sequenceString.length()];
		
		for(int i = 0; i< IS.length(); i++)
		{
			sequenceBases[i] = Base.getExistingBase(String.valueOf(IS.charAt(i)));
		}
	}
	
	/**
	* Returns a string containing the bases in the sequence
	*
	* @param IS Abbreviation of Incoming-Sequence.
	*/
	
	static public String getSequenceString( Sequence IS)
	{
		if (IS.sequenceString == null)
		{
			String s = new String();
			
			for( Base i : IS.sequenceBases)
			{
				s = s + Base.getName(i);
			}
			IS.sequenceString = s;
		}
		return IS.sequenceString;
	}
	
	/**
	* Returns a string containing the binding complement of the sequence
	*
	* @param IS Abbreviation of Incoming-Sequence.
	*/
	
	static public String getComplementString( Sequence IS)
	{
		if (IS.complementString == null)
		{
			String s = new String();
			
			for(int i = IS.sequenceBases.length-1; i >= 0; i--)
			{
				s = s + Base.getName(Base.getComplement(IS.sequenceBases[i]));
			}
			IS.complementString = s;
		}
		return IS.complementString;
	}
	
	static public String getName(Sequence IS)
	{
		return IS.name;
	}
	
	static public int getLength(Sequence IS)
	{
		return IS.sequenceBases.length;
	}
	
	static public Base[] getBases(Sequence IS)
	{
		return IS.sequenceBases;
	}
}