/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import ServerUDP.client.Client;
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
import java.util.SortedSet;
import java.util.TreeSet;
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
    final LinkedList<Client> newClient = new LinkedList<Client>();

    final LinkedList<MyDatagram> messages = new  LinkedList<MyDatagram>();

    /*
     * INTERNAL DATA
     */
    Selector serverSelector;
    private final DatagramChannel serverChannel;
    private final SelectionKey serverKey;

    NetworkListener(int inputPort) throws IOException {

        InetSocketAddress address = new InetSocketAddress(inputPort);

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

    SortedSet<Integer> ris = new TreeSet<Integer>();
    private void readData(SelectionKey serverKey) {
        try {
            ByteBuffer input = ByteBuffer.allocate(1024);
            SocketAddress clientAddress = serverChannel.receive(input);
            input.flip();
            System.out.println( "Reading data, data as integer:"+input.asIntBuffer().get()+" from: "+clientAddress );
            ris.add(input.asIntBuffer().get());

            synchronized(inputList){
                ArrayList<ByteBuffer> client = inputList.get(clientAddress);
                if (client==null){
                    client = acceptNewConnections(clientAddress);
                }
                if (client!=null){
                    synchronized(client){
                        client.add(input);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NetworkListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<ByteBuffer> acceptNewConnections(SocketAddress clientAddress) {
        System.out.println("New connection: "+clientAddress);
        final ArrayList<ByteBuffer> buf = new ArrayList<ByteBuffer>();
        Client temp;
        try {
            temp = new Client(clientAddress, buf);
        } catch (IOException ex) {
            Logger.getLogger(NetworkListener.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        synchronized(inputList){
            inputList.put(clientAddress, buf);
            synchronized(newClient){
                newClient.add(temp);
            }
            return buf;
        }
    }

    public LinkedList<Client> getAndRemoveWaitingClient(){
        synchronized(newClient){
            LinkedList<Client> tempNewClient = new LinkedList<Client>(newClient);
            newClient.clear();
            return tempNewClient;
        }
    }
}
