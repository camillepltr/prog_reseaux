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

public class ReceptionClientThread extends Thread {
	
	private Socket clientSocket;
	
    /**
     * Constructor
     * @param s the client's socket 
     */
	ReceptionClientThread(Socket s) {
		this.clientSocket = s;
	}

 	/**
  	* Client side : 
  	* receives a message and displays it
  	* @exception 
  	**/
	public void run() {
        try {
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
            String line = "";
    		while (!line.equals("You are disconnected")) {
                line = socIn.readLine();
                System.out.println(line);
    		}
    		//After disconnection, close the streams
            socIn.close();
            clientSocket.close();
    	} catch (Exception e) {
        	System.err.println("Error in ReceptionClientThread:" + e); 
        }
       }
  
  }
