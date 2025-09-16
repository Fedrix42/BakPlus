package core.journal.factories;

import core.journal.Journal;
import core.journal.JournalPath;
import core.journal.JournalType;
import core.strategy.Strategy;
import core.strategy.StrategyManager;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import logging.LoggerManager;
import utils.formatters.FilenameFormats;
import io.FilePatternFinder;
import io.serialization.SerializerFactory;

/**
 * Search and build of complete journals 
 * @author Fedrix
 */
public class CompleteJournalFactory {
    
    /**
     * Compute a complete journal for a specific strategy.
     * @param strategyID
     * @see make()
     * @return an empty or filled complete journal
     */
    public static Journal make(long strategyID) {
        try {
            return (Journal) SerializerFactory.getObjectSerializer().deserialize(JournalPath.getOfComplete(strategyID));
        } catch (ClassNotFoundException | IOException exception){
            try {
                // Trying rebuilding journal by making the union of partial journals
                List<Path> partialJournalPaths = FilePatternFinder.search(
                    JournalPath.getJournalsFolder(strategyID), FilenameFormats.JOURNAL_PARTIAL_TYPE_PATTERN, "stgid", Long.toString(strategyID));
                if(!partialJournalPaths.isEmpty()){
                    Journal complete = new Journal(strategyID, JournalType.COMPLETE);
                    for(Path partialJournalPath : partialJournalPaths){
                        complete = complete.union((Journal) SerializerFactory.getObjectSerializer().deserialize(partialJournalPath));
                    }
                    SerializerFactory.getObjectSerializer().serialize(JournalPath.getOfComplete(complete.strategyID), complete);
                    return complete;
                }
            } catch (IOException ex) {
                LoggerManager.exception().log(Level.WARNING, "Error accessing journal folder", ex);
            } catch (ClassNotFoundException ex) {
                LoggerManager.exception().log(Level.WARNING, "Error casting journal path to journal object", ex);
            }
        }
        return new Journal(strategyID, JournalType.COMPLETE);
    }
    
    /**
     * Search for the complete journal of the currently loaded strategy.
     * If the search fail then an empty complete journal is returned.
     * @return an empty or filled complete journal
     */
    public static Journal make(){
        return make(requireNonNull(StrategyManager.getCurrentlyLoaded()).getId());
    }
    
}
