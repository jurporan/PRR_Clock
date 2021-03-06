import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

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
        DatagramPacket packet = (DatagramPacket) ((Object[]) arg)[0];
        byte[] data = packet.getData();

        if (data[0] == Protocol.DELAY_RESPONSE)
        {
            byte[] copy = new byte[data.length];
            System.arraycopy(data, 0, copy, 0, data.length);
            queue.store(copy, (Long) ((Object[]) arg)[1]);
            resume();
        }
        else if (data[0] == Protocol.SYNC && !sender.isAlive())
        {
            System.out.println("Demarrage des delay requests, adresse " + packet.getAddress());
            sender.setMaster(packet.getAddress());
            sender.start();
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
                try {suspend();}
                catch (Exception e) {}
            }

            Object[] packet = queue.getNext();
            Object[] sent = sender.getLastDelayRequest();

            ByteBuffer bf = ByteBuffer.wrap((byte[]) packet[0]);
            no = bf.getChar(1);
            nanotime = bf.getLong(3);

            System.out.println("Local " + new Integer((char) sent[0]) + ", reçu " + new Integer(no));

            if (no == (char) sent[0])
            {
                delay.setDelay((nanotime - (Long) sent[1]) / 2);
                System.out.println("ReceivedDelayResponseTIme " + nanotime);
                System.out.println("DelayRequestTimeSaved " + (Long)sent[1]);
                System.out.println("Nouveau décalage: " + ((nanotime - (Long) sent[1]) / 2));
            }
        }
    }
}
