package osap;
import osap.Sequence;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
/**
 * WCStructure objects represent a stretch of complementary bases.
 *
 *
 * @author Tobiason
 * @version 2021-02-09
*/

public class WCStructure
{
	private Sequence S1;
	private Sequence S2;
	private int indexS1B1;
	private int indexS2B1;
	private int length;
	
	/**
	* Create a WCStructure from the relevant information. 
	*
	*
	* @param IS1 Abbreviation of Incoming Sequence 1.
	* @param IS2 Abbreviation of Incoming Sequence 2.
	* @param IS1B1 Abbreviation of Incoming Sequence 1 Base 1. The index of the first base on sequence 1.
	* @param IS2B1 Abbreviation of Incoming Sequence 2 Base 1. The index of the first base on sequence 2.
	*/
	public WCStructure(Sequence IS1,Sequence IS2, int IS1B1, int IS2B1, int IL)
	{
		S1 = IS1;
		S2 = IS2;
		indexS1B1 = IS1B1;
		indexS2B1 = IS2B1;
		length = IL;
	}
	
	/**
	* Returns the length (in bases) of structure IS.
	*
	* @param IS Abbreviation of Incoming-Structure
	*
	*/
	
	public static int getLength(WCStructure IS)
	{
		return IS.length;
	}
}