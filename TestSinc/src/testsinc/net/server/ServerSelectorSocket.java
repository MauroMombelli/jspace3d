/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.net.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.utils.ByteStream;
import testsinc.net.shared.autentication.Login;
import testsinc.net.utils.ChannelStream;

/**
 *
 * @author mauro
 */
public class ServerSelectorSocket implements Runnable {

    Selector selector;
    //Selector clientListWrite;
    //Selector serverList;
    // The buffer into which we'll read data when it's available
    ConnectionInfoContainer streamContainer;
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
    public ServerSelectorSocket(int port, ConnectionInfoContainer objLayer) {
        streamContainer = objLayer;
        try {
            selector = Selector.open();
            // Create a non-blocking server socket channel on port, and listen to it
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port));

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
        loginByteSize=ByteStream.toBytes(new Login()).length;
        System.out.println("Waiting for a login of max size:"+loginByteSize);
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
                        if (key.isAcceptable()) {
                            acceptNewConnections(key);
                        } else if (key.isReadable()) {
                            ChannelStream.readData(key);
                        } else if (key.isWritable()) {
                            ChannelStream.writeData(key);
                        }
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

    private ConnectionInfo acceptNewConnections(SelectionKey serverKey) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) serverKey.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        SelectionKey clientKey = socketChannel.register(selector, SelectionKey.OP_READ);

        ConnectionInfo byteStream = new ConnectionInfo(clientKey, socketChannel, loginByteSize);
        clientKey.attach(byteStream);
        streamContainer.addClient(byteStream);
        System.out.println("Acceptated connection from socket:" + socketChannel.socket().getRemoteSocketAddress()+" max input buffer: "+socketChannel.socket().getReceiveBufferSize()+" max output buffer"+socketChannel.socket().getSendBufferSize());
        return byteStream;
    }

    public void close() {
        streamContainer.close();
    }
}
