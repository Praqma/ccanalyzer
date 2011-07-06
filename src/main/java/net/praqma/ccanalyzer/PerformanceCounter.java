package net.praqma.ccanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.praqma.util.execute.AbnormalProcessTerminationException;
import net.praqma.util.execute.CmdResult;
import net.praqma.util.execute.CommandLine;

public class PerformanceCounter {
    private static String prog = "typeperf";
    private static int numberOfSamples = 1;
    private static int timeInterval = 1; // Seconds
    
    private static Pattern rx_ = Pattern.compile( "^\\\"(.*?)\\\",\\\"(.*?)\\\"$" );
    
    /**
     * For a specific performance counter string, get the values for the class given number of samples and interval
     * @param performanceString
     * @return
     */
    public static List<String> get( String performanceString ) {
	return get( performanceString, numberOfSamples, timeInterval );
    }
    
    /**
     * 
     * @param performanceString
     * @param numberOfSamples
     * @param timeInterval
     * @return
     */
    public static List<String> get( String performanceString, int numberOfSamples, int timeInterval ) {
	String cmd = prog + " -si " + timeInterval + " - sc " + numberOfSamples + " " + performanceString;
	try {
	    CmdResult result = CommandLine.getInstance().run( cmd );
	    
	    List<String> list = new ArrayList<String>();
	    boolean first = true;
	    
	    for( String s : result.stdoutList ) {
		Matcher m = rx_.matcher( s );
		if( m.find() ) {
		    if( !first ) {
			list.add(m.group(2));
		    } else {
			first = false;
		    }
		}
	    }
	    
	    return list;
	    
	} catch( AbnormalProcessTerminationException e ) {
	    throw new PerformanceCounterException("Could not get " + performanceString + ", " + e.getMessage());
	}
    }
    
    
    public static Float getDiskUsage( String driveLetter ) {
	if( driveLetter == null ) {
	    driveLetter = "_Total";
	}
	List<String> result = get( "\\LogicalDisk(" + driveLetter + ")\\% Free Space" );
	
	if( result.size() != 1 ) {
	    throw new PerformanceCounterException("Error in number of results");
	} else {
	    
	    Float usage = new Float( result.get(0) );
	    
	    return usage;
	}
    }
}
