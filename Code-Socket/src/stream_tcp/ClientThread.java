
package stream_tcp;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

/**
 * A ClientThread is created for each client who is connected to a server.
 * 
 * It handles every messages of this specific client i.e. this class receives 
 * the message sent by the client and transmits this message to other connected
 * clients. The lists of connected clients are stored in the LinkedList socketOuts.
 * 
 * When a client wants to leave, he sends a message containing his pseudo 
 * and the mention " left.". The class ClientThread removes this client from the 
 * lists of connected clients and sends a disconnection message to the client.
 * 
 * @author Camille Peltier, Camélia Guerraoui
 * @see EchoServerMultiThreaded
 */
public class ClientThread extends Thread {
	
	private Socket clientSocket;
    private static LinkedList<PrintStream> socketOuts = new LinkedList<PrintStream>();

    /**
     * Constructor
     * Initializes the client's socket.
     * @param initialSocket the client's socket 
     */
	public ClientThread(Socket initialSocket) {
		this.clientSocket = initialSocket;
	}
    
 	/**
 	* Receives a message from a client then sends it to all the clients through the output
  	* streams in the list socketsOut
  	* A IOException can be thrown. This is produced by failed or interrupted I/O operations.
    */
	public void run() {
    	  try {
    		  BufferedReader socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
    		  PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
    		  ClientThread.socketOuts.add(socOut);
    		  String message = "";
    		  while (!message.equals("quit")) {
    			  message = socIn.readLine();
    			  if(message.equals("quit")){
    				  this.quitConversation(socOut);
    			  } else {
    				  this.sendMessage(message);
    			  }
    		  }
    		  socOut.close();
    	  } catch (Exception e) {
    		  System.err.println("Error in ClientThread:" + e); 
    	  }
	}
	
    /**
     * Removes the client's stream from the list of output streams of the server and
     * Sends to the client a disconnection message.
     * @param socOut PrintStream connected to the socket's output  
     * */
    private void quitConversation(PrintStream socOut){
        ClientThread.socketOuts.remove(socOut);
        socOut.println("You are disconnected.");
    }
    
    /**
     * Sends a received message to every connected clients
     * @param message Message to send
     * */
    private void sendMessage(String message){
    	System.out.println("Message client "+clientSocket.getInetAddress()+" : "+message);
        for (PrintStream socClient : ClientThread.socketOuts){
            socClient.println(message);
        }
    }
}

  
