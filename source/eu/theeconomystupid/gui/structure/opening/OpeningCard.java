package eu.theeconomystupid.gui.structure.opening;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.AnimatedCardPanel;
import eu.theeconomystupid.gui.components.CardDisplay;
import eu.theeconomystupid.gui.components.NotifiableCard;
import eu.theeconomystupid.gui.effects.AlphaFadeEffect;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;


public final class OpeningCard extends JLayeredPane implements CardDisplay, NotifiableCard {

    
    private static final Font LARGE_FONT = new Font( Font.SERIF, Font.BOLD, 22 );
    private static final Font FONT = new Font( Font.SERIF, Font.BOLD, 16 );
    private static final Font SMALL_FONT = new Font( Font.SERIF, Font.PLAIN, 12 );
    
    private AnimatedCardPanel cardPanel;
    
    
    public OpeningCard() {
        setName("opening");
        setOpaque( true );
        setBackground( Color.BLACK );
        
        Dimension d = GameGUI.getInstance().getDisplaySize();
        setPreferredSize(d);
        
        cardPanel = new AnimatedCardPanel( new AlphaFadeEffect(false), 1000 );
        cardPanel.setAlignmentX( 0.5f );
        cardPanel.add( createEmptyCard() );
        cardPanel.add( createLanguageSelectionCard() );                
        cardPanel.setBounds( (d.width-500)/2, 0, 500, d.height );
                
        add( cardPanel, new Integer(0) );                        
    }
    
    private void showSceneCard() {
        GameGUI.getInstance().showCard( "game.scene.report" );
    }

    private JComponent createEmptyCard() {
        JPanel emptyCard = new JPanel();
        emptyCard.setName("empty");
        emptyCard.setBackground( Color.BLACK );
        return emptyCard;
    }
    
