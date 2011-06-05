/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.net.server;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author mauro
 */
public class ConnectionInfoContainer {

    long actualID=0;
    protected final TreeMap<ID, ConnectionInfo> clients = new TreeMap<ID, ConnectionInfo>();
    
    private AtomicBoolean listening = new AtomicBoolean(true);

    private ID getNewID(){
        ID tempID = new ID(actualID);
        actualID++;
        while (clients.containsKey(tempID)){
            tempID = new ID(actualID);
            actualID++;
        }
        return tempID;
    }

    public ID addClient(ConnectionInfo obj){
        if (obj!=null){
            
            synchronized (clients){
                obj.id =getNewID();
                clients.put(obj.id, obj);
                return obj.id;
            }
        }
        return null;
    }

    public void removeAndCloseClient(ConnectionInfo client) {
        synchronized (clients){
            System.out.println("ConnectionInfoContainer closing client");
            client.close();
            clients.remove(client.id);
        }
    }

    public void clientWrite(ID id, Object obj){
        synchronized (clients){
            clients.get(id).write(obj);
        }
    }

    public int getNumberOfClients(){
        synchronized(clients){
            return clients.size();
        }
    }

    public void writeBroadcats(Object obj) {
        synchronized (clients){
            for (ConnectionInfo read:clients.values()){
                read.write(obj);
            }
        }
    }

    public ArrayList<IDandObject> readAll() {
        ArrayList<IDandObject> readed = new ArrayList<IDandObject>();
        ConnectionInfo stream[] = new ConnectionInfo[clients.size()];
        synchronized (clients){
            clients.values().toArray(stream);
        }
        ArrayList<Object> temp;
        for (int i=0;i < stream.length; i++){
            temp = stream[i].readAndClearAll();
            if (temp.size()>0)
                readed.add( new IDandObject(stream[i].id, temp));
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

    void close() {
        synchronized (clients){
            for (ConnectionInfo o:clients.values()){
                o.close();
            }
            clients.clear();
            actualID=0;
            setListening(false);
        }
    }
}
