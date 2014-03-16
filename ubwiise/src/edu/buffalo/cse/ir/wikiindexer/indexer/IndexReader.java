/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.ObjectConverter;

/**
 * @author nikhillo This class is used to introspect a given index The
 *         expectation is the class should be able to read the index and all
 *         associated dictionaries.
 */
public class IndexReader {

	private RandomAccessFile[] indexReader;
	private RandomAccessFile dictionaryReader;
	private INDEXFIELD field;
	private Properties props;
	private int noOfFiles;
	private int totalKeys;
	private int totalValues;

	/**
	 * Constructor to create an instance
	 * 
	 * @param props
	 *            : The properties file
	 * @param field
	 *            : The index field whose index is to be read
	 */
	public IndexReader(Properties props, INDEXFIELD field) {
		// TODO: Implement this method
		this.props = props;
		this.field = field;
		this.noOfFiles = 1;
	
		initialize_Reader();
	}

	private void initialize_Reader() {
		String fileName = field.name();
		File file = null;
		int total = 0;
		
		try {
			dictionaryReader = new RandomAccessFile(new File(
					FileUtil.getIndexFilesFolder(props) + INDEXFIELD.LINK
							+ ".dic"), "r");
			totalValues = dictionaryReader.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (field.equals(INDEXFIELD.TERM)) {
			noOfFiles = Partitioner.getNumPartitions();
			indexReader = new RandomAccessFile[noOfFiles];
			for (int i = 0; i < noOfFiles; i++) {
				fileName = field.name() + "_" + i;
				file = new File(FileUtil.getIndexFilesFolder(props) + fileName
						+ ".idx");
				try {
					indexReader[i] = new RandomAccessFile(file, "r");
					total += indexReader[i].readInt();
				} catch (FileNotFoundException fnf) {
					System.out.println("Index files not found or corrupted.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				indexReader = new RandomAccessFile[noOfFiles];
				file = new File(FileUtil.getIndexFilesFolder(props) + fileName
						+ ".idx");
				indexReader[0] = new RandomAccessFile(file, "r");
				total = indexReader[0].readInt();
			} catch (FileNotFoundException fnf) {
				System.out.println("Index files not found or corrupted.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.totalKeys = total;
	}

	/**
	 * Method to get the total number of terms in the key dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
		// TODO: Implement this method
		return this.totalKeys;
	}

	/**
	 * Method to get the total number of terms in the value dictionary
	 * 
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		// TODO: Implement this method
		return totalValues;
	}

	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * 
	 * @param key
	 *            : The dictionary term to be queried
	 * @return The postings list with the value term as the key and the number
	 *         of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		// TODO: Implement this method
		initialize_Reader();
		try {
			Map<String, PostingsList> dictionary = new HashMap<String, PostingsList>();
			Map<String, Integer> postings = new TreeMap<String, Integer>();
			PostingsList postingsList = null;
			if (key == null || key.isEmpty()) {
				return postings;
			}
			try {
				dictionary = readIndex(key, -1);
				if (dictionary != null) {
					postingsList = dictionary.get(key);
				}
				if (postingsList != null) {
					Map<Integer, String> docDict = readDictionary();
					for(Entry<String, Integer> etr : postingsList.getPostingsList().entrySet()) {
						postings.put(docDict.get(etr.getKey()), etr.getValue());
					}
				}
			} finally {
				dictionary = null;
			}

			return postings;
		} finally {
			close();
		}
	}

	/**
	 * This Utility method is used to load the partition into memory 1. Used to
	 * getPostings given a term. In this case Partition needs to be identified.
	 * 2. Used to load a particular partition. Partition number is passed in
	 * argument.
	 * 
	 * @param key
	 * @param partion
	 * @return
	 */
	private Map<String, PostingsList> readIndex(String key, int partion) {
			Map<String, PostingsList> dictionary = new HashMap<String, PostingsList>();
			int partNum = noOfFiles - 1;

			// Partition will -1 for getting postings list as it needs to be
			// computed using key.
			// To load a particular partition the partition number is provided.
			// So key is discarded.
			if (field == INDEXFIELD.TERM) {
				if (partion == -1) {
					partNum = Partitioner.getPartitionNumber(key);
				} else {
					partNum = partion;
				}
			}
			FileChannel inChannel = indexReader[partNum].getChannel();
			MappedByteBuffer buffer = null;
			try {
				buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0,
						inChannel.size());
				buffer.load();
				buffer.getInt();
				int dictSize = buffer.getInt();
				byte[] objectDictionary = new byte[dictSize];
				buffer.wrap(objectDictionary);
				buffer.get(objectDictionary);
				dictionary = (Map<String, PostingsList>) ObjectConverter
						.deserialize(objectDictionary);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (buffer != null) {
					buffer.clear();
				}
				try {
					inChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return dictionary;
	}
	/**
	 * One Time Operation for each operation
	 * @param key
	 * @return
	 */
	private Map<Integer, String> readDictionary() {
			Map<Integer, String> dictionary = new HashMap<Integer, String>();

			try {
				int byteSize = dictionaryReader.readInt();
				byte[] b = new byte[byteSize];
				ByteBuffer buff = ByteBuffer.wrap(b);
				buff.get();
				dictionaryReader.readFully(b);
				dictionary = (Map<Integer, String>) ObjectConverter
						.deserialize(b);
				buff.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return dictionary;
	}
	

	private String[] preProcess(String... queryTerms) {
		// TODO Auto-generated method stub
		return queryTerms;
	}

	/**
	 * Method to get the top k key terms from the given index The top here
	 * refers to the largest size of postings.
	 * 
	 * @param k
	 *            : The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the
	 *         requirement If k is more than the total size of the index, return
	 *         the full index and don't pad the collection. Return null in case
	 *         of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
		// TODO: Implement this method
		initialize_Reader();
		try {
			Map<String, PostingsList> theWholeIndex = new HashMap<String, PostingsList>();

			for (int i = 0; i < noOfFiles; i++) {
				for(Entry<String, PostingsList> etr: readIndex("", i).entrySet())
					theWholeIndex.put(etr.getKey(), etr.getValue());
			}
			
			ValueComparator vac = new ValueComparator(theWholeIndex);
			Map<String, Integer> topKTerms = new TreeMap<String, Integer>(vac);
			
			if (theWholeIndex == null || theWholeIndex.size() == 0) {
				return topKTerms.keySet();
			}
			if(field == INDEXFIELD.LINK) {
				Map<Integer, String> docDict = readDictionary();
				String key = null;
				for (Entry<String, PostingsList> etr : theWholeIndex.entrySet()) {
					key = docDict.get(etr.getKey());
					topKTerms.put(key, etr.getValue().getPostingsList().size());
				}
			} else {
				for (Entry<String, PostingsList> etr : theWholeIndex.entrySet()) {
					topKTerms.put(etr.getKey(), etr.getValue().getPostingsList().size());
				}
			}
			
			
			if (k > totalKeys) {
				return topKTerms.keySet();
			} else {
				List<String> val = new ArrayList<String>();
				if (topKTerms != null) {
					val = new ArrayList<String>(topKTerms.keySet());
				}
				if(k >= val.size()) {
					k = val.size() - 1;
				}
				return val.subList(0, k);
			}
		} finally {
			close();
		}
	}

	/**
	 * Method to execute a boolean AND query on the index
	 * 
	 * @param terms
	 *            The terms to be queried on
	 * @return An ordered map containing the results of the query The key is the
	 *         value field of the dictionary and the value is the sum of
	 *         occurrences across the different postings. The value with the
	 *         highest cumulative count should be the first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		// TODO: Implement this method (FOR A BONUS)
		initialize_Reader();
		try {
			if (terms == null || terms.length == 0) {
				return null;
			}
			Map<Integer, List<String>> partMap = new HashMap<Integer, List<String>>();
			Map<String, PostingsList> postings = new TreeMap<String, PostingsList>();
			PostingsList qPosts = null;
			List<String> partTerms = null;
			for (String term : terms) {
				partTerms = partMap.get(Partitioner.getPartitionNumber(term));
				if (null == partTerms) {
					partTerms = new ArrayList<String>();
				}
				partTerms.add(term);
				partMap.put(Partitioner.getPartitionNumber(term), partTerms);
			}
			List<Map<String, Integer>> pList = new ArrayList<Map<String, Integer>>();
			for (Entry<Integer, List<String>> etr : partMap.entrySet()) {
				postings = readIndex("", etr.getKey());
				for (String str : etr.getValue()) {
					qPosts = postings.get(str);
					if (qPosts != null) {
						pList.add(qPosts.getPostingsList());
					}

				}
			}
			Collections.sort(pList, new Comparator<Map<String, Integer>>() {

				@Override
				public int compare(Map<String, Integer> o1,
						Map<String, Integer> o2) {
					// TODO Auto-generated method stub
					return o1.size() - o2.size();
				}
			});

			return intersect(pList);
		} finally {
			close();
		}
	}

	private Map<String, Integer> intersect(List<Map<String, Integer>> pList) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (pList == null || pList.size() == 0) {
			return result;
		}
		if (pList.size() == 1) {
			result = pList.get(0);
		} else {
			for (Map<String, Integer> map : pList) {
				result = intersect(result, map);
			}
		}
		Map<Integer, String> docDict = readDictionary();
		String key = null;
		
		Map<String, Integer> documentResult = new HashMap<String, Integer>();
		for (Entry<String, Integer> etr : result.entrySet()) {
			key = docDict.get(etr.getKey());
			documentResult.put(key, etr.getValue());
		}
		FreqComparator freqComparator = new FreqComparator(documentResult);
		Map<String, Integer> finalResult = new TreeMap<String, Integer>(freqComparator);
		finalResult.putAll(documentResult);
		return finalResult;

	}

	private Map<String, Integer> intersect(Map<String, Integer> p1,
			Map<String, Integer> p2) {
		Map<String, Integer> answer = new TreeMap<String, Integer>();
		if (p1 == null || p1.size() == 0) {
			if (p2 == null || p2.size() == 0) {
				return answer;
			} else {
				return p2;
			}
		} else {
			if (p2 == null || p2.size() == 0)
				return p1;
		}
		// Iterate through the smaller postings list
		for (Entry<String, Integer> etr : p1.entrySet()) {
			String key = etr.getKey();
			Integer value = etr.getValue();
			if (p2.containsKey(key)) {
				answer.put(key, value + p2.get(key));
			}
		}
		return answer;
	}

	public void close() {
		try {
			dictionaryReader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (RandomAccessFile fileReader : indexReader) {
			try {
				fileReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Properties properties = null;
		try {
			properties = FileUtil.loadProperties("./files/properties.config");
			IndexReader indexReader = new IndexReader(properties,
					INDEXFIELD.AUTHOR);
			System.out.println("AUTHOR");
			System.out.println("-------");
			System.out.println(indexReader.getTotalKeyTerms());
			System.out.println(indexReader.getTotalValueTerms());
			System.out.println(indexReader.getTopK(10));
			System.out.println(indexReader.getTopK(100000000).size());
			indexReader = new IndexReader(properties, INDEXFIELD.CATEGORY);
			System.out.println("CATEGORY");
			System.out.println("---------");
			System.out.println(indexReader.getTotalKeyTerms());
			System.out.println(indexReader.getTotalValueTerms());
			System.out.println(indexReader.getTopK(10));
			System.out.println(indexReader.getTopK(100000000).size());
			indexReader = new IndexReader(properties, INDEXFIELD.LINK);
			System.out.println("LINK");
			System.out.println("-------");
			System.out.println(indexReader.getTotalKeyTerms());
			System.out.println(indexReader.getTotalValueTerms());
			System.out.println(indexReader.getTopK(10));
			System.out.println(indexReader.getTopK(100000000).size());
			indexReader = new IndexReader(properties, INDEXFIELD.TERM);
			System.out.println("TERM");
			System.out.println("-------");
			System.out.println(indexReader.getTotalKeyTerms());
			System.out.println(indexReader.getTotalValueTerms());
			System.out.println(indexReader.readIndex("", 0).keySet());
			//System.out.println(indexReader.readIndex("", 1).keySet());
			/*System.out.println(indexReader.query("bolotin","default"));
			System.out.println(indexReader.query("bolotin"));
			System.out.println(indexReader.getPostings("bolotin"));
			System.out.println(indexReader.getTopK(10));
			System.out.println(indexReader.getTopK(100000000).size());
			System.out.println(indexReader.getPostings("dileepunik13"));
			System.out.println(indexReader.query("dileepunik13","default"));
			System.out.println(indexReader.query("philosophy","Engineering"));
			System.out.println(indexReader.getPostings("dileepunik33"));
			System.out.println(indexReader.getPostings("dileepunik"));*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Value based comparators for TreeMap
	 * @author mata
	 *
	 */
	class FreqComparator implements Comparator<String> {

		Map<String, Integer> base;

		public FreqComparator(Map<String, Integer> base) {
			this.base = base;
		}

		@Override
		public int compare(String o1, String o2) {
			Integer p1 = base.get(o1);
			Integer p2 = base.get(o2);
			if (p1 == null || p2 == null) {
				return 1;
			}
			if (p1 >= p2) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
	
	class ValueComparator implements Comparator<String> {

		Map<String, PostingsList> base;

		public ValueComparator(Map<String, PostingsList> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			PostingsList p1 = base.get(a);
			PostingsList p2 = base.get(b);
			if (p1 == null || p2 == null) {
				return 1;
			}
			if (p1.getPostingsList().size() >= p2.getPostingsList().size()) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
