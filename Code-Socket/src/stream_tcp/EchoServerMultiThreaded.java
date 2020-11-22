
package stream_tcp;

import java.net.*;

/**
 * This class represents a server.
 * It has to be launched with 1 parameter which is the server name 
 * This class :
 * - receives a connection from a client using the TCP Protocol and accepts it.
 * - transfers messages from a client to the other clients.
 * A ClientThread is created for every client who is connected to the server.
 * This thread handles every messages of this specific client.
 * This Thread is closed when a disconnection message is shown.
 * @author Camille Peltier, Camélia Guerraoui
 * @see ClientThread
 */
public class EchoServerMultiThreaded  {
  
 	/**
  	* Main method of the server. It :
  	* 	- uses TCP protocol.
  	* 	- sets a socket to listen for new clients
  	* 	- creates for each new client a communication socket and starts a ClientThread
	* @exception
	* @see ClientThread
  	**/
   public static void main(String args[]){ 
	   ServerSocket listenSocket;
        
	   if (args.length != 1) {
		   System.out.println("Usage: java EchoServerMultiThreaded <Server port>");
		   System.exit(1);
	   }
	   try {
		   listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
		   System.out.println("Server ready..."); 
		   while (true) {
			   Socket clientSocket = listenSocket.accept();
			   System.out.println("Connexion from:" + clientSocket.getInetAddress());
			   ClientThread ct = new ClientThread(clientSocket);
			   ct.start();
		   }
	   } catch (Exception e) {
		   System.err.println("Error in EchoServerMultiThreaded:" + e);
	   }
	}
}

  
