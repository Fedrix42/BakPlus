package core.tasks.newbackup;

import core.Compression;
import static core.Compression.Deflate;
import static core.Compression.None;
import core.Encryption;
import core.tasks.newbackup.utils.CombinedPath;
import static core.Encryption.AES256;
import static core.Encryption.ZIP_STANDARD;
import core.journal.manager.JournalIOManager;
import core.journal.manager.JournalRollbackManager;
import core.tasks.ProgressTask;
import utils.sysinfo.DiskSpeedBenchmark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

/**
 * Create an incremental backup zip file based on journal system
 * @author Fedrix
 */
public class IncrementalZipCreationTask extends IncrementalJournalInizializationTask {
    private static ZipFile zip;
    private static ZipParameters param;
    private static long totalSize = 0; // Sum of each file size in files list
    
    public IncrementalZipCreationTask(ProgressTask engine, char[] key) {
        super(engine, key);
    }

    /**
     * Check if the limit of the incremental backups has been reached.
     * If the new backup would exceed the limit then the new backup must be
     * a complete backup of all files in target. This is accomplished by deleting
     * the journals.
     */
    private void checkIncrementalLimit(){
        try {
            int numOfPartialJournals = JournalIOManager.getAllPartialJournals(cls.getId()).size();
            if(numOfPartialJournals + 1 > cls.getIncLimit()){ // The new partial backup would exceed the limit
                JournalRollbackManager.deleteAllJournals(cls.getId());
            }
        } catch (IOException | ClassNotFoundException ex) {
            progress.setException(ex);
            progress.setTask(this.getClass().getSimpleName() + ": errors during incremental limit check");
        }
    }
    
    
    
    @Override
    public void run() {
        super.run();
        if(!progress.isException()){
            try {
                if(!toBackupFiles.isEmpty()){
                    progress.setTask("Checking if incremental limit has been reached...");
                    checkIncrementalLimit();
                    progress.setTask("Creating zip file");
                    create();
                    progress.setTask("Trying to make new journals persistent in file system");
                    JournalIOManager.makePersistent(difference);
                    JournalIOManager.makePersistent(found.union(difference));
                    progress.setTask("Operation completed successfully.");
                } else
                    progress.setTask("Nothing to backup, stopping...");
                progress.setProgress(100);
            } catch(IOException ex){
                progress.setException(ex);
                progress.setTask("Operation ended with errors while making new journals persistent. Backup cannot be considered valid!");
            } catch (InterruptedException ex) {
                progress.setTask("Operation interrupted.");
            }
        }
    }
    
    
    /**
     * Start the creation of a new zip archive.
     * Estimate the current progress by elapsed time and a benchmark of hardware performance per second.
     */
    private void create() throws InterruptedException, IOException {
        // Setting up parameters for zip file and zip file object
        param = new ZipParameters();
        setParam(backupFilename, cls.geteMode(), cls.getcMode(), key);
        assert(zip instanceof ZipFile);
        
        // Setting up things needed for progress monitoring
        progress.setTask("Calculating backup file sizes");
        computeTotalSize();
        long computeSpeed = DiskSpeedBenchmark.writeLowerBound(); // bytes per second
        Instant startTime = Instant.now();
        
        // Adding files to zip object
        progress.setTask("Add File");
        for(CombinedPath cPath : toBackupFiles){
            if(Thread.interrupted()) throw new InterruptedException("Execution has been interrupted");
            
            // Calculating paths for zip structure
            Path postFixParent = cPath.getPostfix().getParent();
            if(postFixParent != null)
                param.setRootFolderNameInZip(postFixParent.toString());
            
            Path entryFull = cPath.getFullPath();
            assert(Files.isRegularFile(entryFull));
            
            progress.setFile(entryFull.toString());
            try {
                zip.addFile(entryFull.toFile(), param);
            } catch (ZipException ex){
                difference.removeFile(entryFull);
                progress.setException(ex);
            }
            
            long computedBytes = ( (Instant.now().getEpochSecond() - startTime.getEpochSecond()) *  computeSpeed);
            if(computedBytes < totalSize) //An estimate is an estimate at the end of the day...
                progress.setProgress((computedBytes * 100) / totalSize);

        }
        
    }
    
    
    private void computeTotalSize() throws IOException, InterruptedException{
        for(CombinedPath file : toBackupFiles){
            if(Thread.interrupted()) throw new InterruptedException("Execution has been interrupted");
            totalSize += Files.size(file.getFullPath());
        }
    }
    
    /**
     * Set 'param' attribute and create instance for 'zip' reference attribute
     */
    private void setParam(Path zipFilename, Encryption eMode, Compression cMode, char[] key){
        switch(cMode){
            case None -> param.setCompressionMethod(CompressionMethod.STORE);
            case Deflate -> param.setCompressionMethod(CompressionMethod.DEFLATE);
            default -> param.setCompressionMethod(CompressionMethod.STORE);
        }
        switch(eMode){
            case AES256 -> {
                param.setEncryptFiles(true);
                param.setEncryptionMethod(EncryptionMethod.AES);
                param.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                zip = new ZipFile(zipFilename.toString(), key);
            }
            case ZIP_STANDARD -> {
                param.setEncryptFiles(true);
                param.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                zip = new ZipFile(zipFilename.toString(), key);
            }
            default -> {
                param.setEncryptFiles(false);
                zip = new ZipFile(zipFilename.toString());
            }

        }
    }

    
    
}
