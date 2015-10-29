/*
    Auteurs :       Jan Purro et Benois Wolleb
    Projet :        PRR_Clock
    Fichier :       Slave.java
    Description :   Cette classe contient le programme principal du laboratoire. Elle lance simplement un client PTP et affiche périodiquement l'heure synchronisée.
*/


public class Slave
{
    /*
        Paramètre : -args : Arguments de la ligne de commande (non utilisés)
        Description : Crée un client PTP et affiche périodiquement l'heure synchronisée
    */
    public static void main(String[] args)
    {
        // Construction du client PTP
        PTPSlave slave = new PTPSlave();
        
        System.out.println("Heure avant le lancement de PTPSlave: " + slave.getTime());
        slave.start();
        
        while(true)
        {
            try {Thread.sleep(2000);}
            catch (Exception e) {}
            
            System.out.println("Heure: " + slave.getTime() + ", écart: " + slave.getGap() + ", délai: " + slave.getDelay());
        }
    }
}
