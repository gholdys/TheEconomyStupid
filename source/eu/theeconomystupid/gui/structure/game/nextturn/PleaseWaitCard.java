package eu.theeconomystupid.gui.structure.game.nextturn;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import javax.swing.Timer;


final class PleaseWaitCard extends JLabel {
    
    private static final Font FONT = new Font( Font.SERIF, Font.BOLD, 16 );
    
    public PleaseWaitCard() {
        super( Resources.getInstance().getText("gui", "game.nextturn.pleaseWait") );
        setName( "pleaseWait" );
        setFont( FONT );
        setForeground( Color.WHITE );
        setHorizontalAlignment( CENTER );
        
        addComponentListener( new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                runEngine();
            }
        });
    }
    
    private void runEngine() {
        ActionListener action = new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                System.gc();
                GameEngine.getInstance().nextTurn();
                try {
                    Thread.currentThread().sleep( 500 );
                } catch (InterruptedException ex) {
                    // ignore
                } finally {
                    GameGUI.getInstance().showCard( "game.nextturn.empty", new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            showNextCard();
                        }
                    });
                }
            }
        };
        
        Timer t = new Timer( 100, action );
        t.setRepeats( false );
        t.start();
    }
    
    private void showNextCard() {
        GameEngine engine = GameEngine.getInstance();
        if ( engine.isElectionsTurn() ) {
            GameGUI.getInstance().showCard( "game.nextturn.elections" );
        } else if ( engine.gameOver() ) {  // Prime minister removed from office
            GameGUI.getInstance().showCard( "game.nextturn.gameOver" );
        } else {
            GameGUI.getInstance().showCard( "game.nextturn.date" );
        }
    }
    
}
