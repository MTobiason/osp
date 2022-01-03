/*
 * The MIT License
 *
 * Copyright 2021 mtobi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.boisestate.osp.analyzer;

import edu.boisestate.osp.seqevo.ISeqEvoAnalyzer;
import edu.boisestate.osp.sequence.Base;
import edu.boisestate.osp.sequence.LinearSequence;
import edu.boisestate.osp.structure.SimpleSecondaryStructure;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DevProAnalyzer implements ISeqEvoAnalyzer
{
	public DevProAnalyzer()
	{
	}
	
	/**
	* Calculate and return all intra-oligo structures in the Sequence "IS".
	*
	* @param sequence Abbreviation of Incoming-Sequence
	*/
	
	private static SimpleSecondaryStructure[] getUniqueIntramolecularStructures(LinearSequence sequence)
	{
		LinearSequence S1 = sequence;
		LinearSequence S2 = sequence;
		Base[] S1bases = S1.getBases();
		Base[] S2bases = S1bases;
		int S1length = S1bases.length;
		int S2length = S2bases.length;
		int minstructure = 1;				
		int b1Max = S1length-1;
		
		// for every base in S1 (aka reference position)
		Collection<WCStructure> SSS = 
			IntStream.range(0,S1length)
			.parallel()
			.mapToObj(j->
			{
				Collection<WCStructure> ASS = new HashSet<WCStructure>();
				int structureLength = 0;
				int b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
				int b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;
				
				int length = S1length/2;
				if(S1length % 2 == 0 && j%2 == 1)
				{
					length = length -1;
				}
			
				if( Base.areComplements(S1bases[b1],S2bases[b2]))
				{
					structureLength = 1;
				}
				
				//For every base-pair in the reference position
				for ( int k =1; k < length; k++)
				{
					if( b1 == b1Max) 
					{
						if (structureLength >= minstructure)
						{
							ASS.add(new WCStructure(S1,S2,b1-structureLength+1,b2,structureLength));
						}
						b1 = 0;
						structureLength = 0;
					} else {b1++;}
					
					if( b2 == 0) 
					{
						if (structureLength >= minstructure)
						{
							ASS.add(new WCStructure(S1,S2,b1-structureLength, b2, structureLength));
						}
						b2 = b1Max;
						structureLength = 0;
					} else {b2--;}
					
					if(Base.areComplements(S1bases[b1],S2bases[b2]))
					{
						structureLength++;
					}
					else
					{
						if (structureLength >= minstructure)
						{
							ASS.add(new WCStructure(S1,S2, b1-structureLength, b2+1, structureLength));
						}
						structureLength =0;
					}
				}
					
				//if the loop ended with an active structure, record it.
				if (structureLength >= minstructure)
				{
					ASS.add(new WCStructure(S1,S2, b1-structureLength+1, b2, structureLength));
				};
				return ASS;
			})
			.collect(HashSet<WCStructure>::new,(a,b)->a.addAll(b),(a,b)->a.addAll(b));
			
		WCStructureSet intraSS = new WCStructureSet(SSS);
		return intraSS;
	}
	
	/**
	* Calculate and return the lengths of all intra-oligo structures in the Sequence "IS".
	*
	* @param IS Abbreviation of Incoming-Sequence
	*/
	
	private static Collection<Integer> getUniqueIntraOligoStructureLengths(Sequence IS)
	{
		Sequence S1 = IS;
		Sequence S2 = IS;
		Base[] S1bases = Sequence.getBases(IS);
		Base[] S2bases = S1bases;
		int S1length = S1bases.length;
		int S2length = S2bases.length;
		int minstructure = 1;				
		int b1Max = S1length-1;
		
		// for every base in S1 (aka reference position)
		Collection<Integer> allLengths = 
			IntStream.range(0,S1length)
			.parallel()
			.mapToObj(j->
			{
				//Collection<WCStructure> ASS = new HashSet<WCStructure>();
				Collection<Integer> ASS = new ArrayList<Integer>();
				int structureLength = 0;
				int b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
				int b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;
				
				int length = S1length/2;
				if(S1length % 2 == 0 && j%2 == 1)
				{
					length = length -1;
				}
			
				if( Base.areComplements(S1bases[b1],S2bases[b2]))
				{
					structureLength = 1;
				}
				
				//For every base-pair in the reference position
				for ( int k =1; k < length; k++)
				{
					if( b1 == b1Max) 
					{
						if (structureLength >= minstructure)
						{
							//ASS.add(new WCStructure(S1,S2,b1-structureLength+1,b2,structureLength));
							ASS.add(structureLength);
						}
						b1 = 0;
						structureLength = 0;
					} else {b1++;}
					
					if( b2 == 0) 
					{
						if (structureLength >= minstructure)
						{
							//ASS.add(new WCStructure(S1,S2,b1-structureLength, b2, structureLength));
							ASS.add(structureLength);
						}
						b2 = b1Max;
						structureLength = 0;
					} else {b2--;}
					
					if(Base.areComplements(S1bases[b1],S2bases[b2]))
					{
						structureLength++;
					}
					else
					{
						if (structureLength >= minstructure)
						{
							//ASS.add(new WCStructure(S1,S2, b1-structureLength, b2+1, structureLength));
							ASS.add(structureLength);
						}
						structureLength =0;
					}
				}
					
				//if the loop ended with an active structure, record it.
				if (structureLength >= minstructure)
				{
					//ASS.add(new WCStructure(S1,S2, b1-structureLength+1, b2, structureLength));
					ASS.add(structureLength);
				};
				return ASS;
			})
			.collect(ArrayList<Integer>::new,(a,b)->a.addAll(b),(a,b)->a.addAll(b));
		return allLengths;
	}
	
	/** 
	* Calculate the points for WCStructureSet ISS by assigning 10^L points to each structure such that L is the length of the structure.
	*
	* @param ISS Abbreviation of Incoming-Structure-Set
	*/
	
	private static BigInteger calculatePoints(WCStructureSet ISS)
	{
		BigInteger score = BigInteger.valueOf(0);
		TreeMap<Integer,Integer> counts = new TreeMap<Integer,Integer>();
		int length = 0;
		Integer currentValue = 0;
		int newValue = 0;
		
		//create profile of the unique structures (size,count)
		for( WCStructure i : WCStructureSet.getWCStructures(ISS))
		{
			length = WCStructure.getLength(i);
			currentValue = counts.get(length);
			if (currentValue != null)
			{
				newValue = currentValue + 1;
			}
			else { newValue =  1;}
			counts.put(length,newValue);
		}
				
		//create profile of all structures (size,count)
		TreeMap<Integer,Integer> completeCounts = new TreeMap<Integer,Integer>();
		int numberOfStructures=0;
		Integer k = 0;
		
		for(Map.Entry<Integer,Integer> i : counts.entrySet())
		{
			length = i.getKey();
			for (int j = 0; j < length; j++)
			{
				numberOfStructures = (j+1)*i.getValue();
				k = completeCounts.get(length-j);
				if (k!=null)
				{
					numberOfStructures = numberOfStructures + k;
				}
				completeCounts.put(length-j,numberOfStructures);
			}
		}
		
		//Calculate total points
		for(Map.Entry<Integer,Integer> i : completeCounts.entrySet())
		{
			length = i.getKey();
			numberOfStructures = i.getValue();
			score = score.add(BigInteger.valueOf(10).pow(length).multiply(BigInteger.valueOf(numberOfStructures)));
		}
		
		return score;
	}
	
	/** 
	* Calculate the points for a collection of Lengths ICL by assigning 10^L points to each structure such that L is the length of the structure.
	*
	* @param ICL Abbreviation of Incoming-Collection-Lengths
	*/
	
	private static BigInteger calculatePoints(Collection<Integer> ICL)
	{
		BigInteger score = BigInteger.valueOf(0);
		TreeMap<Integer,Integer> counts = new TreeMap<Integer,Integer>();
		int length = 0;
		Integer currentValue = 0;
		int newValue = 0;
		
		//create profile of the unique structures (size,count)
		for( Integer i : ICL)
		{
			length = i;
			currentValue = counts.get(length);
			if (currentValue != null)
			{
				newValue = currentValue + 1;
			}
			else { newValue =  1;}
			counts.put(length,newValue);
		}
		
		//create profile of all structures (size,count)
		TreeMap<Integer,Integer> completeCounts = new TreeMap<Integer,Integer>();
		int numberOfStructures=0;
		Integer k = 0;
		
		for(Map.Entry<Integer,Integer> i : counts.entrySet())
		{
			length = i.getKey();
			for (int j = 0; j < length; j++)
			{
				numberOfStructures = (j+1)*i.getValue();
				k = completeCounts.get(length-j);
				if (k!=null)
				{
					numberOfStructures = numberOfStructures + k;
				}
				completeCounts.put(length-j,numberOfStructures);
			}
		}
		
		//Calculate total points
		for(Map.Entry<Integer,Integer> i : completeCounts.entrySet())
		{
			length = i.getKey();
			numberOfStructures = i.getValue();
			score = score.add(BigInteger.valueOf(10).pow(length).multiply(BigInteger.valueOf(numberOfStructures)));
		}
		
		return score;
	}
	
	/** 
	* Returns the total oligo fitness score (O) for SequenceSet ISS.
	*
	* @param ISS Abbreviation of Incoming-Structure-Set
	*/
	
	public static BigInteger getTotalOligoFitnessScore(SequenceSet ISS)
	{
		BigInteger score = BigInteger.valueOf(0);
		Sequence[] allSequences = SequenceSet.getSequences(ISS);
		List<BigInteger> allScores = Arrays.stream(allSequences)
			.parallel()
			.map(i->
			{
				Collection<Integer> structureLengths = getUniqueIntraOligoStructureLengths(i);
				BigInteger subScore = calculatePoints(structureLengths);
				return subScore;
			})
			.collect(Collectors.toList());
		
		for(BigInteger i : allScores)
		{
			score = score.add(i);
		}
		
		return score;		
	}
	
	/**
	* Returns the oligo fitness score (O_Oligo) for sequence "IS".
	* 
	* @param IS Abbreviation of Incoming-Sequence.
	*/
	
	public static BigInteger getOligoFitnessScore(Sequence IS)
	{
		Collection<Integer> structureLengths = getUniqueIntraOligoStructureLengths(IS);
		BigInteger tempScore = calculatePoints(structureLengths);
		return tempScore;
	}
	
	/**
	* Returns the partial oligo fitness score (O_Oligo_Selection) for sequence "ISeq" base on selection ISel.
	* 
	* @param ISeq Abbreviation of Incoming-Sequence.
	* @param ISel Abbreviation of Incoming-Selection.
	*/
	
	public static BigInteger getPartialOligoFitnessScore(Sequence ISeq, SequenceSelection ISel)
	{
		WCStructureSet tempStructures = getPartialUniqueIntraOligoStructures(ISeq,ISel);
		BigInteger tempScore = calculatePoints(tempStructures);
		return tempScore;
	}
	
	/**
	* Returns the set of all structures which involve the selection ISel for sequence ISeq. 
	* 
	* @param ISeq Abbreviation of Incoming-Sequence.
	* @param ISel Abbreviation of Incoming-Selection.
	*/
	
	private static WCStructureSet getPartialUniqueIntraOligoStructures(Sequence ISeq, SequenceSelection ISel)
	{
		if( SequenceSelection.getSequenceName(ISel) != Sequence.getName(ISeq))
		{
			return new WCStructureSet();
		}
		
		Sequence S1 = ISeq;
		Sequence S2 = ISeq;
		Base[] S1bases = Sequence.getBases(ISeq);
		Base[] S2bases = S1bases;
		int S1length = S1bases.length;
		int S2length = S2bases.length;
		int minstructure = 1;				
		int b1Max = S1length-1;
		
		// for every base in S1 (aka reference position)
		Collection<WCStructure> SSS = 
			IntStream.range(0,S1length)
			.parallel()
			.mapToObj(j->
			{
				Collection<WCStructure> ASS = new ArrayList<WCStructure>();
				int structureLength = 0;
				boolean involvesSelection = false;
				int b1 = (S1length - (j)/2) % S1length; // index of base on the top strand;
				int b2 = (b1Max -((j+1)/2)) ;// index of base on the bottom strand;
				
				int length = S1length/2;
				if(S1length % 2 == 0 && j%2 == 1)
				{
					length = length -1;
				}
			
				if( Base.areComplements(S1bases[b1],S2bases[b2]))
				{
					structureLength = 1;
					if( SequenceSelection.isSelected(ISel,b1) || SequenceSelection.isSelected(ISel,b2))
					{
						involvesSelection = true;
					}
				}
				
				//For every base-pair in the reference position
				for ( int k =1; k < length; k++)
				{
					if( b1 == b1Max) 
					{
						if (structureLength >= minstructure && involvesSelection)
						{
							ASS.add(new WCStructure(S1,S2,b1-structureLength+1,b2,structureLength));
						}
						b1 = 0;
						structureLength = 0;
						involvesSelection = false;
					} else {b1++;}
					
					if( b2 == 0) 
					{
						if (structureLength >= minstructure && involvesSelection)
						{
							ASS.add(new WCStructure(S1,S2,b1-structureLength, b2, structureLength));
						}
						b2 = b1Max;
						structureLength = 0;
						involvesSelection = false;
					} else {b2--;}
					
					if(Base.areComplements(S1bases[b1],S2bases[b2]) )
					{
						structureLength++;
						if( SequenceSelection.isSelected(ISel,b1) || SequenceSelection.isSelected(ISel,b2))
						{
							involvesSelection = true;
						}
					}
					else
					{
						if (structureLength >= minstructure && involvesSelection)
						{
							ASS.add(new WCStructure(S1,S2, b1-structureLength, b2+1, structureLength));
						}
						structureLength =0;
						involvesSelection = false;
					}
				}
					
				//if the loop ended with an active structure, record it.
				if (structureLength >= minstructure && involvesSelection)
				{
					ASS.add(new WCStructure(S1,S2, b1-structureLength+1, b2, structureLength));
				};
				return ASS;
			})
			.collect(ArrayList<WCStructure>::new,(a,b)->a.addAll(b),(a,b)->a.addAll(b));
			
		WCStructureSet intraSS = new WCStructureSet(SSS);
		return intraSS;
	}
	
	/**
	* Returns the Fitness Score for all structures which may form between Sequence "IS1" and Sequence "IS2". 10^L Points are accumulated for each inter-oligo structure with length L.
	*
	* @param IS1 Abbreviation of Incoming-Sequence-One 
	* @param IS2 Abbreviation of Incoming-Sequence-Two
	*/
	
	public static BigInteger getPairFitnessScore(Sequence IS1, Sequence IS2)
	{
		WCStructureSet structures = getUniqueInterOligoStructures(IS1,IS2);
		BigInteger tempScore = calculatePoints(structures);
		return tempScore;
	}
	
	/**
	* Calculate and return all inter-oligo structures which may occur between Sequence "IS1" and Sequence "IS2".
	*
	* @param IS1 Abbreviation of Incoming-Sequence-One
	* @param IS2 Abbreviation of Incoming-Sequence-Two
	*/
	
	private static WCStructureSet getUniqueInterOligoStructures(Sequence IS1, Sequence IS2)
	{
		Sequence S1 = IS1;
		Sequence S2 = IS2;
		Base[] S1Bases = Sequence.getBases(S1);
		Base[] S2Bases = Sequence.getBases(S2);
		int S1length = S1Bases.length;
		int S2length = S2Bases.length;
		int minstructure = 1;				
		int b1Max = S1length-1;
		int b2Max = S2length-1;
		
		
		//int S1Index = i[0];
		//int S2Index = i[1];
		//int[] S1 = ISS.sequenceBases[S1Index];
		//int[] S2 = ISS.sequenceBases[S2Index];
		//int b1Max = S1.length-1;
		//int b2Max = S2.length-1;
		
		//System.out.println(S1Index+", "+S2Index);
		
		//Iterate through all reference positions
		Collection<WCStructure> allStructuresCollection = 
			IntStream.range(0,b2Max+1)
			.parallel()
			.mapToObj(j->
			{
				//StructureProfile ASP = new StructureProfile();
				
				ArrayList<WCStructure> referencePositionStructures = new ArrayList<WCStructure>();
				int structureLength = 0;
				int b1 = 0; // index of base on the top strand;
				int b2 = (b2Max + j ) % (b2Max+1);// index of base on the bottom strand;
				
				// consider the first base pair.
				if (Base.areComplements(S1Bases[b1],S2Bases[b2]))
				{
					structureLength = 1;
				};
				
				while (b1 < b1Max)
				{
					b1++;
					if( b2 == 0) 
					{
						if (structureLength > 1)
						{
							referencePositionStructures.add(new WCStructure(S1, S2, b1-structureLength, 0, structureLength));
							
						}
						b2 = b2Max;
						structureLength = 0;
					} else {b2--;};
					
					//if the bases are complementary, increase structure length, else record structure;
					if (Base.areComplements(S1Bases[b1],S2Bases[b2]))
					{
						structureLength++;
					} else
					{
						if (structureLength > 1)
						{
							referencePositionStructures.add(new WCStructure(S1, S2, b1-structureLength, b2+1, structureLength));
							//StructureProfile.add(ASP, structureLength);
						}
						structureLength = 0;
					};
				};
				
				//if the loop ended with an active structure, record it.
				if (structureLength > 1)
				{
					referencePositionStructures.add(new WCStructure(S1, S2, b1-structureLength+1, b2, structureLength));
					//StructureProfile.add(ASP, structureLength);
				};
				
				return referencePositionStructures;
			})
			.collect(ArrayList<WCStructure>::new, (response, element) -> response.addAll(element), (response1, response2) -> response1.addAll(response2))
			;
			
		WCStructureSet allStructuresSS = new WCStructureSet(allStructuresCollection.toArray(new WCStructure[0]));
		return allStructuresSS;
	}
}
