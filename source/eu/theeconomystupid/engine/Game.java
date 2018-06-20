package eu.theeconomystupid.engine;


import eu.theeconomystupid.model.framework.core.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;


final class Game implements Serializable {
    

    private Model model;
    private Map <String,List<Number>> dataBuffers;
    private Map <PolicyKey,Double> newPolicyValueLookup;
    private Map <PolicyKey,Double> newPolicyCostsLookup;
    private Map <PolicyKey,Double> newPolicyRevenuesLookup;
    private long gameStartTime;
    private long gameFinishTime;
    private boolean electionsWon;
    private boolean electionsTurn;
    private int numMonthsPerTurn;
    private int numWarmupMonths;
    private GameResult gameResult;
    private Calendar calendar;
    private Random random;
    
    
    Game() {
        calendar = new GregorianCalendar();
        random = new Random();
    }
    
    Game( Model model, Key[] bufferKeys, int maxGameLength ) {
        this.calendar = new GregorianCalendar();
        this.model = model;
        this.dataBuffers = new HashMap <String,List<Number>> ();        
        for ( Key k : bufferKeys ) {
            dataBuffers.put( k.toString(), new ArrayList <Number> () );
        }        
        calendar.setTime( (Date) model.getRegistry().get( Keys.GAME_TIME.toString() ) );
        this.gameStartTime = calendar.getTimeInMillis();
        calendar.add( Calendar.YEAR, maxGameLength );
        this.gameFinishTime = calendar.getTimeInMillis();
        this.gameResult = null;
        this.electionsWon = false;
        this.electionsTurn = false;
        this.random = new Random();
        this.newPolicyValueLookup = new HashMap <PolicyKey,Double> ();
        this.newPolicyCostsLookup = new HashMap <PolicyKey,Double> ();
        this.newPolicyRevenuesLookup = new HashMap <PolicyKey,Double> ();
        
        numMonthsPerTurn = model.getRegistry().getInteger("game.numMonthsPerTurn");
    }

    void warmup( int numWarmupMonths ) {
        this.numWarmupMonths = numWarmupMonths;
        
        Date d0 = (Date) getFromRegistry( Keys.GAME_TIME );
        
        Calendar calendar = new GregorianCalendar();
        calendar.setTime( d0 );
        calendar.add( Calendar.MONTH, -numWarmupMonths );        
        putToRegistry( Keys.GAME_TIME, calendar.getTime() );
        putToRegistry( Keys.TURN_NUMBER, -numWarmupMonths/numMonthsPerTurn );
        int j=0, numRecords = numWarmupMonths/numMonthsPerTurn;
        for ( int i=0; i<numWarmupMonths; i++ ) {
            stepModel();
            if ( i%numMonthsPerTurn == 0 ) recordWarmupValues( numRecords, j++ );
        }
        setInitialPolicyParameters();
        putToRegistry( Keys.GAME_TIME, d0 );
    }
    
    void step() {
        submitPolicyChanges();
        for ( int i=0; i<numMonthsPerTurn; i++ ) {
            stepModel();            
        }
        recordValues();
        handleElections();
        checkGameOverConditions();
    }

    long getVariableHistoryStartingTime( Key key ) {
        if ( key.toString().contains("approvalRating") ) {
            calendar.setTimeInMillis( gameStartTime );
            calendar.add( Calendar.MONTH, -numMonthsPerTurn );
            return calendar.getTimeInMillis();
        } else {
            calendar.setTimeInMillis( gameStartTime );
            calendar.add( Calendar.MONTH, -numWarmupMonths+1 );
            return calendar.getTimeInMillis();
        }
    }
    
    Model getModel() {
        return model;
    }

    boolean isElectionsTurn() {
        return electionsTurn;
    }
    
    boolean isElectionsWon() {
        return electionsWon;
    }
    
    double getElectionsResult() {
        return (Double) getFromRegistry( Keys.ELECTIONS_RESULT );
    }
    
    boolean gameOver() {
        return gameResult != null;
    }

