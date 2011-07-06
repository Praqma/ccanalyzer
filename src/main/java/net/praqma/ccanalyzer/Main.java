package net.praqma.ccanalyzer;

import java.io.IOException;
import java.util.List;

import net.praqma.clearcase.Cool;
import net.praqma.clearcase.Cool.ContextType;
import net.praqma.clearcase.ucm.view.UCMView;
import net.praqma.clearcase.Region;
import net.praqma.clearcase.Site;
import net.praqma.clearcase.Vob;
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
	
	//String regionString = oregion.getString();
	
	MonKit mk = new MonKit();
	mk.addCategory("vobs", "number");
	mk.addCategory("views", "number");
	
	Site ccsite = new Site("mysite");
	
	
	for (String r : oregion.getStrings()) {
	    Region ccregion = new Region(r, ccsite);

	    List<Vob> vobs = ccregion.getVobs();

	    mk.add(r, vobs.size() + "", "vobs");

	    List<UCMView> views = ccregion.getViews();

	    mk.add(r, views.size() + "", "views");
	}
	
	/* Final */
	mk.save();
    }

}
