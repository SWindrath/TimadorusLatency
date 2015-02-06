package de.windrath.timadorus.latency.util.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Based on the TCP example by David Drager found at
 * http://systembash.com/content/a-simple-java-tcp-server-and-tcp-client/
 * 
 * @author Steffen
 *
 */
public class DelayedClientSocket implements Runnable {

    private int minDelay;

    private int doubleDeviation;

    private BlockingQueue<Message> outputQueue;

    private Random generator;

    Socket clientSocket = null;

    public DelayedClientSocket(String host, int port, int delay, int deviation) throws IOException {
        this(InetAddress.getByName(host), port, delay, deviation);
    }

    public DelayedClientSocket(InetAddress host, int port, int delay, int deviation) throws IOException {
        this.generator = new Random();
        this.minDelay = Math.max(delay - deviation, 0);
        this.doubleDeviation = Math.min(deviation * 2, minDelay);
        this.outputQueue = new ArrayBlockingQueue<Message>(1024);
        clientSocket = new Socket(host, port);
    }

    public BlockingQueue<Message> getOutputQueue() {
        return outputQueue;
    }

    @Override
    public void run() {
        System.out.println("Start listening Thread");
        InputStream in;
        try {

            in = clientSocket.getInputStream();
            byte[] buffer = new byte[1024];

            // Run until stopped
            System.out.println("Listen loop");
            while (!Thread.interrupted()) {
                // Read messages from the Client
                System.out.println("Listen loop");
                int bytes = in.read(buffer);
                if (bytes == -1) {
                    System.out.println("Break");
                    break;
                }
                
                byte[] rawMessage = Arrays.copyOfRange(buffer, 0, bytes);
                // Put message into outputQueue
                System.out.println("Client: Add Message to outputQueue");
                outputQueue.add(Message.deserialize(rawMessage));
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("End listening Thread");
    }

    public void send(Message msg) {
        try {
            byte[] rawMessage = msg.serialize();
            Thread.sleep(minDelay + generator.nextInt(doubleDeviation));
            clientSocket.getOutputStream().write(rawMessage);
        } catch (InterruptedException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
