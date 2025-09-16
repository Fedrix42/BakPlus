package core.tasks.newbackup;

import core.tasks.ProgressTask;
import core.journal.Journal;
import core.journal.manager.JournalManager;
import core.tasks.newbackup.utils.CombinedPath;
import java.io.IOException;
import java.util.List;

/**
 * Initialize journals used to make an incremental backup
 * @see journal package
 * @author Fedrix
 */
public class IncrementalJournalInizializationTask extends BackupInizializationTask {
    protected Journal found = null, difference = null;
    protected List<CombinedPath> toBackupFiles = null;
    
    public IncrementalJournalInizializationTask(ProgressTask engine, char[] key) {
        super(engine, key);
    }


    @Override
    public void run() {
        super.run();
        if(!progress.isException()){ //If no exception was set by parent
            progress.setTask("Building journals, this may take some time...");
            try {
                found = JournalManager.makeComplete();
                difference = JournalManager.makePartial(cls.getFiltersCopy());
                toBackupFiles = JournalManager.partialAsList();
            } catch (IOException ex) {
                progress.setException(ex);
                progress.setTask("Backup operation inizialization ended with errors while building journals.");
            }
        }
    }
    
}
