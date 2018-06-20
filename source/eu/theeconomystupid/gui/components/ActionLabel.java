package eu.theeconomystupid.gui.components; 


import eu.theeconomystupid.engine.Key;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.structure.game.popups.ChartPopup;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;


public final class ActionLabel extends JComponent {
    
    
    public static final int ICON_CHARTS = 0;
    public static final int ICON_DETAILS = 1;
    public static final int ICON_EDITOR = 2;
    
    private static final String[] ICON_KEYS = { "chart", "details", "editor" };
    
    
    private JButton button;
    private JLabel label;
    private Action action;
    
    
    public ActionLabel( String text, int icon, String targetCardName ) {
        this( new ShowCardAction( text, targetCardName ), icon );
    }
    
    public ActionLabel( Action action, int icon ) {
        String iconKey = ICON_KEYS[icon];
        this.action = action;
        
        Resources res = Resources.getInstance();
        
        button = new JButton();
        button.setIcon( res.getImageIcon( iconKey+"_0.png" ) );
        button.setRolloverIcon( res.getImageIcon( iconKey+"_1.png" ) );
        button.setPressedIcon( res.getImageIcon( iconKey+"_2.png" ) );        
        button.setToolTipText( res.getText( "gui", "game.buttons." + iconKey + ".tooltip" ) );
        button.setMargin( new Insets(0,0,0,5) );
        button.setBorderPainted( false );
        button.setContentAreaFilled( false );
        button.setFocusPainted( false );
        button.setAlignmentY(0.5f);
        button.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeAction();
            }
        });
        
        label = new JLabel( (String) action.getValue( Action.NAME )  );
        label.setAlignmentY(0.5f);
        
        setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
        add( button );
        add( label );
        
    }
    
    public void setFont( Font f ) {
        label.setFont(f);
    }
    
    private void executeAction() {
        if ( action != null ) action.actionPerformed( new ActionEvent( this, 0, null ) );
    }
    
    
    public static final class ShowCardAction extends AbstractAction {
        
        private String targetCardName;
        
        public ShowCardAction( String text, String targetCardName ) {
            putValue( NAME, text );
            this.targetCardName = targetCardName;
        }
        
        public void actionPerformed( ActionEvent e ) {
            GameGUI.getInstance().showCard( targetCardName );
        }
        
    }
    
    public static final class ShowChartAction extends AbstractAction {
        
        private Key nominalVariableKey;
        private Key realVariableKey;
        private NumberFormat yAxisFormat;
        private String closeCardName;
        
        public ShowChartAction( Key nominalVariableKey, NumberFormat yAxisFormat ) {
            this( nominalVariableKey, null, yAxisFormat, "game.scene" );
        }
        
        public ShowChartAction( Key nominalVariableKey, NumberFormat yAxisFormat, String closeCardName ) {
            this( nominalVariableKey, null, yAxisFormat, closeCardName );
        }
        
        public ShowChartAction( Key nominalVariableKey, Key realVariableKey, NumberFormat yAxisFormat ) {
            this( nominalVariableKey, realVariableKey, yAxisFormat, "game.scene" );
        }
        
        public ShowChartAction( Key nominalVariableKey, Key realVariableKey, NumberFormat yAxisFormat, String closeCardName ) {
            this.nominalVariableKey = nominalVariableKey;
            this.realVariableKey = realVariableKey;
            this.yAxisFormat = yAxisFormat;
            this.closeCardName = closeCardName;
            putValue( NAME, Resources.getInstance().getText( "variables", nominalVariableKey.toString() ) );
        }
        
        public void actionPerformed(ActionEvent e) {
            ChartPopup cp = (ChartPopup) GameGUI.getInstance().getCard( "game.popups.chart" );
            cp.configureChart( nominalVariableKey, realVariableKey, yAxisFormat, closeCardName );
            GameGUI.getInstance().showCard( "game.popups.chart" );
        }
        
    }
    
}
