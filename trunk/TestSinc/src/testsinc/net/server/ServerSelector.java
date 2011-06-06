/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.net.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.utils.ChannelStream;

/**
 *
 * @author mauro
 */
public class ServerSelector implements Runnable {

    ConnectionInfoContainer streamContainer;

    public ServerSelector(int port, ConnectionInfoContainer objLayer) {
        streamContainer = objLayer;
        SocketAddress address = new InetSocketAddress(port);
        try {
            DatagramChannel channel = DatagramChannel.open();
            DatagramSocket socket = channel.socket();
            socket.bind(address);
        } catch (IOException ex) {
            Logger.getLogger(ServerSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        //System.out.println("Starting with key: "+selector.keys().size());
        while (streamContainer.isListening()) {
            
        }
    }

    public void close() {
        streamContainer.close();
    }
}
