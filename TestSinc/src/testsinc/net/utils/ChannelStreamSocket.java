/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.net.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.SyncObjectStream;
import testsinc.net.server.ServerSelector;

/**
 *
 * @author mauro
 */
public class ChannelStreamSocket {
    public static boolean writeData(SelectionKey key) {
        DatagramChannel socketChannel = (DatagramChannel) key.channel();
        SyncObjectStream byteStream = (SyncObjectStream) key.attachment();
        byte out[] = byteStream.getWriteData();
        if (out==null)
            return false;
        ByteBuffer buf = ByteBuffer.wrap( out );
        //buf.flip();
        try {
            int numBytesWritten = socketChannel.write(buf);

            if (numBytesWritten!=out.length){
                System.out.println("Not all data has been written");
                byteStream.addUnwrittenData(Arrays.copyOfRange(out, numBytesWritten, out.length));
            }
            return true;
            //System.out.println("Written: "+numBytesWritten+" byte"+buf);
        } catch (IOException ex) {
            System.out.println("Error writing data to socket"+socketChannel.socket().getRemoteSocketAddress());
            //Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            byteStream.close();
            return false;
        }
    }

    static final int PACKET_SIZE = 1024;
    private static ByteBuffer readBuffer = ByteBuffer.allocateDirect(PACKET_SIZE);
    public static void readData(SelectionKey key) {
        DatagramChannel socketChannel = (DatagramChannel) key.channel();
        SyncObjectStream byteStream = (SyncObjectStream) key.attachment();

        // Clear out our read buffer so it's ready for new data
        readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException ex) {
            //Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Wrong disconnection from socket:"+socketChannel.socket().getRemoteSocketAddress());
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            byteStream.close();
            key.cancel();
            try {
                socketChannel.close();
            } catch (IOException ex1) {
                Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return;
        }

        if (numRead == -1) {
            System.out.println("Good disconnection from socket:"+socketChannel.socket().getRemoteSocketAddress());
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            byteStream.close();
            try {
                key.channel().close();
            } catch (IOException ex) {
                Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
            }
            key.cancel();
            return;
        }

        // Hand the data off to the rest of the program
        byte lettura[] = new byte[numRead];
        readBuffer.flip();
        readBuffer.get(lettura);
        System.out.println("Data in buffer: "+numRead+" "+new String(lettura) );
        byteStream.addInput(lettura);
    }
}
