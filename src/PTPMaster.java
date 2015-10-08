import java.nio.*;
import java.io.*;
import java.net.*;

public class PTPMaster
{
    // Associer un port de communication a un groupe
    MulticastSocket socket;
    Thread syncThread;

    public PTPMaster() throws Exception
    {
        socket = new MulticastSocket(Protocol.port);

        syncThread = new Thread(new Runnable (){

            public void run()
            {
                try{
                char no = 0;
                Long currentNanoTime;

                byte[] tampon = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
                DatagramPacket packet = new DatagramPacket(tampon, tampon.length, InetAddress.getByName(Protocol.group), Protocol.port);

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
                    socket.send(packet);

                    // Building FOLLOW_UP message
                    tampon[0] = Protocol.FOLLOW_UP;
                    System.arraycopy(noObject, 0, tampon, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                    nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(currentNanoTime).array();
                    System.arraycopy(nanoTime, 0, tampon, Byte.SIZE / Byte.SIZE + Character.SIZE / Byte.SIZE, nanoTime.length);

                    // Sending the FOLLOW_UP message
                    packet.setData(tampon);
                    socket.send(packet);

                    Thread.sleep(Protocol.K);
                }}
                    catch (Exception e) {}
                }

            });
        syncThread.start();
    }

}
