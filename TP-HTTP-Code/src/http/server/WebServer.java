
package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	  private static final String INDEX = "doc/index.html";
	  private static final String BAD_REQUEST = "doc/400_Bad_Request.html";
	  private static final String NOT_FOUND = "doc/404_Not_Found.html";
	  private static final String INTERNAL_ERROR = "doc/500_Internal_Error.html";

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
		          //BufferedOutputStream out = new BufferedOutputStream(remote.getOutputStream());
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
		        	  String requestBody = "";
			          switch(requestHeaderSplit[0]) {
			          		case "GET" :
								System.out.println("GET request received");
								requestGET(out, "doc"+requestHeaderSplit[1]);
						        break;
						        
							case "POST" :
								System.out.println("POST request received");
								requestBody = getRequestBody(in);
								requestPOST(out, "doc"+requestHeaderSplit[1], requestBody);
								break;
								
							case "HEAD" :
								System.out.println("HEAD request received");
								requestHEAD(out, "doc"+requestHeaderSplit[1]);
								break;
								
							case "PUT" :
								System.out.println("PUT request received");
								requestBody = getRequestBody(in);
								requestPUT(out, "doc"+requestHeaderSplit[1], requestBody);
								break;
								
							case "DELETE" :
								System.out.println("DELETE request received");
								requestDELETE(out, "doc"+requestHeaderSplit[1]);
								break;
								
					        default : 
					        	sendHeader(out, "501 Not Implemented", "doc"+requestHeaderSplit[1]);
					        	out.flush();
					        	break;
			          };
				  }
		          remote.close();
		      } catch (Exception e) {
		          System.out.println("Error: " + e);
		      }
	      }
	  }
  
	  private String getRequestBody(BufferedReader in) throws IOException{
		  String requestBody = "";
		  char[] cbuf = new char[1000];
		  boolean done = false;
		  while (!done) {
			  done = in.read(cbuf) > -1;
			  requestBody += new String(cbuf);
		  }
		  return requestBody;
	  }
	  
	  private ArrayList<String> readFile(String filePath) {
		  ArrayList<String> content = new ArrayList<String>();

		  // read the requested file
		  try {
			  BufferedInputStream inFile = new BufferedInputStream(new FileInputStream(filePath));
			  byte[] cbuf = new byte[1000];
			  while (inFile.read(cbuf) > -1) {
				  content.add(new String(cbuf));
			  }
			  return content;
		  } catch (Exception e) {
			  System.err.println("Error in WebServer while reading a ressource:" + e);
			  return null;
		  }
		  
	}

	private void sendBody(PrintWriter out, ArrayList<String> content) {
		  // Send the response's body
		 if(content != null) {
			  for(String line : content) {
				  out.println(line);
			  }
			  out.flush();
		 }
	}
	  
	  /*private void requestGET(BufferedOutputStream out, String filePath){
	        long size = -1;
	      
	        try {
	        	//BufferedInputStream inFile = new BufferedInputStream(new FileInputStream(filePath));
	            // Send the response's body
	  		  	byte[] cbuf = new byte[1000];
	  		  	ArrayList<byte[]> readBuffers = new ArrayList<byte[]>();
	  		  	int numberReadBytes = 0;

	  		  	while (numberReadBytes > -1) {
	  		  		numberReadBytes = inFile.read(cbuf);
	  		  		readBuffers.add(cbuf);
	  		  		size += numberReadBytes;
	  		  	}
	  		  	// Send the response's header
	            sendHeader(out, size+1, "200 OK", filePath);
	            for(byte[] rb : readBuffers) {
	            	//out.write(rb);
	            }
	            out.flush();
	            inFile.close();
	        } catch (Exception e) {
	            System.err.println("Error in WebServer while loading ressource:" + e);
	        }
            
            System.out.println("Response to GET : data sent");
	  }*/
	  
	  private void requestGET(PrintWriter out, String filePath) {
			Path path = Paths.get(filePath);
			String status = "200 OK";
	      
	        try {
	        	if (!Files.exists(path)) {
	        		path = Paths.get(NOT_FOUND);
	        		filePath = NOT_FOUND;
	        		status = "404 Not Found";
				}
	        	
	        	ArrayList<String> content = readFile(filePath);
	  		    sendHeader(out, Files.size(path), status, filePath);
	        	sendBody(out, content);

	 	        System.out.println("Response to GET : data sent");
	        } catch (Exception e) {
				ArrayList<String> content = readFile(INTERNAL_ERROR);
	  		    sendHeader(out, "500 Internal Server Error", INTERNAL_ERROR);
	        	sendBody(out, content);
	            System.err.println("Error in WebServer while loading ressource:" + e);
	        }
	  }
	  
	  //private void requestPOST(BufferedOutputStream out, String filePath, String requestBody) {
	  private void requestPOST(PrintWriter out, String filePath, String requestBody) {
		  try {
			  Path path = Paths.get(filePath);
			  	if (requestBody.isEmpty()) {
			  		ArrayList<String> content = readFile(BAD_REQUEST);
		  		    sendHeader(out, "400 Bad Request", BAD_REQUEST);
		        	sendBody(out, content);
			  		return;
			  	}

				if (!Files.exists(path)){
	                Files.createFile(path);
	                Files.write(path, requestBody.getBytes(), StandardOpenOption.APPEND);
					sendHeader(out,"201 Created", filePath);
	            } else {
					Files.write(path, requestBody.getBytes(), StandardOpenOption.APPEND);
					sendHeader(out,"200 OK", filePath);
	            }

				out.flush();
				System.out.println("Response to POST : data sent");
			} catch (Exception e) {
				ArrayList<String> content = readFile(INTERNAL_ERROR);
	  		    sendHeader(out, "500 Internal Server Error", INTERNAL_ERROR);
	        	sendBody(out, content);
				System.err.println("Error in WebServer while posting ressource:" + e);
			}
	  }
	  
	  //private void requestHEAD(BufferedOutputStream out, String filePath) {
	  private void requestHEAD(PrintWriter out, String filePath) {
		    Path path = Paths.get(filePath);
			String status = "200 OK";
	      
	        try {
	        	if (!Files.exists(path)) {
	        		path = Paths.get(NOT_FOUND);
	        		filePath = NOT_FOUND;
	        		status = "404 Not Found";
				}
	        	
	        	ArrayList<String> content = readFile(filePath);
	  		    sendHeader(out, Files.size(path), status, filePath);
	  		    out.flush();

	 	        System.out.println("Response to HEAD : data sent");
	        } catch (Exception e) {
				ArrayList<String> content = readFile(INTERNAL_ERROR);
	  		    sendHeader(out, "500 Internal Server Error", INTERNAL_ERROR);
	        	sendBody(out, content);
	            System.err.println("Error in WebServer while loading ressource:" + e);
	        }

	  }
	  
	  //private void requestPUT(BufferedOutputStream out, String filePath, String requestBody) {
	  private void requestPUT(PrintWriter out, String filePath, String requestBody) {
		  
		  try {
			    Path path = Paths.get(filePath);
			    String status = "200 OK";
			  	if (requestBody.isEmpty()) {
			  		ArrayList<String> content = readFile(BAD_REQUEST);
		  		    sendHeader(out, "400 Bad Request", BAD_REQUEST);
		        	sendBody(out, content);
			  		return;
			  	}

				if (!Files.exists(path)){
	                Files.createFile(path);
	                Files.write(path, requestBody.getBytes(), StandardOpenOption.CREATE);
					status = "201 Created";
	            } else {
					Files.write(path, requestBody.getBytes(), StandardOpenOption.CREATE);
	            }
				
				sendHeader(out, status, filePath);
				out.flush();
				System.out.println("Response to PUT : data sent");
			} catch (Exception e) {
				ArrayList<String> content = readFile(INTERNAL_ERROR);
	  		    sendHeader(out, "500 Internal Server Error", INTERNAL_ERROR);
	        	sendBody(out, content);
				System.err.println("Error in WebServer while puting ressource:" + e);
			}
	  }
	  
	  //private void requestDELETE(BufferedOutputStream out, String filePath) {
	  private void requestDELETE(PrintWriter out, String filePath) {
		  try {

				System.out.println("DELETE request received");
				File toDelete = new File(filePath);

				boolean deleted = false;
				boolean existed = false;
				//Check that it is the filePath to a correct filre
				if((existed = toDelete.exists()) && toDelete.isFile()) {
					deleted = toDelete.delete();
				}
				
				// Send Header
				if(deleted) {
					sendHeader(out,"204 No Content", filePath);
					out.flush();
				} else if (!existed) {
					ArrayList<String> content = readFile(NOT_FOUND);
		  		    sendHeader(out, "404 Not Found", NOT_FOUND);
		        	sendBody(out, content);
				} 
				System.out.println("Response to DELETE : data sent");
			} catch (Exception e) {
				ArrayList<String> content = readFile(INTERNAL_ERROR);
	  		    sendHeader(out, "500 Internal Server Error", INTERNAL_ERROR);
	        	sendBody(out, content);
				System.err.println("Error in WebServer while deleting ressource:" + e);
			}
	  }
	  
	  private String getContentType(String filePath) {
		  String type = "";
		  if (filePath.endsWith(".html")) {
			  type = "text/html";
		  } else if (filePath.endsWith(".jpeg")) {
			  type = "image/jpeg";
		  } else if (filePath.endsWith(".png")) {
			  type = "image/png";
		  }
		  
		  /*String[] filePathSplitted = new String[2];
		  filePathSplitted = filePath.split(".");
		  System.out.println(filePathSplitted.length-1);
		  String extension = filePathSplitted[filePathSplitted.length-1];
		  String type = "";
		  switch(extension) {
		  		case "jpg" :
		  		case "svg" :
		  		case "png" :
		  			type = "image/"+extension;
		  			break;
		  		case "html" :
		  		case "txt" :
		  			type = "text/"+extension;
		  			break;
		  		case "mp4" :
		  		case "mpeg" :
		  			type = "video/"+extension;
		  			break;
		  		case "mp3" :
		  			type = "audio/"+extension;
		  			break;
		  		case "zip" :
		  		case "xml" :
		  		case "pdf" :
		  			type = "application/"+extension;
		  			break;
		  }*/
		  return type;
	  }
	  
	  /**
	   * Sends the response's header.
	   * First sendHeader method, when there is a content length to add to the header
	   * @param out socket's output i.e. the stream where the data should be sent
	   * @param size content length
	   * @param responseStatus response's status according to the HTTP Protocol
	   */
	  //private void sendHeader(BufferedOutputStream out, long size, String responseStatus, String filePath){
	  private void sendHeader(PrintWriter out, long size, String responseStatus, String filePath){
		  String type = getContentType(filePath);
		  String header = "HTTP/1.0 " + responseStatus + "\r\n";
		  header += "Content-Type: "+type+"; charset=UTF-8\r\n";
		  header += "Content-Length : " + size +"\r\n";
		  header += "Server: Bot\r\n";
          // this blank line signals the end of the headers
		  header += "\r\n";
		  System.out.println(header);
		  try {
			  out.println(header);
		  } catch (Exception e) {
			  System.err.println("Error while sending the Header : "+e);
		  }
	  }
	  
	  /**
	   * Sends the response's header.
	   * Second sendHeader method, when there is no content length to add to the header
	   * @param out socket's output i.e. the stream where the data should be sent
	   * @param responseStatus response's status according to the HTTP Protocol
	   */
	  //private void sendHeader(BufferedOutputStream out, String responseStatus, String filePath){
	  private void sendHeader(PrintWriter out, String responseStatus, String filePath) {
		  String type = getContentType(filePath);
		  String header = "HTTP/1.0 " + responseStatus +"\r\n";
		  header += "Content-Type: "+type+"; charset=UTF-8\r\n";
		  header += "Server: Bot\r\n";
          // this blank line signals the end of the headers
		  header += "\r\n";
		  System.out.println(header);
		  try {
			  out.println(header);
		  } catch (Exception e) {
			  System.err.println("Error while sending the Heade : "+e);
		  }
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
