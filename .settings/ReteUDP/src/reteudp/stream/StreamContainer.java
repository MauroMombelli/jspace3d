/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reteudp.stream;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author mauro
 */
public class StreamContainer {

    AtomicBoolean isValid = new AtomicBoolean(true);
    StreamReader reader;
    StreamWriter writer;

    public StreamContainer(StreamWriter w, StreamReader r) {
        reader = r;
        writer = w;
    }

    public void addBuffer(ByteBuffer data) {
        reader.addBuffer(data);
        //System.out.println( "Setting data to write" );
        //writer.write(data);
    }

    public void close() {
        isValid.set(false);
        reader.close();
        writer.close();
    }

}
