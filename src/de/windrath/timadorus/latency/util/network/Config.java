package de.windrath.timadorus.latency.util.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Config {
    
    // Server Config
    // Initialize the Server Address in the static block
    public final static InetAddress SERVER_ADDRESS;
    public final static int SERVER_PORT = 56867;
    public static final int DELAY = 1000;
    public static final int DEVIATION = 10;
    
    static{
        try {
            //Set InetAdress here
            SERVER_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to get host address");
        }
    }
}
