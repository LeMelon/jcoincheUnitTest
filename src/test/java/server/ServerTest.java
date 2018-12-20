package server;

import org.junit.Test;

public class ServerTest {

    @Test
    public void servTest() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                String[] s = new String[1];
                new Server().main(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.sleep(10000);
    }
}