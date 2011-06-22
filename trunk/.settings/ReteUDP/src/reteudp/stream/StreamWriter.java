/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reteudp.stream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import payload.AbstractPayload;

/**
 *
 * @author mauro
 */
public class StreamWriter {

    AtomicBoolean isValid = new AtomicBoolean(true);
    private final DatagramChannel outputChannel;
    private final int MTU_MINUS_UDP_HEADER;
    private final ByteBuffer output;
    private final SelectionKey myKey;
    Selector serverSelector;

    private final TreeMap<Byte, LinkedList<AbstractPayload>> outputList = new TreeMap<Byte, LinkedList<AbstractPayload>>();
    private byte datagramNumber=0;

    public StreamWriter(SocketAddress address, int port, Selector serverSelector) throws IOException {
        this.serverSelector = serverSelector;
        
        outputChannel = DatagramChannel.open();
        outputChannel.configureBlocking(false);
        InetSocketAddress adr = (InetSocketAddress) address;
        adr = new InetSocketAddress(adr.getHostName(), port);
        outputChannel.connect(adr);

        NetworkInterface network = NetworkInterface.getByInetAddress(((InetSocketAddress) outputChannel.socket().getLocalSocketAddress()).getAddress());
        MTU_MINUS_UDP_HEADER = network.getMTU() - 100;
        System.out.println("connected with: " + adr + " " + network.getDisplayName() + " rilevated MTU: " + network.getMTU());

        output = ByteBuffer.allocateDirect(MTU_MINUS_UDP_HEADER);
        output.clear();

        myKey = outputChannel.register(serverSelector, SelectionKey.OP_WRITE);
        myKey.attach(this);
    }

    public void flush() throws IOException {
        prepareDatagram();
        System.out.println("Flush activated"+output.position()+"of"+output.limit());


        //if (output.position() != 0) {
            System.out.println("Flipping buffer");
            output.flip();
        //}

        System.out.println( "INFO buffer output: "+output.position()+"of"+output.limit() );
        if (output.limit() == output.position()) {
            //there is nothing to write
            System.out.println("there is nothing to write");
            synchronized (myKey) {
                myKey.interestOps(myKey.interestOps() ^ SelectionKey.OP_WRITE);
            }
        }else{
            int writed = outputChannel.write(output);
            System.out.println( "writed: "+writed+"of"+output.limit() );
            //myKey.interestOps(myKey.interestOps() ^ SelectionKey.OP_WRITE);
        }
        output.clear();
    }

    void close() {
        System.out.println("Closing output");
        isValid.set(false);
        synchronized (myKey) {
            myKey.cancel();
        }
        try {
            outputChannel.disconnect();
            outputChannel.close();
        } catch (IOException ex) {
            Logger.getLogger(StreamWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/*
    void write(ByteBuffer data) {
        output.put(data);
        synchronized (myKey) {
            myKey.interestOps(myKey.interestOps() & SelectionKey.OP_WRITE);
        }
        serverSelector.wakeup();

    }
*/
    public SocketAddress getIP() {
        return outputChannel.socket().getRemoteSocketAddress();
    }

    public void write(AbstractPayload p, byte turn){
        synchronized(outputList){
            LinkedList<AbstractPayload> t = outputList.get(turn);
            if (t==null){
                t = new LinkedList<AbstractPayload>();
                outputList.put(turn, t);
            }
            t.add(p);
        }
    }

    private void write(LinkedList<AbstractPayload> pList, Byte turn) {
        synchronized(outputList){
            LinkedList<AbstractPayload> t = outputList.get(turn);
            if (t==null){
                t = new LinkedList<AbstractPayload>();
                outputList.put(turn, t);
            }
            t.addAll(pList);
        }
    }

    private void prepareDatagram() {
        Entry<Byte, LinkedList<AbstractPayload>> e = null;
        synchronized(outputList){
            e = outputList.pollFirstEntry();
        }
        if (e == null || e.getValue()==null || e.getValue().size()<=0){
            //if treemap is ampty, if key is't associated to list or key's list is empty do nothing
            return;
        }
        if (output.remaining()>2){ //ouput shold be clear, but a check is always a good idea
            //writing header
            output.put(datagramNumber);
            datagramNumber+=1;
            output.put( e.getKey() );
            
            LinkedList<AbstractPayload> t = e.getValue();
            AbstractPayload temp;
            while ( (temp = t.poll())!=null && temp.getSizeInByte()<=output.remaining() ){
                temp.writeToBuffer(output);
            }
            if (temp!=null){//if not all data has been written
                t.addFirst(temp); //put last not writtend Payload back in the list
                write(t, e.getKey()); //put the list back in the Tree
            }
        }else{
            System.out.println("Prepare datagram error: Very strange error, MTU seems to be <= 2");
        }

    }

}
