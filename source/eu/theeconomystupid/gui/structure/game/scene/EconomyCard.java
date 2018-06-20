package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Key;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.ActionLabel;
import eu.theeconomystupid.gui.components.TablePanel;
import java.text.NumberFormat;
import javax.swing.Action;


final class EconomyCard extends AbstractCenterCard {
    
    
    private Key[] variableKeys;
    private NumberFormat[] valueFormats;
    private TablePanel table;
    
    
    EconomyCard() {
        super( "economy" );

        Resources res = Resources.getInstance();
        
        NumberFormat billionFormat = res.getBillionFormat();
        NumberFormat percentFormat = res.getPercentFormat();

        String[] headerKeys = { 
            "game.scene.economy.variable", 
            "game.scene.economy.value", 
            "game.scene.economy.change" 
        };
        
        variableKeys = new Key[] {
            Keys.GDP, 
            Keys.GDP_GROWTH_RATE,
            Keys.UNEMPLOYMENT_RATE,
            Keys.INFLATION_RATE,
            Keys.OUTPUT,
            Keys.CONSUMPTION,
            Keys.INVESTMENT,
            Keys.EXPORTS,
            Keys.IMPORTS,
            Keys.GOVERNMENT_SPENDING,
            Keys.TAX_REVENUES
        };
        
        Key[] realVariableKeys = new Key[] {
            null, 
            null,
            null,
            null,
            Keys.REAL_OUTPUT,
            Keys.REAL_CONSUMPTION,
            Keys.REAL_INVESTMENT,
            Keys.REAL_EXPORTS,
            Keys.REAL_IMPORTS,
            Keys.REAL_GOVERNMENT_SPENDING,
            Keys.REAL_TAX_REVENUES
        };
        
        valueFormats = new NumberFormat[] {
            billionFormat,
            percentFormat,
            percentFormat,
            percentFormat,
            billionFormat,
            billionFormat,
            billionFormat,
            billionFormat,
            billionFormat,
            billionFormat,
            billionFormat
        };
        
        Action[] actions = new Action[variableKeys.length];
        for ( int i=0; i<variableKeys.length; i++ ) {
            actions[i] = new ActionLabel.ShowChartAction( variableKeys[i], realVariableKeys[i], valueFormats[i] );
        }
        
        table = new TablePanel( headerKeys, actions, ActionLabel.ICON_CHARTS );
        
        setContents( table );
    }
    
    protected void refreshData() {
        GameEngine engine = GameEngine.getInstance();
        
        NumberFormat billionFormat = Resources.getInstance().getBillionFormat();
        NumberFormat decimalFormat = Resources.getInstance().getDecimalFormat();
        NumberFormat percentFormat = Resources.getInstance().getPercentFormat();
        
        NumberFormat nf;
        double value, delta;
        for ( int i=0; i<valueFormats.length; i++ ) {
            nf = valueFormats[i];
            if ( nf == billionFormat ) {
                value = (Double) engine.getVariableValue( variableKeys[i] );
                delta = (Double) engine.getVariableRelativeDelta( variableKeys[i] );
                table.valueLabels[i][0].setText( nf.format( value ) );
                if ( delta > 0.0 ) {
                    table.valueLabels[i][1].setText( "+" + percentFormat.format( delta ) );
                } else {
                    table.valueLabels[i][1].setText( percentFormat.format( delta ) );
                }
            } else {
                value = (Double) engine.getVariableValue( variableKeys[i] );
                delta = (Double) engine.getVariableAbsoluteDelta( variableKeys[i] );
                table.valueLabels[i][0].setText( nf.format( value ) );
                if ( delta > 0.0 ) {
                    table.valueLabels[i][1].setText( "+" + decimalFormat.format( 100.0*delta ) );
                } else {
                    table.valueLabels[i][1].setText( decimalFormat.format( 100.0*delta ) );
                }
            }
        }
        
    }
    
}
