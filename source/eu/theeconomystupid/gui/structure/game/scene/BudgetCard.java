package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Key;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.engine.PolicyKey;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.ActionLabel;
import eu.theeconomystupid.gui.components.NotifiableCard;
import eu.theeconomystupid.gui.components.TablePanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;
import java.util.Random;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;


final class BudgetCard extends AbstractCenterCard implements NotifiableCard {
    
    
    private PolicyKey[] policyKeys;
    private JLabel currentTotalLabel;
    private JLabel plannedTotalLabel;
    private JLabel budgetDeficitLabel;
    private JLabel nationalDebtLabel;
    private JLabel nationalDebtToGdpLabel;
    private TablePanel table;
    
    
    BudgetCard() {
        super( "budget" );
        
        JPanel p0 = createTopPanel();
        table = createTablePanel();
        
        JPanel p1 = new JPanel();
        p1.setOpaque( false );
        p1.setLayout( new BoxLayout( p1, BoxLayout.PAGE_AXIS ) );
        p1.add( Box.createRigidArea( new Dimension(0, 50) ) );
        p1.add( p0 );
        p1.add( Box.createRigidArea( new Dimension(0, 10) ) );
        p1.add( table );
        p1.add( Box.createGlue() );
        
        setContents( p1 );
    }
    
    private JPanel createTopPanel() {
        Resources res = Resources.getInstance();
        
        ActionLabel budgetDeficitCaptionLabel = new ActionLabel( 
            new ActionLabel.ShowChartAction( Keys.BUDGET_DEFICIT, res.getBillionFormat() ),
            ActionLabel.ICON_CHARTS
        );
        ActionLabel nationalDebtCaptionLabel = new ActionLabel( 
            new ActionLabel.ShowChartAction( Keys.NATIONAL_DEBT, res.getBillionFormat() ),
            ActionLabel.ICON_CHARTS
        );
        ActionLabel nationalDebtToGdpCaptionLabel = new ActionLabel( 
            new ActionLabel.ShowChartAction( Keys.NATIONAL_DEBT_TO_GDP, res.getPercentFormat() ),
            ActionLabel.ICON_CHARTS
        );
        budgetDeficitLabel = new JLabel();
        budgetDeficitLabel.setHorizontalAlignment( JLabel.TRAILING );
        nationalDebtLabel = new JLabel();
        nationalDebtLabel.setHorizontalAlignment( JLabel.TRAILING );
        nationalDebtToGdpLabel= new JLabel();
        nationalDebtToGdpLabel.setHorizontalAlignment( JLabel.TRAILING );
        
        budgetDeficitCaptionLabel.setFont( TablePanel.FONT );
        nationalDebtCaptionLabel.setFont( TablePanel.FONT );
        nationalDebtToGdpCaptionLabel.setFont( TablePanel.FONT );
        budgetDeficitLabel.setFont( TablePanel.FONT );
        nationalDebtLabel.setFont( TablePanel.FONT );
        nationalDebtToGdpLabel.setFont( TablePanel.FONT );        
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setAlignmentX( 0.5f );
        p0.setLayout( new GridBagLayout() );
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;        
        c.weightx = 1.0;
        
        c.gridx = 0;
        p0.add( budgetDeficitCaptionLabel, c );
        
        c.gridx = 1;
        p0.add( budgetDeficitLabel, c );
        
        c.gridy = 1;
        c.gridx = 0;
        p0.add( nationalDebtCaptionLabel, c );
        
        c.gridx = 1;
        p0.add( nationalDebtLabel, c );
        
        c.gridy = 2;
        c.gridx = 0;
        p0.add( nationalDebtToGdpCaptionLabel, c );
        
        c.gridx = 1;
        p0.add( nationalDebtToGdpLabel, c );
        
        p0.setBorder( new EmptyBorder( 0, 100, 0, 100 ) );
        
        return p0;
    }
    
