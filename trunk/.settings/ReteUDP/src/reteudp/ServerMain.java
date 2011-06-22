/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reteudp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import reteudp.server.ServerNetwork;

/**
 *
 * @author mauro
 */
public class ServerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("SERVER");
        try {
            ServerNetwork rete = new ServerNetwork();
            Thread a = new Thread(rete);
            a.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("FINE MAIN");
    }
}
