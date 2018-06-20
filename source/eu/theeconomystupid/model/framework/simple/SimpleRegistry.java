package eu.theeconomystupid.model.framework.simple;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.Map.Entry;


public class SimpleRegistry implements Registry {
    
    
    private Map <String,Object> valueMap;
    private Map <String,Class<?>> typeMap;
    
    
    public SimpleRegistry( Map <String,Class<?>> typeMap ) {
        this.typeMap = new LinkedHashMap <String,Class<?>> ( typeMap );
        valueMap = new LinkedHashMap <String,Object> ();
        Class <?> c;
        for ( String key : typeMap.keySet() ) {
            c = typeMap.get( key );
            if ( c == Double.class ) {
                valueMap.put( key, 0.0 );
            } else if ( c == Float.class ) {
                valueMap.put( key, 0f );
            } else if ( c == Integer.class || c == Byte.class || c == Short.class ) {
                valueMap.put( key, 0 );
            } else if ( c == Long.class ) {
                valueMap.put( key, 0l );
            } else if ( c == Boolean.class ) {
                valueMap.put( key, false );
            } else {
                valueMap.put( key, null );
            }
            
        }
    }

    public Object get( String key ) {
        checkKey( key );
        return valueMap.get( key );
    }
    
    public Class<?> getType(String key) {
        checkKey( key );
        return typeMap.get( key );
    }
    
    public Object put( String key, Object value ) {
        checkKey( key );
        if ( value != null ) {
            Class <?> c0 = typeMap.get( key );
            Class <?> c1 = value.getClass();
            if ( !c0.isAssignableFrom( c1 ) ) {
                throw new IllegalArgumentException( 
                    "Value with key \"" + key + "\" should be of type \"" + 
                    c0.getName() + "\" and not \"" + c1.getName() + "\"."  
                );
            }
        }
        return valueMap.put( key, value );
    }

    public Set<String> keySet() {
        return valueMap.keySet();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for ( Entry <String,Object> e : valueMap.entrySet() ) {
            buffer.append( e.getKey() );
            buffer.append( " = " );
            buffer.append( e.getValue() );
            buffer.append( "\n" );
        }
        return buffer.toString();
    }
    
    public double getDouble( String key ) {
        return (Double) get( key );
    }

    public int getInteger( String key ) {
        return (Integer) get( key );
    }        
    
    private void checkKey( String key ) {
        if ( !valueMap.containsKey( key ) ) {
            throw new IllegalArgumentException( "Registry does not contain an entry with key \"" + key + "\"" );
        }
    }
            
}
