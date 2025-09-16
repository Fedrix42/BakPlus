package core.journal;

import utils.formatters.FilenameDateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import settings.SettingsManager;
import utils.formatters.FilenameFormats;


public class JournalPath {
    public static final String EXTENSION = ".journal";
    
    /**
     * Get the journals folder of a strategy
     * @param strategyID
     * @return 
     */
    public static Path getJournalsFolder(long strategyID){
        Path result = SettingsManager.getCurrent().getStrategiesFolderPath().resolve(FilenameFormats.JOURNAL_FOLDER_NAME.formatted((strategyID))); 
        return result;
    }
    
    /**
     * Get the path of a journal in file system
     * @param journal
     * @return 
     */
    public static Path get(Journal journal){
        return switch(journal.type){
            case COMPLETE -> getOfComplete(journal.strategyID);
            case PARTIAL -> getOfPartial(journal.strategyID, journal.getCreatedTime()); 
            default -> getOfComplete(journal.strategyID);
        };
    }
    
    /**
     * Get the path of a complete journal in file system
     * @param strategyID
     * @return 
     */
    public static Path getOfComplete(long strategyID){
        Path result = getJournalsFolder(strategyID);
        Path filename = Paths.get(FilenameFormats.JOURNAL_COMPLETE_TYPE.formatted(strategyID));
        result = result.resolve(filename);
        return result;
    }
    
    /**
     * Get the path of a partial journal in file system
     * @param strategyID
     * @param creationTime
     * @return 
     */
    public static Path getOfPartial(long strategyID, LocalDateTime creationTime){
        Path result = getJournalsFolder(strategyID);
        Path filename = Paths.get(FilenameFormats.JOURNAL_PARTIAL_TYPE.formatted(strategyID, FilenameDateTimeFormatter.getFormatted(creationTime)));
        result = result.resolve(filename);
        return result;
    }
    
    
}
