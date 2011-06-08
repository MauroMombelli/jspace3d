/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsinc.net;

import java.nio.ByteBuffer;
import testsinc.net.utils.ByteStream;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class SyncObjectStream {

    final int OBJECT_DIMENSION_BYTE = 4;
    private final SelectionKey key;
    final ArrayList<byte[]> dataToWrite = new ArrayList<byte[]>();
    final ArrayList<Object> receivedObject = new ArrayList<Object>();
    AtomicBoolean closed = new AtomicBoolean(false);
    private PartialObjectBuffer realPartialObj = new PartialObjectBuffer(OBJECT_DIMENSION_BYTE);//because 4 is the dimension of int. see below
    private AtomicInteger maxObjectSize = new AtomicInteger(0);//zero if we don't want limit the size
    private Object waitingForInput;
    private long timeSincelastRead = Calendar.getInstance().getTimeInMillis();

    /*
     * How we read object: first of all we need the dimension of the object, in int (4 byte = OBJECT_DIMENSION_BYTE), then we can read many byte as the dimension
     */
    public SyncObjectStream(SelectionKey k) {
        this.key = k;
    }

    public SyncObjectStream(SelectionKey k, int maxObjSize) {
        this(k);
        maxObjectSize.set(maxObjSize);
    }
    /*
     *
     * METHOD FOR SERVER ONLY!!!
     *
     */

    public void addInput(ByteBuffer array) {
        timeSincelastRead = Calendar.getInstance().getTimeInMillis();
        try {

            array = realPartialObj.addToBuffer(array);

            if (realPartialObj.objReady) {
                if (realPartialObj.readinAnObject) {
                    //convert the stream to object
                    byte[] objByte = realPartialObj.getBuffer();
                    Object risObj = ByteStream.toObject(objByte);
                    synchronized (receivedObject) {
                        receivedObject.add(risObj);
                    }
                    try {
                        System.out.println("I've readed an obj: " + (String) risObj);
                    } catch (java.lang.ClassCastException e) {
                        System.out.println("I've readed an uncastable to string object: " + risObj.toString());
                    }
                    //readed an the object. Now wait for dimension of the next object
                    try {
                        realPartialObj.setMissingByte(OBJECT_DIMENSION_BYTE);
                    } catch (OutOfMemoryError e) {
                        close();
                        return;
                    }
                    realPartialObj.readinAnObject = false;
                    System.out.println("I'm waiting for a dimension of dimension: " + realPartialObj.missingByte);
                    //now wake up the waiting thread, if any

                    if (waitingForInput != null) {
                        synchronized (waitingForInput) {
                            if (waitingForInput != null) {
                                System.out.println("Wake up!");
                                waitingForInput.notify();
                                waitingForInput = null;
                            }
                        }
                    }
                } else {
                    //readed the dimension of the object.
                    int expectedObjDimension = ByteStream.byteArrayToInt(realPartialObj.getBuffer());
                    System.out.println("I'm waiting for an object of dimension: " + expectedObjDimension + " max is:" + maxObjectSize.get());
                    if (expectedObjDimension > 0) {
                        if (maxObjectSize.get() == 0 || (maxObjectSize.get() != 0 && expectedObjDimension <= maxObjectSize.get())) {
                            realPartialObj.setMissingByte(expectedObjDimension);
                        } else {
                            System.out.println("Error, requested obj size is too big, disconnecting!");
                            close();
                            return;
                        }
                    } else {
                        System.out.println("Error, requested obj size is void, disconnecting!");
                        close();
                        return;
                    }

                    realPartialObj.readinAnObject = true;
                }
            }

            //if we still have some data, recursively call this funtion
            if (array != null) {
                addInput(array);
            }
        } catch (Exception e) {
            System.out.println("Eccezzione in lettura: " + e);
            close();
            return;
        }
    }

    public byte[] getWriteData() {
        System.out.println("Ready to write datda");
        try {
            if (closed.get()) {
                return null;
            }

            byte[] out;
            synchronized (dataToWrite) {
                System.out.println("Size of datawrite:" + dataToWrite.size());
                if (dataToWrite.isEmpty()) {
                    return null;
                }

                out = dataToWrite.get(0);
                dataToWrite.remove(0);
                if (dataToWrite.isEmpty()) {
                    System.out.println("Datawrite is now empty");
                }
            }
            System.out.println("out: " + new String(out));
            return out;
        } catch (Exception e) {
            System.out.println("Eccezzione in scrittura: " + e);
            close();
            return null;
        }
    }

    public void addUnwrittenData(byte[] data) {
        try {
            if (closed.get()) {
                return;
            }

            synchronized (dataToWrite) {
                dataToWrite.add(0, data);
                System.out.println("Added unwritten data:" + dataToWrite.size());
            }
            synchronized (key) {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                key.selector().wakeup();
            }
        } catch (Exception e) {
            System.out.println("Eccezzione in rescrittura: " + e);
            close();
            return;
        }
    }

    /*
     *
     * METHOD FOR THE REST OF THE WORLD
     *
     */
    public void close() {
        closed.set(true);
        System.out.println("Closing stream");

        if (waitingForInput != null) {
            synchronized (waitingForInput) {
                if (waitingForInput != null) {
                    System.out.println("Wake up!");
                    waitingForInput.notify();
                    waitingForInput = null;
                }
            }
        }
    }

    public boolean write(Object data) {
        if (closed.get()) {
            return false;
        }
        if (data == null) {
            return false;
        }

        byte out[] = ByteStream.toBytes(data);

        if (out.length <= 0) {
            return false;
        }

        byte ris[] = new byte[OBJECT_DIMENSION_BYTE + out.length];

        System.out.println("Data to write size: " + out.length + " ris len:" + ris.length);

        ByteStream.intToByteArray(out.length, ris);

        System.arraycopy(out, 0, ris, OBJECT_DIMENSION_BYTE, out.length);
        return writeByte(ris);
    }

    private boolean writeByte(byte[] data) {
        if (closed.get()) {
            return false;
        }
        boolean ok = false;
        synchronized (dataToWrite) {
            ok = dataToWrite.add(data);
            System.out.println("Added to datawrite:" + ok + " " + dataToWrite.size() + " " + data);
        }
        synchronized (key) {
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            key.selector().wakeup();
        }
        return ok;
    }

    public int availableInput() {
        synchronized (receivedObject) {
            return receivedObject.size();
        }
    }

    public Object readAndClear() {
        synchronized (receivedObject) {
            if (receivedObject.size() > 0) {
                Object ris = receivedObject.get(0);
                receivedObject.remove(0);
                return ris;
            }
        }
        return false;
    }

    public ArrayList<Object> readAndClearAll() {
        ArrayList<Object> ris = new ArrayList<Object>();
        synchronized (receivedObject) {
            ris.addAll(receivedObject);
            receivedObject.clear();
        }
        return ris;
    }

    public Object readAndClearBlocking(Object obj) {
        waitingForInput = obj;
        try {
            System.out.println("Going to wait");
            synchronized (waitingForInput) {
                while (availableInput() == 0 && !isClosed()) {
                    waitingForInput.wait();
                }
                waitingForInput = null;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SyncObjectStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return readAndClear();
    }

    public boolean isClosed() {
        return closed.get();
    }

    public Object isValidKey() {
        synchronized (key) {
            return key.isValid();
        }
    }

    public long getTimeSinceLastRead() {
        return timeSincelastRead;
    }
    /*
    public SelectionKey getKey() {
    return key;
    }
     */

    private class PartialObjectBuffer {

        int missingByte;
        byte actualBuffer[];
        public boolean objReady = false;
        public boolean readinAnObject = false;//true if we are reading an objcet, false if we are still reading the dimension

        public PartialObjectBuffer(int missingByte) {
            setMissingByte(missingByte);
        }

        public final void setMissingByte(int missingByte) {
            this.missingByte = missingByte;
            try {
                actualBuffer = new byte[missingByte];
            } catch (OutOfMemoryError e) {
                System.out.println("Errore creazione array");
                close();
            }
            objReady = false;
        }

        public byte[] addToBuffer(byte[] b) {
            int howManyByteCopy = 0;
            if (b.length <= missingByte) {
                howManyByteCopy = b.length;
            } else {
                howManyByteCopy = missingByte;
            }
            //actualBuffer = Arrays.copyOf(actualBuffer, actualBuffer.length + howManyByteCopy);
            System.arraycopy(b, 0, actualBuffer, actualBuffer.length - missingByte, howManyByteCopy);

            missingByte -= howManyByteCopy;

            if (b.length >= missingByte) {
                objReady = true;
            }

            if (b.length <= missingByte) {
                //the objcet is NOT ready, return null
                return null;
            } else {
                return Arrays.copyOfRange(b, howManyByteCopy, b.length);
            }
        }

        private byte[] getBuffer() {
            return actualBuffer;
        }
    }
}
