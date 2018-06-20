package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameEngineAdapter;
import eu.theeconomystupid.engine.Key;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;


final class VariableDisplay extends JComponent {
    
    
    private static final int WIDTH = 100;
    private static final int HEIGHT = 120;
    private static final Color FONT_COLOR = Color.WHITE;
    
    private String caption;
    private Key variableKey;
    private NumberFormat format;
    private Object value;
    private Font captionFont;
    private Font variableFont;
    private Dimension captionBounds;
    private TextLayout[] captionLayouts;
    private boolean layoutNeeded;
    
    
    VariableDisplay( String caption, Key variableKey, NumberFormat format ) {
        this.caption = caption;
        this.variableKey = variableKey;
        this.format = format;
        setPreferredSize( new Dimension( WIDTH, HEIGHT ) );
        setMaximumSize( new Dimension( WIDTH, HEIGHT ) );
        setMinimumSize( new Dimension( WIDTH, HEIGHT ) );
        setAlignmentX( 1f );        
        captionFont = new Font( Font.SERIF, Font.ITALIC|Font.BOLD, 16 );
        variableFont = new Font( Font.SERIF, Font.ITALIC|Font.BOLD, 42 );
        layoutNeeded = true;
        
        GameEngine.getInstance().addGameEngineListener( new GameEngineAdapter() {
            public void turnCompleted() {
                handleNextTurn();
                
            }
            public void gameCreated() {
                handleNextTurn();
            }
        } );
    }
    
    protected void paintComponent(Graphics g) {
        if ( layoutNeeded ) layoutText(g);
        
        int w = getWidth();
        int h = getHeight();
        float x, y;
        String valueString;
        FontMetrics fm;
        Rectangle2D bounds;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor( FONT_COLOR );
//        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        // Draw caption:
        g2d.setFont( captionFont );
        y = 0;
        for ( TextLayout layout : captionLayouts ) {
            x = round(0.5*(w-layout.getBounds().getWidth()));
            y += layout.getAscent();
            layout.draw( g2d, x, y );
            y += layout.getDescent() + layout.getLeading();
        }
        
        // Draw variable value:
        if ( value != null ) {
            valueString = format.format(value);
        } else {
            valueString = "";
        }
        g2d.setFont( variableFont );
        fm = g.getFontMetrics();
        bounds = fm.getStringBounds( valueString, g2d );
        x = round( 0.5*(w-bounds.getWidth()) );
        y += round( bounds.getHeight() );
        g2d.drawString( valueString, x, y );

    }
    
    private void handleNextTurn() {
        value = GameEngine.getInstance().getVariableValue( variableKey );
        layoutNeeded = true;
    }
    
    private void layoutText( Graphics g ) {
        Map <TextAttribute, Object> map = new HashMap <TextAttribute, Object> ();
        map.put( TextAttribute.FONT, captionFont );
        
        AttributedCharacterIterator aci = new AttributedString(caption, map).getIterator();

        int i_0 = aci.getBeginIndex();
        int i_1 = aci.getEndIndex();
        FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(aci, frc);
        lineMeasurer.setPosition(i_0);

        int h = 0;
        int w = 0;
        List <TextLayout> textLayoutList = new ArrayList <TextLayout> ();
        TextLayout tl;
        while ( lineMeasurer.getPosition() < i_1 ) {
            tl = lineMeasurer.nextLayout(WIDTH);
            h += tl.getAscent() + tl.getDescent() + tl.getLeading();
            textLayoutList.add( tl );
        }

        if ( textLayoutList.size() > 1 ) {
            w = WIDTH;
        } else {
            w = (int) textLayoutList.get(0).getBounds().getWidth();
        }
        
        captionBounds = new Dimension( w, h );
        captionLayouts = textLayoutList.toArray( new TextLayout[textLayoutList.size()] );
        
        layoutNeeded = false;
    }
    
    private static final int round( double d ) {
        return (int) Math.round(d);
    }
    
}
