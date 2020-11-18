/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream_tcp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



public class EchoClient  extends JFrame implements ActionListener{
    private PrintStream socOut = null;
    private String pseudo = null;

    private JTextArea conversation = new JTextArea(100, 50);
    private JTextArea msgToSend = new JTextArea(100, 50);
    private JButton send = new JButton("Send");
    private JButton disconnect = new JButton("Disconnect");
    private JScrollPane pane2, pane1;
    
    public EchoClient(PrintStream so, String p) {
    	super("Chat TCP - Client side - " + p);
    	socOut = so;
    	pseudo = p;
		
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
  	* main method, client side
  	* 	- TCP protocol
  	* 	- creates a communication socket with the server and starts a ReceptionClientThread to receive messages from the server
  	* 	- reads user (client) input and sends the message to to server
	* @param EchoServerMultiThreadedPort port
	* @exception
	* @see ReceptionClientThread.java
  	* 
  	**/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;
        

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }
        /*
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What's your pseudo?");
        String pseudo = stdIn.readLine();
        */
        
        String pseudo = JOptionPane.showInputDialog( "Chat's your pseudo?");

        try {
      	    // creation socket ==> connexion
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());  
            socOut= new PrintStream(echoSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            EchoClient interfaceClient = new EchoClient(socOut, pseudo);
            ReceptionClientThread ct = new ReceptionClientThread(echoSocket, interfaceClient.conversation );
			ct.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
        

        /*
        String line = "";
        while (true) {
        	line=stdIn.readLine();
        	if (line.equals(".")){
                socOut.println(line);
                break;
            }
        	socOut.println(pseudo+" : "+line);
        }
        stdIn.close();*/
        System.out.println("done");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == send) {
            sendMessage();
        } else if (e.getSource() == disconnect) {
        	disconnect();
        }
    }
	
	public void sendMessage() {
		String line=msgToSend.getText();
		System.out.println(line);
		
		// "." was initially the character to disconnect, replaced by button
    	if (line.equals(".")){
            socOut.println(line);
        } 
    	socOut.println(pseudo+" : " + line);
		
        msgToSend.setText("");
        msgToSend.setCaretPosition(0);
	}
	
	public void disconnect() {
		socOut.println(".");
	}
	
}


