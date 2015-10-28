import java.net.*;
import java.io.*;
import java.nio.*;

public class PTPMaster
{
    private MulticastSocket socket;

    public static void main(String[] args)
    {
        MulticastSocket socket;
        try
        {
            socket = new MulticastSocket(Protocol.port);
        }
        catch(IOException e)
        {
            System.out.println("Error. Coudln't instanciate socket");
            return;
        }

        MasterDelayRequest delaySender = new MasterDelayRequest(socket);
        MasterSync syncSender = new MasterSync(socket);
        PacketReceiver receiver = new PacketReceiver(socket);

        receiver.addObserver(delaySender);

        syncSender.start();
        delaySender.start();
        receiver.run();
    }
}
