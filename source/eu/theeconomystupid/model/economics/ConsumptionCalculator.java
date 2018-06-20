package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;


public final class ConsumptionCalculator extends Operator {
 
    
    private String priceLevelKey;
    private String realConsumptionKey;
    private String consumptionKey;
    private String interestRateKey;
    private String realOutputKey;
    private double f1;
    private double f2;

    
    public ConsumptionCalculator() {}
    
    public void configure( Registry r, Map <String,String> config ) {
        priceLevelKey = config.get( "priceLevelKey" );
        realConsumptionKey = config.get( "realConsumptionKey" );
        consumptionKey = config.get( "consumptionKey" );
        realOutputKey = config.get( "realOutputKey" );
        double consumption = r.getDouble( realConsumptionKey );
        double output = r.getDouble( realOutputKey );
        f1 = Double.parseDouble( config.get("f1") );
        f2 = consumption-f1*output;
    }
    
    public void execute( Registry r ) {
        double output = r.getDouble( realOutputKey );
        
        double realConsumption = Math.max( 0.0, round3( Math.max( 0.0, f1*output+f2 ) ) );
        double consumption = realConsumption*r.getDouble( priceLevelKey );
        
        r.put( realConsumptionKey, realConsumption );
        r.put( consumptionKey, consumption );
    }
    
    private static final double round3( double value ) {
        return 0.001 * Math.round( 1000.0*value );
    }
    
}