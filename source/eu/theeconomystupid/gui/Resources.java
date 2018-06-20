package eu.theeconomystupid.gui;


import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public final class Resources {
    
    
    private static final String LOCALES_DIR = "locales";
    private static final String GRAPHICS_DIR = "graphics";
    private static final String[] GROUPS = {"gui", "variables", "messages", "moods"};
    private static Resources instance;
    
    
    public static Resources getInstance() {
        if ( instance == null ) throw new RuntimeException( "\"Resources\" not configured yet!" );
        return instance;
    }
    
    public static Resources configure( Locale locale ) {
        instance = new Resources( locale );
        return instance;
    }
    
    
    private Locale locale;
    private String localeDir;
    private PropertyResourceBundle[] bundles;
    private MessageFormat messageFormatter;
    private DateFormat dateFormatter;
    private Map <String,BufferedImage> imageCache;
    private NumberFormat percentFormat;
    private NumberFormat decimalFormat;
    private NumberFormat billionFormat;
    private NumberFormat integerFormat;
    private NumberFormat currencyFormat;
    private DateFormat mediumDateFormat;
    private DateFormat fullDateFormat;

    
    private Resources( Locale locale ) {
        this.locale = locale;
        localeDir = LOCALES_DIR + "/" + locale + "/";
        bundles = new PropertyResourceBundle[GROUPS.length];
        
        ClassLoader classLoader = getClass().getClassLoader();
        
        try {
            for ( int i=0; i<GROUPS.length; i++ ) {
                bundles[i] = new PropertyResourceBundle( 
                    classLoader.getResourceAsStream( localeDir + GROUPS[i] + ".txt" )
                );
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        messageFormatter = new MessageFormat("");
        messageFormatter.setLocale(locale);
        dateFormatter = DateFormat.getDateInstance( DateFormat.LONG, locale );
        imageCache = new HashMap <String,BufferedImage> ();
        
        percentFormat = NumberFormat.getPercentInstance( locale );
        percentFormat.setMinimumFractionDigits(1);
        percentFormat.setMaximumFractionDigits(1);
        
        decimalFormat = NumberFormat.getNumberInstance( locale );
        decimalFormat.setMinimumFractionDigits(1);
        decimalFormat.setMaximumFractionDigits(1);
        
        final NumberFormat df = NumberFormat.getNumberInstance( locale );
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        
        currencyFormat = new NumberFormat() {
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                toAppendTo = df.format(number, toAppendTo, pos);
                if ( number < 0 ) {
                    toAppendTo.insert(1, "\u20AC");
                } else {
                    toAppendTo.insert(0, "\u20AC");
                }
                return toAppendTo;
            }
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return null;
            }
            public Number parse(String source, ParsePosition parsePosition) {
                return null;
            }
        };
        
        billionFormat = new NumberFormat() {
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                toAppendTo = currencyFormat.format(number, toAppendTo, pos);
                toAppendTo.append( " bln" );
                return toAppendTo;
            }
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return null;
            }
            public Number parse(String source, ParsePosition parsePosition) {
                return null;
            }
        };
        
        integerFormat = NumberFormat.getIntegerInstance( locale );
        
        mediumDateFormat = DateFormat.getDateInstance( DateFormat.MEDIUM, locale );
        fullDateFormat = DateFormat.getDateInstance( DateFormat.FULL, locale );
    }
    
    public String getText( String textGroup, String key ) {
        for ( int i=0; i<GROUPS.length; i++ ) {
            if ( textGroup.equalsIgnoreCase( GROUPS[i] ) ) {
                return bundles[i].getString(key);
            }
        }
        throw new IllegalArgumentException( "No such text group: \"" + textGroup + "\"!" );
    }
    
    public String getText( String textGroup, String key, Object... args ) {
        String text = null;
        for ( int i=0; i<GROUPS.length; i++ ) {
            if ( textGroup.equalsIgnoreCase( GROUPS[i] ) ) {
                text = bundles[i].getString(key);
            }
        }
        if ( text != null ) {
            messageFormatter.applyPattern( text );
            return messageFormatter.format( args );
        } else {
            throw new IllegalArgumentException( "No such text group: \"" + textGroup + "\"!" );
        }
    }
    
    public String formatDate( Date date ) {
        return dateFormatter.format( date );
    }
    
    public BufferedImage getImage( String fileName ) {
        BufferedImage img = imageCache.get(fileName);
        if ( img == null ) {
            try {
                String path = GRAPHICS_DIR + "/" + fileName;
                img = ImageIO.read( getClass().getClassLoader().getResourceAsStream( path ) );
                if ( img != null ) {
                    imageCache.put( fileName, img );
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return img;
    }
    
    public ImageIcon getImageIcon( String fileName ) {
        BufferedImage image = getImage(fileName);
        return new ImageIcon(image);
    }
    
    public Locale getCurrentLocale() {
        return locale;
    }
    
    public NumberFormat getIntegerFormat() {
        return integerFormat;
    }

    public NumberFormat getDecimalFormat() {
        return decimalFormat;
    }

    public NumberFormat getPercentFormat() {
        return percentFormat;
    }

    public NumberFormat getBillionFormat() {
        return billionFormat;
    }

    public NumberFormat getCurrencyFormat() {
        return currencyFormat;
    }

    public DateFormat getMediumDateFormat() {
        return mediumDateFormat;
    }
    
    public DateFormat getFullDateFormat() {
        return fullDateFormat;
    }

}