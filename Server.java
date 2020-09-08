/**
 * Name- Utpala Vasantha Nandini Korrapolu
 * Student ID - 1001667678
 * REFERENCES:
 *	http://www.csc.villanova.edu/~schragge/CSC8560/project1/webserver_prt.htm
 *	https://github.com/jyotisalitra/web-client-server
 *	https://www.infoworld.com/article/2853780/socket-programming-for-scalable-systems.html

 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public final class Server{
 private static ServerSocket serverSocket = null;
 private static Socket clientSocket = null;

public static void main(String[] args) throws Exception{
	int port = 8080;
	
	//accept the port number from user if given else initialize port to 8080
	if(args.length ==0)
		port = 8080;
	else {
		try {
			port = Integer.parseInt(args[0]);
		}catch (NumberFormatException nfe){
			System.err.println("[SERVER]> Integer Port is not provided. Server will start at default port 8080.");
			}
		
	}
		
	
	try {
		InetAddress serverInet = InetAddress.getByName("localhost");
		//Create a new server socket with the given port number
		 serverSocket = new ServerSocket(port,0, serverInet);
		 
		 System.out.println("[SERVER]>Server started at host:"+ serverSocket.getInetAddress()+"and port: "+ port);
		 System.out.println("[SERVER]>Waiting for a client....");
		 
		int clientID =0;
	    while(true) {
	    	//Accept the incoming connections from client. Establishes connection
	    	clientSocket = serverSocket.accept();
	    	System.out.println("[SERVER - CLIENT"+clientID+"]> Connection established with the client at " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
	    	System.out.println("Socket family: AddressFamily.AF_INET");
	    	System.out.println("TimeOut: " +clientSocket.getSoTimeout());
	    	
	    	//create a httprequest object to process the request 
	    	HttpRequest httpreq = new HttpRequest(clientSocket, clientID);
	   
	    	//create a new thread for every request received from client
	    	Thread thread = new Thread(httpreq);
	    	thread.start();
	    	clientID ++;
	    }
   
	}
	catch (UnknownHostException e) {
		System.err.println("[SERVER]> UnknownHostException for the hostname: localhost");
	} catch (IllegalArgumentException iae) {
		System.err.println("[SERVER]> EXCEPTION in starting the SERVER: " + iae.getMessage());
	}
	catch (IOException e) {
		System.err.println("[SERVER]> EXCEPTION in starting the SERVER: " + e.getMessage());
	}
	finally {
			try {
				if(serverSocket != null){
					serverSocket.close();
				}
			} catch (IOException e) {
				System.err.println("[SERVER]> EXCEPTION in closing the server socket." + e);
			}
	}
		

}
}
