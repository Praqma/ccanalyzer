package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.praqma.clearcase.Cool;
import net.praqma.clearcase.Cool.ContextType;
import net.praqma.monkit.MonKit;
import net.praqma.util.debug.PraqmaLogger;
import net.praqma.util.debug.PraqmaLogger.Logger;
import net.praqma.util.option.Option;
import net.praqma.util.option.Options;

public class Main {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
	
        Options o = new Options( Server.textualVersion );

        Option ohost = new Option( "host", "H", true, -1, "The host name/IP" );
        Option oname = new Option( "name", "n", true, -1, "The name/title" );
        Option ofile = new Option( "file", "f", false, 1, "The name of the MonKit file output" );
        Option oconf = new Option( "config", "c", false, 1, "The config file, default is config.xml" );

        o.setOption( ohost );
        o.setOption( oname );
        o.setOption( ofile );
        o.setOption( oconf );

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

        if( hosts.size() != names.size() ) {
            System.err.println( "The number of hosts must the same as the number of names." );
            System.exit( 1 );
        }

        MonKit mk = new MonKit();
        
        ConfigurationReader cr = null;
        
        if( oconf.used ) {
            cr = new ConfigurationReader( new File( oconf.getString() ) );
        } else {
            cr = new ConfigurationReader( new File( "config.xml" ) );
        }

        /* If any hosts defined to analyze */
        if( hosts.size() > 0 ) {
            for( int i = 0; i < hosts.size(); ++i ) {
                Client c = new Client();
    
                c.start( hosts.get( i ), names.get( i ), cr.getPerformanceCounters(), mk );
            }
        }
        
        /* Do the ClearCase */
        
        
        /* Save the MonKit file */
        if( ofile.used ) {
            mk.save( new File( ofile.getString() ) );
        } else {
            mk.save();
        }
    }

}
