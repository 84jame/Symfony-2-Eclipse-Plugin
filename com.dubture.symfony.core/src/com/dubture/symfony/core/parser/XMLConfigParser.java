/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.core.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import com.dubture.symfony.core.log.Logger;
import com.dubture.symfony.index.dao.Route;
import com.dubture.symfony.index.dao.Service;

/**
 * 
 * {@link XMLConfigParser} retrieves project configuration
 * from xml files and contributes it to the Symfony2 model.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class XMLConfigParser implements IConfigParser {

	private XPath xPath;
	private Document doc;

	private HashMap<String, String> parameters;
	private HashMap<String, Service> services;
	private Stack<Route> routes = new Stack<Route>();

	public XMLConfigParser(File file) throws Exception {

		try {
//		    SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
		    
		    FileInputStream is = new FileInputStream(file);
		    doc = PositionalXMLReader.readXML(is);
//		    doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
//		    sp.parse(file, new LocationRecordingHandler(doc));
		    
		    xPath = XPathFactory.newInstance().newXPath();
		    
        } catch (SAXParseException e) {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("<dummy></dummy>");
            Logger.log(Logger.WARNING, "Error parsing xml ");
        }
		parameters = new HashMap<String, String>();
		services = new HashMap<String, Service>();

	}


	@Override
	public void parse() throws Exception {

		// get parameters
		parseParameters();

		// get services
		parseServices();

		// get aliased services
		parseAliases();
		
		// parse synthetic services
		parseSynthetic();

		// parse routes
		parseRoutes();

	}

	private void parseRoutes() {

		try {

			String servicePath = "/routes/route";
			NodeList routeNodes = getNodes(servicePath);

			for (int i = 0; i < routeNodes.getLength(); i++) {

				Node node = routeNodes.item(i);				
				NamedNodeMap atts = node.getAttributes();

				String name = null;
				String pattern = null;

				for (int j = 0; j < atts.getLength(); j++) {

					Attr attr = (Attr) atts.item(j);

					String key = attr.getName();
					if (key.equals("id"))
						name = attr.getValue();
					else if (key.equals("pattern"))
						pattern = attr.getValue();

				}

				XPathExpression expr = xPath.compile("default[@key='_controller']");
				Object _default = expr.evaluate(node, XPathConstants.NODESET);
				NodeList defaults = (NodeList) _default;

				if (defaults.getLength() == 1) {
					Node controllerNode = defaults.item(0);			
					routes.push(new Route(name, pattern, controllerNode.getTextContent()));					
				}

			}

		} catch (Exception e) {
			Logger.logException(e);
		}
	}


	/**
	 * Parse the service parameters from the XML file.
	 * 
	 * 
	 * @throws Exception
	 */
	private void parseParameters() throws Exception {

		String expr = "/container/parameters/parameter[@key]";
		XPathExpression xpathExpr = xPath.compile(expr);			

		Object result = xpathExpr.evaluate(doc,XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		for (int i = 0; i < nodes.getLength(); i++) {			
			Node node = nodes.item(i);
			NamedNodeMap atts = node.getAttributes();			
			for (int j = 0; j < atts.getLength(); j++) {

				Attr attr = (Attr) atts.item(j);	

				if (attr != null && attr.getName() != null && attr.getName().equals("key")) {
					parameters.put(attr.getValue(), node.getTextContent());
					break;					
				}
			}
		}
	}

	/**
	 * Parse services if available from the xml file.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void parseServices() throws Exception {

	    NodeList nodeList = doc.getElementsByTagName("service");
	    
	    for (int i=0; i < nodeList.getLength(); i++) {
	        
	        Node childNode = nodeList.item(i);
	        System.err.println(childNode.getUserData("lineNumber"));
	        
	        NamedNodeMap attributes = childNode.getAttributes();
	        
            String id = attributes.getNamedItem("id").getNodeValue();
            String phpClass = attributes.getNamedItem("class").getNodeValue();
            
            Node publicItem = attributes.getNamedItem("public");
            String _public = null;
            
            if (publicItem != null) {
                _public = attributes.getNamedItem("public").getNodeValue();    
            }
            
            if (phpClass != null && id != null) {

                Service _service = null;
                if (phpClass.startsWith("%") && phpClass.endsWith("%")) {

                    String placeHolder = phpClass.replace("%", "");
                    Iterator it = getParameters().keySet().iterator();

                    while (it.hasNext()) {
                        String key = (String) it.next();                        
                        String val = (String) getParameters().get(key);

                        if (placeHolder.equals(key)) {                  
                            _service = new Service(id, val, null);                              
                        }
                    }                       
                } else {
                    _service = new Service(id, phpClass, null);                 
                }
                
                /*
                for(int k=0; k < tags.getLength(); k++) {
                    
                    Node tag = tags.item(k);
                    NamedNodeMap map = tag.getAttributes();                                 
                    Node tagName = map.getNamedItem("name");

                    if (tagName != null && tagName.getNodeValue() != null)
                        _service.addTag(tagName.getNodeValue());
                    
                }
                */
                
                if (_service != null) {
                    _service.setPublic(_public);
                    _service.setLine(Integer.parseInt((String) childNode.getUserData("lineNumber")));
                }
                
                synchronized (services) {
                    services.put(id, _service); 
                }
            }	        

	    }
	    
