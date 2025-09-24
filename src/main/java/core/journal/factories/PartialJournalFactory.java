package core.journal.factories;

import core.journal.Journal;
import core.journal.JournalType;
import core.tasks.newbackup.utils.CombinedPath;
import core.strategy.Strategy;
import core.strategy.StrategyManager;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build a journal by computing difference between last journal found on file system
 * and files of the currently loaded strategy.
 * @author Fedrix
 */
public class PartialJournalFactory  {
    private static List<CombinedPath> filesList; /* filesList.get(i) is a regular file */
    private static Journal completeJ;
    private static Journal journal;
    
    @FunctionalInterface
    private interface CheckedFunction<T, U, R> {
       R apply(T t, U u) throws IOException;
    }
    
    private static void targetTraversal(Path targetRoot, Path postfix, CheckedFunction<Journal, Path, Boolean> fileCheck, DirectoryStream.Filter<Path> filter, int level) throws IOException{
        Path file = targetRoot.resolve(postfix);
        if(Files.isRegularFile(file)){
            /**
             * If level is zero then we must check the filter against the file since
             * the filtration at directory stream level has not been done. 
             * If level is > 0 then the filtration is done down in the method using newDirectoryStream()
             */
            
            if(level == 0){
                if(!filter.accept(file)){
                    return;
                }
            }
            if(fileCheck.apply(completeJ, file)){
                journal.add(file);
                filesList.add(new CombinedPath(targetRoot, postfix));
            }
        } else if(Files.isDirectory(file)){
            for (Path dirEntry : Files.newDirectoryStream(file, filter)) {
                targetTraversal(targetRoot, 
                        dirEntry.subpath(targetRoot.getNameCount(), dirEntry.getNameCount()), //Subpath from the end of target root to filename
                        fileCheck,
                        filter,
                        level + 1
                );
            }
        }
    }
    
    
    
    
    /**
     * Build a partial journal by comparing the targets of the currently loaded strategy with a provided complete journal.
     * The partial journal contains all new and modified files in the targets since the complete journal.
     * @param completeJ
     * @return
     * @throws IOException if an error is encountered accessing the targets
     */
    public static Journal make(Journal completeJ) throws IOException {
        return make(completeJ, null);
    }
    
    /**
     * Same as make(Journal) but the files are filtered by the pattern list passed as parameter.
     * If one or more pattern matches the filename of a file then it is excluded in the journal.
     * @throws java.io.IOException if an error is encountered accessing the targets
     * @see make(Journal)
     * @param completeJ
     * @param exclusiveFilenamePattern can be null, does not apply any filter
     * @return 
     */
    public static Journal make(Journal completeJ, List<Pattern> exclusiveFilenamePattern) throws IOException {
        Strategy cls = StrategyManager.getCurrentlyLoaded();
        requireNonNull(completeJ);
        requireNonNull(cls);
        PartialJournalFactory.completeJ = completeJ;
        PartialJournalFactory.journal = new Journal(cls, JournalType.PARTIAL);
        PartialJournalFactory.filesList = new LinkedList<>();
        List<Path> targets = cls.getTargetsPathList();
        
        // Setting up filters
        CheckedFunction<Journal, Path, Boolean> check;
        if(completeJ.isEmpty()){
            check = (Journal t, Path u) -> true;
        } else {
            check = (Journal completeJournal, Path file) -> !completeJournal.has(file).first();
        }
        DirectoryStream.Filter<Path> exclusiveFilenameFilter;
        if(exclusiveFilenamePattern != null){
            exclusiveFilenameFilter = (Path file) -> {
                for(Pattern pattern : exclusiveFilenamePattern){
                    if(pattern.matcher(file.getFileName().toString()).matches())
                        return false;
                }
                return true;
            };
        } else {
            exclusiveFilenameFilter = (Path file) -> true;
        }
        
        // Traversal
        for(var target : targets)
            targetTraversal(target.getParent(), target.getFileName(), check, exclusiveFilenameFilter, 0);
        
        return journal;
    }
    
    
    /**
     * 
     * @return a list containing all new and modified files which have been found when the make() method was called. 
     * Every CombinedPath of the list must be a regular file (Not a directory).
     * @see make()
     */
    public static List<CombinedPath> asFiles(){
        if(filesList == null)
            throw new IllegalStateException("Files structure is not inizialize! Call make() method first!");
        return filesList;
    }
}
