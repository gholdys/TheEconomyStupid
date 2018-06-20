package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;
import java.util.Random;


public final class ExportsGenerator extends Operator {
 
    
    private String realExportsKey;
    private String exportsKey;
    private String priceLevelKey;
    private String globalEconomyConditionKey;
    private Random r1;
    private double realExports;
    private double globalEconomyCondition;
    private double gec_p, gec_a;
    private double re_a, re_b, re_deley;
    
    
    public ExportsGenerator() {}

    public void configure( Registry r, Map<String,String> config ) {
        realExportsKey = config.get( "realExportsKey" );
        exportsKey = config.get( "exportsKey" );
        priceLevelKey = config.get( "priceLevelKey" );
        globalEconomyConditionKey = config.get( "globalEconomyConditionKey" );
        long seed = Long.parseLong( config.get( "seed" ) );
        if ( seed == 0 ) seed = System.currentTimeMillis();
        //System.out.println(seed);
        r1 = new Random(seed);
        gec_p = Double.parseDouble( config.get("gec_p") );
        gec_a = Double.parseDouble( config.get("gec_a") );
        re_b = Double.parseDouble( config.get("re_b") );
        re_deley = Double.parseDouble( config.get("re_deley") );
        realExports = r.getDouble( realExportsKey );
        globalEconomyCondition = r.getDouble( globalEconomyConditionKey );
        re_a = (realExports-re_b)/globalEconomyCondition;
    }
    
    public void execute( Registry r ) {
        if ( r1.nextDouble() < gec_p ) {
            globalEconomyCondition += gec_a*2.0*(r1.nextDouble()-0.5);
            if ( globalEconomyCondition > 1.0 ) {
                globalEconomyCondition = 1.0;
            } else if ( globalEconomyCondition < 0.0 ) {
                globalEconomyCondition = 0.0;
            }
        }
        double exportGap = re_a*globalEconomyCondition+re_b - realExports;
        realExports += r1.nextDouble()*exportGap/re_deley;
        if ( realExports < 0.0 ) realExports = 0.0;
        r.put( globalEconomyConditionKey, globalEconomyCondition );
        r.put( realExportsKey, realExports );        
        r.put( exportsKey, realExports*r.getDouble( priceLevelKey ) );
    }
        
}