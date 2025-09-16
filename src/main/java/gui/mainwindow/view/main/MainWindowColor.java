package gui.mainwindow.view.main;

import java.awt.Color;

/**
 * Colors used in main window
 * @author Fedrix
 */
public enum MainWindowColor {
    CoolYellow((short)255, (short)204, (short)51),
    CoolBlue((short)143, (short)208, (short)237);
    
    private final Color c;
    MainWindowColor(short red, short green, short blue){
        c = new Color(red, green, blue);
    }
    
    public Color get(){
        return c;
    }
}
