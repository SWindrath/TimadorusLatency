package de.windrath.timadorus.latency.util.network;

public class Message{

    private int iD;

    private int xPosition;
    
    private int yPosition;
    
    private long timestamp;
    
    public int getiD() {
        return iD;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    public static Message deserialize(byte[] rawMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    public byte[] serialize() {
        // TODO Auto-generated method stub
        return null;
    }

}
