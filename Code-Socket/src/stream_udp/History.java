/***
 * History
 * Remote object to save the message history
 * @since 16/11/2021
 * @author Camille Peltier, Camélia Guerraoui
 * @see HistoryInterface
 */

package stream_udp;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.LinkedList;

public class History implements HistoryInterface {
    
    private LinkedList<String> history;
    
    /**
     * Default constructor
     * Initialise an empty list of messages (String)
     */
    public History(){
        this.history = new LinkedList<String>();
    }
	
    /**
     * Remote method to get the message history
     * @return the list of messages
     */
    public LinkedList<String> getHistory() throws RemoteException {
        return this.history;
    }
    
    /**
     * Add a message to the message history
     * @param a new packet received by the server
     */
    public void addMessageToHistory(DatagramPacket newPacket) {
        String newMessage = new String(newPacket.getData(), newPacket.getOffset(), newPacket.getLength());
        this.history.add(newMessage);
    }
  
}