    private JComponent createLanguageSelectionCard() {
        Resources res = Resources.getInstance();
        
        JLabel label = new JLabel( res.getText("gui", "opening.language.select") );
        label.setFont( LARGE_FONT );
        label.setForeground( Color.WHITE );
        label.setHorizontalAlignment( JLabel.CENTER );
        label.setAlignmentX( 0.5f );        
        
        JPanel p0 = new JPanel();
        p0.setBackground( Color.BLACK );
        p0.setLayout( new FlowLayout() );
        p0.add( createLanguageButton("en", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLanguageSelection( new Locale("en", "GB") );
            }
        }) );
        p0.add( createLanguageButton("es", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLanguageSelection( new Locale("es", "ES") );
            }
        }) );
        
        JPanel languageSelectCard = new JPanel();
        languageSelectCard.setName("languageSelect");
        languageSelectCard.setBackground( Color.BLACK );
        languageSelectCard.setLayout( new BoxLayout(languageSelectCard, BoxLayout.PAGE_AXIS) );
        languageSelectCard.add( Box.createGlue() );
        languageSelectCard.add( label );
        languageSelectCard.add( Box.createRigidArea( new Dimension(0, 10) ) );
        languageSelectCard.add( p0 );
        languageSelectCard.add( Box.createGlue() );
        
        return languageSelectCard;
    }
    
    private void handleLanguageSelection( Locale locale ) {
        Resources.configure( locale );
        cardPanel.add( createStartingGameCard() );
        cardPanel.add( createIntroductionCard() );
        if ( locale.getLanguage().equals("es") ) {
            cardPanel.add( createCreditsCard() );
            showCard("credits");
        } else {
            showCard("startingGame");
        }
    }
    
    private JButton createLanguageButton( String language, ActionListener listener ) {
        Resources res = Resources.getInstance();
        final JButton b = new JButton( res.getImageIcon(language + ".png") );
        b.setContentAreaFilled( false );
        b.setBorderPainted( false );
        b.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBorderPainted( true );
            }
            @Override
            public void mouseExited(MouseEvent e) {
                b.setBorderPainted( false );
            }
        } );        
        b.addActionListener(listener);
        return b;
    }
    
    private JComponent createCreditsCard() {
        JLabel label = new JLabel( "Traducido por Simulación Política" );
        label.setFont( FONT );
        label.setForeground( Color.WHITE );
        label.setHorizontalAlignment( JLabel.CENTER );
        label.setAlignmentX( 0.5f );        
        
        JPanel p0 = new JPanel();
        p0.setName( "credits" );
        p0.setOpaque( true );
        p0.setBackground( Color.BLACK );
        p0.setLayout( new BoxLayout(p0, BoxLayout.PAGE_AXIS) );
        p0.add( Box.createGlue() );
        p0.add( label );
        p0.add( Box.createGlue() );
        p0.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep( 2000 );
                        } catch (InterruptedException ex) {
                            // ignore
                        } finally {
                            cardPanel.showCard( "startingGame" );
                        }
                    }
                };
                t.start();
            }
        });
        
        return p0;
        
    }
    
    private JComponent createStartingGameCard() {
        Resources res = Resources.getInstance();
        
        JLabel label = new JLabel( res.getText("gui", "opening.starting.pleaseWait") );        
        label.setFont( FONT );
        label.setForeground( Color.WHITE );
        label.setHorizontalAlignment( JLabel.CENTER );
        label.setAlignmentX( 0.5f );        
        
        JPanel p0 = new JPanel();
        p0.setName( "startingGame" );
        p0.setOpaque( true );
        p0.setBackground( Color.BLACK );
        p0.setLayout( new BoxLayout(p0, BoxLayout.PAGE_AXIS) );
        p0.add( Box.createGlue() );
        p0.add( label );
        p0.add( Box.createGlue() );
        p0.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                createGame();
            }
        });
        
        return p0;
    }
    
    private JComponent createIntroductionCard() {                
        Resources res = Resources.getInstance();
        
        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule(
            "body {" +
                "color: #ffffff; " +
                "font-size: 16pt; " +
                "font-weight: plain; " +
                "font-family: serif; " +
                "text-align: justify; " +
                "line-height: 1.5em;" +
            "} " +
            "h1 {" +
                "font-size: 26pt; " +
                "text-align: center;" +
                "margin-bottom: 20px;" +
            "}" +
            "p {" +
                "text-indent: 2em;" +
                "margin-bottom: 10px;" +
            "} " +
            "p.right {" +
                "text-align: right;" +
            "}"
        );
        
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.setStyleSheet( styleSheet );
        
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable( false );
        editorPane.setOpaque( false );
        editorPane.setEditorKit( kit );
        editorPane.setDocument( kit.createDefaultDocument() );
        editorPane.setAlignmentY( 0.5f );
        editorPane.setText( "<html><body>"+res.getText("gui","opening.introduction.text")+"</body></html>" );
        
        JLabel pressAnyKeyLabel = new JLabel();
        pressAnyKeyLabel.setText( "("+res.getText( "gui", "game.nextturn.pressAnyKeyToContinue" )+")" );
        pressAnyKeyLabel.setFont( SMALL_FONT );
        pressAnyKeyLabel.setForeground( Color.WHITE );
        pressAnyKeyLabel.setAlignmentX(0.5f);                
        
        JPanel p0 = new JPanel();
        p0.setName("introduction");
        p0.setOpaque( true );
        p0.setBackground( Color.BLACK );
        p0.setLayout( new BoxLayout( p0, BoxLayout.PAGE_AXIS ) );
        p0.add( Box.createRigidArea( new Dimension(0, 200) ) );
        p0.add( editorPane );
        p0.add( Box.createGlue() );
        p0.add( pressAnyKeyLabel );
        
        return p0;
    }
    
    private void createGame() {
        Dimension d = GameGUI.getInstance().getDisplaySize();
        JPanel overlay = new JPanel();
        overlay.setOpaque( false );
        overlay.setFocusable( true );
        overlay.setSize(d);
        overlay.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSceneCard();
            }
        });
        add( overlay, new Integer(1) );
        
        Thread t = new Thread() {
            @Override
            public void run() {
                GameEngine.getInstance().createGame();
                try {
                    sleep( 100 );
                } catch (InterruptedException ex) {
                    // ignore
                } finally {
                    cardPanel.showCard( "introduction" );
                }
            }
        };
        t.start();
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