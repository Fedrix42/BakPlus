package core.tasks.progress.backup;

import core.tasks.progress.Progress;
import core.tasks.progress.ProgressDecorator;

/**
 * A class rappresenting the progress of a backup operation
 * @author Fedrix
 */
public class BackupProgress extends ProgressDecorator {
    protected String file;

    public BackupProgress(Progress progress){
        super(progress);
        file = "--";
    }
    
    public void setFile(String file) {
        this.file = file;
    }
    
    
    public String getFile() {
        return file;
    }
    
}
