package eu.theeconomystupid.engine;

final class MessageCondition {
    
    
    private String variableKey;
    private MessageConditionType type;
    private Object value;
    
    
    MessageCondition( String variableKey, MessageConditionType type, Object value ) {
        this.variableKey = variableKey;
        this.type = type;
        this.value = value;
    }

    public String getVariableKey() {
        return variableKey;
    }

    public MessageConditionType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int hashCode() {
        return (variableKey+type.toString()).hashCode();
    }
    
}
