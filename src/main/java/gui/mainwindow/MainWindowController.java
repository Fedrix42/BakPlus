package gui.mainwindow;

import core.tasks.TaskManager;
import core.journal.Journal;
import core.journal.manager.JournalManager;
import core.journal.JournalType;
import gui.Controller;
import gui.mcvutils.ControllerFactory;
import core.strategy.Strategy;
import core.strategy.StrategyManager;
import init.Init;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import gui.settings.SettingsDialogModel;
import gui.strategy.creation.StrategyCreationModel;
import gui.strategy.management.StrategyManagementModel;
import gui.View;
import gui.mcvutils.InitMCV;
import gui.strategy.creation.StrategyCreationDialog;
import gui.strategy.management.StrategyManagementDialog;
import gui.mainwindow.view.main.MainWindow;
import logging.notifications.SimpleNotification;
import gui.settings.SettingsDialog;
import gui.Model;
import gui.backups.incremental.IncrementalCreationController;
import gui.backups.incremental.BackupCreationProgressDialog;
import gui.backups.integrity.IntegrityVerificationProgressController;
import gui.backups.integrity.IntegrityVerificationProgressModel;
import gui.backups.integrity.view.BackupKeyInputDialog;
import gui.backups.integrity.view.IntegrityVerificationProgressDialog;
import gui.journal.rollback.JournalRollbackController;
import gui.journal.rollback.JournalRollbackDialog;
import gui.journal.rollback.model.JournalRollbackModel;
import gui.mainwindow.view.AboutDialog;
import gui.settings.SettingsController;
import gui.strategy.creation.StrategyCreationController;
import gui.strategy.management.StrategyManagementController;
import utils.Pair;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;

/**
 * Main Controller for main window, implement features
 * @author Fedrix
 */
public class MainWindowController implements Controller {
    private MainWindow view;
    private MainWindowModel model;
    
    @Override
    public <viewT extends View> void setView(viewT view) {
        assert(view instanceof MainWindow);
        this.view = view.toMainWindow();
    }

    @Override
    public <modelT extends Model> void setModel(modelT model) {
        assert(model instanceof MainWindowModel);
        this.model = model.toMainWindowModel();
    }
    
    /**
     * Update main window displayed info based on model data
     */
    public void updateView(){
        model.updateViewAndModel();
    }
    
    /**
     * Create a new backup from current loaded strategy
     * @see Backup class
     */
    public void newBackup() {
        if(StrategyManager.isCurrentLoaded()){
            InitMCV.setReferencesAndUpdate(
                ControllerFactory.getController(IncrementalCreationController.class),
                new BackupCreationProgressDialog(view),
                null);
            ControllerFactory.getController(IncrementalCreationController.class).run();
            updateView();
        } else {
            NotificationManager.notify(view, new SimpleNotification("Load a strategy first!", Level.INFO));
        }
    }

    
    
    private List<Path> manualIntegrityFileSelection(){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.showOpenDialog(view);
        return Arrays.stream(fc.getSelectedFiles()).map((file) -> file.toPath()).toList();
    }
    
    private List<Pair<Path, char[]>> getBackupsKey(List<Path> backups){
        if(backups == null || backups.isEmpty())
            throw new IllegalArgumentException("Backups list must be non null and non empty!");
         return (new BackupKeyInputDialog(view, true, backups)).showDialog();
    }
    
    /**
     * Check integrity of backups in destination folder of currently loaded strategy
     * @param auto automatic search or manual file selection
     */
    public void checkBackupIntegrity(boolean auto) {
        List<Pair<Path, char[]>> backups;
        List<Path> searchResult = null;
        if(StrategyManager.isCurrentLoaded()){
            if(auto){
                try {
                    searchResult = TaskManager.searchForBackups();
                } catch (IOException | IllegalArgumentException ex) {
                    NotificationManager.notify(view, new ExceptionNotification("Couldn't automatically search for backups. Check your strategy destination!", ex, Level.WARNING));
                }
            } else {
                searchResult = manualIntegrityFileSelection();
            }
        } else {
            NotificationManager.notify(view, new SimpleNotification("Load a strategy first!", Level.INFO));
            return;
        }
        assert(searchResult != null);
        if(!searchResult.isEmpty()){
            backups = getBackupsKey(searchResult);
            if(backups != null){
                InitMCV.setReferencesAndUpdate(
                            ControllerFactory.getController(IntegrityVerificationProgressController.class), 
                            new IntegrityVerificationProgressDialog(view, true), 
                            new IntegrityVerificationProgressModel());
                Journal ofCLS = JournalManager.makeComplete();
                if(!(ofCLS.type == JournalType.COMPLETE))
                    NotificationManager.notify(view, new SimpleNotification("Journal not found! Inconsistent state.", Level.SEVERE));
                else
                    ControllerFactory.getController(IntegrityVerificationProgressController.class).run(ofCLS, backups);
            }
        } else
            NotificationManager.notify(view, new SimpleNotification("No backups where found on destination folder. Check for file names, they must have the correct format.", Level.INFO));
            
    }
    
