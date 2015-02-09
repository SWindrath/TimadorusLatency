package de.windrath.timadorus.latency.prediction.client;

import java.util.concurrent.BlockingQueue;

import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.DelayedClientSocket;
import de.windrath.timadorus.latency.util.network.IClientLogic;
import de.windrath.timadorus.latency.util.network.Message;

/**
 * This example assumes, that new messages cant be send fast enough to disturb
 * each other. (i.e. a new message before the old message was accepted/rejected)
 * 
 * @author Steffen
 * 
 */
public class PredictionClientLogic implements IClientLogic, Runnable {

	private DelayedClientSocket client;

	CoordGui gui;

	private int myID;

	public PredictionClientLogic(DelayedClientSocket client, int myID) {
		this.client = client;
		this.myID = myID;
	}

	public void setGui(CoordGui gui) {
		this.gui = gui;
	}

	@Override
	public void onGUIEvent(Message msg) {
    	//Set the Position of the blue Cross 
    	gui.setTertiaryEntityPosition(msg.getiD(), msg.getPosition());
		// Client displays the Messagecontent
		gui.setPrimaryEntityPosition(msg.getiD(), msg.getPosition());
		// Client sends messages to the Server
		client.send(msg);
	}

	@Override
	public void run() {
		// Client processes messages from the Server
		BlockingQueue<Message> queue = client.getOutputQueue();
		try {
			while (!Thread.interrupted()) {
				// Take next message, wait if no messages are available.
				Message msg = queue.take();
				if (msg.isSuccessful()) {
					// Set Red Position
					gui.setSecondaryEntityPosition(msg.getiD(),
							msg.getPosition());
				} else {
					//Check, if this Message was sent by me, ignore it otherwise
					if(msg.getiD() == myID){
						// Message was rejected, revert to old position
						gui.revertToSecondaryPosition(msg.getiD());
					}
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
