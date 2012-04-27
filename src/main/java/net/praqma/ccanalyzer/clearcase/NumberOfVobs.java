package net.praqma.ccanalyzer.clearcase;

import java.util.List;

import net.praqma.ccanalyzer.ClearCaseCounter;
import net.praqma.ccanalyzer.ClearCaseFunction;
import net.praqma.clearcase.Region;
import net.praqma.clearcase.Vob;
import net.praqma.clearcase.exceptions.ClearCaseException;
import net.praqma.clearcase.exceptions.CleartoolException;

public class NumberOfVobs extends ClearCaseFunction{

    @Override
    public String perform( ClearCaseCounter ccc ) throws ClearCaseException {
        Region r = new Region( ccc.getModifier(), site );
        
        List<Vob> vobs = r.getVobs();
        
        return Integer.toString( vobs.size() );
    }

}
