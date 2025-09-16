package gui.backups.integrity;

import core.tasks.TaskManager;
import core.journal.Journal;
import core.journal.JournalType;
import core.tasks.progress.integrity.IntegrityEntry;
import core.tasks.progress.integrity.IntegrityProgress;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import gui.Controller;
import gui.Model;
import gui.View;
import gui.backups.integrity.view.IntegrityVerificationProgressDialog;
import logging.LoggerManager;
import utils.Pair;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;

/**
 * Controller for IntegrityCheckProgressDialog and model
 * @author Fedrix
 */
public class IntegrityVerificationProgressController implements Controller {
    private IntegrityVerificationProgressModel model;
    private IntegrityVerificationProgressDialog view;
    private final long updateTimeMs = 1000; // Milliseconds
    private Thread processMonitor;
    private IntegrityProgress progress;
    
    public void cancel(){
        int choice = JOptionPane.showConfirmDialog(view, "Are you sure you want to cancel current operation?", "Confirm choice", JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION){
            // Stop backup engine
            if(TaskManager.isRunning())
                TaskManager.interrupt();
            try {
                processMonitor.join();
            } catch (InterruptedException ex) {
                LoggerManager.exception().log(Level.SEVERE, "Controller thread was unexpectedly interrupted!", ex);
            }
            view.dispose();
        }
    }
    
    private void startLoop(){
        do {
            try {
                Thread.sleep(updateTimeMs);
                if(progress.isException())
                    NotificationManager.notify(view, new ExceptionNotification("Exception occurred during operation.", progress.getException(),Level.SEVERE));
                view.setProgress((int)progress.getProgress());
                view.setStatus(progress.getTask());
                while(!progress.isEmpty()){
                    IntegrityEntry entry = progress.remove();
                    model.addRow(entry.filename(), entry.sha256(), entry.verified(), entry.note());
                }
            } catch (InterruptedException ex){
                TaskManager.interrupt();
            }
            
        } while (TaskManager.isRunning());
        view.setCompletePass(progress.completePass);
    }
    
    /**
     * Starts a new thread for checking integrity of backups
     * WARNIGN: calling this method starts out a new thread, do not call more than once
     * @param journal
     * @param backups
     */
    public void run(Journal journal, List<Pair<Path, char[]>> backups){
        assert(!TaskManager.isRunning());
        view.setJournalInfo(journal.strategyID, journal.type, journal.getCreatedTime());
        progress = TaskManager.checkIntegrity(journal, backups);
        processMonitor = new Thread(() -> {
            startLoop();
        });
        processMonitor.start();
        view.setVisible(true);
    }
    
    
    @Override
    public <viewT extends View> void setView(viewT v) {
        assert(v instanceof IntegrityVerificationProgressDialog);
        this.view = v.toIntegrityVerificationProgressDialog();
    }

    @Override
    public <modelT extends Model> void setModel(modelT m) {
        assert(m instanceof IntegrityVerificationProgressModel);
        this.model = m.toIntegrityVerificationProgressModel();
    }

    @Override
    public IntegrityVerificationProgressController toIntegrityVerificationProgressController() {
        return this;
    }
    
    
    
}
