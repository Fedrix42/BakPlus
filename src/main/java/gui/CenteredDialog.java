package gui;

import static java.util.Objects.requireNonNull;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Superclass for every dialog which implements user input and interaction
 * @author Fedrix
 */
public abstract class CenteredDialog extends JDialog implements View {    
    public CenteredDialog(JFrame parent, boolean modal){
        super(requireNonNull(parent), modal);
        if(parent.getMousePosition() != null)
            this.setLocation(parent.getMousePosition());
    }
    
    public CenteredDialog(JDialog parent, boolean modal){
        super(requireNonNull(parent), modal);
        if(parent.getMousePosition() != null)
            this.setLocation(parent.getMousePosition());
    }
    
}
