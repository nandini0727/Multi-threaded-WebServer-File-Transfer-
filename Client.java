/**
 * Name- Utpala Vasantha Nandini Korrapolu
 * Student ID - 1001667678
 * REFERENCES:
 *	http://www.csc.villanova.edu/~schragge/CSC8560/project1/webserver_prt.htm
 *	https://github.com/jyotisalitra/web-client-server
 *	https://www.infoworld.com/article/2853780/socket-programming-for-scalable-systems.html

 */

import java.io.*;
import java.net.*;

public class Client{
	
	//initialize variables to default
	static String ipaddress = "127.0.0.1";
	static int port = 8080;
	static String fileName = "index.html";
	private static BufferedReader input = null;
	
	static Socket socket = null;
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		final String CRLF = "\r\n";
		//if args[0] is given by user use that as ipaddress
		if(args.length >= 1) {
			ipaddress = args[0];
		}
		
		//if args[1] is given by user use that as port
		if(args.length >= 2) {
			try {
				port = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("Integer Port is not provided... Connecting with default port 8080");
			}
		}
		
		//if args[2] is given by user use that as file name
		if(args.length >=3) {
			fileName = args[2];
		}
		
		boolean isConnected = true;
		
		
		
		
		//if the server is not able to connect with given ipaddress and port number then exception is thrown
		try {
			//connect to server
			socket = new Socket(ipaddress, port);
		}
		catch(Exception e) {
			System.out.println("Unable to connect to server with ipaddress and port.. Try again!!!");
			isConnected = false;
		}
		
		
		if(isConnected) {
			System.out.println("[Client]>Client is connected to server at: "+ ipaddress + ":" + port);
			System.out.println("Socket family: AddressFamily.AF_INET");
			System.out.println("TimeOut: " +socket.getSoTimeout());
			
			//create input and output stream objects
			InputStream inputStream = socket.getInputStream();
			
			PrintStream outputStream = new PrintStream(socket.getOutputStream());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			
			FileOutputStream fos = null;
			
			//start the timer to calculate RTT
			long start_time = System.currentTimeMillis();
			
			// Follow the HTTP protocol of GET /<filename > HTTP/1.0 followed by an empty line
			String requestLine =  "GET " + "/" + fileName + " HTTP/1.0"+CRLF;
			System.out.println("[CLIENT]> Sending HTTP GET request: " + requestLine);
			
			
			outputStream.println(requestLine);
			outputStream.println();
			
			//read data from file until we reach the end
			String line = br.readLine();
			//extract status and print on console
			System.out.println("[Client]>Status: "+line);
			
			line = br.readLine();
			
			//extract content type and print on console
			System.out.println("[Client]>"+line);
			
			//end of header line CRLF
			line = br.readLine();
			StringBuilder content = new StringBuilder();
			String result = null;
			
			System.out.println("[Client]>Content");
			if(line != null) {
				while((result = br.readLine()) != null) {
					
					//save content to buffer
					content.append(result + "\n");
					System.out.println(result);

				}
			}
			
			
			try {
				//get a name of the file from the response
				String fileName = getFileName(content.toString());
				
				//open a outputstream to the fileName
				//file will be created if it does not exist
				fos = new FileOutputStream(fileName);
				
				fos.write(content.toString().getBytes());
				fos.flush();
				
				System.out.println("[CLIENT]> HTTP Response received. File Created: " + fileName);
			}catch(Exception e) {
				System.out.println("");
			}
			
			
			
			
			//stop the timer as response including the file transfer is done
			long end_time = System.currentTimeMillis();
			
			//calculate RTT and display on console
			System.out.println("RTT for client request:" +(end_time-start_time) +"ms");
			
			try {
				//Close all input stream, buffered reader, socket and output stream
				System.out.println("[Client]> : Closing all streams and sockets");
	        	inputStream.close();
	        	br.close();
	        	outputStream.close();
	        	if(fos!=null)
	        		fos.close();
	            socket.close(); 
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * method to get filename from the value provided between "title" tag or consider default index.html
	 * @param content
	 * @return
	 */
	private static String getFileName(String content)
	{
		//default filename if <title> tag is empty
		String filename = "";
	
		filename = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));
		
		if(filename.equals(""))
		{
			filename = "index";
		}
		
		filename = filename+".html";
		
		return filename;
	}
}