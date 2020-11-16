/***
 * ReceptionMessageClientThread
 * This class extends Thread Class.
 * It receives every messages for the client using the UDP Protocol
 * It is launched by the class EchoClient.
 * @since 16/11/2021
 * @author Camille Peltier, Camélia Guerraoui
 * @see EchoClient
 */

package stream_udp;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ReceptionMessageClientThread extends Thread {
	
	private MulticastSocket clientSocket;
    private final String PSEUDO;
    private final InetAddress GROUP_ADDRESS;
	
    /**
     * Constructor
     * @param initialSocket client's socket 
     * @param INITIAL_PSEUDO client's pseudonym 
     * @param INITIAL_GROUP_ADDRESS client's group IP address
     */
	ReceptionMessageClientThread(MulticastSocket initialSocket, final String INITIAL_PSEUDO, final InetAddress INITIAL_GROUP_ADDRESS) {
		this.clientSocket = initialSocket;
        this.PSEUDO = INITIAL_PSEUDO;
        this.GROUP_ADDRESS = INITIAL_GROUP_ADDRESS;
	}

 	/**
    * Main Method :
    *   - receives every messages for the client from his group
    * @Exception
    */
	public void run() {
        try {
             
            String received_message = "";
    		while (!received_message.equals(this.PSEUDO+" left.")) {
                
                byte[] buf = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                
                // Receive response
                clientSocket.receive(packet);
                received_message = new String(packet.getData());
                System.out.println(received_message);

    		}
            System.out.println("FINI RECEPTION");
            clientSocket.leaveGroup(this.GROUP_ADDRESS);
            clientSocket.close();

    	} catch (Exception e) {
        	System.err.println("Error in ReceptionClientThread:" + e); 
        }
    }
  
  }
