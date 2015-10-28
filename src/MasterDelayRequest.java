import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class MasterDelayRequest extends Thread implements Observer
{
    private Queue requestQueue = new Queue();
    private MulticastSocket socket;

    public MasterDelayRequest(MulticastSocket socket)
    {
        this.socket = socket;
    }

    public void update(Observable o, Object arg)
    {
        Object[] data = (Object[]) arg;
        if (((DatagramPacket)data[0]).getData()[0] == Protocol.DELAY_REQUEST)
        {
            System.out.println("COUCOU");
            requestQueue.store(data[0], (Long) data[1]);
            resume();
        }
    }

    public void run()
    {
        Object[] delayRequest;
        byte[] buffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        DatagramPacket delayResponse = new DatagramPacket(buffer, buffer.length);
        delayResponse.setPort(Protocol.port);
        buffer[0] = Protocol.DELAY_RESPONSE;
        byte[] nanoTime;

        while (true)
        {
            System.out.println("Fougère");

            if (requestQueue.size() == 0)
            {
                suspend();
            }

            System.out.println("Fromage");
            delayRequest = requestQueue.getNext();
            System.arraycopy(((DatagramPacket)delayRequest[0]).getData(), 1, buffer, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
            nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong((Long)delayRequest[1]).array();
            System.arraycopy(nanoTime, 0, buffer, (Byte.SIZE + Character.SIZE) / Byte.SIZE, nanoTime.length);
            System.out.println("NVUASFISA");

            delayResponse = new DatagramPacket(buffer, buffer.length, ((DatagramPacket)delayRequest[0]).getAddress(), 1212);
            delayResponse.setData(buffer);
            //delayResponse.setAddress(((DatagramPacket)delayRequest[0]).getAddress());

            System.out.println("DelayResponseMasterTime : " + (Long)delayRequest[1]);

            try
            {
                socket.send(delayResponse);
            }

            catch(IOException e)
            {
                System.out.println("Error : delayResponse couldn't be sent.");
            }
        }
    }
}
