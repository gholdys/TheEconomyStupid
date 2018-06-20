package eu.theeconomystupid.gui.effects;


import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;


public final class AlphaFadeEffect extends FadeEffect {
    
    
    private float alpha;
    private boolean fadeOutImage_1;
    

    public AlphaFadeEffect() {
        super();
    }
    
    public AlphaFadeEffect( boolean crossFading ) {
        super( crossFading );
    }

    public synchronized void setup( Component c1, Component c2 ) {
        super.setup( c1, c2 );
        fadeOutImage_1 = !c1.isOpaque();
    }

    public void render( Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        if ( isCrossFading() ) {
            if ( fadeOutImage_1 ) {
                g2d.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha) );
            } else {
                g2d.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f) );
            }
            g2d.drawImage( image_1, 0, 0, null );
            g2d.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f-alpha) );
            g2d.drawImage( image_2, 0, 0, null );
        } else {
            g2d.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha) );
            if ( getElapsedTime() < 0.5 ) {
                g2d.drawImage( image_1, 0, 0, null );
            } else {
                g2d.drawImage( image_2, 0, 0, null );
            }
        }
    }
    
    protected void stepFading() {
        if ( isCrossFading() ) {
            alpha = (float) (0.5*(1.0+Math.cos( Math.PI*getElapsedTime() )));
        } else {
            alpha = (float) (0.5*(1.0+Math.cos( 2.0*Math.PI*getElapsedTime() )));
        }
    }

}
