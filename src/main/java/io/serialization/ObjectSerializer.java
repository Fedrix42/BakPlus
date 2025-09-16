package io.serialization;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;


/**
 * Serialize objects using standard java object serialization (Object Output/Input Streams)
 * @author Fedrix
 */
public final class ObjectSerializer implements Serializer {
    /**
     * Tries to serialize object using ObjectOutputStream
     * @param file
     * @param obj
     * @throws IOException
     * @throws NotSerializableException
     * @throws InvalidClassException 
     */
    @Override
    public void serialize(Path file, Object obj) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toString()))){
            out.writeObject(obj);
        }
    }

    
    /**
     * Tries to deserialize object using ObjectInputStream
     * @param file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public Object deserialize(Path file) throws IOException, ClassNotFoundException {
        Object ret;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toString()))){
            ret = in.readObject();
        }
        return ret;
    }
    
}
