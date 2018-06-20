package eu.theeconomystupid.gui.structure.game.nextturn;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.NotifiableCard;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;


final class NarrationCard extends JPanel implements NotifiableCard, ActionListener {
    
    
    private static final Font SMALL_FONT = new Font( Font.SERIF, Font.PLAIN, 12 );
    
    private JEditorPane editorPane;
    
    
    NarrationCard() {
        setName( "narration" );        
        
        Resources res = Resources.getInstance();
        
        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {color:#ffffff; font-size:16pt; font-weight:plain; font-family:serif; text-align:justify;}");
        
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.setStyleSheet( styleSheet );
        
        editorPane = new JEditorPane();
        editorPane.setEditable( false );
        editorPane.setOpaque( false );
        editorPane.setEditorKit( kit );
        editorPane.setDocument( kit.createDefaultDocument() );
        editorPane.setAlignmentY( 0.5f );
        
        JLabel pressAnyKeyLabel = new JLabel();
        pressAnyKeyLabel.setText( "("+res.getText( "gui", "game.nextturn.pressAnyKeyToContinue" )+")" );
        pressAnyKeyLabel.setFont( SMALL_FONT );
        pressAnyKeyLabel.setForeground( Color.WHITE );
        pressAnyKeyLabel.setAlignmentX(0.5f);        
        
        setOpaque( true );
        setBackground( Color.BLACK );
        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        add( Box.createRigidArea( new Dimension(0, 200) ) );
        add( editorPane );
        add( Box.createGlue() );
        add( pressAnyKeyLabel );
        
    }
    
    public void aboutToBeShown() {
        GameEngine engine = GameEngine.getInstance();
        Resources res = Resources.getInstance();
        String[] narratives = engine.getNarrativeMessages();
        editorPane.setText( "<html><body>"+res.getText("messages",narratives[0])+"</body></html>" );
    }
    
    
    // ***************************************************
    // ************** ACTION EVENT LISTENER **************
    // ***************************************************
    public void actionPerformed(ActionEvent e) {
        GameGUI.getInstance().showCard( "game.nextturn.empty", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameGUI.getInstance().showCard( "game.scene.report" );
            }
        });
    }
    
}
