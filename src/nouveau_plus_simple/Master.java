/*
    Auteurs :       Jan Purro et Benois Wolleb
    Projet :        PRR_Clock
    Fichier :       Master.java
    Description :   Cette classe contient l'un des programmes principaux du
                    laboratoire. Elle lance un maître PTP et affiche
                    périodiquement son heure.

    Description du programme :
    Il s'agit du programme maître du protocole PTP. Il se contente de lancer un
    objet PTPMaster et de lui demander l'heure à intervalles réguliers, afin de
    donner un exemple de fonctionnement.

    Il existe un programme esclave, qui peut être lancé sur une autre machine,
    qui fonctionne de manière similaire : lancement d'une classe PTPSlave et
    affichage régulier de l'heure ensuite.

    L'objet PTPMaster est celui qui s'occupe de toute la logique du côté maître.
    Il lance un Heartbeat, un thread indépendant dont le rôle est d'envoyer les
    deux messages de synchronisations (SYNC et FOLLOW_UP) à intervalles réguliers
    K. La classe PTPMaster s'occupe également de la réception des messages de
    type DELAY_REQUEST qui lui sont adressé par les esclaves. Lorsqu'un tel
    message est reçu, un message DELAY_RESPONSE est construit et envoyé à
    l'esclave par le PTPMaster.

    L'objet PTPSlave effectue un travail similaire du côté esclave du protocole.
    Il lance un thread DelayRequester qui se charge d'envoyer des messages
    DELAY_REQUEST à intervalles [4k-60k] au maître. Ce thread n'est lancé qu'une
    fois qu'un paquet SYNC a été reçu.
    La classe PTPSlave s'occupe également de la réception des paquets SYNC,
    FOLLOW_UP et DELAY_RESPONSE envoyés par le maître. C'est également elle qui
    à la suite de la réception de ses paquets peut calculer l'écart entre
    son horloge et celle du maître ainsi que le délai de transmission des
    paquets, lui permettant ainsi de disposer d'une heure correcte.

    Nous avons choisi que la réception et le traitement des paquets seraient
    effectués par une seule classe, aussi bien du côté maître que du côté
    esclave, car ils reçoivent relativement peu de messages et que leur
    traitement est rapide.
    L'envoi des messages est dédié a des threads séparés. Vu que ces messages
    sont rarement envoyé et que la réception de paquets est un appel bloquant.

    Il existe 4 types de messages différents : SYNC, FOLLOW_UP, DELAY_REQUEST et
    DELAY_RESPONSE. Ils sont structurés de la manière suivante :
    TYPE|IDENTIFIANT|[TEMPS] où TYPE est un byte indiquant le type du message,
    IDENTIFIANT un char (entier non-signé de 16 bits) identifiant le message
    (des messages de types différents peuvent posséder le même identifiant) et
    TEMPS un long (entier signé 64 bits) qui contient un temps. Cette dernière
    partie n'est pas présente dans tous les messages. Seuls les messages de type
    FOLLOW_UP et DELAY_RESPONSE en contiennent.
    Les identifiants recommenceront à zéro au bout d'un moment (2^16 - 1
    messages), mais cela ne devrait pas être problématique (les messages étant
    rarement envoyés, il est extrêmement improbable que deux messages ayant le
    même identifiant et le même type se trouvent sur le réseau en même temps.)

    La classe Protocol contient certaines constantes nécessaire au maître et
    aux esclaves, notamment les valeurs correspondant aux différents types de
    message. Nous avons choisi arbitrairement un intervalle K de 1 seconde, qui
    peut facilement être changé dans cette classe.
*/


public class Master
{
    public static void main(String[] args)
    {
        // Création du maître et Lancement de celui-ci.
        PTPMaster master = new PTPMaster();

        System.out.println("Démarrage du PTPMaster");
        master.start();

        // Affiche l'heure du maître toutes les 2 secondes.
        while(true)
        {
            try {Thread.sleep(2000);}
            catch (Exception e) {}
            System.out.println("Heure: " + master.getTime());
        }
    }
}
