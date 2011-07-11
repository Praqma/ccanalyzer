package net.praqma.ccanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import net.praqma.monkit.MonKit;

public class ClearCaseClient extends AbstractClient {

    public ClearCaseClient( int port, String host, String clientName, MonKit mk ) {
        super( port, host, clientName, mk );
    }

    protected void perform( ConfigurationReader counters, PrintWriter out, BufferedReader in ) throws IOException {
        
        String line = "";
        
        /* Get the performance counters */
        for( ClearCaseCounterConfiguration ccc : counters.getClearCaseCounters() ) {
            out.println( PerformanceCounterMeter.RequestType.NAMED_COUNTER.toString() );
            out.println( ccc.counter );
            out.println( "." );

            while( ( line = in.readLine() ) != null ) {
                break;
            }

            System.out.println( ccc.name + ": " + line + " " + ccc.scale );

            monkit.addCategory( ccc.name, ccc.scale );

            monkit.add( this.clientName, line, ccc.name );
        }
    }
    
}
