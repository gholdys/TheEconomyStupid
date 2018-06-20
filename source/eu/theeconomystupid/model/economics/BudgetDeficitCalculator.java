package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;


public final class BudgetDeficitCalculator extends Operator{
    
    
    private String governmentSpendingKey;
    private String taxRevenuesKey;
    private String budgetDeficitKey;
    
    
    public BudgetDeficitCalculator() {}

    public void configure( Registry r, Map <String,String> config ) {
        governmentSpendingKey = config.get( "governmentSpendingKey" );
        taxRevenuesKey = config.get( "taxRevenuesKey" );
        budgetDeficitKey = config.get( "budgetDeficitKey" );
    }
    
    public void execute( Registry r ) {
        double govtSpending = r.getDouble( governmentSpendingKey );
        double taxRevenues = r.getDouble( taxRevenuesKey );
        double deficit = govtSpending - taxRevenues;
        r.put( budgetDeficitKey, deficit );
    }
    
}
