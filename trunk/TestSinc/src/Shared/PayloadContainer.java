/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Shared;

import Shared.payload.Payload;
import Shared.payload.StringPayload;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

/**
 *
 * @author mauro
 */
public class PayloadContainer {

    PayloadWriter writer;
    PayloadReader reader = new PayloadReader();
    
    //DatagramHeader myHeader = new DatagramHeader(numberOutput, (byte) 0);
    
    //TreeSet<Byte> missingDatagram = new TreeSet<Byte>();
    
/*
    public PayloadContainer(StreamWriter outputStream) {
        writer  =new PayloadWriter(outputStream);
    }
*/
    public PayloadContainer(SocketAddress address, int port, Selector serverSelector) {
        writer = new PayloadWriter(address, port, serverSelector);
        
    }

    public StringPayload getString() {
        Payload ris = reader.getPayload( StringPayload.dataID );
        if ( ris != null ){
            return (StringPayload)ris;
        }
        return null;
    }

    public void read(ByteBuffer input) {
        reader.read(input);
    }

    public void flush(){
        writer.write();
    }

    public void addPayload(StringPayload echo, byte turn) {
        writer.add(echo, turn);
    }

}
