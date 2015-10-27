import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class SyncProcessor extends Thread implements Observer
{
    private Delay delay;
    private Queue queue = new Queue();
    private Character lastNo;
    private Long nanotime;
    
    public SyncProcessor(Delay delay)
    {
        this.delay = delay;
    }
    
    public void update(Observable o, Object arg)
    {
        DatagramPacket packet = (DatagramPacket) ((Object[]) arg)[0];
        byte[] data = packet.getData();
        
        if (data[0] == Protocol.SYNC || data[0] == Protocol.FOLLOW_UP)
        {
            queue.store(data, (Long) ((Object[]) arg)[1]);
            notify();
        }
    }
    
    public void run()
    {
        while (true)
        {
            if (queue.size() == 0)
            {
                System.out.println("caca");
                try {wait();}
                catch (Exception e) {System.out.println("prout");}
            }
            
            Object[] packet = queue.getNext();
            ByteBuffer bf = ByteBuffer.wrap((byte[]) packet[0]);
            byte type = bf.get(0);
            Character no = bf.getChar(1);

            switch (type)
            {
                case Protocol.SYNC:
                nanotime = (Long) packet[1];
                lastNo = no;
                break;
                
                case Protocol.FOLLOW_UP:
                if (lastNo != no) {break;}
                Long time = bf.getLong(2);
                delay.setDelay(time - nanotime);
                break;
            }
        }
    }
}
