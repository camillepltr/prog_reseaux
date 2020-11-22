
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
     * @throws RemoteException A RemoteException is the common superclass for a number of 
     * 			communication-related exceptions that may occur during the execution of a remote method call.
     * 			Each method of a remote interface, an interface that extends java.rmi.Remote, 
     * 			must list RemoteException in its throws clause.
     */
    public ArrayList<String> getHistory() throws RemoteException;
  
}
