import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class PacketReceiver extends Observable implements Runnable
{
    private MulticastSocket socket;
    private byte[] buffer;
    private DatagramPacket packet;
    private Object[] timeStampedPacket;

    public PacketReceiver(MulticastSocket socket)
    {
        this.socket = socket;
        buffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        packet = new DatagramPacket(buffer, buffer.length);
        timeStampedPacket = new Object[2];
    }

    public void run()
    {
        while (true)
        {
            try {socket.receive(packet);}
            catch (Exception e) {}
            timeStampedPacket[0] = packet;
            timeStampedPacket[1] = System.nanoTime();
System.out.println("paquet reçz");
            setChanged();
            notifyObservers(timeStampedPacket);
        }
    }

}
