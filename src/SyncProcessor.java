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
            byte[] copy = new byte[data.length];
            System.arraycopy(data, 0, copy, 0, data.length);
            queue.store(copy, (Long) ((Object[]) arg)[1]);
            resume();
        }
    }

    public void run()
    {
        while (true)
        {
            if (queue.size() == 0)
            {
                try {suspend();}
                catch (Exception e) {}
            }

            Object[] packet = queue.getNext();
            ByteBuffer bf = ByteBuffer.wrap((byte[]) packet[0]);
            byte type = ((byte[]) packet[0])[0];
            Character no = bf.getChar(1);

            switch (type)
            {
                case Protocol.SYNC:
                nanotime = System.nanoTime();
                lastNo = no;
                break;

                case Protocol.FOLLOW_UP:
                if (lastNo != no) {break;}
                Long time = bf.getLong(3);
                delay.setGap(time - nanotime);
                System.out.println("traitement followup: " + (time - nanotime));
                break;
            }
        }
    }
}
