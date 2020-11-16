/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ClientThread extends Thread {
	
	private Socket clientSocket;
    private static LinkedList<PrintStream> socketOuts = new LinkedList<PrintStream>();
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}
    
    /**
     * 
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
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
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

  
