package eu.theeconomystupid.gui;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.RepaintManager;
import javax.swing.Timer;


public final class Utilities {
    
    
    private static final JFrame DUMMY_FRAME = new JFrame();
    private static final AlphaComposite CLEAR_COMPOSITE = AlphaComposite.getInstance( AlphaComposite.CLEAR, 0.0f );
    
    
    public static BufferedImage renderContainerToImage( Container container ) {
        return renderContainerToImage( container, null );
    }
    
    public static BufferedImage renderContainerToImage( Container container, BufferedImage targetImage ) {
        if ( !container.isValid() ) {
            DUMMY_FRAME.setContentPane( container );
            DUMMY_FRAME.pack();
        }
        
        int w = container.getWidth();
        int h = container.getHeight();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        
        if ( targetImage == null || targetImage.getWidth() != w || targetImage.getHeight() != h ) {
            if ( container.isOpaque() ) {
                targetImage = gc.createCompatibleImage( w, h );
            } else {
                targetImage = gc.createCompatibleImage( w, h, Transparency.TRANSLUCENT );
            }
        } else {
            Graphics2D g2d = targetImage.createGraphics();
            g2d.setComposite( CLEAR_COMPOSITE );
            g2d.fillRect( 0, 0, w, h );
        }
        
        RepaintManager rm = RepaintManager.currentManager(container);
        boolean temp = rm.isDoubleBufferingEnabled();
        rm.setDoubleBufferingEnabled( false );
        container.paint( targetImage.createGraphics() );
        rm.setDoubleBufferingEnabled( temp );
        
        return targetImage;
    }
    
    public static BufferedImage renderComponentToImage( Component component, BufferedImage targetImage ) {
        int w = component.getWidth();
        int h = component.getHeight();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        
        if ( targetImage == null || 
             targetImage.getWidth() != w || 
             targetImage.getHeight() != h || 
             !component.isOpaque() && (targetImage.getTransparency() != Transparency.TRANSLUCENT) ||
             component.isOpaque() && (targetImage.getTransparency() != Transparency.OPAQUE) ) {
            if ( component.isOpaque() ) {
                targetImage = gc.createCompatibleImage( w, h );
            } else {
                targetImage = gc.createCompatibleImage( w, h, Transparency.TRANSLUCENT );
            }
        } else {
            Graphics2D g2d = targetImage.createGraphics();
            g2d.setComposite( CLEAR_COMPOSITE );
            g2d.fillRect( 0, 0, w, h );
        }
        
        RepaintManager rm = RepaintManager.currentManager(component);
        boolean temp = rm.isDoubleBufferingEnabled();
        rm.setDoubleBufferingEnabled( false );
        component.paint( targetImage.createGraphics() );
        rm.setDoubleBufferingEnabled( temp );
        
        return targetImage;
    }
    
    public static String decapitalize( String s ) {
        if ( Character.isUpperCase(s.charAt(0)) ) {
            StringBuffer buffer = new StringBuffer(s);
            buffer.setCharAt( 0, Character.toLowerCase(s.charAt(0)) );
            s = buffer.toString();
        }
        return s;
    }
    
    public static String capitalize( String s ) {
        if ( Character.isLowerCase(s.charAt(0)) ) {
            StringBuffer buffer = new StringBuffer(s);
            buffer.setCharAt( 0, Character.toUpperCase(s.charAt(0)) );
            s = buffer.toString();
        }
        return s;
    }
    
    public static FontRenderContext getFontRenderContext() {
        if ( !DUMMY_FRAME.isValid() ) DUMMY_FRAME.pack();
        return ( (Graphics2D) DUMMY_FRAME.getGraphics() ).getFontRenderContext();
    }
    
    public static Dimension computeStringBounds( String s, Font f ) {
        if ( !DUMMY_FRAME.isValid() ) DUMMY_FRAME.pack();
        FontMetrics fm = DUMMY_FRAME.getGraphics().getFontMetrics(f);
        int h = fm.getHeight();
        int w = fm.stringWidth(s);
        return new Dimension( w, h );
    }

    private Utilities() {}
    
}
