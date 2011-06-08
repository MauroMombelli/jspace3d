/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerUDP.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 *
 * @author mauro
 */
public class ClientWriter {
    private final DatagramChannel outputChannel;

    ClientWriter(SocketAddress address) throws IOException {
        outputChannel = DatagramChannel.open();
        outputChannel.configureBlocking(false);
        InetSocketAddress adr = (InetSocketAddress)address;
        adr = new InetSocketAddress(adr.getHostName(), 5001);
        outputChannel.connect(adr);
    }

    void write(ByteBuffer t) throws IOException {
        //t.flip();
        int byteWritten = outputChannel.write(t);
        System.out.println( "Writed:"+byteWritten+" byte" );
        /*
        if (byteWritten!=t.capacity()){
            System.out.println( "Reading data, data as integer:"+t.asIntBuffer().get() );
        }
         */
    }

}
