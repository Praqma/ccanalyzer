package net.praqma.ccanalyzer;

import java.io.IOException;

import net.praqma.clearcase.Cool;
import net.praqma.clearcase.Site;
import net.praqma.clearcase.exceptions.ClearCaseException;
import net.praqma.util.debug.Logger;

public abstract class ClearCaseFunction {
    
	protected Logger logger = Logger.getLogger();
    
    protected static Site site = new Site( "My site" );
    
    public abstract String perform( ClearCaseCounter ccc ) throws IOException, ClearCaseException;
}
