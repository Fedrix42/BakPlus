package core.tasks.newbackup;

import core.Encryption;
import core.tasks.ProgressTask;
import core.tasks.MonitoredTaskDecorator;
import core.tasks.exception.InvalidEncryptionKeyException;
import core.tasks.progress.backup.BackupProgress;
import utils.formatters.FilenameFormats;
import utils.formatters.FilenameDateTimeFormatter;
import core.strategy.Strategy;
import core.strategy.StrategyManager;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Fedrix
 */
public class BackupInizializationTask extends MonitoredTaskDecorator {
    protected final BackupProgress progress;
    protected final Strategy cls;
    protected final Path backupFilename;
    protected final char[] key;
    
    public BackupInizializationTask(ProgressTask engine, char[] key) {
        super(engine);
        this.cls = StrategyManager.getCurrentlyLoaded();
        this.progress = new BackupProgress(engine.getProgress());
        this.key = key;
        this.backupFilename = Paths.get(cls.getDst(), FilenameFormats.BACKUP_FORMAT.formatted(cls.getId(), FilenameDateTimeFormatter.getFormattedNow()));
    }

    @Override
    public BackupProgress getProgress() {
        return progress;
    }
    
    @Override
    public void run() {
        super.run();
        if(!progress.isException()){ //If no exception was set by parent
            if(cls.geteMode() != Encryption.None && (key == null || key.length == 0)){
                progress.setException(new InvalidEncryptionKeyException("Encryption key is not set but the strategy encryption mode requires a key."));
            }
        }
    }
    
    
}
