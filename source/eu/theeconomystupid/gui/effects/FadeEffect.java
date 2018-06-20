package eu.theeconomystupid.gui.effects;


import eu.theeconomystupid.gui.Utilities;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.RepaintManager;


public abstract class FadeEffect implements VisualEffect {
    
    
    protected BufferedImage image_1;
    protected BufferedImage image_2;
    private boolean crossFading;
    private double elapsedTime;
    
    
    public FadeEffect() {
        this( false );
    }
    
    public FadeEffect( boolean crossFading ) {
        this.crossFading = crossFading;
    }

    public void setCrossFading( boolean crossFading ) {
        this.crossFading = crossFading;
    }

    public final boolean isCrossFading() {
        return crossFading;
    }

    public synchronized void setup( Component c1, Component c2 ) {
        image_1 = Utilities.renderComponentToImage( c1, image_1 );
        image_2 = Utilities.renderComponentToImage( c2, image_2 );
    }
    
    public synchronized final void step( double elapsedTime ) {
        this.elapsedTime = elapsedTime;
        stepFading();
    }

    protected double getElapsedTime() {
        return elapsedTime;
    }
    
    public synchronized void cleanup() {
        if ( image_1 != null ) {
            image_1.flush();
            image_1 = null;
        }
        if ( image_2 != null ) {
            image_2.flush();
            image_2 = null;
        }
    }
    
    protected abstract void stepFading();
        
}
