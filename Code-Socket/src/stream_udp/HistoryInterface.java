
package stream_udp;

import java.rmi.*;
import java.util.ArrayList;

/***
 * Remote interface for a History object
 * @author Camille Peltier, Camélia Guerraoui
 * @see History
 */
public interface HistoryInterface extends Remote {
	
    /** 
     * Method provided by the remote object to get the message history
     * @return a list of saved messages
     */
    public ArrayList<String> getHistory() throws RemoteException;
  
}
