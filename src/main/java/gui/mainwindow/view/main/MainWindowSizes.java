package gui.mainwindow.view.main;

import java.awt.Dimension;

/**
 * Main window sizes that can be set by settings dialog
 * For definition, min sizes are first element of array
 * For convention, second element is default size
 * @author Fedrix
 */
public final class MainWindowSizes {
    private final static Integer[] widths =  {700, 900, 1100, 1300, 1500};
    private final static Integer[] heights = {467, 600, 733,  858, 990};
    
    public static Integer getMinWidth() {
        return widths[0];
    }

    public static Integer getMinHeight() {
        return heights[0];
    }
    
    public static Integer getMaxWidth() {
        return widths[widths.length - 1];
    }

    public static Integer getMaxHeight() {
        return heights[heights.length - 1];
    }
    
    public static Integer getDefaultWidth(){
        return widths[3];
    }
    
    public static Integer getDefaultHeight(){
        return heights[2];
    }
    
    public static Dimension getMinSize(){
        return new Dimension(getMinWidth(), getMinHeight());
    }
    
    public static Dimension getDefaultSize(){
        return new Dimension(getDefaultWidth(), getDefaultHeight());
    }
    
    
    
    /**
     * Check if a width is inside the min and max bounds
     * @param width
     * @return true if the width is valid, false otherwise
     */
    public static boolean validateWidth(int width){
        return width >= getMinWidth() && width <= getMaxWidth();
    }
    
    /**
     * Check if a height is inside the min and max bounds
     * @param height
     * @return true if the height is valid, false otherwise
     */
    public static boolean validateHeight(int height){
        return height >= getMinHeight() && height <= getMaxHeight();
    }
    
    
    /**
     * 
     * @return all possible widths for main window
     */
    public static Integer[] getWidths(){
        return widths;
    }
    
    /**
     * 
     * @return all possible heights for main window
     */
    public static Integer[] getHeights(){
        return heights;
    }
}
