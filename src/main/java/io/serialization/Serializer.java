package io.serialization;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for classes which implements object persistence (Write/Read to/from disk)
 * @author Fedrix
 */
public interface Serializer {
    /**
     * Write object to disk
     * @param file path of the destination file
     * @param obj object to be written
     * @throws java.io.IOException
     */
    public abstract void serialize(Path file, Object obj) throws IOException;
    
    /**
     * Load an object from disk
     * @param file path of the source file
     * @return object read from file
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public abstract Object deserialize(Path file) throws IOException, ClassNotFoundException;
}
