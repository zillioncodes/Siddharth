package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class PageBuckets<K, V> {
	static int HASH_PARTITIONS = 13;
	private List<Page<K, V>> pages;
	private INDEXFIELD field;

	public PageBuckets(File file, INDEXFIELD field) {
		this.field = field;
		pages = new ArrayList<Page<K, V>>(HASH_PARTITIONS);
		for (int i = 0; i < HASH_PARTITIONS; i++) {
			pages.add(new Page<K, V>(file, i));
		}
	}

	public void save(Entry<K, V> pageEntry) {
		K key = pageEntry.getKey();
		Page<K, V> page = findPage(key);
		page.save(pageEntry, field);
	}

	public Entry<K, V> get(K key) {
		return findPage(key).load(key);
	}

	private Page<K, V> findPage(K key) {
		int idx = key.hashCode() % HASH_PARTITIONS;
		return pages.get(Math.abs(idx));
	}

	public void close() {
		for (Page<K, V> page : pages) {
			page.close();
		}
	}

}
