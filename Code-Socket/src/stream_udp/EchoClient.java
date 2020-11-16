/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream_udp;

import java.io.*;
import java.net.*;



public class EchoClient {

 
  /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {
        BufferedReader stdIn = null;
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        int serverPort = 3500; 
        String serverHost = "Macbook-Cami-3";
        InetAddress serverAddress = InetAddress.getByName(serverHost);
        
        // Create a datagram socket
        InetAddress groupAddress = InetAddress.getByName("228.5.6.7");
        MulticastSocket clientSocket = new MulticastSocket(serverPort);
        clientSocket.joinGroup(groupAddress);
        
        System.out.println("What's your pseudo?");
        String pseudo = stdIn.readLine();
        String line = "";
        
        ReceptionMessageClientThread reception = new ReceptionMessageClientThread(clientSocket, pseudo, groupAddress);
        reception.start();
        
        while(!line.equals(pseudo+" left.")){

            // Build a request
            line = pseudo+" : ";
            line += stdIn.readLine();
            if (line.equals(pseudo+" : .")){
                line = pseudo+" left.";
            }
            
            byte[] buf = new byte[1000];
            buf = line.getBytes();
            
            // Create a datagram packet destined for the server
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, serverPort);
           
            // Send datagram packet to server
            clientSocket.send(packet);
        }

    }
}


