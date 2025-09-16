package init;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * This class contains the global variables used by the software
 * @author Fedrix
 */
public class Environment {
    // General info
    public static final String VERSION = "1.0";
    public static final String LICENSE = "GNU GPL 3.0";
    public static String JAVA_VERSION = System.getProperty("java.version");
    
    // Env variables and properties
    private static final String[] properties = {"java.io.tmpdir", "user.home"};
    private static final List<String> envErrors = new LinkedList<>();
    
    
    // Useful paths
    private static Path dataFolder = null;
    private static Path settingsFile = null;
    private static Path tmpFolderPath = null;
    
    /**
     * Checks if there are errors accessing the environment variables
     * which are necessary for the program.
     * Errors details can be obtained calling 
     * @see getEnvironmentErrors()
     * @return false if there are errors, true otherwise
     * @throws SecurityException if the security policy does not allow for access
     * to env variables or system properties
     */
    public static boolean isEnvironmentOK() throws SecurityException{
        boolean result = true;
        for(String propKey : properties){
            if(System.getProperty(propKey) == null){
                envErrors.add("The necessary property %s was not found!".formatted(propKey));
                result = false;
            }
        }
        if(!Files.exists(getDataFolder())){
            try {
                Files.createDirectory(dataFolder);
            } catch (IOException ex) {
                result = false;
                envErrors.add("Error while trying to create missing data folder: " + ex.toString());
            }
        }
        if(!Files.isWritable(getTmpFolder()) || !Files.isReadable(getTmpFolder())){
            result = false;
            envErrors.add("Temp folder %s must be readable and writable!".formatted(getTmpFolder()));
        }
        return result;
    }
    
    /**
     * @return an empty or filled list with error descriptions generated during the call to
     * @see isEnvironmentOK()
     */
    public static List<String> getEnvironmentErrors(){
        return envErrors;
    }
    
    /**
     * 
     * @return the path of the temp folder in the system which is used to extract zip backups and
     * to perform operations with temp files.
     */
    public static Path getTmpFolder(){
        if(tmpFolderPath == null)
            tmpFolderPath = Paths.get(System.getProperty(properties[0]));
        return tmpFolderPath;
    }
    
    /**
     * @return the path of the data folder where logs, strategies, ecc files are located
     */
    public static Path getDataFolder() {
        if(dataFolder == null){
            dataFolder = Paths.get(System.getProperty(properties[1])).resolve(".bakplus_data/");
        }
        return dataFolder;
    }
    
    /**
     * @return the path of the settings file which usually resides inside the installPath folder
     */
    public static Path getSettingsFile() {
        if(settingsFile == null)
            settingsFile = Paths.get(getDataFolder().toString(), "settings.config");
        return settingsFile;
    }
    
    
}
