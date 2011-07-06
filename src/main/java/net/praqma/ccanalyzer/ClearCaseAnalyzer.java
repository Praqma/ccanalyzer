package net.praqma.ccanalyzer;

import java.util.List;

import net.praqma.clearcase.Cool;
import net.praqma.clearcase.Vob;
import net.praqma.clearcase.Cool.ContextType;
import net.praqma.clearcase.ucm.view.UCMView;
import net.praqma.clearcase.Region;
import net.praqma.monkit.MonKit;
import net.praqma.util.debug.PraqmaLogger;
import net.praqma.util.debug.PraqmaLogger.Logger;

public class ClearCaseAnalyzer {
    static {
	Cool.setContext(ContextType.CLEARTOOL);
	
	Logger logger = PraqmaLogger.getLogger();
	logger.subscribeAll();
	Cool.setLogger(logger);
    }
    
    public static void getVobs( List<Region> regions, MonKit mk ) {
	
	mk.addCategory("vobs", "number");
	
	for( Region region : regions ) {
	    List<Vob> vobs = region.getVobs();

	    mk.add(region.getName(), vobs.size() + "", "vobs");
	}
    }
    
    public static void getViews( List<Region> regions, MonKit mk ) {
	
	mk.addCategory("views", "number");
	
	for( Region region : regions ) {
	    List<UCMView> views = region.getViews();

	    mk.add(region.getName(), views.size() + "", "views");
	}
    }
}
