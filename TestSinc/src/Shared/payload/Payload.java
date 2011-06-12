/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Shared.payload;

import java.nio.ByteBuffer;

/**
 *
 * @author mauro
 */
public abstract class Payload {

    public abstract void write(ByteBuffer output);

    public abstract void read(ByteBuffer input) ;

    public abstract int getSizeInByte();

    public abstract byte getID();
}
