package eu.theeconomystupid.gui.effects;


import java.awt.Graphics;


public interface VisualEffect {
    void step( double elapsedTime );
    void render( Graphics g );    
}
