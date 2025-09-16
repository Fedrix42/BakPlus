package core.strategy.validation;


import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A Destination represents the destination where the backup is stored (Usually a folder).
 * @author Fedrix
 */
public class Destination extends EvaluablePath {
    private static final long serialVersionUID = 5L;

    /**
     * Needed to make the class Externalizable (Serializable)
     * Warning: do not use or the state of the class will be corrupted
     */
    public Destination(){} 
    
    public Destination(String path) {
        super(path);
    }

    /**
     * This method checks if the destination is valid, which means:
     * 1) The path of the destination exists
     * 2) The path of the destination is a folder
     * 3) The path of the destination is readble and writable
     * Then fills the validationFailures List with generated exceptions
     * @return true if all conditions are respected, false otherwise
     */
    @Override
    public List<Exception> validate() {
        validationFailures.clear();
        Path file = super.getPath();
        // Checking path existance
        if(!Files.exists(file)){
            validationFailures.add(new FileNotFoundException("The destination does not exists!"));
        }
        else {
            if(!Files.isDirectory(file)){
                validationFailures.add(new NotDirectoryException("The destination is not a folder!"));
            }
            else {
                // Checking path read permissions
                if(!Files.isReadable(file)){
                    validationFailures.add(new AccessDeniedException("The path of the destination folder does not have read permissions!"));
                }
                // Checking path write permissions
                if(!Files.isWritable(file)){
                    validationFailures.add(new AccessDeniedException("The path of the destination folder does not have write permissions!"));
            }
        }
        }
        return validationFailures;
    }
    
    
    
    
}
