package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;


public final class GovernmentSpendingCalculator extends Operator {
    
    
    private String governmentSpendingKey;
    private String realGovernmentSpendingKey;
    private String priceLevelKey;
    private String publicSectorFundingValueKey;
    private String publicSectorFundingCostsKey;
    private String retirementBenefitsValueKey;
    private String retirementBenefitsCostsKey;    
    private String unemploymentBenefitsValueKey;
    private String unemploymentBenefitsCostsKey;
    private String retiredNumPeopleKey;
    private String retiredAverageIncomeKey;
    private String retiredRealAverageIncomeKey;
    private String unemployedAverageIncomeKey;
    private String unemployedRealAverageIncomeKey;
    private String unemployedNumPeopleKey;
    
    
    public GovernmentSpendingCalculator() {}
    
    public void configure( Registry r, Map <String,String> config ) {
        governmentSpendingKey = config.get("governmentSpendingKey");
        realGovernmentSpendingKey = config.get("realGovernmentSpendingKey");
        priceLevelKey = config.get("priceLevelKey");
        publicSectorFundingValueKey = config.get("publicSectorFundingValueKey");
        publicSectorFundingCostsKey = config.get("publicSectorFundingCostsKey");
        retirementBenefitsValueKey = config.get("retirementBenefitsValueKey");
        retirementBenefitsCostsKey = config.get("retirementBenefitsCostsKey");
        unemploymentBenefitsValueKey = config.get("unemploymentBenefitsValueKey");
        unemploymentBenefitsCostsKey = config.get("unemploymentBenefitsCostsKey");
        retiredAverageIncomeKey = config.get("retiredAverageIncomeKey");
        retiredRealAverageIncomeKey = config.get("retiredRealAverageIncomeKey");
        retiredNumPeopleKey = config.get("retiredNumPeopleKey");
        unemployedAverageIncomeKey = config.get("unemployedAverageIncomeKey");
        unemployedRealAverageIncomeKey = config.get("unemployedRealAverageIncomeKey");
        unemployedNumPeopleKey = config.get("unemployedNumPeopleKey");
    }

    public void execute( Registry r ) {
        double priceLevel = r.getDouble( priceLevelKey );
        double publicSectorFunding = r.getDouble( publicSectorFundingValueKey );
        double retirementBenefits = r.getDouble( retirementBenefitsValueKey );
        double unemploymentBenefits = r.getDouble( unemploymentBenefitsValueKey );
        double numRetired = r.getDouble( retiredNumPeopleKey );
        double numUnemployed = r.getDouble( unemployedNumPeopleKey );
        
        double retirementBenefitsCosts = 1e-9*numRetired*retirementBenefits;
        double unemploymentBenefitsCosts = 1e-9*numUnemployed*unemploymentBenefits;
        double spending = Math.max( 0.0, retirementBenefitsCosts+unemploymentBenefitsCosts+publicSectorFunding );
        
        r.put( retirementBenefitsCostsKey, retirementBenefitsCosts );
        r.put( unemploymentBenefitsCostsKey, unemploymentBenefitsCosts );
        r.put( publicSectorFundingCostsKey, publicSectorFunding );
        r.put( retiredAverageIncomeKey, retirementBenefits );
        r.put( retiredRealAverageIncomeKey, retirementBenefits/priceLevel );
        r.put( unemployedAverageIncomeKey, unemploymentBenefits );
        r.put( unemployedRealAverageIncomeKey, unemploymentBenefits/priceLevel );
        r.put( governmentSpendingKey, spending );
        r.put( realGovernmentSpendingKey, round3(spending/priceLevel) );
    }
    
    private static final double round3( double value ) {
        return 0.001 * Math.round( 1000.0*value );
    }
    
}