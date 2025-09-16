package core.tasks.progress;

/**
 *
 * @author Fedrix
 */
public abstract class ProgressDecorator implements Progress {
    private final Progress progress;
    
    public ProgressDecorator(Progress progress){
        this.progress = progress;
    }
    
    @Override
    public boolean isException(){
        return progress.isException();
    }

    @Override
    public Throwable getException(){
        return progress.getException();
    }

    @Override
    public void addProgress(long increment) {
        progress.addProgress(increment);
    }
    

    @Override
    public void setException(Throwable ex){
        progress.setException(ex);
    }

    @Override
    public String getTask(){
        return progress.getTask();
    }

    @Override
    public void setTask(String task){
        progress.setTask(task);
    }

    @Override
    public long getProgress(){
        return progress.getProgress();
    }

    @Override
    public void setProgress(long progress){
        this.progress.setProgress(progress);
    }
    
}
