import java.io.*;
import java.net.*;

public class PTPMaster
{
    // Associer un port de communication a un groupe
    InetAddress groupe;
    MulticastSocket socket;
    int port;
    
    public PTPMaster(String adresse, int port) throws Exception
    {
        groupe = InetAddress.getByName(adresse);
        this.port = port;
        socket = new MulticastSocket(port);
    }
    
    public void send() throws Exception
    {
        String message = "Allo";
        byte[] tampon = message.getBytes();
        DatagramPacket paquet = new DatagramPacket(tampon, tampon.length, groupe, port);
        socket.send(paquet);
    }
}
