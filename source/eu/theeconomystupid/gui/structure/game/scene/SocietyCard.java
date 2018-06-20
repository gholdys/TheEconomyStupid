package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.engine.SocialGroupKey;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.ActionLabel;
import eu.theeconomystupid.gui.components.TablePanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


final class SocietyCard extends AbstractCenterCard {
    

    private SocialGroupKey[] groupKeys;
    private JLabel approvalRatingLabel;
    private JLabel populationLabel;
    private JLabel workforceLabel;
    private TablePanel table;
    
    
    SocietyCard() {
        super( "society" );
        
        JPanel p0 = createTopPanel();
        table = createTablePanel();
        
        JPanel p1 = new JPanel();
        p1.setOpaque( false );
        p1.setLayout( new BoxLayout( p1, BoxLayout.PAGE_AXIS ) );
        p1.add( Box.createRigidArea( new Dimension(0, 50) ) );
        p1.add( p0 );
        p1.add( table );
        p1.add( Box.createRigidArea( new Dimension(0, 50) ) );
        
        setContents( p1 );
        
    }
    
    protected void refreshData() {
        GameEngine engine = GameEngine.getInstance();
        NumberFormat integerFormat = Resources.getInstance().getIntegerFormat();
        NumberFormat percentFormat = Resources.getInstance().getPercentFormat();
        SocialGroupKey k;
        for ( int i=0; i<table.valueLabels.length; i++ ) {
            k = groupKeys[i];
            table.valueLabels[i][0].setText( integerFormat.format( engine.getVariableValue( k.NUM_PEOPLE ) ) );
            table.valueLabels[i][1].setText( percentFormat.format( engine.getVariableValue( k.APPROVAL_RATING ) ) );
        }
        populationLabel.setText( integerFormat.format( engine.getVariableValue( Keys.TOTAL_POPULATION ) ) );
        approvalRatingLabel.setText( percentFormat.format( engine.getVariableValue( Keys.APPROVAL_RATING ) ) );
        workforceLabel.setText( integerFormat.format( engine.getVariableValue( Keys.WORKFORCE ) ) );
    }
    
    private JPanel createTopPanel() {
        Resources res = Resources.getInstance();
        
        ActionLabel approvalRatingCaptionLabel = new ActionLabel( 
            new ActionLabel.ShowChartAction( Keys.APPROVAL_RATING, res.getPercentFormat() ), 
            ActionLabel.ICON_CHARTS
        );
        ActionLabel populationCaptionLabel = new ActionLabel( 
            new ActionLabel.ShowChartAction( Keys.TOTAL_POPULATION, res.getIntegerFormat() ), 
            ActionLabel.ICON_CHARTS
        );
        ActionLabel workforceCaptionLabel = new ActionLabel( 
            new ActionLabel.ShowChartAction( Keys.WORKFORCE, res.getIntegerFormat() ), 
            ActionLabel.ICON_CHARTS
        );
        
        approvalRatingLabel = new JLabel();
        approvalRatingLabel.setHorizontalAlignment( JLabel.TRAILING );
        populationLabel = new JLabel();
        populationLabel.setHorizontalAlignment( JLabel.TRAILING );
        workforceLabel = new JLabel();
        workforceLabel.setHorizontalAlignment( JLabel.TRAILING );
        
        approvalRatingCaptionLabel.setFont( TablePanel.FONT );
        populationCaptionLabel.setFont( TablePanel.FONT );
        workforceCaptionLabel.setFont( TablePanel.FONT );
        approvalRatingLabel.setFont( TablePanel.FONT );
        populationLabel.setFont( TablePanel.FONT );
        workforceLabel.setFont( TablePanel.FONT );
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setAlignmentX( 0.5f );
        p0.setLayout( new GridBagLayout() );
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;        
        c.weightx = 1.0;
        
        c.gridx = 0;
        p0.add( approvalRatingCaptionLabel, c );
        
        c.gridx = 1;
        p0.add( approvalRatingLabel, c );
        
        c.gridy = 1;
        c.gridx = 0;
        p0.add( populationCaptionLabel, c );
        
        c.gridx = 1;
        p0.add( populationLabel, c );
        
        c.gridy = 2;
        c.gridx = 0;
        p0.add( workforceCaptionLabel, c );
        
        c.gridx = 1;
        p0.add( workforceLabel, c );
        
        p0.setBorder( new EmptyBorder( 0, 100, 0, 100 ) );
        
        return p0;
    }
    
    private TablePanel createTablePanel() {
        Resources res = Resources.getInstance();

        String[] headerKeys = { 
            "game.scene.society.socialGroup", 
            "game.scene.society.population", 
            "game.scene.society.approvalRating" 
        };
        
        groupKeys = new SocialGroupKey[] {
            Keys.PRIVATE_SECTOR_EMPLOYEES,
            Keys.PUBLIC_SECTOR_EMPLOYEES,
            Keys.RETIRED,
            Keys.UNEMPLOYED
        };
        
        String[] targetCards = {
            "game.popups.details_privateSectorEmployees",
            "game.popups.details_publicSectorEmployees",
            "game.popups.details_retired",
            "game.popups.details_unemployed",
        };
        
        String[] groupNames = new String[groupKeys.length];
        for ( int i=0; i<groupKeys.length; i++ ) {
            groupNames[i] = res.getText( "variables", groupKeys[i].toString() );
        }
        
        table = new TablePanel( headerKeys, groupNames, targetCards, ActionLabel.ICON_DETAILS );
        table.setAlignmentX( 0.5f );
        
        return table;
    }
    
    private JLabel createTotalCaptionLabel() {
        JLabel label = new JLabel( Resources.getInstance().getText("gui", "game.scene.budget.total") );
        label.setFont( new Font( Font.SERIF, Font.BOLD, 22 ) );
        label.setHorizontalAlignment( JLabel.TRAILING );
        return label;
    }
    
    private JLabel createTotalValueLabel() {
        JLabel label = new JLabel();
        label.setFont( new Font( Font.SERIF, Font.BOLD, 22 ) );
        label.setHorizontalAlignment( JLabel.TRAILING );
        return label;
    }
    
}
