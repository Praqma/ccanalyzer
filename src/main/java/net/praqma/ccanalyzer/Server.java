package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static int port = 4444;
    private int counter = 0;

    public static void main(String[] args) {
	Server s = new Server();
	s.start();
    }

    public Server() {

    }

    public void start() {
	try {
	    ServerSocket listener = new ServerSocket(port);
	    Socket server;

	    System.out.println("Server started.");

	    while (true) {
		// while( counter < 1 ) {
		server = listener.accept();
		System.out.println("Accepted client");
		T connection = new T(server);
		Thread t = new Thread(connection);
		t.start();
		t.join();
		counter++;
	    }
	} catch (Exception e) {
	    System.err.println("Something went wrong: " + e);
	}

	System.out.println("OUT.");
    }

    public class T implements Runnable {

	private Socket server;

	public T(Socket server) {
	    this.server = server;
	}

	public void run() {
	    String line;
	    List<String> request = new ArrayList<String>();

	    PrintWriter out = null;
	    BufferedReader in = null;

	    try {
		// Get input from the client
		out = new PrintWriter(server.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader( server.getInputStream()));
		boolean running = true;
		while (running) {
		    request.clear();
		    while ((line = in.readLine()) != null && !line.equals(".")) {
			if (line.equalsIgnoreCase("exit")) {
			    System.out.println("Breaking....");
			    running = false;
			    break;
			}
			System.out.println("LINE: " + line);
			request.add(line);
		    }
		    
		    if( !running || request.size() == 0 ) {
			break;
		    }

		    /* Parse request */
		    String r = PerformanceCounter.parseRequest(request);

		    /* Reply */
		    out.println(r);
		}

	    } catch (IOException ioe) {
		System.err.println("IOException on socket listen: " + ioe);
		ioe.printStackTrace();
	    } finally {
		try {
		    server.close();
		    in.close();
		    out.close();
		} catch (IOException e) {
		}
	    }

	    System.out.println("THREAD DONE!");
	}

    }
}
