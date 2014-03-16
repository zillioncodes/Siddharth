package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */

public class GroupMessengerProvider extends ContentProvider {
	
	private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
    //........................
    
    static final String TAG = GroupMessengerActivity.class.getSimpleName();

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that I used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
    	//Set<String> key=values.keySet();
    	String filename = null;
    	String val = null;
    	
    //	Set<Entry<String, Object>> value=values.valueSet();
Set<String> key=values.keySet();
int n=1;
for(String s:key)
{
	if(n==1){
		val= values.getAsString(s);
		System.out.println("values---"+val);
		
	}
	if(n==2){
		 filename=	values.getAsString(s);
		 System.out.println("key----"+filename);
	}
	n++;
	//System.out.println("keys sid--"+s);
 

 
}

      //  String filename = "SimpleMessengerOutput";
       // String string = strReceived + "\n";
        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(val.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "File write failed");
        }
        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
    	String[] col={KEY_FIELD,VALUE_FIELD};
    	MatrixCursor mx=new MatrixCursor(col);
    	FileInputStream fs;
    	try {
			fs= getContext().openFileInput(selection);
			//Byte[] s= fs.read();
			 InputStreamReader isr = new InputStreamReader(fs);
			   BufferedReader bufferedReader = new BufferedReader(isr);
			   StringBuilder sb = new StringBuilder();
			   String line;
			   while ((line = bufferedReader.readLine()) != null) {
			       sb.append(line);
			       
			   }
			   mx.addRow(new Object[]{selection,sb.toString()});
			   return mx;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
       // Log.v("query", selection);
       // return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
}
