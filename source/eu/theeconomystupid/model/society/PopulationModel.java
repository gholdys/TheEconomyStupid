package eu.theeconomystupid.model.society;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;
import java.util.Random;


public final class PopulationModel extends Operator {
    
    
    
    private String retirementAgeKey;
    private String totalPopulationKey;
    private String numRetiredKey;
    private String workforceKey;    
    private String maturityAgeKey;
    private String maximumAgeKey;
    
    private long seed;
    private double populationMaxSpread;
    private double totalPopulation;
    private int maturityAge;
    private int maximumAge;
    private Random random;
    
    
    public PopulationModel() {}

    public void configure( Registry r, Map <String,String> config ) {
        retirementAgeKey = config.get("retirementAgeKey");
        totalPopulationKey = config.get("totalPopulationKey");
        numRetiredKey = config.get("numRetiredKey");
        workforceKey = config.get("workforceKey");        
        seed = Long.parseLong( config.get("seed") );
        maturityAgeKey = config.get("maturityAgeKey");
        maximumAgeKey = config.get("maximumAgeKey");
        populationMaxSpread = Double.parseDouble( config.get("populationMaxSpread") );
        
        if ( seed == 0 ) seed = System.currentTimeMillis();
        random = new Random( seed );
        
        totalPopulation = r.getDouble( totalPopulationKey );
        maturityAge = r.getInteger(maturityAgeKey);
        maximumAge = r.getInteger(maximumAgeKey);
    }

    public void execute( Registry r ) {
        double retirementAge = (Double) r.get( retirementAgeKey );
        
        totalPopulation = Math.round( totalPopulation + random.nextGaussian()*populationMaxSpread );
        
        double n = totalPopulation/maximumAge;
        double workforce = Math.round( (retirementAge-maturityAge)*n );
        double numRetired = Math.round( (maximumAge-retirementAge)*n );
        
        r.put( workforceKey, workforce );
        r.put( numRetiredKey, numRetired );
        r.put( totalPopulationKey, totalPopulation );
        
    }
    
}