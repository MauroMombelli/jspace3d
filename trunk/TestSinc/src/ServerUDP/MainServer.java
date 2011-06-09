/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import ServerUDP.client.ClientContainer;
import ServerUDP.physic.PhysicContainer;
import ServerUDP.physic.PhysicListener;
import ServerUDP.player.PlayerContainer;
import ServerUDP.user.UserContainer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class MainServer {

    public static final int inputPort = 5000;
    NetworkListener server;

    ClientContainer clients = new ClientContainer();

    UserContainer users = new UserContainer();

    PhysicContainer physics = new PhysicContainer();

    PlayerContainer players = new PlayerContainer(physics);
    
    PhysicListener physicsListener = new PhysicListener(players);

    public static void main(String arg[]) {
        MainServer server = new MainServer();

        server.startNetwork();

        server.run();
    }

    private void startNetwork() {
        try {
            server = new NetworkListener(inputPort);
            new Thread(server, "ServerNet").start();
            System.out.println("Running ");

            //scriviUnPoDiRoba();
        } catch (IOException ex) {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void run() {
        while (server.isListening.get()) {
            /*
             * accept new client
             * logins
             * read/execute actions
             * step physic
             * write actions
             */
            clients.addAll(server.getAndRemoveWaitingClient());
            clients.update();
            server.banIP(clients.getAndRemoveBannedIP());

            users.addAll(clients.getAndRemoveLoggedClient());
            users.update();
            server.banIP(clients.getAndRemoveBannedIP());
            clients.banUser( users.getAndRemoveBannedUser() );

            players.addAll( users.getAndRemoveNewPlayers() );
            players.update();
            server.banIP(players.getAndRemoveBannedIP());
            clients.banUser( players.getAndRemoveBannedUser() );

            physics.update();
        }
        clients.close();
        users.close();
        players.close();
    }
}
