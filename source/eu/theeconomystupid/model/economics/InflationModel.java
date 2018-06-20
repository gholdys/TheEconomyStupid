package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public final class InflationModel extends Operator {
 
    
    private String inflationRateKey;
    private String priceLevelKey;
    private String inflationPressureKey;
    private String vatRateKey;    
    private double deflationWeight;
    private List <Double> priceLevelHistory;
    private double vatRate_0;
    private double inflationRate;
    
    
    public InflationModel() {}

    public void configure( Registry r, Map<String,String> config ) {
        inflationRateKey = config.get( "inflationRateKey" );
        priceLevelKey = config.get( "priceLevelKey" );
        inflationPressureKey = config.get( "inflationPressureKey" );
        vatRateKey = config.get( "vatRateKey" );
        
        deflationWeight = Double.parseDouble( config.get("deflationWeight") );
        
        inflationRate = r.getDouble( inflationRateKey );
        double p1 = r.getDouble( priceLevelKey );
        
        double p0 = p1/(1.0+inflationRate);
        priceLevelHistory = new LinkedList <Double> ();
        double x, p;        
        p = p0;
        for ( int i=0; i<12; i++ ) {
            p *= Math.pow(1.0+inflationRate, 1.0/13.0);
            priceLevelHistory.add(p);
        }
        
        vatRate_0 = r.getDouble( vatRateKey );
    }        
    
    public void execute( Registry r ) {
        double inflationPressure = r.getDouble( inflationPressureKey );
        double priceLevel = r.getDouble( priceLevelKey );
        double vatRate_1 = r.getDouble( vatRateKey );
                        
        inflationPressure += vatRate_1-vatRate_0;
        vatRate_0 = vatRate_1;

        if ( inflationPressure < 0.0 ) inflationPressure *= deflationWeight;
        double priceLevel_1 = priceLevel*(1.0+inflationPressure);
        priceLevelHistory.add(priceLevel_1);
        
        double p0 = priceLevelHistory.remove(0);
        double inflationRate = (priceLevel_1-p0)/p0;
        inflationPressure = 0.0;

        r.put( priceLevelKey, priceLevel_1 );
        r.put( inflationRateKey, inflationRate );
        r.put( inflationPressureKey, inflationPressure );
    }
    
}