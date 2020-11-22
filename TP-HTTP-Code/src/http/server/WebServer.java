///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer { 
	  /**Path to the home page, to change depending on the path on your computer*/
	  private static final String INDEX = "/home/camille/git/prog_reseaux/TP-HTTP-Code/doc/index.html";

	  protected void start() {
	    ServerSocket s;
	
	    System.out.println("Webserver starting up on port 3000");
	    System.out.println("(press ctrl-c to exit)");
	    try {
	      // create the main server socket
	      s = new ServerSocket(3000);
	    } catch (Exception e) {
	      System.out.println("Error: " + e);
	      return;
	    }
	
	    System.out.println("Waiting for connection");
	    for (;;) {
	        try {
		          // wait for a connection
		          Socket remote = s.accept();
		          // remote is now the connected socket
		          System.out.println("Connection, sending data.");
		          BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
		          PrintWriter out = new PrintWriter(remote.getOutputStream());
		
		          // read the data sent. We basically ignore it,
		          // stop reading once a blank line is hit. This
		          // blank line signals the end of the client HTTP
		          // headers.
		          
		          String requestHeader ="";
		          // Le header fini par \r\n\r\n (CR LF CR LF)
		          int currentChar = '\0', previousChar = '\0';
				  boolean newline = false;
				  while((currentChar = in.read()) != -1 && !(newline && previousChar == '\r' && currentChar == '\n')) {
					  if(previousChar == '\r' && currentChar == '\n') {
						  newline = true;
					  } else if(!(previousChar == '\n' && currentChar == '\r')) {
						  newline = false;
					  }
					  previousChar = currentChar;
					  requestHeader += (char) currentChar;
				  }
					
		          String []requestHeaderSplit = requestHeader.split(" ");
		          System.out.println("Request header :");
		          System.out.println(requestHeader);
		          
		         // Default : open index.html
				 if(requestHeaderSplit[1].equals("/")) {
					 	System.out.println("Open index");
						requestGET(out, INDEX);
				 } else {
		          
			          switch(requestHeaderSplit[0]) {
							case "GET" :
								System.out.println("GET request received");
								requestGET(out, requestHeaderSplit[1]);
						        break;
						        
							case "POST" :
								System.out.println("POST request received");
								break;
								
							case "HEAD" :
								System.out.println("HEAD request received");
								requestHEAD(out, requestHeaderSplit[1]);
								break;
								
							case "PUT" :
								System.out.println("PUT request received");
								break;
								
							case "DELETE" :
								System.out.println("DELETE request received");
								break;
								
					        default : 
					        	break;
			          };
				  }
		          remote.close();
		      } catch (Exception e) {
		          System.out.println("Error: " + e);
		      }
	      }
	  }
  
	  private void requestGET(PrintWriter out, String filePath) {
			Path path = Paths.get(filePath);
	        ArrayList<String> content = new ArrayList<String>();
	        long size = -1;
	      
	        try {
	             content = (ArrayList<String>) Files.readAllLines(path, Charset.forName("UTF-8"));
	             size = Files.size(path);
	        } catch (Exception e) {
	            System.err.println("Error in WebServer while loading ressource:" + e);
	        }
	        // Send the response
            // Send the header
            sendHeader(out, size);
            // Send the HTML page
            for(String line : content) {
        	    out.println(line);
            }
            out.println("<CR><LF>");
            out.flush();
            
            System.out.println("Response to GET : data sent");
	  }
	  
	  private void requestPOST(PrintWriter out, String filePath) {
			
	  }
	  
	  private void requestHEAD(PrintWriter out, String filePath) {
		  Path path = Paths.get(filePath);
		  long size = -1;
		  

	      try {
	    	  size = Files.size(path);
	      } catch (Exception e) {
	          System.err.println("Error in WebServer while loading ressource:" + e);
	      }
	        
          // Send the header
          sendHeader(out, size);
          
          out.flush();
          System.out.println("Response to HEAD : data sent");
	  }
	  
	  private void requestPUT(PrintWriter out, String filePath) {
			
	  }
	  
	  private void requestDELETE(PrintWriter out, String filePath) {
			
	  }
	  
	  private void sendHeader(PrintWriter out, long size) {
		  String header = "HTTP/1.0 200 OK\r\n";
		  header += "Content-Type: text/html; charset=UTF-8\r\n";
		  header += "Content-Length : " + size +"\r\n";
		  header += "Server: Bot\r\n";
          // this blank line signals the end of the headers
		  header += "\r\n";
		  System.out.println(header);
		  out.write(header);
	  }
	  
  

	  /**
	   * Start the application.
	   * 
	   * @param args
	   *            Command line parameters are not used.
	   */
	  public static void main(String args[]) {
		    WebServer ws = new WebServer();
		    ws.start();
	  }
}
