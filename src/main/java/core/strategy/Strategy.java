package core.strategy;

import java.util.LinkedList;
import java.util.List;
import utils.Pair;
import core.Compression;
import core.Encryption;
import core.strategy.exception.StrategyInvalidNameException;
import core.strategy.validation.Destination;
import core.strategy.validation.Target;
import io.serialization.StatedExternalizable;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Path;
import static java.util.Objects.requireNonNull;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * A Backup Strategy design the way a backup is made
 * - Backup mode (Compression, encryption, ...)
 * - List of targets (files, folders) to be back-upped
 * - Destination folder of the backup
 * 
 * 
 * Serialization
 * This class implements Externalizable in order to perform explicit serialization 
 * using read and write external methods. UPDATE these methods when the class is 
 * changed to assure compatibility through different software versions!
 * 
 * 
 * @author Fedrix
 */
public class Strategy extends StatedExternalizable {
    private static final long serialVersionUID = 3L;
    public static transient final String NAME_REGEX = "(?<stgname>[a-zA-Z]\\w*)";
    public static transient final String ID_REGEX = "(?<stgid>[0-9]+)";
    public static transient final int MIN_INCREMENTAL_LIMIT = 0;  // Incluse
    public static transient final int MAX_INCREMENTAL_LIMIT = 10; // Incluse
    /* Invariant: Every attribute cannot be null */
    private long id; /* Unique identifier for strategy, Invariant: id >= 0 */
    private String name;
    private List<Target> targets; 
    private Destination dst;
    private Encryption eMode;
    private Compression cMode;
    /**
     * List of pattern which will be use to filter file / folder during a backup.
     */
    private List<Pattern> filters;
    
    /** 
     * Incremental limit for partial backups, after incLimit partial backups a complete one is done 
     * This limit is necessary for performance reason, check online/offline reasources to better understand.
     * 
     * Invariant: MIN_INCREMENTAL_LIMIT <= incLimit <= MAX_INCREMENTAL_LIMIT
     */
    private int incLimit = MAX_INCREMENTAL_LIMIT; // Default value

    
    private int incLimitInvariant(int limit){
        if(limit >= MIN_INCREMENTAL_LIMIT && limit <= MAX_INCREMENTAL_LIMIT)
            return limit;
        else
            throw new IllegalArgumentException("Incremental limit must be between %d and %d".formatted(MIN_INCREMENTAL_LIMIT, MAX_INCREMENTAL_LIMIT));
    }
    
    private long getRandomID(){
        return new Random().nextLong(0, Long.MAX_VALUE);
    }
    
    /**
     * Needed to make the class Externalizable (Serializable)
     * Warning: do not use or the state of the class will be corrupted
     */
    public Strategy(){}   
    
    /**
     * @param incLimit
     * @see class description in documentation for what a strategy is
     * @param name unique name of the strategy
     * @param dstPath destination folder of the strategy
     * @param targets targets which will be back-upped
     * @param eMode
     * @param cMode 
     * @param filters
     * @throws core.strategy.exception.StrategyInvalidNameException if name is not valid
     */
    public Strategy(String name, String dstPath, List<Target> targets, 
            Encryption eMode, Compression cMode, List<Pattern> filters, int incLimit) throws StrategyInvalidNameException {
        this.id = getRandomID();
        this.name = requireNonNull(name);
        if(!name.matches(Strategy.NAME_REGEX))
            throw new StrategyInvalidNameException();
        this.eMode = requireNonNull(eMode);
        this.cMode = requireNonNull(cMode);
        this.targets = requireNonNull(targets);
        dst = new Destination(requireNonNull(dstPath));
        this.filters = requireNonNull(filters);
        this.incLimit = incLimitInvariant(incLimit);
    }
    
    
    
    /**
     * Copy constructor
     * @param s 
     */
    public Strategy(Strategy s){
        this.setAll(s);
    }
    
    
    
