package eu.theeconomystupid;


import eu.theeconomystupid.gui.GameGUI;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public final class Main {
    
    public static void main( String... args ) {
        final JFrame frame = new JFrame( "The economy, stupid!" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
//        GameGUI.configure( new Locale("es", "ES") );
        GameGUI.configure( new Locale("en", "GB") );
        GameGUI.getInstance().setOwner( frame, null );
        
        frame.pack();
        frame.setResizable( false );
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    frame.setVisible( true );
                    GameGUI.getInstance().newGame();
                }
            }
        );
    }
    
}
