package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;
import java.util.Random;


public final class ImportsCalculator extends Operator {
 
    
    private String realImportsKey;
    private String importsKey;
    private String priceLevelKey;
    private String inflationRateKey;    
    private double f1;
    private double f2;
    private double deley;
    private Random random;
    
    
    public ImportsCalculator() {}
    
    public void configure( Registry r, Map<String,String> config ) {
        realImportsKey = config.get("realImportsKey");
        importsKey = config.get("importsKey");
        priceLevelKey = config.get("priceLevelKey");
        inflationRateKey = config.get("inflationRateKey");
        double realImports = r.getDouble( realImportsKey );
        double inflationRate = r.getDouble( inflationRateKey );
        f2 = Double.parseDouble( config.get("f2") );
        f1 = (realImports-f2)/inflationRate;
        deley = Double.parseDouble( config.get("deley") );
        long seed = Long.parseLong( config.get("seed") );
        if ( seed == 0 ) seed = System.currentTimeMillis();
        random = new Random( seed );
    }
    
    public void execute( Registry r ) {
        double realImports = r.getDouble( realImportsKey );
        double inflationRate = r.getDouble( inflationRateKey );
        double priceLevel = r.getDouble( priceLevelKey );
        double realImports_1 = Math.max( 0.0, f1*inflationRate+f2 );
        realImports += random.nextDouble()*(realImports_1-realImports)/deley;
        r.put( realImportsKey, realImports );
        r.put( importsKey, realImports*priceLevel );
    }
        
}