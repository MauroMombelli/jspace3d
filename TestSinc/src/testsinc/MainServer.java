/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc;

import Test.server.physic.EnginePhysic;

import java.util.logging.Level;
import java.util.logging.Logger;

import testsinc.net.server.ServerSelector;

import testsinc.server.RawConnectionContainer;
import testsinc.server.UserContainer;

/**
 *
 * @author mauro
 */
public class MainServer {
    RawConnectionContainer connectionContainer;
    UserContainer usersEngine = new UserContainer();

    EnginePhysic physic = new EnginePhysic();


    public static void main(String args[]){
        MainServer server = new MainServer();

        System.out.println("Starting physic");
        server.startPhysic();

        System.out.println("Starting server");
        server.startListen(5000);

        System.out.println("Starting logic");
        server.run();

        System.out.println("Server end");
        System.exit(0);
    }

    private void startListen(int port) {
        connectionContainer = new RawConnectionContainer();
        ServerSelector testServer = new ServerSelector(5000, connectionContainer);
        new Thread(testServer).start();
    }

    private void startPhysic() {
        physic = new EnginePhysic();
    }

    private void run() {
        while (connectionContainer.isListening()){
            connectionContainer.update();

            usersEngine.addAll( connectionContainer.getAndClearWaitingUser() );
            usersEngine.update();
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
