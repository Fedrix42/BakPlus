package core.journal.manager;

import core.journal.Journal;
import core.journal.JournalPath;
import core.journal.JournalType;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.logging.Level;
import logging.LoggerManager;

/**
 * Extends JournalIOManager in order to provide the function to rollback to a specific date time
 * by deleting partial journals or every journal of a strategy.
 * @author fedrix
 */
public class JournalRollbackManager extends JournalIOManager {
    
    /**
     * Make a rollback of journals since the passed date time of a specific strategy.
     * Every partial journal which has a greater or equal creation date-time then the passed one will be deleted.
     * The complete journal is recalculated based on the new partial journals remaining.
     * @param since
     * @param strategyID
     * @throws java.io.IOException if there is an IO error deleting a journal file
     */
    public static void rollback(LocalDateTime since, long strategyID) throws IOException {
        try {
            // Deleting partial journals
            boolean deleteOccurred = false;
            for(Journal current : getAllJournals(strategyID)){
                if(current.type == JournalType.PARTIAL){
                    if(current.getCreatedTime().compareTo(since) >= 0){
                        deleteFromDisk(current);
                        deleteOccurred = true;
                    }
                }
            }
            if(deleteOccurred){
                // Deleting complete journal
                Files.deleteIfExists(JournalPath.getOfComplete(strategyID));
                // Computing new complete journal
                makePersistent(makeComplete());
            }
        } catch (ClassNotFoundException ex) {
            LoggerManager.exception().log(Level.SEVERE, "Error casting a journal", ex);
        }
    }
    
    /**
     * Delete all journals of a strategy.
     * @param strategyID 
     * @throws java.io.IOException 
     */
    public static void deleteAllJournals(long strategyID) throws IOException {
        deleteJournalsDirectory(strategyID);
        createJournalsDirectory(strategyID);
    }
    
    
}
