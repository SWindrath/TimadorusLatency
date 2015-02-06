package de.windrath.timadorus.latency.ticks.server;

import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.Message;

public class TicksServerLogic implements Runnable {

    /**
     * Incoming Messages
     */
    private BlockingQueue<Message> inputQueue;

    /**
     * outgoing Messages
     */
    private BlockingQueue<Message> outputQueue;

    private CoordGui gui;

    public TicksServerLogic(BlockingQueue<Message> inputQueue,
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
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
