package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public final class PrivateSectorModel extends Operator {
 
    
    private static enum KEYS {
        workforceKey,
        numEmployedPrivKey,
        numEmployedPublKey,
        numUnemployed,
        outputKey,
        realOutputKey,
        realConsumptionKey,
        realInvestmentKey,
        realGovernmentSpendingKey,
        realTaxRevenuesKey,
        realExportsKey,
        realImportsKey,
        labourProductivityKey,
        priceLevelKey,
        realAveWagePrivKey,
        aveWagePrivKey,
        unemploymentRateKey,
        inflationPressureKey,
        unemploymentBenefitsKey
    }
    
    private Map <KEYS,String> keyMap;
    
    private double workforce;
    private double numEmployedPriv;
    private double numEmployedPubl;
    private double numUnemployed;
    private double unemploymentBenefits;
    private double realOutput;
    private double realConsumption;
    private double realInvestment;
    private double realGovernmentSpending;
    private double realTaxRevenues;
    private double realExports;
    private double realImports;
    private double labourProductivity;
    private double priceLevel;
    private double realAveWagePriv;
    private double aveWagePriv;
    private double unemploymentRate;    
    private double employmentPressure;
    private double inflationPressure;
    private double expectedRealOutput;
    private Random random;
    
    // Pressure Weights
    private double w_ep_output; // Employment pressure weight 
    private double w_ip_output; // Inflation pressure weight
    private double w_ip_wage;   // Inflation pressure weight
    // Number Employed
    private double ne_wtw;
    private double ne_baseDeley;
    private double ne_f1;
    private double ne_f2;
    // Average Wage
    private double aw_min;
    private double aw_deley;
    private double aw_f1;
    private double aw_f2;
    
    
    public PrivateSectorModel() {}
    
    @Override
    public void configure( Registry r, Map <String,String> config ) {
        readKeys( config );
        readInitialValues(r);
        setupRandomizer( config );
        setupEmploymentParameters( config );
        setupAveWageParameters( config );
        setupPressureWeights( config );
    }

    public void execute( Registry r ) {
        readInputData(r);
        calculatePlannedOutput();
        calculateEmployment();
        calculateInflationPressure();
        calculateActualOutput();
        calculateAveWage();
        writeOutputData(r);
    }
    
    
    //*************************************************
    //********* MAIN MODEL'S LOGIC METHODS ************
    //*************************************************
    private void readInputData( Registry r ) {
        numEmployedPriv = getDouble( r, KEYS.numEmployedPrivKey );
        numEmployedPubl = getDouble( r, KEYS.numEmployedPublKey );
        workforce = getDouble( r, KEYS.workforceKey );
        unemploymentRate = getDouble( r, KEYS.unemploymentRateKey );
        unemploymentBenefits = getDouble( r, KEYS.unemploymentBenefitsKey );
        realGovernmentSpending = getDouble( r, KEYS.realGovernmentSpendingKey );
        realTaxRevenues = getDouble( r, KEYS.realTaxRevenuesKey );
        realConsumption = getDouble( r, KEYS.realConsumptionKey );
        realInvestment = getDouble( r, KEYS.realInvestmentKey );
        realImports = getDouble( r, KEYS.realImportsKey );
        realExports = getDouble( r, KEYS.realExportsKey );
        inflationPressure = getDouble( r, KEYS.inflationPressureKey );
        priceLevel = getDouble( r, KEYS.priceLevelKey );
        labourProductivity = getDouble( r, KEYS.labourProductivityKey );
    }

    private void calculatePlannedOutput() {
        expectedRealOutput = realConsumption+realInvestment+realGovernmentSpending+realExports-realImports-realTaxRevenues;
        if ( expectedRealOutput < 0 ) expectedRealOutput = 0;
        employmentPressure = w_ep_output*( expectedRealOutput/labourProductivity - numEmployedPriv );
    }
    
    private void calculateEmployment() {
        double employmentDelay;
        
        double willingness = ne_wtw*realAveWagePriv/unemploymentBenefits;
                        
        if ( employmentPressure > 0 ) {
            employmentDelay = trimDelay( Math.max( 0.0, ne_baseDeley+ne_f1*willingness+ne_f2 ) );
        } else {
            employmentDelay = trimDelay( ne_baseDeley );
        }
        
        double available = workforce - numEmployedPriv - numEmployedPubl;
        double dNumEmployedPriv = Math.min( available, employmentPressure ) / employmentDelay;
        
        numEmployedPriv += dNumEmployedPriv;
        if ( numEmployedPriv < 0 ) numEmployedPriv = 0;
        
        double minUnemploymentRate = 0.01+random.nextGaussian()*0.005;
        double minNumUnemployed = minUnemploymentRate*workforce;
        
        numUnemployed = workforce - numEmployedPriv - numEmployedPubl;
        if ( numUnemployed < minNumUnemployed ) {
            numEmployedPriv -= minNumUnemployed-numUnemployed;
            numUnemployed = minNumUnemployed;
        }
        
        unemploymentRate = numUnemployed/workforce;
        
    }
    
    private void calculateInflationPressure() {
        inflationPressure += w_ip_output * (expectedRealOutput/(numEmployedPriv*labourProductivity) - 1.0);
    }
    
    private void calculateActualOutput() {
        realOutput = round3( numEmployedPriv * labourProductivity );
    }
    
    private void calculateAveWage() {
        double expectedRealAveWagePriv = aw_f1*Math.exp(aw_f2*unemploymentRate);
        double wageGap = expectedRealAveWagePriv*priceLevel - aveWagePriv;
        double aveWagePriv_1 = aveWagePriv+random.nextDouble()*wageGap/aw_deley;
        if ( aveWagePriv_1 < aw_min*priceLevel ) aveWagePriv_1 = aw_min*priceLevel;
        inflationPressure += w_ip_wage*(aveWagePriv_1-aveWagePriv)/aveWagePriv;
        aveWagePriv = aveWagePriv_1;
        realAveWagePriv = aveWagePriv/priceLevel;
    }
    
    private void writeOutputData( Registry r ) {
        put( r, KEYS.outputKey, realOutput*priceLevel );
        put( r, KEYS.realOutputKey, realOutput );
        put( r, KEYS.aveWagePrivKey, aveWagePriv );
        put( r, KEYS.realAveWagePrivKey, realAveWagePriv );
        put( r, KEYS.numEmployedPrivKey, numEmployedPriv );
        put( r, KEYS.inflationPressureKey, inflationPressure );
        put( r, KEYS.unemploymentRateKey, unemploymentRate );
        put( r, KEYS.numUnemployed, numUnemployed );
    }
    
    
    //*************************************************
    //*********** PARAMETER SETUP METHODS *************
    //*************************************************
    private void readKeys( Map <String,String> config ) {
        keyMap = new HashMap <KEYS,String> ();
        for ( KEYS k : KEYS.values() ) {
            keyMap.put( k, config.get(k.toString()) );
        }
    }
    
    private void readInitialValues( Registry r ) {
        priceLevel = getDouble( r, KEYS.priceLevelKey );
        realAveWagePriv = getDouble( r, KEYS.realAveWagePrivKey );        
        unemploymentRate = getDouble( r, KEYS.unemploymentRateKey );
        workforce = getDouble( r, KEYS.workforceKey );
        numEmployedPubl = getDouble( r, KEYS.numEmployedPublKey );
        realOutput = getDouble( r, KEYS.realOutputKey );
        numEmployedPriv = getDouble( r, KEYS.numEmployedPrivKey );
        labourProductivity = getDouble( r, KEYS.labourProductivityKey );
        realOutput = getDouble( r, KEYS.realOutputKey );
        unemploymentBenefits = getDouble( r, KEYS.unemploymentBenefitsKey );
        aveWagePriv = realAveWagePriv*priceLevel;
    }
    
    private void setupRandomizer( Map <String,String> config ) {
        long seed = Long.parseLong( config.get( "seed" ) );
        if ( seed == 0 ) seed = System.currentTimeMillis();
        random = new Random( seed );
    }
    
    private void setupEmploymentParameters( Map <String,String> config ) {
        double d1 = Double.parseDouble( config.get("ne_d1") );
        ne_baseDeley = Double.parseDouble( config.get("ne_baseDeley") );
        ne_f2 = Double.parseDouble( config.get("ne_f2") );
        ne_f1 = d1-ne_baseDeley-ne_f2;
        ne_wtw = unemploymentBenefits/realAveWagePriv;
    }
    
    private void setupAveWageParameters( Map <String,String> config ) {
        aw_f1 = Double.parseDouble( config.get("aw_f1") );        
        aw_f2 = Math.log(realAveWagePriv/aw_f1)/unemploymentRate;
        aw_min = Double.parseDouble( config.get("aw_min") );        
        aw_deley = Double.parseDouble( config.get("aw_deley") );        
    }
    
    private void setupPressureWeights( Map <String,String> config ) {
        w_ep_output = Double.parseDouble( config.get( "w_ep_output" ) );
        w_ip_output = Double.parseDouble( config.get( "w_ip_output" ) );
        w_ip_wage = Double.parseDouble( config.get( "w_ip_wage" ) );
    }
        
    
    //***************************************************
    //************ PRIVATE UTILITY METHODS **************
    //***************************************************
    private static double trimDelay( double d ) {
        if ( d < 1.0 ) d = 1.0;
        return d;
    }

    private static final double round3( double value ) {
        return 0.001 * Math.round( 1000.0*value );
    }
    
    private static int round( double value ) {
        return (int) Math.round(value);
    }
    
    private double getDouble( Registry r, KEYS key ) {
        return r.getDouble( keyMap.get(key) );
    }
    
    private void put( Registry r, KEYS key, Object value ) {
        r.put( keyMap.get(key), value );
    }
    
}