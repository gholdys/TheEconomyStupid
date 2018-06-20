package eu.theeconomystupid.model.framework.simple;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;


public class SimpleModelFactory {
    
    
    public static final String[] DEFAULT_CLASS_NAME_PREFIXES = {"java.lang.", "java.util."};
    
    private Reader registryDefinitionReader;
    private Reader registryValuesReader;
    private Reader operatorClassNamesReader;
    private Reader operatorConfigReader;
    
    public SimpleModelFactory() {
        
    }

    public void setOperatorClassNamesReader(Reader operatorClassNamesReader) {
        this.operatorClassNamesReader = operatorClassNamesReader;
    }

    public void setOperatorConfigReader(Reader operatorConfigReader) {
        this.operatorConfigReader = operatorConfigReader;
    }

    public void setRegistryDefinitionReader(Reader registryDefinitionReader) {
        this.registryDefinitionReader = registryDefinitionReader;
    }

    public void setRegistryValuesReader(Reader registryValuesReader) {
        this.registryValuesReader = registryValuesReader;
    }    
    
    public Registry createRegistry() throws IOException, ClassNotFoundException {
        Map <String,Map<String,String>> patternDefMap = new HashMap <String,Map<String,String>> ();
        Map <String,String> typeNameMap = new LinkedHashMap <String,String> ();
        Map <String,Class<?>> typeMap = new LinkedHashMap <String,Class<?>> ();
        
        readRegistryDefinition( registryDefinitionReader, patternDefMap, typeNameMap );
        createTypeMap( patternDefMap, typeNameMap, typeMap );
        
        return new SimpleRegistry( typeMap );
    }
    
    public void parseRegistryValues( Registry r ) throws IOException {
        KeyValuePairReader reader = new KeyValuePairReader( registryValuesReader );
        
        String[] keyValuePair = new String[2];
        String key, value;
        String dataType;
        Object objValue;
        Map <String,Object> specifiedValues = new HashMap <String,Object> ();
        
        while ( ( keyValuePair = reader.readNextKeyValuePair(keyValuePair) ) != null ) {
            key = keyValuePair[0];
            value = keyValuePair[1];
            dataType = r.getType( key ).getName();
            objValue = parseValue( value, dataType );
            r.put( key, objValue );
        }
        
        reader.close();
    }
    
    public Operator[] createOperators( Registry r ) 
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

        String[] classNames = parseOperatorClassNames( operatorClassNamesReader );
        Map <String,Map<String,String>> configs = parseOperatorConfigurations( operatorConfigReader );
        
        Operator[] operators = new Operator[ classNames.length ];
        Class <?> c;
        Map <String,String> config;
        Operator operator;
        for ( int i=0; i<operators.length; i++ ) {
            c = Class.forName( classNames[i] );            
            operator = (Operator) c.newInstance();            
            config = configs.get( c.getSimpleName() );
            if ( config == null ) {  // If the simple name doesn't work, try the full name.
                config = configs.get( c.getName() );
            }
            operator.configure( r, config );
            operators[i] = operator;
        }
        
