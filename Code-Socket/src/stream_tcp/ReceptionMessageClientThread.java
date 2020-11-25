
package stream_tcp;

import java.io.*;
import java.net.*;

import javax.swing.JTextArea;

/**
 * This class extends Thread Class.
 * It receives every messages for the client using the TCP Protocol and displays them.
 * It is launched by the class ClientInterface.
 * @author Camille Peltier, Camélia Guerraoui
 * @see ClientInterface
 */
public class ReceptionMessageClientThread extends Thread {
	private final String PSEUDO;
	private Socket clientSocket;
	private JTextArea conversation;

	/**
	 * Constructor
	 * @param initialSocket the client's socket 
	 * @param initialConversation JTextArea containing every messages sent to the group
	 * @param INITIAL_PSEUDO Client's Pseudonym
	 */
	ReceptionMessageClientThread(Socket initialSocket, final String INITIAL_PSEUDO, JTextArea initialConversation) {
		this.PSEUDO = INITIAL_PSEUDO;
		this.clientSocket = initialSocket;
		this.conversation = initialConversation;
	}

 	/**
  	* Client side : receives a message and displays it.
  	* When the disconnection message is received,
  	* the socket is closed and the program is finished
  	* A IOException can be thrown. This is produced by failed or interrupted I/O operations.
  	**/
	public void run() {
        try {
    		BufferedReader clientSocketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
            String line = "";

            while (!line.equals("You are disconnected.")) {
                line = clientSocketIn.readLine();
                conversation.append(line + "\n");
    		}

    		//After disconnection, close the input stream and terminate program
            clientSocket.shutdownInput();
            clientSocket.close();
            System.exit(0);

    	} catch (Exception e) {
        	System.err.println("Error in ReceptionClientThread:" + e); 
        }
    }

  
  }
