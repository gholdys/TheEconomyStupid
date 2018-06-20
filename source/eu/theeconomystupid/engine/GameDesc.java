package eu.theeconomystupid.engine;


import java.util.Date;


public final class GameDesc {
    
    private String name;
    private Date saveDate;
    
    GameDesc( String name, Date saveDate ) {
        this.name = name;
        this.saveDate = saveDate;
    }

    public String getName() {
        return name;
    }

    public Date getSaveDate() {
        return saveDate;
    }
    
}