/*	    
		String servicePath = "/container/services/service[@class]";
		NodeList serviceNodes = getNodes(servicePath);
		
		for (int i = 0; i < serviceNodes.getLength(); i++) {

			Element service = (Element) serviceNodes.item(i);
			
			//TODO: Check the services visibility and if it's abstract
			
			Object userData = service.getUserData(LocationRecordingHandler.KEY_LINE_NO);
			
			System.err.println(userData);
			
			String id = service.getAttribute("id");
			String phpClass = service.getAttribute("class");			
			String _public = service.getAttribute("public");
			
			NodeList tags = service.getElementsByTagName("tag");			
			
			if (_public == null || _public.equals(""))
				_public = "true";			
			
			if (phpClass != null && id != null) {

				Service _service = null;
				if (phpClass.startsWith("%") && phpClass.endsWith("%")) {

					String placeHolder = phpClass.replace("%", "");
					Iterator it = getParameters().keySet().iterator();

					while (it.hasNext()) {
						String key = (String) it.next();						
						String val = (String) getParameters().get(key);

						if (placeHolder.equals(key)) {					
							_service = new Service(id, val, null);								
						}
					}						
				} else {
					_service = new Service(id, phpClass, null);					
				}
				
				for(int k=0; k < tags.getLength(); k++) {
					
					Node tag = tags.item(k);
					NamedNodeMap map = tag.getAttributes();									
					Node tagName = map.getNamedItem("name");

					if (tagName != null && tagName.getNodeValue() != null)
						_service.addTag(tagName.getNodeValue());
					
				}
				
				if (_service != null)
					_service.setPublic(_public);
				
				synchronized (services) {
					services.put(id, _service);	
				}
			}
		}
		*/
	}

	@SuppressWarnings("rawtypes")
	private void parseAliases() throws Exception {

		String servicePath = "/container/services/service[@alias]";
		NodeList serviceNodes = getNodes(servicePath);

		for (int i = 0; i < serviceNodes.getLength(); i++) {

			Element service = (Element) serviceNodes.item(i);
			String id = service.getAttribute("id");
			String alias = service.getAttribute("alias");

			if (alias != null && id != null) {

				synchronized (services) {
					Iterator it = services.keySet().iterator();
					HashMap<String, Service> newServices = new HashMap<String, Service>();
					while (it.hasNext()) {
						String aliasID = (String) it.next();						
						Service _s=  services.get(aliasID);
						if (_s != null) {
						    _s.addAlias(aliasID);
						    if (alias.equals(aliasID)) {
						        newServices.put(id, _s);
						    }
						}
					}
					services.putAll(newServices);
				}
			}
		}
	}

	private void parseSynthetic() throws Exception {

		String servicePath = "/container/services/service[@synthetic]";
		NodeList serviceNodes = getNodes(servicePath);

		for (int i = 0; i < serviceNodes.getLength(); i++) {

			Element service = (Element) serviceNodes.item(i);

			//TODO: Check the services visibility and if it's abstract
			String id = service.getAttribute("id");
			String isSynthetic = service.getAttribute("synthetic");

			if (isSynthetic != null && id != null) {
				
				services.put(id, new Service(id, "synthetic", null));
			} 		
		}		
	}

	/**
	 * Retrieve a list of nodes for a given xpath expression
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private NodeList getNodes(String path) throws Exception {

		XPathExpression xpathExpr = xPath.compile(path);
		Object result = xpathExpr.evaluate(doc,XPathConstants.NODESET);
		return (NodeList) result;

	}

	/**
	 * Get all loades services.
	 * 
	 * 
	 * @return
	 */
	public HashMap<String, Service> getServices() {
		return services;
	}


	/**
	 * Did the parser find any services definitions?
	 * 
	 * @return
	 */
	public boolean hasServices() {

		return services.size() > 0;
	}


	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public boolean hasRoutes() {

		return routes.size() > 0;
	}

	public Stack<Route> getRoutes() {

		return routes;
	}

}
