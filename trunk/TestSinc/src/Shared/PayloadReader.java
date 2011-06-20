/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Shared;

import Shared.DatagramHeader;
import Shared.payload.Payload;
import Shared.payload.StringPayload;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 *
 * @author mauro
 */
public class PayloadReader {

    byte numberInput = 0;
    int missingDatagramNumber = 0;

    long missingDatagram[] = new long[Byte.MAX_VALUE - Byte.MIN_VALUE];
    final static int OK = 0;

    final TreeMap<Integer, LinkedList<Payload>> dataIn = new TreeMap<Integer, LinkedList<Payload>>();

    public PayloadReader(){
        for (int i = 0; i < Byte.MAX_VALUE - Byte.MIN_VALUE; i++) {
            missingDatagram[i] = OK;
        }
    }

    public void read(ByteBuffer input) {
        DatagramHeader myHeader = DatagramHeader.read(input);

        for (byte missing = numberInput; missing < myHeader.getNumber(); missing++) {//find all missing packet if there is no overflow
            missingDatagram[missing - Byte.MIN_VALUE] = System.currentTimeMillis();
            missingDatagramNumber++;
        }



        System.out.println("DatagramReceived: " + myHeader.getNumber() + " turn: " + myHeader.getTurn() + " lost: " + missingDatagramNumber);

        synchronized (dataIn) {
            while (input.remaining() > 0) {
                int actionType = input.get();//read payload type
                switch (actionType) {
                    case StringPayload.dataID:
                        StringPayload p = new StringPayload("");
                        p.read(input);
                        LinkedList<Payload> data = dataIn.get(0);
                        if (data == null) {
                            data = new LinkedList<Payload>();
                            dataIn.put(0, data);
                        }
                        data.add(p);
                        break;
                    default:
                        System.out.println("No payload with this ID!: " + actionType);
                }
            }
        }
        numberInput++;
    }

    public Payload getPayload(int type) {
        synchronized (dataIn) {
            LinkedList<Payload> arr = dataIn.get(type);
            if (arr==null)
                return null;
            return arr.poll();
        }
    }
}
