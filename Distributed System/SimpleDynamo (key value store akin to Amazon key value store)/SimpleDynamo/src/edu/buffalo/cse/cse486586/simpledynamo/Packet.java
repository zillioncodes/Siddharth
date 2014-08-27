package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Packet implements Serializable {
    //private static final long serialVersionUID = 1L;
    private static final long serialVersionUID = 123456543L;
    String sendersPort;
    String recieversPort;
    String message;
    String key;
    String value;
    String reply;
    Map<Integer,String> indexKey=new HashMap<Integer, String>();
    Map<String,String> keyValue=new HashMap<String, String>();
    public Packet() {
        // TODO Auto-generated constructor stub
    }

}
