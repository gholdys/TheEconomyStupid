package eu.theeconomystupid.gui.structure.game.nextturn;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameResult;
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


final class GameOverCard extends JPanel implements NotifiableCard, ActionListener {
    
    
    private static final Font LARGE_FONT = new Font( Font.SERIF, Font.BOLD, 40 );
    private static final Font SMALL_FONT = new Font( Font.SERIF, Font.PLAIN, 12 );
    
    private JLabel headerLabel;
    private JEditorPane editorPane;
    private JLabel pressAnyKeyLabel;
    
    
    GameOverCard() {
        setName( "gameOver" );
        
        Resources res = Resources.getInstance();
        
        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {color:#ffffff; font-size:16pt; font-weight:bold; font-family:serif; text-align:center;}");
        
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.setStyleSheet( styleSheet );
        
        headerLabel = new JLabel();
        headerLabel.setText( res.getText( "gui", "game.nextturn.gameover.header" ) );
        headerLabel.setFont( LARGE_FONT );
        headerLabel.setForeground( Color.WHITE );
        headerLabel.setAlignmentX(0.5f);
        
        editorPane = new JEditorPane();
        editorPane.setEditable( false );
        editorPane.setOpaque( false );
        editorPane.setEditorKit( kit );
        editorPane.setDocument( kit.createDefaultDocument() );
        editorPane.setAlignmentY( 0.5f );
        
        pressAnyKeyLabel = new JLabel();
        pressAnyKeyLabel.setText( "("+res.getText( "gui", "game.nextturn.gameover.pressAnyKeyToEnd" )+")" );
        pressAnyKeyLabel.setFont( SMALL_FONT );
        pressAnyKeyLabel.setForeground( Color.WHITE );
        pressAnyKeyLabel.setAlignmentX(0.5f);        
        
        setOpaque( true );
        setBackground( Color.BLACK );
        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        add( Box.createRigidArea( new Dimension(0, 250) ) );
        add( headerLabel );
        add( Box.createRigidArea( new Dimension(0, 20) ) );
        add( editorPane );
        add( Box.createGlue() );
        add( pressAnyKeyLabel );

    }

    public void aboutToBeShown() {
        GameEngine engine = GameEngine.getInstance();
        Resources res = Resources.getInstance();
        
        GameResult result = engine.getGameResult();
        switch ( result ) {
            case SUCCESS:
                editorPane.setText( "<html><body>"+res.getText("gui","game.nextturn.gameover.gameWon")+"</body></html>" );
                break;
            case FAILURE_LOST_ELECTIONS:
                editorPane.setText( "<html><body>"+res.getText("gui","game.nextturn.gameover.electionsLost")+"</body></html>" );
                break;
            case FAILURE_REMOVED_FROM_OFFICE:
                editorPane.setText( "<html><body>"+res.getText("gui","game.nextturn.gameover.resignantion")+"</body></html>" );
                break;
        }
        
    }

    public void actionPerformed(ActionEvent e) {
        GameGUI.getInstance().exit();
    }
    
}
