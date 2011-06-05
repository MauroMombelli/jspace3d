/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test.net;

import testsinc.net.server.IDandObject;
import testsinc.net.server.ConnectionInfoContainer;
import testsinc.net.server.ServerSelector;

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
        ConnectionInfoContainer objLayer = new ConnectionInfoContainer();
        ServerSelector testServer = new ServerSelector(5000, objLayer);
        new Thread(testServer).start();
        System.out.println("\tattesaClient");
        while (objLayer.getNumberOfClients() == 0) {
            ;
        }
        System.out.println("\tINIZIO TEST");
        objLayer.writeBroadcats("test numero 1");

        System.out.println("\tINIZIO TEST 2");
        objLayer.writeBroadcats("test numero 2");

        System.out.println("\tINIZIO TEST 3");
        objLayer.writeBroadcats("test numero 3");

        System.out.println("\tINIZIO TEST FINALE");
        objLayer.writeBroadcats("FINE");

        for (IDandObject r : objLayer.readAll()) {
            System.out.println("\tReaded from: "+r.id);
            for (Object o : r.obj) {
                System.out.println("\t\t"+(String) o);
            }
        }
        testServer.close();
    }

}
