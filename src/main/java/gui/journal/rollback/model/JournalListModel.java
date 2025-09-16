package gui.journal.rollback.model;

import core.journal.Journal;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import static java.util.Objects.requireNonNull;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import utils.formatters.EasyDateTimeFormatter;


public class JournalListModel implements ListModel<String> {
    private final LinkedList<Journal> journals;
    private ListDataListener listener = null;
    
    public JournalListModel(){
        journals = new LinkedList<>();
    }
    
    private String getCreationTime(Journal j){
        return EasyDateTimeFormatter.getFormatted(j.getCreatedTime()); 
    }
    
    public void sort(Comparator<Journal> comparator){
        journals.sort(comparator);
    }
    
    /**
     * Add a journal to the model
     * @param j 
     */
    public void addElement(Journal j){
        journals.add(j);
        if(listener != null)
            listener.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, journals.size()));
    }
    
    /**
     * Remove a journal to the model
     * @param j 
     */
    public void removeElement(Journal j){
        journals.remove(j);
        if(listener != null)
            listener.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, journals.size()));
    }
    
    /**
     * Remove all elements of the model
     */
    public void clear(){
        journals.clear();
        if(listener != null)
            listener.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, journals.size()));
    }
    
    
    /**
     * Remove a journal to the model by passing the string associated
     * with the journal (obtained by getElementAt). 
     * The cost is linear to the size of the list.
     * @param j 
     */
    public void removeElement(String j){
        journals.removeIf((Journal t) -> getCreationTime(t).equals(j));
    }
    
    /**
     * Add all journals of the collection c to the list
     * @param c 
     */
    public void addAll(Collection<Journal> c){
        journals.addAll(c);
        if(listener != null)
            listener.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, journals.size()));
    }
    
    /**
     * Return journals at index
     * @param index
     * @return 
     * @throws IndexOutOfBoundException
     */
    public Journal getJournalAt(int index){
        return journals.get(index);
    }
    
    @Override
    public int getSize() {
        return journals.size();
    }

    @Override
    public String getElementAt(int index) {
        return getCreationTime(journals.get(index));
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listener = requireNonNull(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listener = null;
    }

        
    
}