/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.ObjectConverter;

/**
 * @author nikhillo
 * An abstract class that represents a dictionary object for a given index
 */
public abstract class Dictionary implements Writeable {
	static final int capacity = 16;// (int) ((2^20)/0.75+1);
	static final int THRESHOLD = 1000;
	AtomicInteger ATOMIC_TMP_INDEX = new AtomicInteger(0);
	Map<String, Integer> dictionary;
	Properties props;
	INDEXFIELD field;
	private ReadWriteLock rwlock = new ReentrantReadWriteLock();
	//private RandomAccessFile rafWriter;

	public Dictionary(Properties props, INDEXFIELD field) {
		switch (field) {
		case AUTHOR:
			dictionary = new HashMap<String, Integer>(capacity);
			break;
		case CATEGORY:
			dictionary = new HashMap<String, Integer>(capacity);
			break;
		case LINK:
			dictionary = new HashMap<String, Integer>();
			break;
		case TERM:
			dictionary = new HashMap<String, Integer>(capacity);
			break;
		default:
			break;

		}
		this.props = props;
		this.field = field;
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		File dir = new File(FileUtil.getIndexFilesFolder(props));
		dir.mkdir();
		File file = new File(FileUtil.getIndexFilesFolder(props) + field.name()
				+ ".dic");
		RandomAccessFile dictionaryWriter = null;
		ObjectConverter objConvert = null;
		Map <String, String> fileMap = null;
		try {
			dictionaryWriter = new RandomAccessFile(file, "rw");
			objConvert = new ObjectConverter();
			byte[] objectDictionary = null;
			try {
				if (field == INDEXFIELD.LINK) {
					fileMap = new TreeMap<String, String>();
					for (Entry<String, Integer> etr : dictionary.entrySet()) {
						fileMap.put(String.valueOf(etr.getValue()),
								etr.getKey());
					}
					objectDictionary = objConvert.serialize(fileMap);
					dictionaryWriter.writeInt(fileMap.size());
					dictionaryWriter.writeInt(objectDictionary.length);
					// dictionaryWriter.write(0);
					dictionaryWriter.write(objectDictionary);
				} else {
					objectDictionary = objConvert.serialize(dictionary);
					dictionaryWriter.writeInt(dictionary.size());
					dictionaryWriter.writeInt(objectDictionary.length);
					// dictionaryWriter.write(0);
					dictionaryWriter.write(objectDictionary);
				}
				
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
		/*PageBuckets<String, Integer> pageBucket = null;
		try {
			pageBucket = new PageBuckets<String, Integer>(file, field);
			for (Entry<String, Integer> etr : dictionary.entrySet()) {
				pageBucket.save(etr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pageBucket.close();
		}*/
	}
	
	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToTempDisk() throws IndexerException {
		rwlock.writeLock().lock();
		File dirFile = new File(FileUtil.getIndexFilesFolder(props) + File.separator + "tmp");
		if(null == dirFile || !dirFile.exists()) {
			dirFile.mkdir();
		}
		File file = null;
		try {
			file = File.createTempFile(field.name() + "_" + ATOMIC_TMP_INDEX.incrementAndGet(), ".dat", dirFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if(null != file)
				file.deleteOnExit();
			//dirFile.deleteOnExit();
		}
		
		ObjectConverter objConvert = null;
		BufferedWriter writer = null;
		try {
			/*rafWriter = new RandomAccessFile(file, "rw");
			objConvert = new ObjectConverter();
			byte[] objectDictionary = null;*/
				/*objectDictionary = objConvert.serialize(dictionary);
				rafWriter.writeInt(objectDictionary.length);
				// dictionaryWriter.write(0);
				rafWriter.write(objectDictionary);*/
				StringBuilder mapBuilder = new StringBuilder();
				for (Entry<String, Integer> etr : dictionary.entrySet()) {
					mapBuilder.append(etr.getKey());
					mapBuilder.append(",");
					mapBuilder.append(etr.getValue());
					mapBuilder.append("\n");
				}
				writer = new BufferedWriter(new FileWriter(file));
	            writer.write(mapBuilder.toString());
	            mapBuilder = null;
			// dictionary_file.write
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				//rafWriter.close();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				switch (field) {
				case AUTHOR:
					dictionary = new HashMap<String, Integer>(capacity);
					break;
				case CATEGORY:
					dictionary = new HashMap<String, Integer>(capacity);
					break;
				case LINK:
					dictionary = new TreeMap<String, Integer>();
					break;
				case TERM:
					dictionary = new HashMap<String, Integer>(capacity);
					break;
				default:
					break;

				}
				rwlock.writeLock().unlock();
				try {
					this.finalize();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method
		/*if(dictionary.size() > 0) {
			try {
				writeToTempDisk();
			} catch (IndexerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		merge();*/
	}
	
	private void merge() {
		File[] tmpFiles = null;
		File dirFile = new File(FileUtil.getIndexFilesFolder(props) + File.separator + "tmp");
		BufferedReader[] reader = null; 
		int i =0;
		if(dirFile.exists()) {
			tmpFiles = dirFile.listFiles();
			for(File tmpFile : tmpFiles) {
				try {
					reader[i] = new BufferedReader(new FileReader(tmpFile));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * Method to check if the given value exists in the dictionary or not
	 * Unlike the subclassed lookup methods, it only checks if the value exists
	 * and does not change the underlying data structure
	 * @param value: The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		return (dictionary == null ? false : dictionary.containsKey(value));
	}
	
	/**
	 * MEthod to lookup a given string from the dictionary.
	 * The query string can be an exact match or have wild cards (* and ?)
	 * Must be implemented ONLY AS A BONUS
	 * @param queryStr: The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 * null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		// TODO: Implement this method (FOR A BONUS)
		List<String> query = new ArrayList<String>();
		if (queryStr != null) {
			if (queryStr.contains("*") || queryStr.contains("?")) {
				queryStr = queryStr.replaceAll("(\\*|\\?)", ".$1");
				Pattern pattern = Pattern.compile(queryStr,
						Pattern.CASE_INSENSITIVE);
				Set<String> keys = dictionary.keySet();
				Iterator<String> keyIterator = keys.iterator();
				Matcher match = null;
				String value = null;
				while (keyIterator.hasNext()) {
					value = keyIterator.next();
					match = pattern.matcher(value);
					if (null != match && match.matches()) {
						query.add(value);
					}
					match.reset();
				}

				return query;
			} else {
				if (exists(queryStr)) {
					query.add(queryStr);
					return query;
				}
			}
		}
		return null;
	}
	
	/**
	 * Method to get the total number of terms in the dictionary
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		return (dictionary == null ? 0 : dictionary.size());
	}

	/**
	 * @return the dictionary
	 */
	public Map<String, Integer> getDictionary() {
		return dictionary;
	}

}
