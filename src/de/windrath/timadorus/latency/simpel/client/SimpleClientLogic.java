package de.windrath.timadorus.latency.simpel.client;

import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.DelayedClientSocket;
import de.windrath.timadorus.latency.util.network.IClientLogic;
import de.windrath.timadorus.latency.util.network.Message;

public class SimpleClientLogic implements IClientLogic, Runnable{

    private DelayedClientSocket client;
    
    CoordGui gui;

    public SimpleClientLogic(DelayedClientSocket client) {
        this.client = client;
    }
    
    public void setGui(CoordGui gui) {
        this.gui = gui;
    }
    
    @Override
    public void onGUIEvent(Message msg) {
    	//Set the Position of the blue Cross 
    	gui.setTertiaryEntityPosition(msg.getiD(), msg.getPosition());
        // Client sends messages to the Server
        client.send(msg);    
    }

    @Override
    public void run() {
        // Client processes messages from the Server
        BlockingQueue<Message> queue = client.getOutputQueue();
        try {
            while(!Thread.interrupted()){
                //Take next message, wait if no messages are available.
                Message msg = queue.take();
                gui.setPrimaryEntityPosition(msg.getiD(), msg.getPosition());
                gui.setSecondaryEntityPosition(msg.getiD(), msg.getPosition());
            }
        } catch (InterruptedException e) {
        }
    }
}
