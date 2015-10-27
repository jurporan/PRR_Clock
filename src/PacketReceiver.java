import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class PacketReceiver extends Observable implements Runnable
{
    private Queue packetQueue;
    private MulticastSocket socket;
    private InetAddress group;
    private byte[] buffer;
    private DatagramPacket packet;
    private Object[] timeStampedPacket;

    public PacketReceiver(Queue queue) throws IOException
    {
        packetQueue = queue;

        socket = new MulticastSocket(1212);
        group = InetAddress.getByName(Protocol.group);
        buffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        packet = new DatagramPacket(buffer, buffer.length);
        timeStampedPacket = new Object[2];
        timeStampedPacket[0] = buffer;
    }

    public void run()
    {
        while (true)
        {
            try {socket.receive(packet);}
            catch (Exception e) {}
            timeStampedPacket[1] = System.nanoTime();

            setChanged();
            notifyObservers(timeStampedPacket);
        }
    }

}
