package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;


public final class NationalDebtCalculator extends Operator {
    
    
    private String budgetDeficitKey;
    private String nationalDebtKey;
    private String nationalDebtToGdpKey;
    private String gdpKey;
    
    
    public NationalDebtCalculator() {}

    public void configure( Registry r, Map <String,String> config ) {
        budgetDeficitKey = config.get("budgetDeficitKey");
        nationalDebtKey = config.get("nationalDebtKey");
        nationalDebtToGdpKey = config.get("nationalDebtToGdpKey");
        gdpKey = config.get("gdpKey");
    }
    
    public void execute( Registry r ) {
        double budgetDeficit = r.getDouble( budgetDeficitKey );
        double nationalDebt = r.getDouble( nationalDebtKey );
        double gdp = r.getDouble( gdpKey );
        
        nationalDebt += budgetDeficit;
        if ( nationalDebt < 0 ) nationalDebt = 0;
                
        r.put( nationalDebtKey, nationalDebt );
        r.put( nationalDebtToGdpKey, nationalDebt/gdp );
    }
    
}
