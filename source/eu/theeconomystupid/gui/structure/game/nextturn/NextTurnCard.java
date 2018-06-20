package eu.theeconomystupid.gui.structure.game.nextturn;


import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.components.AnimatedCardPanel;
import eu.theeconomystupid.gui.components.CardDisplay;
import eu.theeconomystupid.gui.components.NotifiableCard;
import eu.theeconomystupid.gui.effects.AlphaFadeEffect;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;


public final class NextTurnCard extends JLayeredPane implements CardDisplay, NotifiableCard {
    
    
    private AnimatedCardPanel cardPanel;
    

    public NextTurnCard() {
        setName("nextturn");
        setOpaque( true );
        setBackground( Color.BLACK );
        addComponentListener( new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                showPleaseWaitCard();
            }
        });
        
        Dimension d = GameGUI.getInstance().getDisplaySize();
        setPreferredSize(d);
        
        cardPanel = new AnimatedCardPanel( new AlphaFadeEffect(false), 500 );
        cardPanel.setAlignmentX( 0.5f );
        cardPanel.add( createEmptyCard() );
        cardPanel.add( new PleaseWaitCard() );        
        cardPanel.add( new DateCard() );
        cardPanel.add( new ElectionsCard() );
        cardPanel.add( new GameOverCard() );
        cardPanel.add( new NarrationCard() );
        cardPanel.setBounds( (d.width-500)/2, 0, 500, d.height );
        
        JPanel overlay = new JPanel();
        overlay.setOpaque( false );
        overlay.setFocusable( true );
        overlay.setSize(d);
        overlay.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked();
            }
        });
        
        add( cardPanel, new Integer(0) );
        add( overlay, new Integer(1) );
    }
    
    protected void paintComponent( Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent(g);
    }

    private JComponent createEmptyCard() {
        JPanel emptyCard = new JPanel();
        emptyCard.setName("empty");
        emptyCard.setBackground( Color.BLACK );
        return emptyCard;
    }
    
    private void showPleaseWaitCard() {
        GameGUI.getInstance().showCard( "game.nextturn.pleaseWait" );
    }
    
    private void handleMouseClicked() {
        ActionEvent evt = new ActionEvent( this, 0, "continue" );
        for ( Component component : cardPanel.getComponents() ) {            
            if ( component.isVisible() && component instanceof ActionListener ) {
                ((ActionListener) component).actionPerformed( evt );
            }
        }
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

    public void showCardImmediately( String name ) {
        cardPanel.showCardImmediately( name );
    }
    
    public Component getCard( String name ) {
        return cardPanel.getCard( name );
    }

    public boolean containsCard( String name ) {
        return cardPanel.containsCard( name );
    }
    
    
    // ***************************************************
    // ***** NOTIFIABLE CARD INTERFACE IMPLEMENTATION ****
    // ***************************************************    
    public void aboutToBeShown() {
        cardPanel.showCardImmediately( "empty" );
    }
    
}