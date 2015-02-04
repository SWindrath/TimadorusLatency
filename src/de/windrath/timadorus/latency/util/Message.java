package de.windrath.timadorus.latency.util;

public interface Message {
    
    public Message deserialize(byte[] rawMessage);
    
    public byte[] serialize();
    
}
