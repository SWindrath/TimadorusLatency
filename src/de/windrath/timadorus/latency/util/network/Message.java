package de.windrath.timadorus.latency.util.network;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message{

    private int iD;

    private Point position;
    
    private long timestamp;
    
    private boolean successful;
    
	private Message(int iD, Point position, long timestamp, boolean b){
        this.iD = iD;
        this.position = position;
        this.timestamp = timestamp;
        this.successful = b;
    }
    
    public Message(int iD, Point position){
        this(iD, position, System.currentTimeMillis(), false);
    }
    
    private Message(int iD, int xPosition, int yPosition, long timestamp, boolean b){
        this(iD, new Point(xPosition, yPosition), timestamp, b);
    }
    
    public Message(int iD, int xPosition, int yPosition){
        this(iD, new Point(xPosition, yPosition));
    }
    
    public int getiD() {
        return iD;
    }

    public Point getPosition() {
        return position;
    }
    
    public int getxPosition() {
        return position.x;
    }

    public int getyPosition() {
        return position.y;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
    
    public static Message deserialize(byte[] rawMessage) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(rawMessage);
        DataInputStream dis = new DataInputStream(bais);
        return new Message(dis.readInt(), dis.readInt(), dis.readInt(), dis.readLong(), dis.readBoolean());
    }

    public byte[] serialize() throws IOException {
        
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            
            dos.writeInt(iD);
            dos.writeInt(position.x);
            dos.writeInt(position.y);
            dos.writeLong(timestamp);
            dos.writeBoolean(successful);
            
            return baos.toByteArray();
        }  finally {
            if (dos != null) {
                dos.close();
            }
            if (baos != null) {
                baos.close();
            }
        }
    }
}
