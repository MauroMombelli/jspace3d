/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.server;

import java.util.ArrayList;
import testsinc.net.server.ID;

/**
 *
 * @author mauro
 */
public class IDandObject {
    public ID id;
    public ArrayList<Object> obj;

    IDandObject(ID id, ArrayList<Object> objs) {
        this.id = id;
        obj = objs;
    }
}
