/**
 * Object to store the services to server mapping.
 */
package services;
/*
 *  
 * author Shruti Gandhi
 */
import java.util.Hashtable;

public class ServerObject {
	Hashtable<String, String> servers_by_servicename = new Hashtable<String, String>();

	/**
	 * @return the servers_by_servicename
	 */
	public Hashtable<String, String> getServers_by_servicename() {
		return servers_by_servicename;
	}

}
