package eu.theeconomystupid.gui.structure.game.popups;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameEngineListener;
import eu.theeconomystupid.engine.Key;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.engine.SocialGroupKey;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.ActionLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;


final class SocialGroupDetailsPopup extends AbstractPopupCard implements GameEngineListener {
    
    
    public static final int MIN_INTERNAL_WIDTH = 500;
    public static final Font BIG_FONT = new Font( Font.SERIF, Font.BOLD, 18 );
    public static final Font NORMAL_FONT = new Font( Font.SERIF, Font.BOLD, 14 );    
    
    private SocialGroupKey groupKey;
    private String shortGroupKey;
    private JLabel numPeopleLabel;
    private JLabel popPartLabel;
    private JLabel incomeLabel;
    private JLabel approvalRatingLabel;
    private JPanel appreciationPanel;
    private JPanel criticsPanel;
    
    
    SocialGroupDetailsPopup( SocialGroupKey groupKey ) {
        Resources res = Resources.getInstance();
        
        this.groupKey = groupKey;
        
        String s = groupKey.toString();
        shortGroupKey = s.substring( s.indexOf('.')+1 );
        setName( "details_" + shortGroupKey );
        setCaption( res.getText( "variables", groupKey.toString() ) );
        setCloseCardName( "game.scene" );
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setLayout( new GridLayout(1,2) );
        p0.add( createAppreciationPanel() );
        p0.add( createCriticsPanel() );
        
        JPanel p1 = new JPanel();
        p1.setOpaque( false );
        p1.setBorder( new EmptyBorder( 20, 50, 30, 50 ) );
        p1.setLayout( new BoxLayout( p1, BoxLayout.PAGE_AXIS ) );
        p1.add( Box.createRigidArea( new Dimension(MIN_INTERNAL_WIDTH, 30) ) );
        p1.add( createVariableValuesPanel() );
        p1.add( Box.createRigidArea( new Dimension(0, 30) ) );
        p1.add( p0 );
        
        setContents( p1 );
        
        GameEngine.getInstance().addGameEngineListener( this );
    }
    
    private void refreshData() {
        GameEngine engine = GameEngine.getInstance();
        Resources res = Resources.getInstance();
        
        double n = (Double) engine.getVariableValue( groupKey.NUM_PEOPLE );
        double i = (Double) engine.getVariableValue( groupKey.AVERAGE_GROSS_INCOME );
        double p = (Double) engine.getVariableValue( groupKey.POPULATION_PART );
        double a = (Double) engine.getVariableValue( groupKey.APPROVAL_RATING );
        
        numPeopleLabel.setText( res.getIntegerFormat().format(n) );
        popPartLabel.setText( res.getPercentFormat().format(p) );
        incomeLabel.setText( res.getCurrencyFormat().format(i) );
        approvalRatingLabel.setText( res.getPercentFormat().format(a) );
        
        String[] moods = (String[]) engine.getVariableValue( Keys.SOCIETY_MOODS );
        refreshAppreciationPanel( moods );
        refreshCriticPanel( moods );
        
    }
    
    private void refreshAppreciationPanel( String[] moods ) {
        List <String> appreciationReasons = new ArrayList <String> ();
        for ( String mood : moods ) {
            if ( mood.startsWith( shortGroupKey+"." ) || mood.startsWith( "all." ) ) {
                if ( mood.contains( ".happy." ) ) {
                    appreciationReasons.add( mood.substring( mood.lastIndexOf('.')+1 ) );
                }
            }
        }
        
        Resources res = Resources.getInstance();
        JLabel label;
        appreciationPanel.removeAll();
        if ( appreciationReasons.size() > 0 ) {
            for ( String reason : appreciationReasons ) {
                label = new JLabel( res.getText( "moods", reason ) );
                label.setFont( NORMAL_FONT );
                label.setHorizontalAlignment( JLabel.CENTER );
                label.setAlignmentX( 0.5f );
                label.setBorder( new EmptyBorder( 10, 0, 10, 0 ) );
                appreciationPanel.add( label );
            }
            appreciationPanel.add( Box.createGlue() );
        } else {
            label = new JLabel( res.getText( "gui", "game.popups.details.nothing" ) );
            label.setFont( NORMAL_FONT );
            label.setHorizontalAlignment( JLabel.CENTER );
            label.setAlignmentX( 0.5f );
            label.setBorder( new EmptyBorder( 10, 0, 10, 0 ) );
            appreciationPanel.add( label );
            appreciationPanel.add( Box.createGlue() );
        }
    }
    
