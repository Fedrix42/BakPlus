package gui.mcvutils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * A generic and simple list model to implement the way the object is rappresented as a string.
 * 
 * @author fedrix
 * @param <T>
 */
public abstract class GenericListModel<T> extends AbstractListModel<String> {
    private final List<T> list;
    
    public GenericListModel(){
        list = new LinkedList<>();
    }
    
    /**
     * @param element
     * @return the string rappresentation of the object the way it is displayed by the
     * swing component which uses this model.
     */
    public abstract String elementToString(T element);
    
    
    @Override
    public String getElementAt(int index) {
        return elementToString(get(index));
    }
    
    /**
     * Remove an element of the model by using
     * it's string rappresentation defined by elementToString()
     * @param element 
     * @see elementToString()
     */
    public void removeElement(String element){
        for(int i = 0; i < list.size(); i++){
            if(elementToString(list.get(i)).equals(element)){
                list.remove(i);
                fireIntervalRemoved(this, i, i);
            }
        }
    }
    
    /**
     * Add an element to the model
     * @param element 
     */
    public void addElement(T element){
        int index = list.size();
        list.add(element);
        fireIntervalAdded(this, index, index);
    }
    
    /**
     * Remove an element of the model
     * @param element 
     */
    public void removeElement(T element){
        int index = list.indexOf(element);
        list.remove(index);
        fireIntervalRemoved(this, index, index);
    }
    
    /**
     * Remove an element of the model by using index
     * @param index
     */
    public void removeElement(int index){
        list.remove(index);
        fireIntervalRemoved(this, index, index);
    }
    
    /**
     * Add a collection of elements to the model
     * @param elements 
     */
    public void addAll(Collection<T> elements){
        if (!elements.isEmpty()){
            int startIndex = list.size();
            list.addAll(elements);
            fireIntervalAdded(this, startIndex, list.size() - 1);
        }
    }
    
    /**
     * @param index
     * @return the element of the model at the passed index
     */
    public T get(int index){
        return list.get(index);
    }
    
    /**
     * Remove all elements of the model
     */
    public void clear(){
        int lastElIndex = list.size() - 1;
        if (lastElIndex >= 0) {
            list.clear();
            fireIntervalRemoved(this, 0, lastElIndex);
        }
    }
    
    /**
     * Get the list used by the model.
     * Changes made to the returned list affect the list used by the model.
     * Do NOT make changes to the data of the list through the returned reference
     * or the data listeners would not be notified.
     * @return 
     */
    public List<T> getList(){
        return list;
    }
    
    
    /**
     * Get a copy of the list used by the model.
     * @return 
     */
    public List<T> getListCopy(){
        return List.copyOf(list);
    }
    
    @Override
    public int getSize() {
        return list.size();
    }
    
    
    
}
