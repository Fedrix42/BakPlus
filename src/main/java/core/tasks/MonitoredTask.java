package core.tasks;

import core.tasks.progress.Progress;

/**
 * A runnable with the addition of a progress monitor
 * @author Fedrix
 */
public interface MonitoredTask extends Runnable {
    
    @Override
    public void run();
    public Progress getProgress();
}
