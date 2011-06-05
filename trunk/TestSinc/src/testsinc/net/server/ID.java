/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.net.server;

/**
 *
 * @author mauro
 */
public class ID implements Comparable<ID>{
    long id;

    public ID(long id){
        this.id = id;
    }

    public int compareTo(ID o) {
        if (id>o.id)
            return 1;
        else if (o.id<id)
            return -1;
        return 0;
    }
}
