import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class PTPMaster extends Thread
{
    private MulticastSocket socket;
    private InetAddress group;
    private Heartbeat heartbeat;
    
    public PTPMaster()
    {
        try
        {
            socket = new MulticastSocket(Protocol.masterPort);
            group = InetAddress.getByName(Protocol.group);
            socket.joinGroup(group);
        }
        catch(Exception e)
        {
            System.out.println("Impossible d'accéder au réseau");
        }
        
        heartbeat = new Heartbeat(socket, group);
    }
    
    public void run()
    {
        heartbeat.start();
        
        byte[] inputBuffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        DatagramPacket packet = new DatagramPacket(inputBuffer, inputBuffer.length);
        
        byte[] data;
        ByteBuffer bf;
        byte type;
        char no;
        Long receivedNanotime;
        
        byte[] outputBuffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        outputBuffer[0] = Protocol.DELAY_RESPONSE;
        DatagramPacket delayResponse = new DatagramPacket(outputBuffer, outputBuffer.length);
        delayResponse.setPort(Protocol.slavePort);
        byte[] receivedNanotimeArray;
        
        while (true)
        {
            try {socket.receive(packet);}
            catch (Exception e) {}
            
            receivedNanotime = System.nanoTime();
            data = packet.getData();
            bf = ByteBuffer.wrap(data);
            type = data[0];
            no = bf.getChar(1);
            
            System.out.println("Paquet reçu, type " + new Integer(type));
            
            switch(type)
            {
                case Protocol.DELAY_REQUEST:
                System.arraycopy(data, 1, outputBuffer, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                receivedNanotimeArray = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(receivedNanotime).array();
                System.arraycopy(receivedNanotimeArray, 0, outputBuffer, (Byte.SIZE + Character.SIZE) / Byte.SIZE, receivedNanotimeArray.length);
                delayResponse.setAddress(packet.getAddress());
                try
                {
                    socket.send(delayResponse);
                }

                catch(IOException e)
                {
                    System.out.println("Error : delayResponse couldn't be sent.");
                }
                break;
            }
        }
    }
    
    public Long getTime()
    {
        return System.nanoTime();
    }
}
