/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class Client {

    private final ArrayList<ByteBuffer> input;
    private final ClientWriter output;

    public Client(SocketAddress address, ArrayList<ByteBuffer> input) throws IOException {
        this.input = input;
        output = new ClientWriter(address);
    }

    int readedData = 0;
    public void echo() {
        synchronized (input) {
            //while(input.size()>0){
            if (input.size() > 0) {
                ByteBuffer t = input.get(0);
                input.remove(0);
                try {
                    //t.flip();
                    System.out.println("Sending number"+readedData+" size:"+t.limit());
                    output.writeNow(t);
                    readedData++;
                    System.out.println("Send number"+readedData+" size:"+t.limit());
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
