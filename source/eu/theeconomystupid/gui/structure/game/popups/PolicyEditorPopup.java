package eu.theeconomystupid.gui.structure.game.popups;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.engine.PolicyKey;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.NotifiableCard;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


final class PolicyEditorPopup extends AbstractPopupCard implements NotifiableCard, MouseListener {
    
    
    private static final int MIN_WIDTH = 300;
    private static final Font BUTTON_FONT = new Font( Font.SERIF, Font.BOLD, 14 );
    private static final Font LABEL_FONT = new Font( Font.SERIF, Font.BOLD, 18 );
    
    private Object[] config;
    private PolicyKey policyKey;
    private int policyIndex;
    private NumberFormat valueFormat;
    private NumberFormat costsRevenuesFormat;
    private JLabel valueLabel;
    private JLabel costsRevenuesLabel;
    private double value;
    private Timer timer;
    private JButton decrementButton;
    private JButton incrementButton;
    
    
    PolicyEditorPopup( PolicyKey policyKey ) {
        
        this.policyKey = policyKey;
        policyIndex = -1;
        for ( int i=0; i<Keys.getPolicyKeys().length; i++ ) {
            if ( policyKey == Keys.getPolicyKeys()[i] ) {
                policyIndex = i;
                break;
            }
        }
        
        Resources res = Resources.getInstance();        
        NumberFormat percentFormat = res.getPercentFormat();
        NumberFormat currencyFormat = res.getCurrencyFormat();
        NumberFormat billionFormat = res.getBillionFormat();
        NumberFormat integerFormat = res.getIntegerFormat();
        
        String gpe = "game.popups.editor.";
        
        config = new Object[] {
            "incomeTax", gpe+"taxRate", gpe+"revenues", percentFormat, 0.0, 0.5, 0.01,
            "vat", gpe+"taxRate", gpe+"revenues", percentFormat, 0.0, 0.5, 0.01,
            "unemploymentBenefits", gpe+"aveBenefit", gpe+"costs", currencyFormat, 100.0, 10000.0, 100.0,
            "retirementBenefits", gpe+"aveBenefit", gpe+"costs", currencyFormat, 100.0, 10000.0, 100.0,
            "publicSectorFunding", gpe+"funds", gpe+"costs", billionFormat, 4.0, 20.0, 0.1
        };
        
        setName( "editor_" + config[policyIndex*7] );
        setCaption( res.getText( "variables", "policies." + config[policyIndex*7] ) );
        
        JPanel p0 = new JPanel();
        p0.setLayout( new BoxLayout( p0, BoxLayout.PAGE_AXIS ) );
        p0.setOpaque( false );
        p0.add( Box.createRigidArea( new Dimension( MIN_WIDTH, 0 ) ) );
        p0.add( createCenterPanel() );
        p0.add( createBottomPanel() );        
        p0.setBorder( new EmptyBorder( 10, 50, 10, 50 ) );
        
        setContents( p0 );
    }
    
