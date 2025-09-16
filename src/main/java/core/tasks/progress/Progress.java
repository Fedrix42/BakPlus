package core.tasks.progress;

/**
 *
 * @author Fedrix
 */
public interface Progress {
    public void setProgress(long progress);
    public void addProgress(long increment);
    public long getProgress();
    public void setTask(String task);
    public String getTask();
    public void setException(Throwable ex);
    public Throwable getException();
    public boolean isException();
}
