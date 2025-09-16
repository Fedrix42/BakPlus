package gui.journal.rollback;

import core.journal.Journal;
import gui.Controller;
import gui.Model;
import gui.View;
import gui.mcvutils.InitMCV;
import gui.journal.JournalDataDialog;
import gui.journal.JournalDataModel;
import gui.journal.rollback.model.JournalRollbackModel;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import logging.LoggerManager;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;
import logging.notifications.SimpleNotification;

/**
 * Controller for journal rollback feature
 * @author fedrix
 */
public class JournalRollbackController implements Controller {
    private JournalRollbackModel model;
    private JournalRollbackDialog view;
    
    public void rollback(){
        if(view.getSelectedIndex() != -1){
            int ret = JOptionPane.showConfirmDialog(
                    view, "This will delete all logs of previous backups with a date equal or greater then the selected.", 
                    "Are you sure?",JOptionPane.YES_NO_OPTION);
            if(ret == JOptionPane.YES_OPTION){
                try {
                    model.rollback();
                    model.updateViewAndModel();
                    NotificationManager.notify(view, new SimpleNotification("Done!", Level.INFO));
                } catch (IOException ex) {
                    var not = new ExceptionNotification("Coudldn't make a complete rollback", ex, Level.WARNING);
                    LoggerManager.exceptionNL().log(not);
                    NotificationManager.notify(view, not);
                }
            }
        }else 
            NotificationManager.notify(view, new SimpleNotification("Select an item first!", Level.INFO));
    }
    
    
    public void seeJournalData(){
        if(view.getSelectedIndex() != -1){
            Journal selected = model.getSelectedJournal();
            JournalDataDialog dataDialog = InitMCV.setReferencesAndUpdate(null, 
                    new JournalDataDialog(view, false, selected), 
                    new JournalDataModel(selected));
            dataDialog.setVisible(true);
        }else 
            NotificationManager.notify(view, new SimpleNotification("Select an item first!", Level.INFO));
        
    }
    
    
    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toJournalRollbackDialog();
    }

    @Override
    public <modelT extends Model> void setModel(modelT m) {
        this.model = m.toJournalRollbackModel();
    }

    @Override
    public JournalRollbackController toJournalRollbackController() {
        return this;
    }
    
}