    /**
     * Create the dialog used to create a new strategy
     * @see Strategy class
     */
    public void newStrategy(){
        StrategyCreationDialog dialog = InitMCV.setReferencesAndUpdate(
                ControllerFactory.getController(StrategyCreationController.class), 
                new StrategyCreationDialog(view, true), 
                new StrategyCreationModel());
        dialog.setVisible(true);
    }
    
    
    /**
     * Create the dialog used to edit / delete strategies
     * @see Strategy class
     */
    public void manageStrategies() {
        if(StrategyManager.size() > 0){
            StrategyManagementDialog dialog = InitMCV.setReferencesAndUpdate(
                    ControllerFactory.getController(StrategyManagementController.class), 
                    new StrategyManagementDialog(view, true),
                    new StrategyManagementModel());
            dialog.setVisible(true);
        } else {
            NotificationManager.notify(view, new SimpleNotification("There are no strategies to edit / delete.", Level.INFO));
        }
    }
    
    
    /** TO IMPROVE
     * Load the selected strategy updating display information and strategy manager
     * @param selected_idx 
     */
    public void loadStrategy(int selected_idx){
        Strategy selectedStrategy = model.getStrategyAt(selected_idx);
        if(selectedStrategy != null){
            view.pushStatus("Loading Strategy...");
            model.clearHighlight();
            
            // Checking targets validitity
            boolean targetsOK = true;
            var targetsValidationErrors = selectedStrategy.validateTargets();
            for (var pair : targetsValidationErrors){ // For every target get target and a list of exceptions
                if(!pair.second().isEmpty()){ // If exceptions where generated during validation
                    targetsOK = false;
                    String target = pair.first();
                    for(Exception error : pair.second()){
                        view.pushStatus("[WARNING] Target %s is not valid: %s".formatted(target, error.toString()));
                    }
                    model.highlightTarget(target);
                }
            }
            if(!targetsOK)
                NotificationManager.notify(view, new SimpleNotification("Could not load a strategy.", Level.SEVERE));
            
            // Checking destination validitity
            var dstErrors = selectedStrategy.validateDestination();
            if(!dstErrors.isEmpty()){
                NotificationManager.notify(view, new SimpleNotification(
                        "The destination of the strategy is not valid!", 
                        Level.WARNING));
                view.pushStatus("[ERROR] Invalid backup destination: %s - Change it.".formatted(selectedStrategy.getDst()));
                return;
            }
            
            // Loading strategy
            StrategyManager.loadCurrent(selectedStrategy);
            
            // Updating view info
            model.updateViewAndModel();
            view.pushStatus("Strategy Loaded: %s".formatted(selectedStrategy.getName()));
            
        } else {
            NotificationManager.notify(view, new SimpleNotification("Invalid strategy name. It must start with a character(a-Z) and only contain alfanumeric characters or digits(0-9)", Level.WARNING));
        }
    }
    
    
    public void rollbackJournal(){
        if(StrategyManager.isCurrentLoaded()){
            JournalRollbackDialog dialog = InitMCV.setReferencesAndUpdate(
                    ControllerFactory.getController(JournalRollbackController.class), 
                    new JournalRollbackDialog(view, true),
                    new JournalRollbackModel(StrategyManager.getCurrentlyLoaded()));
            dialog.setVisible(true);
        } else
            NotificationManager.notify(view, new SimpleNotification("Load a strategy first!", Level.INFO));
    }
    
    /**
     * Open settings dialog to change program settings
     */
    public void settings() {
        SettingsDialog s = InitMCV.setReferencesAndUpdate(
                ControllerFactory.getController(SettingsController.class), 
                new SettingsDialog(view, true),
                new SettingsDialogModel());
        s.setVisible(true);
    }
    
    /**
     * Open about dialog to show information
     */
    public void about() {
        AboutDialog dialog = new AboutDialog(view, false);
        dialog.setVisible(true);
    }

    /**
     * Dispose main window and exit program safely
     */
    public void exit() {
        Init.shutdown(0);
    }

    @Override
    public MainWindowController toMainWindowController() {
        return this;
    }

    
    
    
    
}
