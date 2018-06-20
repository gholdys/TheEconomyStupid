package eu.theeconomystupid.gui.structure.game.nextturn;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameEngineAdapter;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
import javax.swing.JLabel;
import javax.swing.Timer;


final class DateCard extends JLabel {
    
    private static final Font FONT = new Font( Font.SERIF, Font.BOLD, 16 );
    
    DateCard() {
        setName( "date" );
        setFont( FONT );
        setForeground( Color.WHITE );
        setHorizontalAlignment( CENTER );
        
        GameEngine.getInstance().addGameEngineListener( new GameEngineAdapter() {
            public void turnCompleted() {
                refreshDate();
            }
        } );
        
        addComponentListener( new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                pauseAndShowSceneCard();
            }
        });
    }

    private void refreshDate() {
        DateFormat f = Resources.getInstance().getFullDateFormat();
        setText( f.format( GameEngine.getInstance().getVariableValue( Keys.GAME_TIME ) ) );
    }
    
    private void pauseAndShowSceneCard() {
        ActionListener action = new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                if ( GameEngine.getInstance().getNumNarrativeMessages() > 0 ) {
                    GameGUI.getInstance().showCard( "game.nextturn.narration" );
                } else {
                    GameGUI.getInstance().showCard( "game.nextturn.empty", new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            GameGUI.getInstance().showCard( "game.scene.report" );
                        }
                    });
                }
            }
        };
        
        Timer t = new Timer( 800, action );
        t.setRepeats( false );
        t.start();
    }
}
