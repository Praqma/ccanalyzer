package net.praqma.ccanalyzer;

import java.io.IOException;

import net.praqma.ccanalyzer.ConfigurationReader.Configuration;
import net.praqma.monkit.MonKit;

public class ConfigurationRunner {
    
    Configuration cr;
    
    public ConfigurationRunner( Configuration cr ) throws IOException {
        this.cr = cr;
    }
    
    public void run( MonKit mk, String caption ) {
        
        System.out.println( "Running for " + caption );
        
        /* Performance counters */
        for( PerformanceCounterConfiguration pc : cr.getPerformanceCounters("") ) { // TODO not a host name

            String result = PerformanceCounterMeter.parseRequest( pc );

            System.out.println( pc.host + ": " + result + " " + pc.scale );

            mk.addCategory( pc.host, pc.scale );

            mk.add( caption, result, pc.host );
        }
    }
}
