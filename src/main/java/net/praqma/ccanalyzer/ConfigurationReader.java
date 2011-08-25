package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import net.praqma.util.debug.Logger;
import net.praqma.util.xml.XML;

public class ConfigurationReader extends XML implements Serializable {

    private static final long serialVersionUID = 3648821402865625037L;
    
    private static Logger logger = Logger.getLogger();

    //List<ClearCaseCounterConfiguration> ccounters = new ArrayList<ClearCaseCounterConfiguration>();
    //Map<String, Map<String, PerformanceCounterConfiguration>> config = new HashMap<String, Map<String, PerformanceCounterConfiguration>>();
    
    //List<PerformanceCounterConfiguration> pcounters = new ArrayList<PerformanceCounterConfiguration>();
    //Map<String, List<PerformanceCounterConfiguration>> config = new HashMap<String, List<PerformanceCounterConfiguration>>();

    
    public ConfigurationReader() {
    }

    public ConfigurationReader( File conf ) throws IOException {
        super( conf );
        
        //initialize();
        //System.out.println("XML= " + this.getXML() );
    }
    
    public List<String> getSites() {
		Element esites = null;
		try {
			esites = getFirstElement( "sites" );
		} catch( Exception e ) {
			throw new CCAnalyzerException( "<sites> does not exist." );
		}
		
        List<Element> esiteList = getElements( esites, "site" );
        List<String> sites = new ArrayList<String>();
        for(Element e : esiteList) {
        	sites.add( e.getAttribute( "name" ) );
        }
        
        return sites;
    }
    
    public Configuration getConfiguration( List<String> hosts, List<String> names, String site ) throws CCAnalyzerException {
        
    	String ccHost = null;
    	Configuration p = new Configuration(site);
    	
        /* Not given, find the config hosts */
        if( hosts.size() == 0 ) {
        	if( site == null ) {
        		Element ehs = null;
        		try {
        			ehs = getFirstElement( "hosts" );
        		} catch( Exception e ) {
        			throw new CCAnalyzerException( "No site or hosts given, but <hosts> did not exist." );
        		}
        		
        		ccHost = ehs.getAttribute( "clearcase" );
        		
	            List<Element> ehosts = getElements( ehs );
	            for(Element e : ehosts) {
	            	hosts.add( e.getTextContent() );
	            	names.add( e.getAttribute( "name" ) );
	            }
        	} else {
        		Element esites = null;
        		try {
        			esites = getFirstElement( "sites" );
        		} catch( Exception e ) {
        			throw new CCAnalyzerException( "<sites> does not exist." );
        		}
        		
	            List<Element> esitesList = getElementsWithAttribute( esites, "name", site );
	            if( esitesList.size() == 0 ) {
	            	throw new CCAnalyzerException( "Unkown site " + site );
	            }
	            
	            ccHost = esitesList.get( 0 ).getAttribute( "clearcase" );
	            
	            List<Element> esiteList = getElements( esitesList.get( 0 ) );
	            for(Element e : esiteList) {
	            	hosts.add( e.getTextContent() );
	            	names.add( e.getAttribute( "name" ) );

	            }
        	}
        }
        
        

        try {
            Element ccs = getFirstElement( "clearcase" );
            List<Element> elements = getElements( ccs );
    
            for( Element e : elements ) {
                String name = e.getAttribute( "name" );
                String scale = e.getAttribute( "scale" );
                String counter = e.getTextContent();
    
                p.ccounters.add( new ClearCaseCounterConfiguration( name, scale, counter ) );
            }
        } catch( Exception e ) {
        	/* No clearcase stuff */
        }
        
        for( String host : hosts ) {
            //System.out.println( "Adding host " + host );
        	p.config.put( host, new HashMap<String, PerformanceCounterConfiguration>() );
        }

        /* Get the general performance counters */
        try {
            Element pcs = getFirstElement( "performance" );
            try {
                Element gpcs = getFirstElement( pcs, "general" );
                List<Element> pelements = getElements( gpcs );
        
                for( Element e : pelements ) {
                    String name = e.getAttribute( "name" );
                    String scale = e.getAttribute( "scale" );
                    String counter = e.getTextContent();
        
                    String samples = e.getAttribute( "samples" );
                    int ns = 1;
                    if( !samples.equals( "" ) ) {
                        ns = Integer.parseInt( samples );
                    }
        
                    String interval = e.getAttribute( "interval" );
                    int i = 1;
                    if( !interval.equals( "" ) ) {
                        i = Integer.parseInt( interval );
                    }
        
                    for( String host : hosts ) {
                    	p.config.get( host ).put( name, new PerformanceCounterConfiguration( name, scale, counter, ns, i ) );
                    }
                }
            } catch( Exception e ) {
                /* No op */
            }
            
            /* Get the specific performance counters */
            try {
                Element spcs = getFirstElement( pcs, "specific" );
                List<Element> hostElements = getElements( spcs );
        
                /* For all hosts */
                for( Element e : hostElements ) {
                    
                    List<Element> he = getElements( e );
                    String host = e.getAttribute( "host" );
                    
                    logger.debug( "Specific host: " + host );
        
                    if( p.config.get( host ) == null ) {
                        //System.out.println( "Adding host " + host );
                    	p.config.put( host, new HashMap<String, PerformanceCounterConfiguration>() );
                    }
                    
                    /* For all counters in host */
                    for( Element hostCounter : he ) {
                        String name = hostCounter.getAttribute( "name" );
                        String scale = hostCounter.getAttribute( "scale" );
                        String counter = hostCounter.getTextContent();
                        
                        logger.debug( "Counter name: " + name );
            
                        String samples = hostCounter.getAttribute( "samples" );
                        int ns = 1;
                        if( !samples.equals( "" ) ) {
                            ns = Integer.parseInt( samples );
                        }
            
                        String interval = e.getAttribute( "interval" );
                        int i = 1;
                        if( !interval.equals( "" ) ) {
                            i = Integer.parseInt( interval );
                        }
            
                        p.config.get( host ).put( name, new PerformanceCounterConfiguration( name, scale, counter, ns, i ) );
                    }
                }
            } catch( Exception e ) {
                /* No op */
            }
        } catch( Exception e ) {
            /* No op */
        }
        
        if( ccHost != null && ccHost.length() > 0 ) {
        	p.clearcaseHost = ccHost;
        }
        
        p.hosts = hosts;
        p.names = names;
        
        return p;
    }
    
