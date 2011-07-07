package net.praqma.ccanalyzer;

import java.io.Serializable;

public class PerformanceCounter implements Serializable {

    private static final long serialVersionUID = -1811413449246591974L;

    public String name;
    public String scale;
    public String counter;
    public int numberOfSamples = 1;
    public int intervalTime = 1; // Seconds

    public AggregateFunction function = AggregateFunction.NUMERICAL_AVERAGE;

    public enum AggregateFunction {
        NUMERICAL_SUM, 
        NUMERICAL_AVERAGE, 
        FIRST
    }

    public PerformanceCounter( String name, String scale, String counter, int numberOfSamples, int intervalTime, AggregateFunction function ) {
        this.name = name;
        this.scale = scale;
        this.counter = counter;
        this.numberOfSamples = numberOfSamples;
        this.intervalTime = intervalTime;
        this.function = function;
    }

    public PerformanceCounter( String name, String scale, String counter, int numberOfSamples, int intervalTime ) {
        this.name = name;
        this.scale = scale;
        this.counter = counter;
        this.numberOfSamples = numberOfSamples;
        this.intervalTime = intervalTime;
    }
}
