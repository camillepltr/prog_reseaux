
package stream_udp;

import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JOptionPane;

/**
 * A client connects to a specific server and then chats with other people.
 * This class creates an object ClientInterface which allows the client to :
 * - connects the client to the server using the UDP Protocol.
 * - launches a parallel reception thread.
 * - emits every messages to the server.
 * @author Camille Peltier, Camélia Guerraoui
 * @see ReceptionMessageClientThread
 * @see ClientInterface
 */
public class ClientApplication {
	
    /**
    * Initializes the server port, the server name, the group name and the client's pseudo
    * Creates a User Interface for the client to chat
    * @throws IOException 
    * @see ClientInterface
    */
    public static void main(String[] args) throws IOException {
        // Server's Parameters 
        final int SERVER_PORT = 3500; 
        final String SERVER_NAME = "localhost";
        final InetAddress SERVER_ADDRESS = InetAddress.getByName(SERVER_NAME);
        
        // Group's Parameters 
        final String GROUP_NAME = "228.5.6.7";
        final InetAddress GROUP_ADDRESS = InetAddress.getByName(GROUP_NAME);
        
        //Client's Parameters
        String pseudo = JOptionPane.showInputDialog( "What's your pseudo?");
        if (pseudo == null || pseudo.isEmpty()) {
        	pseudo = "Anonymous Person";
        }
        
        ClientInterface clientInterface = new ClientInterface(SERVER_ADDRESS, GROUP_ADDRESS, SERVER_PORT, pseudo);
        clientInterface.connectUser();

    }

}
