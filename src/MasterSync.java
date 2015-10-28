import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class MasterSync extends Thread
{
    private Queue requestQueue = new Queue();
    private MulticastSocket socket;

    public MasterSync(MulticastSocket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        char lastId = 0;
        Long currentNanoTime;

        byte[] bufferSync = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];
        byte[] bufferFollowUp = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        DatagramPacket packetSync;
        DatagramPacket packetFollowUp;

        try
        {
            packetSync = new DatagramPacket(bufferSync, bufferSync.length, InetAddress.getByName(Protocol.group), Protocol.port);
            packetFollowUp = new DatagramPacket(bufferFollowUp, bufferFollowUp.length, InetAddress.getByName(Protocol.group), Protocol.port);
        }
        catch (UnknownHostException e)
        {
            System.out.println("Error : Unknown Host : " + Protocol.group);
            return;
        }

        bufferSync[0] = Protocol.SYNC;
        bufferFollowUp[0] = Protocol.FOLLOW_UP;

        while (true)
        {
            try
            {
                // Building SYNC message
                byte[] idMessage = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(++lastId).array();
                System.arraycopy(idMessage, 0, bufferSync, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);

                // Sending the SYNC message
                packetSync.setData(bufferSync);
                currentNanoTime = System.nanoTime();
                socket.send(packetSync);
                
                // Building FOLLOW_UP message
                System.arraycopy(idMessage, 0, bufferFollowUp, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                byte[] nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(currentNanoTime).array();
                System.arraycopy(nanoTime, 0, bufferFollowUp, Byte.SIZE / Byte.SIZE + Character.SIZE / Byte.SIZE, nanoTime.length);

                // Sending the FOLLOW_UP message
                packetFollowUp.setData(bufferFollowUp);
                socket.send(packetFollowUp);
                
                System.out.println("Nanotime envoye " + currentNanoTime);
                
                Thread.sleep(Protocol.K);
            }
            catch(Exception e){}
        }
    }
}
