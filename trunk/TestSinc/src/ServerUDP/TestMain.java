/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class TestMain {

    NetworkListener server;

    public static void main(String args[]) {
        TestMain azz = new TestMain();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        azz.calculate();
    }

    public TestMain() {
        try {
            server = new NetworkListener(5000);
            new Thread(server, "ServerNet").start();
            System.out.println("Running ");

            //scriviUnPoDiRoba();
        } catch (IOException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void calculate() {
        System.out.println("Starting calculation");
        server.calculateStat();
    }

}
