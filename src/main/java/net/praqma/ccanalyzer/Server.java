package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static int port = 4444;
    private int counter = 0;
    
    public static void main( String[] args ) {
	Server s = new Server();
	s.start();
    }
    
    public Server() {
	
    }
    
    public void start() {
	try {
	    ServerSocket listener = new ServerSocket( port );
	    Socket server;
	    
	    System.out.println( "Server started." );
	    
	    while(true) {
	    //while( counter < 1 ) {
		server = listener.accept();
		System.out.println( "Accepted client" );
		T connection = new T( server );
		Thread t = new Thread( connection );
		t.start();
		t.join();
		counter++;
	    }
	} catch( Exception e) {
	    System.err.println( "Something went wrong: " + e );
	}
	
	System.out.println( "OUT." );
    }
    
    public class T implements Runnable {

	private Socket server;

	public T(Socket server) {
	    this.server = server;
	}

	public void run() {
	    String line, input = "";
	    try {
		// Get input from the client
		PrintWriter out = new PrintWriter(server.getOutputStream());
		BufferedReader br = new BufferedReader(	new InputStreamReader( server.getInputStream() ));
		while ((line = br.readLine()) != null && !line.equals(".")) {
		    input = input + line;
		}
		// Now write to the client

		out.println("--->Overall message is:" + input);
		
		

		server.close();
	    } catch (IOException ioe) {
		System.out.println("IOException on socket listen: " + ioe);
		ioe.printStackTrace();
	    }
	    
	    System.out.println( "THREAD DONE!" );
	}

    }
}
