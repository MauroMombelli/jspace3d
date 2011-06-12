/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Shared;

import Shared.payload.Payload;
import Shared.payload.StringPayload;
import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 *
 * @author mauro
 */
public class PayloadContainer {
    DatagramHeader myHeader;
    LinkedList<Payload> data = new LinkedList<Payload>();
    byte number=0;

    public int write(ByteBuffer output, byte turn){
        int maxSize = output.remaining();
        
        maxSize-=DatagramHeader.getSizeInByte();
        if (maxSize<0)
            return 0;
        (new DatagramHeader(number, turn)).write(output);
        

        int dataPutted =0;
        Payload actual = data.poll();
        while(actual != null){
            maxSize-=actual.getSizeInByte()+1;
            if (maxSize<0)
                break;
            output.put( actual.getID() );
            actual.write(output);
            dataPutted++;
            actual = data.poll();
        }
        if (actual!=null){
            data.addFirst(actual);
        }
        return dataPutted;
    }

    public void read(ByteBuffer input){
        myHeader.read(input);

        while (input.remaining()>0){
            int actionType = input.get();//read payload type
            switch(actionType){
                case 0:
                    StringPayload p = new StringPayload("");
                    p.read(input);
                    data.add(p);
                    break;
                default:
                    System.out.println("No action with this ID!: "+actionType);
            }
        }
    }

}
