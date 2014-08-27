package edu.buffalo.cse.cse486586.simpledht;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class SimpleDhtProvider extends ContentProvider {
	private static final String KEY_FIELD = "key";
	private static final String VALUE_FIELD = "value";

	// stdgatic final String TAG = GroupMessengerActivity.class.getSimpleName();
	static String S_PORT;
	static String P_PORT;
	static String P_Hash;
	HashMap<String, String> keysmap = new HashMap<String, String>();
	static final String J = "JOIN";
	static final String B = "ADDNEW";
	static final String Q = "QUERY";
	static final String R = "RESULT";
	static final String O = "OWN";
	static final String S = "SUCCESSOR";
	static final String P = "PREDECESSOR";
	static final String I = "INSERT";
	static final String D = "DELETE";
	static final String F = "FAILED";
	static final String SU = "SUCCESS";
	static final String THIS = "@";
	static final String ALL = "*";
	static boolean Wait=false;

	static final String JOIN_MANAFER = "11108";
	static final String[] myport = new String[6];
	static final int SERVER_PORT = 10000;
	private Uri mUri;
	static HashMap<String, String> hp = new HashMap<String, String>();
	static String myPort;
	static final String TAG = SimpleDhtProvider.class.getSimpleName();

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		// start
		ArrayList<String> test=new ArrayList<String>();
		HashMap<String,String> test2=new HashMap<String,String>();
		mUri = buildUri("content",
				"/edu.buffalo.cse.cse486586.simpledht.provider");
		TelephonyManager tel = (TelephonyManager) this.getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(
				tel.getLine1Number().length() - 4);
		myPort = String.valueOf((Integer.parseInt(portStr) * 2));
		System.out.println(myPort);

		if (myPort.equalsIgnoreCase(JOIN_MANAFER)) {

			myport[0] = myPort;
		} else {
			new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, J);
		}
			
		try {
			P_Hash = genHash(portStr);
//System.out.println(genHash("BttJ9tpANV5tWLm0yx6R0FKOTHxSmBLx"));
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					serverSocket);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Wait=true;
		// TODO Auto-generated method stub
		String selection = null;
		String val = null;
		Set<String> key = values.keySet();
		int n = 1;
		for (String s : key) {
			if (n == 1) {
				val = values.getAsString(s);
			}
			if (n == 2) {
				selection = values.getAsString(s);
			break;			}
			n++;

		}
		System.out.println("inside insert" + selection + key);
		// String selection =filename;
		if (lookup(selection).equalsIgnoreCase(O)) {
			System.out.println("insert own");
			String res = testInsert(selection, val);
			if (res.equalsIgnoreCase(F)) {
				Wait=false;

				return uri;
			}
		} else if (lookup(selection).equalsIgnoreCase(P)
				|| lookup(selection).equalsIgnoreCase(S)) {
			System.out.println("inside indest non own" + lookup(selection));
			String res = next(lookup(selection), Q + "###" + I + "###"
					+ selection + "###" + val);
			if (res.equalsIgnoreCase(F)) {
				Wait=false;

				return uri;
			}
		}
