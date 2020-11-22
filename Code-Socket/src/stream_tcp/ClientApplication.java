
package stream_tcp;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

/**
 * A client connects to a specific server and then chats with other people.
 * This class creates an object ClientInterface which allows the client to :
 * - connects the client to the server using the TCP Protocol.
 * - launches a parallel reception thread
 * - emits every messages to the server.
 * @author Camille Peltier, Camélia Guerraoui
 * @see ReceptionMessageClientThread
 * @see ClientInterface
 */
public class ClientApplication {

	/**
    * This class has to be launched with 2 parameters : first the server name 
    * and then the server port. It : 
    *  - Initializes the server port, the server name and the client's pseudo.
    *  - Creates a User Interface for the client to chat.
    * @param args list of two Strings containing first the server name and the
    * 		server port
	* @throws IOException Signals that an I/O exception of some sort has occurred.
	* 			This class is the general class of exceptions produced by failed or interrupted I/O operations.
	* @throws UnknownHostException Thrown to indicate that the IP address of a host could not be determined.
	* @see ClientInterface
	* @see ReceptionMessageClientThread
  	**/
    public static void main(String[] args) throws UnknownHostException, IOException{

        if (args.length != 2) {
          System.out.println("Usage: java ClientInterface <EchoServerMultiThreaded host> <EchoServerMultiThreaded port>");
          System.exit(1);
        }

        final String SERVER_ADDRESS = args[0];
        final int SERVER_PORT = new Integer(args[1]).intValue();
        String pseudo = JOptionPane.showInputDialog( "What's your pseudo?");
        
        if(pseudo == null || pseudo.isEmpty()) {
        	pseudo = "Anonymous Person";
        }

        ClientInterface clientInterface = new ClientInterface(SERVER_ADDRESS, SERVER_PORT, pseudo);
        clientInterface.launchParallelThread();

    }

}
