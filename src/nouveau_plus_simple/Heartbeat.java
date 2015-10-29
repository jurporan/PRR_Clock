/*
    Auteurs : Jan Purro et Benois Wolleb
    Projet : PRR_Clock
    Fichier : Heartbeat.java
    Description : Cette classe implémente une unité du maître PTP. Elle
    envoye, à intervall k, un message SYNC, suivi d'un message FOLLOW_UP à
    tout les esclaves abonnés au groupe.
*/

import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class Heartbeat extends Thread
{
    // Socket permettant l'envoi et la reception de datagram.
    private MulticastSocket socket;
    // Adresse du groupe multicast auquel est abonné le socket
    private InetAddress group;
    /*
        Paramètre : - socket : le socket qui sera utilisé par le heartbeat pour
                               envoyer des paquets sur le réseau.
                    - group : adresse du groupe auquel le socket sera abonné.
        Description : Construit un objet Heartbeats. Une fois constrtuit il
        suffit de le lancer.
    */
    public Heartbeat(MulticastSocket socket, InetAddress group)
    {
        this.socket = socket;
        this.group = group;
    }

    public void run()
    {
        /* Identifiant des messages. On utilise un char car il peut être
           interpréter comme un entier non-signé de 16 bits.*/
        char lastId = 0;
        // Variable utilisée pour stocker l'heure d'envoi du message SYNC.
        Long currentNanoTime;

        // Buffer utilisé par les paquets SYNC et FOLLOW_UP.
        byte[] bufferSync = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];
        byte[] bufferFollowUp = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];

        // Paquets qui seront utilisé pour envoyé les messages.
        DatagramPacket packetSync;
        DatagramPacket packetFollowUp;

        /* Initialisation des paquets (les buffer, group et port ne changeront
           pas). */
        packetSync = new DatagramPacket(bufferSync, bufferSync.length, group, Protocol.slavePort);
        packetFollowUp = new DatagramPacket(bufferFollowUp, bufferFollowUp.length, group, Protocol.slavePort);

        // Initialisation des types des deux messages qui ne changeront pas.
        bufferSync[0] = Protocol.SYNC;
        bufferFollowUp[0] = Protocol.FOLLOW_UP;

        // Variables utilisées lors de la construction des messages.
        byte[] idMessage;

        // Envoie un SYNC suivi d'un FOLLOW_UP à interval K.
        while (true)
        {
            try
            {
                // Construction du message SYNC (on change l'id du message.)
                idMessage = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(++lastId).array();

                System.arraycopy(idMessage, 0, bufferSync, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);

                /* Envoi du message SYNC. On sauvegarde l'heure d'envoi au
                   préalable. */
                packetSync.setData(bufferSync);
                currentNanoTime = System.nanoTime();
                socket.send(packetSync);

                /* Construction du message FOLLOW_UP. Copi de l'id et de l'heure
                   d'envoi du SYNC. */
                System.arraycopy(idMessage, 0, bufferFollowUp, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);

                byte[] nanoTime = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(currentNanoTime).array();

                System.arraycopy(nanoTime, 0, bufferFollowUp, Byte.SIZE / Byte.SIZE + Character.SIZE / Byte.SIZE, nanoTime.length);

                // Envoi du message FOLLOW_UP.
                packetFollowUp.setData(bufferFollowUp);
                socket.send(packetFollowUp);

                // Attente de K ms.
                Thread.sleep(Protocol.K);
            }
            catch(Exception e){}
        }
    }
}
