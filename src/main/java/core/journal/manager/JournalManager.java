package core.journal.manager;

import core.journal.Journal;
import core.journal.factories.PartialJournalFactory;
import core.journal.factories.CompleteJournalFactory;
import core.tasks.newbackup.utils.CombinedPath;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Manager of journals (which are logs of backups) for the Currently Loaded Strategy.
 * Provide features used by the component which will perform the backup
 * @author fedrix
 */
public class JournalManager {
    private static Journal lastComplete = null;
    private static Journal lastPartial = null;
    
    
    /**
     * Create a complete journal for the currently loaded strategy.
     * If there is a serialized complete journal on disk then it is loaded.
     * Otherwise, if there are partial journal of the strategy, complete journal is re-computed
     * based on partial journals.
     * If there is no complete nor partial journal on disk, then an empty complete journal is created.
     * @return an empty or filled complete journal containing all files which were back-upped the last time
     */
    public static Journal makeComplete() {
        lastComplete = CompleteJournalFactory.make();
        return lastComplete;
    }
    
    
    /**
     * @param exclusiveFilenamePattern
     * @return a partial journal containing all new and modified files since the last complete backup filtered by the patterns.
     * If one or more pattern matches the filename of a file then it is excluded in the journal.
     * @throws java.io.IOException
     */
    public static Journal makePartial(List<Pattern> exclusiveFilenamePattern) throws IOException {
        if(lastComplete == null)
            throw new IllegalStateException("You must create the complete journal first in order to make the partial one.");
        lastPartial = PartialJournalFactory.make(lastComplete, exclusiveFilenamePattern);
        return lastPartial;    
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * Get a list of files found in the last call of makePartial() which are new or modified since the last complete journal.
     * @return 
     */
    public static List<CombinedPath> partialAsList(){
        if(lastPartial == null)
            throw new IllegalStateException("You must create the partial journal first in order to obtain the result.");
        return PartialJournalFactory.asFiles();
    }
    
    
}
