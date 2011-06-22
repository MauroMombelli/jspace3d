/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reteudp.server;

import reteudp.stream.StreamContainer;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import reteudp.stream.StreamReader;
import reteudp.stream.StreamWriter;

/**
 *
 * @author mauro
 */
public class ServerNetwork implements Runnable{
    private int ACTUAL_MAX_MTU = 0;
    private int INPUT_PORT = 5000;
    private final int OUTPUT_PORT = 5001;
    private Selector serverSelector;
    private DatagramChannel serverChannel;
    public AtomicBoolean isListening = new AtomicBoolean();
    private final ByteBuffer bridge;
    private SelectionKey myKey;
    private final LinkedList<StreamContainer> newStream = new LinkedList<StreamContainer>();
    private final HashMap<SocketAddress, StreamContainer> allStream = new HashMap<SocketAddress, StreamContainer>();
    private final LinkedList<SocketAddress> bannedIP = new LinkedList<SocketAddress>();


    public ServerNetwork() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            if (netint.getMTU() > ACTUAL_MAX_MTU) {
                ACTUAL_MAX_MTU = netint.getMTU();
            }
        }
        System.out.println("Using max MTU of: " + ACTUAL_MAX_MTU);

        bridge = ByteBuffer.allocateDirect(ACTUAL_MAX_MTU);
    }

    private void startNetwork() throws IOException {
        InetSocketAddress address = new InetSocketAddress(INPUT_PORT);

        serverSelector = Selector.open();
        serverChannel = DatagramChannel.open();
        serverChannel.configureBlocking(false);

        DatagramSocket socket = serverChannel.socket();
        socket.bind(address);
        myKey = serverChannel.register(serverSelector, SelectionKey.OP_READ);

        isListening.set(true);

        startLogic();
    }

    private void startLogic() {
        while (isListening.get()) {
            try {
                serverSelector.select();

                Set<SelectionKey> list = serverSelector.selectedKeys();
                for (SelectionKey k : list) {
                    if (!k.isValid()) {
                        continue;
                    }

                    if (myKey.equals(k)) {
                        // Check what event is available and deal with it
                        if (k.isReadable()) {
                            readData();
                        } else if (k.isWritable()) {
                            //writeData();
                            //We don't need to send data, so shut down this cpu angry feature :-)
                            k.interestOps(k.interestOps() ^ SelectionKey.OP_WRITE);
                        }
                    } else {
                        StreamWriter w = (StreamWriter) k.attachment();
                        try{
                            
                            if (w != null) {
                                w.flush();
                            }
                        }catch (IOException ex) {
                            Logger.getLogger(ServerNetwork.class.getName()).log(Level.SEVERE, null, ex);
                            banIP( w.getIP() );
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerNetwork.class.getName()).log(Level.SEVERE, null, ex);
                close();
            }

        }
    }

    private void readData() throws IOException {
        SocketAddress clientAddress = serverChannel.receive(bridge);
        if (clientAddress == null) {
            //null sender, means no data read. return now
            return;
        }

        StreamContainer client;
        client = allStream.get(clientAddress);
        try {
            if (client == null) {
                client = createNewClient(clientAddress);
            }

            if (client == null) {
                return;
            }
            bridge.flip();
            System.out.println("Reading data, data as integer:" + bridge.asIntBuffer().get() + " from: " + clientAddress + " size:" + bridge.limit());
            ByteBuffer data = ByteBuffer.allocate(bridge.limit());
            data.put(bridge);
            bridge.clear();
            client.addBuffer(data);
        } catch (IOException ex) {
            Logger.getLogger(ServerNetwork.class.getName()).log(Level.SEVERE, null, ex);
            client.close();
            if (client != null) {
                synchronized(allStream){
                    allStream.remove(clientAddress);
                }
            }
            banIP(clientAddress);
        }
    }

    private void close() {
        isListening.set(false);
    }

    private StreamContainer createNewClient(SocketAddress clientAddress) throws IOException {
        System.out.println("New connection: " + clientAddress);
        if (bannedIP.contains(clientAddress)){
            System.out.println("IP is banned, connection not accepted from: " + clientAddress);
            return null;
        }
        StreamWriter w = new StreamWriter(clientAddress, OUTPUT_PORT, serverSelector);
        StreamReader r = new StreamReader();
        StreamContainer c = new StreamContainer(w, r);
        synchronized (newStream) {
            newStream.add(c);
        }
        synchronized(allStream){
            allStream.put(clientAddress, c);
        }
        return c;
    }

    private void banIP(SocketAddress clientAddress) {
        System.out.println("Banning IP: " + clientAddress);
        bannedIP.add(clientAddress);
        synchronized(allStream){
            StreamContainer s = allStream.get(clientAddress);
            if (s!=null){
                s.close();
                allStream.remove(clientAddress);
            }
        }
    }

    public void run() {
        System.out.println("START LISTENER");
        try {
            startNetwork();
        } catch (IOException ex) {
            Logger.getLogger(ServerNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        close();
        System.out.println("END LISTENER");
    }
}
