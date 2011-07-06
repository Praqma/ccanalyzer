package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.praqma.util.xml.XML;

public class ConfigurationReader extends XML {

    public class Configuration {
	String name;
	
	public Configuration( String name ) {
	    this.name = name;
	}
    }
    
    private List<Configuration> cfs = new ArrayList<Configuration>();
    
    public ConfigurationReader( File conf ) throws IOException {
	super( conf );
    }
    
    public List<Configuration> get() {
	
	return cfs;
    }

    public void getValue( String key ) {
	//getElementsWithAttribute(e, attr, name)
	
    }
    
}
