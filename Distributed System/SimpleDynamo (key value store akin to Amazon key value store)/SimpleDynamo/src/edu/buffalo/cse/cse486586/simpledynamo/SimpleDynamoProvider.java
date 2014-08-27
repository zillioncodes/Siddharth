package edu.buffalo.cse.cse486586.simpledynamo;
//import edu.buffalo.cse.cse486586.simpledynamo.SimpleDynamoProvider.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDynamoProvider extends ContentProvider {
    static String myPort=new String();
    public static final int SERVER_PORT=10000;
    Map<String,Integer> nodes2int=new HashMap<String,Integer>();
    Map<Integer,String> int2nodes=new HashMap<Integer,String>();
    Map<String,String> HashWithRealPorts=new HashMap<String,String>();
    Map<String,String> myVals=new HashMap<String,String>();
    Map<String,String> pred1Vals=new HashMap<String,String>();
    Map<String,String> pred2Vals=new HashMap<String,String>();
    //Map<String,String> starQuery= new HashMap<String,String>();
    Star starResponse=new Star();
    ArrayList<String> network=new ArrayList<String>();
    String mySuccessor1=new String();
    String mySuccessor2=new String();
    String myPredicessor1=new String();
    String myPredicessor2=new String();
    String response=new String();
    String curKey=new String();
    int Reply=0;
    int initReady=0;
    Functions func=new Functions();
    Object insLock=new Object();
    static Object initLock=new Object();
    Object irLock=new Object();
    //Object atLock=new Object();
    Thread at;
   



    ////////// on Create /////////////////
    @Override
    
    public boolean onCreate() {
        synchronized(initLock){    

        ///////////////clearing all old files///////////////
        String[] savFiles = getContext().fileList();
        for (int i = 0; i < savFiles.length; i++) {
            func.del(savFiles[i]);
        }
        initReady=0;

        // Checking current port
        TelephonyManager tel = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        myPort = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        Log.i("myPort is ",myPort);

        //Creating hashmaps to compute successors and predicessors.

        try{
            String[] remotePorts={"5562","5556","5554","5558","5560"};
            for(String s:remotePorts){
                //Log.d("hashmap", s);
                HashWithRealPorts.put(genHash(s), s); 
                network.add(genHash(s)) ;
            }
        }
        catch(Exception e){
            Log.e("Error while creating network","Ouch");
            e.printStackTrace();
        }
        Collections.sort(network);
        for(int i=1;i<6;i++){
            nodes2int.put(HashWithRealPorts.get(network.get(i-1)),i);
            int2nodes.put(i, HashWithRealPorts.get(network.get(i-1)));
            //Log.d(i+"",HashWithRealPorts.get(network.get(i-1)));
        }

        // checking successors and other chain elements.
        NodeInfo temp= new NodeInfo();
        temp=nodeLookup(myPort);
        mySuccessor1=temp.succ1;
        mySuccessor2=temp.succ2;
        myPredicessor1=temp.pred1;
        myPredicessor2=temp.pred2;
        Log.d("Successor1",mySuccessor1);
        Log.d("Successor2",mySuccessor2);
        Log.d("Predicessor1",myPredicessor1);
        Log.d("Predicessor2",myPredicessor2);


        // Starting the Server Listener
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new Server().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
 

        } catch (IOException e) {
            Log.e("Server", "Can't create the Listening Server");
            e.printStackTrace();
        }

        ////////// Gathering info that was lost during failure//////////

        ////////// Sending messages to successors and predicessors for my values and replicas.///////////////
        String[] recipients={mySuccessor1,myPredicessor1,myPredicessor2};
        for(String s:recipients){
            Packet Outgoing=new Packet();
            Outgoing.message="initVals";
            Outgoing.sendersPort=myPort;
            Outgoing.recieversPort=s;
            new Client().execute(Outgoing);
        }

        //        try {
        //            Thread.sleep(1024);
        //        } catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            Log.e("Exception while waiting in","onCreate");
        //            e.printStackTrace();
        //        }

        return true;
    }
    }

    ////////// Content provider data functions ////////////////////

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {

        if(selection.equals("@")){
            String[] savFiles = getContext().fileList();
            for (int i = 0; i < savFiles.length; i++) {
                myVals.remove(savFiles[i]);
                func.del(savFiles[i]);
            }
        }
        else if(selection.equals("*")){
            String[] savFiles = getContext().fileList();
            for (int i = 0; i < savFiles.length; i++) {
                myVals.remove(savFiles[i]);
                func.del(savFiles[i]);
            }
            String[] remotePorts={"5562","5556","5554","5558","5560"};
            for(String s:remotePorts){
                if(!s.equals(myPort)){
                    Packet Outgoing=new Packet();
                    Outgoing.message="*del";
                    Outgoing.recieversPort=s;
                    Outgoing.sendersPort=myPort;
                    new Client().execute(Outgoing);
                }
            }
        }
        else{
            NodeInfo temp=new NodeInfo();
            if((temp=Lookup(selection)).matchingNode.equals(myPort)){
                myVals.remove(selection);
                func.del(selection);
                Packet Outgoing=new Packet();
                Outgoing.message="repDel";
                Outgoing.recieversPort=temp.succ1;
                Outgoing.key=selection;
                Outgoing.sendersPort=myPort;
                new Client().execute(Outgoing);
                Packet Outgoing2=new Packet();
                Outgoing2.message="repDel";
                Outgoing2.recieversPort=temp.succ2;
                Outgoing2.key=selection;
                Outgoing2.sendersPort=myPort;
                new Client().execute(Outgoing2);
            }
            else{
                Packet Outgoing=new Packet();
                Outgoing.message="del";
                Outgoing.recieversPort=temp.matchingNode;
                Outgoing.key=selection;
                Outgoing.sendersPort=myPort;
                new Client().execute(Outgoing);
                Packet Outgoing2=new Packet();
                Outgoing2.message="repDel";
                Outgoing2.recieversPort=temp.succ1;
                Outgoing2.key=selection;
                Outgoing2.sendersPort=temp.matchingNode;
                new Client().execute(Outgoing2);
                Packet Outgoing3=new Packet();
                Outgoing3.message="repDel";
                Outgoing3.recieversPort=temp.succ2;
                Outgoing3.key=selection;
                Outgoing3.sendersPort=temp.matchingNode;
                new Client().execute(Outgoing3);

            }

        }
        return 0;
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //TODO
        //while(initReady<3){}
        NodeInfo temp=new NodeInfo();
        temp=Lookup(values.getAsString("key"));
        if(temp.matchingNode.equals(myPort)){
            myVals.put(values.getAsString("key"),values.getAsString("value"));
            func.Ins(values.getAsString("key"),values.getAsString("value"));
            Packet Outgoing=new Packet();
            Packet Outgoing2=new Packet();
            Outgoing.message="replica";
            Outgoing.key=values.getAsString("key");
            Outgoing.value=values.getAsString("value");
            Outgoing.sendersPort=myPort;
            Outgoing.recieversPort=temp.succ1;
            Log.d("Sending replica 1 from "+myPort+"to ",temp.succ1);
            new Client().execute(Outgoing);
            Outgoing2.message="replica";
            Outgoing2.key=values.getAsString("key");
            Outgoing2.value=values.getAsString("value");
            Outgoing2.sendersPort=myPort;
            Outgoing2.recieversPort=temp.succ2;
            Log.d("Sending replica 2 from "+myPort+"to ",temp.succ2);
            new Client().execute(Outgoing2);
           
            return null;
        }
        else if(temp.succ1.equals(myPort)){
            String key=values.getAsString("key");
            String value=values.getAsString("value");
            
            //TODO change from myVals to predsomething
            pred1Vals.put(values.getAsString("key"),values.getAsString("value"));
            func.Ins(values.getAsString("key"),values.getAsString("value"));
            
            Packet Outgoing=new Packet();
            Packet Outgoing2=new Packet();
            Outgoing.message="insert";
            Outgoing.key=key;
            Outgoing.value=value;
            //Outgoing.version=keyVersion.get(key);
            Outgoing.sendersPort=myPort;
            Outgoing.recieversPort=temp.matchingNode;
            Log.d("Sending replica 1 from "+myPort+"to ",myPredicessor1);
            new Client().execute(Outgoing);
            Outgoing2.message="replica";
            Outgoing2.key=key;
            Outgoing2.value=value;
            //Outgoing2.version=keyVersion.get(key);
            Outgoing2.sendersPort=temp.matchingNode;
            Outgoing2.recieversPort=temp.succ2;
            Log.d("Sending replica 2 from "+myPort+"to ",temp.succ2);
            new Client().execute(Outgoing2);

            return null;
        }
        else if(temp.succ2.equals(myPort)){
            String key=values.getAsString("key");
            String value=values.getAsString("value");
            
            pred2Vals.put(key,value);
            func.Ins(key,value);
           
            
            Packet Outgoing=new Packet();
            Packet Outgoing2=new Packet();
            Outgoing.message="replica";
            Outgoing.key=key;
            Outgoing.value=value;
            //Outgoing.version=keyVersion.get(key);
            Outgoing.sendersPort=temp.matchingNode;
            Outgoing.recieversPort=temp.succ1;
            Log.d("Sending replica 1 from "+myPort+"to ",temp.succ1);
            new Client().execute(Outgoing);
            Outgoing2.message="insert";
            Outgoing2.key=key;
            Outgoing2.value=value;
            //Outgoing2.version=keyVersion.get(key);
            Outgoing2.sendersPort=myPort;
            Outgoing2.recieversPort=temp.matchingNode;
            Log.d("Sending replica 2 from "+myPort+"to ",myPredicessor2);
            new Client().execute(Outgoing2);

            return null;
        }
        else{
            Packet Outgoing=new Packet();
            Packet Outgoing2=new Packet();
            Packet Outgoing3=new Packet();
            Outgoing.message="insert";
            Outgoing.key=values.getAsString("key");
            Outgoing.value=values.getAsString("value");
            Outgoing.sendersPort=myPort;
            Outgoing.recieversPort=temp.matchingNode;
            //Log.i("Sending to origin",temp.matchingNode);
            new Client().execute(Outgoing);

            Outgoing2.message="replica";
            Outgoing2.key=values.getAsString("key");
            Outgoing2.value=values.getAsString("value");
            Outgoing2.sendersPort=temp.matchingNode;
            Outgoing2.recieversPort=temp.succ1;
            //Log.d("Sending replica 1 from "+myPort+"to ",temp.succ1);
            new Client().execute(Outgoing2);

            Outgoing3.message="replica";
            Outgoing3.key=values.getAsString("key");
            Outgoing3.value=values.getAsString("value");
            Outgoing3.sendersPort=temp.matchingNode;
            Outgoing3.recieversPort=temp.succ2;
            //Log.d("Sending replica 2 from "+myPort+"to ",temp.succ2);
            new Client().execute(Outgoing3);

            return null;
        }
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        MatrixCursor mc = new MatrixCursor(new String[]{"key","value"});
        NodeInfo temp=new NodeInfo();
        Log.i("Recieved direct query ",selection);
        

        if(selection.equals("@")){
            Log.i("Value of initReady before while is",""+initReady);
            while(initReady<3){try {
                irLock.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                Log.e("Problem while waiting in","@");
                e.printStackTrace();
            }
            }    

            
            String[] savFiles = getContext().fileList();
            String curFile=new String();
            for (int i = 0; i < savFiles.length; i++) {
                curFile=savFiles[i];
                String column[] = new String[2];
                column[0] = curFile;

                column[1] = func.qry(curFile);    

                mc.addRow(column);
            }

            Log.i("Returning @ values","!!"); 
            return mc;

        }


        else if(selection.equals("*")){
            //broadcasting start to all
            String[] savFiles = getContext().fileList();
            for (int i = 0; i < savFiles.length; i++) {
                selection=savFiles[i];

                starResponse.keyValue.put(selection, func.qry(selection));
            }

            String[] remotePorts={"5562","5556","5554","5558","5560"};
            for(String s:remotePorts){
                if(!s.equals(myPort)){
                    Packet Outgoing=new Packet();
                    Outgoing.message="*query";
                    Outgoing.recieversPort=s;
                    Outgoing.sendersPort=myPort;
                    new Client().execute(Outgoing);
                }
            }

            /// wait for a few seconds for all responses to get compiled.
            try {
                Thread.sleep(2400);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
            // release the collection of all replies
            List<String> keys = new ArrayList<String>(starResponse.keyValue.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String column[] = new String[2];
                column[0]= key;
                column[1]= starResponse.keyValue.get(column[0]);
                Log.d("starMap: "+column[0], column[1]);
                mc.addRow(column);

            }
            // this might cause errors, update: did not
            starResponse=null;
            return mc;

        }



        else{

            temp=Lookup(selection);
            if((temp.matchingNode.equals(myPort)||temp.succ1.equals(myPort)||temp.succ2.equals(myPort)) && (func.qry(selection).length()>3)){
                Log.i("Query belongs to me",selection);
                String column[] = new String[2];
                column[0] = selection;
                column[1] = func.qry(selection);
                mc.addRow(column);
                //String value=func.qry(selection);
                Packet Outgoing=new Packet();
                Outgoing.message="query";
                Outgoing.key=selection;
                Outgoing.sendersPort=myPort;
                // Outgoing to be sent to Succ1 and Succ2 and wait till timeout!!
                //return cursor
                return mc;
            }
            else{
                Log.d("Qry belongs to "+temp.matchingNode,selection);
                curKey=selection;
                Reply=0;
                Packet Outgoing=new Packet();
                Outgoing.message="query";
                Outgoing.key=selection;
                Outgoing.sendersPort=myPort;
                Outgoing.recieversPort=temp.matchingNode;
                new Client().execute(Outgoing);
                Log.d("Qry belongs to "+temp.matchingNode+"sent to "+Outgoing.recieversPort,selection);
                Packet Outgoing2=new Packet();
                Outgoing2.message="query";
                Outgoing2.key=selection;
                Outgoing2.sendersPort=myPort;
                Outgoing2.recieversPort=temp.succ1;
                new Client().execute(Outgoing2);
                Log.d("Qry belongs to "+temp.matchingNode+"sent to "+Outgoing2.recieversPort,selection);
                Packet Outgoing3=new Packet();
                Outgoing3.message="query";
                Outgoing3.key=selection;
                Outgoing3.sendersPort=myPort;
                Outgoing3.recieversPort=temp.succ2;
                new Client().execute(Outgoing3);
                Log.d("Qry belongs to "+temp.matchingNode+"sent to "+Outgoing3.recieversPort,selection);
                Log.d("waiting for a response on",selection);
                while(Reply<1){}
                //Reply=false;
                //TODO
                curKey="Tanmay";
                String[] column=new String[2];
                column[0] = selection;
                column[1] = response;
                mc.addRow(column);
                Log.d("Response recieved, returning",selection);
                return mc;

            }

        }
        //return null;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {

        return 0;
    }


    ///////// Listening Server////////////////////

    public class Server extends AsyncTask<ServerSocket, Packet, Void>
    {

        @Override
        protected Void doInBackground(ServerSocket... params) {

            //try {
            ServerSocket serverSocket = params[0];
            while(true){
                try{
                    synchronized(initLock){
                    Socket accept = serverSocket.accept();
                    ObjectInputStream in = new ObjectInputStream(accept.getInputStream());
                    
                    Packet Incoming=new Packet();
                    Incoming=(Packet) in.readObject();
                    
                    //
                    //TODO
                    if(Incoming.message.equals("replica")){
                        //Log.d("Replica recieved from "+Incoming.sendersPort,"Pred1:"+myPredicessor1+
                        //        " Pred2:"+myPredicessor2);
                        
                        if(Incoming.sendersPort.equals(myPredicessor1)){
                            pred1Vals.put(Incoming.key, Incoming.value);
                            func.Ins(Incoming.key, Incoming.value);
                        }
                        else if(Incoming.sendersPort.equals(myPredicessor2)){
                            pred2Vals.put(Incoming.key, Incoming.value);
                            func.Ins(Incoming.key, Incoming.value);
                        }
                        }
                        //Log.d("replica recieved", " key"+Incoming.key+" value"+Incoming.value);
                    

                    else if(Incoming.message.equals("insert")){
                        
                        //Log.d("insert recieved", " key"+Incoming.key+" value"+Incoming.value);
                        myVals.put(Incoming.key, Incoming.value);
                        func.Ins(Incoming.key, Incoming.value);
                        
                    }
                    else if(Incoming.message.equals("query")){
                        Log.d("Query recieved from "+Incoming.sendersPort,Incoming.key);
                        Packet Outgoing=new Packet();
                        String value=new String();

                        value=func.qry(Incoming.key);    

                        Outgoing.message="qryResponse";
                        Outgoing.key=Incoming.key;
                        Outgoing.value=value;
                        Outgoing.sendersPort=myPort;
                        Outgoing.recieversPort=Incoming.sendersPort;
                        new Client().execute(Outgoing);
                        Log.d("Sent response back to "+Outgoing.recieversPort,Outgoing.key);

                    }
                    else if(Incoming.message.equals("*query")){
                        Packet Outgoing=new Packet();
                        String selection=new String();
                        String[] savFiles = getContext().fileList();
                        //int size=Incoming.indexKey.size();
                        for (int i = 0; i < savFiles.length; i++) {
                            selection=savFiles[i];
                            Outgoing.indexKey.put(i, selection);

                            Outgoing.keyValue.put(selection, func.qry(selection));

                        }
                        Outgoing.message="*response";
                        Outgoing.recieversPort=Incoming.sendersPort;
                        Outgoing.sendersPort=myPort;
                        new Client().execute(Outgoing);
                    }
                    else if(Incoming.message.equals("*response")){

                        for(int i=0;i<Incoming.indexKey.size();i++){


                            starResponse.keyValue.put(Incoming.indexKey.get(i), Incoming.keyValue.get(Incoming.indexKey.get(i)));
                           
                        }
                     
                    }
                    else if(Incoming.message.equals("*del")){
                        String[] savFiles = getContext().fileList();
                        for (int i = 0; i < savFiles.length; i++) {
                            myVals.remove(savFiles[i]);
                            func.del(savFiles[i]);
                        }
                    }
                    else if(Incoming.message.equals("del")){
                        myVals.remove(Incoming.key);
                        func.del(Incoming.key);
                    }
                    else if(Incoming.message.equals("repDel")){

                        if(Incoming.sendersPort.equals(myPredicessor1)){
                            pred1Vals.remove(Incoming.key);
                            func.del(Incoming.key);
                        }
                        else if(Incoming.sendersPort.equals(myPredicessor2)){
                            pred2Vals.remove(Incoming.key);
                            func.del(Incoming.key);
                        }
                    }

                    else if(Incoming.message.equals("qryResponse") &&
                            Incoming.key.equals(curKey)){
                        if(Incoming.value.length()>3){
                            Log.i("Response Recieved from "+Incoming.sendersPort,Incoming.key +" : "+Incoming.value);
                            response=Incoming.value;
                            Reply++;    
                        }
                        else
                            Log.e("Null Response Recieved from "+Incoming.sendersPort,Incoming.key +" : "+Incoming.value);
                        //Reply++;

                    }

                    else if(Incoming.message.equals("initVals")){
                        //while(initReady<3){}
                        if(Incoming.sendersPort.equals(myPredicessor1)){
                            Packet Outgoing=new Packet();
                            Outgoing.message="initResponse";
                            Outgoing.keyValue=pred1Vals;
                            Outgoing.sendersPort=myPort;
                            Outgoing.recieversPort=Incoming.sendersPort;
                            new Client().execute(Outgoing);
                        }
                        else if(Incoming.sendersPort.equals(myPredicessor2)){
                            Packet Outgoing=new Packet();
                            Outgoing.message="initResponse";
                            Outgoing.keyValue=pred2Vals;
                            Outgoing.sendersPort=myPort;
                            Outgoing.recieversPort=Incoming.sendersPort;
                            new Client().execute(Outgoing);
                        }
                        else if(Incoming.sendersPort.equals(mySuccessor1)){
                            Packet Outgoing=new Packet();
                            Outgoing.message="initResponse";
                            Outgoing.keyValue=myVals;
                            Outgoing.sendersPort=myPort;
                            Outgoing.recieversPort=Incoming.sendersPort;
                            new Client().execute(Outgoing);
                        }
                        else if(Incoming.sendersPort.equals(mySuccessor2)){
                            Packet Outgoing=new Packet();
                            Outgoing.message="initResponse";
                            Outgoing.keyValue=myVals;
                            Outgoing.sendersPort=myPort;
                            Outgoing.recieversPort=Incoming.sendersPort;
                            new Client().execute(Outgoing);
                        }
                    }
                    else if(Incoming.message.equals("initResponse")){
                        //synchronized (obj) {
                           

                        if(Incoming.sendersPort.equals(mySuccessor1)){
                            myVals=Incoming.keyValue;
                            Log.d("initResponse recieved from "+Incoming.sendersPort,"Successor1"+mySuccessor1);
                        }
                        else if(Incoming.sendersPort.equals(mySuccessor2)){
                            myVals=Incoming.keyValue;
                            Log.d("initResponse recieved from "+Incoming.sendersPort,"Successor2"+mySuccessor2);
                        }
                        else if(Incoming.sendersPort.equals(myPredicessor1)){
                            pred1Vals=Incoming.keyValue;
                            Log.d("initResponse recieved from "+Incoming.sendersPort,"Predicessor1"+myPredicessor1);
                        }
                        else if(Incoming.sendersPort.equals(myPredicessor2)){
                            pred2Vals=Incoming.keyValue;
                            Log.d("initResponse recieved from "+Incoming.sendersPort,"Predicessor2"+myPredicessor2);
                        }

                        List<String> keys = new ArrayList<String>(Incoming.keyValue.keySet());

                        for (int i = 0; i < keys.size(); i++) {
                            String key = keys.get(i);
                            String value = Incoming.keyValue.get(key);
                            Log.d("Writing values from "+Incoming.sendersPort,key+" "+value);
                            func.Ins(key,value);
                        }
                        synchronized(irLock){
                        initReady++;
                        irLock.notify();
                        }
                        //}
                    }
                    else{
                        Log.e("Late Reply|Discarding|U don't belong here!!"+Incoming.message,Incoming.key+" : "+Incoming.value);
                    }

                    
                    in.close();
                    accept.close();
                    }
                }
                catch (Exception e) {

                    e.printStackTrace();
                } 
            }
        
        }

    }


    /////////Sending Client function///////


    public class Client extends AsyncTask<Packet, Void , Void>{

        @Override
        protected  Void doInBackground(Packet... params) {

            try{
                //Log.e("Sending",params[0].recieversPort);
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        (Integer.parseInt(params[0].recieversPort)*2));
                //Log.i("sendingTo",(Integer.parseInt(params[0].recieversPort))+"");

                ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                Object temp=params[0];
                out.writeObject(temp);
                out.flush();
                out.close();
                socket.close();
                //Log.i(myPort,"Message Sent");
            }
            catch( Exception e){
                Log.e("Error sending from Client to port",params[0].recieversPort);
                if(params[0].message.equals("initVals"))
                    synchronized(irLock){
                        initReady++;
                        irLock.notify();
                    }
                    
                    
                Log.e("I was doing : ", params[0].message);
                e.printStackTrace();
            }
            return null;
        }
    }


    ///////////// Content modification functions//////////////

    public class Functions
    {
        public int del(String key){
            getContext().deleteFile(key);
            return 0;
        }
        public void Ins(String key, String value){
            synchronized(insLock){
            String Filename = key;
            String string = value;
            //Log.e(Filename, string );
            File Location = new File(getContext().getFilesDir().getAbsolutePath());

            try {
                File fout=new File(Location+"/"+Filename);
                FileOutputStream outputStream = new FileOutputStream(fout);
                //Log.v(Filename+" Write", string);
                outputStream.write(string.getBytes());
                outputStream.close();

            } catch (Exception e) {
                Log.e("File not written", Filename);
                e.printStackTrace();
            }
            }
        }

        public String qry(String selection){
//            synchronized(queryLock){
                //MatrixCursor mc = new MatrixCursor(new String[]{"key","value"});
              
                File Location = new File(getContext().getFilesDir().getAbsolutePath());
                BufferedReader br = null;
                StringBuilder sb = new StringBuilder();
                String column[] = new String[2];
                column[0] = selection;
                //Log.e("Read filename", selection );
                try{
                    File fin=new File(Location+"/"+(selection));
                    FileInputStream inputStream = new FileInputStream(fin);
                    br=new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
                catch(Exception e){
                    Log.e("error while reading or Hashing filename",e.getMessage());
                    e.printStackTrace();
                }
                finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //Log.e("Read Value", sb.toString());
                return sb.toString();
                //TODO
            
        }
    }

    /////////////Lookup Method/////////////
    private NodeInfo Lookup(String check){
        try{
            for(int i=1;i<5;i++){
                if( (genHash(check).compareTo(genHash(int2nodes.get(i))) > 0) && 
                        (genHash(check).compareTo(genHash(int2nodes.get(i+1))) <= 0)){
                    return nodeLookup(int2nodes.get(i+1));
                }
            }
            if((genHash(check).compareTo(genHash(int2nodes.get(1))) < 0) ||
                    (genHash(check).compareTo(genHash(int2nodes.get(5))) > 0)){
                return nodeLookup(int2nodes.get(1));
            }
        }
        catch(Exception e){
            Log.e("Error in Lookup","Lookup Error");
            e.printStackTrace();
        }

        return null;
    }

    /////////////nodeLookup Method/////////////
    public NodeInfo nodeLookup(String curNode){
        NodeInfo returnVal=new NodeInfo();
        int i=nodes2int.get(curNode);
        if(i==1){
            returnVal.matchingNode=curNode;
            returnVal.succ1=int2nodes.get(2);
            returnVal.succ2=int2nodes.get(3);
            returnVal.pred1=int2nodes.get(5);
            returnVal.pred2=int2nodes.get(4);
            return returnVal;
        }
        else if (i == 2){
            returnVal.matchingNode=curNode;
            returnVal.succ1=int2nodes.get(3);
            returnVal.succ2=int2nodes.get(4);
            returnVal.pred1=int2nodes.get(1);
            returnVal.pred2=int2nodes.get(5);
            return returnVal;
        }
        else if (i == 3){
            returnVal.matchingNode=curNode;
            returnVal.succ1=int2nodes.get(4);
            returnVal.succ2=int2nodes.get(5);
            returnVal.pred1=int2nodes.get(2);
            returnVal.pred2=int2nodes.get(1);
            return returnVal;
        }
        else if(i==4){
            returnVal.matchingNode=curNode;
            returnVal.succ1=int2nodes.get(5);
            returnVal.succ2=int2nodes.get(1);
            returnVal.pred1=int2nodes.get(3);
            returnVal.pred2=int2nodes.get(2);
            return returnVal;
        }
        else{
            returnVal.matchingNode=curNode;
            returnVal.succ1=int2nodes.get(1);
            returnVal.succ2=int2nodes.get(2);
            returnVal.pred1=int2nodes.get(4);
            returnVal.pred2=int2nodes.get(3);
            return returnVal;        }
    }

    //////// Hashing Function///////////////
    private String genHash(String input) throws NoSuchAlgorithmException {
        String returnVal;
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        returnVal=formatter.toString();
        formatter.close();
        return returnVal;
    }
}


// to reinstall!!