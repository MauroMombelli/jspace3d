/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.net.server;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author mauro
 */
public class ConnectionInfoContainer {

    long actualID=0;
    protected final TreeMap<ID, ConnectionInfo> clients = new TreeMap<ID, ConnectionInfo>();
 //   protected final Set<Map.Entry<ID, ConnectionInfo>> clientMapSet = clients.entrySet();

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
                ID id = getNewID();
                clients.put(id, obj);
                return id;
            }
        }
        return null;
    }

    public void removeAndCloseClient(ID id) {

        synchronized (clients){
            System.out.println("ConnectionInfoContainer closing client");
            ConnectionInfo client = clients.remove(id);
            client.close();
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
        ArrayList<Object> temp;
        ArrayList<IDandObject> readed=new ArrayList<IDandObject>();

        for (Map.Entry<ID, ConnectionInfo> stream:clients.entrySet()){
            temp = stream.getValue().readAndClearAll();
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
