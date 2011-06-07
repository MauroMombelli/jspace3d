/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.server;

import testsinc.net.SyncObjectStream;

/**
 *
 * @author mauro
 */
public class RawConnection{
    SyncObjectStream stream;

    RawConnection(SyncObjectStream obj) {
        stream = obj;
    }

    void close() {
        stream.close();
    }

    SyncObjectStream getStream() {
        return stream;
    }

}
