package gui.strategy.creation;

import core.strategy.Strategy;
import core.strategy.StrategyManager;
import core.strategy.exception.StrategyAlreadyExistsException;
import core.strategy.exception.StrategyInvalidNameException;
import gui.Model;
import gui.View;
import gui.mcvutils.ControllerFactory;
import gui.mainwindow.MainWindowController;
import gui.strategy.StrategyController;
import gui.strategy.filters.StrategyFiltersDialog;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;
import logging.notifications.SimpleNotification;

/**
 * The controller which offers the features to create strategies for the dialog
 * @author fedrix
 */
public class StrategyCreationController extends StrategyController {
    
    
    /**
     * Create a new strategy
     * @param name
     * @param destination
     */
    public void createStrategy(String name, String destination) {
        // Checking if strategy name is valid
        if(name.matches(Strategy.NAME_REGEX)){
            Path destF = Paths.get(destination);
            // Checking if destination is not valid
            if(!Files.exists(destF) || !Files.isDirectory(destF)){
                NotificationManager.notify(view, new SimpleNotification("Invalid strategy destination folder, checks for folder existence, read and write permissions.", Level.WARNING));
            } else { 
                try {
                    StrategyManager.add(new Strategy(
                            name, 
                            destination, 
                            model.getTargets(), 
                            model.getSelectedEncryptionMode(), 
                            model.getSelectedCompressionMode(), 
                            model.getPatterns(),
                            model.getMaxIncrements())
                    );
                    ControllerFactory.getController(MainWindowController.class).updateView();
                    NotificationManager.notify(view, new SimpleNotification("Strategy Created!"));
                } catch (StrategyAlreadyExistsException| StrategyInvalidNameException | IOException ex) {
                    NotificationManager.notify(view, new ExceptionNotification("An error occured during strategy creation.",ex, Level.SEVERE));
                }
            }
        } else {
            NotificationManager.notify(view, new SimpleNotification("Invalid strategy name. It must start with a character(a-Z) and only contain alfanumeric characters or digits(0-9)", Level.SEVERE));
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
        super.view = v.toStrategyCreationDialog();
    }

    @Override
    public <modelT extends Model> void setModel(modelT m) {
        super.model = m.toStrategyCreationModel();
    }

    @Override
    public StrategyCreationController toStrategyCreationController() {
        return this;
    }

    @Override
    public StrategyController toStrategyController() {
        return this;
    }
    
}
