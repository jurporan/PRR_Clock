import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class PTPSlave extends Thread
{
    private MulticastSocket socket;
    private InetAddress group;
    private DelayRequester requester;
    private Long delay = 0l;
    private Long gap = 0l;
    private char lastSyncId;
    private Long lastSyncTime;
    
    public PTPSlave()
    {
        try
        {
            socket = new MulticastSocket(Protocol.slavePort);
            group = InetAddress.getByName(Protocol.group);
            socket.joinGroup(group);
        }
        catch(Exception e)
        {
            System.out.println("Impossible d'accéder au réseau");
        }
    }
    
    public void run()
    {
        byte[] buffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        Long currentNanotime;
        
        byte[] data;
        ByteBuffer bf;
        byte type;
        char no;
        Long receivedNanotime;
        
        while (true)
        {
            try {socket.receive(packet);}
            catch (Exception e) {}
            
            currentNanotime = System.nanoTime();
            data = packet.getData();
            bf = ByteBuffer.wrap(data);
            type = data[0];
            no = bf.getChar(1);
            
            System.out.println("Paquet reçu, type " + new Integer(type));
            
            switch (type)
            {
                case Protocol.SYNC:
                lastSyncId = no;
                lastSyncTime = currentNanotime;
                break;
                
                case Protocol.FOLLOW_UP:
                if (lastSyncId != no) {break;}
                receivedNanotime = bf.getLong(3);
                gap = receivedNanotime - lastSyncTime;
                if (requester == null)
                {
                    System.out.println("Démarrage du delay requester");
                    requester = new DelayRequester(socket);
                    requester.setMaster(packet.getAddress());
                    requester.start();
                }
                requester.setGap(gap);
                System.out.println("Follow up traité, nouvel écart de " + gap);
                break;
                
                case Protocol.DELAY_RESPONSE:
                if (requester.getLastSentID() != no) {break;}
                receivedNanotime = bf.getLong(3);
                delay = (receivedNanotime - requester.getLastSentTime()) / 2;
                System.out.println("Delay response traité, nouveau décalage de " + delay);
                break;
            }
        }
    }
    
    public Long getTime()
    {
        return System.nanoTime() + gap + delay;
    }
}
