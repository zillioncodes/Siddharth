package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.Serializable;

public class InsertQueryPacket implements Serializable {
    String message;
    String key;
    String value;
    String sendersPort;
    String recieversPort;
    int version;
InsertQueryPacket(){}
}
