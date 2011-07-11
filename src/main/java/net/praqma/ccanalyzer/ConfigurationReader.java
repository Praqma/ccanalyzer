package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import net.praqma.util.xml.XML;

public class ConfigurationReader extends XML implements Serializable {

    private static final long serialVersionUID = 3648821402865625037L;

    List<ClearCaseCounterConfiguration> ccounters = new ArrayList<ClearCaseCounterConfiguration>();
    List<PerformanceCounterConfiguration> pcounters = new ArrayList<PerformanceCounterConfiguration>();
    
    public ConfigurationReader() {
    }

    public ConfigurationReader( File conf ) throws IOException {
        super( conf );
        
        initialize();
    }
    
    public void initialize() {
        
        /* Get the ClearCase counters */
        Element ccs = getFirstElement( "clearcase" );
        List<Element> elements = getElements( ccs );

        for( Element e : elements ) {
            String name = e.getAttribute( "name" );
            String scale = e.getAttribute( "scale" );
            String counter = e.getTextContent();

            ccounters.add( new ClearCaseCounterConfiguration( name, scale, counter ) );
        }

        /* Get the performance counters */
        Element pcs = getFirstElement( "performance" );
        List<Element> pelements = getElements( pcs );

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

            pcounters.add( new PerformanceCounterConfiguration( name, scale, counter, ns, i ) );
        }
    }

    public List<PerformanceCounterConfiguration> getPerformanceCounters() {
        return pcounters;
    }
    
    public List<ClearCaseCounterConfiguration> getClearCaseCounters() {
        return ccounters;
    }
    
    public void addPerformanceCounters( List<PerformanceCounterConfiguration> pcs ) {
        /* Check the performance counters */
        for( PerformanceCounterConfiguration pc1 : pcs ) {
            boolean same = false;
            for( PerformanceCounterConfiguration pc2 : pcounters ) {
                /* TODO check for more than the name? The actual counter perhaps? */
                if( pc1.name.equals( pc2.name ) ) {
                    same = true;
                    break;
                }
            }
            
            if( !same ) {
                pcounters.add( pc1 );
            }
        }
    }
    
    
    public void addClearCaseCounters( List<ClearCaseCounterConfiguration> ccs ) {
        /* Check the ClearCase counters */
        for( ClearCaseCounterConfiguration cc1 : ccs ) {
            boolean same = false;
            for( ClearCaseCounterConfiguration cc2 : ccounters ) {
                /* TODO check for more than the name? The actual counter perhaps? */
                if( cc1.getName().equals( cc2.getName() ) ) {
                    same = true;
                    break;
                }
            }
            
            if( !same ) {
                ccounters.add( cc1 );
            }
        }
    }
    
    public void addFrom( ConfigurationReader cr ) {
        
        /* Check the performance counters */
        for( PerformanceCounterConfiguration pc1 : cr.getPerformanceCounters() ) {
            boolean same = false;
            for( PerformanceCounterConfiguration pc2 : pcounters ) {
                /* TODO check for more than the name? The actual counter perhaps? */
                if( pc1.name.equals( pc2.name ) ) {
                    same = true;
                    break;
                }
            }
            
            if( !same ) {
                pcounters.add( pc1 );
            }
        }
        
        /* Check the ClearCase counters */
        for( ClearCaseCounterConfiguration cc1 : cr.getClearCaseCounters() ) {
            boolean same = false;
            for( ClearCaseCounterConfiguration cc2 : ccounters ) {
                /* TODO check for more than the name? The actual counter perhaps? */
                if( cc1.getName().equals( cc2.getName() ) ) {
                    same = true;
                    break;
                }
            }
            
            if( !same ) {
                ccounters.add( cc1 );
            }
        }
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for( PerformanceCounterConfiguration pc : pcounters ) {
            sb.append( pc.toString() + " - " );
        }
        
        return sb.toString();
    }

}
