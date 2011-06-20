/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP.client;

import Shared.PayloadContainer;
import Shared.payload.StringPayload;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

/**
 *
 * @author mauro
 */
public class Client {
    
    private final PayloadContainer dataContainer;

    public Client(SocketAddress clientAddress, Selector serverSelector) {
        dataContainer = new PayloadContainer(clientAddress, 5001, serverSelector);
    }

    public void elaborateDatagram(ByteBuffer input) {
        dataContainer.read(input);
    }

    public void writeData(){
        dataContainer.flush();
    }

    public void echo() {
        StringPayload echo;
        while ( (echo=dataContainer.getString()) !=null){
            System.out.println(echo);
            dataContainer.addPayload(echo, Byte.MAX_VALUE);
        }
        
        System.out.println("Invio Datagramma");
        dataContainer.flush();
    }
/*
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
*/
}
