package core.journal;

import core.journal.utils.Checksum;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Iterator for a journal used to get data from journal hash map
 * @author fedrix
 */
public class JournalIterator implements Iterator<JournalEntry> {
    private final Iterator<Map.Entry<String, List<Checksum>>> entriesIt;
    private Map.Entry<String, List<Checksum>> currentEntry = null;
    private Iterator<Checksum> checksumIt = null;
    
    
    public JournalIterator(Journal journal) {
        this.entriesIt = journal.getHashmap().entrySet().iterator();
        if(entriesIt.hasNext()){
            currentEntry = entriesIt.next();
            checksumIt = currentEntry.getValue().iterator();
        }
    }

    @Override
    public boolean hasNext() {
        return entriesIt.hasNext() || checksumIt.hasNext();
    }

    @Override
    public JournalEntry next() {
        if(checksumIt == null || currentEntry == null)
            throw new NoSuchElementException();
        
        if(!checksumIt.hasNext()){
            if(!entriesIt.hasNext())
                throw new NoSuchElementException();
            currentEntry = entriesIt.next();
            checksumIt = currentEntry.getValue().iterator();
        }
        
        return new JournalEntry(
                currentEntry.getKey(), 
                checksumIt.next(),
                currentEntry.getValue().size()
        );
    }
    
}
