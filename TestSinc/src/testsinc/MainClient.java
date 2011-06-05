/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import testsinc.net.SyncObjectStream;
import testsinc.net.client.ClientSelector;
import testsinc.net.shared.autentication.Login;
import testsinc.net.shared.autentication.OkAuth;
import testsinc.net.shared.autentication.Seed;
import testsinc.utils.SHA1Calculator;

/**
 *
 * @author mauro
 */
public class MainClient {

    SyncObjectStream serverStream;

    public static void main(String args[]){
        MainClient client = new MainClient();

        System.out.println("Starting connection");
        client.connect("127.0.0.1", 5000);

        System.out.println("Login");
        if ( !client.login() )
            return;

        System.out.println("Login ok");
    }

    private void connect(String ip, int port) {
        ClientSelector connection = new ClientSelector();
        serverStream = connection.connect(ip, port, true);
        new Thread(connection).start();
    }

    private boolean login() {
        try {
            Seed seme = (Seed)serverStream.readAndClearBlocking(this);
        }catch(java.lang.ClassCastException e){
            System.out.println("Seed non riconosciuto: "+e);
            return false;
        }
        Login mioLogin = new Login();
        try {
            mioLogin.setPass(SHA1Calculator.SHA1(mioLogin.getPass()));
            System.out.println("SHA1 size:"+SHA1Calculator.SHA1(mioLogin.getPass()).length());
            serverStream.write(mioLogin);

            //wait for ok response
            OkAuth t=(OkAuth)serverStream.readAndClearBlocking(this);
            return true;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("No SHA1 present: "+ex);
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error encoding SHA1: "+ex);
            Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
        }catch(java.lang.ClassCastException ex){
            System.out.println("Login non riconosciuto: "+ex);
            return false;
        }
        return false;
    }
}
