package eu.theeconomystupid.gui.effects;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


public final class HorizontalRotationEffect extends FadeEffect {
    
    
    private double alpha;
    
    
    public HorizontalRotationEffect() {
        super( false );
    }
    
    protected void stepFading() {
        alpha = 0.5*(1.0+Math.cos( 2.0*Math.PI*getElapsedTime() ));
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.transform( AffineTransform.getTranslateInstance( 0.5*image_1.getWidth(), 0.0 ) );
        g2d.transform( AffineTransform.getScaleInstance( alpha, 1.0 ) );
        g2d.transform( AffineTransform.getTranslateInstance( -0.5*image_1.getWidth(), 0.0 ) );
        if ( getElapsedTime() < 0.5 ) {
            g2d.drawImage( image_1, 0, 0, null );
        } else {
            g2d.drawImage( image_2, 0, 0, null );
        }
    }
    
}
