package utils.sysinfo;

import init.Environment;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Random;

/**
 * Useful to make predictions about progress being made during backup / file creation
 * @author Fedrix
 */
public class DiskSpeedBenchmark {
    private static final Path tmpFile = Environment.getTmpFolder().resolve("bkp101diskbench.tmp");
    public static final long MB_UNIT = 1024*1024;
    public static final long DEFAULT_WRITE_LOWER = 10*MB_UNIT;
    
    private static char[] randomCharArray(int size){
        Random r = new Random();
        char[] random  = new char[size];
        for(int i = 0; i < size; ++i){
            random[i] = (char) r.nextInt(0, 255);
        }
        return random;
    }
    
    /**
     * @return lower bound for an estimated bytes per second for disk speed write
     */
    public static long writeLowerBound(){
        Instant start = Instant.now();
        final int chunks = 100;
        final int chunksSize = (int) (MB_UNIT);
        try(FileWriter fw = new FileWriter(tmpFile.toFile())){
            for(int i = 0; i < chunks; ++i)
                fw.write(randomCharArray(chunksSize));
            long secondsPassed = Instant.now().getEpochSecond() - start.getEpochSecond();
            long nanoPassed = Instant.now().getNano() - start.getNano();
            double preciseSecondsPassed = secondsPassed + nanoPassed*Math.pow(10, -9);
            return (long) ((chunks*chunksSize) / preciseSecondsPassed);
        } catch (IOException ex){
            return DEFAULT_WRITE_LOWER;
        }
    }
}
