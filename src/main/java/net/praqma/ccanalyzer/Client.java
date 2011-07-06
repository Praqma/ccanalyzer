package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import net.praqma.monkit.MonKit;

public class Client {
    
    public static void main( String[] args ) throws IOException {
	Client c = new Client();
	//List<PerformanceCounter> pc = new ArrayList<PerformanceCounter>();
	//pc.add(new PerformanceCounter("\\Processor(_Total)\\% privileged time",10,1) );
	ConfigurationReader cr = new ConfigurationReader(new File( "config.xml") );
	MonKit mk = new MonKit();
	c.start("", "Wolles", cr.getCounters(), mk);
	c.start("", "Praqma", cr.getCounters(), mk);
	mk.save();
    }

    public void start( String host, String clientName, List<PerformanceCounter> counters, MonKit mk ) throws IOException {
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
        
        /* Super simple handshaking.... */
        out.println("version " + Server.version );
	while((line = in.readLine()) != null) {
	    break;
	}
        if( line.equals("0") ) {
            System.err.println("Version mismatch!");
            throw new PerformanceCounterException("Version mismatch");
        }

        
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
	    
	    mk.addCategory(pc.name, pc.scale);
	    
	    mk.add(clientName, line, pc.name);
        }
        
	
	out.println("exit");

	out.close();
	in.close();
	
	socket.close();
    }
}
