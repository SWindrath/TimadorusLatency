package de.windrath.timadorus.latency.util.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Config {
    
    // Server config
    
    public final static InetAddress SERVER_ADDRESS;
    public final static int SERVER_PORT = 56867;
    
    static{
        try {
            SERVER_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to get host address");
        }
    }
    
    
}
