/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author nikhillo
 * THis class is responsible for assigning a partition to a given term.
 * The static methods imply that all instances of this class should 
 * behave exactly the same. Given a term, irrespective of what instance
 * is called, the same partition number should be assigned to it.
 */
public class Partitioner {
	/**
	 * Method to get the total number of partitions
	 * THis is a pure design choice on how many partitions you need
	 * and also how they are assigned.
	 * @return: Total number of partitions
	 */
	//public static Map <Character, Integer> partMap = new TreeMap<Character, Integer>(); 
	
	public static int getNumPartitions() {
		// TODO: Implement this method
		return 11;
	}
	
	/**
	 * Method to fetch the partition number for the given term.
	 * The partition numbers should be assigned from 0 to N-1
	 * where N is the total number of partitions.
	 * @param term: The term to be looked up
	 * @return The assigned partition number for the given term
	 */
	public static int getPartitionNumber(String term) {
		// TDOD: Implement this method
		int partition = 0;
		if (term != null) {

			Character part = term.toLowerCase().charAt(0);
			if (Character.isDigit(part)) {
				partition = 0;
			} else {
				switch (part) {
				default:
					partition = 0;
				break;
				case 'a':
				case 'b':
					partition = 1;
					break;
				case 'c':
					partition = 2;
					break;
				case 'd':
				case 'e':
					partition = 3;
					break;
				case 'f':
				case 'g':
				case 'h':
					partition = 4;
					break;
				case 'i':
				case 'j':
				case 'k':
				case 'l':
					partition = 5;
					break;
				case 'm':
				case 'n':
				case 'o':
					partition = 6;
					break;
				case 'p':
				case 'q':
				case 'u':
					partition = 7;
					break;
				case 'r':
				case 't':
					partition = 8;
					break;
				case 's':
					partition = 9;
					break;
				case 'v':
				case 'w':
				case 'x':
				case 'y':
				case 'z':
					partition = 10;
					break;
				/*default:
					partition = 11;*/
				}
			}

			/*Integer a = partMap.get(part);
			if (null != a) {
				partMap.put(part, a + 1);
			} else {
				partMap.put(part, 1);
			}*/
		}
		return partition;
	}
}
