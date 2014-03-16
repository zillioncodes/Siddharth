/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.ObjectConverter;

/**
 * @author nikhillo
 * This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {

	/* private LocalDictionary localDictionary; */
	static int capacity = 16; // (int) ((1000000) / 0.75 + 1);
	private boolean isForward;
	private Map<String, PostingsList> theIndex;
	private INDEXFIELD keyField, valueField;
	private Properties props;
	private int partitionNum;

	/**
	 * Constructor that assumes the underlying index is inverted
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}
	
	/**
	 * Overloaded constructor that allows specifying the index type as
	 * inverted or forward
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 * @param isForward: true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField,
			INDEXFIELD valueField, boolean isForward) {
		// TODO: Implement this method
		this.isForward = isForward;
		this.keyField = keyField;
		this.valueField = valueField;
		this.props = props;
		theIndex = new HashMap<String, PostingsList>(capacity);
/*
		switch (keyField) {
		case AUTHOR:
			localDictionary = new LocalDictionary(props, keyField);
			break;
		case CATEGORY:
			localDictionary = new LocalDictionary(props, keyField);
			break;
		case TERM:
			localDictionary = new LocalDictionary(props, keyField);
			break;
		default:
			break;
		}
*/
	}
	
	/**
	 * Method to make the writer self aware of the current partition it is handling
	 * Applicable only for distributed indexes.
	 * @param pnum: The partition number
	 */
	public void setPartitionNumber(int pnum) {
		// TODO: Optionally implement this method
		partitionNum = pnum;
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances)
			throws IndexerException {
		// TODO: Implement this method
		String key = String.valueOf(keyId);

		// Check whether the Index already contains any posting list for the
		// corresponding id
		PostingsList postingList = theIndex.get(key);
		// Create new posting list if the id doesn't exist in Index
		if (postingList == null) {
			postingList = new PostingsList(keyField);
		}
		// Add the postings to Index
		postingList.addPostings(valueId, numOccurances);
		theIndex.put(key, postingList);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances)
			throws IndexerException {
		// Check whether the Index already contains any posting list for the
		// corresponding id
        PostingsList postingList = theIndex.get(key);
		// Create new posting list if the id doesn't exist in Index
		if (postingList == null) {
			postingList = new PostingsList(keyField);
		}
		// Add the postings to Index
		postingList.addPostings(valueId, numOccurances);
		theIndex.put(key, postingList);
	}
	
	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		String fileName = keyField.name();
		if (keyField.equals(INDEXFIELD.TERM)) {
			fileName = keyField.name() + "_" + partitionNum;
		}
		File dir = new File(FileUtil.getIndexFilesFolder(props));
		dir.mkdir();
		File file = new File(FileUtil.getIndexFilesFolder(props) + fileName
				+ ".idx");
		RandomAccessFile dictionaryWriter = null;
		try {
			dictionaryWriter = new RandomAccessFile(file, "rw");
			byte[] objectDictionary = null;
			try {
				objectDictionary = ObjectConverter.serialize(theIndex);
				dictionaryWriter.writeInt(theIndex.size());
				dictionaryWriter.writeInt(objectDictionary.length);
				// dictionaryWriter.write(0);
				dictionaryWriter.write(objectDictionary);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// dictionary_file.write
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				dictionaryWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method

	}

}
