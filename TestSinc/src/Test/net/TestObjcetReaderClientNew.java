/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test.net;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.SyncObjectStream;
import testsinc.net.client.ClientSelector;

/**
 *
 * @author mauro
 */
public class TestObjcetReaderClientNew {

    public static void main(String[] args) {
        new TestObjcetReaderClientNew();
    }

    public TestObjcetReaderClientNew() {
        System.out.println("\tInizio New Client");

        ClientSelector connection = new ClientSelector();
        SyncObjectStream serverStream = connection.connect("192.168.1.64", 5000, true);
        new Thread(connection, "Client Stream").start();

        ArrayList<Object> input = new ArrayList<Object>();
        System.out.println("\tConnesso");

        serverStream.write("Ciao, io sono il client");
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestObjcetReaderClientNew.class.getName()).log(Level.SEVERE, null, ex);
        }

        serverStream.write("Ciao, io sono il client2");

        serverStream.write("Ciao, io sono il client3");

        while (!serverStream.isClosed()) {
            //read all input and show them
            input.clear();
            input.addAll(serverStream.readAndClearAll());
            for (Object received : input) {
                try {
                    String r = (String) received;
                    System.out.println("\tReaded:" + r);
                    if (r.equals("FINE")) {
                        System.out.println("\tGood connection lost");
                        serverStream.close();
                    }
                } catch (java.lang.ClassCastException e) {
                    System.out.println("\tReaded to string :" + received.toString());
                }
            }
        }

        System.out.println("\tClient closed, but now i'll try to write something:");
        serverStream.write("Tentativo scrittura invalido");
    }
}
