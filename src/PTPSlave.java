import java.net.*;
import java.io.*;
import java.nio.*;

public class PTPSlave
{
    static Thread thread;
    static MulticastSocket socket;
    static InetAddress group;
    
   public static void main(String args[]) throws IOException
   {
      socket = new MulticastSocket(1212);
      group = InetAddress.getByName("228.5.6.7");
      socket.joinGroup(group);
      
      thread = new Thread(new Runnable() {
          
          public void run()
          {
              byte[] buffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
              
              while (true)
              {
                  DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                  try {PTPSlave.socket.receive(packet);}
                  catch (Exception e) {}
                  
                  if (buffer[0] == Protocol.SYNC[0]) {System.out.println("Message reçu Sync");}
                  
                  ByteBuffer bf = ByteBuffer.wrap(buffer);
                  Byte bidon = bf.getByte();
                  Character c = bf.getChar(1);
                  
                  System.out.println("No: " + c);
              }
          }
          
          });
        
        thread.start();
   }
}

//socket.leaveGroup(group);
//socket.close();
