package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.praqma.ccanalyzer.ConfigurationReader.Configuration;
import net.praqma.monkit.MonKit;
import net.praqma.util.debug.Logger;
import net.praqma.util.debug.Logger.LogLevel;
import net.praqma.util.option.Option;
import net.praqma.util.option.Options;

public class Main {

	private static Logger logger = Logger.getLogger();
	
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
	
        Options o = new Options( Server.textualVersion );

        Option oport = new Option( "port", "p", false, 1, "The port, default is 44444" );
        Option ohost = new Option( "host", "H", false, -1, "The host name/IP" );
        Option oname = new Option( "name", "n", false, -1, "The name/title" );
        Option osite = new Option( "site", "s", false, 1, "Use a named configuration defined site" );
        Option oasit = new Option( "sites", "S", false, 0, "Execute CCAnalyzer using all configured sites" );
        Option occ   = new Option( "clearcase", "C", false, 1, "The ClearCase host" );
        Option ofile = new Option( "file", "f", false, 1, "The name of the MonKit file output" );
        Option oconf = new Option( "config", "c", false, 1, "The config file, default is config.xml" );

        o.setOption( ohost );
        o.setOption( oname );
        o.setOption( ofile );
        o.setOption( oconf );
        o.setOption( occ );
        o.setOption( osite );
        o.setOption( oasit );

        o.setDefaultOptions();

        o.setSyntax( "Main [-p <port number>] [-H <list of hosts>] [-n <list of names>] [-C <host of the ClearCase server] [-c <path to config>] [-s <site>] [-S]" );
        o.setHeader( "Query a CCAnalyser server" );
        String desc = "Given a set of hosts and a set of names(there's a one to one correspondence between hosts and names),\n" +
                      "the hosts a queried for a configured set of performance and/or ClearCase counters.\n\nFx Main -H 127.0.0.1 10.10.1.83 -n localhost CC_CLIENT2 -c \"c:\\config.xml\"\n\n" +
                      "The hosts and names are given as white space separated lists.";
        o.setDescription( desc );

        o.parse( args );
        
        logger.toStdOut( true );
        
        if( o.isVerbose() ) {
        	logger.setMinLogLevel( LogLevel.DEBUG );
        } else {
        	logger.setMinLogLevel( LogLevel.INFO );
        }

        try {
            o.checkOptions();
        } catch( Exception e ) {
        	logger.error( "Incorrect option: " + e.getMessage() );
            o.display();
            System.exit( 1 );
        }
        
        Integer port = Server.defaultPort;
        if( oport.isUsed() ) {
            port = oport.getInteger();
        }

        List<String> hosts = null;
        List<String> names = null;
        if( ohost.isUsed() && oname.isUsed() ) {
        	if( o.isVerbose() ) logger.info("Using CLI defined hosts");
	        hosts = ohost.getStrings();
	        names = oname.getStrings();
	
	        if( hosts.size() != names.size() ) {
	        	logger.error( "The number of hosts must the same as the number of names." );
	            System.exit( 1 );
	        }
	    } else {
        	if( o.isVerbose() ) logger.info("Using config defined hosts");
        	hosts = new ArrayList<String>();
        	names = new ArrayList<String>();
        }

        MonKit mk = new MonKit();
        
        ConfigurationReader cr = null;
        
        if( oconf.isUsed() ) {
        	if( o.isVerbose() ) logger.info("Using configuration file " + oconf.getString());
            cr = new ConfigurationReader( new File( oconf.getString() ) );
        } else {
            cr = new ConfigurationReader( new File( "config.xml" ) );
        }
        
        Configuration[] confs = null;

        try {
        	if( oasit.isUsed() ) {
        		List<String> sites = cr.getSites();
        		confs = new Configuration[sites.size()];
        		logger.info( "Sites: " + cr.getSites() );
        		for( int i = 0 ; i < sites.size() ; i++ ) {
                	hosts = new ArrayList<String>();
                	names = new ArrayList<String>();
                	
        			confs[i] = cr.getConfiguration( hosts, names, sites.get( i ) );
        		}
        		
        	} else {
        		confs = new Configuration[1];
        		confs[0] = cr.getConfiguration( hosts, names, osite.getString() );
        	}
        } catch( CCAnalyzerException e ) {
        	logger.error( "Could not initialize configuration: " + e.getMessage() );
            System.exit( 1 );
        }
        
        for( Configuration conf : confs ) {
	        
        	if( conf.getSite() != null ) {
        		logger.info( "Site: " + conf.getSite() + "\n" );
        	}
        	
	        /* If any hosts defined to analyze */
	        if( hosts.size() > 0 ) {
	            for( int i = 0; i < hosts.size(); ++i ) {
	                try {
	                    PerformanceClient c = new PerformanceClient( port, conf.getHosts().get( i ), conf.getNames().get( i ), mk );
	        
	                    c.start( conf );
	                } catch( CCAnalyzerException e ) {
	                	logger.info( "Unable to connect to server: " + e.getMessage() );
	                }
	            }
	        }
	        
	        /* Do the ClearCase */
	        String ccHost = ( occ.isUsed() ? occ.getString() : conf.getClearcaseHost() );
	        if( ccHost != null ) {
	            try {
	                ClearCaseClient c = new ClearCaseClient( port, ccHost, "CC", mk );
	                
	                c.start( conf );
	            } catch( CCAnalyzerException e ) {
	            	logger.info( "Unable to connect to server: " + e.getMessage() );
	            }
	        }

        }
        
        /* Save the MonKit file */
        if( ofile.isUsed() ) {
            mk.save( new File( ofile.getString() ) );
        } else {
            mk.save();
        }
    }

}
