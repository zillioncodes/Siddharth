package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.ObjectConverter;

public class Page<K, V> {
	// private Map<Integer, Long> locationMap;
	private long bucketSize;
	private static long GLOBAL_BASELOCATION = 130;
	private static int OFFSET = 1000;
	private static int HEADER_SIZE = 8;
	long localbaseLocation;
	private ReadWriteLock rwlock = new ReentrantReadWriteLock();
	private RandomAccessFile rafWriter;
	private RandomAccessFile rafReader;
	private int bucketId;

	public Page(File file, int i) {
		// locationMap = new HashMap<Integer, Long>();
		localbaseLocation = GLOBAL_BASELOCATION + i * OFFSET;
		bucketSize = 0;
		this.bucketId = i;
		try {
			rafWriter = new RandomAccessFile(file, "rw");
			rafReader = new RandomAccessFile(file, "r");
			rafWriter.seek(localbaseLocation);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (null != rafReader) {
					rafReader.close();
				}
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			}
			try {
				if (null != rafReader) {
					rafWriter.close();
				}
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			}
		}
	}

	public void save(Entry<K, V> pageEntry, INDEXFIELD field) {
		rwlock.writeLock().lock();
		try {

			byte[] keyBuffer = ObjectConverter.serialize(pageEntry.getKey());
			byte[] valueBuffer = ObjectConverter
					.serialize(pageEntry.getValue());
			int entryBuffer = keyBuffer.length + valueBuffer.length;
			// long location = rafWriter.getFilePointer();
			if(field == INDEXFIELD.LINK) {
				rafWriter.writeInt(valueBuffer.length);
				rafWriter.write(valueBuffer);
				rafWriter.writeInt(keyBuffer.length);
				rafWriter.write(keyBuffer);
			} else {
				rafWriter.writeInt(keyBuffer.length);
				rafWriter.write(keyBuffer);
				rafWriter.writeInt(valueBuffer.length);
				rafWriter.write(valueBuffer);
			}
				// Total bytes required for each ENTRY<K,V>
				bucketSize += entryBuffer + 8;
			// locationMap.put(pageEntry.getKey().hashCode(), location);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			rwlock.writeLock().unlock();
		}
	}

	public void writeHeader() {
		rwlock.writeLock().lock();
		try {
			rafWriter.seek(bucketId * HEADER_SIZE);
			rafWriter.writeLong(bucketSize);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			rwlock.writeLock().unlock();
		}
	}

	public void readHeader() {
		rwlock.readLock().lock();
		try {
			rafReader.seek(bucketId * HEADER_SIZE);
			bucketSize = rafReader.readLong();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			rwlock.readLock().unlock();
		}
	}

	public Entry<K, V> load(K key) {
		rwlock.readLock().lock();
		Entry<K, V> output = null;
		try {
			// Long location = locationMap.get(key);
			rafReader.seek(localbaseLocation);
			int byteSize = rafReader.readInt();
			byte[] k = new byte[byteSize];
			ByteBuffer buff = ByteBuffer.wrap(k);
			buff.get();
			rafReader.read(k);
			buff.clear();
			byteSize = rafReader.readInt();
			byte[] v = new byte[byteSize];
			buff = ByteBuffer.wrap(v);
			buff.get();
			rafReader.read(v);
			buff.clear();
			K o_key = (K) ObjectConverter.deserialize(k);
			V o_value = (V) ObjectConverter.deserialize(v);
			Map<K, V> map = new HashMap<K, V>();
			map.put(o_key, o_value);
			for (Entry<K, V> etr : map.entrySet()) {
				output = etr;
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			rwlock.readLock().unlock();
		}
		return output;
	}

	public void close() {
		try {
			writeHeader();
			rafReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rafWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
