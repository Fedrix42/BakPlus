package gui.strategy.creation;

import core.Compression;
import core.Encryption;
import core.strategy.Strategy;
import core.strategy.validation.Target;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import gui.Model;
import gui.View;
import gui.mcvutils.GenericListModel;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ListModel;


public class StrategyCreationModel implements Model {
    private StrategyCreationDialog view;
    protected String destFolder;   // Destination folder of the last created strategy
    protected final DefaultComboBoxModel encryption;
    protected final DefaultComboBoxModel compression;
    
    protected final GenericListModel<Target> targets;
    protected final GenericListModel<Pattern> filtersModel;
    
    protected DefaultBoundedRangeModel maxIncrementsModel;
    
    
    public StrategyCreationModel() {
        encryption = new DefaultComboBoxModel(Encryption.values());
        compression = new DefaultComboBoxModel(Compression.values());
        maxIncrementsModel = new DefaultBoundedRangeModel(Strategy.MIN_INCREMENTAL_LIMIT, 0, Strategy.MIN_INCREMENTAL_LIMIT, Strategy.MAX_INCREMENTAL_LIMIT);
        destFolder = "";
        targets = new GenericListModel<Target>() {
            @Override
            public String elementToString(Target element) {
                return element.getPathString();
            }
        };
        filtersModel = new GenericListModel<Pattern>() {
            @Override
            public String elementToString(Pattern element) {
                return element.pattern();
            }
        };
    }
    
    @Override
    public void updateViewAndModel() {
        this.view.setDestFolder(destFolder);
    }
    
    // Filters Management
    
    /**
     * @return the list of patterns 
     */
    public List<Pattern> getPatterns(){
        return filtersModel.getList();
    }

    public ListModel<String> getFiltersModel() {
        return filtersModel;
    }
    
    /**
     * Create a new exclusion filter as a regex for the strategy.
     * @param filter 
     * @throws PatternSyntaxException if the provided input is not a valid regex
     */
    public void addFilter(String filter) throws PatternSyntaxException {
        filtersModel.addElement(Pattern.compile(filter));
    }
    
    /**
     * Remove an exclusion filter from the filter list model
     * @param filter 
     * @throws PatternSyntaxException if the provided input is not a valid regex
     */
    public void removeFilter(String filter) throws PatternSyntaxException {
        filtersModel.removeElement(filter);
    }
    
    
    // Destination and Targets Management
    
    public String setDestFolder(String destFolder) {
        this.destFolder = destFolder;
        return destFolder;
    }

    public String getDestFolder() {
        return destFolder;
    }
    
    /**
     * Add a target to the targets list
     * @param path
     */
    public void addTarget(Path path){
        targets.addElement(new Target(path.toString()));
    }
    
    /**
     * Remove a target to the target list
     * @param target 
     */
    public void removeTarget(String target){
        targets.removeElement(target);
    }
    
    public List<Target> getTargets(){
        return targets.getList();
    }
    
    /**
     * Check if a passed item is already in the targets list
     * @param path
     * @return true if target in already a target, false otherwise
     */
    public boolean containsTarget(Path path){
        for(Target curr : targets.getList()){
            if(curr.getPath().equals(path))
                return true;
        }
        return false;
    }
    
    
    
    // All the rest
    
    public Encryption getSelectedEncryptionMode(){
        return (Encryption) encryption.getSelectedItem();
    }
    
    public Compression getSelectedCompressionMode(){
        return (Compression) compression.getSelectedItem();
    }
    
    public int getMaxIncrements(){
        return maxIncrementsModel.getValue();
    }
    
    
    
    
   
    
    

    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toStrategyCreationDialog();
        this.view.setTargetsList(targets);
        this.view.setEncryptionComboBoxModel(encryption);
        this.view.setCompressionComboBoxModel(compression);
        this.view.setDestFolder(destFolder);
        this.view.setMaxIncrementsLimitSliderModel(maxIncrementsModel);
    }

    @Override
    public StrategyCreationModel toStrategyCreationModel() {
        return this;
    }
    
    
}
