/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
class NetworkListener implements Runnable {
    /*
     * ALMOST PUBLIC DATA
     */
    AtomicBoolean isListening = new AtomicBoolean(false);

    final HashMap<SocketAddress, ArrayList<ByteBuffer>> inputList = new HashMap<SocketAddress, ArrayList<ByteBuffer>>();
    final LinkedList<SocketAddress> newClient = new LinkedList<SocketAddress>();

    final LinkedList<MyDatagram> messages = new  LinkedList<MyDatagram>();

    /*
     * INTERNAL DATA
     */
    Selector serverSelector;
    private final DatagramChannel serverChannel;
    private final SelectionKey serverKey;

    NetworkListener(int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(port);

        serverSelector = Selector.open();

        serverChannel = DatagramChannel.open();
        serverChannel.configureBlocking(false);

        DatagramSocket socket = serverChannel.socket();
        socket.bind(address);

        serverKey = serverChannel.register(serverSelector, SelectionKey.OP_READ);

        isListening.set(true);
    }

    public void run() {
        while (isListening.get()) {
            try {
                serverSelector.select();
                System.out.println( "Select ok" );
                if (!serverKey.isValid()) {
                    continue;
                }

                // Check what event is available and deal with it
                if (serverKey.isReadable()) {
                    readData(serverKey);
                } else if (serverKey.isWritable()) {
                    synchronized (messages) {
                        MyDatagram t = messages.poll();
                        if (t != null) {
                            serverChannel.send(t.data, t.address);
                        } else {
                            //data is empty
                            serverKey.interestOps(serverKey.interestOps() ^ SelectionKey.OP_WRITE);
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(NetworkListener.class.getName()).log(Level.SEVERE, null, ex);
                close();
            }

        }
    }

    private void close() {
        isListening.set(false);
    }

    private void readData(SelectionKey serverKey) {
        try {
            ByteBuffer input = ByteBuffer.allocate(1024);
            SocketAddress clientAddress = serverChannel.receive(input);
            input.flip();
            System.out.println( "Reading data, data as integer:"+input.asIntBuffer().get()+" from: "+clientAddress );

            synchronized(inputList){
                ArrayList<ByteBuffer> client = inputList.get(clientAddress);
                if (client==null){
                    client = acceptNewConnections(clientAddress);
                }
                synchronized(client){
                    client.add(input);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NetworkListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<ByteBuffer> acceptNewConnections(SocketAddress clientAddress) {
        System.out.println("New connection: "+clientAddress);
        final ArrayList<ByteBuffer> buf = new ArrayList<ByteBuffer>();
        inputList.put(clientAddress, buf);
        synchronized(newClient){
            newClient.add(clientAddress);
        }
        return buf;
    }
}
