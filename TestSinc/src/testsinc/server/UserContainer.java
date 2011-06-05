/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsinc.server;

import java.util.HashMap;
import testsinc.net.server.ConnectionInfo;
import testsinc.net.shared.autentication.Login;

/**
 *
 * @author mauro
 */
public class UserContainer {
    HashMap<Login, ConnectionInfo> users = new HashMap<Login, ConnectionInfo>();

    
}
