package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameEngineAdapter;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.gui.Resources;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JComponent;


final class CalendarDisplay extends JComponent {
    
    
    private static final Font DAY_FONT = new Font( Font.SERIF, Font.ITALIC|Font.BOLD, 40 );
    private static final Font MONTH_FONT = new Font( Font.SERIF, Font.ITALIC|Font.BOLD, 14 );
    private static final Font YEAR_FONT = new Font( Font.SERIF, Font.ITALIC|Font.BOLD, 14 );
    private static final int MARGIN_LEFT = -2;
    private static final int MARGIN_TOP = 15;
        
    private BufferedImage backgroundImage;
    private Calendar calendar;
    private String dayFormatString;
    private String monthFormatString;
    private String yearFormatString;
    
    
    CalendarDisplay() {
        backgroundImage = Resources.getInstance().getImage( "calendar.png" );
        setPreferredSize( new Dimension( backgroundImage.getWidth(), backgroundImage.getHeight() ) );
        setMaximumSize( getPreferredSize() );
        setMinimumSize( getPreferredSize() );
        setAlignmentX( 1f );
        calendar = new GregorianCalendar();
        dayFormatString = "%1$te";
        monthFormatString = "%1$tB";
        yearFormatString = "%1$tY";
        
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
        Graphics2D g2d = (Graphics2D) g;
        
        int w = getWidth()-MARGIN_LEFT;
        int h = getHeight()-MARGIN_TOP;
        int x, y;
        FontMetrics fm;
        Rectangle2D bounds;
        
        g2d.drawImage( backgroundImage, 0, 0, null );
//        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        Locale locale = Resources.getInstance().getCurrentLocale();
        String day = String.format( locale, dayFormatString, calendar );
        String month = String.format( locale, monthFormatString, calendar );
        String year = String.format( locale, yearFormatString, calendar );
        
        g.setFont( DAY_FONT );
        fm = g.getFontMetrics();
        bounds = fm.getStringBounds( day, g );
        x = MARGIN_LEFT+round(0.5*(w-bounds.getWidth()));
        y = MARGIN_TOP + round(bounds.getHeight());
        g.drawString( day, x, y );
        
        g.setFont( MONTH_FONT );
        fm = g.getFontMetrics();
        bounds = fm.getStringBounds( month, g );
        x = MARGIN_LEFT+round(0.5*(w-bounds.getWidth()));
        y += 15+bounds.getHeight();
        g.drawString( month, x, y );
        
        g.setFont( YEAR_FONT );
        fm = g.getFontMetrics();
        bounds = fm.getStringBounds( year, g );
        x = MARGIN_LEFT+round(0.5*(w-bounds.getWidth()));
        y += 5+bounds.getHeight();
        g.drawString( year, x, y );
        
    }
    
    private void handleNextTurn() {
        calendar.setTime( (Date) GameEngine.getInstance().getVariableValue( Keys.GAME_TIME ) );
    }
    
    private static final int round( double d ) {
        return (int) Math.round(d);
    }
    
}