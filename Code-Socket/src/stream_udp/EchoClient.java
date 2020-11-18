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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.net.*;
import java.rmi.registry.*;
import java.util.ArrayList;

public class EchoClient extends JFrame implements ActionListener{
	private MulticastSocket clientSocket = null;
	private InetAddress serverAddress;
	private int serverPort;

    private JTextArea conversation = new JTextArea(100, 50);
    private JTextArea msgToSend = new JTextArea(100, 50);
    private JButton send = new JButton("Send");
    private JButton disconnect = new JButton("Disconnect");
    private JScrollPane pane2, pane1;
    
    public EchoClient(MulticastSocket cs, InetAddress addr, int port, String p) {
    	super("Chat UDP - Client side - " + p);
    	this.clientSocket = cs;
    	this.serverAddress = addr;
    	this.serverPort = port;
		
        setBounds(0, 0, 400, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        
        conversation.setEditable(false);
        conversation.setBackground(Color.LIGHT_GRAY);
        conversation.setForeground(Color.BLUE);
        conversation.setText("");
 
        conversation.setWrapStyleWord(true);
        conversation.setLineWrap(true);
 
        pane2 = new JScrollPane(conversation);
        pane2.setBounds(2, 2, 396, 400);
        pane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane2);
 
        msgToSend.setBackground(Color.WHITE);
        msgToSend.setForeground(Color.BLACK);
        msgToSend.setLineWrap(true);
        msgToSend.setWrapStyleWord(true);
        msgToSend.setText("Write Message here");
 
        pane1 = new JScrollPane(msgToSend);
        pane1.setBounds(2, 404, 396, 60);
        pane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane1);
 
        send.setBounds(2, 466, 194, 40);
        add(send);
        send.addActionListener(this);
        disconnect.setBounds(200, 466, 194, 40);
        add(disconnect);
        disconnect.addActionListener(this);
        
        setVisible(true);
	}
 
    /**
    * Main Method :
    *   - connects the client to the server using the UDP Protocol.
    *   - launchs a parallel reception thread
    *   - emits every messages to the server.
    * @exception
    * @see ReceptionMessageClientThread
    */
    public static void main(String[] args) throws IOException {
        // Server's Parameters 
        final int SERVER_PORT = 3500; 
        //final String SERVER_NAME = "Macbook-Cami-3";
        final String SERVER_NAME = "camille-UX310UA";
        final InetAddress SERVER_ADDRESS = InetAddress.getByName(SERVER_NAME);
        
        // Group's Parameters 
        final String GROUP_NAME = "228.5.6.7";
        final InetAddress GROUP_ADDRESS = InetAddress.getByName(GROUP_NAME);
        
        MulticastSocket clientSocket = new MulticastSocket(SERVER_PORT);
        clientSocket.joinGroup(GROUP_ADDRESS);
        
        /*
        BufferedReader stdIn = null;
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("What's your pseudo?");
        final String PSEUDO = stdIn.readLine();
        */
        final String PSEUDO = JOptionPane.showInputDialog( "What's your pseudo?");
        EchoClient interfaceClient = new EchoClient(clientSocket, SERVER_ADDRESS, SERVER_PORT, PSEUDO);
        
        
        String firstMessageToServer = PSEUDO+" is connected";
        interfaceClient.sendMessage(firstMessageToServer);
        interfaceClient.printHistory();
        
        // Launch reception thread
        ReceptionMessageClientThread reception = new ReceptionMessageClientThread(clientSocket, PSEUDO, GROUP_ADDRESS, interfaceClient.conversation);
        reception.start();
        
        /*
        String emittedMessage = "";
        while(!emittedMessage.equals(PSEUDO+" left.")){

            // Build a message to emit
            emittedMessage = PSEUDO + " : " + stdIn.readLine();
            if (emittedMessage.equals(PSEUDO+" : .")){
                emittedMessage = PSEUDO+" left.";
            }
            
            sendMessage(clientSocket, emittedMessage, SERVER_ADDRESS, SERVER_PORT);
        }
        */
    }
    
    
    private void printHistory(){
        try {
            Registry registry = LocateRegistry.getRegistry(this.serverPort);
            HistoryInterface h = (HistoryInterface) registry.lookup("History");
            ArrayList<String> history = h.getHistory();
            
            for (String previousMessage : history){
                //System.out.println(previousMessage);
            	conversation.append(previousMessage + "\n");
            }
            
        } catch (Exception e) {
            System.err.println("Error in printHistory EchoClient:" + e);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send) {
            sendMessage();
        } else if (e.getSource() == disconnect) {
        	sendMessage(".");
        }
    }
	
    /**
     * Sends the message in text area msgToSend to the server
     */
	public void sendMessage() {
		String message=msgToSend.getText();
		byte[] buf = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, this.serverAddress, this.serverPort);
        try {
            clientSocket.send(packet);
        } catch (Exception e) {
            System.err.println("Error in sendMessage - EchoClient:" + e);
        }
		
        msgToSend.setText("");
        msgToSend.setCaretPosition(0);
	}
	
	/**
	 * Sends the message in parameter to the server
	 * @param message
	 */
	public void sendMessage(String message) {
		byte[] buf = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, this.serverAddress, this.serverPort);
        try {
            clientSocket.send(packet);
        } catch (Exception e) {
            System.err.println("Error in sendMessage - EchoClient:" + e);
        }
	}
	

    
}


