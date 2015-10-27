import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class DelayRequest extends Thread
{
    private MulticastSocket socket;
    private char lastSentID;
    private Long lastSentTime;
    
    public DelayRequest(MulticastSocket socket)
    {
        this.socket = socket;
    }
    
    public void run()
    {
        try
        {
            Random r = new Random();
            byte[] sendBuffer = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];
            char no = 0;
            Long currentNanoTime;
            
            sendBuffer[0] = Protocol.DELAY_REQUEST;
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(Protocol.group), Protocol.port);

            while (true)
            {
                byte[] noObject = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(no++).array();
                System.arraycopy(noObject, 0, sendBuffer, 1, Character.SIZE / Byte.SIZE);

                sendPacket.setData(sendBuffer);
                currentNanoTime = System.nanoTime();
                socket.send(sendPacket);

                try {Thread.sleep(Protocol.K * (r.nextInt(56) + 4));}
                catch (Exception e) {}
            }
        }
        catch (Exception e) {}
    }
    
    public synchronized void setLastDelayRequest(char id, Long time)
    {
        lastSentID = id;
        lastSentTime = time;
    }
    
    public synchronized Object[] getLastDelayRequest()
    {
        return new Object[] {lastSentID, lastSentTime};
    }
}
