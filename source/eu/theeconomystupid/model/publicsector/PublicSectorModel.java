package eu.theeconomystupid.model.publicsector;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;


public final class PublicSectorModel extends Operator {
    
    
    private String priceLevelKey;
    private String publicSectorEffectivenessKey;
    private String publicSectorOptimumEmplToPopKey;
    private String publicSectorOptimumFundToGDPKey;
    private String gdpKey;
    private String numEmployedPublKey;
    private String numEmployedPrivKey;
    private String aveWageInPublicSectorKey;
    private String realAveWageInPublicSectorKey;
    private String aveWageInPrivateSectorKey;
    private String fundingKey;
    private String numUnemployedKey;
    private String workforceKey;
    private String unemploymentRateKey;
    private String unemploymentBenefitsKey;
    private String populationKey;
    private String eventsKey;
    
    private double wtw_a;
    private double wtq_a;
    private double ne_baseDeley;
    private double ne_f1;
    private double ne_f2;
    private double ne_f3;
    private double pse_deley;
    private double events_a;
    private String[] events;
    private double optimumFundingToGDP;
    private double optimumEmploymentToPopulation;
    private double wageFundCoeff;
    private long seed;
    private Random random;

    private List <String> eventsList;
            
    
    public PublicSectorModel() {}

    public void configure( Registry r, Map <String,String> config ) {
        priceLevelKey = config.get( "priceLevelKey" );
        publicSectorEffectivenessKey = config.get( "publicSectorEffectivenessKey" );
        publicSectorOptimumEmplToPopKey = config.get( "publicSectorOptimumEmplToPopKey" );
        publicSectorOptimumFundToGDPKey = config.get( "publicSectorOptimumFundToGDPKey" );
        gdpKey = config.get( "gdpKey" );
        numEmployedPublKey = config.get( "numEmployedPublKey" );
        numEmployedPrivKey = config.get( "numEmployedPrivKey" );
        aveWageInPublicSectorKey = config.get( "aveWageInPublicSectorKey" );
        realAveWageInPublicSectorKey = config.get( "realAveWageInPublicSectorKey" );
        aveWageInPrivateSectorKey = config.get( "aveWageInPrivateSectorKey" );
        fundingKey = config.get( "fundingKey" );
        numUnemployedKey = config.get( "numUnemployedKey" );
        unemploymentRateKey = config.get( "unemploymentRateKey" );
        unemploymentBenefitsKey = config.get( "unemploymentBenefitsKey" );
        workforceKey = config.get( "workforceKey" );
        populationKey = config.get( "populationKey" );                
        eventsKey = config.get( "eventsKey" );
        
        double numEmployedPubl = r.getDouble( numEmployedPublKey );
        double gdp = r.getDouble( gdpKey );
        double effectiveness = r.getDouble( publicSectorEffectivenessKey );
        double population = r.getDouble( populationKey );
        double aveWagePubl = r.getDouble( aveWageInPublicSectorKey );
        double aveWagePriv = r.getDouble( aveWageInPrivateSectorKey );
        double funding = r.getDouble( fundingKey );
        double annualFunding = 12*funding;
        double unemplBenefits = r.getDouble( unemploymentBenefitsKey );
        double d1 = Double.parseDouble( config.get("ne_d1") );
        
        ne_f2 = Double.parseDouble( config.get( "ne_f2" ) );
        ne_f3 = Double.parseDouble( config.get( "ne_f3" ) );
        pse_deley = Double.parseDouble( config.get( "pse_deley" ) );
        ne_baseDeley = Double.parseDouble( config.get( "ne_baseDeley" ) );
        events_a = Double.parseDouble( config.get( "events_a" ) );
        events = parseStringArray( config.get("events") );
        seed = Long.parseLong( config.get("seed") );
        if ( seed == 0 ) seed = System.currentTimeMillis();
        random = new Random( seed );       
        
        wageFundCoeff = 1e-9*numEmployedPubl*aveWagePubl/funding;
        ne_f1 = d1-ne_f2-ne_baseDeley;
        wtw_a = 1.0 / Math.pow( 0.5*(aveWagePubl/unemplBenefits+aveWagePubl/aveWagePriv), 4.0 );
        wtq_a = aveWagePubl/aveWagePriv;
                
        eventsList = new ArrayList <String> ();
        
    }    

