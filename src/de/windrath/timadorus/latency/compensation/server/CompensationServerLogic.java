package de.windrath.timadorus.latency.compensation.server;

import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.World;
import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.Message;

/**
 * This Code is an Example that is not working.
 *
 */
public class CompensationServerLogic implements Runnable {

	/**
	 * Incoming Messages
	 */
	private BlockingQueue<Message> inputQueue;

	/**
	 * outgoing Messages
	 */
	private BlockingQueue<Message> outputQueue;

	private CoordGui gui;

	private World world = new World();

	public CompensationServerLogic(BlockingQueue<Message> inputQueue,
			BlockingQueue<Message> outputQueue, CoordGui gui) {
		this.inputQueue = inputQueue;
		this.outputQueue = outputQueue;
		this.gui = gui;
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				// Take next Message
				Message msg = inputQueue.take();
				// Set the Server World back by x seconds, where x is the
				// current latency.
				// In this example, the latency is calculated by comparing the
				// current time with the creation time of the message.
				// This is a simplified process to show the mechanic, it is not
				// applicable in practice, i.e. since the clocks of the server
				// and clients are not synchronized.
				world.setTimeTo(msg.getTimestamp());
				// Change the World according to the Message
				world.incorporate(msg);
				// Set the World's time to the current time.
				world.revertTime();
				outputQueue.add(world.delta());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
