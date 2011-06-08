/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ClientUDP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
class NetworkClient{
    private final DatagramChannel outputChannel;
    private final DatagramChannel inputChannel;
    private final InetSocketAddress serverAddress;
    boolean isConnect = true;

    NetworkClient(String ip, int portaOutput, int portaInput) throws IOException {
        serverAddress =new InetSocketAddress(ip, portaOutput);
        outputChannel = DatagramChannel.open();
        outputChannel.configureBlocking(false);
        outputChannel.connect(serverAddress);

        inputChannel = DatagramChannel.open();
        inputChannel.configureBlocking(false);
        inputChannel.socket().bind(new InetSocketAddress(portaInput));
    }

    public int write(ByteBuffer c) throws IOException {
        c.flip();
        return outputChannel.write(c);
    }

    public ByteBuffer readDatagram(){
        ByteBuffer input = ByteBuffer.allocate(1024);
        InetSocketAddress sender = null;
        boolean readed = false;
        while (!readed ){
            input.clear();
            try {
                sender = (InetSocketAddress)inputChannel.receive(input);
                //System.out.println( "Net Reading data, data as integer:"+input.asIntBuffer().get()+" form: "+sender+" server: "+serverAddress );
                if (sender == null || serverAddress.getHostName().equals(sender.getHostName())) {
                    readed = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
                readed = true;
            }
        }
        //input.flip();
        if (sender==null)
            return null;
        input.flip();
        return input;
    }

    boolean isConnect() {
        return isConnect;
    }

}
