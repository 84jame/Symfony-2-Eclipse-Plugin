/**
 * 
 */
package org.eclipse.symfony.core.builder;

import org.eclipse.symfony.core.model.ModelManager;
import org.eclipse.symfony.core.parser.XMLConfigParser;
import org.eclipse.symfony.core.parser.YamlConfigParser;

/**
 * @author Robert Gruendler <robert@dubture.com>
 *
 */
public abstract class AbstractSymfonyVisitor {

	private XMLConfigParser xmlParser = null;
	
	private YamlConfigParser ymlParser = null;
	
	protected XMLConfigParser getXmlParser() {
		
		if (xmlParser == null)
			xmlParser = new XMLConfigParser();
		
		
		return xmlParser;
		
	}
	
	protected YamlConfigParser getYamlParser() {
		
		if (ymlParser == null)
			ymlParser = new YamlConfigParser();
		
		return ymlParser;
	}
	
	protected ModelManager getModel() {

		return ModelManager.getInstance();
		
	}
}
