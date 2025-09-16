package gui.settings;

import gui.Controller;
import init.Init;
import java.io.File;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import gui.View;
import gui.Model;
import java.io.IOException;
import settings.SettingsManager;
import logging.LoggerManager;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;

/**
 * Settings Controller base class for interaction with settings dialog
 * @author Fedrix
 */
public class SettingsController implements Controller {
    private SettingsDialog view;
    private SettingsDialogModel model;
    
    
    /**
     * Try to override current settings (Which does also write them to disk)
     */
    public void saveAction(){
        try {
            SettingsManager.override(model.getSettings());
            view.showShutdownButton(true);
        } catch (IOException ex){
            // Error notification
            var not = new ExceptionNotification("Could not save the settings! Try deleating settings file in installation folder to fix.", ex,Level.SEVERE);
            LoggerManager.exceptionNL().log(not);
            NotificationManager.notify(view, not);
            model.setStatus(true, "Something went wrong saving settings!");
        }
        model.setStatus(false, "Settings saved! Restart to apply!");
    }
    
    /**
     * Reset view settings to defaults and update fields for user feedback
     */
    public void resetAction(){
        int reply = JOptionPane.showConfirmDialog(view, "Are you sure?", "Reset settings to defaults", JOptionPane.YES_NO_OPTION);
        if(reply == JOptionPane.YES_OPTION){
            model.setSettings(SettingsManager.getDefaults());
            model.updateViewAndModel();
        }
    }
    
    /**
     * Make user choose a folder as the default strategy folder
     */
    public void strategyFolderAction(){
        JFileChooser fc = new JFileChooser(model.getSettings().getStrategiesFolder());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = fc.showOpenDialog(view.getParent());
        if(ret == JFileChooser.APPROVE_OPTION){
            File newFolder = fc.getSelectedFile();
            if(newFolder.exists()){
                if(newFolder.isDirectory() && newFolder.canRead()){
                    model.getSettings().setStrategiesFolder(newFolder.toString());
                    model.updateViewAndModel();
                } else {
                    model.setStatus(true, "Something went wrong, selected item is not readable or not a folder, check your permissions!");
                }
            } else {
                model.setStatus(true, "Something went wrong, folder appear as non existing!");
            }
        }
    }
    
    public void themeUpdatedAction(){
        model.updateTheme();
    }
    
    public void windowWidthUpdatedAction(){
        model.updateWindowWidth();
    }
    
    public void windowHeightUpdatedAction(){
        model.updateWindowHeight();
    }
    
    /**
     * Action performed when the shutdown button is pressed
     */
    public void shutdownAction(){
        Init.shutdown(0);
    }

    @Override
    public <viewT extends View> void setView(viewT view) {
        assert(view instanceof SettingsDialog);
        this.view = view.toSettingsDialog();
    }

    @Override
    public <modelT extends Model> void setModel(modelT model) {
        assert(model instanceof SettingsDialogModel);
        this.model = model.toSettingsModel();
    }

    @Override
    public SettingsController toSettingsController() {
        return this;
    }
    
    
}
