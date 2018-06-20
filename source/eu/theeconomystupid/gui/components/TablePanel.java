package eu.theeconomystupid.gui.components;


import eu.theeconomystupid.gui.Resources;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;


public class TablePanel extends JPanel {
    
    
    public static final Font FONT = new Font( Font.SERIF, Font.BOLD, 14 );

    public final JLabel[][] valueLabels;
    

    public TablePanel( String[] headerKeys, String[] variableNames, String[] targetCards, int iconType ) {
        this( headerKeys, createShowCardActions( variableNames, targetCards ), iconType );
    }
    
    public TablePanel( String[] headerKeys, Action[] actions, int iconType ) {
        valueLabels = new JLabel[ actions.length ][ headerKeys.length-1 ];
        
        setLayout( new GridBagLayout() );
        setOpaque( false );
        setBorder( new EmptyBorder(10, 40, 5, 40) );
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.weightx = 1.0;
        
        JLabel[] headerLabels = new JLabel[ headerKeys.length ];
        JLabel label;
        for ( int i=0; i<headerKeys.length; i++ ) {
            label = createHeaderLabel( headerKeys[i] );
            c.gridx = i;
            if ( i == headerKeys.length-1 ) {
                label.setBorder( new CompoundBorder( new MatteBorder( 0, 0, 1, 0, Color.GRAY ), new EmptyBorder(10, 0, 10, 0) ) );
            } else {
                label.setBorder( new CompoundBorder( new MatteBorder( 0, 0, 1, 1, Color.GRAY ), new EmptyBorder(10, 0, 10, 0) ) );
            }
            headerLabels[i] = label;
            add( label, c );
        }
        
        JComponent comp;
        for ( int i=0; i<actions.length; i++ ) {
            c.gridy = i+1;
            c.gridx = 0;
            comp = createVariableNameLabel( actions[i], iconType );
            comp.setBorder( new CompoundBorder( new MatteBorder( 0, 0, 0, 1, Color.GRAY ), new EmptyBorder(2, 5, 2, 5) ) );
            add( comp, c  );
            
            for ( int j=1; j<headerKeys.length; j++ ) {
                c.gridx = j;
                comp = createValueLabel();
                if ( j == headerKeys.length-1 ) {
                    comp.setBorder( new EmptyBorder(2, 5, 2, 5) );
                } else {
                    comp.setBorder( new CompoundBorder( new MatteBorder( 0, 0, 0, 1, Color.GRAY ), new EmptyBorder(2, 5, 2, 5) ) );
                }
                valueLabels[i][j-1] = (JLabel) comp;
                add( comp, c );
            }
            
        }
    }
    
    protected JLabel createHeaderLabel( String textKey ) {
        JLabel label = new JLabel( Resources.getInstance().getText( "gui", textKey ) );
        label.setFont( FONT );
        label.setHorizontalAlignment( JLabel.CENTER );
        label.setVerticalAlignment( JLabel.CENTER );
        return label;
    }

    protected JComponent createVariableNameLabel( Action action, int icon ) {
        ActionLabel label = new ActionLabel( action, icon );
        label.setFont( FONT );
        label.setAlignmentX(0f);
        return label;
    }
    
    protected JLabel createValueLabel() {
        JLabel label = new JLabel(" ");
        label.setFont( FONT );
        label.setHorizontalAlignment( JLabel.TRAILING );
        label.setVerticalAlignment( JLabel.CENTER );
        return label;
    }
    
    
    private static final Action[] createShowCardActions( String[] variableNames, String[] targetCards ) {
        Action[] actions = new Action[variableNames.length];
        for ( int i=0; i<variableNames.length; i++ ) {
            actions[i] = new ActionLabel.ShowCardAction( variableNames[i], targetCards[i] );
        }
        return actions;
    }
    
}
