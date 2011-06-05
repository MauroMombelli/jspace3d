/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.server;

import testsinc.net.shared.autentication.Login;

/**
 *
 * @author mauro
 */
class Database {

    static boolean checkLogin(Login login) {
        if (login!=null)
            if (login.isValid())
                return true;
        
        return false;
    }

}
