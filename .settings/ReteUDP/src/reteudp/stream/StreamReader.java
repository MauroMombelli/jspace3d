/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reteudp.stream;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import payload.AbstractPayload;
import payload.PayloadString;

/**
 *
 * @author mauro
 */
public class StreamReader {

    AtomicBoolean isValid = new AtomicBoolean(true);
    TreeMap<Byte, TreeMap<Byte, LinkedList<AbstractPayload>>> dataBuffer = new TreeMap<Byte, TreeMap<Byte, LinkedList<AbstractPayload>>>();
    final long missingDatagram[] = new long[Byte.MAX_VALUE - Byte.MIN_VALUE];
    private final long OK = 0;

    public StreamReader() {
        for (int i = 0; i < Byte.MAX_VALUE - Byte.MIN_VALUE; i++) {
            missingDatagram[i] = OK;
        }
    }

    public void addBuffer(ByteBuffer data) {
        TreeMap<Byte, LinkedList<AbstractPayload>> thisTurnAction = readHeader(data);

        while (data.remaining() >= 0) {
            byte id = data.get();

            LinkedList<AbstractPayload> toAdd = thisTurnAction.get(id);
            if (toAdd == null) {
                toAdd = new LinkedList<AbstractPayload>();
                thisTurnAction.put(id, toAdd);
            }
            switch (id) {
                case PayloadString.ID:
                    toAdd.add( new PayloadString(data) );
                default:
                    System.out.println("action unrecognized");

            }
        }
    }

    void close() {
        isValid.set(false);
    }

    private TreeMap<Byte, LinkedList<AbstractPayload>> readHeader(ByteBuffer data) {
        byte datagramNumber = data.get();

        updateMissingDatagram(datagramNumber);

        byte turn = data.get();

        TreeMap<Byte, LinkedList<AbstractPayload>> thisTurnAction = dataBuffer.get(turn);
        if (thisTurnAction == null) {
            thisTurnAction = new TreeMap<Byte, LinkedList<AbstractPayload>>();
            dataBuffer.put(turn, thisTurnAction);
        }
        return thisTurnAction;
    }

    private void updateMissingDatagram(byte datagramNumber) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
