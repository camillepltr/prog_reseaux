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

import java.net.*;

import javax.swing.JTextArea;

public class ReceptionMessageClientThread extends Thread {
	
	private MulticastSocket clientSocket;
    private final String PSEUDO;
    private final InetAddress GROUP_ADDRESS;
    private JTextArea conversation;
    
    /**
     * Constructor
     * @param initialSocket client's socket 
     * @param INITIAL_PSEUDO client's pseudonym 
     * @param INITIAL_GROUP_ADDRESS client's group IP address
     */
	ReceptionMessageClientThread(MulticastSocket initialSocket, final String INITIAL_PSEUDO, final InetAddress INITIAL_GROUP_ADDRESS, JTextArea conv) {
		this.clientSocket = initialSocket;
        this.PSEUDO = INITIAL_PSEUDO;
        this.GROUP_ADDRESS = INITIAL_GROUP_ADDRESS;
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
                
                // Receive response
                clientSocket.receive(packet);
                receivedMessage = new String(packet.getData(), packet.getOffset(), packet.getLength());

                //System.out.println(receivedMessage);
                conversation.append(receivedMessage + "\n");

    		}
            //clientSocket.leaveGroup(this.GROUP_ADDRESS);
            //clientSocket.close();
    		System.exit(0);

    	} catch (Exception e) {
        	System.err.println("Error in ReceptionClientThread:" + e); 
        }
    }
  
  }
