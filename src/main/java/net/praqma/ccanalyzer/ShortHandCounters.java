package net.praqma.ccanalyzer;

import java.util.List;

public abstract class ShortHandCounters {
    
    public static Float getFreeSpace( String driveLetter ) {
	if( driveLetter == null ) {
	    driveLetter = "_Total";
	}
	List<String> result = PerformanceCounter.get( "\\LogicalDisk(" + driveLetter + ")\\% Free Space" );
	
	if( result.size() != 1 ) {
	    throw new PerformanceCounterException("Error in number of results");
	} else {
	    
	    Float usage = new Float( result.get(0) );
	    
	    return usage;
	}
    }
    
}
