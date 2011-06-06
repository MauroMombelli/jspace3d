/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import testsinc.net.server.ConnectionInfoContainer;
import testsinc.net.server.ConnectionInfo;
import testsinc.net.server.ID;
import testsinc.net.shared.autentication.Login;
import testsinc.net.shared.autentication.OkAuth;

/**
 *
 * @author mauro
 */
public class RawConnectionContainer extends ConnectionInfoContainer {

    ArrayList<Integer> lastUp = new ArrayList<Integer>();
    int TIMEOUTMILLISEC = 2000;

    HashMap<Login, ConnectionInfo> waitingUser = new HashMap<Login, ConnectionInfo>();

    private void loginConnection(ConnectionInfo client) {

        if (client.isClosed()) {
            //client.close();
            //System.out.println("selector keys: " + client.getKey().selector().keys().size());
            System.out.println("Trovata connessione chiusa, la rimuovo");
            removeAndCloseClient(client);
        } else if (client.availableInput() == 0) {
            //no data, test if timeout
            if (Calendar.getInstance().getTime().getTime() - client.getTime() > TIMEOUTMILLISEC) {
                System.out.println("client in timeout, disconnecting");
                removeAndCloseClient(client);
            }
        } else if (client.availableInput() > 1) {
            //too much data!
            System.out.println("client sent too much data instead of login, maybe flood, disconnecting");
            removeAndCloseClient(client);
        } else {
            //we have a probabile login! let see!
            Object obj = client.readAndClear();
            if (obj instanceof Login) {
                Login login = (Login) obj;
                if (login.isValid()) {
                    if (Database.checkLogin(login)) {
                        client.write(new OkAuth());
                        removeClient(client.id);
                        waitingUser.put(login, client);
                        return;
                    } else {
                        //wrong user or passwd
                        System.out.println("client used wrong user or passwd, disconnecting");
                        removeAndCloseClient(client);
                    }
                } else {
                    //invalid login
                    System.out.println("client's login is invalid, disconnecting");
                    removeAndCloseClient(client);
                }

            } else {
                //no, it wasn't a login, disconnect
                System.out.println("client hasn't send a Login object, disconnecting");
                removeAndCloseClient(client);
            }
        }

    }

    public void update() {
        
        ConnectionInfo clientArray[];
        synchronized (clients) {
            clientArray = clients.values().toArray(new ConnectionInfo[0]);
        }
        ConnectionInfo client;
        for (int i = 0; i < clientArray.length; i++) {
            client = clientArray[i];
            loginConnection(client);
        }
    }

    private void removeClient(ID id) {
        synchronized (clients) {
            clients.remove(id);
        }
    }

    public HashMap<Login, ConnectionInfo> getAndClearWaitingUser(){
        HashMap<Login, ConnectionInfo> temp = waitingUser;
        waitingUser = new HashMap<Login, ConnectionInfo>();
        return temp;
    }
}
