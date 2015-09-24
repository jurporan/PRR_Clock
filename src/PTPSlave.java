import java.net.*;
import java.io.*;

public class PTPSlave
{
   public static void main(String args[]) throws IOException
   {
      byte[] buffer = new byte[256];
      
      MulticastSocket socket = new MulticastSocket(1212);
      InetAddress group = InetAddress.getByName("228.5.6.7");
      socket.joinGroup(group);
      
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      socket.receive(packet);
      String receivedMessage = new String(packet.getData(),0);
      System.out.println("DiffusionClient: Message recu: " + receivedMessage);
      socket.leaveGroup(group);
      socket.close();
   }
}

