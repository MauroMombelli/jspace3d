/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reteudp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import reteudp.client.ClientNetwork;

/**
 *
 * @author mauro
 */
public class ClientMain {
    public static void main(String args[]) {
        System.out.println("CLIENT");
        try {
            ClientNetwork c = new ClientNetwork();
            c.startNetwork("192.168.1.64");
            Thread a = new Thread(c);
            a.start();
        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
