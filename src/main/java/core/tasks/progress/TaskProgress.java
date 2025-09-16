package core.tasks.progress;

import java.util.logging.Level;
import logging.LoggerManager;


/**
 *
 * @author Fedrix
 */
public class TaskProgress implements Progress {
    protected Throwable exception;
    protected boolean isException;
    protected long progress = 0;
    protected String task;
    
    public TaskProgress(){
        this.progress = 0;
        isException = false;
    }
    
    @Override
    public void setProgress(long progress){
        if(progress >= 0 && progress <= 100)
            this.progress = progress;
        else
            throw new IllegalArgumentException("Progress must be between 0 and 100 (included)");
    }

    @Override
    public void addProgress(long increment) {
        long tmp = this.progress + increment;
        if(tmp >= 0 && tmp <= 100)
            this.progress = tmp;
        else
            throw new IllegalArgumentException("Progress sum must be between 0 and 100 (included)");
    }
    
    @Override
    public long getProgress(){
        return progress;
    }

    @Override
    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public String getTask() {
        LoggerManager.console().log(Level.FINE, task);
        return task;
    }

    @Override
    public Throwable getException() {
        return exception;
    }
    
    /**
     * Check if exceptions occurred during engine operation.
     * @return 
     */
    @Override
    public boolean isException(){
        return isException;
    }
    
    @Override
    public void setException(Throwable ex){
        exception = ex;
        LoggerManager.exception().log(Level.WARNING, "An exception was set during an engine operation, last task: %s".formatted(task), ex);
        isException = true;
    }

}