    public class Configuration{
        List<ClearCaseCounterConfiguration> ccounters = new ArrayList<ClearCaseCounterConfiguration>();
        Map<String, Map<String, PerformanceCounterConfiguration>> config = new HashMap<String, Map<String, PerformanceCounterConfiguration>>();
        
        List<String> hosts = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        
        String clearcaseHost = null;
        
        private String site;
        
        public Configuration( String site ) {
        	this.site = site;
        }
        
        public List<PerformanceCounterConfiguration> getPerformanceCounters( String host ) {
            Map<String, PerformanceCounterConfiguration> c = config.get( host );
            return new ArrayList<PerformanceCounterConfiguration>(c.values());
        }
        
        public List<ClearCaseCounterConfiguration> getClearCaseCounters() {
            return ccounters;
        }
        
        public List<String> getHosts() {
        	return hosts;
        }
        
        public List<String> getNames() {
        	return names;
        }
        
        public String getClearcaseHost() {
        	return clearcaseHost;
        }
        
        public String getSite() {
        	return site;
        }
    }

    /*
    public List<PerformanceCounterConfiguration> getPerformanceCounters( String host ) {
        Map<String, PerformanceCounterConfiguration> c = config.get( host );
        return new ArrayList<PerformanceCounterConfiguration>(c.values());
    }
    
    public List<ClearCaseCounterConfiguration> getClearCaseCounters() {
        return ccounters;
    }
    */
    
    /*
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for( PerformanceCounterConfiguration pc : pcounters ) {
            sb.append( pc.toString() + " - " );
        }
        
        return sb.toString();
    }
    */
    
    
//    
//    public void addPerformanceCounters( List<PerformanceCounterConfiguration> pcs ) {
//        /* Check the performance counters */
//        for( PerformanceCounterConfiguration pc1 : pcs ) {
//            boolean same = false;
//            for( PerformanceCounterConfiguration pc2 : pcounters ) {
//                /* TODO check for more than the name? The actual counter perhaps? */
//                if( pc1.name.equals( pc2.name ) ) {
//                    same = true;
//                    break;
//                }
//            }
//            
//            if( !same ) {
//                pcounters.add( pc1 );
//            }
//        }
//    }
//    
//    
//    public void addClearCaseCounters( List<ClearCaseCounterConfiguration> ccs ) {
//        /* Check the ClearCase counters */
//        for( ClearCaseCounterConfiguration cc1 : ccs ) {
//            boolean same = false;
//            for( ClearCaseCounterConfiguration cc2 : ccounters ) {
//                /* TODO check for more than the name? The actual counter perhaps? */
//                if( cc1.getName().equals( cc2.getName() ) ) {
//                    same = true;
//                    break;
//                }
//            }
//            
//            if( !same ) {
//                ccounters.add( cc1 );
//            }
//        }
//    }
//    
//    public void addFrom( ConfigurationReader cr ) {
//        
//        /* Check the performance counters */
//        for( PerformanceCounterConfiguration pc1 : cr.getPerformanceCounters( "" ) ) { // TODO This shold be a host, not an empty string
//            boolean same = false;
//            for( PerformanceCounterConfiguration pc2 : pcounters ) {
//                /* TODO check for more than the name? The actual counter perhaps? */
//                if( pc1.name.equals( pc2.name ) ) {
//                    same = true;
//                    break;
//                }
//            }
//            
//            if( !same ) {
//                pcounters.add( pc1 );
//            }
//        }
//        
//        /* Check the ClearCase counters */
//        for( ClearCaseCounterConfiguration cc1 : cr.getClearCaseCounters() ) {
//            boolean same = false;
//            for( ClearCaseCounterConfiguration cc2 : ccounters ) {
//                /* TODO check for more than the name? The actual counter perhaps? */
//                if( cc1.getName().equals( cc2.getName() ) ) {
//                    same = true;
//                    break;
//                }
//            }
//            
//            if( !same ) {
//                ccounters.add( cc1 );
//            }
//        }
//    }
    


}
