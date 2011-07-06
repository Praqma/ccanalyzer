package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import net.praqma.util.xml.XML;

public class ConfigurationReader extends XML {

    List<PerformanceCounter> counters = new ArrayList<PerformanceCounter>();
    
    public ConfigurationReader( File conf ) throws IOException {
	super( conf );
	
	List<Element> elements = getElements();
	
	for( Element e : elements ) {
	    String name = e.getAttribute("name");
	    String scale = e.getAttribute("scale");
	    String counter = e.getTextContent();
	    
	    String samples = e.getAttribute("samples");
	    int ns = 1;
	    if( !samples.equals("") ) {
		ns = Integer.parseInt(samples);
	    }
	    
	    String interval = e.getAttribute("interval");
	    int i = 1;
	    if( !interval.equals("") ) {
		i = Integer.parseInt(interval);
	    }
	    
	    counters.add(new PerformanceCounter(name, scale, counter, ns, i));
	}
    }
    
    public List<PerformanceCounter> getCounters() {
	return counters;
    }
    

    
}
