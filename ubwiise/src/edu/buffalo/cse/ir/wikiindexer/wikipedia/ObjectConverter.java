package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Utility class to serialize and deserialize dictionaries
 */
public class ObjectConverter {

	public static <T> byte[] serialize(T dictionary) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(outStream);
		objStream.writeObject(dictionary);
		outStream.flush();
		return outStream.toByteArray();
	}

	public static <T> T deserialize(byte[] objBuffer) {
		ByteArrayInputStream inStream = new ByteArrayInputStream(objBuffer);
		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(inStream);
			return (T) objStream.readObject();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				inStream.close();
				objStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
