package de.windrath.timadorus.latency.simpel.server;

import de.windrath.timadorus.latency.util.gui.CoordGui;
import de.windrath.timadorus.latency.util.network.Config;
import de.windrath.timadorus.latency.util.network.DelayedServerSocket;

public class SimpleServer {

    public static void main(String[] args) {
        //Socket
        //Initialize and start a server socket
        DelayedServerSocket server = new DelayedServerSocket(Config.SERVER_PORT, Config.DELAY, Config.DEVIATION);
        Thread serverThread = new Thread(server);
        serverThread.start();
        
        //GUI
        //This GUI is for observation only and has no own Entity
        CoordGui gui = new CoordGui("Server", 0, null);
        
        //Logic
        SimpleServerLogic logic = new SimpleServerLogic(server.getInputQueue(), server.getOutputQueue(), gui);
        Thread logicThread = new Thread(logic);
        logicThread.start();
        
        //Start GUI
        gui.setVisible(true);
    }
}
