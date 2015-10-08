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
              ByteBuffer bf = ByteBuffer.wrap(buffer);
              DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
              Character c;

              while (true)
              {
                  try {PTPSlave.socket.receive(packet);}
                  catch (Exception e) {}

                  c = bf.getChar(1);

                  switch (buffer[0])
                  {
                      case Protocol.SYNC:
                      System.out.println("SYNC");
                      break;

                      case Protocol.FOLLOW_UP:
                      System.out.println("FOLLOW_UP " + (int) c);
                      break;
                  }
              }
          }

          });

        thread.start();
   }
}

//socket.leaveGroup(group);
//socket.close();
