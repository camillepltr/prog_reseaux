package http.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * WebPing connects to a Http Server with a socket. It sends a request for the file 
 * Adder.html and displays a response.
 * This application has to be started with two parameters :
 *  - first the name of the server host
 *  - then the server port number
 * If the application is started with less or more than two parameters, it stops.
 * @author Camille Peltier, Camélia Guerraoui
 * @see WebServer
 */
public class WebPing {

	/**
	 * Main Method which connects to a Http Server with a socket.
	 * It sends a request for the file Adder.html and displays a response.
	 * @param args a list of 2 strings containing first the server host name 
	 * and then the server port. If the list contains less or more than two parameters,
	 * the method stops.
	 */
	public static void main(String[] args) {
  
		if (args.length != 2) {
			System.err.println("Usage java WebPing <server host name> <server port number>");
			return;
		}	
  
	    String httpServerHost = args[0];
	    int httpServerPort = Integer.parseInt(args[1]);
	    httpServerHost = args[0];
	    httpServerPort = Integer.parseInt(args[1]);
	
	    try {  
	    	//Create a socket
	    	Socket sock = new Socket(httpServerHost, httpServerPort);
	    	InetAddress addr = sock.getInetAddress();
	    	System.out.println("Connected to " + addr);

	    	//Send a request
		    PrintWriter out = new PrintWriter(sock.getOutputStream());
		    out.println("GET src/Adder.html HTTP/1.0\r\n");
	        out.println("Host : localhost\r\n");
	        out.println("\r\n");
	        out.flush();

	        //Print the response's status and header
	        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	        String s = "";
	        while(!s.equals("\r\n")) {
	        	s = in.readLine();
	        	System.out.println(s);
	        }

	        //Print the responses's content i.e. the message body
	        s = "";
	        while(!s.equals("\r\n")) {
	        	s = in.readLine();
	        	System.out.println(s);
	        }

	    } catch (java.io.IOException e) {
		      System.err.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
		      System.err.println(e);
	    }
	}
}