/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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

    private void loginConnection(Map.Entry<ID, ConnectionInfo> client) {

        if (client.getValue().isClosed()) {
            //client.close();
            //System.out.println("selector keys: " + client.getKey().selector().keys().size());
            System.out.println("Trovata connessione chiusa, la rimuovo");
            removeAndCloseClient(client.getKey());
        } else if (client.getValue().availableInput() == 0) {
            //no data, test if timeout
            if (Calendar.getInstance().getTime().getTime() - client.getValue().getTime() > TIMEOUTMILLISEC) {
                System.out.println("client in timeout, disconnecting");
                removeAndCloseClient(client.getKey());
            }
        } else if (client.getValue().availableInput() > 1) {
            //too much data!
            System.out.println("client sent too much data instead of login, maybe flood, disconnecting");
            removeAndCloseClient(client.getKey());
        } else {
            //we have a probabile login! let see!
            Object obj = client.getValue().readAndClear();
            if (obj instanceof Login) {
                Login login = (Login) obj;
                if (login.isValid()) {
                    if (Database.checkLogin(login)) {
                        client.getValue().write(new OkAuth());
                        removeClient(client.getKey());
                        waitingUser.put(login, client.getValue());
                        return;
                    } else {
                        //wrong user or passwd
                        System.out.println("client used wrong user or passwd, disconnecting");
                        removeAndCloseClient(client.getKey());
                    }
                } else {
                    //invalid login
                    System.out.println("client's login is invalid, disconnecting");
                    removeAndCloseClient(client.getKey());
                }

            } else {
                //no, it wasn't a login, disconnect
                System.out.println("client hasn't send a Login object, disconnecting");
                removeAndCloseClient(client.getKey());
            }
        }

    }

    public void update() {
        
        ConnectionInfo clientArray[];
        synchronized (clients) {
            for (Map.Entry<ID, ConnectionInfo> client:clients.entrySet()){
                loginConnection(client);
            }
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
