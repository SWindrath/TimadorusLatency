package de.windrath.timadorus.latency.simpel.server;

import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.Message;

public class SimpleServerLogic implements Runnable {

    /**
     * Incoming Messages
     */
    private BlockingQueue<Message> inputQueue;

    /**
     * outgoing Messages
     */
    private BlockingQueue<Message> outputQueue;

    private CoordGui gui;

    public SimpleServerLogic(BlockingQueue<Message> inputQueue,
            BlockingQueue<Message> outputQueue, CoordGui gui) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                //Take next Message
                Message msg = inputQueue.take();
                //Display Message content in GUI
                gui.setPrimaryEntityPosition(msg.getiD(), msg.getPosition());
                gui.setSecondaryEntityPosition(msg.getiD(), msg.getPosition());
                //Send Message to all clients
                outputQueue.put(msg);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
