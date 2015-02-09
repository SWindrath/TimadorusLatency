package de.windrath.timadorus.latency.util.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Based on the TCP example by David Drager found at
 * http://systembash.com/content/a-simple-java-tcp-server-and-tcp-client/
 * 
 * @author Steffen
 *
 */
public class DelayedServerSocket implements Runnable {

    private List<Thread> listenerSenderList;

    private int port;

    private int minDelay;

    private int doubleDeviation;

    /**
     * Queue will be filled with recieved messages.
     */
    private BlockingQueue<Message> inputQueue;

    /**
     * All messages added to this Queue will be send to the clients.
     */
    private BlockingQueue<Message> outputQueue;

    /**
     * Instantiates a new delayed server. This Server will wait for messages and
     * reply after the delay. if a deviation is given, the delay will be a
     * random number between delay-deviation and delay+deviation.
     *
     * @param port
     *            the port
     * @param delay
     *            the delay
     * @param deviation
     *            the deviation
     */
    public DelayedServerSocket(int port, int delay, int deviation) {
        listenerSenderList = new ArrayList<Thread>();
        this.port = port;
        this.inputQueue = new ArrayBlockingQueue<Message>(1024);
        this.outputQueue = new ArrayBlockingQueue<Message>(1024);
        // Delay and deviation is modified for the use with the java.util.Random
        // class
        this.minDelay = delay - deviation;
        this.doubleDeviation = deviation * 2;
    }

    /**
     * Instantiates a new delayed server. This Server will wait for messages and
     * reply after the delay. if a deviation is given, the delay will be a
     * random number between delay-deviation and delay+deviation.
     *
     * @param port
     *            the port
     * @param delay
     *            the delay
     */
    public DelayedServerSocket(int port, int delay) {
        this(port, delay, 0);
    }

    /**
     * Queue will be filled with recieved messages.
     */
    public BlockingQueue<Message> getInputQueue() {
        return inputQueue;
    }

    /**
     * All messages added to this Queue will be send to the clients.
     */
    public BlockingQueue<Message> getOutputQueue() {
        return outputQueue;
    }

    @Override
    public void run() {

        ServerSocket welcomeSocket = null;
        Sender sender;
        Thread senderThread;
        try {
            // Create ServerSocket and a Thread, that sends answers back to the
            // clients
            welcomeSocket = new ServerSocket(port);
            sender = new Sender(minDelay, doubleDeviation, outputQueue);
            senderThread = new Thread(sender);
            senderThread.start();

            while (!Thread.interrupted()) {
                // Wait for connection
                Socket connectionSocket = welcomeSocket.accept();
                // If a Client connected, a Thread, that listens for messages
                // from that Client, will be created. This part is in an own
                // try/catch block, so that an exception when creating a new
                // Listener and Sender won't let the whole server crash.
                try {
                    listenerSenderList.add(Listener.newInstance(
                            connectionSocket, inputQueue, sender));
                    sender.addClient(connectionSocket);
                } catch (IOException e) {
                    // Ignore or Log
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                welcomeSocket.close();
            } catch (IOException e) {
            }

            for (Thread thread : listenerSenderList) {
                thread.interrupt();
            }
        }

    }

	public void sendTo(int getiD, Message message) {
		// TODO Auto-generated method stub
		
	}
}

class Listener implements Runnable {

    private Socket connectionSocket;

    private InputStream in;

    private BlockingQueue<Message> inputQueue;

    private Sender sender;

    private Listener(Socket connectionSocket,
            BlockingQueue<Message> inputQueue, Sender sender)
            throws IOException {
        this.inputQueue = inputQueue;
        this.connectionSocket = connectionSocket;
        this.sender = sender;
        this.in = connectionSocket.getInputStream();
    }

    /**
     * Creates a new Listener, that is automatically started.
     * 
     * @param connectionSocket
     * @param sender
     * @param doubleDeviation
     * @param minDelay
     * @throws IOException
     */
    static Thread newInstance(Socket connectionSocket,
            BlockingQueue<Message> inputQueue, Sender sender)
            throws IOException {
        Thread thread = new Thread(new Listener(connectionSocket, inputQueue,
                sender));
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        try {
            // Initialize
            byte[] buffer = new byte[1024];

            // Run until stopped
            while (!Thread.interrupted()) {
                // Read messages from the Client
                int bytes = in.read(buffer);
                if (bytes == -1) {
                    System.out.println("Socket Closed");
                    break;
                } else {
                    System.out.println("Message recieved");
                }
                byte[] rawMessage = Arrays.copyOfRange(buffer, 0, bytes);
                // Put message into outputQueue
                inputQueue.add(Message.deserialize(rawMessage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the Socket, if finished
                connectionSocket.close();
                sender.removeClient(connectionSocket);
            } catch (IOException e) {
            }
        }
    }

}

class Sender implements Runnable {

    private BlockingQueue<Message> outputQueue;

    private CopyOnWriteArraySet<OutputStream> clients;

    private Random generator;

    private int minDelay;

    private int doubleDeviation;

    public Sender(int minDelay, int doubleDeviation,
            BlockingQueue<Message> outputQueue) throws IOException {
        this.minDelay = minDelay;
        this.doubleDeviation = doubleDeviation;
        generator = new Random();
        clients = new CopyOnWriteArraySet<OutputStream>();
        this.outputQueue = outputQueue;
    }

    public void addClient(Socket connectionSocket) throws IOException {
        clients.add(connectionSocket.getOutputStream());
    }

    public void removeClient(Socket connectionSocket) throws IOException {
        clients.remove(connectionSocket.getOutputStream());
    }

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            try {
                System.out.println("Wait for Message");
                Message message = outputQueue.take();
                System.out.println("Sending Message to Clients");
                final byte[] rawMessage = message.serialize();
                for (final OutputStream out : clients) {
                    System.out.println("Send Message to Client");
                    Thread sendThread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            // Send reply back to the Client after 'delay'
                            // milliseconds. This will be done in an own thread
                            // to ensure, that no message from the Client is
                            // missed while the Thread waits during the delay
                            try {
                                Thread.sleep(minDelay
                                        + generator.nextInt(doubleDeviation));
                                out.write(rawMessage);
                            } catch (InterruptedException e) {
                                // This thread wont be interrupted
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    sendThread.start();
                }

            } catch (InterruptedException e) {
            } catch (IOException e) {
                e.printStackTrace();
            } // no need for a finally block, as the sockets will be closed by
              // the listeners
        }

    }

}