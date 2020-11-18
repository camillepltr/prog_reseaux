package http.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {
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
		      InetAddress addr;      
		      Socket sock = new Socket(httpServerHost, httpServerPort);
		      addr = sock.getInetAddress();
		      System.out.println("Connected to " + addr);
		      PrintWriter out = new PrintWriter(sock.getOutputStream());
		      out.println("GET src/Adder.html HTTP/1.0 <CR><LF>");
	          out.println("Host : localhost <CR><LF>");
	          out.println("<CR><LF>");
	          out.flush();
	          
	          BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	          String s = ".";
	          //Print response status and header
    		  while(!s.equals("<CR><LF>")) {
    			  s = in.readLine();
    			  System.out.println(s);
    		  }
    		  
    		  //print content
    		  s = ".";
    		  while(!s.equals("<CR><LF>")) {
    			  s = in.readLine();
    			  System.out.println(s);
    		  }
	        		 
	          
	          
		      //sock.close();
	    } catch (java.io.IOException e) {
		      System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
		      System.out.println(e);
	    }
  }
}