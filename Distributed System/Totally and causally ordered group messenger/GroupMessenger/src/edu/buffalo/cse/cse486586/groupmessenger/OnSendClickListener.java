package edu.buffalo.cse.cse486586.groupmessenger;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


import edu.buffalo.cse.cse486586.groupmessenger.GroupMessengerActivity.counter;
//import edu.buffalo.cse.cse486586.groupmessenger.SimpleMessengerActivity.ClientTask;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class OnSendClickListener implements OnClickListener {
	  static final String REMOTE_PORT0 = "11108";
	    static final String REMOTE_PORT1 = "11112";
	    static final String REMOTE_PORT2 = "11116";
	    static final String[] myport = {REMOTE_PORT0,REMOTE_PORT1,REMOTE_PORT2,"11120","11124"};
    private static final String TAG = OnSendClickListener.class.getName();
    private static final int TEST_CNT = 50;
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
    private final String mPort;
    private final TextView mTextView;
    private final EditText mEditText;
    private final ContentResolver mContentResolver;
    private final Uri mUri;
    private final ContentValues[] mContentValues;
    counter counter;
    public OnSendClickListener(TextView _tv,EditText _ed,counter count,String myPort, ContentResolver _cr) {
        mTextView = _tv;
        mContentResolver = _cr;
        mEditText=_ed;
        mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
        mContentValues = initTestValues();
        counter=count;
        mPort=myPort;
    }

    /**
     * buildUri() demonstrates how to build a URI for a ContentProvider.
     * 
     * @param scheme
     * @param authority
     * @return the URI
     */
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    private ContentValues[] initTestValues() {
        ContentValues[] cv = new ContentValues[TEST_CNT];
        for (int i = 0; i < TEST_CNT; i++) {
            cv[i] = new ContentValues();
            cv[i].put(KEY_FIELD, "key" + Integer.toString(i));
            cv[i].put(VALUE_FIELD, "val" + Integer.toString(i));
        }

        return cv;
    }

    @Override
    public void onClick(View v) {
        String msg = mEditText.getText().toString() + "\n";	
        mEditText.setText("");
    System.out.println("in click");
    	
    	 new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg,REMOTE_PORT0);
    	
    }

    /**
     * testInsert() uses ContentResolver.insert() to insert values into your ContentProvider.
     * 
     * @return true if the insertions were successful. Otherwise, false.
     */
    private boolean testInsert(String key, String val) {
        try {
        	ContentValues[] cv = new ContentValues[1];
    
                cv[0] = new ContentValues();
                cv[0].put(KEY_FIELD, key);
                cv[0].put(VALUE_FIELD, val);
            
    
                mContentResolver.insert(mUri, cv[0]);
            
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }

        return true;
    }

    /**
     * testQuery() uses ContentResolver.query() to retrieves values from your ContentProvider.
     * It simply queries one key at a time and verifies whether it matches any (key, value) pair
     * previously inserted by testInsert().
     * 
     * Please pay extra attention to the Cursor object you return from your ContentProvider.
     * It should have two columns; the first column (KEY_FIELD) is for keys 
     * and the second column (VALUE_FIELD) is values. In addition, it should include exactly
     * one row that contains a key and a value.
     * 
     * @return
     */
    private String testQuery(String key) {
        try {
          

                Cursor resultCursor = mContentResolver.query(mUri, null, key, null, null);
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
                System.out.println(returnValue);
               /* String returnKey = resultCursor.getString(keyIndex);
               
                if (!(returnKey.equals(key) && returnValue.equals(val))) {
                    Log.e(TAG, "(key, value) pairs don't match\n");
                    resultCursor.close();
                    throw new Exception();
                }
*/
                resultCursor.close();
                return returnValue;
            
        } catch (Exception e) {
            return null;
        }

     //   return null;
    
   }


    
    
    public class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

        //	int n=counter.count;
        	counter.Increment();
        	String n=counter.GetvectorStamp();
        	String msg="PP"+"###"+n+"###"+counter.WhichAVD()+"###"+msgs[0];
        	System.out.println("onclick sender"+"::::"+msg);

            try {
            
          
            	
            	System.out.println("onclick sender"+"::::"+msg);
      
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        11108);
               
                
                String msgToSend = msg;
               BufferedWriter outt=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
               
               outt.write(msgToSend);
               outt.flush();
             
                socket.close();
        
            	
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }


    
    
    
    

}
