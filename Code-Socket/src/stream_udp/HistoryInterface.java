/***
 * HistoryInterface
 * Remote interface for a History object
 * @since 16/11/2021
 * @author Camille Peltier, Camélia Guerraoui
 * @see History
 */

package stream_udp;

import java.io.*;
import java.rmi.*;
import java.util.LinkedList;

public interface HistoryInterface extends Remote {
	
    /** 
     * Method provided by the remote object to get the message history
     * @return a list of saved messages
     */
    public LinkedList<String> getHistory() throws RemoteException;
  
}
