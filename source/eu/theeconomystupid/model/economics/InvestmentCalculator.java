package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public final class InvestmentCalculator extends Operator {
 
    
    private String priceLevelKey;
    private String inflationRateKey;
    private String realInvestmentKey;
    private String investmentKey;
    private String nationalDebtKey;
    private String publicSectorEffectivenessKey;
    private String realAveWageInPrivateSectorKey;
    private double infl_a, infl_b;
    private double ndebt_a, ndebt_b;
    private double pse_a, pse_b;
    private double aw_a, aw_b;
    private double baseInvestment;
    private double deley;
    
    
    
    public InvestmentCalculator() {}
    
    public void configure( Registry r, Map <String,String> config ) {
        priceLevelKey = config.get( "priceLevelKey" );
        inflationRateKey = config.get( "inflationRateKey" );
        realInvestmentKey = config.get( "realInvestmentKey" );
        investmentKey = config.get( "investmentKey" );
        nationalDebtKey = config.get( "nationalDebtKey" );
        publicSectorEffectivenessKey = config.get( "publicSectorEffectivenessKey" );
        realAveWageInPrivateSectorKey = config.get( "realAveWageInPrivateSectorKey" );
        
        double inflationRate = r.getDouble( inflationRateKey );
        baseInvestment = r.getDouble( realInvestmentKey );
        
        // Investment factors:
        // -> Inflation rate factor:
        infl_b = Double.parseDouble( config.get("infl_b") );
        infl_a = (1.0-infl_b)/(inflationRate*inflationRate);
        // -> National debt factor:
        ndebt_a = Double.parseDouble( config.get("ndebt_a") );
        ndebt_b = Math.log(1.0/ndebt_a)/r.getDouble( nationalDebtKey );
        // -> Public sector effectiveness factor:
        pse_a = Double.parseDouble( config.get("pse_a") );
        pse_b = 1.0-pse_a*r.getDouble( publicSectorEffectivenessKey );
        // -> Private sector average wage factor:
        aw_a = Double.parseDouble( config.get("aw_a") );
        aw_b = 1.0-aw_a*r.getDouble( realAveWageInPrivateSectorKey );
        
        // Deley:
        deley = Double.parseDouble( config.get("deley") );
    }
    
    public void execute( Registry r ) {
        double realInvestment = r.getDouble( realInvestmentKey );
        double inflationRate = r.getDouble( inflationRateKey );
        
        double infl = Math.max( 0.0, infl_a*inflationRate*inflationRate+infl_b );
        double ndebt = Math.max( 0.0, ndebt_a*Math.exp( ndebt_b*r.getDouble( nationalDebtKey ) ) );
        double pse = Math.max( 0.0, pse_a*r.getDouble( publicSectorEffectivenessKey ) + pse_b );
        double aw = Math.max( 0.0, aw_a*r.getDouble( realAveWageInPrivateSectorKey ) + aw_b );

        double realInvestment_1 = Math.max( 0.0, baseInvestment*(infl+ndebt+pse+aw)/4.0 );
        
        realInvestment += (realInvestment_1-realInvestment)/deley;
        double investment = realInvestment*r.getDouble( priceLevelKey );
                
        r.put( realInvestmentKey, realInvestment );
        r.put( investmentKey, investment );
        
    }
    
}