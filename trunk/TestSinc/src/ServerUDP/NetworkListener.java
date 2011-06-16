/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import ServerUDP.client.Client;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
    final LinkedList<Client> newClient = new LinkedList<Client>();
    int ACTUAL_MAX_MTU=0;
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

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            if (netint.getMTU()>ACTUAL_MAX_MTU){
                ACTUAL_MAX_MTU = netint.getMTU();
            }
        System.out.println("Using max MTU of: "+ACTUAL_MAX_MTU);
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
                    //We don't need to send data, so shut down this cpu ungry feature :-)
                    serverKey.interestOps(serverKey.interestOps() ^ SelectionKey.OP_WRITE);
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
            ByteBuffer input = ByteBuffer.allocate(ACTUAL_MAX_MTU);
            SocketAddress clientAddress = serverChannel.receive(input);
            if (clientAddress==null){
                //null sender, means no data read. return now
                return;
            }
            input.flip();
            System.out.println( "Reading data, data as integer:"+input.asIntBuffer().get()+" from: "+clientAddress+" size:"+input.limit() );

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
            if (newClient.size()==0)
                return null;
            LinkedList<Client> tempNewClient = new LinkedList<Client>(newClient);
            newClient.clear();
            return tempNewClient;
        }
    }

    void banIP(HashSet<String> andRemoveBannedIP) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
