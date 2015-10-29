/*
    Auteurs :       Jan Purro et Benois Wolleb
    Projet :        PRR_Clock
    Fichier :       Protocol.java
    Description :   Cette classe sert uniquement de conteneur pour les constantes du protocole PTP. Nous avons décidé de placer les constantes de type des messages dans un byte et non de faire un type énuméré, ce qui aurait été plus logique. Cependant, en Java, les types énumérés sont stockés sur 4 octets alors que nous avons besoin de très peu de valeurs (4 au total). Nous avons donc choisi d'utiliser un byte, soit un seul octet pour stocker ces valeurs, ce qui diminue la taille de la trame à transmettre.
*/

public class Protocol
{
    public static final byte SYNC = 1;
    public static final byte FOLLOW_UP = 2;
    public static final byte DELAY_REQUEST = 3;
    public static final byte DELAY_RESPONSE = 4;
    public static final int K = 1000;
    public static final String group = "228.5.6.7";
    public static final int slavePort = 1212;
    public static final int masterPort = 1818;
}
