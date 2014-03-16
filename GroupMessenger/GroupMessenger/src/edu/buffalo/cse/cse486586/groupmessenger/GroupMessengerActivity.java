package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.cse486586.groupmessenger.R;
import edu.buffalo.cse.cse486586.groupmessenger.OnSendClickListener.ClientTask;
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

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 * 
 */
public class GroupMessengerActivity extends Activity {

	private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
	static final String TAG = GroupMessengerActivity.class.getSimpleName();
	static final String REMOTE_PORT0 = "11108";
	static final String REMOTE_PORT1 = "11112";
	static final String REMOTE_PORT2 = "11116";
	static final String[] myport = { REMOTE_PORT0, REMOTE_PORT1, REMOTE_PORT2,"11120","11124" };
	static final int SERVER_PORT = 10000;
    private Uri mUri;

	static String myPort;
	counter count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_messenger);

		/*
		 * TODO: Use the TextView to display your messages. Though there is no
		 * grading component on how you display the messages, if you implement
		 * it, it'll make your debugging easier.
		 */
		
		  mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
		TextView tv = (TextView) findViewById(R.id.textView1);
		EditText ed = (EditText) findViewById(R.id.editText1);
		tv.setMovementMethod(new ScrollingMovementMethod());
		count = counter.getInstance();
		System.out.println("" + count.count);

		TelephonyManager tel = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(
				tel.getLine1Number().length() - 4);
		myPort = String.valueOf((Integer.parseInt(portStr) * 2));
		/*
		 * Registers OnPTestClickListener for "button1" in the layout, which is
		 * the "PTest" button. OnPTestClickListener demonstrates how to access a
		 * ContentProvider.
		 */

		// -----------------sidd------------------
/*
		testInsert("AVD0","0");
		testInsert("AVD1","0");
		testInsert("AVD2","0");
		testInsert("AVD3","0");
		testInsert("AVD3","0");*/
		// ---------------------------------------------------
		findViewById(R.id.button1).setOnClickListener(
				new OnPTestClickListener(tv, getContentResolver()));

		/*
		 * TODO: You need to register and implement an OnClickListener for the
		 * "Send" button. In your implementation you need to get the message
		 * from the input box (EditText) and send it to other AVDs in a
		 * total-causal order.
		 */
		// /----------Siddharth new Listener-----------
		findViewById(R.id.button4).setOnClickListener(
				new OnSendClickListener(tv, ed, count, myPort,
						getContentResolver()));

		try {
			/*
			 * Create a server socket as well as a thread (AsyncTask) that
			 * listens on the server port.
			 * 
			 * AsyncTask is a simplified thread construct that Android provides.
			 * Please make sure you know how it works by reading
			 * http://developer.android.com/reference/android/os/AsyncTask.html
			 */
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					serverSocket);
		} catch (IOException e) {
			/*
			 * Log is a good way to debug your code. LogCat prints out all the
			 * messages that Log class writes.
			 * 
			 * Please read
			 * http://developer.android.com/tools/debugging/debugging
			 * -projects.html and
			 * http://developer.android.com/tools/debugging/debugging-log.html
			 * for more information on debugging.
			 */
			Log.e(TAG, "Can't create a ServerSocket");
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
		return true;
	}

	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

		// private Void msgl;

		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];
			try {

				Socket socket;
				while (true) {
					socket = serverSocket.accept();
					System.out.println("incoming msg"+ count.WhichAVD());
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));

					this.publishProgress(br.readLine());
				

					socket.close();
					socket = null;

				}

			} catch (IOException e) {

				e.printStackTrace();
				System.out.println(e.getMessage());
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
			System.out.println(parts.length);
			try{
				System.out.println("Inside propose"+parts[0]+parts[1]+parts[2]+parts[3]);
				}
			catch(Exception e){System.out.println("Exception caught");}
			if(parts[0].equalsIgnoreCase("PP"))
			{
			System.out.println("inside propose rel");
				
				if(count.Iseligible(parts[1])){
					System.out.println("inside eligible");

					testInsert(parts[1],parts[3]);
					count.assign(parts[1]);
					ArrayList<String> cc=count.eligible();
					cc.add(parts[1]);
					System.out.println(cc.size());
					for (String key: cc)
					{
						String val= testQuery(key);
						System.out.println(val);
					
				 new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, count.count+"",key,val);
					count.IncrementCount();
					}
					
				}
				else
				{
					testInsert(parts[1],parts[3]);
					count.m.put(parts[1],"0");
					
					//testInsert(count.count+"",parts[3]);
				}
					
			
				
			}
			
			else {
				
				
				String key = parts[0];
				int keyR = Integer.parseInt(key);
				
				// String value=parts[1];
				testInsert(parts[0],parts[1]);
				TextView remoteTextView = (TextView) findViewById(R.id.textView1);
				remoteTextView.append(parts[0] + ": " + parts[1] + "\t\n");
			
			}
			return;
		}
	}

	private class ClientTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... msgs) {
	
			String msg = msgs[0]+"###"+msgs[2];
			try {
				System.out.println("BroadCast");
			for(String s:myport)
			{
				//System.out.println(msgs[0]);
					String remotePort = s.trim();

					Socket socket = new Socket(
							InetAddress
									.getByAddress(new byte[] { 10, 0, 2, 2 }),
							Integer.parseInt(remotePort));

					String msgToSend = msg;
					BufferedWriter outt = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));

					outt.write(msgToSend);
					outt.flush();

					socket.close();
			
			}	//count.increment();
			//	}

			} catch (UnknownHostException e) {
				Log.e(TAG, "ClientTask UnknownHostException");
			} catch (IOException e) {
				Log.e(TAG, "ClientTask socket IOException");
			}
			
			//}
			
			
			return null;
		}
	}

