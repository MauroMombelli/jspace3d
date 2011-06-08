/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.wetorrent.upnp.GatewayDevice;
import org.wetorrent.upnp.GatewayDiscover;
import org.wetorrent.upnp.PortMappingEntry;
import org.xml.sax.SAXException;



/**
 *
 * @author mauro
 */
public class UPNPHelper {

    public static void go(int SAMPLE_PORT) {

        GatewayDiscover discover = new GatewayDiscover();
        try {
            discover.discover();
        } catch (SocketException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        GatewayDevice d = discover.getValidGateway();

        if (null != d) {
            System.out.println("Gateway device found:)"+d.getModelName()+" "+ d.getModelDescription());
        } else {
            System.out.println("No valid gateway device found.");
            return;
        }

        InetAddress localAddress = d.getLocalAddress();
        System.out.println("Using local address: "+ localAddress.getHostAddress());
        String externalIPAddress;
        try {
            externalIPAddress = d.getExternalIPAddress();
            System.out.println("External address: "+ externalIPAddress);
        } catch (IOException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        PortMappingEntry portMapping = new PortMappingEntry();
        try {
            if (!d.getSpecificPortMappingEntry(SAMPLE_PORT, "UDP", portMapping)) {
                if (d.addPortMapping(SAMPLE_PORT, SAMPLE_PORT, localAddress.getHostAddress(), "UDP", "MAUROTEST")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   // d.deletePortMapping(SAMPLE_PORT, "UDP");
                    System.out.println("Port mapping added");
                    System.out.println("Test SUCCESSFUL");
                } else {
                    System.out.println("Port mapping removal failed");
                    System.out.println("Test FAILED");
                }
            } else {
                d.deletePortMapping(SAMPLE_PORT, "UDP");
                System.out.println("Port was already mapped. Deleted mapping.");
            }
        } catch (IOException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
