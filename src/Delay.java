import java.util.concurrent.locks.ReentrantLock;

public class Delay
{
    private Long delay;
    private ReentrantLock lock = new ReentrantLock();
    
    public synchronized void setDelay(Long delay)
    {
        lock.lock();
        this.delay = delay;
        lock.unlock();
    }
    
    public synchronized Long getDelay()
    {
        lock.lock();
        Long d = delay;
        lock.unlock();
        return d;
    }
}
