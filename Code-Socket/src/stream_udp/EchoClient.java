/**
 * EchoClient
 * A client connects to a specific server and then chats with other people.
 * This class :
 * - connects the client to the server using the UDP Protocol.
 * - launchs a parallel reception thread
 * - emits every messages to the server.
 * @since 16/11/2021
 * @author Camille Peltier, Camélia Guerraoui
 * @see ReceptionMessageClientThread
 */
package stream_udp;

import java.io.*;
import java.net.*;


public class EchoClient {

 
    /**
    * Main Method :
    *   - connects the client to the server using the UDP Protocol.
    *   - launchs a parallel reception thread
    *   - emits every messages to the server.
    * @Exception
    * @See ReceptionMessageClientThread
    */
    public static void main(String[] args) throws IOException {
        // Server's Parameters 
        final int SERVER_PORT = 3500; 
        final String SERVER_NAME = "Macbook-Cami-3";
        final InetAddress SERVER_ADDRESS = InetAddress.getByName(SERVER_NAME);
        
        // Group's Parameters 
        final String GROUP_NAME = "228.5.6.7";
        final InetAddress GROUP_ADDRESS = InetAddress.getByName(GROUP_NAME);
        
        MulticastSocket clientSocket = new MulticastSocket(SERVER_PORT);
        clientSocket.joinGroup(GROUP_ADDRESS);
        
        BufferedReader stdIn = null;
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("What's your pseudo?");
        final String PSEUDO = stdIn.readLine();
        String emitted_message = "";
        
        // Launch reception thread
        ReceptionMessageClientThread reception = new ReceptionMessageClientThread(clientSocket, PSEUDO, GROUP_ADDRESS);
        reception.start();
        
        while(!emitted_message.equals(PSEUDO+" left.")){

            // Build a message to emit
            emitted_message = PSEUDO + " : " + stdIn.readLine();
            if (emitted_message.equals(PSEUDO+" : .")){
                emitted_message = PSEUDO+" left.";
            }
            
            byte[] buf = new byte[1000];
            buf = emitted_message.getBytes();
            
            // Send a datagram packet destined for the server
            DatagramPacket packet = new DatagramPacket(buf, buf.length, SERVER_ADDRESS, SERVER_PORT);
            clientSocket.send(packet);
        }

    }
}

