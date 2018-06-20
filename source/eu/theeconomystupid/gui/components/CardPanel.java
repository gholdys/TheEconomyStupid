package eu.theeconomystupid.gui.components;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;


public class CardPanel extends JPanel implements CardDisplay {
    
    
    protected ActionListener actionLister;
    private Map <String,Component> cardLookup;
    private String currentCardName;
    
    
    public CardPanel() {
        setLayout( new Layout() );
        cardLookup = new HashMap <String,Component> ();
    }

    public Component add( Component comp ) {
        super.add( comp );
        cardLookup.put( comp.getName(), comp );
        if ( getComponentCount() == 1 ) {
            comp.setVisible( true );
            currentCardName = comp.getName();
        } else {
            comp.setVisible( false );
        }
        return comp;
    }
    
    public void add( Component comp, Object constraints ) {
        add( comp );
    }

    public Component add( String name, Component comp ) {
        return add( comp );
    }
    
    
    // ***************************************************
    // ****** CARD DISPLAY INTERFACE IMPLEMENTATION ******
    // ***************************************************    
    public void showCard( String name ) {        
        showCard( name, null );
    }
    
    public void showCard( String name, ActionListener actionLister ) {
        if ( name != null && !containsCard( name ) ) return;
        this.actionLister = actionLister;
        setVisibleComponent( name );
        notifyActionListener();
    }

    public void showCardImmediately( String name ) {
        showCard( name, null );
    }
    
    public String getCurrentCardName() {
        return currentCardName;
    }

    public Component getCurrentCard() {
        if ( currentCardName != null ) {
            return cardLookup.get( currentCardName );
        } else {
            return null;
        }
    }

    public Component getCard(String name) {
        return cardLookup.get(name);
    }

    public boolean containsCard(String name) {
        return cardLookup.containsKey(name);
    }

    
    //****************************************************
    //**************** PROTECTED METHODS  ****************
    //****************************************************    
    protected void notifyActionListener() {
        if ( actionLister != null ) {
            actionLister.actionPerformed( new ActionEvent(this, 0, null) );
            actionLister = null;
        }
    }
    
    protected void setVisibleComponent( String name ) {
        synchronized( getTreeLock() ) {
            currentCardName = name;
            Component[] components = getComponents();
            for ( Component c : components ) {
                c.setVisible( false );
            }
            if ( name != null ) cardLookup.get(name).setVisible( true );
            validate();
        }
    }
    
    protected Component getVisibleComponent() {
        synchronized( getTreeLock() ) {
            Component[] components = getComponents();
            for ( Component c : components ) {
                if ( c.isVisible() ) return c;
            }
        }
        return null;
    }

            
    //*****************************************************
    //*************** PRIVATE LAYOUT CLASS  ***************
    //*****************************************************    
    private final class Layout implements LayoutManager2 {

        private Layout() {}

        public void addLayoutComponent(Component comp, Object constraints) {}
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        public void invalidateLayout(Container target) {}

        public Dimension preferredLayoutSize( Container parent ) {
            synchronized (getTreeLock()) {
                Insets insets = getInsets();
                int ncomponents = getComponentCount();
                int w = 0;
                int h = 0;

                for (int i = 0 ; i < ncomponents ; i++) {
                    Component comp = getComponent(i);
                    Dimension d = comp.getPreferredSize();
                    if (d.width > w) {
                        w = d.width;
                    }
                    if (d.height > h) {
                        h = d.height;
                    }
                }
                return new Dimension(
                    insets.left + insets.right + w,
                    insets.top + insets.bottom + h
                );
            }
        }

        public Dimension minimumLayoutSize(Container parent) {
            synchronized (getTreeLock()) {
                Insets insets = getInsets();
                int ncomponents = getComponentCount();
                int w = 0;
                int h = 0;

                for (int i = 0 ; i < ncomponents ; i++) {
                    Component comp = getComponent(i);
                    Dimension d = comp.getMinimumSize();
                    if (d.width > w) {
                        w = d.width;
                    }
                    if (d.height > h) {
                        h = d.height;
                    }
                }
                return new Dimension(
                    insets.left + insets.right + w,
                    insets.top + insets.bottom + h
                );
            }
        }

        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
        }

        public float getLayoutAlignmentX(Container parent) {
            return 0.5f;
        }

        public float getLayoutAlignmentY(Container parent) {
            return 0.5f;
        }

        public void layoutContainer(Container parent) {
            synchronized ( getTreeLock() ) {
                Insets insets = getInsets();
                int ncomponents = getComponentCount();
                Component comp = null;

                for (int i = 0 ; i < ncomponents ; i++) {
                    comp = getComponent(i);
                    comp.setBounds(
                        insets.left, 
                        insets.top,
                        getWidth() - (insets.left + insets.right),
                        getHeight() - (insets.top + insets.bottom)
                    );
                }
            }
        }

    }
    
}
