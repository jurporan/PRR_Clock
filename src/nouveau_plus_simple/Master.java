public class Master
{
    public static void main(String[] args)
    {
        PTPMaster master = new PTPMaster();
        
        System.out.println("Démarrage du PTPMaster");
        master.start();
        
        while(true)
        {
            try {Thread.sleep(10000);}
            catch (Exception e) {}
        }
    }
}
