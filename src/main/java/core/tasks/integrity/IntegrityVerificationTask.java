package core.tasks.integrity;

import core.tasks.ProgressTask;
import core.tasks.MonitoredTaskDecorator;
import core.journal.Journal;
import core.tasks.progress.integrity.IntegrityEntry;
import core.tasks.progress.integrity.IntegrityProgress;
import core.journal.utils.Checksum;
import init.Environment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.stream.Stream;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import logging.LoggerManager;
import utils.Pair;

/**
 * Checks the integrity of created backup using journals
 * @author Fedrix
 */
public class IntegrityVerificationTask extends MonitoredTaskDecorator {
    private static final Path tmpFolder = Environment.getTmpFolder();
    protected final Journal journal;
    protected final List<Pair<Path,char[]>> backups; // List of (BackupFile, Password), password not used if backup is encrypted
    private final IntegrityProgress progress;
    private Path extractedPath; // Tmp path where backup is extracted
    private long totalSize = 1; // Sum of sizes of backups
    private int remaining; // Remaining files in journal which are in backups
   
    public IntegrityVerificationTask(ProgressTask engine, Journal journal, List<Pair<Path,char[]>> backups) {
        super(engine);
        progress = new IntegrityProgress(engine.getProgress());
        this.journal = requireNonNull(journal);
        this.backups = requireNonNull(backups);
    }
    
    private void checkInterrupt() throws InterruptedException{
        if(Thread.interrupted()) throw new InterruptedException("Execution has been interrupted");
    }

    private void extract(Path backupFile, char[] key) throws ZipException{
        ZipFile zip = new ZipFile(backupFile.toString());
        if(zip.isEncrypted())
            zip = new ZipFile(backupFile.toString(), key);
        extractedPath = tmpFolder.resolve("extracted-" + backupFile.getFileName());
        progress.setTask("Extracting at %s".formatted(extractedPath));
        zip.extractAll(extractedPath.toString());
    }
    
    private void clearExtractedFolder(){
        try (Stream<Path> walk = Files.walk(extractedPath)) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException ex){
            LoggerManager.exception().log(Level.WARNING, "Couldn't delete tmp folder used for zip extraction in integrity check", ex);
        }
    }
    
    private void backupTraversal(Path path) throws IOException, InterruptedException{
        checkInterrupt();
        if(Files.exists(path)){
            if(Files.isDirectory(path)){
                Iterator<Path> it = Files.list(path).iterator();
                while(it.hasNext()){
                    backupTraversal(it.next());
                }
            } else if(Files.isRegularFile(path)){
                Pair<Boolean, Checksum> result = journal.has(path);
                if(result.first()){
                    remaining--;
                    progress.queue(new IntegrityEntry(path.getFileName().toString(), result.second().lastToString(), result.first(), ""));
                }else {
                    progress.completePass = false;
                    progress.queue(new IntegrityEntry(path.getFileName().toString(), "none", result.first(), "Possible file corruption"));
                }
                progress.setProgress(100 - ((100 * remaining) / totalSize));
            }
        }
    }
    
    @Override
    public void run() {
        super.run();
        if(!progress.isException()){
            try {
                remaining = journal.size();
                totalSize = journal.size();
                for(var pair : backups){
                    checkInterrupt();
                    progress.setTask("Extracting backup file %s ".formatted(pair.first()));
                    extract(pair.first(), pair.second());
                    if(extractedPath != null){
                        progress.setTask("Verifying extracted data");
                        backupTraversal(extractedPath);
                        progress.setTask("Cleaning tmp folder");
                        clearExtractedFolder();
                    }
                }
                if(remaining > 0){
                    progress.completePass = false;
                    progress.queue(new IntegrityEntry(remaining + " entries in journal not found in backups!", "none", false, "Verification FAILED"));
                }
            } catch(InterruptedException ex){
                progress.setTask(this.getClass().getSimpleName() + " interrupted!");
            } catch(IOException ex){
                progress.setException(ex);
                progress.setTask(this.getClass().getSimpleName() + " ended with errors.");
            }
            progress.setProgress(100);
            progress.setTask(this.getClass().getSimpleName() + " completed.");
        }
    }

    @Override
    public IntegrityProgress getProgress() {
        return progress;
    }
    
    
    
    
}
