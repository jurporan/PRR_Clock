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
      group = InetAddress.getByName(Protocol.group);
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
                  byte[] tamponEnvoi = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];
                  byte[] tamponReception = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
                  tamponEnvoi[0] = Protocol.DELAY_REQUEST;
                  char no = 0;
                  DatagramPacket packetEnvoi = new DatagramPacket(tamponEnvoi, tamponEnvoi.length, InetAddress.getByName(Protocol.group), Protocol.port);
                  DatagramPacket packetReception = new DatagramPacket(tamponReception, tamponReception.length, InetAddress.getByName(Protocol.group), Protocol.port);
                  Long currentNanoTime;

                  while (true)
                  {
                      byte[] noObject = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(no++).array();
                      System.arraycopy(noObject, 0, tampon, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);
                      
                      packetEnvoi.setData(tamponEnvoi);
                      currentNanoTime = System.nanoTime();
                      PTPSlave.socket.send(packetEnvoi);
                      
                      try {PTPSlave.socket.receive(packetReception);}
                      catch (Exception e) {}
                      
                      
                      
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
