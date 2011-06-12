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
public class StringPayload extends Payload{
    private static final byte dataID=0;

    String data;

    public StringPayload(String in){
        if (in.length()>Byte.MAX_VALUE)
            data = in.substring(0, Byte.MAX_VALUE);
        else
            data = in;
    }

    @Override
    public void read(ByteBuffer input) {
        int len = input.getInt();
        data = new String();
        for (int i=0; i < len; i++){//read data
            data+=input.getChar();
        }
    }

    @Override
    public void write(ByteBuffer output) {

        output.putInt( data.length() );

        for (int i=0; i < data.length(); i++){ //write data
            output.putChar( data.charAt(i) );
        }
    }

    @Override
    public int getSizeInByte() {
        if (data.length() > Byte.MAX_VALUE){
            data = data.substring(0, Byte.MAX_VALUE);
        }
        return 2 + data.length() * 2; //superclass, 2 for number of char, char*2(unicode, 2 byte per char)
    }

    @Override
    public byte getID() {
        return dataID;
    }
}
