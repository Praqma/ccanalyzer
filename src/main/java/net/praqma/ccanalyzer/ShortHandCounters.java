package net.praqma.ccanalyzer;

import java.util.List;

public abstract class ShortHandCounters {
    
    public static String execute( List<String> request ) {
	/* Get available memory */
	if( request.get(1).equalsIgnoreCase("getavailablememory") ) {
	    return getAvailableMemory().toString();
	    
	/* Get free space */
	} else if( request.get(1).equalsIgnoreCase("getfreespace") ) {
	    if (request.size() > 2) {
		return getFreeSpace(request.get(2)).toString();
	    } else {
		return getFreeSpace("_Total").toString();
	    }
	
	/* Get Pages per second */
	} else if(request.get(1).equalsIgnoreCase("getPagesPerSecond")) {
	    int numberOfamples = 1;
	    int timeInterval = 1;

	    /* Number of samples is given */
	    if (request.size() > 2) {
		numberOfamples = Integer.parseInt(request.get(2));
	    }

	    /* Time interval is given */
	    if (request.size() > 3) {
		timeInterval = Integer.parseInt(request.get(3));
	    }

	    return getPagesPerSecond(numberOfamples, timeInterval).toString();
	    
	/* Default */
	} else {
	    return null;
	}
    }
    
    public static Float getAvailableMemory( ) {
	
	List<String> result = PerformanceCounterMeter.get( "\\Memory\\available mbytes", 1, 1 );
	
	if( result.size() != 1 ) {
	    throw new CCAnalyzerException("Not one result");
	} else {
	    
	    Float total = new Float( result.get(0) );
	    return total;
	}
    }
    
    public static Float getFreeSpace( String driveLetter ) {
	
	if( driveLetter == null ) {
	    driveLetter = "_Total";
	}
	List<String> result = PerformanceCounterMeter.get( "\\LogicalDisk(" + driveLetter + ")\\% Free Space" );
	
	if( result.size() != 1 ) {
	    throw new CCAnalyzerException("Error in number of results");
	} else {
	    
	    Float usage = new Float( result.get(0) );
	    
	    return usage;
	}
    }
    

    
    public static Float getPagesPerSecond( int numberOfamples, int timeInterval ) {
	
	List<String> result = PerformanceCounterMeter.get( "\\Memory\\Pages/sec", numberOfamples, timeInterval );
	
	if( result.size() < 1 ) {
	    throw new CCAnalyzerException("Too few results");
	} else {
	    
	    Float total = 0.0f;
	    int num = 0;
	    for( String s : result ) {
		total += new Float( s );
		num++;
	    }
	    
	    return total / num;
	}
    }
    
}
