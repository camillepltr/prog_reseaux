
package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
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
 * @author Jeff Heaton, Camille Peltier, Camélia Guerraoui
 * @version 1.2
 */
public class WebServer { 
	  /**Path to the home page, to change depending on the path on your computer*/
	  private static final String INDEX = "/home/camille/git/prog_reseaux/TP-HTTP-Code/doc/index.html";

	  /**
	   * Creates a socket and waits for connection.
	   * When a client send a request of connection, the server accepts it. 
	   * The server reads the data sent and stops reading once a blank line is hit. 
	   * This blank line siganls the end of the client HTTP Headers
	   */
	  protected void start() {
		  ServerSocket s;
	
		  System.out.println("Webserver starting up on port 3000");

		  try {
			  s = new ServerSocket(3000);
		  } catch (Exception e) {
			  System.out.println("Error: " + e);
			  return;
		  }
	
		  System.out.println("Waiting for connection");
		  for (;;) {
			  try {
		          // Wait for a connection
		          Socket remote = s.accept();
		          // Remote is now the connected socket
		          System.out.println("\r\nConnection, sending data.");
		          BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
		          PrintWriter out = new PrintWriter(remote.getOutputStream());

		          String requestHeader ="";
		          // Le header fini par \r\n\r\n (CR LF CR LF)
		          int currentChar = '\0';
		          int previousChar = '\0';
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
								requestDELETE(out, requestHeaderSplit[1]);
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
	        // Send the response's header
            sendHeader(out, size, "200 OK");
            // Send the response's body
            for(String line : content) {
        	    out.println(line);
            }
            out.println("\r\n");
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
	        
          // Send the response's header
          sendHeader(out, size, "200 OK");
          
          out.flush();
          System.out.println("Response to HEAD : data sent");
	  }
	  
	  private void requestPUT(PrintWriter out, String filePath) {
			
	  }
	  
	  private void requestDELETE(PrintWriter out, String filePath) {
		  try {
				File toDelete = new File(filePath);
				boolean deleted = false;
				boolean existed = false;
				System.out.println("DELETE request received");
				//Check that it is the filePath to a correct filre
				if((existed = toDelete.exists()) && toDelete.isFile()) {
					deleted = toDelete.delete();
				}
				
				// Send Header
				if(deleted) {
					sendHeader(out,"204 No Content");
				} else if (!existed) {
					//TODO msg d'erreur 404 not found a faire dans la suite
					System.out.println("file did not exist");
				} else {
					// TODO si trouvé mais pas supprimé, autre erreur
					System.out.println("file was not deleted");
				}
				
				out.flush();
				System.out.println("Response to DELETE : data sent");
			} catch (Exception e) {
				System.err.println("Error in WebServer while deleting ressource:" + e);
			}
	  }
	  
	  /**
	   * Sends the response's header.
	   * First sendHeader method, when there is a content length to add to the header
	   * @param out socket's output i.e. the stream where the data should be sent
	   * @param size content length
	   * @param responseStatus response's status according to the HTTP Protocol
	   */
	  private void sendHeader(PrintWriter out, long size, String responseStatus) {
		  String header = "HTTP/1.0 " + responseStatus + "\r\n";
		  header += "Content-Type: text/html; charset=UTF-8\r\n";
		  header += "Content-Length : " + size +"\r\n";
		  header += "Server: Bot\r\n";
          // this blank line signals the end of the headers
		  header += "\r\n";
		  System.out.println(header);
		  out.write(header);
	  }
	  
	  /**
	   * Sends the response's header.
	   * Second sendHeader method, when there is no content length to add to the header
	   * @param out socket's output i.e. the stream where the data should be sent
	   * @param responseStatus response's status according to the HTTP Protocol
	   */
	  private void sendHeader(PrintWriter out, String responseStatus) {
		  String header = "HTTP/1.0 " + responseStatus +"\r\n";
		  header += "Content-Type: text/html; charset=UTF-8\r\n";
		  header += "Server: Bot\r\n";
          // this blank line signals the end of the headers
		  header += "\r\n";
		  System.out.println(header);
		  out.write(header);
	  }
	  
	  /**
	   * Start the application.
	   * @param args Command line parameters are not used.
	   */
	  public static void main(String args[]) {
		    WebServer ws = new WebServer();
		    ws.start();
	  }
}
