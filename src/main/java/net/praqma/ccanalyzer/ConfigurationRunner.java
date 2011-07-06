package net.praqma.ccanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigurationRunner {
    
    ConfigurationReader cr;
    
    public ConfigurationRunner( String filename ) throws IOException {
	cr = new ConfigurationReader( new File( filename ) );
    }
}
