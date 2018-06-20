package eu.theeconomystupid.engine;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;


final class MessageGenerator {
    
    
    private List<MessageDefinition> messageDefinitions;
    private Comparator<MessageDefinition> comparator;
    
    
    MessageGenerator( Reader reader ) {
        BufferedReader defReader = new BufferedReader(reader);
        messageDefinitions = new ArrayList <MessageDefinition> ();
        
        String line;
        String[] idAndCat = null;
        String id;
        String cat;
        
        try {
            for ( line = defReader.readLine(); line != null; line = defReader.readLine() ) {
                if ( line.startsWith("Message") ) {
                    idAndCat = extractIDAndCat( line, idAndCat );
                    if ( idAndCat == null ) continue;
                    id = idAndCat[0];
                    cat = idAndCat[1];
                    messageDefinitions.add( readMessageDefinition( defReader, id, cat ) );
                }
            }
            defReader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        comparator = new MessageComparator();
    }
    
    String[] generateMessages( Game game ) {
        List <MessageDefinition> messageDefs = new ArrayList <MessageDefinition> ();
        
        for ( MessageDefinition def : messageDefinitions ) {
            if ( conditionsMet( def, game ) ) {
                messageDefs.add( def );
            }
        }
                
        sortAndRemoveDuplicates( messageDefs );
        String[] messages = new String[messageDefs.size()];
        
        for ( int i=0; i<messages.length; i++ ) {
            messages[i] = messageDefs.get(i).getID();
        }
        
        return messages;
    }

    private boolean conditionsMet( MessageDefinition def, Game game ) {
        Set <MessageCondition> conditions = def.getConditions();
        boolean conditionHolds;
        Object objCurrentValue;
        Object objTargetValue;
        double v_c, v_t;
        double rd_c, rd_t;
        double ad_c, ad_t;
        
        for ( MessageCondition c : conditions ) {
            objCurrentValue = game.getVariableValue( c.getVariableKey() );
            objTargetValue = c.getValue();
            conditionHolds = true;
            if ( objCurrentValue == null ) {
                conditionHolds = false;
            } else if ( objCurrentValue instanceof Number ) {
                switch (c.getType()) {
                    case VALUE_EQ:
                        v_c = ((Number) objCurrentValue).doubleValue();
                        v_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = v_t == v_c;
                        break;
                    case VALUE_GT:
                        v_c = ((Number) objCurrentValue).doubleValue();
                        v_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = v_c > v_t;
                        break;
                    case VALUE_GE:
                        v_c = ((Number) objCurrentValue).doubleValue();
                        v_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = v_c >= v_t;
                        break;
                    case VALUE_LT:
                        v_c = ((Number) objCurrentValue).doubleValue();
                        v_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = v_c < v_t;
                        break;
                    case VALUE_LE:
                        v_c = ((Number) objCurrentValue).doubleValue();
                        v_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = v_c <= v_t;
                        break;
                    case RELATIVE_DELTA_EQ:
                        objCurrentValue = game.getVariableRelativeDelta( c.getVariableKey() );
                        rd_c = ((Number) objCurrentValue).doubleValue();
                        rd_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = rd_t == rd_c;
                        break;
                    case RELATIVE_DELTA_GT:
                        objCurrentValue = game.getVariableRelativeDelta( c.getVariableKey() );
                        rd_c = ((Number) objCurrentValue).doubleValue();
                        rd_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = rd_c > rd_t;
                        break;
                    case RELATIVE_DELTA_GE:
                        objCurrentValue = game.getVariableRelativeDelta( c.getVariableKey() );
                        rd_c = ((Number) objCurrentValue).doubleValue();
                        rd_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = rd_c >= rd_t;
                        break;
                    case RELATIVE_DELTA_LT:
                        objCurrentValue = game.getVariableRelativeDelta( c.getVariableKey() );
                        rd_c = ((Number) objCurrentValue).doubleValue();
                        rd_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = rd_c < rd_t;
                        break;
                    case RELATIVE_DELTA_LE:
                        objCurrentValue = game.getVariableRelativeDelta( c.getVariableKey() );
                        rd_c = ((Number) objCurrentValue).doubleValue();
                        rd_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = rd_c <= rd_t;
                        break;
                    case ABSOLUTE_DELTA_EQ:                        
                        objCurrentValue = game.getVariableAbsoluteDelta( c.getVariableKey() );
                        ad_c = ((Number) objCurrentValue).doubleValue();
                        ad_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = ad_t == ad_c;
                        break;
                    case ABSOLUTE_DELTA_GT:
                        objCurrentValue = game.getVariableAbsoluteDelta( c.getVariableKey() );
                        ad_c = ((Number) objCurrentValue).doubleValue();
                        ad_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = ad_c > ad_t;
                        break;
                    case ABSOLUTE_DELTA_GE:
                        objCurrentValue = game.getVariableAbsoluteDelta( c.getVariableKey() );
                        ad_c = ((Number) objCurrentValue).doubleValue();
                        ad_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = ad_c >= ad_t;
                        break;
                    case ABSOLUTE_DELTA_LT:
                        objCurrentValue = game.getVariableAbsoluteDelta( c.getVariableKey() );
                        ad_c = ((Number) objCurrentValue).doubleValue();
                        ad_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = ad_c < ad_t;
                        break;
                    case ABSOLUTE_DELTA_LE:
                        objCurrentValue = game.getVariableAbsoluteDelta( c.getVariableKey() );
                        ad_c = ((Number) objCurrentValue).doubleValue();
                        ad_t = ((Number) objTargetValue).doubleValue();
                        conditionHolds = ad_c <= ad_t;
                        break;
                }
            } else if ( objCurrentValue.getClass().isArray() ) {
                switch (c.getType()) {
                    case VALUE_EQ:
                        if ( !Arrays.deepEquals( (Object[]) objCurrentValue, (Object[]) objTargetValue ) ) {
                            conditionHolds = false;
                        }
                        break;
                    case VALUE_CONTAINS:
                        Object[] array_c = (Object[]) objCurrentValue;
                        Object[] array_t;
                        if ( objTargetValue.getClass().isArray() ) {
                            array_t = (Object[]) objTargetValue;
                        } else {
                            array_t = new Object[] {objTargetValue};
                        }
                        int n = array_t.length;
                        for ( Object obj_t : array_t ) {
                            for ( Object obj_c : array_c ) {
                                if ( obj_c.equals( obj_t ) ) {
                                    n--;
                                    break;
                                }
                            }
                        }
                        if ( n > 0 ) {
                            conditionHolds = false;
                        }
                        break;
                }
            } else {
                if ( c.getType() == MessageConditionType.VALUE_EQ ) {
                    if ( !objCurrentValue.equals(objTargetValue) ) {
                        conditionHolds = false;
                    }
                }
            }
            if ( !conditionHolds ) return false;
        }
        
        return true;
    }
    
    private void sortAndRemoveDuplicates( List <MessageDefinition> messageDefs ) {
        Collections.sort( messageDefs, comparator );
        
        Map <Integer,List<MessageDefinition>> duplicatesLookup = new HashMap <Integer,List<MessageDefinition>> ();        
        
        MessageDefinition def0 = null, def1 = null;
        List<MessageDefinition> duplicates;
        for ( Iterator <MessageDefinition> iter = messageDefs.iterator(); iter.hasNext(); ) {
            if ( def0 == null ) {
                def0 = iter.next();
            } else {
                def1 = iter.next();
                if ( def0.getWeight() == def1.getWeight() ) {
                    duplicates = duplicatesLookup.get( def0.getWeight() );
                    if ( duplicates != null ) {
                        duplicates.add( def1 );
                    } else {
                        duplicates = new ArrayList <MessageDefinition> ();
                        duplicates.add( def0 );
                        duplicates.add( def1 );
                        duplicatesLookup.put( def0.getWeight(), duplicates );
                    }
                }
                def0 = def1;
            }
        }
        
        int iRemains, i;
        Random r = new Random();
        for ( List <MessageDefinition> d : duplicatesLookup.values() ) {
            iRemains = r.nextInt( d.size() );
            for ( i=0; i<d.size(); i++ ) {
                if ( i != iRemains ) messageDefs.remove( d.get(i) );
            }
        }
        
    }

    
    //**********************************************
    //********** STATIC PARSING METHODS ************
    //**********************************************    
    private static MessageDefinition readMessageDefinition( BufferedReader defReader, String id, String weightString ) throws IOException {
        Set <MessageCondition> conditions = new LinkedHashSet <MessageCondition> ();
        String line;
        int weight;
        MessageCondition condition;
        
        if ( weightString == null ) {
            weight = 0;
        } else {
            weight = Integer.parseInt(weightString);
        }
        
        for ( line = defReader.readLine(); line != null; line = defReader.readLine() ) {
            if ( line.startsWith("#") ) {
                continue;
            } else if ( line.startsWith("End") ) {
                return new MessageDefinition( id, weight, conditions );
            } else {
                condition = readCondition( line );
                if ( condition != null ) conditions.add( condition );
            }
        }
        
        return null;
    }
    
    private static MessageCondition readCondition( String text ) throws IOException {   
        int i0 = text.indexOf(':');
        if (i0 == -1) return null;
        
        String variableKey = text.substring(0, i0).trim();
        text = text.substring(i0+1);
        
        StringTokenizer tokenizer = new StringTokenizer(text, " ");
        String strConditionType = tokenizer.nextToken();
        String strValue = tokenizer.nextToken();
        
        MessageConditionType type = MessageConditionType.valueOf( strConditionType.toUpperCase() );
        Object value = parseValue( strValue );

        return new MessageCondition( variableKey, type, value );
    }
    
    private static String[] extractIDAndCat( String text, String[] ret ) {
        int i0 = text.indexOf(':');
        int i1 = text.indexOf('[');
        int i2 = text.indexOf(']');
        
        if ( i0 == -1 ) return null;
        
        String id;
        String cat;
        if ( i1 == -1 ) {
            id = text.substring(i0+1).trim();
            cat = null;
        } else {
            id = text.substring(i0+1, i1).trim();
            cat = text.substring(i1+1, i2).trim();
        }
        
        if ( ret == null ) ret = new String[2];
        ret[0] = id;
        ret[1] = cat;
        
        return ret;
    }
    
    private static Object parseValue( String text ) {
        try {
            double dValue = Double.parseDouble( text );
            return dValue;
        } catch ( NumberFormatException ex ) {}
        
        if ( text.equalsIgnoreCase("true") ) {
            return true;
        } else if ( text.equalsIgnoreCase("false") ) {
            return false;
        }
        
        Object[] array = parseArray( text );
        if ( array != null ) return array;
        
        return text;
        
    }
    
    private static Object[] parseArray( String text ) {
        StringTokenizer tokenizer = new StringTokenizer( text, "," );
        Object[] array = new Object[tokenizer.countTokens()];
        if ( array.length == 0 ) return null;
        
        int i=0;
        while ( tokenizer.hasMoreTokens() ) {
            array[i++] = tokenizer.nextToken().trim();
        }
        
        return array;
    }
    
    
    //**********************************************
    //************* COMPARATOR CLASS ***************
    //**********************************************
    private static final class MessageComparator implements Comparator <MessageDefinition> {
        
        public int compare( MessageDefinition d1, MessageDefinition d2 ) {
            return d2.getWeight() - d1.getWeight();
        }
        
    }

}