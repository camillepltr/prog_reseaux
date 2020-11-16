/**
 * EchoServerMultiThreaded
 * A server connects with client and transfers messages
 * This class :
 * - connects the client to the server using the UDP Protocol.
 * - transfers messages from a client to the rest of his group
 * @since 16/11/2021
 * @author Camille Peltier, Camélia Guerraoui
 */

package stream_udp;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class EchoServerMultiThreaded  {
  
 	/**
  	* Main Method :
    *   - connects the client to the server using the UDP Protocol.
    *   - transfers messages from a client to the rest of his group
    * @exception
  	**/
    public static void main(String args[]){ 
        
        System.out.println("Server Launched");
        final int SERVER_PORT = 3500;
        LinkedList<DatagramPacket> history = new LinkedList<DatagramPacket>();
        
        try {
            // Group's Parameters 
            final String GROUP_NAME = "228.5.6.7";
            final InetAddress GROUP_ADDRESS = InetAddress.getByName(GROUP_NAME);
            
            MulticastSocket serverSocket = new MulticastSocket(SERVER_PORT);
            
            while (true) {
                
                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                serverSocket.receive(packet);
                System.out.println("A packet has been received.");
                
                int groupPort = packet.getPort();
                InetAddress clientAddress = packet.getAddress();
                
                String message = new String(packet.getData());
                if (message.startsWith("First Connexion")){
                    for (DatagramPacket previousPacket : history){
                        sendMessage(serverSocket, previousPacket, clientAddress, groupPort);
                    }

                } else {
                    history.add(packet);
                    sendMessage(serverSocket, packet, GROUP_ADDRESS, groupPort);
                }
                
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServerMultiThreaded:" + e);
        }
    }
    
    private static void sendMessage(MulticastSocket serverSocket, DatagramPacket packet, InetAddress address, int port){
        DatagramPacket packet_response = new DatagramPacket(packet.getData(), packet.getLength(), address, port);
        try {
            serverSocket.send(packet_response); 
        } catch (Exception e) {
            System.err.println("Error in EchoServerMultiThreaded:" + e);
        }
    }

}

  
