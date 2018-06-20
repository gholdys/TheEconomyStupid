package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public final class GDPCalculator extends Operator {
    
    
    private String outputKey;
    private String gdpKey;
    private String gdpGrowthRateKey;
    private List <Double> outputBuffer;
    private List <Double> gdpBuffer;
    

    public GDPCalculator() {}

    public void configure( Registry r, Map<String,String> config ) {
        outputKey = config.get("outputKey");
        gdpKey = config.get("gdpKey");
        gdpGrowthRateKey = config.get("gdpGrowthRateKey");
        outputBuffer = new LinkedList <Double> ();
        gdpBuffer = new LinkedList <Double> ();
        
        double output = r.getDouble( outputKey );
        
        for ( int i=0; i<12; i++ ) {
            outputBuffer.add( output );
            gdpBuffer.add( output*12 );
        }
    }

    public void execute( Registry r ) {
        double output = r.getDouble( outputKey );        
        
        outputBuffer.add( output );
        outputBuffer.remove(0);
        double gdp_1 = 0.0;
        for ( double o : outputBuffer ) {
            gdp_1 += o;
        }
        double gdp_0 = gdpBuffer.get(0);
        gdpBuffer.add( gdp_1 );
        gdpBuffer.remove(0);
        
        r.put( gdpKey, gdp_1 );
        r.put( gdpGrowthRateKey, (gdp_1-gdp_0)/gdp_0 );
    }
    
}