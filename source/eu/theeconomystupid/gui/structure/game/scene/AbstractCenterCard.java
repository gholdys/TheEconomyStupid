package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameEngineListener;
import eu.theeconomystupid.gui.Resources;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


abstract class AbstractCenterCard extends JPanel implements GameEngineListener {
    
    
    public static final Color GRADIENT_COLOR_1 = new Color( 194, 194, 194, 221 );
    public static final Color GRADIENT_COLOR_2 = new Color( 226, 226, 226, 221 );
    public static final Color BORDER_COLOR = Color.BLACK;
    public static final int BORDER_WIDTH = 3;
    public static final Color CAPTION_COLOR = Color.BLACK;
    public static final Font CAPTION_FONT = new Font( Font.SERIF, Font.ITALIC|Font.BOLD, 26 );
    
    
    AbstractCenterCard( String cardName ) {
        setName( cardName );
        setBorder( new CompoundBorder(
            new LineBorder( BORDER_COLOR, BORDER_WIDTH ), 
            new BevelBorder( BevelBorder.LOWERED )
        ) );
        setLayout( new BorderLayout() );
        setOpaque( false );
        createTopPanel( "game.scene."+cardName+".caption", "game.popups."+cardName+"_help" );
        GameEngine.getInstance().addGameEngineListener( this );
    }
    
    protected void refreshData() {
        // do nothing
    }

    protected JComponent setContents( JComponent c ) {
        add( c, BorderLayout.CENTER );
        return c;
    }
    
    protected void paintComponent(Graphics g) {
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        int w = getWidth()-insets.left-insets.right;
        int h = getHeight()-insets.top-insets.bottom;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint( 
            new GradientPaint(
                0, 0, GRADIENT_COLOR_1, 
                200, 0, GRADIENT_COLOR_2
            )
        );
        g2d.fillRect( x, y, w, h );
    }
    
    
    private void createTopPanel( String captionKey, String helpCardKey ) {
        JLabel label = new JLabel( Resources.getInstance().getText("gui", captionKey) );
        label.setForeground( CAPTION_COLOR );
        label.setFont( CAPTION_FONT );
        label.setHorizontalAlignment( JLabel.CENTER );
        label.setVerticalAlignment( JLabel.CENTER );
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        p0.setLayout( new FlowLayout() );
        p0.add( label );
        
        add( p0, BorderLayout.NORTH );
    }

    
    // ************************************************
    // ******** ENGINE LISTENER IMPLEMENTATION ********
    // ************************************************
    public void turnCompleted() {
        refreshData();
    }

    public void gameCreated() {
        refreshData();
    }

    public void gameAboutToBeCreated() {
        // do nothing
    }
    
}
