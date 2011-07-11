package net.praqma.ccanalyzer;

import java.io.IOException;

import net.praqma.monkit.MonKit;

public class ConfigurationRunner {
    
    ConfigurationReader cr;
    
    public ConfigurationRunner( ConfigurationReader cr ) throws IOException {
        this.cr = cr;
    }
    
    public void run( MonKit mk, String caption ) {
        
        System.out.println( "Running for " + caption );
        
        /* Performance counters */
        for( PerformanceCounterConfiguration pc : cr.getPerformanceCounters("") ) { // TODO not a host name

            String result = PerformanceCounterMeter.parseRequest( pc );

            System.out.println( pc.name + ": " + result + " " + pc.scale );

            mk.addCategory( pc.name, pc.scale );

            mk.add( caption, result, pc.name );
        }
    }
}
