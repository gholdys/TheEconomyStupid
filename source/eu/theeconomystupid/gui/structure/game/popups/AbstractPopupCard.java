package eu.theeconomystupid.gui.structure.game.popups;


import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.CaptionLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public abstract class AbstractPopupCard extends JPanel {
    

    public static final Color GRADIENT_COLOR_1 = new Color( 226, 226, 226, 255 );
    public static final Color GRADIENT_COLOR_2 = new Color( 215, 215, 215, 255 );
    public static final Color BORDER_COLOR = Color.BLACK;
    public static final int BORDER_WIDTH = 3;
    public static final Color CAPTION_COLOR = Color.BLACK;
    public static final Font CAPTION_FONT = new Font( Font.SERIF, Font.BOLD, 26 );
        
    private CaptionLabel captionLabel;
    private JPanel contentsPanel;
    private JPanel bottomPanel;
    private String closeCardName;
    
    
    AbstractPopupCard() {
        this( null, null, null );
    }
    
    AbstractPopupCard( String cardName ) {
        this( cardName, null, null );
    }
    
    AbstractPopupCard( String cardName, String caption ) {
        this( cardName, caption, null );
    }        
    
    AbstractPopupCard( String cardName, String caption, String closeCardName ) {
        contentsPanel = new ContentsPane();
        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        add( Box.createGlue() );
        add( contentsPanel );
        add( Box.createGlue() );
        setOpaque( false );        
        createTopPanel();
        rebuildBottomPanel( closeCardName != null );
        setCloseCardName( closeCardName );
        setName( cardName );
        setCaption( caption );
    }

    public void setCaption( String caption ) {
        captionLabel.setText( caption );
    }
    
    public void setCloseCardName( String cardName ) {
        this.closeCardName = cardName;
        rebuildBottomPanel( closeCardName != null );
    }
    
    public JComponent setContents( JComponent c ) {
        contentsPanel.add( c, BorderLayout.CENTER );
        return c;
    }
    
    private void createTopPanel() {
        captionLabel = new CaptionLabel();
        captionLabel.setForeground( CAPTION_COLOR );
        captionLabel.setFont( CAPTION_FONT );
        captionLabel.setBorder( new EmptyBorder( 10, 20, 10, 20 ) );
        contentsPanel.add( captionLabel, BorderLayout.NORTH );
    }

    private void rebuildBottomPanel( boolean showCloseButton ) {
        if ( showCloseButton ) {
            if ( bottomPanel != null ) {
                bottomPanel.removeAll();
                bottomPanel.add( createCloseButton() );
            } else {
                bottomPanel = new JPanel();
                bottomPanel.setOpaque( false );
                bottomPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
                bottomPanel.setLayout( new FlowLayout() );
                bottomPanel.add( createCloseButton() );
                contentsPanel.add( bottomPanel, BorderLayout.SOUTH );
            }
        } else if ( bottomPanel != null ) {
            bottomPanel.removeAll();
            contentsPanel.remove( bottomPanel );
            bottomPanel = null;
        }
    }
    
    private JButton createCloseButton() {
        Resources res = Resources.getInstance();
        String iconKey = "close";
        JButton b = new JButton( res.getText("gui", "game.popups.close") );
        b.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        return b;
    }
    
    private void close() {
        GameGUI.getInstance().showCard( closeCardName );
    }
    
    
    private static final class ContentsPane extends JPanel {
        
        private ContentsPane() {
            setOpaque( true );
            setBorder( new CompoundBorder(
                new LineBorder( BORDER_COLOR, BORDER_WIDTH ), 
                new BevelBorder( BevelBorder.LOWERED )
            ) );
            setLayout( new BorderLayout() );
            setAlignmentX( 0.5f );
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
                    0, h, GRADIENT_COLOR_2
                )
            );
            g2d.fillRect( x, y, w, h );
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
        
    }
    
}