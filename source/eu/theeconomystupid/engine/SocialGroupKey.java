package eu.theeconomystupid.engine;

public final class SocialGroupKey extends Key {
    
    public final Key NUM_PEOPLE;
    public final Key POPULATION_PART;
    public final Key APPROVAL_RATING;
    public final Key AVERAGE_GROSS_INCOME;
    public final Key REAL_AVERAGE_GROSS_INCOME;
    
    protected SocialGroupKey( String socialGroupKey ) {
        super( socialGroupKey );
        NUM_PEOPLE = new Key( socialGroupKey + ".numPeople" );
        POPULATION_PART = new Key( socialGroupKey + ".populationPart" );
        APPROVAL_RATING = new Key( socialGroupKey + ".approvalRating" );
        AVERAGE_GROSS_INCOME = new Key( socialGroupKey + ".averageGrossIncome" );
        REAL_AVERAGE_GROSS_INCOME = new Key( socialGroupKey + ".realAverageGrossIncome" );
    }
    
}
