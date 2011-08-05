package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.praqma.monkit.MonKit;

public class PerformanceClient extends AbstractClient {

    public PerformanceClient( int port, String host, String clientName, MonKit mk ) {
        super( port, host, clientName, mk );
    }

    protected void perform( ConfigurationReader counters, PrintWriter out, BufferedReader in ) throws IOException {
        
        String line = "";
        
        System.out.println( "Obtaining Performance information" );
        
        /* Get the performance counters */
        for( PerformanceCounterConfiguration pc : counters.getPerformanceCounters( host ) ) {
            out.println( PerformanceCounterMeter.RequestType.NAMED_COUNTER.toString() );
            out.println( pc.counter );
            out.println( pc.numberOfSamples );
            out.println( pc.intervalTime );
            out.println( "." );
            
            System.out.print( "* " + pc.name + ": " );

            while( ( line = in.readLine() ) != null ) {
                break;
            }

            System.out.println( line + " " + pc.scale );

            monkit.addCategory( pc.name, pc.scale );

            monkit.add( this.clientName, line, pc.name );
        }
    }
    
}
