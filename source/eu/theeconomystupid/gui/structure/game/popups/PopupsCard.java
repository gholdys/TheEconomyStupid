package eu.theeconomystupid.gui.structure.game.popups;


import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.engine.PolicyKey;
import eu.theeconomystupid.engine.SocialGroupKey;
import eu.theeconomystupid.gui.effects.PixelizingFadeEffect;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Utilities;
import eu.theeconomystupid.gui.components.AnimatedCardPanel;
import eu.theeconomystupid.gui.components.CardDisplay;
import eu.theeconomystupid.gui.structure.game.scene.SceneCard;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public final class PopupsCard extends JPanel implements CardDisplay {
    
    
    private Object[] chartsConfig;
    private SceneCard sceneCard;
    private BufferedImage backgroundImage;
    private AnimatedCardPanel cardPanel;

    
    public PopupsCard( SceneCard sceneCard ) {
        this.sceneCard = sceneCard;
        setName( "popups" );
        setLayout( null );
        setOpaque( true );
        setSize( GameGUI.getInstance().getDisplaySize() );
        setVisible( false );
        
        addComponentListener( new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
            }
        });
        
        getInputMap( WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "close" );
        getActionMap().put( "close", new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                GameGUI.getInstance().showCard( "game.scene" );
            }
        });
        
        createCardPanel();
    }
    
    protected void paintComponent(Graphics g) {
        if ( !isShowing() ) refreshBackground();
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
        cardPanel.showCard( name );
    }

    public void showCard( String name, ActionListener actionLister ) {
        cardPanel.showCard( name, actionLister );
    }    

    public void showCardImmediately(String name) {
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
    private void createCardPanel() {
        cardPanel = new AnimatedCardPanel( new PixelizingFadeEffect( 4, true ), 500 );
        
        // Charts:
        cardPanel.add( new ChartPopup() );
        
        // Social group details
        SocialGroupKey[] socialGroups = Keys.getSocialGroupKeys();
        for ( int i=0; i<socialGroups.length; i++ ) {
            cardPanel.add( new SocialGroupDetailsPopup( socialGroups[i] ) );
        }
        
        // Policy editors:
        PolicyKey[] policies = Keys.getPolicyKeys();
        for ( int i=0; i<policies.length; i++ ) {
            cardPanel.add( new PolicyEditorPopup( policies[i] ) );
        }
        
        // "Reduce deficit" popup:
        cardPanel.add( new ReduceDeficitPopup() );
        
        // Confirm quit popup:
        cardPanel.add( new ConfirmQuitPopup() );
        
        cardPanel.setSize( cardPanel.getPreferredSize() );
        cardPanel.setBounds(
            (int) Math.round( 0.5*(getWidth()-cardPanel.getWidth()) ),
            (int) Math.round( 0.5*(getHeight()-cardPanel.getHeight()) ),
            cardPanel.getWidth(), 
            cardPanel.getHeight()
        );
        add( cardPanel );
        
    }

    private void refreshBackground() {
        backgroundImage = Utilities.renderContainerToImage( sceneCard, backgroundImage );
        Graphics g = backgroundImage.getGraphics();
        g.setColor( new Color( 0f, 0f, 0f, 0.75f ) );
        g.fillRect( 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight() );
    }
    
}