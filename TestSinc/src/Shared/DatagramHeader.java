/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Shared;

import java.nio.ByteBuffer;

/**
 *
 * @author mauro
 */
public class DatagramHeader{

    public static final byte FAKETURN = Byte.MAX_VALUE;
    
    private byte datagramNumber;
    private byte turnNumber;

    public DatagramHeader(byte datagramNumber, byte turnNumber){
        this.datagramNumber=datagramNumber;
        this.turnNumber = turnNumber;
    }

    public static DatagramHeader read(ByteBuffer input) {
        return new DatagramHeader(input.get(), input.get());
    }

    public void write(ByteBuffer output) {
        output.put(datagramNumber);
        output.put(turnNumber);
    }

    public static byte getSizeInByte(){
        return 2;
    }

    public byte getTurn() {
        return turnNumber;
    }

    public byte getNumber() {
        return datagramNumber;
    }

}
