/*
    Auteurs : Jan Purro et Benois Wolleb
    Projet : PRR_Clock
    Fichier : PTPMaster.java
    Description : Cette classe implémente un maître PTP. Il s'agit d'un thread qui, une fois lancé, mettera en place le processus de synchronisation. Cette classe se charge de répondre aux messages DELAY_REQUEST des escalves. Elle lance également un thread Heartbeat chargé d'émettre régulièrement des messages SYNC et FOLLOW_UP.
*/

import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;


public class PTPMaster extends Thread
{
    // Socket permettant l'envoi et la reception de datagram.
    private MulticastSocket socket;
    // Adresse du groupe multicast auquel est abonné le socket
    private InetAddress group;
    /* Objet envoyant régulièrement les messages SYNC et FOLLOW_UP permettant
       la synchronisation des esclaves. */
    private Heartbeat heartbeat;

    /*
        Paramètre : -
        Description : Construit un objet PTPMaster. Une fois constrtuit il
        suffit de le lancer.
    */
    public PTPMaster()
    {
        try
        {
            socket = new MulticastSocket(Protocol.masterPort);
            group = InetAddress.getByName(Protocol.group);
            socket.joinGroup(group);
        }
        catch(Exception e)
        {
            System.out.println("Impossible d'accéder au réseau");
        }

        heartbeat = new Heartbeat(socket, group);
    }

    /*
        Paramètre : -
        Description : Fonction principale du thread. Lance le heartbeat et
        écoute les paquets entrants et traite les DELAY_REQUEST des clients.
    */
    public void run()
    {
        // Lancement du thread émettant les messages de synchronisation
        heartbeat.start();

        /* Buffer utiliser pour les paquets entrant. Sa taille correspond à la
        taille maximale des paquets disponibles. */
        byte[] inputBuffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        // Paquet utilisé pour la réception des paquets.
        DatagramPacket packet = new DatagramPacket(inputBuffer, inputBuffer.length);

        /* Buffer utiliser pour les paquets sortant. Sa taille correspond à la
        taille maximale des paquets disponibles ainsi que la taille des paquets
        DELAY_RESPONSE, qui seront les seuls paquets envoyés par le maître. */
        byte[] outputBuffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        /* Tout les paquets sortant étant du type DELAY_RESPONSE. On set l'octet
           définitivement. */
        outputBuffer[0] = Protocol.DELAY_RESPONSE;
        // Paquet utilisé pour envoyé des messages DELAY_RESPONSE aux clients.
        DatagramPacket delayResponse = new DatagramPacket(outputBuffer, outputBuffer.length);
        delayResponse.setPort(Protocol.slavePort);

        // Variables utilisées pour traité les paquets reçus.
        byte[] data;
        byte type;
        char idPacket;
        Long receivedNanotime;
        byte[] receivedNanotimeArray;

        while (true)
        {
            // Réception d'un paquet.
            try {socket.receive(packet);}
            catch (Exception e) {}

            // Enregistrement de l'heure de réception du paquet.
            receivedNanotime = System.nanoTime();
            // Extraction des données du paquets.
            data = packet.getData();
            // Extraction du type de paquet et de son identifiant.
            type = data[0];

            // On traite le paquet uniquement s'il est de type DELAY_REQUEST.
            switch(type)
            {
                case Protocol.DELAY_REQUEST:

                    /* Copie de l'identifiant du paquet reçu dans le buffer de
                      la réponse. */
                    System.arraycopy(data, 1, outputBuffer, Byte.SIZE / Byte.SIZE, Character.SIZE / Byte.SIZE);

                    /* Conversion de l'heure de réception du DELAY_REQUEST dans
                       un tableau d'octets.*/
                    receivedNanotimeArray = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(receivedNanotime).array();

                    // Copie de l'heure de réception dans la réponse.
                    System.arraycopy(receivedNanotimeArray, 0, outputBuffer, (Byte.SIZE + Character.SIZE) / Byte.SIZE, receivedNanotimeArray.length);

                    /* Changement del'adresse de destination du paquet à celle
                       de l'esclave ayant envoyé la requête. et envoit de la
                       réponse. */
                    delayResponse.setAddress(packet.getAddress());
                    try
                    {
                        socket.send(delayResponse);
                    }

                    catch(IOException e)
                    {
                        System.out.println("Error : delayResponse couldn't be sent.");
                    }
                    break;

                case default :
                    break;
            }
        }
    }

    /*
        Paramètre : -
        Description : Cette fonction permet à son appelant de connaître l'heure actuelle.
    */
    public Long getTime()
    {
        return System.nanoTime();
    }
}
