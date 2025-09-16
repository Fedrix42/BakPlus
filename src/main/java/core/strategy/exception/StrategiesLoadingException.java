package core.strategy.exception;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import utils.Pair;

/**
 * This exception is a container for every exception which was thrown during
 * the loading of a specific strategy. 
 * 
 * This is necessary because the program must not stop the loading of all strategies
 * if there is only an error during the loading of one.
 * 
 * 
 * @author fedrix
 */
public class StrategiesLoadingException extends IOException {
    private final List<Pair<Throwable, String>> exceptionsDuringLoading; // Throwable, Error Details (Ex. strategy name)
    
    public StrategiesLoadingException(){
        exceptionsDuringLoading = new LinkedList<>();
    }
    
    /**
     * Add an object which was thrown during the loading of a strategy
     * @param t 
     * @param details the path which caused the error, the name of the strategy or other info
     */
    public void addThrowable(Throwable t, String details){
        exceptionsDuringLoading.add(new Pair(t, details));
    }
    
    /**
     * @return a list of pairs (throwable, details) which was thrown during the loading of strategies
     */
    public List<Pair<Throwable, String>> getExceptions(){
        return exceptionsDuringLoading;
    }
    
    
}
