import java.util.*;
import java.nio.*;

public class DelayResponse extends Thread implements Observer
{
    private DelayRequest sender;
    private Delay delay;
    private Queue queue = new Queue();
    
    public DelayResponse(DelayRequest sender, Delay delay)
    {
        this.sender = sender;
        this.delay = delay;
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
        Character no;
        Long nanotime;
        
        while (true)
        {
            if (queue.size() == 0)
            {
                try {wait();}
                catch (Exception e) {}
            }
            
            Object[] packet = queue.getNext();
            Object[] sent = sender.getLastDelayRequest();
            
            ByteBuffer bf = ByteBuffer.wrap((byte[]) packet[0]);
            no = bf.getChar(1);
            nanotime = bf.getLong(2);
            
            if (no == (char) sent[0])
            {
                delay.setDelay((nanotime - (Long) sent[1]) / 2);
            }
        }
    }
}
