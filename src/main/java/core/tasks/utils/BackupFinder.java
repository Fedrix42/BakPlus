package core.tasks.utils;

import utils.formatters.FilenameFormats;
import core.strategy.Strategy;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import io.FilePatternFinder;

/**
 * Search for backups created by this program
 * @author Fedrix
 */
public class BackupFinder {
    
    /**
     * Search for backups of a specific strategy by using the strategy folder
     * @param stg
     * @return 
     * @throws IOException if there is a reading problem accessing folder entries or entries metadata
     * @throws IllegalArgumentException
     */
    public static List<Path> searchForBackups(Strategy stg) throws IOException {
        if(stg == null)
            throw new IllegalArgumentException("Strategy must not be null.");
        return FilePatternFinder.search(stg.getDestinationFolder(), FilenameFormats.BACKUP_PATTERN, "stgid", Long.toString(stg.getId()));
    }
    
    /**
     * Search for backups in a specific folder
     * @param folder
     * @return
     * @throws IOException if there is a reading problem accessing folder entries or entries metadata
     * @throws IllegalArgumentException
     */
    public static List<Path> searchForBackups(Path folder) throws IOException {
        return FilePatternFinder.search(folder, FilenameFormats.BACKUP_PATTERN);
    }
}
