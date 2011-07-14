package net.praqma.ccanalyzer.clearcase;

import java.util.List;
import java.util.Map;

import net.praqma.ccanalyzer.ClearCaseCounter;
import net.praqma.ccanalyzer.ClearCaseFunction;
import net.praqma.clearcase.util.RegistryCheck;

public class NumberOfStrandedViews extends ClearCaseFunction{

    @Override
    public String perform( ClearCaseCounter ccc ) {
        List<Map<String, String>> r = RegistryCheck.checkViews();
        
        return Integer.toString( r.size() );
    }

}