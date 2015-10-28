/*
    Auteurs :       Jan Purro et Benois Wolleb
    Projet :        PRR_Clock
    Fichier :       PTPSlave.java
    Description :   Cette classe représente un client PTP. Celle-ci est un thread qu'il suffit de lancer pour enclancher le processus de synchronisation. Cette classe est responsable d'écouter le réseau en multicast et de traiter les paquets SYNC, FOLLOW_UP et DELAY_RESPONSE de la part du serveur.
*/

import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class PTPSlave extends Thread
{
    // Socket permettant la réception et l'envoi de paquets multicast
    private MulticastSocket socket;
    // Représente l'adresse du groupe multicast auquel est abonné le socket
    private InetAddress group;
    
    // Objet esclave qui envoie périodiquement des demandes DELAY_REQUEST, thread séparé
    private DelayRequester requester;
    
    // Variables locales représentant le délai de transmission et le décalage entre le serveur et le client
    private Long delay = 0l;
    private Long gap = 0l;
    
    // Variables locales mémorisant le numéro et l'heure de réception du plus récent paquet SYNC reçu
    private char lastSyncId;
    private Long lastSyncTime;
    
    /*
        Paramètre : -
        Description : Construit l'objet et initialise le socket et le groupe
    */
    public PTPSlave()
    {
        // Lancement du socket et abonnement au groupe de diffusion
        try
        {
            socket = new MulticastSocket(Protocol.slavePort);
            group = InetAddress.getByName(Protocol.group);
            socket.joinGroup(group);
        }
        catch(Exception e)
        {
            System.out.println("Impossible d'accéder au réseau");
        }
    }
    
    /*
        Paramètre : -
        Description : Fonction principale du thread, écoute les paquets entrants et les traite
    */
    public void run()
    {
        // Création du paquet de réception
        byte[] buffer = new byte[(Byte.SIZE + Character.SIZE + Long.SIZE) / Byte.SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        Long currentNanotime;
        
        // Création des variables dans lesquelles seront extraites les informations du paquet reçu
        byte[] data;
        ByteBuffer bf;
        byte type;
        char no;
        Long receivedNanotime;
        
        while (true)
        {
            // Réception du paquet
            try {socket.receive(packet);}
            catch (Exception e) {}
            
            // Sauvegarde de l'heure de réception du paquet immédiatement après sa réception
            currentNanotime = System.nanoTime();
            // Extraction des informations du paquet
            data = packet.getData();
            bf = ByteBuffer.wrap(data);
            type = data[0];
            no = bf.getChar(1);
            
            // Test du type du paquet
            switch (type)
            {
                case Protocol.SYNC:
                // À la réception d'un SYNC, on ne fait que sauvegarder son numéro et son heure de réception
                lastSyncId = no;
                lastSyncTime = currentNanotime;
                break;
                
                case Protocol.FOLLOW_UP:
                // Si le numéro du FOLLOW_UP ne correspond pas au numéro du plus récent SYNC, on abandonne
                if (lastSyncId != no) {break;}
                // Extraction de l'heure contenue dans le message
                receivedNanotime = bf.getLong(3);
                // Calcul du nouveau décalage
                gap = receivedNanotime - lastSyncTime;
                
                // Si le requester n'existe pas encore, il s'agit de la première synchronisation avec le serveur, il faut en démarrer un
                if (requester == null)
                {
                    requester = new DelayRequester(socket);
                    requester.setMaster(packet.getAddress());
                    requester.start();
                }
                // Le requester a besoin de connaître le décalage
                requester.setGap(gap);
                break;
                
                case Protocol.DELAY_RESPONSE:
                // Si le numéro du DELAY_RESPONSE ne correspond pas au numéro du dernier DELAY_REQUEST envoyé, on abandonne
                if (requester.getLastSentID() != no) {break;}
                // Extraction de l'heure contenue dans le paquet
                receivedNanotime = bf.getLong(3);
                // On peut maintenant calculer le délai de transmission entre le serveur et le client
                delay = (receivedNanotime - requester.getLastSentTime()) / 2;
                break;
            }
        }
    }
    
    /*
        Paramètre : -
        Description : Cette fonction permet à son appelant de connaître l'heure actuelle, synchronisée avec le serveur
    */
    public Long getTime()
    {
        return System.nanoTime() + gap - delay;
    }
}