    public void execute( Registry r ) {
        double priceLevel = r.getDouble( priceLevelKey );
        double effectiveness_0 = r.getDouble( publicSectorEffectivenessKey );
        double optimumEmploymentToPopulation = r.getDouble( publicSectorOptimumEmplToPopKey );
        double optimumFundingToGDP = r.getDouble( publicSectorOptimumFundToGDPKey );
        double gdp = r.getDouble( gdpKey );
        double workforce = r.getDouble( workforceKey );
        double population = r.getDouble( populationKey );
        double numEmployedPubl = r.getDouble( numEmployedPublKey );
        double numEmployedPriv = r.getDouble( numEmployedPrivKey );
        double aveWagePubl = r.getDouble( aveWageInPublicSectorKey );
        double aveWagePriv = r.getDouble( aveWageInPrivateSectorKey );
        double funding = r.getDouble( fundingKey );        
        double unemplBenefits = r.getDouble( unemploymentBenefitsKey );
        
        String[] evts = (String[]) r.get( eventsKey );
        eventsList.clear();
        if ( evts != null ) for ( String s : evts ) eventsList.add(s);
        
        double annualFunding = 12*funding;
        double willingnessToWork = wtw_a*Math.pow( 0.5*(aveWagePubl/unemplBenefits+aveWagePubl/aveWagePriv), 4 );
        double willingnessToQuit = wtq_a*aveWagePriv/aveWagePubl;
        
        double employmentPressure = optimumEmploymentToPopulation*population - numEmployedPubl;
        double possibleEmployment = 1e9*wageFundCoeff*funding/aveWagePubl;
        double resignations = trim( ne_f3*willingnessToQuit*numEmployedPubl, 0.0, numEmployedPubl );
        
        double employmentDelay;
        if ( employmentPressure > 0 ) {
            employmentDelay = trimDelay( ne_f1*willingnessToWork+ne_f2 + ne_baseDeley );
        } else {
            employmentDelay = trimDelay( ne_baseDeley );
        }
        
        double available = workforce - numEmployedPriv - numEmployedPubl;
        double dNumEmployedPubl = Math.min( available, employmentPressure/employmentDelay-resignations/ne_baseDeley );
        
        numEmployedPubl += dNumEmployedPubl;
        if ( numEmployedPubl < 0 ) {
            numEmployedPubl = 0;
        } else if ( numEmployedPubl > possibleEmployment ) {
            numEmployedPubl = Math.round(possibleEmployment);
        }
        
        double numUnemployed = workforce - numEmployedPriv - numEmployedPubl;
        double unemploymentRate = numUnemployed/workforce;
        
        double e_f = (annualFunding/gdp)/optimumFundingToGDP;
        double e_ne = (numEmployedPubl/population)/optimumEmploymentToPopulation;
        double effectiveness_1 = Math.min( 1.0, 0.5*( e_f + e_ne ) );
        double effectiveness = effectiveness_0 + (effectiveness_1-effectiveness_0)/pse_deley;
        
        double p = Math.exp(-events_a*effectiveness);
        for ( String event : events ) {
            if ( random.nextDouble() <= p ) eventsList.add( event );
        }
        
        aveWagePubl = 1e9*wageFundCoeff*funding/numEmployedPubl;
        
        r.put( publicSectorEffectivenessKey, effectiveness );
        r.put( aveWageInPublicSectorKey, aveWagePubl );
        r.put( realAveWageInPublicSectorKey, aveWagePubl/priceLevel );
        r.put( numEmployedPublKey, numEmployedPubl );
        r.put( numUnemployedKey, numUnemployed );
        r.put( unemploymentRateKey, unemploymentRate );
        r.put( eventsKey, eventsList.toArray( new String[eventsList.size()] ) );
        
    }
    
    private static double trimDelay( double d ) {
        if ( d < 1.0 ) d = 1.0;
        return d;
    }
    
    private static double trim( double value, double min, double max ) {
        return Math.max( min, Math.min( max, value ) );
    }
    
    private static String[] parseStringArray( String text ) {
        StringTokenizer tokenizer = new StringTokenizer(text, ",");
        List <String> tokens = new ArrayList<String>();
        while ( tokenizer.hasMoreTokens() ) {
            tokens.add( tokenizer.nextToken().trim() );
        }
        return tokens.toArray( new String[tokens.size()] );
    }
    
}