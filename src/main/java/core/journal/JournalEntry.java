package core.journal;

import core.journal.utils.Checksum;
import static java.util.Objects.requireNonNull;

/**
 * Entry of a journal, used by the journal iterator to store data
 * A file in the journal is defined by his checksum which is unique, so
 * more than one file can exists in the journal with the same filename but different checksum
 * @author fedrix
 */
public record JournalEntry(String filename, Checksum checksum, int numOfFiles) {

    /**
     * 
     * @param filename the filename
     * @param checksum the checksum of this specific file
     * @param numOfFiles number of file with this filename in the journal
     */
    public JournalEntry(String filename, Checksum checksum, int numOfFiles) {
        this.filename = requireNonNull(filename);
        this.checksum = requireNonNull(checksum);
        this.numOfFiles = numOfFiles;
    }
    
}
