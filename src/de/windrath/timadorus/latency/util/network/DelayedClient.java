package de.windrath.timadorus.latency.util.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.Message;

/**
 * Based on the TCP example by David Drager found at
 * http://systembash.com/content/a-simple-java-tcp-server-and-tcp-client/
 * @author Steffen
 *
 */
public class DelayedClient implements Runnable{

    private InetAddress host;
    
    private int port;

    private int minDelay;

    private int doubleDeviation;

    private BlockingQueue<Message> inputQueue;

    private BlockingQueue<Message> outputQueue;

    private Message deserializer;
        
    public DelayedClient(String host, int port, int delay, int deviation, Message deserializer) throws UnknownHostException {
        this(InetAddress.getByName(host), port, delay, deviation, deserializer);
    }
    
    public DelayedClient(InetAddress host, int port, int delay, int deviation, Message deserializer) {
        this.host = host;
        this.port = port;
        this.minDelay = Math.max(delay - deviation, 0);
        this.doubleDeviation = Math.min(deviation * 2, minDelay);
        this.inputQueue = new ArrayBlockingQueue<Message>(1024);
        this.outputQueue = new ArrayBlockingQueue<Message>(1024);
        this.deserializer = deserializer;
    }
    
    @Override
    public void run() {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
            }
        }


    }
}
