package core.journal.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import logging.LoggerManager;

/**
 * Utils to calculate hash or compare hashes of two files
 * @author Fedrix
 */
public class HashUtils {
    public static final int CHUNK_SIZE = 1024*1024; // 1MB
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Compute SHA256 of a file
     * @param file
     * @return sha256 as a byte[]
     * @throws IOException 
     */
    public static byte[] sha256(Path file) throws IOException {
        if(!Files.isRegularFile(file) || Files.size(file) <= 0)
            throw new IllegalArgumentException("Argument must be of type file with length > 0.");
        
        MessageDigest digest;
        try {
             digest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            LoggerManager.exception().log(Level.SEVERE, "Inintented behavior." ,ex);
            return null;
        }
        try(var fs = new FileInputStream(file.toString())){
            byte[] data = new byte[CHUNK_SIZE];
            int bytesCount;
            while((bytesCount = fs.read(data)) != -1){
                digest.update(data, 0, bytesCount);
            }
        }
        return digest.digest();   
    }
    
    /**
     * Compute SHA256 of a file using only the number of bytes
     * @param file
     * @param bytes
     * @return sha256 as a byte[]
     * @throws IOException 
     */
    public static byte[] sha256(Path file, int bytes) throws IOException {
        if(!Files.isRegularFile(file) || Files.size(file) <= 0)
            throw new IllegalArgumentException("Argument must be of type file with length > 0.");
        MessageDigest digest;
        try {
             digest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            LoggerManager.exception().log(Level.SEVERE, "Inintented behavior." ,ex);
            return null;
        }
        try(var fs = new FileInputStream(file.toString())){
            byte[] data = new byte[bytes];
            int bytesCount = fs.read(data);
            digest.update(data, 0, bytesCount);
        }
        return digest.digest();   
    }
    
    /**
     * Convert a digest as byte[] to a String object
     * @param digest
     * @return string rappresentation of digest
     */
    public static String toString(byte[] digest){
        StringBuilder sb = new StringBuilder();
        for(byte b : digest){
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
