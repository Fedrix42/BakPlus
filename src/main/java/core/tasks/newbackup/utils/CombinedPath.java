package core.tasks.newbackup.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Objects.requireNonNull;

/**
 * A class rappresenting a Path by a prefix and a postfix.
 * The prefix rappresents the first part of the path (closest to the root) and must be a folder.
 * The postfix rappresents the last part of the path and can be both a folder or a regular file.
 * The concatenation of prefix + postfix is the full path, this separation is useful for ZipCreator
 * in order to maintan structure in the ZipFile.
 * @see ZipCreator
 * @author Fedrix
 */
public class CombinedPath {
    private final Path prefix;
    private final Path postfix;
    private final Path full;
    
    public CombinedPath(Path prefix, Path postfix){
        requireNonNull(prefix);
        requireNonNull(postfix);
        if(!Files.isDirectory(prefix))
            throw new IllegalArgumentException("Prefix must rappresents a folder!");
        this.prefix = prefix;
        this.postfix = postfix;
        this.full = prefix.resolve(postfix);
    }
    
    public Path getPrefix(){
        return prefix;
    }
    
    public Path getPostfix(){
        return postfix;
    }
    
    public Path getFullPath(){
        return full;
    }
   

    @Override
    public String toString() {
        return "CombinedPath{" + prefix + "," + postfix + ", full=" + full + '}';
    }
    
    
    
}
