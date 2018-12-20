package client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ClientTest {


    @Test
    public void clientTest() throws InterruptedException {
        new ClientTest();
        Thread thread = new Thread(() -> {
           try {
               String[] s = new String[2];
               s[0] = "127.0.0.1";
               s[1] = "8080";
               Client client1 = new Client();
               client1.main(s);
           } catch (Exception e) {
               e.printStackTrace();
           }
       });

       thread.start();
       thread.sleep(2000);
    }
}