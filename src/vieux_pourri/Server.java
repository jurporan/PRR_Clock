import java.io.*;
import java.lang.Thread;

public class Server
{

    public static void main(String[] args) throws Exception
    {
        PTPMaster master;

        master = new PTPMaster();

        while (true)
        {
            Thread.sleep(5000);
        }
    }
}
