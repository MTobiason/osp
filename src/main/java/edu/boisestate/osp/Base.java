package osap;

/**
 * Immutable object representing a given DNA base.
 *
 *
 *
 * @author Tobiason
 * @version 2021-02-08
*/

public class Base
{
	
	final private String name;
	final private Base complement;
	
	static private Base adenine;
	static private Base guanine;
	static private Base cytosine;
	static private Base thymine;
	
	static {
		adenine = new Base("A","T");
		guanine = new Base("G","C");
		thymine = getComplement(adenine);
		cytosine = getComplement(guanine);
	}

	/**
	* Creates a new Base object, with the name "IN" and complement "IC".
	*
	* @param IN Abbrevaition of Incoming-Name
	* @param IC Abbreviation of Incoming-Complement
	*/
	
	private Base(String IN, Base IC)
	{
		name = IN;
		complement = IC;
	}
	
	/**
	 * Creates a new Base object with a generic name.
	 *  
	*/
	
	private Base()
	{
		name = "X";
		complement = new Base("Y",this);
	}
	
	/**
	* Creates a new Base object with the name "IN", and a complementary base with the name "IN2" .
	*
	* @param IN1 Abbrevaition of Incoming-Name-One
	*
	* @param IN2 Abbreviation of Incoming-Name-Two
	*/
	
	private Base(String IN1, String IN2)
	{
		name = IN1;
		complement = new Base(IN2,this);
	}
	
	/**
	/* Returns the complement of Base "IB".
	/*
	/* @param IB Abbreviation of Incoming-Base
	*/
	
	static public Base getComplement(Base IB)
	{
		return IB.complement;
	}
	
	/**
	* Returns true if Bases "IB1" and "IB2" are complementary.
	*
	* @param IB1 Abbrevaition of Incoming-Base-One
	*
	* @param IB2 Abbreviation of Incoming-Base-Two
	*/
	
	static public boolean areComplements(Base IB1, Base IB2)
	{
		return (IB1.complement == IB2);
	}
	
	/**
	* Returns the base with the given name. 
	*
	* @param IN Abbreviation of Incoming-Name
	* 
	*/
	
	static public Base getExistingBase(String IN)
	{
		switch (IN)
		{
			case "A":
			case "a":
				return adenine;
			case "T":
			case "t":
				return thymine;
			case "C":
			case "c":
				return cytosine;
			case "G":
			case "g":
				return guanine;
			case "x":
			case "X":
				return guanine;	
			default:
				break;
		}
		
		System.out.println("Error. Did not recognize \""+IN+ "\" as an existing base.");
		System.exit(0);
		return new Base();
		
	}
	
	/**
	* Returns a generic base with name "X" and Complement "Y"
	*
	*/
	
	static public Base getGenericBase()
	{
		Base newBase = new Base();
		return newBase;
	}
	
	/**
	* Returns the name assigned to base "IB"
	*
	* @param IN Abbreviation of Incoming-Base
	* 
	*/
	
	static public String getName(Base IB)
	{
		return IB.name;
	}
}