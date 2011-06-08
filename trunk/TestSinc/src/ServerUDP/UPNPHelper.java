/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerUDP;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

/**
 * 
 * @author mauro
 */
public class UPNPHelper {

	public static void goCling(int port) {
		PortMapping desiredMapping = new PortMapping(port, "192.168.1.64",
				PortMapping.Protocol.UDP, "TESTMAURO");

                //System.err.println("Invio");
		UpnpService upnpService = new UpnpServiceImpl(new PortMappingListener(
				desiredMapping));
		//System.err.println("Chiudo");
		//upnpService.shutdown();
		//System.err.println("e apro");
		try {
			upnpService.getControlPoint().search();
		} catch (Exception e) {
			System.err.println(e);
		}
		System.err.println("Fine");
	}
	/*
	 * public static void go(int SAMPLE_PORT) {
	 * 
	 * GatewayDiscover discover = new GatewayDiscover(); try {
	 * discover.discover(); } catch (SocketException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (UnknownHostException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (IOException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (SAXException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (ParserConfigurationException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } GatewayDevice d = discover.getValidGateway();
	 * 
	 * if (null != d) {
	 * System.out.println("Gateway device found:)"+d.getModelName()+" "+
	 * d.getModelDescription()); } else {
	 * System.out.println("No valid gateway device found."); return; }
	 * 
	 * InetAddress localAddress = d.getLocalAddress();
	 * System.out.println("Using local address: "+
	 * localAddress.getHostAddress()); String externalIPAddress; try {
	 * externalIPAddress = d.getExternalIPAddress();
	 * System.out.println("External address: "+ externalIPAddress); } catch
	 * (IOException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (SAXException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } PortMappingEntry portMapping = new PortMappingEntry(); try { if
	 * (!d.getSpecificPortMappingEntry(SAMPLE_PORT, "UDP", portMapping)) { if
	 * (d.addPortMapping(SAMPLE_PORT, SAMPLE_PORT,
	 * localAddress.getHostAddress(), "UDP", "MAUROTEST")) { try {
	 * Thread.sleep(1000); } catch (InterruptedException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } // d.deletePortMapping(SAMPLE_PORT, "UDP");
	 * System.out.println("Port mapping added");
	 * System.out.println("Test SUCCESSFUL"); } else {
	 * System.out.println("Port mapping removal failed");
	 * System.out.println("Test FAILED"); } } else {
	 * d.deletePortMapping(SAMPLE_PORT, "UDP");
	 * System.out.println("Port was already mapped. Deleted mapping."); } }
	 * catch (IOException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } catch (SAXException ex) {
	 * Logger.getLogger(UPNPHelper.class.getName()).log(Level.SEVERE, null, ex);
	 * } }
	 */
}
