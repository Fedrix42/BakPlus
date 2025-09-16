package settings;

import init.Environment;
import java.io.IOException;
import static java.util.Objects.requireNonNull;
import io.serialization.SerializerFactory;

/**
 * Settings Manager offering the ability to change settings, restore to default, ...
 * @author fedrix
 */
public class SettingsManager {
    private static Settings current = null;
    
    /**
     * Loading settings from file, if an error occur then defaults are loaded
     * @throws java.io.IOException if an error occurred during deserialization
     * @throws java.lang.ClassNotFoundException if an error occurred during deserialization 
     */
    public static void load() throws IOException, ClassNotFoundException  {
        current = (Settings) SerializerFactory.getObjectSerializer().deserialize(Environment.getSettingsFile());
    }
    
    
    /**
     * @return a copy of current settings
     */
    public static Settings getCurrent(){
        if(current == null)
            current = new Settings();
        return new Settings(current);
    }
    
    /**
     * @return a copy of defaults settings
     */
    public static Settings getDefaults(){
        return new Settings();
    }
    
    /**
     * Restore the settings to the defaults
     * @throws java.io.IOException if an error occur during while making settings persistent
     */
    public static void restoreToDefaults() throws IOException {
        Settings old = getCurrent(); // Copy of current
        try {
            current = new Settings();
            makeCurrentPersistent();
        } catch (IOException ex){
            current = old; // Rollback
            throw ex;
        }
    }
    
    /**
     * Override current settings with new settings
     * @param newSettings 
     */
    public static void override(Settings newSettings) throws IOException {
        // Argument checks
        requireNonNull(newSettings);
        Settings old = getCurrent(); // Copy of current
        try {
            current = newSettings;
            makeCurrentPersistent();
        } catch (IOException ex){
            current = old; // Rollback
            throw ex;
        }
    }
    
    
    /* Write settings to persistent memory */
    private static void makeCurrentPersistent() throws IOException  {
        SerializerFactory.getObjectSerializer().serialize(Environment.getSettingsFile(), current);
    }
    
    
}
