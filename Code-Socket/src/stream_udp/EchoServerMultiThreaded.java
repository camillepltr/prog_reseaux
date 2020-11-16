/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream_udp;

import java.io.*;
import java.net.*;

public class EchoServerMultiThreaded  {
  
 	/**
  	* main method
	* @param EchoServer port
  	* 
  	**/
    public static void main(String args[]){ 
        
        int port = 3500;
        
        try {
            MulticastSocket serverSock = new MulticastSocket(port);
            
            while (true) {
                System.out.println("Waiting for client packet... ");
                byte[] buf = new byte[1000];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                serverSock.receive(packet);
                
                InetAddress groupAddress = InetAddress.getByName("228.5.6.7");
                int groupPort = packet.getPort();
                
                // Build a datagram packet for response
                DatagramPacket packet_response = new DatagramPacket(buf, buf.length, groupAddress, groupPort);
                
                // Send a response
                serverSock.send(packet_response); 
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServerMultiThreaded:" + e);
        }
    }

  }

  
