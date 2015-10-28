import java.util.concurrent.locks.ReentrantLock;

public class Delay
{
    private Long delay;
    private Long gap;
    private ReentrantLock delayLock = new ReentrantLock();
    private ReentrantLock gapLock = new ReentrantLock();
    
    public synchronized void setDelay(Long delay)
    {
        delayLock.lock();
        this.delay = delay;
        delayLock.unlock();
    }
    
    public synchronized Long getDelay()
    {
        delayLock.lock();
        Long d = delay;
        delayLock.unlock();
        return d;
    }
    
    public synchronized void setGap(Long gap)
    {
        gapLock.lock();
        this.gap = gap;
        gapLock.unlock();
    }
    
    public synchronized Long getGap()
    {
        gapLock.lock();
        Long g = gap;
        gapLock.unlock();
        return g;
    }
}
