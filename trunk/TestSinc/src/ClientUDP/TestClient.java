/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientUDP;

import Shared.payload.StringPayload;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            client = new NetworkClient("192.168.1.64", 5000, 5001);
            new Thread(client, "ClientNet").start();

            // ByteBuffer c = ByteBuffer.allocate(4);

            long time = System.currentTimeMillis();
            for (int azz = 0; azz < 1000; azz++) {
                client.add(new StringPayload("inserisco: " + azz + " byte rimanenti"));

                //String out ="inserisco: " + i + " byte rimanenti";
                //System.out.println(out);
                //ByteBuffer c = ByteBuffer.allocate(out.length()*2);

                //for (int indexChar=0; indexChar < out.length();indexChar++){
                //System.out.println("inserimento: "+indexChar+" "+out.length());
                //c.putChar( out.charAt(indexChar) );
                //}

                //if (client.write(c) != out.length()*2) {
                //System.err.println("ERRORE!");
                //}
                //readData();
            }


            //System.out.println(output.asCharBuffer().toString());


            long time2 = System.currentTimeMillis();

            System.out.println("\tTime to write data: " + (time2 - time));
            while (client.isConnect()) {
                readData();
                /*
                 * try { Thread.sleep(10); } catch (InterruptedException ex) {
                 * Logger
                 * .getLogger(TestClient.class.getName()).log(Level.SEVERE,
                 * null, ex); }
                 */
            }
            System.out.println("\tEND CLIENT");
        } catch (IOException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

    }
    int i = 0;

    private void readData() {
        StringPayload s;
        while ((s = client.getString()) != null) {
            System.out.println(s);
        }
    }
}
