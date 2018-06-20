package eu.theeconomystupid.gui.structure.game.scene;


import eu.theeconomystupid.engine.GameEngine;
import eu.theeconomystupid.gui.Resources;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;


final class ReportCard extends AbstractCenterCard {
    
    
    private static final String KEYWORD_ECONOMY_GENERAL = ".economy-general.";
    private static final String KEYWORD_ECONOMY_DETAILS = ".economy-details.";
    private static final String KEYWORD_APPROVAL_RATING = ".approvalRating.";
    private static final String KEYWORD_CRISIS = ".crisis.";
    private static final String KEYWORD_ELECTIONS = ".elections.";
    private static final String KEYWORD_DEMONSTRATIONS = ".demonstrations.";
    private static final String[] KEYWORDS = { 
        KEYWORD_ECONOMY_GENERAL, 
        KEYWORD_ECONOMY_DETAILS, 
        KEYWORD_APPROVAL_RATING, 
        KEYWORD_CRISIS, 
        KEYWORD_ELECTIONS, 
        KEYWORD_DEMONSTRATIONS 
    };
    
    private JEditorPane editorPane;
    
    
    ReportCard() {
        super("report");
        
        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {color:#000000; font-size:16pt; font-family:Serif;}");
        styleSheet.addRule("p {text-align:justify; padding-bottom:5px; padding-top:5px;}");
        
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.setStyleSheet( styleSheet );
        
        editorPane = new JEditorPane();
        editorPane.setEditable( false );
        editorPane.setOpaque( false );
        editorPane.setEditorKit( kit );
        editorPane.setDocument( kit.createDefaultDocument() );
        
        JScrollPane scrollPane = new JScrollPane( 
            editorPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER 
        );
        
        scrollPane.setOpaque( false );        
        scrollPane.setBorder( new EmptyBorder( 20, 60, 60, 60 ) );
        scrollPane.getViewport().setOpaque( false );
        
        JPanel p0 = new JPanel();
        p0.setOpaque( false );
        p0.setLayout( new BoxLayout( p0, BoxLayout.PAGE_AXIS ) );
        p0.add( scrollPane );
        
        setContents(p0);
        
    }

    protected void refreshData() {
        Resources res = Resources.getInstance();
        String[] messages = GameEngine.getInstance().getReportMessages();
        
        String prevKeyword = null;
        String keyword = null;
        StringBuffer htmlBuffer = new StringBuffer();
        htmlBuffer.append( "<html><body>" );
        
        for ( String m : messages ) {
            keyword = findKeyword(m);
            if ( keyword != prevKeyword ) {
                if ( prevKeyword != null ) {
                    htmlBuffer.append( "</p>" );
                }
                htmlBuffer.append( "<p>" );
                htmlBuffer.append( res.getText("messages", m) );
                htmlBuffer.append(" ");
                prevKeyword = keyword;
            } else {
                htmlBuffer.append( res.getText("messages", m) );
                htmlBuffer.append(" ");
            }
        }
        
        htmlBuffer.append( "</body></html>" );
        editorPane.setText( htmlBuffer.toString() );
    }
    
    private static final String findKeyword( String message ) {
        for ( String keyword : KEYWORDS ) {
            if ( message.contains(keyword) ) {
                return keyword;
            }
        }
        return null;
    }
    
}
