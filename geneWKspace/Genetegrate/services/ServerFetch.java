/*
 * Object to fetch the servers serving a given service.
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


public class ServerFetch {
	static String fileName_by_Server = "services/Services.xml";			// This file contains the xml database of the various servers offering different services.
														// Services.xml
	public static void main(String[] args) throws Exception {
		try {
			fileName_by_Server = args[0];

		} catch (Exception ex) {

		}


		server_lookup("xyz");

	}

	/**
	 * Looks up a particular service, giving the servers that offer it
	 * @param serviceName the name of the service
	 * @return A collection of servers that offer the given service
	 */
	public static ServerObject server_lookup(String serviceName) {
		ServerObject po = new ServerObject();
		try {
			// XML setup.
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			.newInstance();

			DocumentBuilder docBuilder;

			docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new FileInputStream(fileName_by_Server));
			Element documentRoot = doc.getDocumentElement();
			NodeList matchedNodes = null;
			String query = "";
			try {
				query = "//*/service";

				// Looking for the "service" tags.

				matchedNodes = XPathAPI.selectNodeList(documentRoot, query);
				if (matchedNodes != null) {

					for (int j = 0; j < matchedNodes.getLength(); j++) {

						if (matchedNodes.item(j) != null){

							if(matchedNodes.item(j).getAttributes() != null){

								// Matching the service name with the input service name
								if(matchedNodes.item(j).getAttributes().item(0).getNodeValue() != null && matchedNodes.item(j).getAttributes().item(0).getNodeValue().equals(serviceName)){
									// Found a match - Getting necessary data
									NodeList nl = matchedNodes.item(j).getChildNodes();

									for (int t = 0; t < nl.getLength(); t++) {
										//	System.out.println("id: " + nl.item(t).getFirstChild().getFirstChild().getNodeValue() + " "+nl.item(t).getFirstChild().getNextSibling().getFirstChild().getNodeValue());									


										po.servers_by_servicename.put(nl.item(t).getFirstChild().getFirstChild().getNodeValue(), nl.item(t).getFirstChild().getNextSibling().getFirstChild().getNodeValue());


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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return po;
	}



	/*private static String xmlToString(Node node) {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}*/
}
