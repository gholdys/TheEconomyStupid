package eu.theeconomystupid.model.framework.simple;


import eu.theeconomystupid.model.framework.core.Model;
import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;


public class SimpleModel implements Model {
    
    
    private Registry registry;
    private Operator[] operators;
    
    
    public SimpleModel( Registry registry, Operator[] operators ) {
        if ( registry == null ) throw new IllegalArgumentException( "Argument \"registry\" cannot be null!!!" );
        
        this.registry = registry;
        this.operators = operators;
    }
    
    public Registry getRegistry() {
        return registry;
    }
    
    public void step() {
        if ( operators != null ) {
            for ( Operator operator : operators ) {
                operator.execute( registry );
            }
        }
    }
    
    public void cleanup() {
        if ( operators != null ) {
            for ( Operator operator : operators ) {
                operator.cleanup();
            }
        }
    }
    
    
}
