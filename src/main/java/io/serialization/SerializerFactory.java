package io.serialization;


/**
 * Singleton and factory design, there's no need to have more than one instance of every Serializer
 * @author Fedrix
 */
public final class SerializerFactory {
    public static Serializer objS = null;
    
    public static Serializer getObjectSerializer(){
        if(objS == null)
            objS = new ObjectSerializer();
        return objS;
    }
}
