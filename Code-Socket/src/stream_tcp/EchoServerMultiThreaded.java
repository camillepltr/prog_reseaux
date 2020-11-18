/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream_tcp;

import java.net.*;

public class EchoServerMultiThreaded  {
  
 	/**
  	* main method, server side
  	* 	- TCP protocol
  	* 	- sets a socket to listen for new clients
  	* 	- for each new client, creates a communication socket, and starts a ClientThread
	* @param EchoServerMultiThreadedPort port
	* @exception
	* @see ClientThread.java
  	* 
  	**/
   public static void main(String args[]){ 
	   ServerSocket listenSocket;
        
	  	if (args.length != 1) {
	          System.out.println("Usage: java EchoServer <EchoServer port>");
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
	            System.err.println("Error in EchoServer:" + e);
	        }
	      }
  }

  
