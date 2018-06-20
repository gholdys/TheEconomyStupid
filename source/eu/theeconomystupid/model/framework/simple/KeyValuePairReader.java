package eu.theeconomystupid.model.framework.simple;


import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;


public class KeyValuePairReader extends LineNumberReader {
    
    public KeyValuePairReader( Reader in ) {
        super(in);
    }
    
    public String[] readNextKeyValuePair() throws IOException {
        return readNextKeyValuePair( null );
    }
    
    public String[] readNextKeyValuePair( String[] keyValuePair ) throws IOException {
        if ( keyValuePair == null || keyValuePair.length != 2 ) keyValuePair = new String[2];
        
        String line;
        while ( ( line = readLine() ) != null ) {
            int i0 = line.indexOf("=");
            int i1 = line.indexOf("#");
            if ( i0 == -1 ) i0 = line.indexOf(":");
            if ( i0 == -1 ) i0 = 0;
            if ( i1 != -1 && i0 > i1 ) i0 = 0;
            if ( i1 == -1 ) i1 = line.length();
            String s;

            if ( i0 == 0 ) {
                if ( i1 == 0 ) {
                    keyValuePair[0] = null;
                    keyValuePair[1] = null;
                } else {
                    s = line.substring( i0, i1 ).trim();
                    keyValuePair[0] = ( s.length() > 0 ) ? s : null;
                    keyValuePair[1] = ( s.length() > 0 ) ? s : null;
                }
            } else {
                s = line.substring( 0, i0 ).trim();
                keyValuePair[0] = ( s.length() > 0 ) ? s : null;
                s= line.substring( i0+1, i1 ).trim();
                keyValuePair[1] = ( s.length() > 0 ) ? s : null;
            }
            
            if ( keyValuePair[0] == null ) {  // Key is null.
                // Comment, or empty line. Ignore.
            } else {
                return keyValuePair;
            }
        }
        
        return null;
    }
     
    
    private static void extractKeyValuePair( String line, String[] keyValuePair ) {
        int i0 = line.indexOf("=");
        int i1 = line.indexOf("#");
        if ( i0 == -1 ) i0 = line.indexOf(":");
        if ( i0 == -1 ) i0 = 0;
        if ( i1 != -1 && i0 > i1 ) i0 = 0;
        if ( i1 == -1 ) i1 = line.length();
        String s;
        
        if ( i0 == 0 ) {
            if ( i1 == 0 ) {
                keyValuePair[0] = null;
                keyValuePair[1] = null;
            } else {
                s = line.substring( i0, i1 ).trim();
                keyValuePair[0] = ( s.length() > 0 ) ? s : null;
                keyValuePair[1] = ( s.length() > 0 ) ? s : null;
            }
        } else {
            s = line.substring( 0, i0 ).trim();
            keyValuePair[0] = ( s.length() > 0 ) ? s : null;
            s= line.substring( i0+1, i1 ).trim();
            keyValuePair[1] = ( s.length() > 0 ) ? s : null;
        }
    }
    
}
