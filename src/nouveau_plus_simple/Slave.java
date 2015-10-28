
public class Slave
{
    public static void main(String[] args)
    {
        PTPSlave slave = new PTPSlave();
        
        System.out.println("Heure avant le lancement de PTPSlave: " + slave.getTime());
        slave.start();
        
        while(true)
        {
            try {Thread.sleep(2000);}
            catch (Exception e) {}
            
            //System.out.println("Heure: " + slave.getTime());
        }
    }
}
