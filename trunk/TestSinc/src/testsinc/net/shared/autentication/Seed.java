/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.net.shared.autentication;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author mauro
 */
public class Seed implements Serializable{

    transient static final Random randomSeed = new Random( Calendar.getInstance().getTimeInMillis() );
    private long seed;

    public Seed(){
        seed = randomSeed.nextLong();
    }

    public long getSeed(){
        return seed;
    }
}
