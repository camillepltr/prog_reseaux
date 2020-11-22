/***
 * ReceptionMessageClientThread
 * This class extends Thread Class.
 * It receives every messages for the client using the UDP Protocol
 * It is launched by the class EchoClient.
 * @author Camille Peltier, Camélia Guerraoui
 * @see EchoClient
 */

package stream_udp;

import java.net.*;

import javax.swing.JTextArea;

public class ReceptionMessageClientThread extends Thread {
	
	private MulticastSocket clientSocket;
    private final String PSEUDO;
    private JTextArea conversation;
    
    /**
     * Constructor
     * @param initialSocket client's socket 
     * @param INITIAL_PSEUDO client's pseudonym 
     * @param INITIAL_GROUP_ADDRESS client's group IP address
     */
	ReceptionMessageClientThread(MulticastSocket initialSocket, final String INITIAL_PSEUDO, JTextArea conv) {
		this.clientSocket = initialSocket;
        this.PSEUDO = INITIAL_PSEUDO;
        this.conversation = conv;
	}

 	/**
    * Main Method :
    *   - receives every messages for the client from his group
    * @exception
    */
	public void run() {
        try {
            String receivedMessage = "";
    		while (!receivedMessage.startsWith(this.PSEUDO+" left.")) {
                
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                
                clientSocket.receive(packet);
                receivedMessage = new String(packet.getData(), packet.getOffset(), packet.getLength());

                conversation.append(receivedMessage + "\n");

    		}
    		System.exit(0);

    	} catch (Exception e) {
        	System.err.println("Error in ReceptionClientThread:" + e); 
        }
    }
  
  }
