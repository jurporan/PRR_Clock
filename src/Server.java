import java.io.*;
import java.lang.Thread;

public class Server
{
    
    public static void main(String[] args) throws Exception
    {
        PTPMaster master;
        
        master = new PTPMaster("228.5.6.7", 1212);
        
        while (true)
        {
            Thread.sleep(5000);
        }
    }
}
