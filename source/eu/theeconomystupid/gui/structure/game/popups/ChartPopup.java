package eu.theeconomystupid.gui.structure.game.popups;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Key;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.LineChart;
import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.border.EmptyBorder;


public final class ChartPopup extends AbstractPopupCard {
    
    
    private static final Color[] PLOT_COLORS = { Color.DARK_GRAY, Color.LIGHT_GRAY };
    
    private LineChart chart;
    
    
    ChartPopup() {
        setName( "chart" );
        
        chart = new LineChart();
        chart.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );
        chart.setPreferredSize( new Dimension( 800, 600 ) );
        setContents( chart );
    }
    
    public void configureChart( Key nominalVariableKey, Key realVariableKey, NumberFormat yAxisFormat ) {
        configureChart( nominalVariableKey, realVariableKey, yAxisFormat, "game.scene" );
    }
    
    public void configureChart( Key nominalVariableKey, Key realVariableKey, NumberFormat yAxisFormat, String closeCardName ) {
        Resources res = Resources.getInstance();
        GameEngine engine = GameEngine.getInstance();
        
        setCloseCardName( closeCardName );
        setCaption( res.getText( "variables", nominalVariableKey.toString() ) );
        
        chart.setYAxisFormat( yAxisFormat );
        chart.clearDataSeries();
        chart.setInitTime( GameEngine.getInstance().getVariableHistoryStartingTime(nominalVariableKey) );
        if ( realVariableKey != null ) {
            chart.addDataSeries( PLOT_COLORS[0], res.getText( "gui", "game.popups.charts.nominalValue" ), engine.getVariableHistory( nominalVariableKey ) );
            chart.addDataSeries( PLOT_COLORS[1], res.getText( "gui", "game.popups.charts.realValue" ), engine.getVariableHistory( realVariableKey ) );
        } else {
            chart.addDataSeries( PLOT_COLORS[0], engine.getVariableHistory( nominalVariableKey ) );
        }
        
    }
        
}
