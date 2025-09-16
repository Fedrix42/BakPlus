package core.journal.utils;

import io.serialization.StatedExternalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Checksum define a file for the journal.
 * Since filename and other data about a file can be duplicate, we use a solid checksum to assure file uniqueness. 
 * (Even if there is a little chance two different files share the same checksum).
 * 
 * To improve performance when comparing different files the comparison is being made
 * gradually on chunks of the file to avoid reading all of it.
 * 
 * Serialization
 * This class implements Externalizable in order to perform explicit serialization 
 * using read and write external methods. UPDATE these methods when the class is 
 * changed to assure compatibility through different software versions!
 * 
 * 
 * 
 * @author Fedrix
 */
public class Checksum extends StatedExternalizable {
    private static final long serialVersionUID = 9L;
    private transient final int[] chunksSize = {1024, HashUtils.CHUNK_SIZE};
    private LinkedList<byte[]> sha256;
    
    /**
     * Needed to make the class Externalizable (Serializable)
     * Warning: do not use or the state of the class will be corrupted
     */
    public Checksum(){}
    
    
    public Checksum(Path file) throws IOException {
        sha256 = new LinkedList<>();
        long fileLength = Files.size(file);
        if(fileLength <= 0)
            return;
        for(int size : chunksSize){ // SHA of small chunks
            sha256.add(HashUtils.sha256(file, size));
            if(fileLength <= size) // Useless to continue if we already sha all the file
                break;
        }
        
        // SHA of complete file if file size is bigger than largest chunk
        if(fileLength > chunksSize[chunksSize.length - 1])
            sha256.add(HashUtils.sha256(file));
    }
    
    
    /**
     * Check if a checksum is the one of a file
     * @param file
     * @return 
     * @throws java.io.IOException 
     */
    public boolean isOf(Path file) throws IOException {
        if(Files.size(file) == 0){
            return sha256.isEmpty();
        } else {
            int sha256Size = sha256.size();
            if(sha256Size > 0){ // File not empty and sha list not empty
                for(int i = 0; i < sha256Size - 1; ++i){ // Comparing sha of every chunk size
                    int size = chunksSize[i];
                    if(!Arrays.equals(sha256.get(i), HashUtils.sha256(file, size)))
                        return false;
                }
                return Arrays.equals(sha256.getLast(), HashUtils.sha256(file)); // Comparing entire file
            } else
                return false; // File not empty but sha list empty
        }
    }
    
    /**
     * @return the complete sha256 of a file as a string, none is there is no sha256 (File is empty)
     */
    public String lastToString(){
        if(sha256.isEmpty())
            return "none";
        return HashUtils.toString(sha256.getLast());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        else if(obj instanceof Checksum toCompare){
            return this.sha256.equals(toCompare.sha256);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return sha256.hashCode();
    }
    
    
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(byte[] sha : sha256){
            sb.append(HashUtils.toString(sha));
            sb.append(" - ");
        }
        sb.append("]");
        return sb.toString();
    } 

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(sha256);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.sha256 = (LinkedList<byte[]>) in.readObject();
        super.reachedEOF = false;
    }
    
    
    
}
