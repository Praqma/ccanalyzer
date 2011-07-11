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
import net.praqma.util.option.Option;
import net.praqma.util.option.Options;

public class Client {

    public static void main( String[] args ) throws IOException {

        Options o = new Options( Server.textualVersion );

        Option ohost = new Option( "host", "H", true, -1, "The host name/IP" );
        Option oname = new Option( "name", "n", true, -1, "The name/title" );
        Option oport = new Option( "port", "p", false, 1, "The port, default is 44444" );

        o.setOption( ohost );
        o.setOption( oname );
        o.setOption( oport );

        o.setDefaultOptions();

        o.setSyntax( "" );
        o.setHeader( "" );
        o.setDescription( "" );

        o.parse( args );

        try {
            o.checkOptions();
        } catch( Exception e ) {
            System.err.println( "Incorrect option: " + e.getMessage() );
            o.display();
            System.exit( 1 );
        }

        List<String> hosts = ohost.getStrings();
        List<String> names = oname.getStrings();
        
        Integer port = Server.defaultPort;
        if( oport.isUsed() ) {
            port = oport.getInteger();
        }

        if( hosts.size() != names.size() ) {
            System.err.println( "The number of hosts must the same as the number of names." );
            System.exit( 1 );
        }
        
        MonKit mk = new MonKit();

        for( int i = 0; i < hosts.size(); ++i ) {
            Client c = new Client();
            ConfigurationReader cr = new ConfigurationReader( new File( "config.xml" ) );

            c.start( port, hosts.get( i ), names.get( i ), cr.getPerformanceCounters(), mk );
        }
        // mk.save( new File( "monkit." + oname.getString() + ".xml" ) );
        mk.save();
    }

    public void start( int port, String host, String clientName, List<PerformanceCounter> counters, MonKit mk ) throws IOException {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket( host, port );
            out = new PrintWriter( socket.getOutputStream(), true );

        } catch( UnknownHostException e ) {
            System.err.println( "Unkown host " + host );
            System.exit( 1 );
        } catch( IOException e ) {
            System.err.println( "Couldn't get I/O for the connection to: " + host );
            System.exit( 1 );
        }

        in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        String line = "";

        /* Super simple handshaking.... */
        out.println( "version " + Server.version );
        while( ( line = in.readLine() ) != null ) {
            break;
        }
        if( line.equals( "0" ) ) {
            System.err.println( "Version mismatch!" );
            throw new PerformanceCounterException( "Version mismatch" );
        }

        for( PerformanceCounter pc : counters ) {
            out.println( PerformanceCounterMeter.RequestType.NAMED_COUNTER.toString() );
            out.println( pc.counter );
            out.println( pc.numberOfSamples );
            out.println( pc.intervalTime );
            out.println( "." );

            while( ( line = in.readLine() ) != null ) {
                break;
            }

            System.out.println( pc.name + ": " + line + " " + pc.scale );

            mk.addCategory( pc.name, pc.scale );

            mk.add( clientName, line, pc.name );
        }

        out.println( "exit" );

        out.close();
        in.close();

        socket.close();
    }
}
