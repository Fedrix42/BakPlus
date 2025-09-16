package core.tasks;

import core.tasks.progress.TaskProgress;
import core.tasks.progress.Progress;

/**
 * Basic implementation of a progress task
 * @author Fedrix
 */
public class ProgressTask implements MonitoredTask {
    private final Progress progress;
    
    public ProgressTask(){
        progress = new TaskProgress();
    }
    
    @Override
    public void run() {
        progress.setProgress(0);
        progress.setTask(this.getClass().getSimpleName() + " started.");
    }
    
    
    @Override
    public Progress getProgress(){
        return progress;
    }
    
}
