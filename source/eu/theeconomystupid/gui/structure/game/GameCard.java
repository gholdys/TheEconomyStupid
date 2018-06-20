package eu.theeconomystupid.gui.structure.game;


import eu.theeconomystupid.gui.structure.game.nextturn.NextTurnCard;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.components.AnimatedCardPanel;
import eu.theeconomystupid.gui.effects.AlphaFadeEffect;
import eu.theeconomystupid.gui.structure.game.popups.PopupsCard;
import eu.theeconomystupid.gui.structure.game.scene.SceneCard;


public final class GameCard extends AnimatedCardPanel {
    
    
    public GameCard() {
        setName("game");
        setOpaque( true );
        setPreferredSize( GameGUI.getInstance().getDisplaySize() );
        setEffect( new AlphaFadeEffect(true) );
        setDuration( 500 );
        setFocusable( false );

        SceneCard sceneCard = new SceneCard();
        PopupsCard popupsCard = new PopupsCard( sceneCard );
        NextTurnCard nextTurnCard = new NextTurnCard();
        
        add( sceneCard );
        add( popupsCard );
        add( nextTurnCard );
    }
    
}
