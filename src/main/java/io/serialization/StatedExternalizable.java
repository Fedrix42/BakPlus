package io.serialization;

import java.io.Externalizable;

/**
 * An Externalizable object which store informations about events
 * that happened during serialization / deserialization.
 * 
 * This is useful because if a new version of the program is released there 
 * is the possibility of a serialized object being upgraded. It is required for the
 * program to know if this upgrade happened.
 * 
 * Ex. New release add features and attributes to Strategy class, user download
 * new release and during deserialization the new attributes of the Strategy class object
 * are initialized as default. In a condition like this we want to know if this new inizialization
 * happen in order to maybe write to disk the new version of the Strategy object with the new attributes.
 * @author Fedrix
 */
public abstract class StatedExternalizable implements Externalizable {
    /**
     * Flag which must be set if a EOF is reached during deserialization.
     * This is an indicator that the stored object was an old version.
     * 
     * This attribute should only be set during readExternal() and writeExternal()
     * methods of the class which extends StatedExternalizable.
     */
    public boolean reachedEOF = false;
}
