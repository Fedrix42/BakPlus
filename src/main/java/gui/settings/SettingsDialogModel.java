package gui.settings;

import javax.swing.DefaultComboBoxModel;
import settings.Settings;
import settings.Theme;
import gui.View;
import gui.mainwindow.view.main.MainWindowSizes;
import gui.Model;
import settings.SettingsManager;

/**
 *
 * @author Fedrix
 */
public class SettingsDialogModel implements Model {
    private SettingsDialog view;
    private Settings copyOfCurrent;
    private final DefaultComboBoxModel<Theme> themes;
    private final DefaultComboBoxModel<String> mainWindowWidths;
    private final DefaultComboBoxModel<String> mainWindowHeights;
    
    public SettingsDialogModel(){
        copyOfCurrent = SettingsManager.getCurrent();
        themes = new DefaultComboBoxModel(Theme.values());
        mainWindowWidths = new DefaultComboBoxModel(MainWindowSizes.getWidths());
        mainWindowHeights = new DefaultComboBoxModel(MainWindowSizes.getHeights());
    }
    
    @Override
    public void updateViewAndModel() {
        view.setStrategyFolderPath(copyOfCurrent.getStrategiesFolder());
        mainWindowWidths.setSelectedItem(copyOfCurrent.getMainWindowWidth());
        mainWindowHeights.setSelectedItem(copyOfCurrent.getMainWindowHeight());
        themes.setSelectedItem(copyOfCurrent.getTheme());
    }
    
    public void setStatus(boolean isError, String text){
        view.setStatus(isError, text);
    }
    
    public void updateTheme(){
        copyOfCurrent.setTheme((Theme)themes.getSelectedItem());
    }
    
    public void updateWindowWidth(){
        copyOfCurrent.setMainWindowWidth(Integer.valueOf(mainWindowWidths.getSelectedItem().toString()));
    }
    
    public void updateWindowHeight(){
        copyOfCurrent.setMainWindowHeight(Integer.valueOf(mainWindowHeights.getSelectedItem().toString()));
    }
    
    public void setSettings(Settings s){
        this.copyOfCurrent = s;
    }
    
    public Settings getSettings(){
        return copyOfCurrent;
    }

    @Override
    public <viewT extends View> void setView(viewT view) {
        this.view = view.toSettingsDialog();
        this.view.setThemeComboBoxModel(themes);
        this.view.setMainWindowWidthComboBoxModel(mainWindowWidths);
        this.view.setMainWindowHeightComboBoxModel(mainWindowHeights);
        this.view.setStrategyFolderPath(copyOfCurrent.getStrategiesFolder());
    }

    @Override
    public SettingsDialogModel toSettingsModel() {
        return this;
    }

    
}
