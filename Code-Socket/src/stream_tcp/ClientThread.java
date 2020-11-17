/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream_tcp;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ClientThread extends Thread {
	
	private Socket clientSocket;
    private static LinkedList<PrintStream> socketOuts = new LinkedList<PrintStream>();

	
    /**
     * Constructor
     * @param s the client's socket 
     */
	ClientThread(Socket s) {
		this.clientSocket = s;
	}
    
    /**
     * when the client wants to quit, removes the client's stream from the list of output streams of the server
     * @exception
     * */
    private void quitConversation(){
        try {
            PrintStream socOut = new PrintStream(this.clientSocket.getOutputStream());
            ClientThread.socketOuts.remove(socOut);
            socOut.println("You are disconnected");
        } catch (Exception e) {
        	System.err.println("Error while disconnecting:" + e); 
        }
    }

 	/**
 	* Server side:
  	* receives a message from a client then sends it to all the clients (through the output
  	* streams in the list socketsOut)
  	**/
	public void run() {
    	  try {
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            ClientThread.socketOuts.add(socOut);
            String line = "";
    		while (!line.equals(".")) {
                line = socIn.readLine();
                if(line.equals(".")){
                    this.quitConversation();
                } else {
                    System.out.println("Message client "+clientSocket.getInetAddress()+" : "+line);
                    for (PrintStream socClient : ClientThread.socketOuts){
                        socClient.println(line);
                    }
                }
    		}
            socOut.close();
    	} catch (Exception e) {
        	System.err.println("Error in ClientThread:" + e); 
        }
       }
  
  }

  
