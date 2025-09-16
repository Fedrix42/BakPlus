package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.Pair;

/**
 * Search for a file matching the specified pattern
 * @author Fedrix
 */
public class FilePatternFinder {
    
    /**
     * Search for a files in a folder where the filename matches the specified pattern
     * @param folder
     * @param pattern
     * @return a list containing entries that matches the patterns or an empty list if there is no one
     * @throws IOException if there is a reading problem accessing folder entries or entries metadata
     * @throws IllegalArgumentException
     */
    public static List<Path> search(Path folder, Pattern pattern) throws IOException{
        if(folder == null || pattern == null)
            throw new IllegalArgumentException("Folder and Pattern arguments must not be null.");
        if(!Files.exists(folder))
            throw new IllegalArgumentException("Folder argument must rappresent an existing folder.");
        if(!Files.isDirectory(folder))
            throw new IllegalArgumentException("Folder argument must be a folder (Files.isDirectory check must pass).");
        List<Path> res = new LinkedList<>();
        Iterator<Path> it = Files.list(folder).iterator();
        while(it.hasNext()){
            Path next = it.next();
            Matcher mt = pattern.matcher(next.getFileName().toString());
            if(mt.matches()){ // Match is before isRegularFile to avoid useless file system metadata reads
                if(Files.isRegularFile(next)){
                    res.add(next);
                }
            }
        }
        return res;
    }
    
    /**
     * Search for a files in a folder where the filename matches the specified pattern and
     * the regex group matches the group value
     * @param folder
     * @param pattern
     * @param groupName
     * @param groupValue
     * @return a list containing the file paths which filenames matched the pattern and the group matched the group value
     * @throws IOException if there is a reading problem accessing folder entries or entries metadata
     * @throws IllegalArgumentException
     */
    public static List<Path> search(Path folder, Pattern pattern, String groupName, String groupValue) throws IOException {
        if(folder == null || pattern == null || groupName == null)
            throw new IllegalArgumentException("Folder, Pattern and groupName arguments must not be null.");
        if(!Files.exists(folder))
            throw new IllegalArgumentException("Folder argument must rappresent an existing folder.");
        if(!Files.isDirectory(folder))
            throw new IllegalArgumentException("Folder argument must be a folder (Files.isDirectory check must pass).");
        List<Path> res = new LinkedList<>();
        Iterator<Path> it = Files.list(folder).iterator();
        while(it.hasNext()){
            Path next = it.next();
            Matcher mt = pattern.matcher(next.getFileName().toString());
            if(mt.matches()){ 
                String matchedGroup = mt.group(groupName);
                if(matchedGroup != null){
                    if(matchedGroup.equals(groupValue)){ // Matches are made before isRegularFile call to avoid useless file system metadata reads
                        if(Files.isRegularFile(next)){
                            res.add(next);
                        }
                    }
                }
            }
        }
        return res;
    }
    
}
