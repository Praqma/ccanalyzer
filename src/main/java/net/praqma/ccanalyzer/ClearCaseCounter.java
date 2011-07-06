package net.praqma.ccanalyzer;

import java.util.List;

import net.praqma.clearcase.Cool;
import net.praqma.clearcase.Region;
import net.praqma.clearcase.Vob;
import net.praqma.clearcase.Cool.ContextType;
import net.praqma.clearcase.ucm.view.UCMView;
import net.praqma.util.debug.PraqmaLogger;
import net.praqma.util.debug.PraqmaLogger.Logger;

public class ClearCaseCounter {
    static {
	Cool.setContext(ContextType.CLEARTOOL);
	
	Logger logger = PraqmaLogger.getLogger();
	logger.subscribeAll();
	Cool.setLogger(logger);
    }
    
    public int getVobs( Region region ) {
	
	List<Vob> vobs = region.getVobs();
	return vobs.size();
    }
    
    public int getViews( Region region ) {

	List<UCMView> views = region.getViews();
	return views.size();
    }
}
