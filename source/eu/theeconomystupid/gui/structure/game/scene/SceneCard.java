package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.CardDisplay;
import eu.theeconomystupid.gui.effects.HorizontalRotationEffect;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.components.AnimatedCardPanel;
import java.awt.event.ComponentAdapter;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;


public final class SceneCard extends JPanel implements CardDisplay {
    
    
    private static final String[] CENTER_CARD_NAMES = { "report", "economy", "society", "policies", "budget" };
    private static final Font COPYRIGHT_FONT = new Font( Font.SANS_SERIF, Font.BOLD, 10 );
    
    
    private BufferedImage backgroundImage;
    private AnimatedCardPanel cardPanel;
    private CalendarDisplay calendarDisplay;
    private VariableDisplay daysToElectionsDisplay;
    private Map <String,JToggleButton> cardButtonLookup;
    
    
    public SceneCard() {
        setName("scene");
        setOpaque( true );
        setSize( GameGUI.getInstance().getDisplaySize() );
        setLayout( null );
        setFocusable( true );
        setFocusCycleRoot( true );
        loadBackgroundImage();
        createLeftPanel();
        createRightPanel();
        createCardPanel();
        createCopyrightLabel();
        
        addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        
        addComponentListener( new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
            }
        });
        
        getInputMap( WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "exit" );
        getActionMap().put( "exit", new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                GameGUI.getInstance().showCard( "game.popups.confirmQuit" );
            }
        });

    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage( backgroundImage, 0, 0, null );
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }

    
    // ***************************************************
    // ****** CARD DISPLAY INTERFACE IMPLEMENTATION ******
    // ***************************************************    
    public String getCurrentCardName() {
        return cardPanel.getCurrentCardName();
    }

    public Component getCurrentCard() {
        return cardPanel.getCurrentCard();
    }

    public void showCard( String name ) {
        JToggleButton tb = cardButtonLookup.get( name );
        if ( !tb.isSelected() ) tb.setSelected( true );
        cardPanel.showCard( name );
    }

    public void showCard( String name, ActionListener actionLister ) {
        JToggleButton tb = cardButtonLookup.get( name );
        if ( !tb.isSelected() ) tb.setSelected( true );
        cardPanel.showCard( name, actionLister );
    }    

    public void showCardImmediately( String name ) {
        JToggleButton tb = cardButtonLookup.get( name );
        if ( !tb.isSelected() ) tb.setSelected( true );
        cardPanel.showCardImmediately( name );
    }
    
    public Component getCard( String name ) {
        return cardPanel.getCard( name );
    }

    public boolean containsCard( String name ) {
        return cardPanel.containsCard( name );
    }
    
    
    // ***************************************************
    // ***************** PRIVATE METHODS  ****************
    // ***************************************************
    private void runNextTurn() {
        if ( GameEngine.getInstance().isNextTurnLegal() ) {
            GameGUI.getInstance().showCard( "game.nextturn" );
        } else {
            GameGUI.getInstance().showCard( "game.popups.reduceDeficit" );
        }
    }
    
    private void loadBackgroundImage() {
        backgroundImage = Resources.getInstance().getImage( "background.jpg" );
    }
    
    private void createLeftPanel() {
        ButtonGroup bg = new ButtonGroup();
        cardButtonLookup = new HashMap <String,JToggleButton> ();
        
        JPanel p0 = new JPanel();
        p0.setBorder( new EmptyBorder( 10, 20, 10, 50 ) );
        p0.setLayout( new BoxLayout( p0, BoxLayout.PAGE_AXIS ) );
        p0.setOpaque( false );
        JToggleButton tb;
        for ( int i=0; i<CENTER_CARD_NAMES.length; i++ ) {
            tb = createToggleButton( CENTER_CARD_NAMES[i], "game.scene."+CENTER_CARD_NAMES[i], bg );
            p0.add( tb );
            p0.add( Box.createRigidArea( new Dimension(0, 10) ) );
            if ( i == 0 ) tb.setSelected( true );
        }
        p0.add( Box.createGlue() );
        p0.setSize( p0.getPreferredSize() );
        p0.setLocation( 0, 0 );
        
        add( p0 );
    }
    
    private void createRightPanel() {
        Resources res = Resources.getInstance();
        
        JPanel p0 = new JPanel();
        p0.setLayout( new BoxLayout( p0, BoxLayout.PAGE_AXIS ) );
        p0.setOpaque( false );
        p0.setBorder( new EmptyBorder( 10, 0, 20, 20 ) );
        
        calendarDisplay = new CalendarDisplay();
        daysToElectionsDisplay = new VariableDisplay( 
            res.getText( "gui", "game.scene.turnsToElections" ), 
            Keys.NUM_TURNS_TO_ELECTIONS, NumberFormat.getIntegerInstance( res.getCurrentLocale() )
        );
        
        p0.add( calendarDisplay );
        p0.add( Box.createRigidArea( new Dimension(0, 20) ) );
        p0.add( daysToElectionsDisplay );
        p0.add( Box.createGlue() );
        p0.add( createNextTurnButton() );
        
        p0.setSize( p0.getPreferredSize().width, GameGUI.getInstance().getDisplaySize().height );
        p0.setLocation( GameGUI.getInstance().getDisplaySize().width - p0.getWidth(), 0 );
        
        add( p0 );
    }
    
    private void createCardPanel() {
        cardPanel = new AnimatedCardPanel( new HorizontalRotationEffect(), 600 );
        cardPanel.setAlignmentX( 0.5f );
        cardPanel.setPreferredSize( new Dimension(620, 560) );
        cardPanel.setMaximumSize( cardPanel.getPreferredSize() );
        cardPanel.setMinimumSize( cardPanel.getPreferredSize() );
        cardPanel.add( new ReportCard() );
        cardPanel.add( new EconomyCard() );
        cardPanel.add( new SocietyCard() );
        cardPanel.add( new PoliciesCard() );
        cardPanel.add( new BudgetCard() );
        
        cardPanel.setSize( cardPanel.getPreferredSize() );
        cardPanel.setLocation( 
            (GameGUI.getInstance().getDisplaySize().width - cardPanel.getWidth())/2,  
            (GameGUI.getInstance().getDisplaySize().height - cardPanel.getHeight())/2 + 10
        );
        
        add( cardPanel );
    }
    
    private void createCopyrightLabel() {
        JLabel label = new JLabel( "\u00a92011 Insight Machines" );
        label.setFont( COPYRIGHT_FONT );
        label.setForeground( Color.WHITE );
        
        label.setSize( label.getPreferredSize() );
        label.setLocation( 
            (GameGUI.getInstance().getDisplaySize().width - label.getWidth())/2,  
            (GameGUI.getInstance().getDisplaySize().height - label.getHeight())-15
        );
        
        add( label );
    }
    
    private JButton createNextTurnButton() {
        Resources res = Resources.getInstance();
        
        String iconKey = "nextturn";
        
        JButton b = new JButton();
        b.setIcon( res.getImageIcon( iconKey+"_0.png" ) );
        b.setRolloverIcon( res.getImageIcon( iconKey+"_1.png" ) );
        b.setPressedIcon( res.getImageIcon( iconKey+"_2.png" ) );        
        b.setToolTipText( res.getText("gui", "game.scene.nextTurnTooltip") );
        b.setMargin( new Insets(0,0,0,0) );
        b.setBorderPainted( false );
        b.setContentAreaFilled( false );
        b.setFocusable( false );
        b.setFocusPainted( false );
        b.setAlignmentX(1f);
        b.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runNextTurn();
            }
        });
        
        return b;
    }
    
    private JToggleButton createToggleButton( String iconKey, final String cardKey, ButtonGroup bg ) {
        Resources res = Resources.getInstance();
        
        JToggleButton tb = new JToggleButton();
        tb.setIcon( res.getImageIcon( iconKey+"_0.png" ) );
        tb.setRolloverIcon( res.getImageIcon( iconKey+"_1.png" ) );
        tb.setPressedIcon( res.getImageIcon( iconKey+"_2.png" ) );
        tb.setSelectedIcon( res.getImageIcon( iconKey+"_2.png" ) );
        tb.setToolTipText( res.getText( "gui", cardKey + ".caption" ) );
        tb.setMargin( new Insets(0,0,0,0) );
        tb.setBorderPainted( false );
        tb.setContentAreaFilled( false );
        tb.setFocusable( false );
        tb.setFocusPainted( false );        
        tb.setAlignmentX(0.5f);
        tb.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameGUI.getInstance().showCard( cardKey );
            }
        });
        
        bg.add( tb );
        
        String shortKey = cardKey.substring( cardKey.lastIndexOf('.')+1 );
        cardButtonLookup.put( shortKey, tb );
        
        return tb;
    }    
    
}