package eu.theeconomystupid.engine;


import java.util.Set;


final class MessageDefinition {
    
    
    private String id;
    private int weight;
    private Set<MessageCondition> conditions;
    
    
    MessageDefinition( String id, int weight, Set<MessageCondition> conditions ) {
        this.id = id;
        this.weight = weight;
        this.conditions = conditions;
    }

    public Set<MessageCondition> getConditions() {
        return conditions;
    }

    public String getID() {
        return id;
    }

    public int getWeight() {
        return weight;
    }
    
}
