package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Client {
    
    public static void main( String[] args ) throws IOException {
	Client c = new Client();
	List<PerformanceCounter> pc = new ArrayList<PerformanceCounter>();
	pc.add(new PerformanceCounter("\\Processor(_Total)\\% privileged time",10,1) );
	c.start("", pc);
    }

    public void start( String host, List<PerformanceCounter> counters ) throws IOException {
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
        
        
        out.println("version " + Server.version );
        
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
        
        for( PerformanceCounter pc : counters ) {
	    out.println(PerformanceCounterMeter.RequestType.NAMED_COUNTER.toString());
	    out.println(pc.counter);
	    out.println(pc.numberOfSamples);
	    out.println(pc.intervalTime);
	    out.println(".");

	    while ((line = in.readLine()) != null) {
		break;
	    }

	    System.out.println("Result: " + line);
        }
        
	
	out.println("exit");

	out.close();
	in.close();
	
	socket.close();
    }
}
