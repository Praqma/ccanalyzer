package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import net.praqma.util.debug.Logger;
import net.praqma.util.xml.XML;

public class ConfigurationReader extends XML implements Serializable {

    private static final long serialVersionUID = 3648821402865625037L;
    
    private static Logger logger = Logger.getLogger();
    
    public ConfigurationReader() {
    }

    public ConfigurationReader( File conf ) throws IOException {
        super( conf );
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
    	//  name  , host
    	Map<String, String> hostsmap = new HashMap<String, String>();
    	
        /* Not given, find the config hosts */
        if( hosts.isEmpty()) {
    		Element ehs = null;
    		try {
    			ehs = getFirstElement( "hosts" );
    		} catch( Exception e ) {
    			throw new CCAnalyzerException( "No site or hosts given, but <hosts> did not exist." );
    		}
    		
    		ccHost = ehs.getAttribute( "clearcase" );
    		
            List<Element> ehosts = getElements( ehs );
            for(Element e : ehosts) {
            	hostsmap.put( e.getAttribute( "name" ), e.getTextContent() );
            }
        }

        /* Generate host map */
    	for( int i = 0 ; i < hosts.size() ; ++i ) {
    		hostsmap.put( names.get( i ), hosts.get( i ) );
    	}
    	
    	logger.debug( hostsmap );
            
        /* Generate current host map given a site */
    	if( site != null ) {
			Element esites = null;
			try {
				esites = getFirstElement( "sites" );
			} catch( Exception e ) {
				throw new CCAnalyzerException( "<sites> does not exist." );
			}
			
	        List<Element> esitesList = getElementsWithAttribute( esites, "name", site );
	        if( esitesList.isEmpty()) {
	        	throw new CCAnalyzerException( "Unkown site " + site );
	        }
	        
	        ccHost = esitesList.get( 0 ).getAttribute( "clearcase" );
	        
	        /* Get regions */
	        String regions = esitesList.get( 0 ).getAttribute( "regions" );
	        if( regions.length() > 0 ) {
		        String[] rs = regions.split( "\\s*(,|;|:)\\s*" );
		        p.getRegions().addAll( Arrays.asList( rs ) );
		        logger.debug( "Region list: " + p.getRegions() );
	        } else {
	        	logger.debug( "No regions specified" );
	        }
	        
	        List<Element> esiteList = getElements( esitesList.get( 0 ) );
			for( Element e : esiteList ) {
				String h = e.getTextContent();
				if( hostsmap.containsKey( h ) ) {
					p.addHost( h, hostsmap.get( h ) );
				} else {
					logger.warning( "Undefined host " + h );
				}
			}
    	} else {
    		Set<String> keys = hostsmap.keySet();
    		for( String key : keys ) {
    			p.addHost( key, hostsmap.get( key ) );
    		}
    		
    	}
        
        

        try {
            Element ccs = getFirstElement( "clearcase" );
            List<Element> elements = getElements( ccs );
    
            for( Element e : elements ) {
                String name = e.getAttribute( "name" );
                String scale = e.getAttribute( "scale" );
                String counter = e.getTextContent();
    
                logger.debug( "Adding CC counter " + counter );
                
                /* Region wild card */
                if( counter.contains( "(*)" ) ) {
                	for( String region : p.getRegions() ) {
                		p.ccounters.add( new ClearCaseCounterConfiguration( name, scale, counter, region ) );
                	}
                } else {
                	p.ccounters.add( new ClearCaseCounterConfiguration( name, scale, counter ) );
                }
            }
        } catch( Exception e ) {
        	/* No clearcase stuff */
        }
        
        /* Generating configuration map */
        Set<String> hostNames = p.getHostsMap().keySet();
        for( String hostName : hostNames ) {
            //System.out.println( "Adding host " + host );
        	p.config.put( hostName, new HashMap<String, PerformanceCounterConfiguration>() );
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
        
                    Set<String> hns = p.getHostsMap().keySet();
                    for( String hostName : hns ) {
                    	logger.debug( "Adding counter " + counter + " to " + hostName );
                    	p.config.get( hostName ).put( name, new PerformanceCounterConfiguration( name, scale, counter, ns, i ) );
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
                    String hostName = e.getAttribute( "name" );
                    String host = p.getHostsMap().get( hostName );
                    
                    logger.debug( "Specific host: " + hostName );
        
                    if( p.config.get( hostName ) == null ) {
                        //System.out.println( "Adding host " + host );
                    	p.config.put( hostName, new HashMap<String, PerformanceCounterConfiguration>() );
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
            
                        logger.debug( "Putting " + name + " for " + hostName );
                        p.config.get( hostName ).put( name, new PerformanceCounterConfiguration( name, scale, counter, ns, i ) );
                    }
                }
            } catch( Exception e ) {
                /* No op */
            }
        } catch( Exception e ) {
            /* No op */
        }
        
        if( ccHost != null && ccHost.length() > 0 ) {
        	p.clearcaseHost = p.getHostsMap().get( ccHost );
        }
        
        return p;
    }
    
    public class Configuration{
        List<ClearCaseCounterConfiguration> ccounters = new ArrayList<ClearCaseCounterConfiguration>();
        
        // hostname, []
        Map<String, Map<String, PerformanceCounterConfiguration>> config = new HashMap<String, Map<String, PerformanceCounterConfiguration>>();
        
        
        Map<String, String> hostsmap = new HashMap<String, String>();
        List<String> regions = new ArrayList<String>();
        
        String clearcaseHost = null;
        
        private String site;
        
        public Configuration( String site ) {
        	this.site = site;
        }
        
        public List<PerformanceCounterConfiguration> getPerformanceCounters( String hostName ) {
        	Map<String, PerformanceCounterConfiguration> c = config.get( hostName );
            return new ArrayList<PerformanceCounterConfiguration>(c.values());
        }
        
        public List<ClearCaseCounterConfiguration> getClearCaseCounters() {
            return ccounters;
        }
        
        public void addRegion( String region ) {
        	regions.add( region );
        }
        
        public List<String> getRegions() {
        	return regions;
        }
        
        public void addHost( String host, String name ) {
        	hostsmap.put( host, name );
        }        	
        
        public Map<String, String> getHostsMap() {
        	return hostsmap;
        }
        
        public String getClearcaseHost() {
        	return clearcaseHost;
        }
        
        public String getSite() {
        	return site;
        }
        
        @Override
        public String toString() {
        	StringBuilder sb = new StringBuilder();
        	
        	sb.append( "Listing configuration" + "\n" );

        	sb.append( "ClearCase Counters:\n" );
        	for( ClearCaseCounterConfiguration c : ccounters ) {
        		sb.append( c.toString()).append("\n");
        	}
        	
        	sb.append( "Performance Counters:\n" );
        	
        	Set<String> hostNames = hostsmap.keySet();
        	for( String hostName : hostNames ) {
        		for( PerformanceCounterConfiguration c : getPerformanceCounters( hostName ) ) {
        			sb.append( c.toString()).append("\n");
        		}
        	}
        	
        	return sb.toString();
        }
    }

}
