import java.nio.*;
import java.io.*;
import java.net.*;

public class PTPMaster
{
    // Associer un port de communication a un groupe
    InetAddress group;
    MulticastSocket socket;
    int port;
    Thread syncThread;
    
    public PTPMaster(String adresse, int port) throws Exception
    {
        group = InetAddress.getByName(adresse);
        this.port = port;
        socket = new MulticastSocket(port);
        
        syncThread = new Thread(new Runnable (){
            
            public void run()
            {
                char no = 0;
                Long currentNanoTime;
                while (true)
                {
                    // Building SYNC message
                    currentNanoTime = System.nanoTime();
                    byte[] tampon = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
                    System.arraycopy(Protocol.SYNC, 0, tampon, 0, Byte.SIZE / Byte.SIZE);
                    byte[] noObject = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(no++).array();
                    System.arraycopy(noObject, 0, tampon, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                    byte[] nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(currentNanoTime).array();
                    System.arraycopy(nanoTime, 0, tampon, Byte.SIZE / Byte.SIZE + Character.SIZE / Byte.SIZE, nanoTime.length);
                    
                    // Sending the SYNC message
                    DatagramPacket packet = new DatagramPacket(tampon, tampon.length, group, PTPMaster.this.port);
                    currentNanoTime = System.nanoTime();
                    try {socket.send(packet);}
                    catch (Exception e) {}
                    
                    // Building FOLLOW_UP message
                    tampon = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
                    System.arraycopy(Protocol.FOLLOW_UP, 0, tampon, 0, Byte.SIZE / Byte.SIZE);
                    System.arraycopy(noObject, 0, tampon, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                    nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(currentNanoTime).array();
                    System.arraycopy(nanoTime, 0, tampon, Byte.SIZE / Byte.SIZE + Character.SIZE / Byte.SIZE, nanoTime.length);
                    
                    // Sending the FOLLOW_UP message
                    packet = new DatagramPacket(tampon, tampon.length, group, PTPMaster.this.port);
                    try {socket.send(packet);}
                    catch (Exception e) {}
                    
                    try {Thread.sleep(2000);}
                    catch (Exception e) {}
                }
            }
            
            });
        syncThread.start();
    }

}
