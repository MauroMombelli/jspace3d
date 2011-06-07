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
import java.util.Iterator;
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
    //Selector clientListWrite;
    //Selector serverList;
    // The buffer into which we'll read data when it's available
    RawConnectionContainer streamContainer;
    private int loginByteSize;

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

    public ServerSelector(int port, RawConnectionContainer objLayer) {
        streamContainer = objLayer;
        address = new InetSocketAddress(port);
        try {
            selector = Selector.open();
            
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            DatagramSocket socket = channel.socket();
            socket.bind(address);

            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException ex) {
            Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        //System.out.println("Starting with key: "+selector.keys().size());
        while (streamContainer.isListening()) {
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
                            acceptNewConnections(key);
                        }// else if (key.isReadable()) {
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
    }

    private void acceptNewConnections(SelectionKey serverKey) throws IOException {
System.out.println("Accepted connection");
        DatagramChannel serverDatagramChannel = (DatagramChannel) serverKey.channel();
        SocketAddress receive = serverDatagramChannel.receive(ByteBuffer.allocate(0));
  
        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read

        //SelectionKey clientKey = socketChannel.register(selector, SelectionKey.OP_READ);

        DatagramChannel serverChannel = DatagramChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.connect(receive);

        //ClientSelector clientChannel = new ClientSelector();
        //SyncObjectStream connect = clientChannel.connect(receive, false);

        SelectionKey key = serverChannel.register(selector, SelectionKey.OP_READ);

        SyncObjectStream connect = new SyncObjectStream(key, serverChannel);
        key.attach(connect);


        System.out.println("Acceptated connection from socket:" + receive +" max input buffer: "+serverDatagramChannel.socket().getReceiveBufferSize()+" max output buffer"+serverDatagramChannel.socket().getSendBufferSize());
        
        //ConnectionInfo byteStream = new ConnectionInfo(connect.getKey(), loginByteSize);
        streamContainer.addClient(connect);
        //System.out.println("Acceptated connection from socket:" + socketChannel.socket().getRemoteSocketAddress()+" max input buffer: "+socketChannel.socket().getReceiveBufferSize()+" max output buffer"+socketChannel.socket().getSendBufferSize());
        //return byteStream;
        //return connect;
    }

    public void close() {
        streamContainer.closeAll();
        selector.wakeup();
    }
}
