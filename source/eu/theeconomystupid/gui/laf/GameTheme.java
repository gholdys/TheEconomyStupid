package eu.theeconomystupid.gui.laf;


import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;


public final class GameTheme extends DefaultMetalTheme {
    
    public String getName() { return "Game"; }
    

    private final ColorUIResource primary1 = new ColorUIResource(0, 0, 0);
    private final ColorUIResource primary2 = new ColorUIResource(180, 180, 180);
    private final ColorUIResource primary3 = new ColorUIResource(200, 200, 200);

    private final ColorUIResource secondary1 = new ColorUIResource(0, 0, 0);
    private final ColorUIResource secondary2 = new ColorUIResource(100, 100, 100);
    private final ColorUIResource secondary3 = new ColorUIResource(200, 200, 200);

    private final ColorUIResource black = new ColorUIResource(0, 0, 0);
    private final ColorUIResource white = new ColorUIResource(255, 255, 255);

    protected ColorUIResource getPrimary1() { return primary1; }
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }

    protected ColorUIResource getBlack() { return black; }
    protected ColorUIResource getWhite() { return white; }

}
