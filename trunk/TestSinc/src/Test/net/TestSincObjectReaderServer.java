/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test.net;

import testsinc.server.IDandObject;
import testsinc.net.server.ServerSelector;
import testsinc.server.RawConnectionContainer;

/**
 *
 * @author mauro
 */
public class TestSincObjectReaderServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("\tTestSincObjectReader");
        new TestSincObjectReaderServer();
    }

    public TestSincObjectReaderServer() {
        RawConnectionContainer objLayer = new RawConnectionContainer();
        ServerSelector testServer = new ServerSelector(5000, objLayer);
        new Thread(testServer, "Server").start();
        System.out.println("\tattesaClient");
        while (objLayer.size() == 0) {
            ;
        }
        System.out.println("\tINIZIO TEST");
        objLayer.writeToAll("test numero 1");

        System.out.println("\tINIZIO TEST 2");
        objLayer.writeToAll("test numero 2");

        System.out.println("\tINIZIO TEST 3");
        objLayer.writeToAll("test numero 3");

        System.out.println("\tINIZIO TEST FINALE");
        objLayer.writeToAll("FINE");

        objLayer.update();

        for (IDandObject r : objLayer.readAll()) {
            System.out.println("\tReaded from: "+r.id);
            for (Object o : r.obj) {
                System.out.println("\t\t"+(String) o);
            }
        }
        
        //wait for client to read data
        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestSincObjectReaderServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        System.out.println("\tClosing server");
        testServer.close();
        System.out.println("\tClosed");
    }

}
