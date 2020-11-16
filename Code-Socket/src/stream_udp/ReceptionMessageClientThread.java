/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream_udp;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ReceptionMessageClientThread extends Thread {
	
	private MulticastSocket clientSocket;
    private String pseudo;
    private InetAddress groupAddress;
	
	ReceptionMessageClientThread(MulticastSocket socketInit, String pseudoInit, InetAddress groupAddressInit) {
		this.clientSocket = socketInit;
        this.pseudo = pseudoInit;
        this.groupAddress = groupAddressInit;
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
        try {
             
            String received = "";
    		while (!received.equals(pseudo+" left.")) {
                byte[] buf_response = new byte[1000];
            
                // Build a datagram packet for response
                DatagramPacket packet_response = new DatagramPacket(buf_response, buf_response.length);
                
                // Receive response
                clientSocket.receive(packet_response);
                received = new String(packet_response.getData());
                System.out.println(received);

    		}
            System.out.println("FINI RECEPTION");
            clientSocket.leaveGroup(groupAddress);
            clientSocket.close();

    	} catch (Exception e) {
        	System.err.println("Error in ReceptionClientThread:" + e); 
        }
       }
  
  }
