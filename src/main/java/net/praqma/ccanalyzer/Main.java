package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.praqma.ccanalyzer.ConfigurationReader.Configuration;
import net.praqma.monkit.MonKit;
import net.praqma.util.debug.Logger;
import net.praqma.util.debug.Logger.LogLevel;
import net.praqma.util.debug.appenders.ConsoleAppender;
import net.praqma.util.option.Option;
import net.praqma.util.option.Options;

public class Main {

	private static Logger logger = Logger.getLogger();
	//private static StreamAppender app = new StreamAppender( System.out );
	//private static ConsoleAppender app = new ConsoleAppender();
	
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
        Option occh  = new Option( "clearcase", "C", false, 1, "The ClearCase host" );
        Option ofile = new Option( "file", "f", false, 1, "The name of the MonKit file output" );
        Option oconf = new Option( "config", "c", false, 1, "The config file, default is config.xml" );
        
        Option oview = new Option( "view", "w", false, 0, "View the configuration" );
        
        //app.setTemplate( "[%level]%space %message%newline" );
        //Logger.addAppender( app );

        o.setOption( ohost );
        o.setOption( oname );
        o.setOption( ofile );
        o.setOption( oconf );
        o.setOption( occh );
        o.setOption( osite );
        o.setOption( oasit );
        o.setOption( oview );

        o.setDefaultOptions();

        o.setSyntax( "Main [-p <port number>] [-H <list of hosts>] [-n <list of names>] [-C <host of the ClearCase server] [-c <path to config>] [-s <site>] [-S]" );
        o.setHeader( "Query a CCAnalyser server" );
        String desc = "Given a set of hosts and a set of names(there's a one to one correspondence between hosts and names),\n" +
                      "the hosts a queried for a configured set of performance and/or ClearCase counters.\n\nFx Main -H 127.0.0.1 10.10.1.83 -n localhost CC_CLIENT2 -c \"c:\\config.xml\"\n\n" +
                      "The hosts and names are given as white space separated lists.";
        o.setDescription( desc );

        o.parse( args );
        
        try {
            o.checkOptions();
        } catch( Exception e ) {
        	logger.error( "Incorrect option: " + e.getMessage() );
            o.display();
            System.exit( 1 );
        }
        
        /*
        if( o.isVerbose() ) {
        	app.setMinimumLevel( LogLevel.DEBUG );
        } else {
        	app.setMinimumLevel( LogLevel.INFO );
        }
        */
        
        Integer port = Server.defaultPort;
        if( oport.isUsed() ) {
            port = oport.getInteger();
        }

        List<String> hosts = null;
        List<String> names = null;
        if( ohost.isUsed() && oname.isUsed() ) {
        	logger.debug("Using CLI defined hosts");
	        hosts = ohost.getStrings();
	        names = oname.getStrings();
	
	        if( hosts.size() != names.size() ) {
	        	logger.error( "The number of hosts must the same as the number of names." );
	            System.exit( 1 );
	        }
	    } else {
        	logger.debug("Using config defined hosts");
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
                	List<String> hosts_ = new ArrayList<String>();
                	hosts_.addAll( hosts );
                	List<String> names_ = new ArrayList<String>();
                	names_.addAll( names );
                	                	
        			confs[i] = cr.getConfiguration( hosts_, names_, sites.get( i ) );
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
        	
            if( oview.isUsed() ) {
            	logger.info( conf.toString() );
            	continue;
            }
        	
	        /* If any hosts defined to analyze */
	        if( conf.getHostsMap().size() > 0 ) {
	        	Set<String> hostNames = conf.getHostsMap().keySet();
	            for( String hostName : hostNames ) {
	                try {
	                    PerformanceClient c = new PerformanceClient( port, conf.getHostsMap().get( hostName ), hostName, mk );
	        
	                    c.start( conf );
	                } catch( CCAnalyzerException e ) {
	                	logger.info( "Unable to connect to server: " + e.getMessage() );
	                }
	            }
	        }
	        
	        /* Do the ClearCase */
	        String ccHost = ( occh.isUsed() ? occh.getString() : conf.getClearcaseHost() );
	        if( ccHost != null ) {
	            try {
	                ClearCaseClient c = new ClearCaseClient( port, ccHost, "CC", mk );
	                
	                c.start( conf );
	            } catch( CCAnalyzerException e ) {
	            	logger.info( "Unable to connect to server: " + e.getMessage() );
	            }
	        }

        }
        
        if( !oview.isUsed() ) {
	        /* Save the MonKit file */
	        if( ofile.isUsed() ) {
	            mk.save( new File( ofile.getString() ) );
	        } else {
	            mk.save();
	        }
        }
    }

}
