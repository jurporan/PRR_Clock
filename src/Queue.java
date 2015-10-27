import java.util.LinkedList;


public class Queue
{
    LinkedList<Object[]> queue = new LinkedList<>();
    
    public synchronized Object[] getNext()
    {
        return queue.removeLast();
    }
    
    public synchronized void store(byte[] data, Long time)
    {
        queue.addFirst(new Object[] {data, time});
    }
    
    public synchronized int size()
    {
        return queue.size();
    }
}
