package osap;
import osap.Sequence;
import osap.WCStructure;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.ArrayList;

import java.util.Collection;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.List;
/**
 * The WCStructureSet class is intended for storing a list of structures.
 *
 *
 *
 * @author Tobiason
 * @version 2021-02-09
*/

public class WCStructureSet
{	
	final private List<WCStructure> structures;
	
	/**
	* Create an empty WSStructureSet.
	*
	* 
	*/
	
	public WCStructureSet()
	{
		structures = new ArrayList<WCStructure>(0);
	}
	
	/**
	* Create a WSStructureSet from an array of structures.
	*
	* @param IS Abbreviation of Incoming-Structures.
	*
	*/
	
	public WCStructureSet( WCStructure[] IS)
	{
		structures = new ArrayList<WCStructure>(Arrays.asList(IS));
	}
	
	/**
	* Create a WSStructureSet from an List of structures.
	*
	* @param IS Abbreviation of Incoming-Structures-List.
	*
	*/
	
	public WCStructureSet( Collection<WCStructure> ISL)
	{
		structures = new ArrayList<WCStructure>(ISL);
	}
	
	/**
	* Create a WCStructureSet by merging two existing WCStructureSets.
	*
	* @param ISS1 Abbreviation of Incoming-Structure-Set-1
	* @param ISS2 Abbreviation of Incoming-Structure-Set-2
	*
	*/
	
	public WCStructureSet(WCStructureSet ISS1, WCStructureSet ISS2)
	{
		structures = new ArrayList<WCStructure>(ISS1.structures.size()+ISS2.structures.size());
		structures.addAll(ISS1.structures);
		structures.addAll(ISS2.structures);
	}
	
	public static WCStructure[] getWCStructures(WCStructureSet ISS)
	{
		return ISS.structures.toArray(new WCStructure[0]);
	}
	
	public static int getSize(WCStructureSet ISS)
	{
		return ISS.structures.size();
	}
}