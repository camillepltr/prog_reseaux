/***
 * History
 * Remote object to save the message history
 * @since 16/11/2021
 * @author Camille Peltier, Camélia Guerraoui
 * @see HistoryInterface
 */

package stream_udp;

import java.nio.file.*;
import java.nio.charset.Charset;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

public class History implements HistoryInterface {
    
    private ArrayList<String> history;
    private final String saveFileName;
    
    /**
     * Default constructor
     * Initialise an empty list of messages (String)
     */
    public History(String fileName){
        this.history = new ArrayList<String>();
        this.saveFileName = fileName;
        this.loadHistory();
    }
	
    /**
     * Remote method to get the message history
     * @return the list of messages
     */
    public ArrayList<String> getHistory() throws RemoteException {
        return this.history;
    }
    
    /**
     * Add a message to the message history
     * @param a new packet received by the server
     */
    public void addMessageToHistory(DatagramPacket newPacket) {
        String newMessage = new String(newPacket.getData(), newPacket.getOffset(), newPacket.getLength());
        this.history.add(newMessage);
        
        try {
            Path path = Paths.get(this.saveFileName);
            newMessage += "\n";
            Files.write(path, newMessage.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Error in History while adding a message :" + e);
        }
    }
    
    private void loadHistory(){
        
        Path path = Paths.get(this.saveFileName);
        
        try {
            if (!Files.exists(path)){
                Files.createFile(path);
            }
            this.history = (ArrayList<String>) Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (Exception e) {
            System.err.println("Error in History while loading history :" + e);
        }
    }
  
}
