/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.net.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.SyncObjectStream;

/**
 *
 * @author mauro
 */
public class ByteStream {

    public static void intToByteArray(int value, byte[] destination) {
        destination[0] = (byte) (value >>> 24);
        destination[1] = (byte) (value >>> 16);
        destination[2] = (byte) (value >>> 8);
        destination[3] = (byte) (value);
    }

    public static int byteArrayToInt(byte[] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    public static byte[] toBytes(Object object) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oos;
        try {
            oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            baos.flush();
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(SyncObjectStream.class.getName()).log(Level.SEVERE, null, ex);
        }
/*
        String test = (String)toObject(baos.toByteArray());
        System.out.println("TEST OBJ:"+test);
*/
        return baos.toByteArray();
    }

    public static Object toObject(byte[] bytes) {
        Object object = null;
        try {
            object = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes)).readObject();
        } catch (java.io.IOException ex) {
            Logger.getLogger(SyncObjectStream.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.lang.ClassNotFoundException ex) {
            Logger.getLogger(SyncObjectStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return object;
    }
}
