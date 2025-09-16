package core.strategy.validation;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A Target represents an item (Usually folder / file) which is subject of a backup
 * @author Fedrix
 */
public class Target extends EvaluablePath {
    private static final long serialVersionUID = 4L;
    
    /**
     * Needed to make the class Externalizable (Serializable)
     * Warning: do not use or the state of the class will be corrupted
     */
    public Target(){} 
    
    public Target(String path){
        super(path);
    }
    
    /**
     * This method checks if the target is valid, which means:
     * 1) The path of the target exists
     * 2) The path of the target is readble
     * @return true if all conditions are respected, false otherwise
     */
    @Override
    public List<Exception> validate(){
        validationFailures.clear();
        // Checking path existance
        Path file = super.getPath();
        if(!Files.exists(file)){
            validationFailures.add(new FileNotFoundException("The target does not exists!"));
        } else {
            // Checking path read permissions
            if(!Files.isReadable(file)){
                validationFailures.add(new AccessDeniedException("The path of the target does not have read permissions!"));
            }
        }
        return validationFailures;
    }
    
}
