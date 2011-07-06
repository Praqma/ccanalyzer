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
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(host, 4444);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            
        } catch (UnknownHostException e) {
            System.err.println( "Unkown host " + host );
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
            System.exit(1);
        }
        
        /* Write first */
        out.println("HEJ");
        out.println(".");

        in = new BufferedReader(new InputStreamReader( echoSocket.getInputStream() ) );
        
        String line = in.readLine();
        
	while( ( line = in.readLine()) != null ) {
	    out.println("I got:" + line);
	    break;
	}
        
        System.out.println( "Result: " + line );

	out.close();
	in.close();
	
	echoSocket.close();
    }
}
