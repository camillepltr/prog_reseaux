
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
 * @author Camille Peltier, Camélia Guerraoui
 * @version 1.2
 */
public class WebServer { 
	  private static final String INDEX = "doc/index.html";
	  private static final String BAD_REQUEST = "doc/Error_Pages/400_Bad_Request.html";
	  private static final String NOT_FOUND = "doc/Error_Pages/404_Not_Found.html";
	  private static final String INTERNAL_ERROR = "doc/Error_Pages/500_Internal_Error.html";
	  private String resourcesDirectory;
	  
	  /**
	   * Start the application.
	   * @param args Command line's arguments, the first argument is the server port
	   */
	  public static void main(String args[]) {
		  	if (args.length != 2) {
			   System.out.println("Usage: java WebServer <Server port> <Path to resources>, i.e. java WebServer 3000 doc");
			   System.exit(1);
		  	}
		  	
		  	int serverPort = Integer.parseInt(args[0]);
		    WebServer ws = new WebServer(args[1]);
		    ws.start(serverPort);
	  }
	  
	  /**
	   * Constructor
	   * @param d the path to the resources directory
	   */
	  public WebServer(String d) {
		  this.resourcesDirectory = d;
	  }

	  /**
	   * Creates a socket and waits for connection.
	   * When a client send a request of connection, the server accepts it. 
	   * The server reads the data sent and stops reading once a blank line is hit. 
	   * This blank line siganls the end of the client HTTP Headers
	   */
	  protected void start(int serverPort) {
		  ServerSocket s;
	
		  

		  try {
			  s = new ServerSocket(serverPort);
			  System.out.println("Webserver starting up on port " + serverPort);
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
		          PrintWriter outStream = new PrintWriter(remote.getOutputStream());
		          BufferedOutputStream bytesOut = new BufferedOutputStream(remote.getOutputStream());
		          OutputStream out = remote.getOutputStream();

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
								requestGET(out, resourcesDirectory+requestHeaderSplit[1]);
						        break;
						        
							case "POST" :
								System.out.println("POST request received");
								requestBody = getRequestBody(in);
								requestPOST(out, resourcesDirectory+requestHeaderSplit[1], requestBody);
								break;
								
							case "HEAD" :
								System.out.println("HEAD request received");
								requestHEAD(out, resourcesDirectory+requestHeaderSplit[1]);
								break;
								
							case "PUT" :
								System.out.println("PUT request received");
								requestBody = getRequestBody(in);
								requestPUT(out, resourcesDirectory+requestHeaderSplit[1], requestBody);
								break;
								
							case "DELETE" :
								System.out.println("DELETE request received");
								requestDELETE(out, resourcesDirectory+requestHeaderSplit[1]);
								break;
								
					        default : 
					        	sendHeader(out, "501 Not Implemented", resourcesDirectory+requestHeaderSplit[1]);
					        	out.flush();
					        	break;
			          };
				  }
		          in.close();
		          out.close();
		          bytesOut.close();
		          remote.close();
		      } catch (Exception e) {
		          System.out.println("Error: " + e);
		      }
	      }
	  }
  
	  /**
	   * Get the request's body 
	   * @param in the input stream to get the body from
	   * @return a string representation of the request's body
	   * @throws IOException Signals that an I/O exception of some sort has occurred.
	   * 			This class is the general class of exceptions produced by failed or interrupted I/O operations.
	   */
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
	  
	  /**
	   * Read a local file (text file
	   * @param filePath the filepath to the file to read
	   * @return an array of strings representing the content of the file
	   * @deprecated
	   */
	  @Deprecated
	  private ArrayList<String> readFile(String filePath) {
		  ArrayList<String> content = new ArrayList<String>();

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
	  
	  private byte[] readFile(String filePath, int size) throws IOException{
		  byte[] image = new byte[size];
          FileInputStream fileIn = null;

          try {
        	  File file = new File(filePath);
              fileIn = new FileInputStream(file);
              fileIn.read(image);
          } finally {
              if (fileIn != null) 
                  fileIn.close();
          }

		  return image;
		  
	  }
	  

	  /**
	   * Send the body, i.e. write the body's content in the output stream
	   * @param out the PrintWriter to write in
	   * @param content an array containt the Strings to write
	   * @deprecated
	   */
	  @Deprecated
	  private void sendBody(PrintWriter out, ArrayList<String> content) {
			if(content != null) {
				for(String line : content) {
					out.println(line);
				}
				  out.flush();
			}
		}
	  

	  private void sendBody(OutputStream bytesOut, byte[]content) {
		  try{
			  bytesOut.write(content, 0, content.length);
			  bytesOut.flush();
		  } catch(Exception e) {
			  System.err.println("Error in WebServer while sending bytes" + e);
		  }
		  
	  }
	  
	  private void sendError(OutputStream out, String status, String filePath) {
		  try {
			  Path p = Paths.get(filePath);
			  long size = Files.size(p);
			  byte[] content = readFile(filePath, (int)size);
			  sendHeader(out, status, filePath);
	      	  sendBody(out, content);
	          System.err.println("Error in WebServer");
		  } catch (Exception e) {
			  e.printStackTrace();
		  }

	  }
	  
	  
		/**
		 * Method called to treat a HTTP GET request
		 * @param out the PrintWriter to write the response to
		 * @param filePath the file path to the requested file
		 */
		  private void requestGET(OutputStream out, String filePath) {
			  	//initial path and status
				Path path = Paths.get(filePath);
				String status = "200 OK";
		        try {
		        	
		        	if (!Files.exists(path)) {
		        		//Change the path to send the 404_Not_Found page and change status code
		        		path = Paths.get(NOT_FOUND);
		        		filePath = NOT_FOUND;
		        		status = "404 Not Found";
					}
		        	
		        	long size = Files.size(path);
	        		byte [] bytesRead = readFile(filePath, (int)size);
	        		sendHeader(out, size, status, filePath);
	        		sendBody(out, bytesRead);
		
		 	        System.out.println("Response to GET : data sent");
		        } catch (Exception e) {
		        	//Send 500_Internal_Error page
					sendError(out, "500 Internal Error", INTERNAL_ERROR);
		        }
		  }
		  
		  /**
		   * Method called to treat a HTTP POST request
		   * @param out the PrintWriter to write the response to
		   * @param filePath the path to the requested file
		   * @param requestBody a String representation of the request's body, needed to trea the POST request
		   */
		  private void requestPOST(OutputStream out, String filePath, String requestBody) {
			  try {
				  Path path = Paths.get(filePath);
				  	if (requestBody.isEmpty()) {
				  		//If there is nothing in the request's body, send a BAD REQUEST response
				  		long size = Files.size(path);
				  		byte[] content = readFile(BAD_REQUEST, (int)size);
			  		    sendHeader(out, "400 Bad Request", BAD_REQUEST);
			        	sendBody(out, content);
				  		return;
				  	}
	
				  	//Create new file containing the request's body or append it to an existing file
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
					//Send 500_Internal_Error page
					sendError(out, "500 Internal Error", INTERNAL_ERROR);
				}
		  }
	  
	   /**
	   * Method called to treat a HTTP HEAD request
	   * @param out the PrintWriter to write the response to
	   * @param filePath the file path to the requested file
	   */
	  private void requestHEAD(OutputStream out, String filePath) {
		  	//initial path and status
		    Path path = Paths.get(filePath);
			String status = "200 OK";
	      
	        try {
	        	if (!Files.exists(path)) {
	        		//Change the path to send the 404_Not_Found page and change status code
					sendError(out, "404 Not Found", NOT_FOUND);
					return;
				}
	        	
	        	//Read requested file, send response header
	        	long size = Files.size(path);
	        	byte[] content = readFile(filePath, (int)size);
	  		    sendHeader(out, size, status, filePath);
	  		    out.flush();

	 	        System.out.println("Response to HEAD : data sent");
	        } catch (Exception e) {
	        	//Send 500_Internal_Error page
	        	sendError(out, "500 Internal Error", INTERNAL_ERROR);
	        }

	  }
	  
	  /**
	   * Method called to treat a HTTP PUT request
	   * @param out the PrintWriter to write the response to
	   * @param filePath the path to the requested file
	   * @param requestBody a String representation of the request's body, needed to treat the PUT request
	   */
	  private void requestPUT(OutputStream out, String filePath, String requestBody) {
		  
		  try {
			    Path path = Paths.get(filePath);
			    String status = "200 OK";
			  	if (requestBody.isEmpty()) {
			  		//If there is nothing in the request's body, send a BAD REQUEST response
			  		sendError(out, "400 Bad Request", BAD_REQUEST);
			  		return;
			  	}

			  //Create new file containing the request's body; if the file already exists replace it
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
				//Send 500_Internal_Error page
				sendError(out, "500 Internal Error", INTERNAL_ERROR);
			}
	  }
	  
	  /**
	   * Method called to treat a HTTP DELETE request
	   * @param out the PrintWriter to write the response to
	   * @param filePath the file path to the requested file
	   */
	  private void requestDELETE(OutputStream out, String filePath) {
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
					//Success
					sendHeader(out,"204 No Content", filePath);
					out.flush();
				} else if (!existed) {
					//Case where the requested file does not exist
					sendError(out, "404 Not Found", NOT_FOUND);
					return;
				} 
				System.out.println("Response to DELETE : data sent");
			} catch (Exception e) {
				//Send 500_Internal_Error page
				sendError(out, "500 Internal Error", INTERNAL_ERROR);
			}
	  }
	  
	  /**
	   * Get the Content-Type for the response header depending on the file extension
	   * @param filePath the filepath to the requested file
	   * @return a String represnetation of the content-type weel-formed for the header
	   */
	  private String getContentType(String filePath) {
		  String type = "";
		  if (filePath.endsWith(".html")) {
			  type = "text/html";
		  } else if (filePath.endsWith(".txt")) {
			  type = "text/txt";
		  } else if (filePath.endsWith(".jpeg")) {
			  type = "image/jpeg";
		  } else if (filePath.endsWith(".png")) {
			  type = "image/png";
		  } else if (filePath.endsWith(".gif")) {
			  type = "image/gif";
		  } else if (filePath.endsWith(".jpg")) {
			  type = "image/jpg";
		  } else if (filePath.endsWith(".svg")) {
			  type = "image/svg";
		  } else if (filePath.endsWith(".mp3")) {
			  type = "audio/mp3";
		  } else if (filePath.endsWith(".mpeg")) {
			  type = "video/mpeg";
		  } else if (filePath.endsWith(".mp4")) {
			  type = "video/mp4";
		  } else if (filePath.endsWith(".pdf")) {
			  type = "application/pdf";
		  } else if (filePath.endsWith(".xml")) {
			  type = "application/xml";
		  } else if (filePath.endsWith(".zip")) {
			  type = "application/zip";
		  }
		  return type;
	  }
	  
	  /**
	   * Sends the response's header.
	   * First sendHeader method, when there is a content length to add to the header
	   * @param out socket's output i.e. the stream where the data should be sent
	   * @param size content length
	   * @param responseStatus response's status according to the HTTP Protocol
	   * @param filePath a String reprentation of the path to the requested file
	   */
	  private void sendHeader(OutputStream out, long size, String responseStatus, String filePath){
		  String type = getContentType(filePath);
		  String header = "HTTP/1.0 " + responseStatus + "\r\n";
		  header += "Content-Type: "+type+"; charset=UTF-8\r\n";
		  header += "Content-Length : " + size +"\r\n";
		  header += "Server: Bot\r\n";
          // this blank line signals the end of the headers
		  header += "\r\n";
		  System.out.println(header);
		  try {
			  out.write(header.getBytes());
		  } catch (Exception e) {
			  System.err.println("Error while sending the Header : "+e);
		  }
	  }
	  
	  /**
	   * Sends the response's header.
	   * Second sendHeader method, when there is no content length to add to the header
	   * @param out socket's output i.e. the stream where the data should be sent
	   * @param responseStatus response's status according to the HTTP Protocol
	   * @param filePath a String reprentation of the path to the requested file
	   */
	  private void sendHeader(OutputStream out, String responseStatus, String filePath) {
		  String type = getContentType(filePath);
		  String header = "HTTP/1.0 " + responseStatus +"\r\n";
		  header += "Content-Type: "+type+"; charset=UTF-8\r\n";
		  header += "Server: Bot\r\n";
          // this blank line signals the end of the headers
		  header += "\r\n";
		  System.out.println(header);
		  try {
			  out.write(header.getBytes());
		  } catch (Exception e) {
			  System.err.println("Error while sending the Heade : "+e);
		  }
	  }
	  

}
