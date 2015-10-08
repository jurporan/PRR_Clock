import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class PTPSlave
{
    static Thread threadSync;
    static Thread threadDelay;
    static MulticastSocket socket;
    static InetAddress group;

   public static void main(String args[]) throws IOException
   {
      socket = new MulticastSocket(1212);
      group = InetAddress.getByName("228.5.6.7");
      socket.joinGroup(group);

      threadSync = new Thread(new Runnable() {

          public void run()
          {
              try{
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

                  if (!PTPSlave.threadDelay.isAlive()) {PTPSlave.threadDelay.start();}
              }
          }
          catch (Exception e) {}
          }

          });

          threadDelay = new Thread(new Runnable() {

              public void run()
              {
                  try{
                  Random r = new Random();
                  byte[] tampon = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
                  DatagramPacket packet = new DatagramPacket(tampon, tampon.length, InetAddress.getByName(Protocol.group), Protocol.port);

                  while (true)
                  {


                      try {Thread.sleep(Protocol.K * (r.nextInt(56) + 4));}
                      catch (Exception e) {}
                  }
              }catch (Exception e) {}
}
          });

        threadSync.start();
   }
}

//socket.leaveGroup(group);
//socket.close();
