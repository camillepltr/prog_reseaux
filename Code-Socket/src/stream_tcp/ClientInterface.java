
package stream_tcp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A client connects to a specific server and then chats with other people.
 * This class :
 * - creates a User Interface by extending JFrame.
 * - connects the client to the server using the UDP Protocol.
 * - launches a parallel reception thread.
 * - emits every messages to the server.
 * @author Camille Peltier, Camélia Guerraoui
 * @see ReceptionMessageClientThread
 * @see ClientApplication
 */
public class ClientInterface  extends JFrame implements ActionListener{
	private Socket clientSocket;
	private PrintStream clientSocketOut;
    private final String PSEUDO;

    private JTextArea conversation = new JTextArea(100, 50);
    private JTextArea msgToSend = new JTextArea(100, 50);
    private JButton send = new JButton("Send");
    private JButton disconnect = new JButton("Disconnect");
    
    /**
     * Constructor
     * Creates a User Interface for the client to chat
     * Initialize a socket to communicate with the server
     * @param serverAddress Server Address
     * @param serverPort Server Port
     * @param pseudo Client's pseudo
     * @throws UnknownHostException Thrown to indicate that the IP address of a host could not be determined.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     * 			This class is the general class of exceptions produced by failed or interrupted I/O operations.
     */
    public ClientInterface(String serverAddress, int serverPort, String pseudo) throws UnknownHostException, IOException {
    	super("Chat TCP - Client side - " + pseudo);
    	
    	this.clientSocket = new Socket(serverAddress, serverPort);
		this.clientSocketOut= new PrintStream(clientSocket.getOutputStream());
    	this.PSEUDO = pseudo;
        
        this.initializeJFrameWindow();
        this.initializeJTextAreaConversion();
        this.initializeJTextAreaMsgToSend();
        this.initializeButtons();
        setVisible(true);
	}
    
    /**
     * Launches a parallel thread to receive the client's messages
     */
    public void launchParallelThread() {
    	clientSocketOut.println(PSEUDO+" is connected.");
        ReceptionMessageClientThread receptionThread = new ReceptionMessageClientThread(clientSocket, PSEUDO, conversation);
        receptionThread.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send) {
            sendMessage();
        } else if (e.getSource() == disconnect) {
        	disconnect();
        }
    }

    /**
     * Sends the message in msgToSend (JTextArea) to the server
     */
	private void sendMessage() {
		String message=msgToSend.getText();
		clientSocketOut.println(PSEUDO+" : " + message);
		
        msgToSend.setText("");
        msgToSend.setCaretPosition(0);
	}
	
	/**
     * Sends a message of disconnection to the server
     */
	private void disconnect() {
		clientSocketOut.println(this.PSEUDO+" left.");
		clientSocketOut.println("quit");
		//After disconnection, close the output stream
		try {
			clientSocket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeJFrameWindow() {
		setBounds(0, 0, 400, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
	}
	
	private void initializeJTextAreaConversion() {
		conversation.setEditable(false);
        conversation.setBackground(Color.LIGHT_GRAY);
        conversation.setForeground(Color.BLUE);
        conversation.setText("");
 
        conversation.setWrapStyleWord(true);
        conversation.setLineWrap(true);
 
        JScrollPane pane = new JScrollPane(conversation);
        pane.setBounds(2, 2, 396, 400);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane);
	}
	
	private void initializeJTextAreaMsgToSend() {
		msgToSend.setBackground(Color.WHITE);
        msgToSend.setForeground(Color.BLACK);
        msgToSend.setLineWrap(true);
        msgToSend.setWrapStyleWord(true);
        msgToSend.setText("Write a message here");
 
        JScrollPane pane = new JScrollPane(msgToSend);
        pane.setBounds(2, 404, 396, 60);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane);
	}
	
	private void initializeButtons() {
		send.setBounds(2, 466, 194, 40);
        add(send);
        send.addActionListener(this);
        disconnect.setBounds(200, 466, 194, 40);
        add(disconnect);
        disconnect.addActionListener(this);
	}

}


