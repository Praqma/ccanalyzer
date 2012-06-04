package net.praqma.ccanalyzer;

import java.io.Serializable;

public class PerformanceCounterConfiguration implements Serializable {

    private static final long serialVersionUID = -1811413449246591974L;

    public String host;
    public String scale;
    public String counter;
    public int numberOfSamples = 1;
    public int intervalTime = 1; // Seconds

    public AggregateFunction function = AggregateFunction.NUMERICAL_AVERAGE;

    public enum AggregateFunction implements Serializable {
        NUMERICAL_SUM, 
        NUMERICAL_AVERAGE, 
        FIRST
    }

    public PerformanceCounterConfiguration( String host, String scale, String counter, int numberOfSamples, int intervalTime, AggregateFunction function ) {
        this.host = host;
        this.scale = scale;
        this.counter = counter;
        this.numberOfSamples = numberOfSamples;
        this.intervalTime = intervalTime;
        this.function = function;
    }

    public PerformanceCounterConfiguration( String host, String scale, String counter, int numberOfSamples, int intervalTime ) {
        this.host = host;
        this.scale = scale;
        this.counter = counter;
        this.numberOfSamples = numberOfSamples;
        this.intervalTime = intervalTime;
    }
    
    @Override
    public String toString() {
        return host + "(" + counter + ")";
    }
}
