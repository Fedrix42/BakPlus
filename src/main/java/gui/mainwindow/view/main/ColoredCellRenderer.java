package gui.mainwindow.view.main;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Cell rendered implementing color change for JList
 * @author Fedrix
 */
public class ColoredCellRenderer extends JLabel implements ListCellRenderer<Object> {
    private LinkedList<Object> highLighted;
    
    public ColoredCellRenderer() {
         setOpaque(true);
         highLighted = new LinkedList<>();
     }

    @Override
     public Component getListCellRendererComponent(JList<?> list,
                                                   Object value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {
         setText(value.toString());
         if(highLighted.contains(value)){
             setBackground(Color.RED);
         } else {
             setBackground(list.getBackground());
         }
         return this;
     }
     
     public void addToHighLighted(Object e){
         highLighted.add(e);
     }
     
     public void clearHighLighted(){
         highLighted.clear();
     }
}
