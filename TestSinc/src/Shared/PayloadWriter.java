/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Shared;

import ServerUDP.client.StreamWriter;
import Shared.payload.Payload;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class PayloadWriter {

    StreamWriter outputStream;
    byte datagramNumber = 0;
    final TreeMap<Byte, LinkedList<Payload>> dataOut = new TreeMap<Byte, LinkedList<Payload>>();
/*
    PayloadWriter(SocketAddress address, int port) {
        outputStream = new StreamWriter(address, port);
    }
*/
    PayloadWriter(SocketAddress address, int port, Selector serverSelector) {
        outputStream = new StreamWriter(address, port, serverSelector);
    }

    public void add(Payload payload, byte turn) {
        synchronized (dataOut) {
            LinkedList<Payload> t = dataOut.get(turn);
            if (t == null) {
                t = new LinkedList<Payload>();
                dataOut.put(turn, t);
            }
            t.add(payload);
        }
    }

    public boolean hasToWrite() {
        synchronized (dataOut) {
            return dataOut.size() > 0 ? true : false;
        }
    }

    public int write() {

        int dataPutted = 0;

        Entry<Byte, LinkedList<Payload>> firstEntry;
        synchronized (dataOut) {
            firstEntry = dataOut.pollFirstEntry();
        }
        if (firstEntry != null) {

            ByteBuffer output = outputStream.getBuffer();

            int maxSize = output.remaining();

            maxSize -= DatagramHeader.getSizeInByte();
            if (maxSize < 0) {
                return 0;
            }

            DatagramHeader header = new DatagramHeader(datagramNumber, firstEntry.getKey());
            header.write(output);

            LinkedList<Payload> actualList = firstEntry.getValue();
            LinkedList<Payload> bufferList = firstEntry.getValue();
            Payload actual;
            while ((actual = actualList.poll()) != null) {
                bufferList.add(actual);
                
                maxSize -= actual.getSizeInByte() + 1;
                if (maxSize < 0) {
                    break;
                }
                output.put(actual.getID());
                System.out.println("Writing " + (actual.getSizeInByte() + 1) + " byte, free:" + maxSize+" "+output.position());
                actual.write(output);
                dataPutted++;
            }
            if (actual != null) {
                actualList.addFirst(actual);
                synchronized (dataOut) {
                    dataOut.put(firstEntry.getKey(), actualList);
                }
            }
            datagramNumber++;
            try {
                output.flip();
                outputStream.writeNow(output);
            } catch (IOException ex) {
                synchronized (dataOut) {
                    dataOut.get(firstEntry.getKey() ).addAll(0, bufferList); //put data back to list, becausa it hasn't been written
                }
                Logger.getLogger(PayloadWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dataPutted;
    }

    boolean flush() throws IOException {
        return outputStream.flush();
    }
}
