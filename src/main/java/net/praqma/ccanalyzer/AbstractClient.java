package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.praqma.ccanalyzer.ConfigurationReader.Configuration;
import net.praqma.monkit.MonKit;
import net.praqma.util.debug.Logger;

public abstract class AbstractClient {
	
	private static Logger logger = Logger.getLogger();

    protected int port;
    protected String host;
    protected String clientName;
    protected MonKit monkit;
    
    public AbstractClient( int port, String host, String clientName, MonKit mk ) {
        this.port = port;
        this.host = host;
        this.clientName = clientName;
        this.monkit = mk;
        
        logger.info( "CCAnalyzer client version " + Server.version );
    }
        

    public void start( Configuration counters ) throws IOException {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        logger.info( "Trying to connect to " + host );

        try {
            socket = new Socket( host, port );
            out = new PrintWriter( socket.getOutputStream(), true );

        } catch( UnknownHostException e ) {
        	logger.warning( "\rError, unkown host " + host + "\n" );
            return;
        } catch( IOException e ) {
        	logger.warning( "\rError, unable to connect to " + host + "\n" );
            return;
        }

        in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        String line = "";

        /* Super simple handshaking.... */
        out.println( "version " + Server.version );
        while( ( line = in.readLine() ) != null ) {
            break;
        }
        if( !line.equals( Integer.toString( Server.version ) ) ) {
        	logger.warning( "\rError, version mismatch at " + host + "\n" );
            
            out.close();
            in.close();
            socket.close();
            
            throw new CCAnalyzerException( "Version mismatch, got " + line + " expected " + Server.version );
        }
        
        logger.info( "Successfully connected to " + host );
        
        /* Do the counting */
        perform( counters, out, in );
        
        out.println( "exit" );

        out.close();
        in.close();

        socket.close();
        
        logger.info( "Disconnected\n" );
    }
    
    protected abstract void perform( Configuration counters, PrintWriter out, BufferedReader in ) throws IOException;

}
