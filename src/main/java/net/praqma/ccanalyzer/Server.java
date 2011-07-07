package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    public static int port = 4444;
    private int counter = 0;
    public static int version = 1;
    public static String textualVersion = "0.1.0";
    
    private static Pattern rx_version = Pattern.compile("^version (\\d+)");

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
		server = listener.accept();
		System.out.println("Accepted client from " + server);
		T connection = new T(server);
		Thread t = new Thread(connection);
		t.start();
		t.join();
		counter++;
	    }
	} catch (Exception e) {
	    System.err.println("Something went wrong: " + e);
	}

	System.out.println("Server is stopping.");
    }

    public class T implements Runnable {

	private Socket server;
	
	private PrintWriter out = null;
	private BufferedReader in = null;

	public T(Socket server) {
	    this.server = server;
	}
	
	public boolean getVersion() {
	    String line;
	    try {
		while((line = in.readLine()) != null && !line.matches("^version \\d+")) {
		}
		
		Matcher m = rx_version.matcher(line);
		if( m.find() ) {
		    int v = Integer.parseInt(m.group(1));
		    return v == version;
		} else {
		    return false;
		}
		
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    
	    return false;
	}

	public void run() {
	    String line;
	    List<String> request = new ArrayList<String>();

	    try {
		// Get input from the client
		out = new PrintWriter(server.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader( server.getInputStream()));
		
		if( !getVersion() ) {
		    System.err.println("Client version mismatch");
		    out.println(0);
		    return;
		}
		out.println(1);
		
		boolean running = true;
		while (running) {
		    request.clear();
		    while ((line = in.readLine()) != null && !line.equals(".")) {
			if (line.equalsIgnoreCase("exit")) {
			    System.out.println("Client quitting");
			    running = false;
			    break;
			}

			request.add(line);
		    }
		    
		    if( !running || request.size() == 0 ) {
			break;
		    }

		    /* Parse request */
		    String r = PerformanceCounterMeter.parseRequest(request);

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
		    /* No op */
		}
	    }
	}

    }
}