Wait=false;
		return uri;
		// return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		System.out.println("inside delete");
		if(selection.equalsIgnoreCase(THIS)){
			int size=keysmap.size();
			keysmap= new HashMap<String,String>();
			return size;
		}
		
		
		
		
		try {
			if(keysmap.remove(genHash(selection))!=null){
return 1;				
					
				}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
			
			
			
			
		if (lookup(selection).equalsIgnoreCase(O)) {
			String res = testDelete(selection);
		} else if (lookup(selection).equalsIgnoreCase(P)
				|| lookup(selection).equalsIgnoreCase(S)) {
			String res = next(lookup(selection), Q + "###" + D + "###"
					+ selection);

		}
		return 1;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String[] col = { KEY_FIELD, VALUE_FIELD };
		System.out.println("inside queryn");
		MatrixCursor mx = new MatrixCursor(col);
		while(Wait){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Interrruoed error");
				e.printStackTrace();
			}
		}
	
		if (selection.equalsIgnoreCase(THIS)) {
			Set<String> set = keysmap.keySet();
			for (String a : set) {
				String res = testQuery(keysmap.get(a));
				if (res != null && res != F)
					mx.addRow(new Object[] { keysmap.get(a), res });
				}
	
			return mx;
		}
		if (selection.equalsIgnoreCase(ALL)) {
			Set<String> set = keysmap.keySet();
			for (String a : set) {
				String res = testQuery(keysmap.get(a));
				if (res != null && res != F)
					mx.addRow(new Object[] { keysmap.get(a), res });
				}
			if(P_PORT==null&&S_PORT==null){
				return mx;
			}
			String all=all();
			String[] parts = all.split("\\###");
			if((parts.length%2)==0){
				for(int i=0;i<parts.length;i++){
				String key=parts[i];
				i++;
				String val=parts[i];
				mx.addRow(new Object[] { key, val });

				}
			}
			return mx;
		}
		
		
		
		
		
		
	try {
		if(keysmap.get(genHash(selection))!=null){
			String res=testQuery(selection);
			mx.addRow(new Object[] { selection, res });
			return mx;
				
			}
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	
	
	
		if (lookup(selection).equalsIgnoreCase(O)) {
			System.out.println("inside own");
			String res = testQuery(selection);
			if (res != null && res != F&&!res.equalsIgnoreCase("")) {
				mx.addRow(new Object[] { selection, res });
				return mx;
			}
		} else if (lookup(selection).equalsIgnoreCase(P)
				|| lookup(selection).equalsIgnoreCase(S)) {
			System.out.println("inside succ and pre");
			String res = next(lookup(selection), Q + "###" + Q + "###"
					+ selection);
			// String[] parts = res.split("\\###");
		//	System.out.println("value of ans" + res);
			if (res != null && res != F && !res.equalsIgnoreCase("")) {
				mx.addRow(new Object[] { selection, res });
				return mx;
			}
		}
		// TODO Auto-generated method stub
		return mx;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		// TODO Auto-generated method stub
		return 0;
	}

	private String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

	// /server listense

	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

		// private Void msgl;

		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];
			try {

				Socket socket;
				while (true) {
					socket = serverSocket.accept();
					//System.out.println("Incoming request on server");
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					/*
					 * BufferedWriter outt = new BufferedWriter( new
					 * OutputStreamWriter(socket.getOutputStream()));
					 */
					PrintWriter outt = new PrintWriter(
							socket.getOutputStream(), true);
					String incoming = null;
					while ((incoming = br.readLine()) == null) {
					}

					String strReceived = incoming;
				//	System.out.println("server msg:" + strReceived);
					String[] parts = strReceived.split("\\###");
					if (parts[0].equalsIgnoreCase(Q)) {
					//	System.out.println("inside server query");
						if (lookup(parts[2]).equalsIgnoreCase(O)) {
						//	System.out.println("server own")dd;
							String res = Own(strReceived);

							outt.println(res);
							// outt.notifyAll();
							// outt.flush();
							// outt.close();
							socket.close();
							socket = null;

						} else if (lookup(parts[2]).equalsIgnoreCase(S)
								|| lookup(parts[2]).equalsIgnoreCase(P)) {
						//	System.out.println("server succ pre");
							String res = next(lookup(parts[2]), incoming);

							outt.println(res);
							// outt.notifyAll();
							// outt.flush();
							 socket.close();
							socket = null;
						}
					}
					else if(parts[0].equalsIgnoreCase("ALL")){
					
						String res=testQuery(ALL);
								res+=ServerAll(parts[1]);	
					outt.println(res);
			socket.close();
					socket = null;
					}
					else if(parts[0].equalsIgnoreCase("ALLNODE")){
					//	System.out.println("Allnode server");
						String res=testQuery("*");	
						outt.println(res);
				socket.close();
						socket = null;
						}
					else {
						System.out.println("inside else in server");
						this.publishProgress(incoming);
						// outt.close();
						// br.close();
						if (socket.isConnected())
							socket.close();
						socket = null;

					}

				}
			} catch (IOException e) {

				e.printStackTrace();
				System.out.println(e.getMessage() + "d");
			}
			/*
			 * TODO: Fill in your server code that receives messages and passes
			 * them to onProgressUpdate().
			 */
			return null;
		}

		protected void onProgressUpdate(String... strings) {
			/*
			 * The following code displays what is received in doInBackground().
			 */
			String strReceived = strings[0].trim();
			System.out.println(strReceived);
			String[] parts = strReceived.split("\\###");
			System.out.println(parts.length + ":");

			if (parts[0].equalsIgnoreCase(J)) {
				System.out.println("inside server join");
				int i = 0;
				while (myport[i] != null && i < 5) {
					i++;
				}
				myport[i] = parts[1];
				System.out.println("server" + i);

				new ClientTask()
						.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, B);

			}

			else if (parts[0].equalsIgnoreCase(B)) {
				System.out.println("inside server addnew");
				HashMap<String, String> ports = new HashMap<String, String>();
				ArrayList<String> port = new ArrayList<String>();
				try {
					for (int i = 1; i < parts.length; i++) {
						ports.put(genHash(Integer.parseInt(parts[i])/2+""), parts[i]);
						port.add(genHash(Integer.parseInt(parts[i])/2+""));
					}
					Collections.sort(port);
					inialization(ports, port);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

			}/*
			 * else if (parts[0].equalsIgnoreCase(Q)) {
			 * System.out.println("inside server query"); String step =
			 * lookup(parts[2]); if (step.equalsIgnoreCase(O)) {
			 * 
			 * } else if (step.equalsIgnoreCase(P) || step.equalsIgnoreCase(S))
			 * {
			 * 
			 * } // else return "Failed"; }
			 */

			// }
			return;
		}

	}

	// uri builder

	private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	private class ClientTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... msgs) {
			System.out.println("inside client task:" + msgs[0]);
			try {
				if (msgs[0].equalsIgnoreCase(B)) {
					System.out.println("BroadCast");
					String msg = B;
					int i = 0;
					while (myport[i] != null && i < 5) {
						msg = msg + "###" + myport[i];
						i++;
					}
					System.out.println(msg);
					int j = 0;
					while (myport[j] != null && j < 5) {
						String remotePort = myport[j].trim();

						Socket socket = new Socket(
								InetAddress.getByAddress(new byte[] { 10, 0, 2,
										2 }), Integer.parseInt(remotePort));

						String msgToSend = msg;
						BufferedWriter outt = new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream()));

						outt.write(msgToSend);
						outt.flush();
						outt.close();
						socket.close();
						j++;
					}

				}

				else if (msgs[0].equalsIgnoreCase(J)) {
					System.out.println("join");
					String msg = J + "###" + myPort;
					System.out.println(msg);
					Socket socket = new Socket(
							InetAddress
									.getByAddress(new byte[] { 10, 0, 2, 2 }),
							Integer.parseInt(JOIN_MANAFER));
					String msgToSend = msg;
					BufferedWriter outt = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));

					outt.write(msgToSend);
					outt.flush();
					outt.close();
					socket.close();
				}

			} catch (UnknownHostException e) {
				Log.e(TAG, "ClientTask UnknownHostException");
			} catch (IOException e) {
				Log.e(TAG, "ClientTask socket IOException");
			}

			// }

			return null;
		}
	}

	void inialization(HashMap<String, String> portMap, ArrayList<String> port)
			throws NoSuchAlgorithmException {
		 Collections.sort(port);
		if (port.size() < 2) {
			return;
		}
		int i;
		for (i = 0; i < port.size(); i++) {
			if (port.get(i).equalsIgnoreCase(P_Hash))
				break;
		}
		int succ = i + 1;
		int pre = i - 1;
		if (succ > (port.size() - 1))
			succ = 0;
		if (pre < 0)
			pre = port.size() - 1;
		S_PORT = portMap.get(port.get(succ));
		P_PORT = portMap.get(port.get(pre));
		System.out.println("Predecessor:" + P_PORT + ":  " + S_PORT + ":");
	}

	String next(String... type) {
		String msg;
		String remotePort;
		System.out.println("inside next: " + type[0]);
		System.out.println("inside next: " + type[1]);

		if (type[0].equalsIgnoreCase(P)) {
			remotePort = P_PORT;
		} else if (type[0].equalsIgnoreCase(S)) {
			remotePort = S_PORT;
		} else {
			return F;
		}
		System.out.println(remotePort);
	
		msg = type[1];
		try {

		
			Socket socket = new Socket(InetAddress.getByAddress(new byte[] {
					10, 0, 2, 2 }), Integer.parseInt(remotePort));
			System.out.println("Outging querfynkh" + msg);
			String msgToSend = msg;
		
			PrintWriter outt = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outt.println(msg);

			String ret = br.readLine();
			
	

			return ret;

		} catch (Exception e) {
			System.out.println("exception in next");
			e.getStackTrace();
			return F;
		}

		// return null;

	}


	String lookup(String key) {
		if (P_PORT == null && S_PORT == null) {
			return O;
		}

		try {
			String hash = genHash(key);
			if (P_Hash.compareToIgnoreCase(genHash(Integer.parseInt(P_PORT)/2+"")) < 0
					&& P_Hash.compareToIgnoreCase(genHash(Integer.parseInt(S_PORT)/2+"")) < 0) {
				
		/*		if (hash.compareToIgnoreCase(genHash(Integer.parseInt(P_PORT)/2+"")) > 0
						&& hash.compareToIgnoreCase(genHash(Integer.parseInt(S_PORT)/2+"")) > 0)*/
				if (hash.compareToIgnoreCase(genHash(Integer.parseInt(P_PORT)/2+"")) > 0)
					return O;
				else if (hash.compareToIgnoreCase(P_Hash) < 0)
					return O;
			}
			if (hash.compareToIgnoreCase(P_Hash) > 0) {
				// if (hash.compareToIgnoreCase(S_PORT) > 0) {
				return S;
				// } else
				// return "Own";
			} else {
				if (hash.compareToIgnoreCase(genHash(Integer.parseInt(P_PORT)/2+"")) < 0) {
					return P;
				} else
					return O;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	String Own(String... step) {
		String strReceived = step[0].trim();
		//System.out.println(strReceived);
		String[] parts = strReceived.split("\\###");
		//System.out.println(parts.length + ":");

		if (parts[1].equalsIgnoreCase(I)) {
			String res = testInsert(parts[2], parts[3]);
			return res;
		}
		if (parts[1].equalsIgnoreCase(Q)) {
			String res = testQuery(parts[2]);
			return res;
		}
		if (parts[1].equalsIgnoreCase(D)) {
			 String res=testDelete(parts[2]);
			 return res;
		}
		return F;
	}

	private String testInsert(String key, String val) {
		try {
			String keyhash = genHash(key);
			keysmap.put(keyhash, key);

			FileOutputStream outputStream;

			try {
				outputStream = getContext().openFileOutput(keyhash,
						Context.MODE_PRIVATE);
				outputStream.write(val.getBytes());
				outputStream.close();
			} catch (Exception e) {
				Log.e(TAG, "File write failed");
			}
			// Log.v("insert", values.toString());

		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return F;
		}

		return SU;
	}

	private String testDelete(String key) {
		try {
			String ret = keysmap.remove(genHash(key));
			if (ret == null)
				return F;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return SU;
	}

	private String testQuery(String key) {
		
		String ret="";

		if(key.equalsIgnoreCase(ALL)){
			//String ret="";
			Set<String> set=keysmap.keySet();
			try {
			for(String s:set){
			FileInputStream fs;
			fs = getContext().openFileInput(s);
			InputStreamReader isr = new InputStreamReader(fs);
			BufferedReader bufferedReader = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			//while ((line = bufferedReader.readLine()) != null)
			line = bufferedReader.readLine();
				sb.append(line);
			ret+=keysmap.get(s)+"###"+sb.toString()+"###";
		//	System.out.println(sb.toString());
			} return ret;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
		try {
			
			String keyHash = genHash(key);
			System.out.println("inside testquery:" + keyHash);
			if (keysmap.get(keyHash) == null)
				return F;
			FileInputStream fs;

			fs = getContext().openFileInput(keyHash);
			// Byte[] s= fs.read();
			InputStreamReader isr = new InputStreamReader(fs);
			BufferedReader bufferedReader = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null)
				sb.append(line);
			System.out.println(sb.toString());
			return sb.toString();

			// return returnValue;

		} 
		catch (Exception e) {
			return F;
		}
		}
		System.out.println("reached an unreacahvle code im test query");
		return ret;

	}
	String ServerAll(String port){
		String msg;
		String ret="";
				msg = "ALLNODE";
		try {
			for(String s:myport){
				if(s==null||port.equalsIgnoreCase(s)||s.equalsIgnoreCase(myPort))continue;
		
			Socket socket = new Socket(InetAddress.getByAddress(new byte[] {
					10, 0, 2, 2 }), Integer.parseInt(s));
			System.out.println("Outging query" + msg);
			String msgToSend = msg;
		
			PrintWriter outt = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outt.println(msg);

			 ret+= br.readLine();
			
			}
			//ret=ret+retval;
			return ret;
		}
		//	return ret;
	
		catch(Exception e){
			return "";
		}
	}
	
	
	String all() {
		String msg;
		msg = "ALL";
		try {

		
			Socket socket = new Socket(InetAddress.getByAddress(new byte[] {
					10, 0, 2, 2 }), Integer.parseInt(JOIN_MANAFER));
			System.out.println("Outging query" + msg);
			String msgToSend = msg+"###"+myPort;
		
			PrintWriter outt = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outt.println(msgToSend);

			String ret = br.readLine();
			
	

			return ret;

		} catch (Exception e) {
			System.out.println("exception in nevxt");
			e.getStackTrace();
			return F;
		}

		// return null;

	}
	// end colonm
}
