package gui.strategy.management;

import javax.swing.JOptionPane;
import core.strategy.Strategy;
import core.strategy.StrategyManager;
import core.strategy.exception.StrategyInvalidNameException;
import java.io.IOException;
import java.util.logging.Level;
import gui.mcvutils.ControllerFactory;
import gui.View;
import gui.Model;
import gui.mainwindow.MainWindowController;
import gui.strategy.StrategyController;
import gui.strategy.filters.StrategyFiltersDialog;
import logging.LoggerManager;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;
import logging.notifications.SimpleNotification;

/**
 * The controller which offers the features to create / edit/ delete strategies
 * @author Fedrix
 */
public class StrategyManagementController extends StrategyController {
    
    
    /**
     * Edit the strategy
     * @param newName new name of the strategy which is being edited
     */
    public void editStrategy(String newName){
        Strategy toEdit = model.toStrategyManagementModel().getSelected();
        Strategy old = new Strategy(toEdit);
        try {
            // Editing in-memory strategy
            toEdit.setName(newName);
            toEdit.setDst(model.getDestFolder());
            toEdit.setIncLimit(model.getMaxIncrements());
            toEdit.seteMode(model.getSelectedEncryptionMode());
            toEdit.setcMode(model.getSelectedCompressionMode());
            toEdit.setFilters(model.getPatterns());
            toEdit.setTargets(model.getTargets());
            
            // Editing strategy in disk 
            StrategyManager.writeToDisk(toEdit);
            ControllerFactory.getController(MainWindowController.class).updateView();
            NotificationManager.notify(view, new SimpleNotification("Strategy edited!"));
        } catch (StrategyInvalidNameException ex) {
            var not = new SimpleNotification("The name of the strategy with id: %d is not valid!".formatted(toEdit.getId()), Level.INFO);
            NotificationManager.notify(view, not); 
            /* Rollback is not necessary since setName() is the first method called, the execution is stopped after the exception */
        } catch(IOException ex){
            var not = new ExceptionNotification("An error occured during editing of strategy with id: " + toEdit.getId(), ex, Level.SEVERE);
            LoggerManager.exceptionNL().log(not);
            NotificationManager.notify(view, not);
            toEdit.setAll(old); // In-memory strategy rollback
            model.updateViewAndModel();
        }
    }
    
    
    /**
     * Delete an existing strategy
     */
    public void deleteStrategy(){
        Strategy toDelete = model.toStrategyManagementModel().getSelected();
        if(toDelete != null){
            int reply = JOptionPane.showConfirmDialog(view, "Sure to delete %s?".formatted(toDelete.getName()), "Delete a strategy", JOptionPane.YES_NO_OPTION);
            if(reply == JOptionPane.YES_OPTION){
                try {
                    StrategyManager.delete(toDelete);
                } catch (IOException ex) {
                    var not = new ExceptionNotification("An error occured during deletion of strategy: %s".formatted(toDelete.getName()), ex, Level.SEVERE);
                    LoggerManager.exceptionNL().log(not);
                    NotificationManager.notify(view, not);
                }
                StrategyManager.unloadCurrent();
                model.toStrategyManagementModel().clearStrategySelection();
                model.updateViewAndModel();
                ControllerFactory.getController(MainWindowController.class).updateView();
            }
        } else {
            NotificationManager.notify(view, new SimpleNotification("Select a strategy first!", Level.INFO));
        }
    }
    
    public void manageFilters(){
        filtersDialog = new StrategyFiltersDialog(view, false);
        filtersDialog.setController(this);
        filtersDialog.setJListModel(model.getFiltersModel());
        filtersDialog.setVisible(true);
    }
    
    
    
    @Override
    public <viewT extends View> void setView(viewT v) {
        super.view = v.toStrategyManagementDialog();
    }

    @Override
    public <modelT extends Model> void setModel(modelT m) {
        super.model = m.toStrategyManagementModel();
    }

    @Override
    public StrategyManagementController toStrategyManagementController() {
        return this;
    }
    
    @Override
    public StrategyController toStrategyController() {
        return this;
    }
    
}