        return operators;
    }
    
    public SimpleModel createSimpleModel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        Registry registry = createRegistry();
        parseRegistryValues( registry );
        Operator[] operators;
        if ( operatorClassNamesReader == null || operatorConfigReader == null ) {
            operators = new Operator[0];
        } else {
            operators = createOperators( registry );
        }
        return  new SimpleModel( registry, operators );
    }
    
    
    //*******************************************
    //************* PRIVATE METHODS *************
    //*******************************************    
    private void readRegistryDefinition( Reader registryDefinitionReader, Map <String,Map<String,String>> patternDefMap, Map <String,String> typeNameMap ) throws IOException {
        KeyValuePairReader r = new KeyValuePairReader( registryDefinitionReader );
        
        String[] keyValuePair = new String[2];
        String key, value;

        while ( ( keyValuePair = r.readNextKeyValuePair(keyValuePair) ) != null ) {
            key = keyValuePair[0];
            value = keyValuePair[1];
            if ( key.equalsIgnoreCase("pattern") ) {
                patternDefMap.put( value, readPatternDefinition(r) );
            } else {
                typeNameMap.put( key, value );
            }
        }
        
        registryDefinitionReader.close();
    }

    private Map <String,String> readPatternDefinition( KeyValuePairReader r ) throws IOException {
        String[] keyValuePair = new String[2];
        String key, value;

        Map <String,String> structTypeNameMap = new HashMap <String,String> ();
        while ( ( keyValuePair = r.readNextKeyValuePair(keyValuePair) ) != null ) {
            key = keyValuePair[0];
            value = keyValuePair[1];
            if ( key.equalsIgnoreCase("end") ) {
                return structTypeNameMap;
            } else {
                structTypeNameMap.put( key, value );
            }
        }
     
        return null;
    }
    
    private void createTypeMap( Map <String,Map<String,String>> patternDefMap, Map <String,String> typeNameMap, Map <String,Class<?>> typeMap ) throws ClassNotFoundException {
        String typeName;
        for ( Entry <String,String> e : typeNameMap.entrySet() ) {
            typeName = e.getValue();
            if ( patternDefMap.containsKey( typeName ) ) {
                Map <String,String> patternDef = patternDefMap.get( typeName );
                for ( Entry <String,String> e1 : patternDef.entrySet() ) {
                    typeName = e1.getValue();
                    typeMap.put( e.getKey()+"."+e1.getKey(), resolveType( typeName ) );
                }
            } else {
                typeMap.put( e.getKey(), resolveType( typeName ) );
            }
        }
    }
    
    private Class <?> resolveType( String typeName ) throws ClassNotFoundException {
        int n = -1;
        Class <?> type = null;
        
        if ( typeName.endsWith("[]") ) {
            int i0 = typeName.indexOf( '[' );
            n = (typeName.length() - i0) / 2;
            typeName = typeName.substring( 0, i0 );
        }
        
        for ( String s : DEFAULT_CLASS_NAME_PREFIXES ) {
            try {
                type = Class.forName( s + typeName );
                break;
            } catch ( ClassNotFoundException ex ) {}
        }
        
        if ( type == null ) throw new ClassNotFoundException( "Unknown class \"" + typeName + "\"." );
        
        if ( n != -1 ) {
            type = Array.newInstance( type, n ).getClass();
        }
        
        return type;
    }
    
    private String[] parseOperatorClassNames( Reader classNamesReader ) throws IOException {
        KeyValuePairReader r = new KeyValuePairReader( classNamesReader );
        List <String> classNamesList = new ArrayList <String> ();
        String[] keyValuePair = null;
        while ( ( keyValuePair = r.readNextKeyValuePair(keyValuePair) ) != null ) {
            classNamesList.add( keyValuePair[0] );
        }
        classNamesReader.close();
        return classNamesList.toArray( new String[classNamesList.size()] );
    }
    
    private Map <String,Map<String,String>> parseOperatorConfigurations( Reader configReader ) throws IOException {
        KeyValuePairReader r = new KeyValuePairReader( configReader );
        String[] keyValuePair = new String[2];
        String key, value;

        Map <String,Map<String,String>> configs = new HashMap <String,Map<String,String>> ();
        while ( ( keyValuePair = r.readNextKeyValuePair(keyValuePair) ) != null ) {
            key = keyValuePair[0];
            configs.put( key, parseOperatorConfiguration(r) );
        }
     
        configReader.close();
        
        return configs;
    }
    
    private Map<String,String> parseOperatorConfiguration( KeyValuePairReader r ) throws IOException {
        String[] keyValuePair = new String[2];
        String key, value;

        Map <String,String> config = new HashMap <String,String> ();
        while ( ( keyValuePair = r.readNextKeyValuePair(keyValuePair) ) != null ) {
            key = keyValuePair[0];
            value = keyValuePair[1];
            if ( key.equalsIgnoreCase("end") ) {
                return config;
            } else {
                config.put( key, value );
            }
        }
     
        return null;
    }
    
    private Object parseValue( String text, String dataType ) {
        if ( dataType.equalsIgnoreCase("java.lang.Integer") ) {
            return ( text != null ) ? Integer.parseInt( text ) : 0;
        } else if ( dataType.equalsIgnoreCase("java.lang.Long") ) {
            return ( text != null ) ? Long.parseLong( text ) : 0;
        } else if ( dataType.equalsIgnoreCase("java.lang.Double") ) {
            return ( text != null ) ? Double.parseDouble( text ) : 0.0;
        } else if ( dataType.equalsIgnoreCase("java.lang.Boolean") ) {
            return ( text != null ) ? Boolean.parseBoolean( text ) : false;
        } else if ( dataType.equalsIgnoreCase("java.lang.String") ) {
            return ( text != null ) ? text : "";
        } else if ( dataType.equalsIgnoreCase("java.util.Date") ) {
            DateFormat df = DateFormat.getDateInstance( DateFormat.SHORT, Locale.UK );
            Date d = null;
            try {
                d = df.parse( text );
            } catch ( ParseException ex ) {
                ex.printStackTrace();
            }
            return d;
        } else if ( dataType.equalsIgnoreCase("[Ljava.lang.String") ) {
            if ( text == null ) return null;
            StringTokenizer tk = new StringTokenizer( text, "," );
            String[] array = new String[tk.countTokens()];
            for ( int i=0; i<array.length; i++ ) {
                array[i] = tk.nextToken().trim();
            }
            return array;
        } else if ( dataType.equalsIgnoreCase("[Ljava.lang.Integer") ) {
            if ( text == null ) return null;
            StringTokenizer tk = new StringTokenizer( text, "," );
            int[] array = new int[tk.countTokens()];
            for ( int i=0; i<array.length; i++ ) {
                array[i] = Integer.parseInt(tk.nextToken().trim());
            }
            return array;
        } else if ( dataType.equalsIgnoreCase("[Ljava.lang.Long") ) {
            if ( text == null ) return null;
            StringTokenizer tk = new StringTokenizer( text, "," );
            long[] array = new long[tk.countTokens()];
            for ( int i=0; i<array.length; i++ ) {
                array[i] = Long.parseLong(tk.nextToken().trim());
            }
            return array;
        } else if ( dataType.equalsIgnoreCase("[Ljava.lang.Double") ) {
            if ( text == null ) return null;
            StringTokenizer tk = new StringTokenizer( text, "," );
            double[] array = new double[tk.countTokens()];
            for ( int i=0; i<array.length; i++ ) {
                array[i] = Double.parseDouble(tk.nextToken().trim());
            }
            return array;
        }
        return null;
    }
    
}