package eu.theeconomystupid.gui.components;


import java.awt.Component;
import java.awt.event.ActionListener;


public interface CardDisplay {
    public String getCurrentCardName();
    public Component getCurrentCard();
    public void showCard( String name );
    public void showCard( String name, ActionListener actionLister );
    public void showCardImmediately( String name );
    public Component getCard( String name );
    public boolean containsCard( String name );
}
