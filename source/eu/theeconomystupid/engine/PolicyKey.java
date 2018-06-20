package eu.theeconomystupid.engine;

public final class PolicyKey extends Key {

    public final Key VALUE;
    public final Key COSTS;
    public final Key REVENUES;
    
    protected PolicyKey( String policyKey ) {
        super( policyKey );
        VALUE = new Key( policyKey+".value" );
        COSTS = new Key( policyKey+".costs" );
        REVENUES = new Key( policyKey+".revenues" );
    }
    
}
