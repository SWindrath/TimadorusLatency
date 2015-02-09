package de.windrath.timadorus.latency.ticks.client;

import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.World;
import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.DelayedClientSocket;
import de.windrath.timadorus.latency.util.network.IClientLogic;
import de.windrath.timadorus.latency.util.network.Message;

/**
 * This Code is an Example that is not working.
 *
 */
public class TicksClientLogic implements IClientLogic, Runnable{

    private DelayedClientSocket client;
    
    private World world = new World();
    
    CoordGui gui;

    public TicksClientLogic(DelayedClientSocket client) {
        this.client = client;
    }
    
    public void setGui(CoordGui gui) {
        this.gui = gui;
    }
    
    @Override
    public void onGUIEvent(Message msg) {
    	//Set the Position of the blue Cross 
    	gui.setTertiaryEntityPosition(msg.getiD(), msg.getPosition());
    	// The Client processes the Input
    	world.incorporate(msg);
        // Client sends messages to the Server
    	client.send(world.delta());
    }

    @Override
    public void run() {
        // Client processes messages from the Server
        BlockingQueue<Message> queue = client.getOutputQueue();
        try {
            while(!Thread.interrupted()){
                //Take next message, wait if no messages are available.
                Message msg = queue.take();
                //Add the Message to the current World. If the Message contains a delta World,
                //the World will be changed accordingly. If the Message contains a World,
                //the local World will be overwritten.
                world.incorporate(msg);
            }
        } catch (InterruptedException e) {
        }
    }
}
