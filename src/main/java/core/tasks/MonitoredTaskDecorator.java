package core.tasks;

import core.tasks.progress.Progress;

/**
 * Decorator abstract class for a monitored task.
 * Decorators will add features and funtionalities to a monitored task.
 * Each decorator will rappresents an operation of the software which will be
 * executed by a thread.
 * @author Fedrix
 */
public abstract class MonitoredTaskDecorator implements MonitoredTask {
    protected MonitoredTask engine;
    
    public MonitoredTaskDecorator(ProgressTask engine){
        this.engine = engine;
    }

    @Override
    public void run(){
        engine.run();
    }
    
    @Override
    public Progress getProgress(){
        return engine.getProgress();
    }

    
    
}