    GameResult getGameResult() {
        return gameResult;
    }
    
    Object getVariableValue( Key key ) {
        return getFromRegistry( key );
    }    
    
    Object getVariableValue( String key ) {
        return getFromRegistry( key );
    }
    
    double getVariableAbsoluteDelta( Key key ) {
        return getVariableAbsoluteDelta( key.toString() );
    }
    
    double getVariableAbsoluteDelta( String key ) {
        List <Number> buffer = dataBuffers.get( key );
        if ( buffer == null ) throw new IllegalArgumentException( "Variable with key \"" + key.toString() + "\" is not recorded!" );
        
        double value_0, value_1, delta_a;
        int index = buffer.size()-1;
        value_1 = buffer.get( index ).doubleValue();
        value_0 = buffer.get( index-1 ).doubleValue();
        delta_a = value_1-value_0;
        
        return delta_a;
    }
    
    double getVariableRelativeDelta( Key key ) {
        return getVariableRelativeDelta( key.toString() );
    }
    
    double getVariableRelativeDelta( String key ) {
        List <Number> buffer = dataBuffers.get( key );
        if ( buffer == null ) throw new IllegalArgumentException( "Variable with key \"" + key.toString() + "\" is not recorded!" );
        
        double value_0, value_1, delta_r;
        int index = buffer.size()-1;
        value_1 = buffer.get( index ).doubleValue();
        value_0 = buffer.get( index-1 ).doubleValue();
        delta_r = (value_1-value_0)/value_0;
        
        return delta_r;
    }
    
    List <Number> getVariableHistory( Key key ) {
        return dataBuffers.get( key.toString() );
    }
    
    void setPlannedPolicyValue( PolicyKey key, double newValue ) {
        newPolicyValueLookup.put( key, newValue );
        newPolicyCostsLookup.put( key, calculateEstimatedPolicyCosts( key, newValue ) );
        newPolicyRevenuesLookup.put( key, calculateEstimatedPolicyRevenues( key, newValue ) );
    }
    
    double getCurrentPolicyValue( PolicyKey key ) {
        return (Double) getVariableValue( key.VALUE );
    }
    
    double getPlannedPolicyValue( PolicyKey key ) {
        return newPolicyValueLookup.get( key );
    }
    
    double getPlannedPolicyCosts( PolicyKey key ) {
        return newPolicyCostsLookup.get( key );
    }
    
    double getPlannedPolicyRevenues( PolicyKey key ) {
        return newPolicyRevenuesLookup.get(key);
    }
    
    double getCurrentDeficit() {
        return (Double) getVariableValue( Keys.BUDGET_DEFICIT );
    }
    
    double getPlannedDeficit() {
        double totalBalance = 0.0;
        for ( PolicyKey k : Keys.getPolicyKeys() ) {
            totalBalance += getPlannedPolicyBalance(k);
        }
        return -totalBalance;
    }
    
    double getCurrentPolicyBalance( PolicyKey key ) {
        double revenues = (Double) getVariableValue( key.REVENUES );
        double costs = (Double) getVariableValue( key.COSTS );
        return revenues - costs;
    }
    
    double getPlannedPolicyBalance( PolicyKey key ) {
        double revenues = getPlannedPolicyRevenues( key );
        double costs = getPlannedPolicyCosts( key );
        return revenues - costs;
    }
    
    double calculateEstimatedPolicyBalance( PolicyKey key, double newValue ) {
        double revenues = calculateEstimatedPolicyRevenues( key, newValue );
        double costs = calculateEstimatedPolicyCosts( key, newValue );
        return revenues - costs;
    }
    
    double calculateEstimatedPolicyCosts( PolicyKey key, double newValue ) {
        if ( key == Keys.UNEMPLOYMENT_BENEFITS || key == Keys.RETIREMENT_BENEFITS || key == Keys.PUBLIC_SECTOR_FUNDING ) {
            double value0 = (Double) getFromRegistry( key.VALUE );
            double costs0 = (Double) getFromRegistry( key.COSTS );
            return newValue/value0*costs0;
        } else {
            return 0.0;
        }
    }
    
