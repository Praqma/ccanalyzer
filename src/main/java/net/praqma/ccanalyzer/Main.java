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

        Option oport = new Option( "port", "p", false, 1, "The port, default is 44444" );
        Option ohost = new Option( "host", "H", true, -1, "The host name/IP" );
        Option oname = new Option( "name", "n", true, -1, "The name/title" );
        Option occ   = new Option( "clearcase", "C", false, -1, "The ClearCase host" );
        Option ofile = new Option( "file", "f", false, 1, "The name of the MonKit file output" );
        Option oconf = new Option( "config", "c", false, 1, "The config file, default is config.xml" );

        o.setOption( ohost );
        o.setOption( oname );
        o.setOption( ofile );
        o.setOption( oconf );
        o.setOption( occ );

        o.setDefaultOptions();

        o.setSyntax( "Main [-p <port number>] [-H <list of hosts>] [-n <list of names>] [-C <host of the ClearCase server] [-c <path to config>]" );
        o.setHeader( "Query a CCAnalyser server" );
        String desc = "Given a set of hosts and a set of names(there's a one to one correspondence between hosts and names),\n" +
                      "the hosts a queried for a configured set of performance and/or ClearCase counters.\n\nFx Main -H 127.0.0.1 10.10.1.83 -n localhost CC_CLIENT2 -c \"c:\\config.xml\"\n\n" +
                      "The hosts and names are given as white space separated lists.";
        o.setDescription( desc );

        o.parse( args );

        try {
            o.checkOptions();
        } catch( Exception e ) {
            System.err.println( "Incorrect option: " + e.getMessage() );
            o.display();
            System.exit( 1 );
        }
        
        Integer port = Server.defaultPort;
        if( oport.isUsed() ) {
            port = oport.getInteger();
        }

        List<String> hosts = ohost.getStrings();
        List<String> names = oname.getStrings();

        if( hosts.size() != names.size() ) {
            System.err.println( "The number of hosts must the same as the number of names." );
            System.exit( 1 );
        }

        MonKit mk = new MonKit();
        
        ConfigurationReader cr = null;
        
        if( oconf.isUsed() ) {
            cr = new ConfigurationReader( new File( oconf.getString() ) );
        } else {
            cr = new ConfigurationReader( new File( "config.xml" ) );
        }
        
        cr.initialize( hosts, "" );

        /* If any hosts defined to analyze */
        if( hosts.size() > 0 ) {
            for( int i = 0; i < hosts.size(); ++i ) {
                PerformanceClient c = new PerformanceClient( port, hosts.get( i ), names.get( i ), mk );
    
                c.start( cr );
            }
        }
        
        /* Do the ClearCase */
        if( occ.isUsed() ) {
            ClearCaseClient c = new ClearCaseClient( port, occ.getString(), "CC", mk );
            
            c.start( cr );
        }
        
        /* Save the MonKit file */
        if( ofile.isUsed() ) {
            mk.save( new File( ofile.getString() ) );
        } else {
            mk.save();
        }
    }

}
