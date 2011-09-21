package net.praqma.ccanalyzer;

import java.io.Serializable;

public class ClearCaseCounterConfiguration implements Serializable {

    private static final long serialVersionUID = -311929916433574394L;

    private String name;
    private String scale;
    private ClearCaseCounter counter;
    
    public ClearCaseCounterConfiguration( String name, String scale, String counter ) {
        this.name = name;
        this.scale = scale;
        this.counter = ClearCaseCounter.fromString( counter );
    }
    
    public ClearCaseCounterConfiguration( String name, String scale, String counter, String region ) {
        this.name = name;
        this.scale = scale;
        counter = counter.replace( "(*)", "(" + region + ")" );
        this.counter = ClearCaseCounter.fromString( counter );
    }
    
    public String getName() {
        return name;
    }

    public String getScale() {
        return scale;
    }

    public ClearCaseCounter getCounter() {
        return counter;
    }
    
    public String toString() {
    	return name + ": " + counter.getCounter();
    }
}
