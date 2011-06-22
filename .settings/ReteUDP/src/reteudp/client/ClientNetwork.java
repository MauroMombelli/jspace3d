/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reteudp.client;

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
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import reteudp.server.ServerNetwork;
import reteudp.stream.StreamContainer;
import reteudp.stream.StreamReader;
import reteudp.stream.StreamWriter;

/**
 *
 * @author mauro
 */
public class ClientNetwork implements Runnable{

    private int INPUT_PORT = 5001;
    private static final int OUTPUT_PORT = 5000;
    private int ACTUAL_MAX_MTU = 0;
    private final ByteBuffer bridge;
    private Selector serverSelector;
    private DatagramChannel serverChannel;
    private SelectionKey myKey;
    private AtomicBoolean isListening = new AtomicBoolean(false);
    private StreamContainer commLink;
    StreamWriter out;
    StreamReader in;

    public ClientNetwork() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            if (netint.getMTU() > ACTUAL_MAX_MTU) {
                ACTUAL_MAX_MTU = netint.getMTU();
            }
        }
        System.out.println("Using max MTU of: " + ACTUAL_MAX_MTU);

        bridge = ByteBuffer.allocateDirect(ACTUAL_MAX_MTU);
    }

    public void startNetwork(String serverAddress) throws IOException {
        startNetwork(new InetSocketAddress(serverAddress, OUTPUT_PORT));
    }

    private void startNetwork(SocketAddress serverAddress) throws IOException {
        InetSocketAddress address = new InetSocketAddress(INPUT_PORT);

        serverSelector = Selector.open();
        serverChannel = DatagramChannel.open();
        serverChannel.configureBlocking(false);

        DatagramSocket socket = serverChannel.socket();
        socket.bind(address);
        myKey = serverChannel.register(serverSelector, SelectionKey.OP_READ);

        isListening.set(true);

        out = new StreamWriter(serverAddress, OUTPUT_PORT, serverSelector);
        in = new StreamReader();
        commLink = new StreamContainer(out, in);

        //startLogic();
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
                            //we don't need to write here
                            k.interestOps(k.interestOps() ^ SelectionKey.OP_WRITE);
                        }
                    } else {
                        writeData();
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
        if (clientAddress!=null){
            bridge.flip();
            System.out.println("Reading data, data as integer:" + bridge.asIntBuffer().get() + " from: " + clientAddress + " size:" + bridge.limit());
            ByteBuffer data = ByteBuffer.allocate(bridge.limit());
            data.put(bridge);
            bridge.clear();
            in.addBuffer(data);
        }
    }

    private void writeData() throws IOException {
        out.flush();
    }

    private void close() {
        isListening.set(false);
    }

    public void run() {
        System.out.println("START LISTENER");
        startLogic();
        close();
        System.out.println("END LISTENER");
    }
}