    private TablePanel createTablePanel() {
        Resources res = Resources.getInstance();
        
        NumberFormat billionFormat = res.getBillionFormat();

        String[] headerKeys = { 
            "game.scene.budget.policy", 
            "game.scene.budget.current", 
            "game.scene.budget.planned" 
        };
        
        policyKeys = new PolicyKey[] {
            Keys.INCOME_TAX, 
            Keys.VAT, 
            Keys.UNEMPLOYMENT_BENEFITS, 
            Keys.RETIREMENT_BENEFITS, 
            Keys.PUBLIC_SECTOR_FUNDING
        };
        
        Action[] actions = new Action[policyKeys.length];
        for ( int i=0; i<policyKeys.length; i++ ) {
            if ( i < 2 ) {
                actions[i] = new ActionLabel.ShowChartAction( policyKeys[i].REVENUES, res.getBillionFormat() );
            } else {
                actions[i] = new ActionLabel.ShowChartAction( policyKeys[i].COSTS, res.getBillionFormat() );
            }
        }
        
        table = new TablePanel( headerKeys, actions, ActionLabel.ICON_CHARTS );
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = policyKeys.length+1;
        c.weightx = 1.0;
        
        JLabel captionLabel = createTotalCaptionLabel();
        captionLabel.setBorder( new CompoundBorder(
            new MatteBorder( 1, 0, 0, 1, Color.GRAY ), new EmptyBorder(15, 5, 15, 5)
        ) );
        c.gridx = 0;
        table.add( captionLabel, c );
        
        currentTotalLabel = createTotalValueLabel();
        currentTotalLabel.setBorder( new CompoundBorder(
            new MatteBorder( 1, 0, 0, 1, Color.GRAY ), new EmptyBorder(15, 5, 15, 5)
        ) );
        c.gridx = 1;
        table.add( currentTotalLabel, c );
        
        plannedTotalLabel = createTotalValueLabel();
        plannedTotalLabel.setBorder( new CompoundBorder(
            new MatteBorder( 1, 0, 0, 0, Color.GRAY ), new EmptyBorder(15, 5, 15, 5)
        ) );
        c.gridx = 2;
        table.add( plannedTotalLabel, c );
        
        table.setAlignmentX( 0.5f );
        
        return table;
    }
    
    private JLabel createTotalCaptionLabel() {
        JLabel label = new JLabel( Resources.getInstance().getText("gui", "game.scene.budget.total") );
        label.setFont( new Font( Font.SERIF, Font.BOLD, 18 ) );
        label.setHorizontalAlignment( JLabel.TRAILING );
        return label;
    }
    
    private JLabel createTotalValueLabel() {
        JLabel label = new JLabel();
        label.setFont( new Font( Font.SERIF, Font.BOLD, 18 ) );
        label.setHorizontalAlignment( JLabel.TRAILING );
        return label;
    }
    
    private String format( Object value ) {
        NumberFormat nf = Resources.getInstance().getBillionFormat();
        if ( ((Number) value).doubleValue() > 0.0 ) {
            return "+" + nf.format(value);
        } else {
            return nf.format(value);
        }
    }
    
    
    // ******************************************************
    // ************ NOTIFIABLE CARD IMPLEMENTATION **********
    // ******************************************************
    public void aboutToBeShown() {
        GameEngine engine = GameEngine.getInstance();
        double value0, value1;
        NumberFormat bf = Resources.getInstance().getBillionFormat();
        NumberFormat pf = Resources.getInstance().getPercentFormat();
        
        budgetDeficitLabel.setText( bf.format( engine.getVariableValue( Keys.BUDGET_DEFICIT ) ) );
        nationalDebtLabel.setText( bf.format( engine.getVariableValue( Keys.NATIONAL_DEBT ) ) );
        nationalDebtToGdpLabel.setText( pf.format( engine.getVariableValue( Keys.NATIONAL_DEBT_TO_GDP ) ) );
        
        // Policies' banace:
        for ( int i=0; i<policyKeys.length; i++ ) {
            value0 = engine.getCurrentPolicyBalance( policyKeys[i] );
            value1 = engine.getPlannedPolicyBalance( policyKeys[i] );
            table.valueLabels[i][0].setText( format(value0) );
            table.valueLabels[i][1].setText( format(value1) );
        }
        
        // Total balance:
        value0 = -(Double) engine.getCurrentDeficit();
        value1 = -(Double) engine.getPlannedDeficit();
        currentTotalLabel.setText( format(value0) );
        plannedTotalLabel.setText( format(value1) );
    }
    
}