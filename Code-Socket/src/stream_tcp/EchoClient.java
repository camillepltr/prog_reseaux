/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream_tcp;

import java.io.*;
import java.net.*;



public class EchoClient {

 
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

        try {
      	    // creation socket ==> connexion
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());  
            socOut= new PrintStream(echoSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            ReceptionClientThread ct = new ReceptionClientThread(echoSocket);
			ct.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
        
        System.out.println("What's your pseudo?");
        String pseudo = stdIn.readLine();
        String line = "";
        while (true) {
        	line=stdIn.readLine();
        	if (line.equals(".")){
                socOut.println(line);
                break;
            }
        	socOut.println(pseudo+" : "+line);
        }
        stdIn.close();
    }
}


