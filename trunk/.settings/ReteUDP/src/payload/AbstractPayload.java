/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package payload;

import java.nio.ByteBuffer;

/**
 *
 * @author mauro
 */
public abstract class AbstractPayload {

    private final byte payloadID;

    public AbstractPayload(byte ID){
        payloadID = ID;
    }

    public byte getID(){
        return payloadID;
    }

    public abstract ByteBuffer writeToBuffer(ByteBuffer buffer);

    public abstract int getSizeInByte();
}
