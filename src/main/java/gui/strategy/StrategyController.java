package gui.strategy;

import gui.Controller;
import gui.Model;
import gui.View;
import gui.strategy.creation.StrategyCreationModel;
import gui.strategy.filters.StrategyFiltersDialog;
import java.io.File;
import java.nio.file.Path;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import logging.notifications.NotificationManager;
import logging.notifications.SimpleNotification;

/**
 *
 * @author fedrix
 */
public abstract class StrategyController implements Controller {
    protected StrategyDialog view;
    protected StrategyFiltersDialog filtersDialog;
    protected StrategyCreationModel model; // StrategyManagementModel extends StrategyCreationModel
    protected final JFileChooser fc = new JFileChooser();
    
    
    /*
    * Update the model and the view according to the current selected strategy
    */
    public void updateView(){
        model.updateViewAndModel();
    }
    
    /**
     * Make the user pick a suitable destination folder
     * Update the model with the selected folder
     */
    public void pickDestinationFolder(){
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = fc.showOpenDialog(view);
        if(ret == JFileChooser.APPROVE_OPTION){
            File selected = fc.getSelectedFile();
            if(selected != null){
                String newDest = selected.toString();
                model.setDestFolder(newDest);
                view.setDestFolder(newDest);
            }
        }
    }
    
    /**
     * Make the user pick a target and add to the view targets list
     * If the target can be a file it must check that links are followed
     */
    public void addTarget(){
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int ret = fc.showOpenDialog(view);
        if(ret == JFileChooser.APPROVE_OPTION){
            File selected = fc.getSelectedFile();
            if(selected != null){ //Verifying the selected item is not already a target
                Path newTarget = selected.toPath();
                if(model.containsTarget(newTarget)){
                    NotificationManager.notify(view, new SimpleNotification("The selected item is already a target!"));
                    return;
                }
                model.addTarget(newTarget);
            }
        }
        
    }
    
    /**
     * Remove the target from the target model
     * @param target
     */
    public void removeTarget(String target){
        model.removeTarget(target);
    }
    
    
    // Filters Management
    
    public void addFilter(){
        try {
            String filter = JOptionPane.showInputDialog("Insert a regex as a new filter");
            if(filter != null){
                model.addFilter(filter);
            }
            
        } catch (PatternSyntaxException ex){
            NotificationManager.notify(view, new SimpleNotification("Invalid input. Check regex validity with other tools."));
        }
    }
    
    public void removeFilter(){
        String selectedFilter = filtersDialog.getSelectedFilter();
        if(selectedFilter != null)
            model.removeFilter(selectedFilter);
    }
    

    @Override
    public abstract <modelT extends Model> void setModel(modelT m);

    @Override
    public abstract <viewT extends View> void setView(viewT v);
    
    @Override
    public abstract StrategyController toStrategyController();
}
