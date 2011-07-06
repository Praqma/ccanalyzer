package net.praqma.ccanalyzer;

import java.io.IOException;

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
	
	Options o = new Options( "0.0.2" );
	
	Option oregion = new Option( "region", "r", true, -1, "The region" );
	Option oconf   = new Option( "configuration", "c", false, 1, "A given configuration file" );
	
	o.setOption( oregion );
	o.setOption( oconf );
	
	o.setDefaultOptions();
	
	o.setSyntax( "" );
	o.setHeader( "" );
	o.setDescription( "" );
	
	o.parse( args );
	
	try {
	    o.checkOptions();
	} catch (Exception e) {
	    System.err.println("Incorrect option: " + e.getMessage());
	    o.display();
	    System.exit(1);
	}
	
	Cool.setContext(ContextType.CLEARTOOL);
	
	Logger logger = PraqmaLogger.getLogger();
	logger.subscribeAll();
	Cool.setLogger(logger);
	
	MonKit mk = new MonKit();
	
	

	
	/* Final */
	mk.save();
    }

}