    @Override
    /**
     * Two strategies are considered equals if they share the same id
     */
    public boolean equals(Object o){
        if(this == o){
            return true;
        } else if(o instanceof Strategy s){
            return (this.id == s.id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    /* Static validations */
    
    /**
     * Statically checks if all targets of the strategy have the right access permissions
     * @return a list of Pairs (Path of target - List of exception not empty if target is not valid)
     */
    public List<Pair<String, List<Exception>>> validateTargets(){
        LinkedList<Pair<String, List<Exception>>> result = new LinkedList<>();
        for(Target t : targets){
            result.add(new Pair<>(t.getPathString(), t.validate()));
        }
        return result;
    }
    
    
    /**
     * Statically checks if the destination of the strategy have the right access permissions
     * @return an empty list is the destination is valid, a list filled with exception otherwise
     */
    public List<Exception> validateDestination(){
        return dst.validate();
    }

    
    
    
    
    /* Getters */
    
    /** @return the unique identifier of the strategy */
    public long getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public List<Target> getTargetsCopy(){
        return List.copyOf(targets);
    }
    
    
    /** @return a copy of the targets list */
    public List<Path> getTargetsPathList() {
        LinkedList<Path> result = new LinkedList<>();
        targets.forEach((t) -> result.add(t.getPath()));
        return result;
    }

    /** @return the destination as a string */
    public String getDst() {
        return dst.getPathString();
    }
    
    /** @return the destination as a path */
    public Path getDestinationFolder(){
        return dst.getPath();
    }
    
    /** @return the encryption mode */
    public Encryption geteMode() {
        return eMode;
    }

    /** @return  the compression mode */
    public Compression getcMode() {
        return cMode;
    }
    
    /** @return the list of patterns which are used as exclusion filters during a backup */
    public List<Pattern> getFiltersCopy() {
        return List.copyOf(filters);
    }
    
    /** @return the limit of partial backups after which a complete one is done */
    public int getIncLimit() {
        return incLimit;
    }
    
    
    
    
    /* Setters */
    /**
     * Set all attributes of this strategy as a copy of the attributes of the passed strategy
     * Cost of function is linear: size(targets) + size(filters)
     * @param s 
     */
    public final void setAll(Strategy s){
        this.id = s.id;
        this.name = s.name;
        this.eMode = s.eMode;
        this.cMode = s.cMode;
        this.targets = List.copyOf(s.getTargetsCopy());
        this.dst = new Destination(s.getDst());
        this.filters = List.copyOf(s.getFiltersCopy());
        this.incLimit = s.incLimit;
    }
    
    /**
     * Generate a new ID for the strategy.
     * This should be unique across all the loaded strategies, it is
     * responsability of the StrategyManager to maintain this invariant.
     * @see StrategyManager
     */
    void generateNewID(){
        this.id = getRandomID();
    }
    
    /**
     * Set the name of the strategy
     * @param name
     * @throws StrategyInvalidNameException if the name regex does not match
     */
    public void setName(String name) throws StrategyInvalidNameException {
        System.out.println("A");
        if(!name.matches(Strategy.NAME_REGEX))
            throw new StrategyInvalidNameException();
        this.name = name;
    }
    
    /**
     * Set the targets of the strategy
     * A reference to a copy of the passed list it's used so changes
     * to the passed list are not reflected to the strategy list.
     * @param newTargets 
     */
    public void setTargets(List<Target> newTargets) {
        this.targets = List.copyOf(newTargets);
    } 

    /**
     * Set the destination of the strategy
     * @param newPath 
     */
    public void setDst(String newPath) {
        dst.setPath(requireNonNull(newPath));
    }

    /**
     * Set the encryption mode of the strategy
     * @param eMode 
     */
    public void seteMode(Encryption eMode) {
        this.eMode = eMode;
    }

    /**
     * Set the compression mode of the strategy
     * @param cMode 
     */
    public void setcMode(Compression cMode) {
        this.cMode = cMode;
    }
    
    /** Set the filters used for exclusion during backups.
     *  A reference to a copy of the passed list it's used so changes
     *  to the passed list are not reflected to the strategy list.
     *  @param filters
     */
    public void setFilters(List<Pattern> filters) {
        this.filters = List.copyOf(filters);
    }

    /** Set the increment limit of partial backups after which a new complete backup is done
     *  @param incLimit
     */
    public void setIncLimit(int incLimit) {
        this.incLimit = incLimitInvariant(incLimit);
    }

    
    
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    /** @return a complete string rappresentation of the strategy containing all attributes value */
    public String getData(){
        return "Strategy{" + "id=" + id + ", name=" + name + ", targets=" + targets + ", dst=" + dst + ", eMode=" + eMode + ", cMode=" + cMode + ", filters=" + filters + ", incLimit=" + incLimit + '}';
    }

    
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeObject(eMode);
        out.writeObject(cMode);
        out.writeObject(dst);
        out.writeObject(targets);
        out.writeObject(filters);
        out.writeInt(incLimit);
        out.writeLong(id);
    }

    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.name = in.readUTF();
        this.eMode = (Encryption) in.readObject();
        this.cMode = (Compression) in.readObject();
        this.dst = (Destination) in.readObject();
        this.targets = (List<Target>) in.readObject();
        this.filters = (List<Pattern>) in.readObject();
        try {
            this.incLimit = in.readInt();
            this.id = in.readLong();
            super.reachedEOF = false;
        } catch (EOFException ex){
            this.incLimit = MAX_INCREMENTAL_LIMIT;
            this.id = getRandomID();
            super.reachedEOF = true;
        }
    }
    
}
