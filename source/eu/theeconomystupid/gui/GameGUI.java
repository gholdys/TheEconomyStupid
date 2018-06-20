package eu.theeconomystupid.gui;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameEngineAdapter;
import eu.theeconomystupid.engine.Key;
import eu.theeconomystupid.engine.Keys;
import eu.theeconomystupid.gui.laf.GameTheme;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.StringTokenizer;
import eu.theeconomystupid.gui.components.AnimatedCardPanel;
import eu.theeconomystupid.gui.components.CardDisplay;
import eu.theeconomystupid.gui.components.NotifiableCard;
import eu.theeconomystupid.gui.effects.AlphaFadeEffect;
import eu.theeconomystupid.gui.structure.game.GameCard;
import eu.theeconomystupid.gui.structure.opening.OpeningCard;
import java.io.FileNotFoundException;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;


public final class GameGUI {
    
    
    private static final Key[] RECORDED_VARIABLE_KEYS = { 
        Keys.GDP, 
        Keys.GDP_GROWTH_RATE,
        Keys.UNEMPLOYMENT_RATE,
        Keys.INFLATION_RATE,
        Keys.OUTPUT, Keys.REAL_OUTPUT,
        Keys.CONSUMPTION, Keys.REAL_CONSUMPTION,
        Keys.INVESTMENT, Keys.REAL_INVESTMENT,
        Keys.EXPORTS, Keys.REAL_EXPORTS,
        Keys.IMPORTS, Keys.REAL_IMPORTS,
        Keys.GOVERNMENT_SPENDING, Keys.REAL_GOVERNMENT_SPENDING,
        Keys.TAX_REVENUES, Keys.REAL_TAX_REVENUES,
        
        Keys.NATIONAL_DEBT, 
        Keys.NATIONAL_DEBT_TO_GDP,
        Keys.BUDGET_DEFICIT,
        Keys.GLOBAL_ECONOMY_CONDITION,
        
        Keys.TOTAL_POPULATION,
        Keys.WORKFORCE,
        Keys.APPROVAL_RATING,
        
        Keys.INCOME_TAX.VALUE,
        Keys.VAT.VALUE,
        Keys.UNEMPLOYMENT_BENEFITS.VALUE,
        Keys.RETIREMENT_BENEFITS.VALUE,
        Keys.PUBLIC_SECTOR_FUNDING.VALUE,
        
        Keys.VAT.REVENUES,
        Keys.INCOME_TAX.REVENUES,
        Keys.UNEMPLOYMENT_BENEFITS.COSTS,
        Keys.RETIREMENT_BENEFITS.COSTS,
        Keys.PUBLIC_SECTOR_FUNDING.COSTS,
        
        Keys.PRIVATE_SECTOR_EMPLOYEES.NUM_PEOPLE,
        Keys.PRIVATE_SECTOR_EMPLOYEES.POPULATION_PART,
        Keys.PRIVATE_SECTOR_EMPLOYEES.AVERAGE_GROSS_INCOME, Keys.PRIVATE_SECTOR_EMPLOYEES.REAL_AVERAGE_GROSS_INCOME,        
        Keys.PRIVATE_SECTOR_EMPLOYEES.APPROVAL_RATING,
        
        Keys.PUBLIC_SECTOR_EMPLOYEES.NUM_PEOPLE,
        Keys.PUBLIC_SECTOR_EMPLOYEES.POPULATION_PART,
        Keys.PUBLIC_SECTOR_EMPLOYEES.AVERAGE_GROSS_INCOME, Keys.PUBLIC_SECTOR_EMPLOYEES.REAL_AVERAGE_GROSS_INCOME,        
        Keys.PUBLIC_SECTOR_EMPLOYEES.APPROVAL_RATING,
        
        Keys.UNEMPLOYED.NUM_PEOPLE,
        Keys.UNEMPLOYED.POPULATION_PART,
        Keys.UNEMPLOYED.AVERAGE_GROSS_INCOME, Keys.UNEMPLOYED.REAL_AVERAGE_GROSS_INCOME,        
        Keys.UNEMPLOYED.APPROVAL_RATING,
        
        Keys.RETIRED.NUM_PEOPLE,
        Keys.RETIRED.POPULATION_PART,
        Keys.RETIRED.AVERAGE_GROSS_INCOME, Keys.RETIRED.REAL_AVERAGE_GROSS_INCOME,        
        Keys.RETIRED.APPROVAL_RATING,
    };
    
