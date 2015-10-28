import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class Heartbeat extends Thread
{
    private MulticastSocket socket;
    private InetAddress group;
    
    public Heartbeat(MulticastSocket socket, InetAddress group)
    {
        this.socket = socket;
        this.group = group;
    }
    
    public void run()
    {
        char lastId = 0;
        Long currentNanoTime;

        byte[] bufferSync = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];
        byte[] bufferFollowUp = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        DatagramPacket packetSync;
        DatagramPacket packetFollowUp;

        packetSync = new DatagramPacket(bufferSync, bufferSync.length, group, Protocol.slavePort);
        packetFollowUp = new DatagramPacket(bufferFollowUp, bufferFollowUp.length, group, Protocol.slavePort);

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

                Thread.sleep(Protocol.K);
            }
            catch(Exception e){}
        }
    }
}
