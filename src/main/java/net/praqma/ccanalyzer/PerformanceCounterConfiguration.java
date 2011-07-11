package net.praqma.ccanalyzer;

import java.io.Serializable;

public class PerformanceCounterConfiguration implements Serializable {

    private static final long serialVersionUID = -1811413449246591974L;

    public String name;
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

    public PerformanceCounterConfiguration( String name, String scale, String counter, int numberOfSamples, int intervalTime, AggregateFunction function ) {
        this.name = name;
        this.scale = scale;
        this.counter = counter;
        this.numberOfSamples = numberOfSamples;
        this.intervalTime = intervalTime;
        this.function = function;
    }

    public PerformanceCounterConfiguration( String name, String scale, String counter, int numberOfSamples, int intervalTime ) {
        this.name = name;
        this.scale = scale;
        this.counter = counter;
        this.numberOfSamples = numberOfSamples;
        this.intervalTime = intervalTime;
    }
    
    public String toString() {
        return name + "(" + counter + ")";
    }
}