    private void refreshCriticPanel( String[] moods ) {
        List <String> criticReasons = new ArrayList <String> ();
        for ( String mood : moods ) {
             if ( mood.startsWith( shortGroupKey+"." ) || mood.startsWith( "all." ) ) {
                if ( mood.contains( ".unhappy." ) || mood.contains( ".angry." ) ) {
                    criticReasons.add( mood.substring( mood.lastIndexOf('.')+1 ) );
                }
            }           
        }
        
        Resources res = Resources.getInstance();
        JLabel label;
        criticsPanel.removeAll();
        if ( criticReasons.size() > 0 ) {
            for ( String reason : criticReasons ) {
                label = new JLabel( res.getText( "moods", reason ) );
                label.setFont( NORMAL_FONT );
                label.setHorizontalAlignment( JLabel.CENTER );
                label.setAlignmentX( 0.5f );
                label.setBorder( new EmptyBorder( 10, 0, 10, 0 ) );
                criticsPanel.add( label );
            }
            criticsPanel.add( Box.createGlue() );
        } else {
            label = new JLabel( res.getText( "gui", "game.popups.details.nothing" ) );
            label.setFont( NORMAL_FONT );
            label.setHorizontalAlignment( JLabel.CENTER );
            label.setAlignmentX( 0.5f );
            label.setBorder( new EmptyBorder( 10, 0, 10, 0 ) );
            criticsPanel.add( label );
            criticsPanel.add( Box.createGlue() );
        }
    }
    
    private JPanel createVariableValuesPanel() {
        Resources res = Resources.getInstance();
        ActionLabel label;
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setLayout( new GridBagLayout() );
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets( 5, 5, 5, 5 );
        
        c.gridy = 0;
        c.gridx = 0;
        label = new ActionLabel( 
            new ShowChartAction( 
                "game.popups.details.numPeople",
                groupKey.NUM_PEOPLE, 
                res.getIntegerFormat(), 
                "game.popups."+getName() 
            ),
            ActionLabel.ICON_CHARTS
        );
        
        label.setFont( NORMAL_FONT );
        p0.add( label, c );
        
        c.gridx = 1;
        numPeopleLabel = new JLabel();
        numPeopleLabel.setHorizontalAlignment( JLabel.TRAILING );
        numPeopleLabel.setFont( NORMAL_FONT );
        p0.add( numPeopleLabel, c );
        
        c.gridy = 1;
        c.gridx = 0;
        label = new ActionLabel( 
            new ShowChartAction( 
                "game.popups.details.populationPart",
                groupKey.POPULATION_PART, 
                res.getPercentFormat(), 
                "game.popups."+getName() 
            ),
            ActionLabel.ICON_CHARTS
        );
        label.setFont( NORMAL_FONT );
        p0.add( label, c );
        
        c.gridx = 1;
        popPartLabel = new JLabel();
        popPartLabel.setHorizontalAlignment( JLabel.TRAILING );
        popPartLabel.setFont( NORMAL_FONT );
        p0.add( popPartLabel, c );
        
        c.gridy = 2;
        c.gridx = 0;
        label = new ActionLabel( 
            new ShowChartAction( 
                "game.popups.details.averageGrossIncome",
                groupKey.AVERAGE_GROSS_INCOME, 
                groupKey.REAL_AVERAGE_GROSS_INCOME, 
                res.getCurrencyFormat(),
                "game.popups."+getName() 
            ), 
            ActionLabel.ICON_CHARTS
        );
        label.setFont( NORMAL_FONT );
        p0.add( label, c );
        
        c.gridx = 1;
        incomeLabel = new JLabel();
        incomeLabel.setHorizontalAlignment( JLabel.TRAILING );
        incomeLabel.setFont( NORMAL_FONT );
        p0.add( incomeLabel, c );
        
        c.gridy = 3;
        c.gridx = 0;
        label = new ActionLabel( 
            new ShowChartAction( 
                "game.popups.details.approvalRating",
                groupKey.APPROVAL_RATING, 
                res.getPercentFormat(),
                "game.popups."+getName() 
                
            ), 
            ActionLabel.ICON_CHARTS
        );
        label.setFont( NORMAL_FONT );
        p0.add( label, c );
        
        c.gridx = 1;
        approvalRatingLabel = new JLabel();
        approvalRatingLabel.setHorizontalAlignment( JLabel.TRAILING );
        approvalRatingLabel.setFont( NORMAL_FONT );
        p0.add( approvalRatingLabel, c );
        
        return p0;
    }
    
