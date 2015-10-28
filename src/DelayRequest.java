import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class DelayRequest extends Thread
{
    private MulticastSocket socket;
    private char lastSentID;
    private Long lastSentTime;
    private InetAddress master;
    private Delay delay;
    
    public DelayRequest(MulticastSocket socket, Delay delay)
    {
        this.socket = socket;
        this.delay = delay;
    }
    
    public void run()
    {
        try
        {
            Random r = new Random();
            byte[] sendBuffer = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];
            char no = 0;
            Long currentNanoTime;
            Long localDelay;
            
            sendBuffer[0] = Protocol.DELAY_REQUEST;
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, master, 1818);

            while (true)
            {
                try {Thread.sleep(Protocol.K * (r.nextInt(1) + 4));}
                catch (Exception e) {}
                
                localDelay = delay.getDelay();
                
                System.out.println("Envoi delay request");
                
                byte[] noObject = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(no).array();
                System.arraycopy(noObject, 0, sendBuffer, 1, Character.SIZE / Byte.SIZE);

                sendPacket.setData(sendBuffer);
                currentNanoTime = System.nanoTime() + localDelay;
                socket.send(sendPacket);
                setLastDelayRequest(no++, currentNanoTime);
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
    
    public void setMaster(InetAddress master)
    {
        this.master = master;
    }
}
