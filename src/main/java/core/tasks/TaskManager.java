package core.tasks;

import core.tasks.integrity.IntegrityVerificationTask;
import core.journal.Journal;
import core.tasks.progress.backup.BackupProgress;
import core.tasks.progress.integrity.IntegrityProgress;
import core.tasks.utils.BackupFinder;
import core.strategy.StrategyManager;
import core.tasks.newbackup.IncrementalZipCreationTask;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static java.util.Objects.requireNonNull;
import utils.Pair;

/**
 * Engine to manage operations on backups and on files
 * @author Fedrix
 */
public class TaskManager {
    private static Thread taskThread;
    private static MonitoredTask task;
    
    private static void createAndStartThread(){
        taskThread = new Thread(task);
        taskThread.start();
    }
    
    /**
     * Create a new incremental backup based on the currently loaded strategy (CLS) and the journal on file system.If the CLS is null then nothing is done.WARNING: This method creates a new thread.
     * @param key encryption key if the strategy of the backup has encryption
     * @return progress object to monitor progress
     */
    public static BackupProgress newIncrementalBackup(char[] key){
        if(TaskManager.isRunning())
            TaskManager.interrupt();
        task = new IncrementalZipCreationTask(new ProgressTask(), key);
        createAndStartThread();
        return (BackupProgress) task.getProgress();
    }
    
    
    /**
     * Search for backups made by the currently loaded strategy in the destination folder.
     * @return list of path rappresenting backups path(which will be zip files)
     * @throws java.io.IOException if read access to destination folder is not permitted
     */
    public static List<Path> searchForBackups() throws IOException {
        return BackupFinder.searchForBackups(requireNonNull(StrategyManager.getCurrentlyLoaded()));
    }
    
    
    /**
     * Check backups against a journal to match checksum and verify data integrity
     * @param journal     
     * @param backups     
     * @return      
     */
    public static IntegrityProgress checkIntegrity(Journal journal, List<Pair<Path,char[]>> backups){
        if(TaskManager.isRunning())
            TaskManager.interrupt();
        task = new IntegrityVerificationTask(new ProgressTask(), journal, backups);
        createAndStartThread();
        return (IntegrityProgress) task.getProgress();
    }
    
    /**
     * Stop the operation which is currently being executed
     */
    public static void interrupt(){
        taskThread.interrupt();
    }
    
    /**
     * @return true the operation is running
     */
    public static boolean isRunning(){
        if(taskThread != null)
            return taskThread.isAlive();
        return false;
    }
    
}
