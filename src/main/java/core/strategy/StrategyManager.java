package core.strategy;

import core.journal.manager.JournalIOManager;
import core.strategy.exception.StrategiesLoadingException;
import core.strategy.exception.StrategyAlreadyExistsException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import settings.SettingsManager;
import io.serialization.SerializerFactory;
import java.util.logging.Level;
import logging.LoggerManager;
import utils.formatters.FilenameFormats;

/**
 * Load from filesystem and stores all the strategies
 * A central autorithy mantaining state of strategies
 * @author Fedrix
 */
public final class StrategyManager {
    private static final List<Strategy> sts = new LinkedList<>(); // List of strategies
    private static Strategy currentlyLoaded = null;
    
    
    /**
     * Load strategies from strategies folder
     * @throws StrategiesLoadingException containing all exceptions which happened during loading
     */
    public static void loadFromDisk() throws StrategiesLoadingException {
        Path stratFolder = SettingsManager.getCurrent().getStrategiesFolderPath();
        
        StrategiesLoadingException exContainer = new StrategiesLoadingException();
        if(!Files.isDirectory(stratFolder) || !Files.isReadable(stratFolder))
            exContainer.addThrowable(new IOException("Could not access strategies folder."), "Check strategies folder read/write permissions.");
        Iterator<Path> it;
        try {
            it = Files.list(stratFolder).iterator();
        } catch (IOException ex) {
            exContainer.addThrowable(ex, "Could not iterate through strategies folder.");
            throw exContainer;
        }
        
        while(it.hasNext()){
            Path next = it.next();
            if(FilenameFormats.STRATEGY_PATTERN.matcher(next.getFileName().toString()).matches()){
                try {
                    Strategy deserialized = (Strategy) SerializerFactory.getObjectSerializer().deserialize(next);
                    if(deserialized.reachedEOF){
                        LoggerManager.console().log(Level.INFO, "Deserialized strategy reached EOF, serializing new version");
                        SerializerFactory.getObjectSerializer().serialize(next, deserialized);
                    }
                    sts.add(deserialized);
                    
                } catch (ClassNotFoundException | IOException e){
                    exContainer.addThrowable(e, next.toString());
                }
            }
        }
        
        if(!exContainer.getExceptions().isEmpty())
            throw exContainer;
    }
    

    
    /**
     * Write the strategy to the disk
     * @param s an existing strategy
     * @throws NoSuchElementException if s is not a found inside strategies collection
     * @throws IOException containing serialization details if some errors happened during serialization
     */
    public static void writeToDisk(Strategy s) throws NoSuchElementException, IOException {
        if(!sts.contains(s))
            throw new NoSuchElementException("Strategy not found. Check the input name.");
        
        SerializerFactory.getObjectSerializer().serialize(StrategyPath.get(s), s);
    }
    
    /**
     * Add a new strategy s and save it to file system
     * @param strategy
     * @throws StrategyAlreadyExistsException if a strategy with the same name already exists
     * @throws java.io.IOException if an error happened during serialization
     * @throws java.io.NotSerializableException if an error happened during serialization
     * @throws java.io.InvalidClassException if an error happened during serialization
     */
    public static void add(Strategy strategy) throws StrategyAlreadyExistsException, IOException {
        assert(strategy != null);
        
        /* The ID should be unique for every strategy in sts.
           The probability of 2 IDs being equals is really low, but just in case 5 attemps are made to generate a new unique id.
           After 5 attemps there is probably an issue in the way IDs are generated, an exception is thrown.
        */
        int MAX_ATTEMPS = 5, i = 1;
        while(sts.contains(strategy) && i <= MAX_ATTEMPS){
            strategy.generateNewID();
            i++;
        }
        if(sts.contains(strategy)) throw new StrategyAlreadyExistsException("Something is wrong with generating unique strategy id, max attemps reached(%d)".formatted(MAX_ATTEMPS));
        SerializerFactory.getObjectSerializer().serialize(StrategyPath.get(strategy), strategy);
        sts.add(strategy);
        JournalIOManager.createJournalsDirectory(strategy.getId());
    }
    
    /**
     * Delete a existing strategy both from memory and disk.
     * If an IO error happen then the strategy is not deleted.
     * The strategy object is not affected but it should not be
     * considered a valid strategy anymore.
     * @param strategy 
     * @throws IOException if the strategy could not be deleted from disk
     */
    public static void delete(Strategy strategy) throws IOException {
        assert(strategy != null);
        assert(sts.contains(strategy));
        Files.delete(StrategyPath.get(strategy));
        sts.remove(strategy);
        JournalIOManager.deleteJournalsDirectory(strategy.getId());
    }
    
    /**
     * @param id identifier of the strategy
     * @return the strategy with a certain id
     */
    public static Strategy get(long id){
        for(Strategy s : sts){
            if(s.getId() == id)
                return s;
        }
        return null;
    }
    
    
    /**
     * Get a copy of the strategy with a certain id
     * @param id identifier of the strategy
     * @return null if the strategy is not found
     */
    public static Strategy getCopy(long id){
        for(Strategy s : sts){
            if(s.getId() == id)
                return new Strategy(s);
        }
        return null;
    }
    
    /**
     * @return a copy to the list of strategies loaded by file system.
     * Changes made to the returned list do not affect the list used by this class,
     * but changes made to the single strategies inside the list are reflected to the
     * global strategies.
     */
    public static List<Strategy> getStrategiesCopy(){
        return List.copyOf(sts);
    }
    
    
    /**
     * @return the number of loaded strategies
     */
    public static int size(){
       return sts.size();
    }
    
    
    /**
     * Load a strategy to make it available to use for the user
     * @param newCurrent
     */
    public static void loadCurrent(Strategy newCurrent){
        if(newCurrent != null){
            currentlyLoaded = newCurrent;
        } else throw new IllegalArgumentException("Passed argument must not be null, use unloadCurrent to unload a strategy!");
    }
    
    
    /**
     * Unset the current strategy
     */
    public static void unloadCurrent(){
        currentlyLoaded = null;
    }
    
    /**
     * @return true if a current loaded strategy is set
     */
    public static boolean isCurrentLoaded(){
        return (currentlyLoaded != null);
    }
    
    /**
     * Get the currently loaded strategy
     * @return the strategy if there is a loaded one, null otherwise
     */
    public static Strategy getCurrentlyLoaded() {
        return currentlyLoaded;
    }
    
}