    private JPanel createAppreciationPanel() {

        JLabel label = new JLabel( Resources.getInstance().getText( "gui", "game.popups.details.appreciate" ) );
        label.setFont( BIG_FONT );
        label.setHorizontalAlignment( JLabel.CENTER );
        label.setAlignmentX( 0.5f );
        label.setBorder( new CompoundBorder( 
            new MatteBorder( 0, 0, 3, 0, Color.BLACK ),
            new EmptyBorder( 0, 0, 5, 0 )
        ) );
        
        appreciationPanel = new JPanel();
        appreciationPanel.setOpaque( false );
        appreciationPanel.setLayout( new BoxLayout( appreciationPanel, BoxLayout.PAGE_AXIS ) );
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setLayout( new BoxLayout( p0, BoxLayout.PAGE_AXIS ) );
        p0.add( label );
        p0.add(  Box.createRigidArea( new Dimension(0, 10) ) );
        p0.add( appreciationPanel );
        
        return p0;
    }
    
    private JPanel createCriticsPanel() {
        
        JLabel label = new JLabel( Resources.getInstance().getText( "gui", "game.popups.details.criticize" ) );
        label.setFont( BIG_FONT );
        label.setHorizontalAlignment( JLabel.CENTER );
        label.setAlignmentX( 0.5f );
        label.setBorder( new CompoundBorder( 
            new MatteBorder( 0, 0, 3, 0, Color.BLACK ),
            new EmptyBorder( 0, 0, 5, 0 )
        ) );
        
        criticsPanel = new JPanel();
        criticsPanel.setOpaque( false );
        criticsPanel.setLayout( new BoxLayout( criticsPanel, BoxLayout.PAGE_AXIS ) );
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setLayout( new BoxLayout( p0, BoxLayout.PAGE_AXIS ) );
        p0.add( label );
        p0.add(  Box.createRigidArea( new Dimension(0, 10) ) );
        p0.add( criticsPanel );
        
        return p0;
    }

    
    // ******************************************************
    // ********** GAME EVENT LISTENER IMPLEMENTATION ********
    // ******************************************************
    public void turnCompleted() {
        refreshData();
    }

    public void gameCreated() {
        refreshData();
    }

    public void gameAboutToBeCreated() {
        // do nothing
    }

    
    private static final class ShowChartAction extends AbstractAction {
        
        private Key nominalVariableKey;
        private Key realVariableKey;
        private NumberFormat yAxisFormat;
        private String closeCardName;
        
        ShowChartAction( String variableNameKey, Key nominalVariableKey, NumberFormat yAxisFormat, String closeCardName ) {
            this( variableNameKey, nominalVariableKey, null, yAxisFormat, closeCardName );
        }
        
        ShowChartAction( String variableNameKey, Key nominalVariableKey, Key realVariableKey, NumberFormat yAxisFormat, String closeCardName ) {
            this.nominalVariableKey = nominalVariableKey;
            this.realVariableKey = realVariableKey;
            this.yAxisFormat = yAxisFormat;
            this.closeCardName = closeCardName;
            putValue( NAME, Resources.getInstance().getText( "gui", variableNameKey ) );
        }
        
        public void actionPerformed(ActionEvent e) {
            ChartPopup cp = (ChartPopup) GameGUI.getInstance().getCard( "game.popups.chart" );
            cp.configureChart( nominalVariableKey, realVariableKey, yAxisFormat, closeCardName );
            GameGUI.getInstance().showCard( "game.popups.chart" );
        }
        
    }
    
}