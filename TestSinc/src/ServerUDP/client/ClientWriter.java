/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class ClientWriter {
    private DatagramChannel outputChannel;
    private ByteBuffer output;

    public ClientWriter(SocketAddress address) {

        try {
            outputChannel = DatagramChannel.open();
            outputChannel.configureBlocking(false);
            InetSocketAddress adr = (InetSocketAddress) address;
            adr = new InetSocketAddress(adr.getHostName(), 5001);
            outputChannel.connect(adr);

            NetworkInterface network = NetworkInterface.getByInetAddress( ((InetSocketAddress)outputChannel.socket().getLocalSocketAddress()).getAddress() );
            int MTU_MINUS_UDP_HEADER = network.getMTU()-100;
            System.out.println(network.getDisplayName()+" rilevated MTU: "+network.getMTU());

            output = ByteBuffer.allocateDirect(MTU_MINUS_UDP_HEADER);
        } catch (IOException ex) {
            Logger.getLogger(ClientWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(ByteBuffer byteToWrite) throws IOException {
        if (output.limit() + byteToWrite.limit() > output.capacity()) {
            //datagram is full, send it
            flush();
        }
        output.put(byteToWrite);
    }

    public void flush() throws IOException {
        output.flip();
        int byteWritten = outputChannel.write(output);

        if (byteWritten != output.limit()) {
            System.out.println("Written only:" + byteWritten + " byte of " + output.limit());
            output.compact();
        } else {
            output.clear();
        }
    }
}
