package de.windrath.timadorus.latency.prediction.server;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.Message;

public class PredictionServerLogic implements Runnable {

    /**
     * Incoming Messages
     */
    private BlockingQueue<Message> inputQueue;

    /**
     * outgoing Messages
     */
    private BlockingQueue<Message> outputQueue;

    private CoordGui gui;

    public PredictionServerLogic(BlockingQueue<Message> inputQueue,
            BlockingQueue<Message> outputQueue, CoordGui gui) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
        	Random generator = new Random();
            while (!Thread.interrupted()) {
                //Take next Message
                Message msg = inputQueue.take();
                //Randomly set if the Message was accepted or not.
                msg.setSuccessful(generator.nextBoolean());
                //Display Message content in GUI if successful
                if(msg.isSuccessful()){
                    gui.setPrimaryEntityPosition(msg.getiD(), msg.getPosition());
                }
                //Send Message to all clients
                outputQueue.put(msg);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
