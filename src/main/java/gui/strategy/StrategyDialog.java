package gui.strategy;

import java.awt.Dimension;
import javax.swing.JFrame;
import gui.strategy.creation.StrategyCreationModel;
import gui.CenteredDialog;
import javax.swing.JOptionPane;

/**
 * Super-class for common attributes of strategy dialogs
 * @author Fedrix
 * @param <modelType> StrategyDialogModel or subclasses
 */
public abstract class StrategyDialog<modelType extends StrategyCreationModel>  extends CenteredDialog {
    private final String helpDesc = """
                                    <html><body>
                                    <p>A strategy is a way to define what files and folders should be backed up, where<br>
                                    the backups should be saved and the way the backup must be made.</p>
                                    <br>
                                    <b>Compression and Encryption</b></br>
                                    <ul>
                                        <li>Encryption is used to apply a password to your backup: Zip Standard encryption is more compatible than AES256 but is less secure.</li>
                                        <li>None compression is faster than Deflate but uses more disk space.</li>
                                    </ul></p>

                                    <p><b>Max increments</b> refer to the maximum number of partial backups after which the next backup will be a complete one.<br> 
                                    This is necessary for performance and it's more reasonable and secure than to do infinite partial backups.<br>
                                    <em>Suggested value is between 5 and 10 depending on the size of the data to be backed up.</em></p>
                                    </body></html>
                                    """;
    private final int DIALOG_WIDTH = 850;
    private final int DIALOG_HEIGHT = 600;
    
    public StrategyDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        this.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
    }
    
    public abstract void setDestFolder(String dest);
    
    protected final void displayHelp(){
        JOptionPane.showMessageDialog(this, helpDesc, "How strategies works", JOptionPane.INFORMATION_MESSAGE);
    }
    
}
