/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import testsinc.net.SyncObjectStream;
import testsinc.net.server.ID;
import testsinc.net.shared.autentication.Login;
import testsinc.net.shared.autentication.OkAuth;

/**
 *
 * @author mauro
 */
public class RawConnectionContainer {

    long actualID=0;
    private AtomicBoolean listening = new AtomicBoolean(true);

    protected final HashMap<ID, RawConnection> clients = new HashMap<ID, RawConnection>();
    ArrayList<Integer> lastUp = new ArrayList<Integer>();
    int TIMEOUTMILLISEC = 2000;

    HashMap<Login, RawConnection> waitingUser = new HashMap<Login, RawConnection>();

    private void loginConnection(Map.Entry<ID, RawConnection> client) {

        if (client.getValue().getStream().isClosed()) {
            //client.close();
            //System.out.println("selector keys: " + client.getKey().selector().keys().size());
            System.out.println("Trovata connessione chiusa, la rimuovo");
            removeAndCloseClient(client.getKey());
        } else if (client.getValue().getStream().availableInput() == 0) {
            //no data, test if timeout
            if (Calendar.getInstance().getTime().getTime() - client.getValue().getStream().getTimeSinceLastRead() > TIMEOUTMILLISEC) {
                System.out.println("client in timeout, disconnecting");
                removeAndCloseClient(client.getKey());
            }
        } else if (client.getValue().getStream().availableInput() > 1) {
            //too much data!
            System.out.println("client sent too much data instead of login, maybe flood, disconnecting");
            removeAndCloseClient(client.getKey());
        } else {
            //we have a probabile login! let see!
            Object obj = client.getValue().getStream().readAndClear();
            if (obj instanceof Login) {
                Login login = (Login) obj;
                if (login.isValid()) {
                    if (Database.checkLogin(login)) {
                        client.getValue().getStream().write(new OkAuth());
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
        SyncObjectStream clientArray[];
        synchronized (clients) {
            for (Map.Entry<ID, RawConnection> client:clients.entrySet()){
                loginConnection(client);
            }
        }
    }

    private void removeClient(ID id) {
        synchronized (clients) {
            clients.remove(id);
        }
    }

    public HashMap<Login, RawConnection> getAndClearWaitingUser(){
        HashMap<Login, RawConnection> temp = waitingUser;
        waitingUser = new HashMap<Login, RawConnection>();
        return temp;
    }

    

    private ID getNewID(){
        ID tempID = new ID(actualID);
        actualID++;
        while (clients.containsKey(tempID)){
            tempID = new ID(actualID);
            actualID++;
        }
        return tempID;
    }

    public ID addClient(SyncObjectStream obj){
        if (obj!=null){
            synchronized (clients){
                ID id = getNewID();
                clients.put(id, new RawConnection(obj) );
                return id;
            }
        }
        return null;
    }

    public void removeAndCloseClient(ID id) {

        synchronized (clients){
            System.out.println("ConnectionInfoContainer closing client");
            RawConnection client = clients.remove(id);
            client.close();
        }
    }

    public void clientWrite(ID id, Object obj){
        synchronized (clients){
            clients.get(id).getStream().write(obj);
        }
    }

    public int getNumberOfClients(){
        synchronized(clients){
            return clients.size();
        }
    }

    public void writeToAll(Object obj) {
        synchronized (clients){
            for (RawConnection read:clients.values()){
                read.getStream().write(obj);
            }
        }
    }

    public ArrayList<IDandObject> readAll() {
        ArrayList<Object> temp;
        ArrayList<IDandObject> readed=new ArrayList<IDandObject>();

        for (Map.Entry<ID, RawConnection> stream:clients.entrySet()){
            temp = stream.getValue().getStream().readAndClearAll();
            if (temp.size()>0)
                readed.add( new IDandObject(stream.getKey(), temp));
        }
        return readed;
    }

    public boolean isListening() {
        return listening.get();
    }

    public void setListening(boolean value){
        listening.set(value);
    }

    public int size(){
        synchronized (clients){
            return clients.size();
        }
    }

    public void closeAll() {
        synchronized (clients){
            for (RawConnection o:clients.values()){
                o.close();
            }
            clients.clear();
            actualID=0;
            setListening(false);
        }
    }
}
