package eu.theeconomystupid;


import eu.theeconomystupid.gui.ExitHandler;
import eu.theeconomystupid.gui.GameGUI;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.swing.JApplet;


public final class GameApplet extends JApplet implements ExitHandler {
    
    public GameApplet() {}

    @Override
    public void init() {
        String localeName = getParameter("locale");
        if ( localeName != null && !localeName.isEmpty() ) {
            String[] parts = localeName.split("_");
            if ( parts.length == 2 ) {
                String language = parts[0];
                String country = parts[1];
                GameGUI.configure( new Locale(language, country) );
            } else {
                GameGUI.configure( Locale.UK );
            }
        } else {
            GameGUI.configure( Locale.UK );
        }
        GameGUI.getInstance().setOwner(this, this);
    }
    
    @Override
    public void start() {
        GameGUI.getInstance().newGame();
    }

    @Override
    public void stop() {
        GameGUI.getInstance().setOwner(null, null);
    }

    public void handleExit() {
        String jsCommand = "self.close();";
        try {
            Method getWindowMethod = null;
            Method evalMethod = null;
            Object jsWindow = null;
            Class c = Class.forName("netscape.javascript.JSObject");
            for ( Method m : c.getMethods() ) {
                if (m.getName().equals("getWindow")) {
                    getWindowMethod = m;
                } else if (m.getName().equals("eval")) {
                    evalMethod = m;
                }
            }
            jsWindow = getWindowMethod.invoke( c, this );
            evalMethod.invoke( jsWindow, jsCommand );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
