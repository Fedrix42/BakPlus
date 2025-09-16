package init;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import gui.mcvutils.ControllerFactory;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import core.strategy.StrategyManager;
import core.strategy.exception.StrategiesLoadingException;
import gui.mainwindow.MainWindowController;
import java.util.logging.Level;
import settings.Theme;
import gui.mainwindow.MainWindowModel;
import gui.mainwindow.view.main.MainWindow;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import settings.Settings;
import settings.SettingsManager;
import logging.LoggerManager;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;


/**
 * This class should do: load the settings, launch main window thread and eventually display starting notifications to the user
 * @author Fedrix
 */
public final class Init {
    private static Thread mainT = null;
    private static final String TITLE = "BakPlus"; // Title of main window
    private static MainWindow mainWindow = null;
    private static MainWindowModel mainWindowModel = null;
    private static LinkedList<ExceptionNotification> startNotifications = null; 
    
    private static void setTheme(Theme t){
        switch (t){
            case Dark -> FlatDarkLaf.setup();
            case Light -> FlatLightLaf.setup();
            default -> FlatLightLaf.setup();
        }
    }
    
    public static MainWindow getMainWindow(){
        return mainWindow;
    }
    
    /**
     * Method called at the start of the program
     */
    public static void run() {
        // Inizialization of critical components: environment and logging
        try {
            if(!Environment.isEnvironmentOK()){
                List<String> errors = Environment.getEnvironmentErrors();
                int size = errors.size();
                for(int i = 0; i < size; i++){
                    String error = errors.get(i);
                    JOptionPane.showMessageDialog(new JFrame(), 
                            "Critical error occurred during program inizialization (%d / %d): %s".formatted(i+1, size, error), 
                            "Critical Errors", JOptionPane.ERROR_MESSAGE
                    );
                }
                System.exit(-1);
            }
            
            LoggerManager.init();
        } catch (SecurityException | IOException ex){
            JOptionPane.showMessageDialog(new JFrame(), 
                    "Critical error occurred during program inizialization, no access to system env variables or properties:  " + ex.toString(), 
                    "Critical Errors", JOptionPane.ERROR_MESSAGE
            );
        }
        
        // Inizialization of uncritical components: settings and strategies
        startNotifications = new LinkedList<>();
        
        // Settings loading
        try {
            SettingsManager.load();
        } catch (IOException | ClassNotFoundException loadingEx){
            var not = new ExceptionNotification("Could not load the settings, using defaults! Normal if it's the first time you start the program, if it's not try deleting settings file in installation folder to fix.", loadingEx, Level.WARNING);
            LoggerManager.exceptionNL().log(not);
            startNotifications.add(not);
            try {
                SettingsManager.restoreToDefaults();
            } catch (IOException restoreEx) {
                LoggerManager.exceptionNL().log(new ExceptionNotification("Could not restore the settings to defaults!", restoreEx, Level.SEVERE));
            }
        }
        
        Settings currentS = SettingsManager.getCurrent();
        setTheme(currentS.getTheme());
        
        // Creating strategies folder if it's missing (Necessary for the first time the program is run)
        if(!Files.exists(currentS.getStrategiesFolderPath())){
            try {
                Files.createDirectory(currentS.getStrategiesFolderPath());
            } catch (IOException ex) {
                startNotifications.add(new ExceptionNotification("Could not create strategies folder which is missing", ex, Level.SEVERE));
            }
        }
        
        
        // Create main window thread
        mainT = new Thread(new Runnable() {
            @Override
            public void run() {
                mainWindow = new MainWindow(
                        TITLE, 
                        currentS.getMainWindowWidth(), 
                        currentS.getMainWindowHeight(),
                        currentS.getTheme()
                );
                mainWindow.setVisible(true);
            }
        });
        mainT.start();
        
        // Waiting for main window thread to initialize mainWindow reference, timeout: about 5s
        try {
            for(short c = 0; mainWindow == null && c < 5; c++)
                TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e){}
        
        
        // Setting references
        if(mainWindow != null){
            mainWindowModel = new MainWindowModel();
            mainWindowModel.setView(mainWindow);
            MainWindowController mwController = ControllerFactory.getController(MainWindowController.class);
            mainWindow.setController(mwController);
            mwController.setView(mainWindow);
            mwController.setModel(mainWindowModel);
            // Checking for start notifications
            for(ExceptionNotification n : startNotifications){
                NotificationManager.notify(mainWindow, n);
            }
            startNotifications.clear();
        }
        
        mainWindow.pushStatus("Loading strategies...");
        // Loading strategies from disk
        try {
            StrategyManager.loadFromDisk();
            // Updating main window strategies
            if(StrategyManager.size() > 0){
                mainWindowModel.resetStrategies();
            }
            mainWindow.pushStatus("Strategies loaded!");
        } catch (StrategiesLoadingException ex) {
            for(var t : ex.getExceptions()){
                var not = new ExceptionNotification("Could not load the strategy: %s".formatted(t.second()), t.first(), Level.SEVERE);
                LoggerManager.exceptionNL().log(not);
                NotificationManager.notify(mainWindow, not);
            }
        }
    }
    
    /**
     * Called at the shutdown, must release windows and resources and exit
     * @param retcode
     */
    public static void shutdown(int retcode){
        try {
            mainWindow.dispose();
            mainT.interrupt();
        } catch(Exception e){
            LoggerManager.exception().log(Level.SEVERE, "Error during shutdown operation of the program.", e);
        }
        System.exit(retcode);
    }
    
}
