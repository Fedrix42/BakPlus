package gui.backups.incremental;

import core.Encryption;
import core.tasks.TaskManager;
import core.tasks.progress.backup.BackupProgress;
import core.strategy.StrategyManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import gui.Controller;
import gui.Model;
import gui.View;
import java.time.Duration;
import logging.LoggerManager;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;

/**
 * 
 * @author Fedrix
 */
public class IncrementalCreationController implements Controller {
    private BackupCreationProgressDialog view;
    private Thread processMonitor;
    private BackupProgress monitor;
    private final long updateTimeMs = 1000; // Milliseconds
    private char[] key; // Key for encryption if a backup is encrypted
    
    /**
     * Cancel the current backup operation
     */
    public void cancel(){
        int choice = JOptionPane.showConfirmDialog(view, "Are you sure you want to cancel current operation?", "Confirm choice",JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION){
            // Stop backup engine
            if(TaskManager.isRunning())
                TaskManager.interrupt();
            try {
                boolean hasTerminated = processMonitor.join(Duration.ofSeconds(3));
                if(!hasTerminated)
                    JOptionPane.showMessageDialog(view, "The cancelling of the operation is scheduled but it may take some time due to intensive work happening with large files.", "May take some time", JOptionPane.INFORMATION_MESSAGE);
                processMonitor.join();
            } catch (InterruptedException ex) {
                LoggerManager.exception().log(Level.SEVERE, "Controller thread was unexpectedly interrupted!", ex);
            }
            view.dispose();
        }
    }
    
    
    private void startLoop() {
        int time = 0;
        int progress;
        do {
            progress = (int) monitor.getProgress();
            try {
                Thread.sleep(updateTimeMs);
                if(monitor.isException())
                    view.appendError(monitor.getException().toString());
                view.setTimeValue(++time);
                view.setProgress(progress);
                view.setWorkingFile(monitor.getFile());
                view.setWorkingTask(monitor.getTask());
            } catch (InterruptedException ex) {
                TaskManager.interrupt();
            }
        } while(TaskManager.isRunning());
        view.setProgress(100);
    }
    
    /**
     * Starts a new thread for managing the backup and updating view informations
     * with current backup progress.
     * WARNIGN: calling this method starts out a new thread, do not call more than once
     */
    public void run() {
        assert(StrategyManager.isCurrentLoaded());
        assert(!TaskManager.isRunning());
        if(StrategyManager.getCurrentlyLoaded().geteMode() != Encryption.None){
            String input = JOptionPane.showInputDialog("The backup require a password since encryption mode is not null. Insert here.");
            if(input != null)
                key = input.toCharArray();
            else
                return;
        }
        monitor = TaskManager.newIncrementalBackup(key);
        processMonitor = new Thread(() -> {
            startLoop();
        });
        processMonitor.start();
        view.setVisible(true);
    }
    
    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toBackupCreationProgressDialog();
    }

    @Override
    public <modelT extends Model> void setModel(modelT m) {return;}

    @Override
    public IncrementalCreationController toIncrementalCreationController() {
        return this;
    }
    
    
}
