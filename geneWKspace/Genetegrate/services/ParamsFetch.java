/**
 * Fetches the necessary parameters from Servers.xml file.
 */
package services;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;


public class ParamsFetch {
	private static String fileName_by_Server = "services/Servers.xml";		// Contains the name of the xml database that specifies how THE service must be invoked on
													// a server.
	/**
	 * Looks up a server for the various service execution parameters.
	 * @param serverid the id of the service to be executed.
	 * @return The required and optional parameters.
	 */
	public static ParamsObject parms_lookup(String serverid) {
		ParamsObject po = new ParamsObject();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder docBuilder;

			docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new FileInputStream(fileName_by_Server));
			Element documentRoot = doc.getDocumentElement();
			NodeList matchedNodes = null;
			String query = "";
			try {
				query = "//*/server";
				matchedNodes = XPathAPI.selectNodeList(documentRoot, query);
				if (matchedNodes != null) {

					for (int j = 0; j < matchedNodes.getLength(); j++) {
						
						if (matchedNodes.item(j) != null){
							
							if(matchedNodes.item(j).getAttributes() != null){
								
								if(matchedNodes.item(j).getAttributes().item(0).getNodeValue() != null && matchedNodes.item(j).getAttributes().item(0).getNodeValue().equals(serverid)){
									// Found a match with the server id.
								NodeList nl = matchedNodes.item(j).getFirstChild().getChildNodes();
								
								for (int t = 0; t < nl.getLength(); t++) {
									//System.out.println("id: " + nl.item(t).getAttributes().item(0).getNodeValue() + " "+nl.item(t).getFirstChild().getNodeValue());									
										
									if(nl.item(t).getAttributes().item(1).getNodeValue().equalsIgnoreCase("true")){									
										po.allRequiredParms.put(nl.item(t).getAttributes().item(0).getNodeValue(), nl.item(t).getFirstChild().getNodeValue());
									}else{
										po.allOptionalParms.put(nl.item(t).getAttributes().item(0).getNodeValue(), nl.item(t).getFirstChild().getNodeValue());
									}
									
								}
							}
								
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("ERROR" + e.getMessage());
				e.printStackTrace();

			}
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return po;
	}
}
