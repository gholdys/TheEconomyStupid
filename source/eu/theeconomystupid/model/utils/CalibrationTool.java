package eu.theeconomystupid.model.utils;


import eu.theeconomystupid.model.framework.core.Registry;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;


public final class CalibrationTool {
    
    
    private static enum Keys {
        gameTime,
        
        gdp,
        workforce,
        unemploymentRate,
        inflationRate,
        realOutput,
        realConsumption,
        realInvestment,
        realImports,
        realExports,
        realGovernmentSpending,
        realTaxRevenues,
        labourProductivity,
        priceLevel,
        
        totalPopulation,
        maturityAge,
        maximumAge,
        retirementAge,
        
        publicSectorEffectiveness,
        publicSectorFunding,
        publicSectorOptimumEmplToPop,
        publicSectorOptimumFundToGDP,
        
        retirementBenefitsCosts,
        unemploymentBenefitsCosts,
        
        numUnemployed,
        unemployedAveGrossIncome,
        
        numPrivateSectorEmployees,        
        privateSectorEmployeesAveGrossIncome,
        
        numPublicSectorEmployees,
        publicSectorEmployeesAveGrossIncome,
        
        numRetired,
        retiredAveGrossIncome,        
        
        vatRate,
        vatRevenues,
        
        incomeTaxRate,
        incomeTaxRevenues
    };

    
    private Map <Keys,List<String>> keyMap;
    private Registry r;
    
    
    public CalibrationTool( InputStream configStream ) {
        keyMap = new HashMap <Keys,List<String>> ();
        try {
            PropertyResourceBundle prb = new PropertyResourceBundle( configStream );
            String value;
            StringTokenizer tokenizer;
            List <String> keyList;
            for ( Keys key : Keys.values() ) {
                value = prb.getString( key.toString() );
                tokenizer = new StringTokenizer(value, ",");
                keyList = new ArrayList <String> ();
                while (tokenizer.hasMoreTokens()) {
                    keyList.add( tokenizer.nextToken().trim() );
                }
                keyMap.put( key, keyList );
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }
    
    public void calibrate( Registry r ) {
        this.r = r;
        setDefaulValues();
        calculateWorkforceAndRetired();
        calculateNumUnemployed();
        calculateNumEmployedInPrivateSector();
        calculateGovernmentSpending();
        calculateTaxRevenues();
        calculateOutput();
        calculateLabourProductivity();
        calculatePublicSectorEffectiveness();
        synchronizeVariables();
        this.r = null;
    }
    
    
    //*************************************************
    //**************** PRIVATE METHODS ****************
    //*************************************************
    private void setDefaulValues() {
        //Calendar calendar = new GregorianCalendar();
        //put( Keys.gameTime, calendar.getTime() );
        put( Keys.priceLevel, 1.0 );
    }        
    
    private void calculateWorkforceAndRetired() {
        double totalPopulation = getDouble( Keys.totalPopulation );
        double maturityAge = getInteger( Keys.maturityAge );
        double maximumAge = getInteger( Keys.maximumAge );
        double retirementAge = getDouble( Keys.retirementAge );
        
        double n = totalPopulation/maximumAge;
        double workforce = Math.round( (retirementAge-maturityAge)*n );
        double numRetired = Math.round( (maximumAge-retirementAge)*n );
        
        put( Keys.workforce, workforce );
        put( Keys.numRetired, numRetired );
    }
    
    private void calculateNumUnemployed() {
        double workforce = getDouble( Keys.workforce );
        double unempl = getDouble( Keys.unemploymentRate );
        double numUnemployed = Math.round(unempl*workforce);
        put( Keys.numUnemployed, numUnemployed );
    }
    
    private void calculateNumEmployedInPrivateSector() {
        double workforce = getDouble( Keys.workforce );
        double numEmployed_publ = getDouble( Keys.numPublicSectorEmployees );
        double numUnemployed = getDouble( Keys.numUnemployed );
        double numEmpl_priv = Math.round( workforce - numUnemployed - numEmployed_publ );
        put( Keys.numPrivateSectorEmployees, numEmpl_priv );
    }
    
    private void calculateGovernmentSpending() {
        double priceLevel = getDouble( Keys.priceLevel );
        double publicSectorFunding = getDouble( Keys.publicSectorFunding );
        double retirementBenefits = getDouble( Keys.retiredAveGrossIncome );
        double unemploymentBenefits = getDouble( Keys.unemployedAveGrossIncome );
        double numRetired = getDouble( Keys.numRetired );
        double numUnemployed = getDouble( Keys.numUnemployed );
        
        double retirementBenefitsCosts = 1e-9*numRetired*retirementBenefits;
        double unemploymentBenefitsCosts = 1e-9*numUnemployed*unemploymentBenefits;
        double spending = retirementBenefitsCosts+unemploymentBenefitsCosts+publicSectorFunding;
        
        put( Keys.retirementBenefitsCosts, retirementBenefitsCosts );
        put( Keys.unemploymentBenefitsCosts, unemploymentBenefitsCosts );
        put( Keys.realGovernmentSpending, spending/priceLevel );
    }
    
    private void calculateTaxRevenues() {
        double realConsumption = getDouble(Keys.realConsumption);
        double realInvestment = getDouble(Keys.realInvestment);
        double realExports = getDouble(Keys.realExports);
        double realImports = getDouble(Keys.realImports);
        double publicSectorFunding = getDouble(Keys.publicSectorFunding);
        
        double incomeTaxRate = getDouble( Keys.incomeTaxRate );
        double vatRate = getDouble( Keys.vatRate );
        double priceLevel = getDouble( Keys.priceLevel );
        
        double incomeTaxBase = realConsumption + realInvestment + realExports + publicSectorFunding - realImports;
        double vatBase = realConsumption + realInvestment;
        
        double realIncomeTaxRevenues = incomeTaxRate*incomeTaxBase;
        double realVatRevenues = vatRate*vatBase;
        double realTaxRevenues = realIncomeTaxRevenues + realVatRevenues;
        
        put( Keys.realTaxRevenues, realTaxRevenues );
        put( Keys.vatRevenues, realVatRevenues*priceLevel );
        put( Keys.incomeTaxRevenues, realIncomeTaxRevenues*priceLevel );
    }
    
    private void calculateOutput() {
        double realConsumption = getDouble(Keys.realConsumption);
        double realInvestment = getDouble(Keys.realInvestment);
        double realExports = getDouble(Keys.realExports);
        double realImports = getDouble(Keys.realImports);
        double realTaxRevenues = getDouble(Keys.realTaxRevenues);
        double realGovernmentSpending = getDouble(Keys.realGovernmentSpending);
        double realOutput = realConsumption+realInvestment+realGovernmentSpending+realExports-realImports-realTaxRevenues;
        put( Keys.realOutput, realOutput );
        put( Keys.gdp, realOutput*12 );
    }
    
    private void calculateLabourProductivity() {
        double output = getDouble( Keys.realOutput );
        double n = getDouble( Keys.numPrivateSectorEmployees );
        put( Keys.labourProductivity, output/n );
    }
    
    private void calculatePublicSectorEffectiveness() {
        double numEmployedPubl = getDouble( Keys.numPublicSectorEmployees );
        double population = getDouble( Keys.totalPopulation );
        double gdp = getDouble( Keys.gdp );
        double annualFunding = 12.0*getDouble( Keys.publicSectorFunding );
        double optimumEmploymentToPopulation = getDouble( Keys.publicSectorOptimumEmplToPop );
        double optimumFundingToGDP = getDouble( Keys.publicSectorOptimumFundToGDP );
        
        double effectiveness = Math.min( 1.0, 0.5*((annualFunding/gdp)/optimumFundingToGDP + (numEmployedPubl/population)/optimumEmploymentToPopulation) );
        
        put( Keys.publicSectorEffectiveness, effectiveness );
    }
    
    private void synchronizeVariables() {
        Object value;
        for ( List<String> keyList : keyMap.values() ) {
            value = r.get( keyList.get(0) );
            for ( String s : keyList ) {
                r.put( s, value );
            }
        }
    }
    
    
    //*****************************************
    //*********** UTILITY METHODS *************
    //*****************************************    
    private Object get( Keys key ) {
        return r.get( keyMap.get( key ).get(0) );
    }
    
    private double getDouble( Keys key ) {
        return r.getDouble( keyMap.get( key ).get(0) );
    }
    
    private int getInteger( Keys key ) {
        return r.getInteger( keyMap.get( key ).get(0) );
    }
    
    private void put( Keys key, Object value ) {
        List <String> keyList = keyMap.get( key );
        for ( String s : keyList ) {
            r.put( s, value );
        }
    }    
    
}