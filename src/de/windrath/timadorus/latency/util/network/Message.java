package de.windrath.timadorus.latency.util.network;

public interface Message {
    
    public Message deserialize(byte[] rawMessage);
    
    public byte[] serialize();
    
}
