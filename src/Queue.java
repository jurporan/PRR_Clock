import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class Queue extends Thread implements Observer
{
    private LinkedList<Object[]> queue = new LinkedList<>();
    private ReentrantLock lock = new ReentrantLock();

    public Object[] getNext()
    {
        lock.lock();
        Object[] data;
        if (queue.size() > 0)
        {data = queue.removeLast();}
        lock.unlock();
        return data;
    }

    public void store(byte[] data, Long time)
    {
        lock.lock();
        queue.addFirst(new Object[] {data, time});
        lock.unlock();
    }
    
    public int size()
    {
        lock.lock();
        int size = queue.size();
        lock.unlock();
        return size;
    }
}