    private JPanel createCenterPanel() {
        
        String key1 = (String) config[policyIndex*7+1];
        String key2 = (String) config[policyIndex*7+2];
        
        JLabel label1 = new JLabel( Resources.getInstance().getText( "gui", key1 ) );
        label1.setFont( LABEL_FONT );
        label1.setHorizontalAlignment( JLabel.TRAILING );
        
        JLabel label2 = new JLabel( Resources.getInstance().getText( "gui", key2 ) );
        label2.setFont( LABEL_FONT );
        label2.setHorizontalAlignment( JLabel.TRAILING );
        
        decrementButton = new JButton("<");
        decrementButton.setMargin( new Insets(0,0,0,0) );
        decrementButton.setFont( LABEL_FONT );
        decrementButton.addMouseListener( this );
        
        incrementButton = new JButton(">");
        incrementButton.setMargin( new Insets(0,0,0,0) );
        incrementButton.setFont( LABEL_FONT );
        incrementButton.addMouseListener( this );
        
        valueLabel = new JLabel();
        valueLabel.setFont( LABEL_FONT );
        valueLabel.setHorizontalAlignment( JLabel.TRAILING );
        valueLabel.setOpaque( true );
        valueLabel.setBackground( Color.WHITE );
        valueLabel.setBorder( new CompoundBorder( new EtchedBorder(), new EmptyBorder(0, 5, 0, 5) ) );
        
        costsRevenuesLabel = new JLabel();
        costsRevenuesLabel.setFont( LABEL_FONT );
        costsRevenuesLabel.setHorizontalAlignment( JLabel.TRAILING );
        
        costsRevenuesFormat = Resources.getInstance().getBillionFormat();
        valueFormat = (NumberFormat) config[policyIndex*7+3];
        
        JPanel p0 = new JPanel() {
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        p0.setLayout( new GridBagLayout() );
        p0.setOpaque( false );
        p0.setBorder( new EmptyBorder( 20, 0, 20, 0 ) );
        p0.setAlignmentX( 0.5f );
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0.0;
        c.insets = new Insets( 10, 0, 10, 40 );
        p0.add( label1, c );
        
        c.gridx = 1;
        c.weightx = 0.0;
        c.insets.right = 0;
        p0.add( decrementButton, c );
        
        c.gridx = 2;
        c.weightx = 1.0;
        p0.add( valueLabel, c );
        
        c.gridx = 3;
        c.insets.right = 0;
        c.insets.left = 0;
        c.weightx = 0.0;
        p0.add( incrementButton, c );
        
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 0.0;
        c.insets.right = 40;
        p0.add( label2, c );
        
        c.gridx = 2;
        c.insets.right = 0;
        c.weightx = 1.0;
        p0.add( costsRevenuesLabel, c );
        
        return p0;
    }
    
    private JPanel createBottomPanel() {        
        
        JButton cancelButton = new JButton( Resources.getInstance().getText("gui","game.popups.editor.cancel") );
        cancelButton.setFont( BUTTON_FONT );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCancel();
            }
        });
        
        JButton okButton = new JButton( Resources.getInstance().getText("gui","game.popups.editor.ok") );
        okButton.setFont( BUTTON_FONT );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOK();
            }
        });
        
        JPanel p0 = new JPanel( new FlowLayout() );
        p0.setOpaque( false );
        p0.setAlignmentX( 0.5f );
        p0.add( cancelButton );
        p0.add( okButton );
        
        return p0;
    }
    
    private void handleDecrementation() {
        GameEngine engine = GameEngine.getInstance();
        value -= (Double) config[policyIndex*7+6];
        if ( value < (Double) config[policyIndex*7+4] ) value = (Double) config[policyIndex*7+4];
        valueLabel.setText( valueFormat.format(value) );
        if ( policyIndex < 2 ) {
            costsRevenuesLabel.setText( costsRevenuesFormat.format( 
                engine.calculateEstimatedPolicyRevenues( policyKey, value ) 
            ) );
        } else {
            costsRevenuesLabel.setText( costsRevenuesFormat.format( 
                engine.calculateEstimatedPolicyCosts( policyKey, value ) 
            ) );
        }
    }
    
    private void handleIncrementation() {
        GameEngine engine = GameEngine.getInstance();
        value += (Double) config[policyIndex*7+6];
        if ( value > (Double) config[policyIndex*7+5] ) value = (Double) config[policyIndex*7+5];
        valueLabel.setText( valueFormat.format(value) );
        if ( policyIndex < 2 ) {
            costsRevenuesLabel.setText( costsRevenuesFormat.format( 
                engine.calculateEstimatedPolicyRevenues( policyKey, value ) 
            ) );
        } else {
            costsRevenuesLabel.setText( costsRevenuesFormat.format( 
                engine.calculateEstimatedPolicyCosts( policyKey, value ) 
            ) );
        }
    }
    
    private void handleOK() {
        GameEngine.getInstance().setPlannedPolicyValue( policyKey, value );
        GameGUI.getInstance().showCard("game.scene");
    }

    private void handleCancel() {        
        GameGUI.getInstance().showCard("game.scene");
    }

    
    // ******************************************************
    // ************ NOTIFIABLE CARD IMPLEMENTATION **********
    // ******************************************************
    public void aboutToBeShown() {
        GameEngine engine = GameEngine.getInstance();
        value = engine.getPlannedPolicyValue( policyKey );
        valueLabel.setText( valueFormat.format(value) );
        if ( policyIndex < 2 ) {
            costsRevenuesLabel.setText( costsRevenuesFormat.format( 
                engine.getPlannedPolicyRevenues( policyKey ) 
            ) );
        } else {
            costsRevenuesLabel.setText( costsRevenuesFormat.format( 
                engine.getPlannedPolicyCosts( policyKey ) 
            ) );
        }
    }

    
    // ******************************************************
    // ************ MOUSE LISTENER IMPLEMENTATION ***********
    // ******************************************************
    public void mousePressed( MouseEvent e ) {
        if ( e.getSource() == incrementButton ) {
            handleIncrementation();
            timer = new Timer( 50, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleIncrementation();
                }
            } );
            
        } else {
            handleDecrementation();
            timer = new Timer( 50, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleDecrementation();
                }
            } );
        }
        timer.setInitialDelay( 500 );
        timer.start();
    }

    public void mouseReleased(MouseEvent e) {
        timer.stop();
        timer = null;
    }

    public void mouseClicked(MouseEvent e) {
        // do nothing
    }

    public void mouseEntered(MouseEvent e) {
        // do nothing
    }

    public void mouseExited(MouseEvent e) {
        // do nothing
    }
    
}