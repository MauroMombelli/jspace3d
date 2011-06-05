/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.net.shared.autentication;

import java.io.Serializable;

/**
 *
 * @author mauro
 */
public class Login implements Serializable{
    transient static final int userMinSize = 5;
    transient static final int userMaxSize = 20;
    
    transient static final int passFixedSize = 40;
    //that because the max size of user will be 20 character
    private String user = "12345678901234567890";
    //that because the max size of password will be 40 character, because sha1 is 40 character long
    private String password = "1234567890123456789012345678901234567890";

    public void setUser(String u){
        if (u!=null)
            if (u.length()>userMinSize && u.length()<userMaxSize)
                this.user=u;
    }

    public String getUser(){
        if (user!=null)
            if (user.length()>=userMinSize && user.length()<=userMaxSize)
                return user;
        return null;
    }

    public void setPass(String u){
        if (u!=null)
            if (u.length()==passFixedSize)
                this.password=u;
    }

    public String getPass(){
        if (password!=null)
            if (password.length()==passFixedSize)
                return password;
        return null;
    }

    public boolean isValid(){
        if (getUser()!=null && getPass()!=null)
            return true;
        return false;
    }
}
