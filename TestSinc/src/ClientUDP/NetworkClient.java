/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ClientUDP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 *
 * @author mauro
 */
class NetworkClient{
    DatagramChannel client;

    NetworkClient(String ip, int porta) throws IOException{
        client = DatagramChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(ip, porta));
    }

    int write(ByteBuffer c) throws IOException {
        c.flip();
        return client.write(c);
    }

}
