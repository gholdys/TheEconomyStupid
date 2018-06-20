package eu.theeconomystupid.gui.effects;


import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public final class PixelizingFadeEffect extends FadeEffect {

    
    private static final AlphaComposite CLEAR_COMPOSITE = AlphaComposite.getInstance( AlphaComposite.CLEAR, 0.0f );
    
    private BufferedImage backgroundImage;
    private int[] tiles;
    private int numTiles;
    private int numTilesInRow;
    private int tileSize;
    private int prevWidth;
    private int prevHeight;
    private double elapsedTime0;
    
    
    public PixelizingFadeEffect() {
        this(4);
    }
    
    public PixelizingFadeEffect( boolean crossFading ) {
        this( 4, crossFading );
    }
    
    public PixelizingFadeEffect( int tileSize ) {        
        this( tileSize, false );
    }
    
    public PixelizingFadeEffect( int tileSize, boolean crossFading ) {
        super( crossFading );
        this.tileSize = tileSize;
    }

    public void render( Graphics g ) {
        g.drawImage( backgroundImage, 0, 0, null );
    }
    
    protected void stepFading() {
        if ( isCrossFading() ) {
            stepCrossFading();
        } else {
            stepSlowFading();
        }
    }
    
    private void stepCrossFading() {
        int i, yt, xt, y, x, len;
        int w = image_1.getWidth();
        int h = image_2.getHeight();
        int wt, ht;
        
        int i0 = (int) Math.floor(1.0*elapsedTime0*numTiles);
        int i1 = (int) Math.floor(1.0*getElapsedTime()*numTiles);
        if ( i1 > numTiles ) i1 = numTiles;
        
        elapsedTime0 = getElapsedTime();
        
        WritableRaster r_b = backgroundImage.getRaster();
        WritableRaster r_1 = image_1.getRaster();
        WritableRaster r_2 = image_2.getRaster();
        int[] data = null;
        
        int tile;
        for ( i=i0; i<i1; i++ ) {
            tile = tiles[i];
            yt = tile/numTilesInRow;
            xt = tile - yt*numTilesInRow;
            y = yt*tileSize;
            x = xt*tileSize;
            if ( x+tileSize >= w ) {
                wt = w-x;
            } else {
                wt = tileSize;
            }
            if ( y+tileSize >= h ) {
                ht = h-y;
            } else {
                ht = tileSize;
            }
            
            len = 4*wt*ht;
            if ( data == null || data.length != len ) {
                data = new int[4*wt*ht];
            }
            
            data = r_2.getPixels( x, y, wt, ht, data );
            r_b.setPixels( x, y, wt, ht, data );
            
        }
        
    }
    
    private void stepSlowFading() {
        int i, yt, xt, y, x, len;
        int w = image_1.getWidth();
        int h = image_2.getHeight();
        int wt, ht;
        double et0, et1;
        
        boolean fadeIn;
        if ( getElapsedTime() > 0.5 ) {
            fadeIn = true;
            et0 = Math.max( 0.0, elapsedTime0-0.5 );
            et1 = getElapsedTime()-0.5;
        } else {
            fadeIn = false;
            et0 = elapsedTime0;
            et1 = getElapsedTime();
            if ( 0.5-et1 < et1-et0 ) et1 = 0.5;
        }
        elapsedTime0 = getElapsedTime();
        
        int i0 = (int) Math.floor(2.0*et0*numTiles);
        int i1 = (int) Math.floor(2.0*et1*numTiles);
        if ( i1 > numTiles ) i1 = numTiles;
        
        WritableRaster r_b = backgroundImage.getRaster();
        WritableRaster r_1 = image_1.getRaster();
        WritableRaster r_2 = image_2.getRaster();
        int[] data = null;
        
        int tile;
        for ( i=i0; i<i1; i++ ) {
            tile = tiles[i];
            yt = tile/numTilesInRow;
            xt = tile - yt*numTilesInRow;
            y = yt*tileSize;
            x = xt*tileSize;
            if ( x+tileSize >= w ) {
                wt = w-x;
            } else {
                wt = tileSize;
            }
            if ( y+tileSize >= h ) {
                ht = h-y;
            } else {
                ht = tileSize;
            }
            
            len = 4*wt*ht;
            if ( data == null || data.length != len ) {
                data = new int[4*wt*ht];
            }
            
            if ( fadeIn ) {
                r_2.getPixels( x, y, wt, ht, data );
                r_b.setPixels( x, y, wt, ht, data );
            } else {
                Arrays.fill( data, 0 );
                r_b.setPixels( x, y, wt, ht, data );
            }
        }
    }
    
    public synchronized void setup( Component c1, Component c2 ) {
        super.setup( c1, c2 );

        int w = image_1.getWidth();
        int h = image_1.getHeight();
        
        if ( w != prevWidth || h != prevHeight ) {
            prevWidth = w;
            prevHeight = h;
            
            backgroundImage = new BufferedImage( w, h, image_1.getType() );

            numTilesInRow = (int) Math.ceil(1.0*w/tileSize);
            numTiles = numTilesInRow * (int) Math.ceil(1.0*h/tileSize);
            List <Integer> tileList = new ArrayList <Integer> ();
            for ( int i=0; i<numTiles; i++ ) {
                tileList.add(i);
            }

            Collections.shuffle( tileList );

            tiles = new int[numTiles];
            for ( int i=0; i<numTiles; i++ ) {
                tiles[i] = tileList.get(i);
            }
        } else {
            Graphics2D g2d = backgroundImage.createGraphics();
            g2d.setComposite( CLEAR_COMPOSITE );
            g2d.fillRect( 0, 0, w, h );
        }
        
        Graphics2D g2d = backgroundImage.createGraphics();
        g2d.drawImage( image_1, 0, 0, null );
        
        elapsedTime0 = 0.0;
    }

    public void cleanup() {
        backgroundImage.flush();
        backgroundImage = null;
        super.cleanup();
    }
    
}
