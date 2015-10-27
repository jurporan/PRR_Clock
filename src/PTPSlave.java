
public class PTPSlave
{
    private MulticastSocket socket;
    private InetAddress group;
    private Delay delay = new Delay();
    
    public PTPSlave()
    {
        socket = new MulticastSocket(Protocol.port);
        group = InetAddress.getByName(Protocol.group);
        socket.joinGroup(group);
        
        DelayRequest sender = new DelayRequest(socket);
        DelayResponse response = new DelayResponse(sender, delay);
    }
    
    public static void main(String[] args)
    {
        PTPSlave slave = new PTPSlave();
    }
}
