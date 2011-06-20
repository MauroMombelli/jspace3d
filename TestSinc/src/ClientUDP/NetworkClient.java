/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientUDP;

import ServerUDP.client.StreamWriter;
import Shared.DatagramHeader;
import Shared.PayloadContainer;
import Shared.payload.Payload;
import Shared.payload.StringPayload;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
class NetworkClient implements Runnable {

    ;
    private final DatagramChannel inputChannel;
    private final InetSocketAddress serverAddress;
    boolean isConnect = true;
    int MTU;
    final PayloadContainer data;
    Selector selector;
    //private final SelectionKey outKey;
    private final SelectionKey inKey;

    NetworkClient(String ip, int portaOutput, int portaInput) throws IOException {
        selector = Selector.open();
        serverAddress = new InetSocketAddress(ip, portaOutput);
        /*
        outputChannel = DatagramChannel.open();
        outputChannel.configureBlocking(false);
        outputChannel.connect(serverAddress);
*/
        StreamWriter ouputChannel = new StreamWriter(serverAddress, 5000);
        data = new PayloadContainer(ouputChannel);

        inputChannel = DatagramChannel.open();
        inputChannel.configureBlocking(false);
        inputChannel.socket().bind(new InetSocketAddress(portaInput));
/*
        NetworkInterface net = NetworkInterface.getByInetAddress(outputChannel.socket().getLocalAddress());
        MTU = net.getMTU() - 100;//-100 is for UDP header
        System.out.println("Rilevated MTU: " + MTU);

        outKey = outputChannel.register(selector, SelectionKey.OP_WRITE);
*/
        inKey = inputChannel.register(selector, SelectionKey.OP_READ);
    }

    public int write(ByteBuffer c) throws IOException {
        c.flip();
        return outputChannel.write(c);
    }

    public ByteBuffer readDatagram() {
        ByteBuffer input = ByteBuffer.allocate(MTU);
        InetSocketAddress sender = null;
        boolean readed = false;
        while (!readed) {
            input.clear();
            try {
                sender = (InetSocketAddress) inputChannel.receive(input);

                //System.out.println( "Net Reading data, data as integer:"+input.asIntBuffer().get()+" form: "+sender+" server: "+serverAddress );
                if (sender != null) {
                    readed = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
                readed = true;
            }
        }
        //input.flip();
        if (sender == null) {
            return null;
        }
        input.flip();
        return input;
    }

    boolean isConnect() {
        return isConnect;
    }

    public ByteBuffer getNewByteBuffer() {
        return ByteBuffer.allocate(MTU);
    }

    public void add(Payload p) {/*
        data.add(p);
        if (data.hasToWrite()) {
            outKey.interestOps(outKey.interestOps() & SelectionKey.OP_WRITE);
        }
  */  }

    public StringPayload getString() {
        return data.getString();
    }

    public void run() {/*
        while (isConnect) {
            try {
                System.out.println("Running");
                selector.select();
System.out.println("select ok!");
                // Check what event is available and deal with it
                if (inKey.isReadable()) {
                    System.out.println("selectr read!");
                    data.read(readDatagram());
                } else if (outKey.isWritable()) {
                    System.out.println("select write!");
                    ByteBuffer output = getNewByteBuffer();
                    int write = data.write(output, DatagramHeader.FAKETURN);
                    if (write>0){
                        if (write(output) != output.limit()) {
                            System.out.println("ERRORE!");
                        }else{
                            System.out.println("Written:"+output.limit());
                        }
                    }else{
                        System.out.println("No data to write");
                    }
                    //We don't need to send data, so shut down this cpu ungry feature :-)
                    if (!data.hasToWrite()) {
                        outKey.interestOps(outKey.interestOps() ^ SelectionKey.OP_WRITE);
                    }
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
                close();
            }

        }

   */ }

    private void close() {
        isConnect = false;
    }
}
*