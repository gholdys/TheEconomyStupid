package eu.theeconomystupid.engine;


import eu.theeconomystupid.model.framework.core.Model;
import eu.theeconomystupid.model.framework.core.Operator;
import eu.theeconomystupid.model.framework.core.Registry;
import eu.theeconomystupid.model.framework.simple.SimpleModel;
import eu.theeconomystupid.model.framework.simple.SimpleModelFactory;
import eu.theeconomystupid.model.utils.CalibrationTool;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public final class GameEngine {

    
    private static final String REGISTRY_DEF_PATH = "config/registry.def";
    private static final String REGISTRY_CFG_PATH = "config/registry.cfg";
    private static final String OPERATORS_DEF_PATH = "config/operators.def";
    private static final String OPERATORS_CFG_PATH = "config/operators.cfg";
    private static final String CALIBRATION_CFG_PATH = "config/calibration.cfg";
    private static final String MESSAGES_DEF_PATH = "config/messages.def";
    private static final int NUM_WARMUP_STEPS = 11;
    private static final int MAX_GAME_LENGTH = 20;
    private static GameEngine instance;
    
    
    public static synchronized GameEngine configure( Key... recordedVariableKeys ) throws FileNotFoundException {
        instance = new GameEngine( recordedVariableKeys );
        return instance;
    }
    
    public static GameEngine getInstance() {
        return instance;
    }
    
    
    private Key[] recordedVariableKeys;
    private Game game;
    private List <GameEngineListener> listeners;
    private MessageGenerator messageGenerator;
    private String[] reportMessages;
    private String[] narrativeMessages;
    
    
    private GameEngine( Key... recordedVariableKeys ) throws FileNotFoundException {
        this.recordedVariableKeys = recordedVariableKeys;
        listeners = new ArrayList <GameEngineListener> ();
        messageGenerator = new MessageGenerator( createResourceReader( MESSAGES_DEF_PATH ) );
    }
    
    
    // ********************************************************
    // *************** ENGINE'S PUBLIC INTERFACE **************
    // ********************************************************
    public void addGameEngineListener( GameEngineListener listener ) {
        listeners.add( listener );
    }
    
    public void removeGameEngineListener( GameEngineListener listener ) {
        listeners.remove( listener );
    }
    
    public void clearGameEngineListeners() {
        listeners.clear();
    }
    
    public GameDesc[] getSavedGames() {
        return null;
    }
    
    public synchronized void createGame() {
        fireGameAboutToBeCreated();
        
        Model model = null;
        try {
            model = createModel();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        game = new Game( model, recordedVariableKeys, MAX_GAME_LENGTH );
        game.warmup( NUM_WARMUP_STEPS );
        
        generateMessages();
        fireGameCreated();
    }
    
    public synchronized void nextTurn() {
        if ( !isNextTurnLegal() ) throw new RuntimeException( "Next turn illegal!" );
        stepGame();
        generateMessages();
        fireTurnCompleted();
    }
    
    public synchronized void cleanup() {
        game.getModel().cleanup();
        reportMessages = null;
        game = null;
        narrativeMessages = null;
    }

    public boolean isElectionsTurn() {
        return game.isElectionsTurn();
    }
    
    public double getElectionsResult() {
        return game.getElectionsResult();
    }
    
    public boolean isElectionsWon() {
        return game.isElectionsWon();
    }

    public boolean gameOver() {
        return game.gameOver();
    }

    public GameResult getGameResult() {
        return game.getGameResult();
    }
    
    public String[] getReportMessages() {
        return reportMessages;
    }
    
    public int getNumReportMessages() {
        if ( reportMessages != null ) {
            return reportMessages.length;
        } else {
            return 0;
        }
    }
    
    public String[] getNarrativeMessages() {
        return narrativeMessages;
    }
    
    public int getNumNarrativeMessages() {
        if ( narrativeMessages != null ) {
            return narrativeMessages.length;
        } else {
            return 0;
        }
    }
    
    public Object getVariableValue( Key key ) {
        return game.getVariableValue( key );
    }
    
    public double getVariableAbsoluteDelta( Key key ) {
        return game.getVariableAbsoluteDelta( key );
    }
    
    public double getVariableRelativeDelta( Key key ) {
        return game.getVariableRelativeDelta( key );
    }
    
    public List <Number> getVariableHistory( Key key ) {
        return game.getVariableHistory( key );
    }
    
    public long getVariableHistoryStartingTime( Key key ) {
        return game.getVariableHistoryStartingTime(key);
    }
    
    public double getCurrentPolicyValue( PolicyKey key ) {
        return game.getCurrentPolicyValue( key );
    }
    
    public void setPlannedPolicyValue( PolicyKey key, double newValue ) {
        game.setPlannedPolicyValue( key, newValue );
    }

    public double getPlannedPolicyValue( PolicyKey key ) {
        return game.getPlannedPolicyValue( key );
    }
    
    public double getPlannedPolicyCosts( PolicyKey key ) {
        return game.getPlannedPolicyCosts( key );
    }
    
    public double getPlannedPolicyRevenues( PolicyKey key ) {
        return game.getPlannedPolicyRevenues( key );
    }
    
    public double getCurrentDeficit() {
        return game.getCurrentDeficit();
    }
    
    public double getPlannedDeficit() {
        return game.getPlannedDeficit();
    }
    
    public double getCurrentPolicyBalance( PolicyKey key ) {
        return game.getCurrentPolicyBalance( key );
    }
    
    public double getPlannedPolicyBalance( PolicyKey key ) {
        return game.getPlannedPolicyBalance( key );
    }
    
    public double calculateEstimatedPolicyBalance( PolicyKey key, double newValue ) {
        return game.calculateEstimatedPolicyBalance( key, newValue );
    }
    
    public double calculateEstimatedPolicyCosts( PolicyKey key, double newValue ) {
        return game.calculateEstimatedPolicyCosts( key, newValue );
    }
    
    public double calculateEstimatedPolicyRevenues( PolicyKey key, double newValue ) {
        return game.calculateEstimatedPolicyRevenues( key, newValue );
    }
    
    public boolean isNextTurnLegal() {
        double natlDebtToGDP = (Double) getFromRegistry( Keys.NATIONAL_DEBT_TO_GDP );        
        double maxNatlDebtToGDP = (Double) getFromRegistry( Keys.MAX_NATIONAL_DEBT_TO_GDP );
        if ( natlDebtToGDP >= maxNatlDebtToGDP && getPlannedDeficit() > 0.0 ) {
            return false;
        } else {
            return true;
        }
    }
    

    // ********************************************************************
    // ********************* PRIVATE UTILITY METHODS **********************
    // ********************************************************************    
    private void stepGame() {
        game.step();
    }
    
    private void generateMessages() {
        String[] messages = messageGenerator.generateMessages( game );
        List <String> reportMessagesList = new ArrayList <String> ();
        List <String> narrativeMessagesList = new ArrayList <String> ();
        for ( String message : messages ) {
            if ( message.startsWith( "report." ) ) {
                reportMessagesList.add( message );
            } else if ( message.startsWith( "narrative." ) ) {
                narrativeMessagesList.add( message );
            }
        }
        reportMessages = reportMessagesList.toArray( new String[reportMessagesList.size()] );
        narrativeMessages = narrativeMessagesList.toArray( new String[narrativeMessagesList.size()] );
    }
    

    // *********************************************************************
    // ******************* LISTENER NOTIFICATION METHODS *******************
    // *********************************************************************    
    private void fireTurnCompleted() {
        List <GameEngineListener> listenersCopy = new ArrayList<GameEngineListener> (listeners);
        for ( GameEngineListener l : listenersCopy ) {
            l.turnCompleted();
        }
    }
    
    private void fireGameCreated() {
        List <GameEngineListener> listenersCopy = new ArrayList<GameEngineListener> (listeners);
        for ( GameEngineListener l : listenersCopy ) {
            l.gameCreated();
        }
    }
    
    private void fireGameAboutToBeCreated() {
        List <GameEngineListener> listenersCopy = new ArrayList<GameEngineListener> (listeners);
        for ( GameEngineListener l : listenersCopy ) {
            l.gameAboutToBeCreated();
        }
    }
    
    
    // *********************************************************************
    // ********************** PRIVATE UTILITY METHODS **********************
    // *********************************************************************
    private Model createModel() throws FileNotFoundException {
        Reader registryDefReader = createResourceReader( REGISTRY_DEF_PATH );
        Reader registryCfgReader = createResourceReader( REGISTRY_CFG_PATH );
        Reader operatorsDefReader = createResourceReader( OPERATORS_DEF_PATH );
        Reader operatorsCfgReader = createResourceReader( OPERATORS_CFG_PATH );
        InputStream calibrationCfgStream = createResourceStream( CALIBRATION_CFG_PATH );
        
        SimpleModelFactory factory = new SimpleModelFactory();
        CalibrationTool ct = new CalibrationTool( calibrationCfgStream );
        Registry r = null;
        Operator[] operators = null;
        Model model = null;
        
        try {
            factory.setRegistryDefinitionReader( registryDefReader );
            factory.setRegistryValuesReader( registryCfgReader );
            factory.setOperatorClassNamesReader( operatorsDefReader );
            factory.setOperatorConfigReader( operatorsCfgReader );
            r = factory.createRegistry();
            factory.parseRegistryValues(r);
            ct.calibrate( r );
            operators = factory.createOperators(r);
            model = new SimpleModel( r, operators );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return model;
    }
    
    private Object getFromRegistry( Key key ) {
        return game.getModel().getRegistry().get( key.toString() );
    }
    
    private void putToRegistry( Key key, Object value ) {
        game.getModel().getRegistry().put( key.toString(), value );
    }
    
    private Reader createResourceReader( String resourceName ) {
        return new InputStreamReader( getClass().getClassLoader().getResourceAsStream(resourceName) );
    }
    
    private InputStream createResourceStream( String resourceName ) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }
    
}