package eu.theeconomystupid.gui.structure.game.popups;


import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


final class ConfirmQuitPopup extends AbstractPopupCard {
    
    public ConfirmQuitPopup() {
        Resources res = Resources.getInstance();
        setName( "confirmQuit" );
        setCaption( res.getText( "gui", "game.popups.confirmQuit.caption" ) );
        
        JButton yesButton = new JButton( res.getText( "gui", "game.popups.confirmQuit.yes" ) );
        yesButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameGUI.getInstance().exit();
            }
        });
        
        JButton noButton = new JButton( res.getText( "gui", "game.popups.confirmQuit.no" ) );
        noButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameGUI.getInstance().showCard("game.scene");
            }
        });
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setLayout( new FlowLayout( FlowLayout.CENTER, 20, 0 ) );
        p0.add( noButton );
        p0.add( yesButton );
        p0.setBorder( new EmptyBorder( 40, 40, 10, 40 ) );
        
        setContents( p0 );
    }
    
}
