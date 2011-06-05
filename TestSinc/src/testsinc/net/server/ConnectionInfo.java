/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.net.server;

import testsinc.net.shared.autentication.Seed;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import testsinc.net.SyncObjectStream;

/**
 *
 * @author mauro
 */
public class ConnectionInfo extends SyncObjectStream{

    public ID id;
    private AtomicLong lastReceivedData = new AtomicLong( Calendar.getInstance().getTimeInMillis() );
    private Seed passSeed;
/*
    public ConnectionInfo(SelectionKey k, SocketChannel c) {
        super (k, c);
    }
*/
    public ConnectionInfo(SelectionKey k, SocketChannel c, int maxObjSize) {
        super (k, c, maxObjSize);
        sendPasswordSeed();
    }

    @Override
    public void addInput(byte[] array) {
        super.addInput(array);
        lastReceivedData.set( Calendar.getInstance().getTimeInMillis() );
    }

    public long getTime(){
        return lastReceivedData.get();
    }

    private void sendPasswordSeed() {
        passSeed = new Seed();
        write( passSeed );
    }
    
}