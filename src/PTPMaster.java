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

                byte[] tampon = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
                DatagramPacket packet = new DatagramPacket(tampon, tampon.length, group, PTPMaster.this.port);

                while (true)
                {
                    // Building SYNC message
                    currentNanoTime = System.nanoTime();
                    tampon[0] = Protocol.SYNC;
                    byte[] noObject = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(no++).array();
                    System.arraycopy(noObject, 0, tampon, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                    byte[] nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(currentNanoTime).array();
                    System.arraycopy(nanoTime, 0, tampon, Byte.SIZE / Byte.SIZE + Character.SIZE / Byte.SIZE, nanoTime.length);

                    // Sending the SYNC message
                    packet.setData(tampon);
                    currentNanoTime = System.nanoTime();
                    try {socket.send(packet);}
                    catch (Exception e) {}

                    // Building FOLLOW_UP message
                    tampon[0] = Protocol.FOLLOW_UP;
                    System.arraycopy(noObject, 0, tampon, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                    nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(currentNanoTime).array();
                    System.arraycopy(nanoTime, 0, tampon, Byte.SIZE / Byte.SIZE + Character.SIZE / Byte.SIZE, nanoTime.length);

                    // Sending the FOLLOW_UP message
                    packet.setData(tampon);
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
