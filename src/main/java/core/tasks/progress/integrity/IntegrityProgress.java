package core.tasks.progress.integrity;

import core.tasks.progress.Progress;
import core.tasks.progress.ProgressDecorator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A queue of entries rappresenting files that have been checked by integrity check engine
 * @author Fedrix
 */
public class IntegrityProgress extends ProgressDecorator {
    private final Queue<IntegrityEntry> checkedEntry;
    public boolean completePass = true;
    
    public IntegrityProgress(Progress progress) {
        super(progress);
        checkedEntry = new LinkedList<>();
    }
    
    public void queue(IntegrityEntry entry){
        checkedEntry.add(entry);
    }
    
    public IntegrityEntry remove(){
        return checkedEntry.remove();
    }
    
    public boolean isEmpty(){
        return checkedEntry.isEmpty();
    }
    
    
}