    private static GameGUI instance;
    
    
    public static void configure( Locale locale) {
        if ( instance == null ) {
            instance = new GameGUI( locale );
        }
    }

    public static GameGUI getInstance() {
        return instance;
    }
    
    
    private RootPaneContainer owner;
    private ExitHandler exitHandler;
    private Dimension displaySize;
    private AnimatedCardPanel rootCard;
    
    
    // ********************************************************
    // ***************** PRIVATE CONSTRUCTOR ******************
    // ********************************************************
    private GameGUI( Locale locale ) {
        displaySize = new Dimension( 960, 680 );
        try {
            Resources.configure( locale );
            GameEngine.configure( RECORDED_VARIABLE_KEYS );
        } catch ( FileNotFoundException ex ) {
            // TODO: Show appropriate info dialog.
            ex.printStackTrace();
        }
    }
    
    
    // ********************************************************
    // ********************* PUBLIC API ***********************
    // ********************************************************    
    public void setOwner( RootPaneContainer owner, ExitHandler exitHandler ) {
        if ( this.owner != owner ) {
            resetEngineListeners();
            if ( this.owner != null ) {                
                this.owner.setContentPane(new JPanel());
            }
            if ( owner != null ) {
                if ( !UIManager.getLookAndFeel().getName().equalsIgnoreCase("game") ) {                    
                    MetalLookAndFeel.setCurrentTheme( new GameTheme() );
                    MetalLookAndFeel laf = new MetalLookAndFeel();
                    try {
                        UIManager.setLookAndFeel(laf);
                    } catch (UnsupportedLookAndFeelException ex) {
                        ex.printStackTrace();
                    }
                }                
                owner.setContentPane( createRootCard() );
            }
            this.owner = owner;
            this.exitHandler = exitHandler;
        }
    }
    
    public void newGame() {
        System.gc();
        GameGUI.getInstance().showCard( "opening.languageSelect" );
    }
    
    public Dimension getDisplaySize() {
        return displaySize;
    }

    public synchronized Component getCard( String path ) {
        String[] pathArray = tokenizePathString( path );
        return getCard( rootCard, pathArray, 0 );
    }

    public synchronized void showCard( String path ) {
        showCard( path, null );
    }
    
    public synchronized void showCard( String path, ActionListener actionListener ) {
        String[] pathArray = tokenizePathString( path );
        showCard( rootCard, pathArray, 0, null, null, actionListener );
    }
    
    public synchronized void showCardImmediately( String path ) {
        String[] pathArray = tokenizePathString( path );
        showCardImmediately( rootCard, pathArray, 0, null, null );
    }
    
    public void exit() {
        if ( exitHandler != null ) {
            exitHandler.handleExit();
        } else {
            System.exit(0);
        }
    }
    
    
    // *********************************************************
    // ****************** PRIVATE METHODS **********************
    // *********************************************************
    private Container createRootCard() {
        rootCard = new AnimatedCardPanel( new AlphaFadeEffect(false), 1500 );
        rootCard.setOpaque( true );
        rootCard.setBackground( Color.BLACK );
        rootCard.setFocusable( false );
        rootCard.add( new OpeningCard() );
        return rootCard;
    }
    
    private void setupSceneCard() {
        rootCard.add( new GameCard() );
    }
    
    private void resetEngineListeners() {
        GameEngine.getInstance().clearGameEngineListeners();
        GameEngine.getInstance().addGameEngineListener( new GameEngineAdapter() {
            @Override
            public void gameAboutToBeCreated() {
                setupSceneCard();
            }
        });
    }
    
