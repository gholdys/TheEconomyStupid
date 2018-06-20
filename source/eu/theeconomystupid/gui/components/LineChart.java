package eu.theeconomystupid.gui.components;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.gui.Resources;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public final class LineChart extends JPanel {
    

    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    private static final Color AXIS_COLOR = new Color( 0, 0, 0 );
    private static final Color GRID_COLOR = new Color( 150, 150, 150 );
    private static final Stroke GRID_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] {5f, 5f}, 0f );
    private static final Stroke PLOT_STROKE = new BasicStroke( 3f );
    private static final Font FONT = new Font( Font.MONOSPACED, Font.PLAIN, 10 );
    private static final int TICK_LINE_LENGTH = 5;
    private static final int TICK_LABEL_GAP = 3;
    private static final int HORIZONTAL_LABEL_GAP = 15;
    private static final int VERTICAL_LABEL_GAP = 15;
    private static final int ICON_SIZE = 12;    
    private static final Color ICON_BORDER_COLOR = Color.BLACK;
    private static final Stroke ICON_BORDER_STROKE = new BasicStroke(2f);
    
    private JPanel legendPane;
    private List <List <Number>> dataSeriesList;
    private List <Color> colorsList;
    private List <String> nameList;
    private double minY, minX;
    private double maxY, maxX;
    private NumberFormat yAxisFormat;
    private String dateFormatString;
    private Calendar calendar;
    private long initTime;
    private double tickSpacingY;
    private int numMonthsPerTurn;

    
    public LineChart() {
        FlowLayout layout = new FlowLayout();
        layout.setHgap( 20 );
        
        legendPane = new JPanel( layout );        
        legendPane.setOpaque( false );
        legendPane.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
        
        setOpaque( false );
        setLayout( new BorderLayout() );
        add( new ChartArea(), BorderLayout.CENTER );
        add( legendPane, BorderLayout.SOUTH );
        
        dataSeriesList = new ArrayList <List <Number>> ();
        colorsList = new ArrayList <Color> ();
        nameList = new ArrayList <String> ();        
        dateFormatString = "%1$tb,%1$tY";
        calendar = new GregorianCalendar();
        
        numMonthsPerTurn = 1;
    }
    
    public void setYAxisFormat(NumberFormat yAxisFormat) {
        this.yAxisFormat = yAxisFormat;
    }
   
    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }
    
    public void addDataSeries( Color color, List <Number> data ) {
        addDataSeries( color, null, data );
    }
    
    public void addDataSeries( Color color, String name, List <Number> data ) {
        dataSeriesList.add( data );
        colorsList.add( color );
        nameList.add( name );
        recalculateBounds();
        refreshLegend();
    }
    
    public void clearDataSeries() {
        dataSeriesList.clear();
        colorsList.clear();
        nameList.clear();
        recalculateBounds();
        refreshLegend();
    }
    
    private void recalculateBounds() {
        numMonthsPerTurn = (Integer) GameEngine.getInstance().getVariableValue( Keys.NUM_MONTHS_PER_TURN );
        if ( dataSeriesList.size() == 0 ) {
            minY = 0;
            maxY = 0;
            minX = 0;
            maxX = 0;
        } else {
            int i, j;
            List <Number> dataSeries;
            Number value;
            minY = Double.MAX_VALUE;
            maxY = Double.MIN_VALUE;
            minX = 0;
            maxX = 0;
            for ( i=0; i<dataSeriesList.size(); i++ ) {
                dataSeries = dataSeriesList.get(i);
                if ( dataSeries != null ) {
                    for ( j=0; j<dataSeries.size(); j++ ) {
                        value = dataSeries.get(j);
                        if ( minY > value.doubleValue() ) minY = value.doubleValue();
                        if ( maxY < value.doubleValue() ) maxY = value.doubleValue();
                    }
                    if ( maxX < dataSeries.size()-1 ) maxX = dataSeries.size()-1;
                }
            }
            double dy = maxY - minY;
            minY -= 0.1*dy;
            maxY += 0.1*dy;
            // Calculate optimum y axis tick spacing
            double tsY = 0.1*(maxY-minY);
            double log10TsY = Math.log10( tsY );
            if ( yAxisFormat == Resources.getInstance().getPercentFormat() ) {
                if ( log10TsY < 0.0 && -log10TsY > yAxisFormat.getMaximumFractionDigits()+2 ) {
                    tickSpacingY = Math.pow( 10.0, -yAxisFormat.getMaximumFractionDigits()-2 );
                }
            } else {
                if ( log10TsY < 0.0 && -log10TsY > yAxisFormat.getMaximumFractionDigits() ) {
                    tickSpacingY = Math.pow(10.0, -yAxisFormat.getMaximumFractionDigits() );
                }
            }
            
        }
    }
    
    private void refreshLegend() {
        legendPane.removeAll();
        if ( nameList.size() > 0 ) {
            JLabel label;
            for ( int i=0; i<nameList.size(); i++ ) {
                if ( nameList.get(i) != null ) {
                    label = new JLabel( nameList.get(i), new ColorIcon(colorsList.get(i)), JLabel.LEADING );
                    legendPane.add( label );
                }
            }
        }
        legendPane.revalidate();
    }
    
    private static final int round( double d ) {
        return (int) Math.round(d);
    }
    
    
    private final class ChartArea extends JComponent {
        protected void paintComponent( Graphics g ) {
            Graphics2D g2d;
            int i, j;
            List <Number> dataSeries;
            double x0, y0, x1, y1;
            int x, y, w, h;
            int numTicksY, numTicksX;
            double firstTickY;
            int firstTickX;
            FontMetrics fm;
            Insets insets;
            double sx, sy;
            int originX, originY;
            int yAxisLength, xAxisLength;
            int labelsMarginX;
            Rectangle2D rect;
            double tsY, tsX;
            String label;
            int order;

            w = getWidth();
            h = getHeight();        
            insets = getInsets();                

            g2d = (Graphics2D) g;        
            g2d.setFont( FONT );
            fm = g2d.getFontMetrics();                

            if ( tickSpacingY > 0.0 ) {
                tsY = 0.5 * tickSpacingY;
            } else {
                tsY = 0.1 * (maxY-minY);
                if ( yAxisFormat == Resources.getInstance().getPercentFormat() ) {
                    order = -(yAxisFormat.getMinimumFractionDigits()+2);
                } else {
                    order = -yAxisFormat.getMinimumFractionDigits();
                }
                if ( tsY < Math.pow( 10, order ) ) {
                    tsY = Math.pow( 10, order );
                }
                tsY *= 0.5;
            }

            // Calculate the Y coordinate of axis origin point
            originY = round( h-insets.bottom-fm.getStringBounds( "/", g2d ).getHeight()-TICK_LABEL_GAP-TICK_LINE_LENGTH );
            // Calculate the length in pixels of the Y axis
            yAxisLength = originY - insets.top - round( 0.5*fm.getStringBounds( "/", g2d ).getHeight() );
            // Calculate the scaling coefficient for Y axis
            sy = yAxisLength / (maxY-minY);

            // Calculate number of ticks along the Y axis
            do {
                tsY *= 2;
                numTicksY = calculateNumTicks( minY, maxY, tsY );
                firstTickY = Math.ceil(minY/tsY)*tsY;
                rect = fm.getStringBounds( yAxisFormat.format(firstTickY), g2d );
            } while ( numTicksY*rect.getHeight()+(numTicksY-1)*VERTICAL_LABEL_GAP > yAxisLength && numTicksY > 1 );
            
            // The number of ticks must at least three
            if ( numTicksY < 3 ) {
                firstTickY = minY;
                tsY = 0.5*(maxY-minY);
                numTicksY = 3;
            }

            // Calculate the left margin for Y value labels
            labelsMarginX = 0;
            for ( i=0; i<numTicksY; i++ ) {
                y0 = firstTickY+i*tsY;
                rect = fm.getStringBounds( yAxisFormat.format(y0), g2d );
                if ( rect.getWidth() > labelsMarginX ) labelsMarginX = round(rect.getWidth());
            }
            labelsMarginX = round( 1.1*(labelsMarginX+TICK_LABEL_GAP) );

            // Calculate the X coordinate of axis origin point
            originX = insets.left+TICK_LINE_LENGTH+labelsMarginX;
            // Calculate the length in pixels of the X axis
            xAxisLength = w - insets.right - originX - labelsMarginX;
            // Calculate the scaling coefficient for X axis
            sx = xAxisLength / (maxX-minX);

            // Calculate number of ticks along the X axis
            calendar.setTimeInMillis( initTime );        
            tsX = 0.5; // 0.5 because we multiply by 2.0 two lines later.
            do {
                tsX *= 2;
                numTicksX = calculateNumTicks( minX, maxX, tsX );
                firstTickX = (int) (Math.ceil(minX/tsX)*tsX);
                label = String.format( Resources.getInstance().getCurrentLocale(), dateFormatString, calendar );
                rect = fm.getStringBounds( label, g2d );
            } while ( numTicksX*rect.getWidth()+(numTicksX-1)*HORIZONTAL_LABEL_GAP > xAxisLength && numTicksX > 1 );

            // Draw ticks along Y axis
            for ( i=0; i<numTicksY; i++ ) {
                y0 = firstTickY+i*tsY;
                label = yAxisFormat.format(y0);
                rect = fm.getStringBounds( label, g2d );
                y = round( originY-sy*(y0-minY) );
                g2d.setColor( GRID_COLOR );
                g2d.setStroke( GRID_STROKE );
                g2d.drawLine( originX, y, originX+xAxisLength, y );
                g2d.setColor( AXIS_COLOR );
                g2d.setStroke( AXIS_STROKE );
                g2d.drawLine( originX-TICK_LINE_LENGTH, y, originX, y );
                g2d.drawString( 
                    label, 
                    round( originX-TICK_LINE_LENGTH-rect.getWidth()-TICK_LABEL_GAP ),
                    round( y-rect.getY()-0.5*rect.getHeight() )
                );
            }

            // Draw ticks and grid lines along X axis
            calendar.setTimeInMillis( initTime );
            calendar.add( Calendar.MONTH, firstTickX );
            
            for ( i=0; i<numTicksX; i++ ) {
                x0 = firstTickX+i*tsX;
                label = String.format( Resources.getInstance().getCurrentLocale(), dateFormatString, calendar );
                rect = fm.getStringBounds( label, g2d );
                x = round( originX+sx*(x0-minX) );
                g2d.setColor( GRID_COLOR );
                g2d.setStroke( GRID_STROKE );
                g2d.drawLine( x, originY-yAxisLength, x, originY );
                g2d.setColor( AXIS_COLOR );
                g2d.setStroke( AXIS_STROKE );
                g2d.drawLine( x, originY+TICK_LINE_LENGTH, x, originY );
                g2d.drawString( 
                    label, 
                    round( x-0.5*rect.getWidth() ),
                    round( originY+TICK_LINE_LENGTH+TICK_LABEL_GAP+rect.getHeight() )
                );
                calendar.add( Calendar.MONTH, round(tsX*numMonthsPerTurn) );
            }

            // Draw plot lines
            g2d.setStroke( PLOT_STROKE );
            for ( i=0; i<dataSeriesList.size(); i++ ) {
                dataSeries = dataSeriesList.get(i);
                if ( dataSeries != null ) {
                    g2d.setColor( colorsList.get(i) );
                    x0 = (0-minX)/(maxX-minX)*xAxisLength;
                    y0 = (dataSeries.get(0).doubleValue()-minY)*sy;
                    for ( j=1; j<dataSeries.size(); j++ ) {
                        x1 = (j-minX)/(maxX-minX)*xAxisLength;
                        y1 = (dataSeries.get(j).doubleValue()-minY)*sy;
                        g2d.drawLine( 
                            round(originX+x0), 
                            round(originY-y0),
                            round(originX+x1),
                            round(originY-y1) 
                        );
                        x0 = x1;
                        y0 = y1;
                    }
                }
            }

            // Draw axis
            g2d.setColor( AXIS_COLOR );
            g2d.setStroke( AXIS_STROKE );
            g2d.drawLine( originX, originY, originX, originY-yAxisLength );
            g2d.drawLine( originX, originY, originX+xAxisLength, originY );

        }
    }
    
    private static int calculateNumTicks( double min, double max, double spacing ) {
        int floor = (int) Math.floor( (max-min) / spacing );
        int ceil = (int) Math.ceil( (max-min) / spacing );
        if ( floor == ceil ) {
            return ceil+1;
        } else {
            return ceil;
        }
    }
    
    
    private static final class ColorIcon implements Icon {

        
        private Color color;


        public ColorIcon( Color color ) {
            this.color = color;
        }

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke( ICON_BORDER_STROKE );
            g2d.setColor( color );
            g2d.fillRect( x, y, ICON_SIZE, ICON_SIZE );
            g2d.setColor( ICON_BORDER_COLOR );
            g2d.drawRect( x+1, y+1, ICON_SIZE-2, ICON_SIZE-2 );
        }

        public int getIconWidth() {
            return ICON_SIZE;
        }

        public int getIconHeight() {
            return ICON_SIZE;
        }

    }
    
}
