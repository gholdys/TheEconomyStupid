package eu.theeconomystupid.gui.structure.game.nextturn;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.engine.GameResult;
import eu.theeconomystupid.gui.GameGUI;
import eu.theeconomystupid.gui.Resources;
import eu.theeconomystupid.gui.components.NotifiableCard;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.NumberFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


final class ElectionsCard extends JPanel implements NotifiableCard, ActionListener {
    
    
    private static final Font LARGE_FONT = new Font( Font.SERIF, Font.BOLD, 40 );
    private static final Font MEDIUM_FONT = new Font( Font.SERIF, Font.BOLD, 20 );
    private static final Font SMALL_FONT = new Font( Font.SERIF, Font.PLAIN, 12 );    
    
    private static final double ANIMATION_PROGRESS_INCREMENT = 0.002;
    
    private JLabel electionsResultValueLabel;
    private JLabel pressAnyKeyLabel;
    private JLabel summaryLabel;
    private double animationProgress;
    private Timer timer;
    private double electionsResult;
    private NumberFormat electionsResultFormat;
    private boolean animatingElectionsResult;
    
    
    ElectionsCard() {
        setName( "elections" );
        
        Resources res = Resources.getInstance();
        
        JLabel electionsLabel = new JLabel( res.getText("gui", "game.nextturn.elections") );
        electionsLabel.setFont( LARGE_FONT );
        electionsLabel.setForeground( Color.WHITE );
        electionsLabel.setAlignmentX(0.5f);
        
        JLabel electionsResultLabel = new JLabel( res.getText("gui", "game.nextturn.electionsResult") );
        electionsResultLabel.setFont( MEDIUM_FONT );
        electionsResultLabel.setForeground( Color.WHITE );
        electionsResultLabel.setAlignmentX(0.5f);
        
        electionsResultValueLabel = new JLabel();
        electionsResultValueLabel.setFont( LARGE_FONT );
        electionsResultValueLabel.setForeground( Color.WHITE );
        electionsResultValueLabel.setAlignmentX(0.5f);
        
        electionsResultFormat = Resources.getInstance().getPercentFormat();
        
        summaryLabel = new JLabel();
        summaryLabel.setFont( LARGE_FONT );
        summaryLabel.setForeground( Color.WHITE );
        summaryLabel.setAlignmentX(0.5f);
        
        pressAnyKeyLabel = new JLabel( "("+res.getText("gui", "game.nextturn.pressAnyKeyToContinue")+")" );
        pressAnyKeyLabel.setFont( SMALL_FONT );
        pressAnyKeyLabel.setForeground( Color.WHITE );
        pressAnyKeyLabel.setAlignmentX(0.5f);        
        pressAnyKeyLabel.setVisible( false );
        
        setOpaque( true );
        setBackground( Color.BLACK );
        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        add( Box.createRigidArea( new Dimension(0, 120) ) );
        add( electionsLabel );
        add( Box.createRigidArea( new Dimension(0, 100) ) );
        add( electionsResultLabel );
        add( Box.createRigidArea( new Dimension(0, 20) ) );
        add( electionsResultValueLabel );
        add( Box.createRigidArea( new Dimension(0, 40) ) );
        add( summaryLabel );
        add( Box.createGlue() );
        add( pressAnyKeyLabel );
        
        timer = new Timer( 20, this );
        addComponentListener( new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                animatingElectionsResult = true;
                timer.start();
            }
        });
    }

    public void aboutToBeShown() {
        electionsResult = GameEngine.getInstance().getElectionsResult();
        electionsResultValueLabel.setText( electionsResultFormat.format(0.0) );
    }

    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == timer ) {
            if ( animatingElectionsResult ) {
                animationProgress += ANIMATION_PROGRESS_INCREMENT;
                double value = (1.0-Math.exp(-5.0*animationProgress))*electionsResult;            
                if ( animationProgress >= 1.0 ) {
                    animationProgress = 1.0;
                    electionsResultValueLabel.setText( electionsResultFormat.format( electionsResult ) );
                    timer.stop();
                    animatingElectionsResult = false;
                    timer.setInitialDelay( 1000 );
                    timer.setDelay( 500 );
                    timer.start();
                } else {
                    electionsResultValueLabel.setText( electionsResultFormat.format( value ) );
                }
            } else {
                if ( GameEngine.getInstance().getGameResult() == GameResult.SUCCESS ) {
                    if ( summaryLabel.getText().equals("") ) {
                        summaryLabel.setText( Resources.getInstance().getText("gui", "game.nextturn.electionsWon") );
                        pressAnyKeyLabel.setVisible( true );
                    } else {
                        summaryLabel.setVisible( !summaryLabel.isVisible() );
                    }
                } else {
                    summaryLabel.setText( Resources.getInstance().getText("gui", "game.nextturn.electionsLost") );
                    timer.stop();
                    pressAnyKeyLabel.setVisible( true );
                }                
            }
        } else if ( !animatingElectionsResult ) {
            if ( timer.isRunning() ) timer.stop();
            GameGUI.getInstance().showCard( "game.nextturn.gameOver" );
        }
    }
    
}
