package settings;

import init.Environment;
import java.nio.file.Path;
import java.nio.file.Paths;
import gui.mainwindow.view.main.MainWindowSizes;
import io.serialization.StatedExternalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Files;
import static java.util.Objects.requireNonNull;

/**
 * General settings of the program
 * 
 * 
 * Serialization
 * This class implements Externalizable in order to perform explicit serialization 
 * using read and write external methods. UPDATE these methods when the class is 
 * changed to assure compatibility through different software versions!
 * 
 * 
 * @author Fedrix
 */
public final class Settings extends StatedExternalizable {
    private static final long serialVersionUID = 1L;
    private Integer mainWindowWidth = MainWindowSizes.getDefaultWidth();
    private Integer mainWindowHeight = MainWindowSizes.getDefaultHeight();
    private String strategiesFolder = null; // Easier to serialize then File obj
    private Theme theme = Theme.Light;
    
    /**
     * Constructor for default settings
     */
    public Settings(){
        strategiesFolder = Environment.getDataFolder().resolve("strategies/").toString();
    }
    
    /**
     * Main constructor
     * @param mainWindowWidth
     * @param mainWindowHeight
     * @param strategiesFolder
     * @param theme 
     */
    public Settings(Integer mainWindowWidth, Integer mainWindowHeight, Path strategiesFolder, Theme theme){
        if(!MainWindowSizes.validateWidth(requireNonNull(mainWindowWidth)) || !MainWindowSizes.validateHeight(requireNonNull(mainWindowHeight)))
            throw new IllegalArgumentException("New settings has invalid width or height!");
        if(!Files.exists(requireNonNull(strategiesFolder)))
            throw new IllegalArgumentException("New settings has invalid strategies folder path, it must be exist on file system!");
        if(!Files.isDirectory(strategiesFolder))
            throw new IllegalArgumentException("New settings has invalid strategies folder path, it must be a directory!");
        this.mainWindowWidth = mainWindowWidth;
        this.mainWindowHeight = mainWindowHeight;
        this.strategiesFolder = strategiesFolder.toString();
        this.theme = theme;
    }
    
    /**
     * Copy construct
     * @param s settings to be copied
     */
    public Settings(Settings s){
        this.mainWindowWidth = s.getMainWindowWidth();
        this.mainWindowHeight = s.getMainWindowHeight();
        this.strategiesFolder = s.getStrategiesFolder();
        this.theme = s.getTheme();
    }
    
    // Getters
    public Integer getMainWindowWidth() {
        return mainWindowWidth;
    }

    public Integer getMainWindowHeight() {
        return mainWindowHeight;
    }

    public String getStrategiesFolder() {
        return strategiesFolder;
    }
    
    public Path getStrategiesFolderPath() {
        return Paths.get(strategiesFolder);
    }

    public Theme getTheme() {
        return theme;
    }

    
    // Setters
    public void setMainWindowWidth(Integer mainWindowWidth) {
        if(!MainWindowSizes.validateWidth(requireNonNull(mainWindowWidth)) )
            throw new IllegalArgumentException("Width is not inside min and max main window bounds!");
        this.mainWindowWidth = mainWindowWidth;
    }

    public void setMainWindowHeight(Integer mainWindowHeight) {
        if(!MainWindowSizes.validateHeight(requireNonNull(mainWindowHeight)) )
            throw new IllegalArgumentException("Height is not inside min and max main window bounds!");
        this.mainWindowHeight = mainWindowHeight;
    }

    public void setStrategiesFolder(String strategiesFolder) {
        Path folder = Paths.get(strategiesFolder);
        if(!Files.exists(folder))
            throw new IllegalArgumentException("Invalid strategies folder path, it must be exist on file system!");
        if(!Files.isDirectory(folder))
            throw new IllegalArgumentException("Invalid strategies folder path, it must be a directory!");
        this.strategiesFolder = strategiesFolder;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Settings{");
        str.append(mainWindowWidth);
        str.append(',');
        str.append(mainWindowHeight);
        str.append(',');
        str.append(strategiesFolder);
        str.append(',');
        str.append(theme);
        str.append("}");
        return str.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(mainWindowWidth);
        out.writeInt(mainWindowHeight);
        out.writeUTF(strategiesFolder);
        out.writeObject(theme);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.mainWindowWidth = in.readInt();
        this.mainWindowHeight = in.readInt();
        this.strategiesFolder = in.readUTF();
        this.theme = (Theme) in.readObject();
        super.reachedEOF = false;
    }
    
}
