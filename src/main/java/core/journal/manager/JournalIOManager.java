
package core.journal.manager;

import core.journal.Journal;
import core.journal.JournalPath;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import utils.formatters.FilenameFormats;
import io.FilePatternFinder;
import io.serialization.SerializerFactory;

/**
 * An extension of the journal manager which is responsible
 * for all IO, serialization of journals
 * @author fedrix
 */
public class JournalIOManager extends JournalManager {
    
    /* Creation */
    
    
    /**
     * Make journal persistent in file system
     * @param journal
     * @throws IOException 
     */
    public static void makePersistent(Journal journal) throws IOException {
        SerializerFactory.getObjectSerializer().serialize(JournalPath.get(journal), journal);
    }
    
    
    /**
     * Create directory for journals of a specific strategy
     * If an IO error occur then it is logged and the directory may not be deleted.
     * @param strategyID 
     * @throws java.io.IOException 
     */
    public static void createJournalsDirectory(long strategyID) throws IOException {
        Path journalsFolder = JournalPath.getJournalsFolder(strategyID);
        if(!Files.exists(journalsFolder) || (Files.exists(journalsFolder) && !Files.isDirectory(journalsFolder)) )
            Files.createDirectory(journalsFolder);
    }
    
    
    
    
    
    
    /* Deletion */
    
    
    /**
     * Delete directory for journals of a specific strategy
     * If an IO error occur then it is logged and the directory may not be deleted.
     * @param strategyID 
     * @throws java.io.IOException 
     */
    public static void deleteJournalsDirectory(long strategyID) throws IOException {
        Path journalsFolder = JournalPath.getJournalsFolder(strategyID);
        if(Files.exists(journalsFolder)){
            Iterator<Path> it = Files.list(journalsFolder).iterator();
            while(it.hasNext()){
                Files.delete(it.next());
            }
            Files.delete(journalsFolder);
        }
    }
    
    /**
     * Delete a journal from disk.
     * @param journal 
     * @throws java.io.IOException if there is an IO error deleting the journal file
     */
    protected static void deleteFromDisk(Journal journal) throws IOException {
        Files.deleteIfExists(JournalPath.get(journal));
    }
    
    
    
    /* Getters */
    
    
    /**
     * Get the complete journal of a strategy from disk
     * @param strategyID
     * @return 
     * @throws java.io.IOException if there is an error during de-serialization of journal from disk
     * @throws java.lang.ClassNotFoundException 
     */
    public static Journal getCompleteJournal(long strategyID) throws IOException, ClassNotFoundException {
        return (Journal) SerializerFactory.getObjectSerializer().deserialize(JournalPath.getOfComplete(strategyID));
    }
    
    /**
     * Get every journal of type partial which is stored in the journals disk folder of a specific strategy
     * @param strategyID
     * @return 
     * @throws java.io.IOException 
     * @throws java.lang.ClassNotFoundException 
     */
    public static List<Journal> getAllPartialJournals(long strategyID) throws IOException, ClassNotFoundException{
        List<Journal> result = new LinkedList<>();
        // Partial Journals
        List<Path> journalsPaths = FilePatternFinder.search(
                JournalPath.getJournalsFolder(strategyID),
                FilenameFormats.JOURNAL_PARTIAL_TYPE_PATTERN, 
                "stgid",
                Long.toString(strategyID)
        );
        for(Path journalFile : journalsPaths){
            result.add((Journal) SerializerFactory.getObjectSerializer().deserialize(journalFile));
        }
        return result;
    }
    
    
    /**
     * Get every journal which is stored in the journals disk folder of a specific strategy
     * @param strategyID
     * @return 
     * @throws java.io.IOException 
     * @throws java.lang.ClassNotFoundException 
     */
    public static List<Journal> getAllJournals(long strategyID) throws IOException, ClassNotFoundException{
        List<Journal> result = getAllPartialJournals(strategyID);
        result.add((Journal) SerializerFactory.getObjectSerializer().deserialize(JournalPath.getOfComplete(strategyID)));
        return result;
    }
    
    
    
    
    
    
}
