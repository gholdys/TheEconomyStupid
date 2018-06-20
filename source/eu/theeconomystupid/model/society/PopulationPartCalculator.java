package eu.theeconomystupid.model.society;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;


public final class PopulationPartCalculator extends Operator {
    
    
    private String totalPopulationKey;
    private String[] numPeopleKeys;
    private String[] populationPartKeys;
    private int numGroups;
    
    
    public PopulationPartCalculator() {}

    public void configure( Registry r, Map <String,String> config ) {
        totalPopulationKey = config.get( "totalPopulationKey" );
        numGroups = Integer.parseInt( config.get( "numberOfGroups" ) );
        numPeopleKeys = new String[numGroups];
        populationPartKeys = new String[numGroups];
        for ( int i=0; i<numGroups; i++ ) {
            readGroupKeys( config, i );
        }
    }
    
    public void execute( Registry r ) {
        double population = r.getDouble( totalPopulationKey );
        double n, p;
        for ( int i=0; i<numGroups; i++ ) {
            n = r.getDouble( numPeopleKeys[i] );
            p = n/population;
            r.put( populationPartKeys[i], p );
        }
    }
    
    private void readGroupKeys( Map <String,String> config, int iGroup ) {
        String preffix = "group[" + iGroup + "].";
        numPeopleKeys[iGroup] = config.get( preffix + "npKey" );
        populationPartKeys[iGroup] = config.get( preffix + "ppKey" );
    }
    
}
