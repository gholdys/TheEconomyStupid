package eu.theeconomystupid.model.framework.core;


import java.io.Serializable;
import java.util.Set;


public interface Registry extends Serializable {
    Object get( String key );
    Class<?> getType( String key );
    Object put( String key, Object value );
    Set <String> keySet();
    double getDouble( String key );
    int getInteger( String key );
}
