package eu.theeconomystupid.gui.structure.game.popups;


import eu.theeconomystupid.gui.Resources;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;


final class ReduceDeficitPopup extends AbstractPopupCard {
    
    ReduceDeficitPopup() {
        Resources res = Resources.getInstance();
        setName( "reduceDeficit" );
        setCaption( res.getText( "gui", "game.popups.reduceDeficit.caption" ) );
        setCloseCardName( "game.scene" );
        
        JLabel label = new JLabel( "<html><body><p style=\"text-align:center\">" + res.getText( "gui", "game.popups.reduceDeficit.text" ) + "</p></body></html>" );
        label.setForeground( Color.BLACK );
        label.setFont( new Font( Font.SERIF, Font.BOLD, 16 ) );
        label.setBorder( new EmptyBorder( 20, 40, 40, 40 ) );
        
        setContents( label );
    }
    
}
