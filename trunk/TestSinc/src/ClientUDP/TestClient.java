/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ClientUDP;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class TestClient {
    public static void main(String args[]){
        new TestClient();
    }

    public TestClient(){
        NetworkClient client;
        try {
            client = new NetworkClient("94.36.213.11", 5000);

            //ByteBuffer c = ByteBuffer.allocate(4);
            
            for (int i=0; i < 10;i++){
                ByteBuffer c = ByteBuffer.allocate(4);
                System.out.println("inserisco: "+i+" byte rimanenti: "+c.remaining());
                c.putInt(i);
                if ( client.write(c) != 4){
                    System.err.println("ERRORE!");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
