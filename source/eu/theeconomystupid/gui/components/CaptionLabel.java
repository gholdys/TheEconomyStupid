package eu.theeconomystupid.gui.components;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import eu.theeconomystupid.gui.Utilities;


public class CaptionLabel extends JComponent {

    
    public static final int CENTER = SwingUtilities.CENTER;
    public static final int LEADING = SwingUtilities.LEADING;
    public static final int TRAILING = SwingUtilities.TRAILING;
    
    private String text;
    private int maxWidth;
    private TextLayout[] textLayouts;
    private Dimension textBounds;
    private boolean layoutNeeded;
    int textAlignment;
    
    
    public CaptionLabel() {
        maxWidth = 600;
        layoutNeeded = false;
        textAlignment = SwingConstants.CENTER;
    }

    public void setText( String text ) {
        this.text = text;
        layoutNeeded = true;
    }
    
    public void setHorizontalAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        layoutNeeded = true;
    }

    @Override
    public void setFont(Font font) {
        super.setFont( font );
        layoutNeeded = true;
    }
    
    @Override
    public Dimension getPreferredSize() {
        if ( text == null ) return new Dimension(0,0);
        if ( layoutNeeded ) layoutText();
        
        int h_text, w_text;
        int h, w;
        Insets insets = getInsets();
        
        h_text = textBounds.height;
        w_text = textBounds.width;
        
        h = h_text + insets.top + insets.bottom;
        w = w_text + insets.left + insets.right;
        
        return new Dimension( w, h );
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        if ( layoutNeeded ) layoutText();
        
        int w = getWidth();
        int h = getHeight();
        int h_i = h - getInsets().top - getInsets().bottom;
        Insets insets = getInsets();
        Graphics2D g2d = (Graphics2D) g;
        float y_text = insets.top + (int) Math.round( 0.5 * (h_i-textBounds.height) );
        float x_text = insets.left;
        
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2d.setColor( getForeground() );
        float x, y;
        y = y_text;
        
        switch ( textAlignment ) {
            case LEADING:
                for ( TextLayout layout : textLayouts ) {
                    x = x_text;
                    y += layout.getAscent();
                    layout.draw( g2d, x, y );
                    y += layout.getDescent() + layout.getLeading();
                }
                break;
            case CENTER:
                for ( TextLayout layout : textLayouts ) {
                    x = x_text + (int) Math.round(0.5*(w-insets.right-x_text-layout.getBounds().getWidth()));
                    y += layout.getAscent();
                    layout.draw( g2d, x, y );
                    y += layout.getDescent() + layout.getLeading();
                }
                break;
            case TRAILING:
                for ( TextLayout layout : textLayouts ) {
                    x = w - insets.right - (int) layout.getBounds().getWidth();
                    y += layout.getAscent();
                    layout.draw( g2d, x, y );
                    y += layout.getDescent() + layout.getLeading();
                }
                break;
        }
        
    }
    
    private void layoutText() {
        if ( text == null ) return;
        
        Insets insets = getInsets();
        int w_max = maxWidth - insets.right - insets.left;
        
        Map <TextAttribute, Object> map = new HashMap <TextAttribute, Object> ();
        map.put( TextAttribute.FONT, getFont() );
        
        AttributedCharacterIterator aci = new AttributedString(text, map).getIterator();

        int i_0 = aci.getBeginIndex();
        int i_1 = aci.getEndIndex();
        FontRenderContext frc = Utilities.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(aci, frc);
        lineMeasurer.setPosition(i_0);

        int h = 0;
        int w = 0;
        List <TextLayout> textLayoutList = new ArrayList <TextLayout> ();
        TextLayout tl;
        while ( lineMeasurer.getPosition() < i_1 ) {
            tl = lineMeasurer.nextLayout(w_max);
            h += tl.getAscent() + tl.getDescent() + tl.getLeading();
            textLayoutList.add( tl );
        }

        if ( textLayoutList.size() > 1 ) {
            w = w_max;
        } else {
            w = (int) textLayoutList.get(0).getBounds().getWidth();
        }
        
        textBounds = new Dimension( w, h );
        textLayouts = textLayoutList.toArray( new TextLayout[textLayoutList.size()] );
        layoutNeeded = false;
    }

}