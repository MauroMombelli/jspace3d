/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Shared;

import Shared.payload.Payload;
import Shared.payload.StringPayload;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 *
 * @author mauro
 */
public class PayloadContainer {
    byte numberOutput=0;
    DatagramHeader myHeader = new DatagramHeader(numberOutput, (byte)0);
    LinkedList<Payload> dataOut = new LinkedList<Payload>();

    TreeMap<Integer, LinkedList<Payload> > dataIn = new TreeMap<Integer, LinkedList<Payload> >();
    //TreeSet<Byte> missingDatagram = new TreeSet<Byte>();
    long missingDatagram[] = new long[Byte.MAX_VALUE-Byte.MIN_VALUE];
    final static int OK = 0;

    byte numberInput=0;

    public PayloadContainer(){
        for (int i =0; i < Byte.MAX_VALUE-Byte.MIN_VALUE; i++){
            missingDatagram[i]=OK;
        }
    }
    
    public int write(ByteBuffer output, byte turn){
        int maxSize = output.remaining();
        
        maxSize-=DatagramHeader.getSizeInByte();
        if (maxSize<0)
            return 0;
        (new DatagramHeader(numberOutput, turn)).write(output);
        

        int dataPutted =0;
        Payload actual = dataOut.poll();
        while(actual != null){
            maxSize-=actual.getSizeInByte()+1;
            if (maxSize<0)
                break;
            System.out.println("Writing "+(actual.getSizeInByte()+1)+" byte, free:"+maxSize);
            output.put( actual.getID() );
            actual.write(output);
            dataPutted++;
            actual = dataOut.poll();
        }
        if (actual!=null){
            dataOut.addFirst(actual);
        }
        numberOutput++;
        return dataPutted;
    }

    int missingDatagramNumber=0;
    public void read(ByteBuffer input){
        myHeader.read(input);

        for (byte missing=numberInput; missing < myHeader.getNumber();missing++){//find all missing packet if there is no overflow
            missingDatagram[missing-Byte.MIN_VALUE]=System.currentTimeMillis();
            missingDatagramNumber++;
        }


        System.out.println( "DatagramReceived: "+myHeader.getNumber()+" turn: "+myHeader.getTurn()+" lost: "+missingDatagramNumber );

        while (input.remaining()>0){
            int actionType = input.get();//read payload type
            switch(actionType){
                case 0:
                    StringPayload p = new StringPayload("");
                    p.read(input);
                    LinkedList<Payload> data = dataIn.get(0);
                    if (data==null){
                        data = new LinkedList<Payload>();
                        dataIn.put(0, data);
                    }
                    data.add(p);
                    break;
                default:
                    System.out.println("No action with this ID!: "+actionType);
            }
        }
    }

    public void add(Payload payload) {
        dataOut.add(payload);
    }

    public StringPayload getString(){
        return (StringPayload)dataIn.get(0).poll();
    }

    public boolean hasToWrite(){
        return dataOut.size()>0?true:false;
    }

}
