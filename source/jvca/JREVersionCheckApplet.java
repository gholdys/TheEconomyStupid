package jvca;


import java.applet.Applet;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;


public class JREVersionCheckApplet extends Applet {
    
    
    private String okPage;
    private String notOkPage;
    
    
    public JREVersionCheckApplet() {}

    public void init() {
        okPage = getParameter("okPage");
        notOkPage = getParameter("notOkPage");
        setBackground( new Color(0,0,0) );
    }
    
    public void start() {
        String jvmVersionString = System.getProperty("java.vm.version");
        int iDot = jvmVersionString.lastIndexOf(".");
        jvmVersionString = jvmVersionString.substring(0, iDot);
        double jvmVersion = Double.parseDouble( jvmVersionString );
        try {
            if ( jvmVersion >= 1.5 ) {
                getAppletContext().showDocument( new URL(getCodeBase()+okPage), "_self" );
            } else {
                getAppletContext().showDocument( new URL(getCodeBase()+notOkPage), "_self" );
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
}
