package eu.theeconomystupid.model.framework.core;


import java.io.Serializable;
import java.util.Map;


public abstract class Operator implements Serializable {
    public void configure( Registry r, Map <String,String> config ) {};
    public abstract void execute( Registry r );
    public void cleanup() {};
}
