/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import ServerUDP.client.Client;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class TestServer {

    static int port = 5000;

    NetworkListener server;

    LinkedList<Client> clients = new LinkedList<Client>();

    public static void main(String args[]) {
        TestServer main = new TestServer();

        main.startNetwork();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        main.echo();
    }

    private void echo() {
        if (server==null){
            System.out.println("You should start the network before");
        }else{
            System.out.println("Echo");
            while(server.isListening.get()){
                clients.addAll( server.getAndRemoveWaitingClient() );
                //System.out.println("ConnectedClient: "+clients.size());
                for (Client c:clients){
                    c.echo();
                }
                /*
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                 * 
                 */
            }
        }
        System.out.println("END Echo");
    }

    private void startNetwork() {
        try {
            server = new NetworkListener(port);
            new Thread(server, "ServerNet").start();
            System.out.println("Running ");

            //scriviUnPoDiRoba();
        } catch (IOException ex) {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
