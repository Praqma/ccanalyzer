package net.praqma.ccanalyzer.clearcase;

import java.util.List;

import net.praqma.ccanalyzer.ClearCaseCounter;
import net.praqma.ccanalyzer.ClearCaseFunction;
import net.praqma.clearcase.Region;
import net.praqma.clearcase.ucm.view.UCMView;

public class NumberOfViews extends ClearCaseFunction{

    @Override
    public String perform( ClearCaseCounter ccc ) {
        Region r = new Region( ccc.getModifier(), site );
        
        List<UCMView> views = r.getViews();
        
        return Integer.toString( views.size() );
    }

}
