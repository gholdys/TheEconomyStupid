package eu.theeconomystupid.engine;


public class Key {
    
    private String keyString;
    
    protected Key( String keyString ) {
        this.keyString = keyString;
    }
    
    public String toString() {
        return keyString;
    }
    
}
