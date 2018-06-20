package eu.theeconomystupid.gui.components;


import java.awt.Graphics;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import eu.theeconomystupid.gui.effects.FadeEffect;
import eu.theeconomystupid.gui.effects.PixelizingFadeEffect;


public class AnimatedCardPanel extends CardPanel {
    
    
    public static boolean SUPRESS_ANIMATIONS = false;
    private static final long MILLIS_PER_FRAME = 30;
    private static final long DEFAULT_DURATION = 1000;

    protected FadeEffect effect;
    private String nextCardName;
    private Thread animationThread;
    private boolean animating;
    private long duration;

    
    public AnimatedCardPanel() {
        this( new PixelizingFadeEffect(), DEFAULT_DURATION );
    }
    
    public AnimatedCardPanel( FadeEffect effect, long duration ) {
        setEffect( effect );
        setOpaque( false );
        animating = false;
        this.duration = duration;
    }
    
    public void setEffect( FadeEffect effect ) {
        this.effect = effect;
    }

    public FadeEffect getEffect() {
        return effect;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void showCard( String name, ActionListener actionLister ) {
        if ( animating ) return;
        if ( name != null && (!containsCard( name ) || getCard(name).isVisible()) ) return;
        this.actionLister = actionLister;
        if ( SUPRESS_ANIMATIONS ) {
            super.showCard( name, actionLister );
        } else {
            nextCardName = name;
            animating = true;        
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    if ( effect != null ) {
                        effect.setup( getCurrentCard(), getCard(nextCardName) );
                        animationThread = new AnimationThread();
                        animationThread.start();
                    }
                }
            });
        }
    }
    
    public void showCardImmediately( String name ) {
        if ( !animating ) super.showCard( name, null );
    }
    
    public boolean isAnimating() {
        return animating;
    }
    
    protected void paintChildren(Graphics g) {
        if ( animationThread != null ) {
            if ( getVisibleComponent() != null ) setVisibleComponent(null);
            effect.render(g);
        } else {
            super.paintChildren(g);
        }
    }
    
    private void handleEffectComplete() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                setVisibleComponent( nextCardName );
                animating = false;
                animationThread = null;
                repaint();
                notifyActionListener();
            }
        } );
    }
    
    
    private final class AnimationThread extends Thread {
        
        public void run() {
            long startTime = 0;
            long t0, t1;
            double elapsedTime;
            
            startTime = System.currentTimeMillis();
            try {
                sleep( MILLIS_PER_FRAME );
            } catch (InterruptedException ex) {
                // ignore
            }
            
            while ( !interrupted() ) {
                t0 = System.currentTimeMillis();
                elapsedTime = (double) (t0-startTime)/duration;
                if ( elapsedTime < 1.0 ) {
                    effect.step(elapsedTime);
                } else {
                    effect.step(1.0);
                    handleEffectComplete();
                    break;   
                }
                t1 = System.currentTimeMillis();
                repaint();
                try {
                    sleep( Math.max( 1, MILLIS_PER_FRAME-(t1-t0) ) );
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
        }
        
    }
    
}
