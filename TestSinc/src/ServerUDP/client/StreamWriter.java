/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class StreamWriter {
    private DatagramChannel outputChannel;
    private ByteBuffer output;
    int MTU_MINUS_UDP_HEADER;
    public AtomicBoolean hasToWrite = new AtomicBoolean(false);
    SelectionKey myKey;
    
    public StreamWriter(SocketAddress address, int port, Selector serverSelector) {
        try {
            outputChannel = DatagramChannel.open();
            outputChannel.configureBlocking(false);
            InetSocketAddress adr = (InetSocketAddress) address;
            adr = new InetSocketAddress(adr.getHostName(), port);
            outputChannel.connect(adr);
            myKey = outputChannel.register(serverSelector, SelectionKey.OP_WRITE);
            myKey.attach(this);

            NetworkInterface network = NetworkInterface.getByInetAddress( ((InetSocketAddress)outputChannel.socket().getLocalSocketAddress()).getAddress() );
            MTU_MINUS_UDP_HEADER = network.getMTU()-100;
            System.out.println("connected with: "+adr+" "+network.getDisplayName()+" rilevated MTU: "+network.getMTU());

            output = ByteBuffer.allocateDirect(MTU_MINUS_UDP_HEADER);
            output.clear();
        } catch (IOException ex) {
            Logger.getLogger(StreamWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void write(ByteBuffer byteToWrite) throws IOException {
        //byteToWrite.flip();
        System.out.println("INFO Written1:" + output.position()+" "+byteToWrite.limit()+" "+output.capacity());
        System.out.println("INFO Written2:" + byteToWrite.position()+" "+byteToWrite.limit()+" "+byteToWrite.capacity());
        /*
        if (output.position() + byteToWrite.limit() > output.capacity()) {
            //datagram is full, send it
            System.out.println("Flush:" + byteToWrite.limit());
            flush();
        }
         *
         */
        System.out.println("Adding:" + byteToWrite.limit());
        output.put(byteToWrite);
    }

    public boolean flush() throws IOException {
        output.flip();
        if (output.limit()<=0){//if nothing to write
            System.out.println("NOT Written:" + output.limit());
            myKey.interestOps(myKey.interestOps() ^ SelectionKey.OP_WRITE);
            return false; //do nothing
        }
        /*
        if (output.position()!=0){
            System.out.println("Flipping buffer");
            output.flip();
        }else{
            System.out.println("NOT Flipping buffer");
        }*/
        

        //output.flip();
        int byteWritten = outputChannel.write(output);

        if (byteWritten != output.limit()) {
            System.out.println("Written only:" + byteWritten + " byte of " + output.limit());
            output.compact();
            hasToWrite.set(true);
            //myKey.interestOps(myKey.interestOps() & SelectionKey.OP_WRITE);
        } else {
            System.out.println("Written:" + byteWritten + " byte of " + output.limit());
            output.clear();
            hasToWrite.set(false);
            myKey.interestOps(myKey.interestOps() ^ SelectionKey.OP_WRITE);
        }
        return !hasToWrite.get();
    }

    public void writeNow(ByteBuffer t) throws IOException {
        write(t);
        flush();
    }

    public ByteBuffer getBuffer() {
        return ByteBuffer.allocate(MTU_MINUS_UDP_HEADER);
    }
}
