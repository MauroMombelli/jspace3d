/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.net.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.SyncObjectStream;
import testsinc.server.RawConnectionContainer;

/**
 *
 * @author mauro
 */
public class ServerSelector implements Runnable {

    Selector selector;
    //Selector selectorClient;
    HashMap<SocketAddress, SyncObjectStream> clients = new HashMap<SocketAddress, SyncObjectStream>();
    //Selector clientListWrite;
    //Selector serverList;
    // The buffer into which we'll read data when it's available
    RawConnectionContainer streamContainer;

    final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();
    //private int loginByteSize;

    /*
    public ServerSelector(int port) {
    try {
    selector = Selector.open();
    // Create a non-blocking server socket channel on port, and listen to it
    serverChannel = ServerSocketChannel.open();
    serverChannel.configureBlocking(false);
    serverChannel.socket().bind(new InetSocketAddress(port));

    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    } catch (IOException ex) {
    Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
     */
    SocketAddress address;
    SelectionKey serverKey;
    DatagramChannel serverChannel;

    public ServerSelector(int port, RawConnectionContainer objLayer) {
        streamContainer = objLayer;
        address = new InetSocketAddress(port);
        try {
            selector = Selector.open();

            serverChannel = DatagramChannel.open();
            serverChannel.configureBlocking(false);
            DatagramSocket socket = serverChannel.socket();
            socket.bind(address);
            serverKey = serverChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException ex) {
            Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        //System.out.println("Starting with key: "+selector.keys().size());
        while (streamContainer.isListening() && selector.isOpen()) {
            try {
                //System.out.println("Actual key: "+selector.keys().size());
                selector.select();
                //System.out.println("2 Actual key: "+selector.keys().size());
                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();

                    selectedKeys.remove();

                    synchronized (key) {
                        if (!key.isValid()) {
                            continue;
                        }

                        // Check what event is available and deal with it
                        if (key.isReadable()) {
                            readData(key);
                        } else if (key.isWritable()) {
                            synchronized(messages){
                                Message t = messages.poll();
                                if (t!=null){
                                    serverChannel.send(t.data, t.address);
                                }else{
                                    //data is empty
                                    key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
                                }
                            }
                        }

                        // else if (key.isReadable()) {
                        //    ChannelStreamSocket.readData(key);
                        //} else if (key.isWritable()) {
                        //    ChannelStreamSocket.writeData(key);
                        //}

                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
                streamContainer.setListening(false);
            }
        }


        try {
            selector.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
        streamContainer.closeAll();
    }
/*
    private void acceptNewConnections(SocketAddress clientAdress) throws IOException {

        DatagramChannel serverChannel = DatagramChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.connect(clientAdress);
        serverChannel.register(selector, SelectionKey.OP_READ);

        //ClientSelector clientChannel = new ClientSelector();
        //SyncObjectStream connect = clientChannel.connect(receive, false);

        SyncObjectStream connect = new SyncObjectStream(serverChannel);


        System.out.println("Acceptated connection from socket:" + clientAdress);


        //ConnectionInfo byteStream = new ConnectionInfo(connect.getKey(), loginByteSize);
        streamContainer.addClient(connect);
        //System.out.println("Acceptated connection from socket:" + socketChannel.socket().getRemoteSocketAddress()+" max input buffer: "+socketChannel.socket().getReceiveBufferSize()+" max output buffer"+socketChannel.socket().getSendBufferSize());
        //return byteStream;
        //return connect;
    }
*/
    private SyncObjectStream acceptNewConnections(SocketAddress clientAdress) throws IOException {
        System.out.println("New client: "+clientAdress);
        SyncObjectStream connect = new SyncObjectStream(serverKey);
        clients.put(clientAdress, connect);
        streamContainer.addClient(connect);
        return connect;
    }
    
    public void close() {
        streamContainer.closeAll();
        selector.wakeup();
    }

    private void readData(SelectionKey key) {
        DatagramChannel serverDatagramChannel = (DatagramChannel) key.channel();
        try {
            ByteBuffer input = ByteBuffer.allocate(1024);
            SocketAddress clientAdress = serverDatagramChannel.receive(input);
            System.out.println("Reading data");
            SyncObjectStream client = clients.get(clientAdress);
            if (client==null){
                client = acceptNewConnections(clientAdress);
            }
            client.addInput(input);
        } catch (IOException ex) {
            Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeData(SelectionKey key) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
