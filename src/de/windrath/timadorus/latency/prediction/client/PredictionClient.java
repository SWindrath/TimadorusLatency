package de.windrath.timadorus.latency.prediction.client;

import java.io.IOException;

import javax.swing.JOptionPane;

import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.Config;
import de.windrath.timadorus.latency.util.network.DelayedClientSocket;

public class PredictionClient {
    
    public static void main(String[] args) {
        try {
            //Set the ID of this Client, it should be manually changed for each client.
            int myID = Integer.parseInt( JOptionPane.showInputDialog("Enter Client ID", "1"));
            
            //Socket
            //Initialize and start a client socket
            DelayedClientSocket client = new DelayedClientSocket(Config.SERVER_ADDRESS, Config.SERVER_PORT, Config.DELAY, Config.DEVIATION);
            Thread clientThread = new Thread(client);
            clientThread.start();
            
            //Logic
            //Initialize the logic
            PredictionClientLogic logic = new PredictionClientLogic(client, myID);
            Thread logicThread = new Thread(logic);
            logicThread.start();
            
            //GUI
            //Initialize the GUI
            CoordGui gui = new CoordGui("Client" + myID, myID, logic);
            logic.setGui(gui);
            
            //Start GUI
            gui.setVisible(true);

                 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
