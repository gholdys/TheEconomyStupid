package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.engine.PolicyKey;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.ActionLabel;
import eu.theeconomystupid.gui.components.NotifiableCard;
import eu.theeconomystupid.gui.components.TablePanel;
import java.text.NumberFormat;


final class PoliciesCard extends AbstractCenterCard implements NotifiableCard {
    
    
    private PolicyKey[] policyKeys;
    private NumberFormat[] valueFormats;
    private TablePanel table;
    
    
    PoliciesCard() {
        super("policies");
        
        Resources res = Resources.getInstance();
        
        NumberFormat percentFormat = res.getPercentFormat();
        NumberFormat billionFormat = res.getBillionFormat();
        NumberFormat integerFormat = res.getIntegerFormat();
        NumberFormat currencyFormat = res.getCurrencyFormat();

        String[] headerKeys = { 
            "game.scene.policies.policy", 
            "game.scene.policies.current", 
            "game.scene.policies.new" 
        };
        
        policyKeys = new PolicyKey[] {
            Keys.INCOME_TAX, 
            Keys.VAT, 
            Keys.UNEMPLOYMENT_BENEFITS, 
            Keys.RETIREMENT_BENEFITS, 
            Keys.PUBLIC_SECTOR_FUNDING
        };
        
        String[] chartKeys = {
            "incomeTax",
            "vat",
            "unemploymentBenefits",
            "retirementBenefits",
            "publicSectorFunding"
        };
        
        valueFormats = new NumberFormat[] {
            percentFormat,
            percentFormat,
            currencyFormat, 
            currencyFormat, 
            billionFormat
        };
        
        String[] targetCards = new String[policyKeys.length];
        String[] policyNames = new String[policyKeys.length];
        for ( int i=0; i<policyNames.length; i++ ) {
            targetCards[i] = "game.popups.editor_" + chartKeys[i];
            policyNames[i] = res.getText( "variables", policyKeys[i].toString() );
        }
        
        table = new TablePanel( headerKeys, policyNames, targetCards, ActionLabel.ICON_EDITOR );
        
        setContents( table );
        
    }
    
    
    // ******************************************************
    // ************ NOTIFIABLE CARD IMPLEMENTATION **********
    // ******************************************************
    public void aboutToBeShown() {
        GameEngine engine = GameEngine.getInstance();
        NumberFormat nf;
        double value0, value1;
        for ( int i=0; i<policyKeys.length; i++ ) {
            nf = valueFormats[i];
            value0 = engine.getCurrentPolicyValue( policyKeys[i] );
            value1 = engine.getPlannedPolicyValue( policyKeys[i] );
            table.valueLabels[i][0].setText( nf.format(value0) );
            table.valueLabels[i][1].setText( nf.format(value1) );
        }
    }
    
}
