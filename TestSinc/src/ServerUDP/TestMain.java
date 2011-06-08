/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerUDP;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class TestMain {
    public static void main(String args[]){
        new TestMain();
    }

    public TestMain(){
        NetworkListener server;
        try {
            server = new NetworkListener(5000);
            new Thread(server, "ServerNet").start();
            System.out.println("Running ");
        } catch (IOException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
