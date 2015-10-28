import java.net.*;
import java.io.*;
import java.nio.*;


public class PTPSlave
{
    private MulticastSocket socket;
    private InetAddress group;
    private Delay delay = new Delay();

    public PTPSlave()
    {
        try
        {
            socket = new MulticastSocket(Protocol.port);
            group = InetAddress.getByName(Protocol.group);
            socket.joinGroup(group);

            DelayRequest sender = new DelayRequest(socket, delay);
            SyncProcessor sync = new SyncProcessor(delay);
            DelayResponse response = new DelayResponse(sender, delay);
            PacketReceiver receiver = new PacketReceiver(socket);

            receiver.addObserver(sync);
            receiver.addObserver(response);

            sync.start();
            response.start();

            receiver.run();
        }
        catch (Exception e) {}
    }

    public static void main(String[] args)
    {
        PTPSlave slave = new PTPSlave();
    }
}
