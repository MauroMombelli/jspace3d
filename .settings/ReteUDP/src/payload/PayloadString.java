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
public class PayloadString extends AbstractPayload{

    public static final byte ID = 1;
    String s = new String();
    
    public PayloadString(){
        super( ID );
    }

    public PayloadString(String s){
        this();
        this.s = s;
        if ( s.length()> (Byte.MAX_VALUE-Byte.MIN_VALUE) ){
            System.out.println("String is too long, cutting");
            s = s.substring( 0, (Byte.MAX_VALUE-Byte.MIN_VALUE) );
        }
    }

    public PayloadString(ByteBuffer data) {
        this();
        int len = data.get();
        len += -Byte.MIN_VALUE;
        for (int i=0; i < len; i++ ){
            s+=data.getChar();
        }
    }

    @Override
    public ByteBuffer writeToBuffer(ByteBuffer buffer) {
        if (s.length()<=0) //if the string is empty, don't write it!
            return buffer;

        buffer.put( this.getID() );
        buffer.put((byte)(s.length()+Byte.MIN_VALUE));
        for (int i=0; i<s.length();i++){
            buffer.putChar( s.charAt(i) );
        }
        return buffer;
    }

    @Override
    public int getSizeInByte() {
        return 1+1+( s.length() )*2;
    }

}
