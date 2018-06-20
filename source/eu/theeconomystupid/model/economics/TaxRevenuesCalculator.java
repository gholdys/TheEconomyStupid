package eu.theeconomystupid.model.economics;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map;


public final class TaxRevenuesCalculator extends Operator {
    
    
    private String taxRevenuesKey;
    private String realTaxRevenuesKey;
    private String vatRateKey;
    private String vatRevenuesKey;
    private String incomeTaxRateKey;
    private String incomeTaxRevenuesKey;
    private String realConsumptionKey;
    private String realInvestmentKey;
    private String realExportsKey;
    private String realImportsKey;
    private String publicSectorFundingKey;
    private String priceLevelKey;
    
    
    public TaxRevenuesCalculator() {}

    public void configure( Registry r, Map <String,String> config ) {
        taxRevenuesKey = config.get("taxRevenuesKey");
        realTaxRevenuesKey = config.get("realTaxRevenuesKey");
        vatRateKey = config.get("vatRateKey");
        vatRevenuesKey = config.get("vatRevenuesKey");
        incomeTaxRateKey = config.get("incomeTaxRateKey");
        incomeTaxRevenuesKey = config.get("incomeTaxRevenuesKey");
        realConsumptionKey = config.get("realConsumptionKey");
        realInvestmentKey = config.get("realInvestmentKey");
        realExportsKey = config.get("realExportsKey");
        realImportsKey = config.get("realImportsKey");
        publicSectorFundingKey = config.get("publicSectorFundingKey");
        priceLevelKey = config.get("priceLevelKey");
    }
    
    public void execute( Registry r ) {
        double priceLevel = r.getDouble( priceLevelKey );
        
        double realConsumption = r.getDouble(realConsumptionKey);
        double realInvestment = r.getDouble(realInvestmentKey);
        double realExports = r.getDouble(realExportsKey);
        double realImports = r.getDouble(realImportsKey);
        double realPublicSectorFunding = r.getDouble(publicSectorFundingKey)/priceLevel;
        
        double incomeTaxRate = r.getDouble( incomeTaxRateKey );
        double vatRate = r.getDouble( vatRateKey );
        
        double incomeTaxBase = realConsumption + realInvestment + realExports + realPublicSectorFunding - realImports;
        double vatBase = realConsumption + realInvestment;
        
        double realIncomeTaxRevenues = Math.max( 0.0, round3(incomeTaxRate*incomeTaxBase) );
        double realVatRevenues = Math.max( 0.0, round3(vatRate*vatBase) );
        double realTaxRevenues = round3(realIncomeTaxRevenues + realVatRevenues);
        
        r.put( realTaxRevenuesKey, realTaxRevenues );
        r.put( taxRevenuesKey, realTaxRevenues*priceLevel );
        r.put( vatRevenuesKey, realVatRevenues*priceLevel );
        r.put( incomeTaxRevenuesKey, realIncomeTaxRevenues*priceLevel );
        
    }
    
    private static final double round3( double value ) {
        return 0.001 * Math.round( 1000.0*value );
    }
    
}
