package eu.theeconomystupid.model.misc;


import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;


public final class GameMiscOperator extends Operator {
    
    
    private String gameTimeKey;
    private String numTurnsPerTermKey;
    private String numMonthsPerTurnKey;
    private String turnNumberKey;
    private String numTurnsToNextElectionsKey;
    private String gameEventsKey;
    private String electionsResultKey;
    private String electionsMaxErrorKey;
    private String approvalRatingKey;
    
    private Random random;
    private Calendar calendar;
    private int numTurnsPerTerm;
    private int numMonthsPerTurn;
    private int gameStartTurn;
    private List <String> eventsList;
    private int numWarnings;
    private int maxNumWarnings;
    private int monthInTurn;
    
    
    public GameMiscOperator() {}

    public void configure( Registry r, Map <String,String> config ) {
        gameTimeKey = config.get( "gameTimeKey" );
        numTurnsPerTermKey = config.get( "numTurnsPerTermKey" );
        numMonthsPerTurnKey = config.get( "numMonthsPerTurnKey" );
        turnNumberKey = config.get( "turnNumberKey" );
        numTurnsToNextElectionsKey = config.get( "numTurnsToNextElectionsKey" );
        gameEventsKey = config.get( "gameEventsKey" );
        electionsResultKey = config.get( "electionsResultKey" );
        electionsMaxErrorKey = config.get( "electionsMaxErrorKey" );
        approvalRatingKey = config.get( "approvalRatingKey" );
        
        maxNumWarnings = Integer.parseInt(config.get( "maxNumWarnings" ));
        
        gameStartTurn = r.getInteger( turnNumberKey ); // should be zero
        numTurnsPerTerm = r.getInteger( numTurnsPerTermKey );
        numMonthsPerTurn = r.getInteger( numMonthsPerTurnKey );
        
        long seed = Long.parseLong( config.get( "seed" ) );
        if ( seed == 0 ) seed = System.currentTimeMillis();
        random = new Random(seed);
        
        calendar = new GregorianCalendar();
        eventsList = new ArrayList <String> ();
        numWarnings = 0;
        monthInTurn = numMonthsPerTurn-1;
    }
    
    public void execute( Registry r ) {
        clearEventsList();
        advanceTurnNumber(r);
        advanceGameTime(r);
        handleElections(r);
        handlePartyPolitics(r);
        writeEventsArray(r);
    }
    
    private void clearEventsList() {
        eventsList.clear();
    }
    
    private void advanceTurnNumber( Registry r ) {
        monthInTurn = (monthInTurn+1)%numMonthsPerTurn;
        if ( monthInTurn == 0 ) {
            int n = r.getInteger( turnNumberKey );
            r.put( turnNumberKey, ++n );
        }
    }
    
    private void advanceGameTime( Registry r ) {
        calendar.setTime( (Date) r.get( gameTimeKey ) );
        calendar.add( Calendar.MONTH, 1 );
        
        int dayOfMonth = (int) Math.max( 1.0, Math.round(Math.random()*6.0) );
        calendar.set( Calendar.DAY_OF_MONTH, dayOfMonth );
        
        int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
        if ( dayOfWeek == Calendar.SATURDAY )  {
            calendar.add( Calendar.DAY_OF_MONTH, 2 );
        } else if ( dayOfWeek == Calendar.SUNDAY )  {
            calendar.add( Calendar.DAY_OF_MONTH, 1 );
        }
        
        r.put( gameTimeKey, calendar.getTime() );
    }        
    
    private void handleElections( Registry r ) {
        if ( monthInTurn != 0 ) return;  // Handle elections only in the last month of a turn
        int turn = r.getInteger( turnNumberKey );        
        int numTurnsToNextElections = numTurnsPerTerm - (turn-gameStartTurn-1) % numTurnsPerTerm;
        if ( numTurnsToNextElections < 0 ) numTurnsToNextElections = numTurnsPerTerm;
        r.put( numTurnsToNextElectionsKey, numTurnsToNextElections );

        boolean electionsTurn = numTurnsToNextElections == numTurnsPerTerm;
        
        if ( electionsTurn ) {
            double ar = r.getDouble( approvalRatingKey );
            double electionsResult = ar + r.getDouble(electionsMaxErrorKey)*random.nextGaussian();
            if ( electionsResult >= 0.5 ) {                
                eventsList.add( "elections_won" );
            } else {
                eventsList.add( "elections_lost" );
            }
            r.put( electionsResultKey, electionsResult );
            numWarnings = 0;
        } else {
            r.put( electionsResultKey, -1.0 );
        }        
        
    }
    
    private void handlePartyPolitics( Registry r ) {
        if ( r.getInteger( turnNumberKey ) < 0 ) return;  // Warmup fix
        if ( monthInTurn != 0 ) return;  // Handle party politics only in the last month of a turn
        int numTurnsToNextElections = r.getInteger( numTurnsToNextElectionsKey );
        if ( numTurnsToNextElections > 2 ) {
            double ar = r.getDouble( approvalRatingKey );
            double ar_min = 0.4;
            if ( ar < ar_min ) {
                double x = (ar_min - ar)/ar_min;
                double p = 0.5+1.0*x;
                if ( random.nextDouble() <= p ) {
                    if ( numWarnings > maxNumWarnings ) {
                        eventsList.add( "prime_minister_steps_down" );
                        numWarnings = 0;
                    } else {
                        numWarnings++;
                        eventsList.add( "party_warning_" + numWarnings );
                    }
                }
            }
        }
    }
    
    private void writeEventsArray( Registry r ) {
        r.put( gameEventsKey, eventsList.toArray( new String[eventsList.size()] ) );
    }
    
}