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
        // Client sends messages to the Server
        System.out.println("clientLogic sends Message to clientSocket");
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
                System.out.println("Client:Message recieved");
                gui.setPrimaryEntityPosition(msg.getiD(), msg.getPosition());
                gui.setSecondaryEntityPosition(msg.getiD(), msg.getPosition());
            }
        } catch (InterruptedException e) {
        }
    }
}
