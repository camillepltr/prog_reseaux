/**
 * ClientInterface
 * A client connects to a specific server and then chats with other people.
 * This class :
 * - connects the client to the server using the UDP Protocol.
 * - launches a parallel reception thread
 * - emits every messages to the server.
 * @author Camille Peltier, Camélia Guerraoui
 * @see ReceptionMessageClientThread
 * @see ClientApplication
 */
package stream_udp;

import java.io.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.net.*;
import java.rmi.registry.*;
import java.util.ArrayList;

public class ClientInterface extends JFrame implements ActionListener, MouseListener, KeyListener{
	private MulticastSocket clientSocket = null;
	private final InetAddress SERVER_ADDRESS;
	private final int SERVER_PORT;
	private final String PSEUDO;

    private JTextArea conversation = new JTextArea(100, 50);
    private JTextArea msgToSend = new JTextArea(100, 50);
    private JButton sendButton = new JButton("Send");
    private JButton disconnectButton = new JButton("Disconnect");
    private final String INITIAL_MESSAGE = "Write your message here";
    
    public ClientInterface(final InetAddress serverAddress,
    				  final InetAddress groupAddress,
    				  final int serverPort,
    				  final String pseudo) throws IOException {

    	super("Chat UDP - Client side - " + pseudo);
    	
    	this.SERVER_ADDRESS = serverAddress;
    	this.SERVER_PORT = serverPort;
    	this.PSEUDO = pseudo;

    	this.clientSocket = new MulticastSocket(SERVER_PORT);
        clientSocket.joinGroup(groupAddress);
		
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = 400;
        int windowHeight= 540;
        setBounds((screenSize.width-windowWidth)/2, (screenSize.height-windowHeight)/2, windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        
        this.initializeConversationArea();
        this.initializeMsgToSendArea();
        this.initializeButtons();

        setVisible(true);
	}
    
    private void initializeConversationArea() {
        conversation.setEditable(false);
        conversation.setBackground(new Color(153,204,255));
        conversation.setForeground(Color.BLACK);
        conversation.setText("");
 
        conversation.setWrapStyleWord(true);
        conversation.setLineWrap(true);
 
        JScrollPane pane = new JScrollPane(conversation);
        pane.setBounds(2, 2, 396, 400);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(pane);
    }
    
    private void initializeMsgToSendArea() {
    	msgToSend.setBackground(new Color(224,224,224));
        msgToSend.setForeground(Color.BLACK);
        msgToSend.setLineWrap(true);
        msgToSend.setWrapStyleWord(true);
        msgToSend.setText(INITIAL_MESSAGE);
        msgToSend.addMouseListener(this);
        msgToSend.addKeyListener(this);
 
        JScrollPane pane = new JScrollPane(msgToSend);
        pane.setBounds(2, 404, 396, 60);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane);
    }
    
    private void initializeButtons() {
    	sendButton.setBounds(2, 466, 194, 40);
        add(sendButton);
        sendButton.addActionListener(this);

        disconnectButton.setBounds(200, 466, 194, 40);
        add(disconnectButton);
        disconnectButton.addActionListener(this);
    }
    
    /**
     * Launches a parallel thread to receive the client's messages
     */
    private void launchReceptionThread() {
        ReceptionMessageClientThread reception = new ReceptionMessageClientThread(clientSocket, PSEUDO, conversation);
        reception.start();
    }
    
    /**
     * Get the remote history saved in the server by using a registry 
     * and displays every messages of this history
     */
    private void printHistory(){
        try {
            Registry registry = LocateRegistry.getRegistry(this.SERVER_PORT);
            HistoryInterface h = (HistoryInterface) registry.lookup("History");
            ArrayList<String> history = h.getHistory();
            
            for (String previousMessage : history){
            	conversation.append(previousMessage + "\n");
            }
            
        } catch (Exception e) {
            System.err.println("Error in printHistory EchoClient:" + e);
        }
    }
	
	/**
	 * Sends the message in parameter to the server
	 * @param message a message to send
	 */
	private void sendMessage(String message) {
		byte[] buf = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, this.SERVER_ADDRESS, this.SERVER_PORT);
        try {
            clientSocket.send(packet);
        } catch (Exception e) {
            System.err.println("Error in sendMessage - EchoClient:" + e);
        }
	}
	
    /**
     * Sends the message in text area msgToSend to the server
     */
	private void sendWrittenMessageFromUser() {
		String message = this.PSEUDO+" : "+msgToSend.getText();
		sendMessage(message);
        msgToSend.setText("");
        msgToSend.setCaretPosition(0);
	}
	
    /**
     * Indicates to the group that a new client is connected
     * displays every last messages for the new connected client
     * and launches the reception thread for the client.
     */
    public void connectUser() {
    	printHistory();
    	sendMessage(PSEUDO+" is connected");
        launchReceptionThread();
    }
	
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
        	sendWrittenMessageFromUser();
        } else if (e.getSource() == disconnectButton) {
        	sendMessage(this.PSEUDO+" left.");
        }
    }
    
    /**
     * When the user clicks on the JTextArea msgToSend and if 
     *   - the initial message is written on it, it is removed
     *   - no message is written, the initial message is written
     */
    public void mouseClicked(MouseEvent e) {  
    	String actualText = msgToSend.getText(); 
    	if (actualText.isEmpty()) {
    		msgToSend.setText(INITIAL_MESSAGE);
    	} else if (actualText.equals(INITIAL_MESSAGE)) {
    		msgToSend.setText("");
    	}
    }  
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    /**
     * When the user presses the key "enter" on the JTextArea msgToSend,
     * this written text is send to the server.
     */
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode()==KeyEvent.VK_ENTER){
    		e.consume();
    		sendWrittenMessageFromUser();
        }
    }
	@Override
	public void keyReleased(KeyEvent arg) {}
	@Override
	public void keyTyped(KeyEvent arg) {}
	
}

