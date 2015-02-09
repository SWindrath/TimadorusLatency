package de.windrath.timadorus.latency.ticks.server;

import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.World;
import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.DelayedServerSocket;
import de.windrath.timadorus.latency.util.network.Message;

/**
 * This Code is an Example that is not working.
 *
 */
public class TicksServerLogic implements Runnable {

	World world = new World();
	
    /**
     * Incoming Messages
     */
    private BlockingQueue<Message> inputQueue;

    /**
     * outgoing Messages
     */
    private BlockingQueue<Message> outputQueue;

    private CoordGui gui;

	private DelayedServerSocket server;

    public TicksServerLogic(BlockingQueue<Message> inputQueue,
            BlockingQueue<Message> outputQueue, CoordGui gui, DelayedServerSocket server) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
        this.gui = gui;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                //Take next Message
                Message msg = inputQueue.take();
                //Check, if the client's world deviation is too big.
                boolean accepted = world.check(msg);
                if(accepted){
                	//Change world according to the (accepted) message.
                	world.incorporate(msg);
                	//Send the data about the changes to all Clients
                	outputQueue.add(world.delta());
                }else{ //The World of the Client contains too many errors.
                	//Send a Message containing the current World of the Server to the Client.
                	server.sendTo(msg.getiD(), world.toMessage());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
