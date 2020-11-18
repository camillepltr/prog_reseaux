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
	          /*
	          String str = ".";
	          while (str != null && !str.equals("")) {
	        	  str = in.readLine();
	          }*/
	          String requestType = in.readLine();
	          String headers = in.readLine();
	          String blank = in.readLine();
	          String []requestTypeSplit = requestType.split(" ");
	          System.out.println(requestType);
	          
	          switch(requestTypeSplit[0]) {
					case "GET" :
						Path path = Paths.get(requestTypeSplit[1]);
				        ArrayList<String> content = new ArrayList<String>();
				      
				        try {
				             content = (ArrayList<String>) Files.readAllLines(path, Charset.forName("UTF-8"));
				        } catch (Exception e) {
				            System.err.println("Error in WebServer while loading ressource:" + e);
				        }
				        // Send the response
			            // Send the headers
			            out.println("HTTP/1.0 200 OK<CR><LF>");
			            out.println("Content-Type: text/html; charset=UTF-8<CR><LF>");
			            out.println("Server: Bot<CR><LF>");
			            // this blank line signals the end of the headers
			            out.println("<CR><LF>");
			            // Send the HTML page
			          
			            for(String line : content) {
			        	    out.println(line);
			            }
			            out.println("<CR><LF>");
			            System.out.println("Response to GET : data sent");
			            out.flush();
				        
				        break;
				        
					case "POST" :
						System.out.println("POST request receveid");
						break;
						
			        default : 
			        	break;
	          }
	          
	          
	
	          
	          ;
	          remote.close();
	      } catch (Exception e) {
	          System.out.println("Error: " + e);
	      }
      }
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