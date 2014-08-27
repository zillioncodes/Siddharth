/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.Properties;

/**
 * @author nikhillo
 * This class represents a subclass of a Dictionary class that is
 * local to a single thread. All methods in this class are
 * assumed thread safe for the same reason.
 */
public class LocalDictionary extends Dictionary {
	
	/**
	 * Public default constructor
	 * @param props: The properties file
	 * @param field: The field being indexed by this dictionary
	 */
	public LocalDictionary(Properties props, INDEXFIELD field) {
		super(props, field);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Method to lookup and possibly add a mapping for the given value
	 * in the dictionary. The class should first try and find the given
	 * value within its dictionary. If found, it should return its
	 * id (Or hash value). If not found, it should create an entry and
	 * return the newly created id.
	 * @param value: The value to be looked up
	 * @return The id as explained above.
	 */
	public int lookup(String value) {
		if (value == null || value.trim() == "") {
			return -1;
		}
		// NEED TO CALL WRite to temp disk
		/*if (dictionary.size() >= THRESHOLD) {
			try {
				writeToTempDisk();
			} catch (IndexerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		int id = value.hashCode();
		if (!exists(value)) {
			dictionary.put(value, id);
		}

		return id;
	}

}
