/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientUDP;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.util.calendar.Gregorian;

/**
 *
 * @author mauro
 */
public class TestClient {

    NetworkClient client;
    
    public static void main(String args[]) {
        new TestClient();
    }

    public TestClient() {
        
        try {
            client = new NetworkClient("127.0.0.1", 5000, 5001);

            //ByteBuffer c = ByteBuffer.allocate(4);

            long time = GregorianCalendar.getInstance().getTimeInMillis();
            for (int i = 0; i < 1000; i++) {
                ByteBuffer c = ByteBuffer.allocate(4);
                System.out.println("inserisco: " + i + " byte rimanenti: " + c.remaining());
                c.putInt(i);
                if (client.write(c) != 4) {
                    System.err.println("ERRORE!");
                }
                readData();
            }
            long time2 = GregorianCalendar.getInstance().getTimeInMillis();

            System.err.println("Time to write data: "+(time2-time));
            while (client.isConnect()) {
                readData();
/*
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
                }
 * 
 */
            }
        } catch (IOException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    int i = 0;
    private void readData() {
        ByteBuffer t = client.readDatagram();
        //if (t.)
        if (t != null) {
            System.out.println("Reading data, data as integer:" + t.asIntBuffer().get() + " number read: " + (i++));
        }
    }
}