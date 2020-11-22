/**
 * EchoServerMultiThreaded
 * A server connects with client and transfers messages
 * This class :
 * - connects the client to the server using the UDP Protocol.
 * - transfers messages from a client to the rest of his group
 * @author Camille Peltier, Camélia Guerraoui
 */

package stream_udp;

import java.io.IOException;
import java.net.*;
import java.rmi.server.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;

public class EchoServerMultiThreaded  {
  
 	/**
  	* Main Method :
    *   - connects the client to the server using the UDP Protocol.
    *   - transmits messages from a client to the rest of his group
    * @exception
  	**/
    public static void main(String args[]){ 
        
        System.out.println("Server Launched");

        //Parameters
        final int SERVER_PORT = 3500;
        final String SAVED_FILE_NAME = "src/stream_udp/savedHistory.txt";
        final String GROUP_NAME = "228.5.6.7";

        try {
            final InetAddress GROUP_ADDRESS = InetAddress.getByName(GROUP_NAME);
            MulticastSocket serverSocket = new MulticastSocket(SERVER_PORT);
            
            History h = createHistory(SAVED_FILE_NAME, SERVER_PORT);
            transmitMessages(h, serverSocket, GROUP_ADDRESS);
            
        } catch (Exception e) {
            System.err.println("Error in EchoServerMultiThreaded:" + e);
        }
    }
    
    /**
     * Creates a message history
     * @param SAVED_FILE_NAME path to the file used to store the messages
     * @param SERVER_PORT server's port number
     * @return the remote object History containing every saved messages
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    private static History createHistory(final String SAVED_FILE_NAME,
    									 final int SERVER_PORT) throws RemoteException, AlreadyBoundException {
    	History h = new History(SAVED_FILE_NAME);
        HistoryInterface h_stub = (HistoryInterface) UnicastRemoteObject.exportObject(h, 0);
        Registry registry = LocateRegistry.createRegistry(SERVER_PORT);
        registry.bind("History", h_stub);
        return h;
    }
    
    /**
     * Transmits messages from a client to the rest of the group
     * and adds these messages to the history
     * @param history remote object containing the history
     * @param serverSocket Server's MulticastSocket 
     * @param GROUP_ADDRESS IP address of the group
     * @throws IOException
     */
    private static void transmitMessages (History history, MulticastSocket serverSocket,
    									  InetAddress GROUP_ADDRESS) throws IOException {
    	while (true) {
            byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            serverSocket.receive(packet);
            System.out.println("A packet has been received.");

            history.addMessageToHistory(packet);
            
            int groupPort = packet.getPort();
            DatagramPacket responsePacket = new DatagramPacket(packet.getData(), packet.getLength(), GROUP_ADDRESS, groupPort);
            serverSocket.send(responsePacket); 
;
        }
    }

}

  
