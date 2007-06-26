/*
 * The object in which the required and optional parameters are stored for the services.
 */
package services;
/*
 *  
 * author Shruti Gandhi
 */
import java.util.Hashtable;

public class ParamsObject {
	Hashtable<String, String> allOptionalParms= new Hashtable<String, String>();
	Hashtable<String, String> allRequiredParms= new Hashtable<String, String>();
	/**
	 * @return the allOptionalParms
	 */
	public Hashtable<String, String> getAllOptionalParms() {
		return allOptionalParms;
	}
	/**
	 * @return the allRequiredParms
	 */
	public Hashtable<String, String> getAllRequiredParms() {
		return allRequiredParms;
	}	
}
