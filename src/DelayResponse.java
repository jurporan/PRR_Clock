import java.util.*;


public class DelayResponse extends Thread implements Observer
{
    private DelayRequest sender;
    private Queue queue = new Queue();
    
    public DelayResponse(DelayRequest sender)
    {
        this.sender = sender;
        this.queue = queue;
    }
    
    public void update(Observable o, Object arg)
    {
        Object[] data = (Object[]) arg;
        if (((byte[]) data[0])[0] == Protocol.DELAY_RESPONSE)
        {
            queue.store((byte[]) data[0], (Long) data[1]);
            notify();
        }
    }
    
    public void run()
    {
        try {wait();}
        catch (Exception e) {}
    }
}
