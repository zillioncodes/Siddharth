package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PostingsList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8910629259707114924L;
	private static int capacity = 16;//(int) ((100000) / 0.75 + 1);
	private Map<String, Integer> postingsList;

	public PostingsList(INDEXFIELD keyField) {
		switch (keyField) {
		case AUTHOR:
			this.postingsList = new TreeMap<String, Integer>();
			break;
		case CATEGORY:
			this.postingsList = new TreeMap<String, Integer>();
			break;
		case LINK:
			this.postingsList = new HashMap<String, Integer>();
			break;
		case TERM:
			this.postingsList = new TreeMap<String, Integer>();
			break;
		default:
			break;

		}
	}

	public void addPostings(int valueId, int numOccurances) {
		String value = String.valueOf(valueId);
		Integer prevNumOccurances = postingsList.get(value);
		if (null != prevNumOccurances) {
			numOccurances += prevNumOccurances;
		}
		postingsList.put(value, numOccurances);
	}

	/**
	 * @return the postingsList
	 */
	public Map<String, Integer> getPostingsList() {
		return postingsList;
	}

	/**
	 * @param postingsList
	 *            the postingsList to set
	 */
	public void setPostingsList(Map<String, Integer> postingsList) {
		this.postingsList = postingsList;
	}

}
