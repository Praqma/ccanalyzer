package net.praqma.ccanalyzer;

public class PerformanceCounter {
    public String counter;
    public int numberOfSamples = 1;
    public int intervalTime = 1; // Seconds
    
    public AggregateFunction function = AggregateFunction.NUMERICAL_AVERAGE;
    
    
    public enum AggregateFunction {
	NUMERICAL_SUM,
	NUMERICAL_AVERAGE,
	FIRST
    }
    
    public PerformanceCounter( String counter, int numberOfSamples, int intervalTime, AggregateFunction function ) {
	this.counter = counter;
	this.numberOfSamples = numberOfSamples;
	this.intervalTime = intervalTime;
	this.function = function;
    }
}