    private void showCard( CardDisplay display, String[] pathArray, int index, CardDisplay cd0, String n0, ActionListener actionListener ) {
        if ( index >= pathArray.length ) return;
        String name = pathArray[index];
        Component c = display.getCard( name );
        if ( c != null ) {
            if ( !c.isVisible() ) {  
                if ( cd0 == null ) {
                    cd0 = display;
                    n0 = name;
                } else {
                    display.showCardImmediately( name );
                }
            }
            if ( index == pathArray.length-1 ) {
                if ( cd0 != null ) {
                    Component c0 = cd0.getCard(n0);
                    if ( c0 instanceof NotifiableCard ) ( (NotifiableCard) c0 ).aboutToBeShown();
                    if ( c0 instanceof CardDisplay ) notifyChildrenCards( (CardDisplay) c0 );
                    cd0.showCard( n0, new ActionListenerImpl(actionListener) );
                }
            } else if ( c instanceof CardDisplay ) {
                showCard( (CardDisplay) c, pathArray, index+1, cd0, n0, actionListener );
            }
        } else {
            StringBuilder builder = new StringBuilder();
            for ( int i=0; i<index; i++ ) {
                builder.append( pathArray[i] );
                builder.append('.');
            }
            builder.append( pathArray[index] );
            throw new RuntimeException( "Card named \"" + builder.toString() + "\" does not exist!" );
        }
    }
    
    private void showCardImmediately( CardDisplay display, String[] pathArray, int index, CardDisplay cd0, String n0 ) {
        if ( index >= pathArray.length ) return;
        String name = pathArray[index];
        Component c = display.getCard( name );
        if ( c != null ) {
            if ( !c.isVisible() ) {  
                if ( cd0 == null ) {
                    cd0 = display;
                    n0 = name;
                } else {
                    display.showCardImmediately( name );
                }
            }
            if ( index == pathArray.length-1 ) {
                if ( cd0 != null ) {
                    Component c0 = cd0.getCard(n0);
                    if ( c0 instanceof NotifiableCard ) ( (NotifiableCard) c0 ).aboutToBeShown();
                    if ( c0 instanceof CardDisplay ) notifyChildrenCards( (CardDisplay) c0 );
                    cd0.showCardImmediately( n0 );
                }
            } else if ( c instanceof CardDisplay ) {
                showCardImmediately( (CardDisplay) c, pathArray, index+1, cd0, n0 );
            }
        } else {
            StringBuilder buffer = new StringBuilder();
            for ( int i=0; i<index; i++ ) {
                buffer.append( pathArray[i] );
                buffer.append('.');
            }
            buffer.append( pathArray[index] );
            throw new RuntimeException( "Card named \"" + buffer.toString() + "\" does not exist!" );
        }
    }
    
    private void notifyChildrenCards( CardDisplay cd ) {        
        Component c = cd.getCurrentCard();
        if ( c instanceof NotifiableCard ) ( (NotifiableCard) c ).aboutToBeShown();
        if ( c instanceof CardDisplay ) notifyChildrenCards( (CardDisplay) c );
    }
    
    private Component getCard( CardDisplay panel, String[] pathArray, int index ) {
        if ( index >= pathArray.length ) return null;
        String name = pathArray[index];
        Component c = panel.getCard( name );
        if ( c != null ) {
            if ( index == pathArray.length-1 ) {
                return c;
            } else if ( c instanceof CardDisplay ) {
                return getCard( (CardDisplay) c, pathArray, index+1 );
            }
        } else {
            throw new RuntimeException( "Card named \"" + name + "\" does not exist!" );
        }
        return null;
    }
    
    private String[] tokenizePathString( String path ) {
        StringTokenizer tokenizer = new StringTokenizer( path, "." );
        String[] pathArray = new String[tokenizer.countTokens()];
        int i=0;
        while ( tokenizer.hasMoreTokens() ) {
            pathArray[i++] = tokenizer.nextToken();
        }
        return pathArray;
    }
    
    
    // **********************************************
    // ********** PRIVATE NESTED CLASSES ************
    // **********************************************
    private static final class ActionListenerImpl implements ActionListener {
        
        
        private ActionListener actionListener;
        
        
        private ActionListenerImpl( ActionListener actionListener ) {
            this.actionListener = actionListener;
        }
        
        public void actionPerformed( ActionEvent e ) {
            // TODO: Decide whether to use this notification.
            if ( actionListener != null ) actionListener.actionPerformed(e);
        }
        
    }
            
}