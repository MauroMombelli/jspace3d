/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import testsinc.net.server.ConnectionInfo;
import testsinc.net.shared.autentication.Login;

/**
 *
 * @author mauro
 */
public class UserContainer {
    private final int MAX_OBJECT_AT_TURN = 10;

    HashMap<Login, ConnectionInfo> users = new HashMap<Login, ConnectionInfo>();
    

    public void addUser(Login name, ConnectionInfo conn){
        if (!users.containsKey(name)){
            users.put(name, conn);
        }else{
            //utente già presente!
            users.get(name).close();
            users.put(name, conn);
        }
    }

    public void addAll(HashMap<Login, ConnectionInfo> waitingUser) {
        Set<Map.Entry<Login, ConnectionInfo>> mapSet = waitingUser.entrySet();
        for (Map.Entry<Login, ConnectionInfo> obj:mapSet){
            addUser(obj.getKey(), obj.getValue());
        }
    }

    public void update(){
        //create a copy of the original collection, because entryset is linked to original, and can cause error if item are removed or added while iterating
        for (Iterator<Map.Entry<Login, ConnectionInfo>> i = users.entrySet().iterator(); i.hasNext();) {
            Map.Entry<Login, ConnectionInfo> user = i.next();
            readData(user);
        }

    }

    private void readData(Entry<Login, ConnectionInfo> user) {
        if (user.getValue().isClosed()){
            users.remove(user.getKey());
        }

        ArrayList<Object> data = user.getValue().readAndClearAll();
        if (data.size()>MAX_OBJECT_AT_TURN){
            users.remove(user.getKey());
        }
    }
}