////////////////////////////   Content Provider//////////////////////////////////////////////	

	
	 private Uri buildUri(String scheme, String authority) {
	        Uri.Builder uriBuilder = new Uri.Builder();
	        uriBuilder.authority(authority);
	        uriBuilder.scheme(scheme);
	        return uriBuilder.build();
	    }
	
	
	
	
	 private boolean testInsert(String key, String val) {
	        try {
	        	ContentValues[] cv = new ContentValues[1];
	    
	                cv[0] = new ContentValues();
	                cv[0].put(KEY_FIELD, key);
	                cv[0].put(VALUE_FIELD, val);
	            
	    
	                getContentResolver().insert(mUri, cv[0]);
	            
	        } catch (Exception e) {
	            Log.e(TAG, e.toString());
	            return false;
	        }

	        return true;
	    }
	
	 private String testQuery(String key) {
	        try {
	          

	                Cursor resultCursor = getContentResolver().query(mUri, null, key, null, null);
	                if (resultCursor == null) {
	                    Log.e(TAG, "Result null");
	                    throw new Exception();
	                }

	                int keyIndex = resultCursor.getColumnIndex(KEY_FIELD);
	                System.out.println("keyindex---"+keyIndex);
	                int valueIndex = resultCursor.getColumnIndex(VALUE_FIELD);
	                System.out.println("valueindex---"+valueIndex);

	                if (keyIndex == -1 || valueIndex == -1) {
	                    Log.e(TAG, "Wrong columns");
	                    resultCursor.close();
	                    throw new Exception();
	                }

	                resultCursor.moveToFirst();

	                if (!(resultCursor.isFirst() && resultCursor.isLast())) {
	                    Log.e(TAG, "Wrong number of rows");
	                    resultCursor.close();
	                    throw new Exception();
	                }
	                String returnValue = resultCursor.getString(valueIndex);
	                
	  
	                resultCursor.close();
	                return returnValue;
	            
	        } catch (Exception e) {
	            return null;
	        }

	     //   return null;
	    
	   }
	
	
	
	
	
	
	////////////////////////////////////////////////Helper class////////////////////////////////////////////////////
	
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	static class counter {
		
		int proposeCount = 2;
		int AVD0;
		int AVD1;
		int AVD2;
		int AVD3;
		int AVD4;
		int count;
		HashMap<String, String> m;
		private static counter n = new counter();

		private counter() {
			AVD0=AVD1=AVD2=AVD3=AVD4=0;
			//proposeCount = 2;
			count=0;
			m=new HashMap<String,String>();
		}
		

		public static counter getInstance() {
			return n;
		}

		synchronized String GetvectorStamp() {
			return AVD0+"."+AVD1+"."+AVD2+"."+AVD3+"."+AVD4;
		}

		synchronized void assign(String n) {
			String[] stamps=n.split("\\.");
			System.out.println(stamps.length);
			assert(stamps.length==5);
			if(Integer.parseInt(stamps[0])>AVD0)
			AVD0=Integer.parseInt(stamps[0]);
			if(Integer.parseInt(stamps[1])>AVD1)
			AVD1=Integer.parseInt(stamps[1]);
			if(Integer.parseInt(stamps[2])>AVD2)
			AVD2=Integer.parseInt(stamps[2]);
			if(Integer.parseInt(stamps[3])>AVD3)
			AVD3=Integer.parseInt(stamps[3]);
			if(Integer.parseInt(stamps[4])>AVD4)
			AVD4=Integer.parseInt(stamps[4]);
			
		}

		synchronized void IncrementCount() {
	
				this.count++;
		}

		/*synchronized void reset() {
			count = 0;
	*/
		synchronized void Increment(){
		if(myPort.equalsIgnoreCase(myport[0]))
			AVD0++;
		if(myPort.equalsIgnoreCase(myport[1]))
			AVD1++;
		if(myPort.equalsIgnoreCase(myport[2]))
			AVD2++;
		if(myPort.equalsIgnoreCase(myport[3]))
			AVD3++;
		if(myPort.equalsIgnoreCase(myport[4]))
			AVD4++;
		}
		synchronized void Decrement(){
			if(myPort.equalsIgnoreCase(myport[0]))
				AVD0--;
			if(myPort.equalsIgnoreCase(myport[1]))
				AVD1--;
			if(myPort.equalsIgnoreCase(myport[2]))
				AVD2--;
			if(myPort.equalsIgnoreCase(myport[3]))
				AVD3--;
			if(myPort.equalsIgnoreCase(myport[4]))
				AVD4--;
			}
		synchronized String WhichAVD()
		{	
			if(myPort.equalsIgnoreCase(myport[0]))
			return "AVD0";
		if(myPort.equalsIgnoreCase(myport[1]))
			return "AVD1";
		if(myPort.equalsIgnoreCase(myport[2]))
			return "AVD2";
		if(myPort.equalsIgnoreCase(myport[3]))
			return "AVD3";
		if(myPort.equalsIgnoreCase(myport[4]))
			return "AVD4";
		else return null;
			
		}
		synchronized String[] ArrayAVD(String stamp){
			return stamp.split("\\.");
		}
		synchronized boolean Iseligible(String stamp){
			System.out.println("Inside iseligible");
			String[] stamps=stamp.split("\\.");
			System.out.println(stamps.length);
			assert(stamps.length==5);
			int n=0;
			if(AVD0+1==Integer.parseInt(stamps[0]))
					n++;
			if(AVD1+1==Integer.parseInt(stamps[1]))
					n++;
			if(AVD2+1==Integer.parseInt(stamps[2]))
					n++;
			if(AVD3+1==Integer.parseInt(stamps[3]))
					n++;
			if(AVD4+1==Integer.parseInt(stamps[4]))
					n++;
			if(n>1)
				return false;
			else return true;
		}
		
		synchronized ArrayList<String> eligible(){
		ArrayList<String> s=new ArrayList<String>();
		int n=AVD0+1;
		String key=n+"."+AVD1+"."+AVD2+"."+AVD3+"."+AVD4;
		if(m.containsKey(key)){ s.add(key); m.remove(key);}
		 n=AVD1+1;
		key=AVD0+"."+n+"."+AVD2+"."+AVD3+"."+AVD4;
		if(m.containsKey(key)) {s.add(key);m.remove(key);}
		 n=AVD2+1;
		key=AVD0+"."+AVD1+"."+n+"."+AVD3+"."+AVD4;
		if(m.containsKey(key)){ s.add(key);m.remove(key);}
		 n=AVD3+1;
		key=AVD0+"."+AVD1+"."+AVD2+"."+n+"."+AVD4;
		if(m.containsKey(key)){ s.add(key);m.remove(key);}
		 n=AVD4+1;
		key=AVD0+"."+AVD1+"."+AVD2+"."+AVD3+"."+n;
		if(m.containsKey(key)){ s.add(key);m.remove(key);}
		
		return s;
		}
		/*synchronized void proAssign(int n){
			proposeCount=n;
		}*/
	}

}
