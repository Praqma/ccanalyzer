package net.praqma.ccanalyzer;

import java.io.Serializable;

public class ClearCaseCounterConfiguration implements Serializable {

    private static final long serialVersionUID = -311929916433574394L;

    String name;
    String scale;
    String counter;
    
    public ClearCaseCounterConfiguration( String name, String scale, String counter ) {
        this.name = name;
        this.scale = scale;
        this.counter = counter;
    }
}
