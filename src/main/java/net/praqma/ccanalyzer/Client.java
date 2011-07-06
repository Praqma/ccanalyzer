package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    
    public static void main( String[] args ) throws IOException {
	Client c = new Client();
	c.start("");
    }

    public void start( String host ) throws IOException {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(host, Server.port);
            out = new PrintWriter(socket.getOutputStream(), true);
            
        } catch (UnknownHostException e) {
            System.err.println( "Unkown host " + host );
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
            System.exit(1);
        }
        
        in = new BufferedReader(new InputStreamReader( socket.getInputStream() ) );
        
        String line = "";
        
        /* Write first */
        /*
        out.println(PerformanceCounter.RequestType.SHORT_HAND_COUNTER.toString());
        out.println("getFreeSpace");
        out.println("C:");
        out.println(".");

	while( ( line = in.readLine()) != null ) {
	    break;
	}
        
        System.out.println( "Result: " + line );
        */
        
        /*
        out.println(PerformanceCounter.RequestType.SHORT_HAND_COUNTER.toString());
        out.println("getPagesPerSecond");
        out.println("1");
        out.println("1");
        out.println(".");

	while( ( line = in.readLine()) != null ) {
	    break;
	}
        
        System.out.println( "Result: " + line );
        */
        
        
        out.println(PerformanceCounter.RequestType.NAMED_COUNTER.toString());
        out.println("\\Processor(_Total)\\% privileged time");
        out.println("10");
        out.println("1");
        out.println(".");

	while( ( line = in.readLine()) != null ) {
	    break;
	}
        
        System.out.println( "Result: " + line );
        
	
	out.println("exit");

	out.close();
	in.close();
	
	socket.close();
    }
}
