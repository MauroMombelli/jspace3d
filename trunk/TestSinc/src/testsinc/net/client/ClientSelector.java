/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.net.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.SyncObjectStream;
import testsinc.net.utils.ChannelStreamSocket;

/**
 *
 * @author mauro
 */
public class ClientSelector implements Runnable {

    SyncObjectStream server;
    Selector selector;

    public void run() {
        if (server.isValidKey() == null) {
            System.err.println("You must connect the selector before run");
            return;
        }

        while (!server.isClosed()) {
            System.err.println("Server is open");
            update();
        }
        try {
            selector.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SyncObjectStream connect(String ip, int port, boolean locking) {
        return connect(new InetSocketAddress(ip, port), locking);
    }

    public SyncObjectStream connect(SocketAddress address, boolean b) {
        try {
            selector = Selector.open();
            DatagramChannel serverChannel = DatagramChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.connect(address);
            /*
            while (locking && !serverChannel.finishConnect()) {
            ;
            }
             */
            System.out.println("Connesso");
            SelectionKey key = serverChannel.register(selector, SelectionKey.OP_READ);

            server = new SyncObjectStream(key, serverChannel);
            key.attach(server);

            return server;
        } catch (IOException ex) {
            Logger.getLogger(ClientSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void close() {
        server.close();
    }

    private void update() {
        try {
            selector.select();

            // Iterate over the set of keys for which events are available
            Iterator selectedKeys = this.selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = (SelectionKey) selectedKeys.next();
                selectedKeys.remove();

                if (!key.isValid()) {
                    continue;
                }

                // Check what event is available and deal with it
                if (key.isReadable()) {
                    ChannelStreamSocket.readData(key);
                } else if (key.isWritable()) {
                    ChannelStreamSocket.writeData(key);
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(ClientSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
