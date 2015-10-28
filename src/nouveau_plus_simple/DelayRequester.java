import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class DelayRequester extends Thread
{
    private MulticastSocket socket;
    private InetAddress master;
    private char lastSentID = 0;
    private Long lastSentTime;
    private Long gap = 0l;
    
    public DelayRequester(MulticastSocket socket)
    {
        this.socket = socket;
    }
    
    public void run()
    {
        try
        {
            Random r = new Random();
            byte[] sendBuffer = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];

            sendBuffer[0] = Protocol.DELAY_REQUEST;
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, master, Protocol.masterPort);

            while (true)
            {
                try {Thread.sleep(Protocol.K * (r.nextInt(1) + 4));}
                catch (Exception e) {}

                byte[] noObject = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(++lastSentID).array();
                System.arraycopy(noObject, 0, sendBuffer, 1, Character.SIZE / Byte.SIZE);

                sendPacket.setData(sendBuffer);
                lastSentTime = System.nanoTime() + gap;
                socket.send(sendPacket);
                
                System.out.println("Envoi d'un delay request n°" + new Integer(lastSentID));
            }
        }
        catch (Exception e) {}
    }
    
    public void setMaster(InetAddress master) {this.master = master;}
    
    public void setGap(Long gap) {this.gap = gap;}
    
    public char getLastSentID() {return lastSentID;}
    
    public Long getLastSentTime() {return lastSentTime;}
}
