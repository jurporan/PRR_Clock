/*
    Auteurs :       Jan Purro et Benois Wolleb
    Projet :        PRR_Clock
    Fichier :       DelayRequester.java
    Description :   Cette classe représente une unité d'un client PTP dont le seul travail est d'envoyer des paquets DELAY_REQUEST à intervalle [4k,60k]. Il agit en tant que thread indépendant.
*/

import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

public class DelayRequester extends Thread
{
    // Socket permettant l'envoi de paquets
    private MulticastSocket socket;
    // Représente l'adresse du serveur auquel envoyer les paquets
    private InetAddress master;
    
    // Variables locales mémorisant le numéro et l'heure d'envoi du dernier paquet
    private char lastSentID = 0;
    private Long lastSentTime;
    
    // Accès local au décalage, car celui-ci doit être inclus dans l'heure d'envoi
    private Long gap = 0l;
    
    /*
        Paramètre : - socket : Référence vers le socket permettant l'envoi
        Description : Construit l'objet
    */
    public DelayRequester(MulticastSocket socket)
    {
        this.socket = socket;
    }
    
    /*
        Paramètre : -
        Description : Fonction principale du thread, envoie périodiquement un DELAY_REQUEST
    */
    public void run()
    {
        try
        {
            // Génératur aléatoire permettant de randomiser les envois de paquets
            Random r = new Random();
            
            // Construction du paquet à envoyer
            byte[] sendBuffer = new byte[(Byte.SIZE + Character.SIZE) / Byte.SIZE];
            sendBuffer[0] = Protocol.DELAY_REQUEST;
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, master, Protocol.masterPort);

            while (true)
            {
                // Attente
                try {Thread.sleep(Protocol.K * (r.nextInt(56) + 4));}
                catch (Exception e) {}
                
                // Copie du numéro de séquence dans le paquet
                byte[] noObject = ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(++lastSentID).array();
                System.arraycopy(noObject, 0, sendBuffer, 1, Character.SIZE / Byte.SIZE);
                
                // Envoi du paquet et stockage de son heure d'envoi
                sendPacket.setData(sendBuffer);
                lastSentTime = System.nanoTime() + gap;
                socket.send(sendPacket);
            }
        }
        catch (Exception e) {}
    }
    
    /*
        Paramètre : - master : Adresse du serveur à qui envoyer les requêtes
        Description : Modifie l'adresse du serveur à qui envoyer les requêtes
    */
    public void setMaster(InetAddress master) {this.master = master;}
    
    /*
        Paramètre : - gap : Décalage
        Description : Modifie la valeur du décalage
    */
    public void setGap(Long gap) {this.gap = gap;}
    
    /*
        Paramètre : -
        Description : Renvoie l'identifiant du dernier paquet envoyé
    */
    public char getLastSentID() {return lastSentID;}
    
    /*
        Paramètre : -
        Description : Renvoie l'heure d'envoi du dernier paquet
    */
    public Long getLastSentTime() {return lastSentTime;}
}
