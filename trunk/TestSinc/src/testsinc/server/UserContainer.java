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
import testsinc.net.SyncObjectStream;
import testsinc.net.shared.autentication.Login;

/**
 *
 * @author mauro
 */
public class UserContainer {
    private final int MAX_OBJECT_AT_TURN = 10;

    HashMap<Login, User> users = new HashMap<Login, User>();
    

    public void addUser(Login name, RawConnection conn){
        User newUser = new User(conn);
        if (!users.containsKey(name)){
            users.put(name, newUser);
        }else{
            //utente già presente!
            users.get(name).close();
            users.put(name, newUser);
        }
    }

    public void addAll(HashMap<Login, RawConnection> waitingUser) {
        Set<Map.Entry<Login, RawConnection>> mapSet = waitingUser.entrySet();
        for (Map.Entry<Login, RawConnection> obj:mapSet){
            addUser(obj.getKey(), obj.getValue() );
        }
    }

    public void update(){
        //create a copy of the original collection, because entryset is linked to original, and can cause error if item are removed or added while iterating
        for (Iterator<Map.Entry<Login, User>> i = users.entrySet().iterator(); i.hasNext();) {
            Map.Entry<Login, User> user = i.next();
            readData(user);
        }

    }

    private void readData(Entry<Login, User> user) {
        if (user.getValue().isClosed()){
            users.remove(user.getKey());
        }

        ArrayList<Object> data = user.getValue().readAndClearAll();
        if (data.size()>MAX_OBJECT_AT_TURN){
            users.remove(user.getKey());
        }
    }
}