    double calculateEstimatedPolicyRevenues( PolicyKey key, double newValue ) {
        if ( key == Keys.INCOME_TAX || key == Keys.VAT ) {
            double value0 = (Double) getFromRegistry( key.VALUE );
            double revenues0 = (Double) getFromRegistry( key.REVENUES );
            return newValue/value0*revenues0;
        } else {
            return 0.0;
        }
    }
    
    
    // **********************************************
    // *********** PRIVATE UTILITY METHODS **********
    // **********************************************
    private void submitPolicyChanges() {
        for ( Entry <PolicyKey,Double> e : newPolicyValueLookup.entrySet() ) {
            putToRegistry( e.getKey().VALUE, e.getValue() );
        }
        newPolicyValueLookup.clear();
        newPolicyCostsLookup.clear();
        newPolicyRevenuesLookup.clear();
    }
    
    private void stepModel() {
        model.step();
    }
    
    private void recordWarmupValues( int numRecords, int currentRecord ) {
        Number value;
        for ( Map.Entry <String,List<Number>> e : dataBuffers.entrySet() ) {
            if ( e.getKey().contains("approvalRating") ) {
                if ( currentRecord >= numRecords-2 ) {
                    value = (Number) getFromRegistry( e.getKey() );
                    e.getValue().add( value );
                }
            } else {
                value = (Number) getFromRegistry( e.getKey() );
                e.getValue().add( value );
            }
        }
    }
    
    private void setInitialPolicyParameters() {
        for ( PolicyKey k : Keys.getPolicyKeys() ) {
            newPolicyValueLookup.put( k, (Double) getVariableValue( k.VALUE ) );
            newPolicyCostsLookup.put( k, (Double) getVariableValue( k.COSTS ) );
            newPolicyRevenuesLookup.put( k, (Double) getVariableValue( k.REVENUES ) );
        }
    }
    
    private void recordValues() {
        Number value;
        for ( Map.Entry <String,List<Number>> e : dataBuffers.entrySet() ) {
            value = (Number) getFromRegistry( e.getKey() );
            e.getValue().add( value );
        }
        
        for ( PolicyKey k : Keys.getPolicyKeys() ) {
            newPolicyValueLookup.put( k, (Double) getVariableValue( k.VALUE ) );
            newPolicyCostsLookup.put( k, (Double) getVariableValue( k.COSTS ) );
            newPolicyRevenuesLookup.put( k, (Double) getVariableValue( k.REVENUES ) );
        }
    }
    
    private void handleElections() {
        String[] events = (String[]) getFromRegistry( Keys.EVENTS );
        electionsTurn = false;
        if ( events != null ) {
            for ( String event : events ) {
                if ( event.equalsIgnoreCase( "elections_lost" ) ) {
                    electionsTurn = true;
                    electionsWon = false;
                    break;
                } else if ( event.equalsIgnoreCase( "elections_won" ) ) {
                    electionsTurn = true;
                    electionsWon = true;
                    break;
                }
            }
        }
    }
    
    private void checkGameOverConditions() {
        // Check if the PM wasn't removed from office:
        String[] events = (String[]) model.getRegistry().get("game.events");
        if ( events != null ) {
            for ( String event : events ) {
                if ( event.equals("prime_minister_steps_down") ) {
                    gameResult = GameResult.FAILURE_REMOVED_FROM_OFFICE;
                    return;
                }
            }
        }
        
        // Check elections condition:
        if ( electionsTurn ) {
            gameResult = (electionsWon) ? GameResult.SUCCESS : GameResult.FAILURE_LOST_ELECTIONS;
            return;
        }
        
        gameResult = null;
    }
    
    private Object getFromRegistry( Key k ) {
        return model.getRegistry().get( k.toString() );
    }
    
    private Object getFromRegistry( String k ) {
        return model.getRegistry().get(k);
    }
    
    private void putToRegistry( Key key, Object value ) {
        model.getRegistry().put( key.toString(), value );
    }
    
}