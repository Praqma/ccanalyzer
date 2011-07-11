package net.praqma.ccanalyzer;

import net.praqma.clearcase.Cool;
import net.praqma.clearcase.Cool.ContextType;
import net.praqma.clearcase.Site;
import net.praqma.util.debug.PraqmaLogger;
import net.praqma.util.debug.PraqmaLogger.Logger;

public abstract class ClearCaseFunction {
    
    static {
        Cool.setContext( ContextType.CLEARTOOL );

        Logger logger = PraqmaLogger.getLogger();
        logger.subscribeAll();
        Cool.setLogger( logger );
    }
    
    protected static Site site = new Site( "My site" );
    
    public abstract String perform( ClearCaseCounter ccc );
}
