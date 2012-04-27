package net.praqma.ccanalyzer;

import java.util.List;

import net.praqma.clearcase.Cool;
import net.praqma.clearcase.Vob;
import net.praqma.clearcase.exceptions.CleartoolException;
import net.praqma.clearcase.ucm.view.UCMView;
import net.praqma.clearcase.Region;
import net.praqma.monkit.MonKit;

public class ClearCaseAnalyzer {

    public static void getVobs( List<Region> regions, MonKit mk ) throws CleartoolException {

        mk.addCategory( "vobs", "number" );

        for( Region region : regions ) {
            List<Vob> vobs = region.getVobs();

            mk.add( region.getName(), vobs.size() + "", "vobs" );
        }
    }

    public static void getViews( List<Region> regions, MonKit mk ) throws CleartoolException {

        mk.addCategory( "views", "number" );

        for( Region region : regions ) {
            List<UCMView> views = region.getViews();

            mk.add( region.getName(), views.size() + "", "views" );
        }
    }
}
