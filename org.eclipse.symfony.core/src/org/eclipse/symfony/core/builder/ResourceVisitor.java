package org.eclipse.symfony.core.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.index2.IIndexerParticipant;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.php.internal.core.index.PhpIndexerParticipant;
import org.eclipse.symfony.core.parser.XMLConfigParser;
import org.eclipse.symfony.core.parser.YamlConfigParser;
import org.eclipse.symfony.core.parser.YamlRoutingParser;
import org.eclipse.symfony.index.SymfonyIndexer;
import org.eclipse.symfony.index.dao.Route;
import org.yaml.snakeyaml.scanner.ScannerException;


/**
 * 
 * The {@link ResourceVisitor} is a standard buildvisitor to 
 * parse xml/yml config files from a Symfony2 project during
 * a full build.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ResourceVisitor extends AbstractSymfonyVisitor 
implements IResourceVisitor {

	private IFile file;
	private IPath path;
	private SymfonyIndexer indexer;
	private int timestamp;

	@Override
	public boolean visit(IResource resource) throws CoreException {
		
		try {

			if (resource instanceof IFile && resource.getFileExtension() != null) {

				indexer = SymfonyIndexer.getInstance();
				file = (IFile) resource;
				path = resource.getFullPath();
				resource.getParent();
				timestamp = (int) resource.getLocalTimeStamp();
				

				if (resource.getFileExtension().equals("xml"))
				{				
					loadXML();

				} else if (resource.getFileExtension().equals("yml")) 
				{
					
					if (resource.getName().equals("services.yml")) {					
						loadYaml();
					} else if (resource.getName().contains("routing")) {
						loadYamlRouting();
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return true;
	}


	private void loadYamlRouting() {

		try {			

			YamlRoutingParser parser = new YamlRoutingParser(file.getContents());
			parser.parse();
			
			indexRoutes(parser.getRoutes());		
			
		} catch (ScannerException se) {
			System.err.println(se.getMessage());
		} catch (Exception e) {			
			System.err.println(e.getMessage() + " " + e.getClass().toString());
		}
	}


	private void indexRoutes(Stack<Route> routes) {

		//indexer.enterRoutes();
		for (Route route : routes) {
			indexer.addRoute(route);
		}
		indexer.exitRoutes();		
		
	}


	private void loadYaml() {

		try {
			
			YamlConfigParser parser = new YamlConfigParser(file.getContents());
			parser.parse();

			indexServices(parser.getServices());

		} catch (Exception e1) {

			System.err.println(e1.getMessage());

		}

	}

	private void loadXML() {

		try {

			XMLConfigParser parser;					
			parser = new XMLConfigParser(file.getContents());
			parser.parse();

			System.err.println("parse xml");
			if (parser.hasServices()) {
				indexServices(parser.getServices());
			} 

		} catch (Exception e) {

			//System.err.println("xml error: " + e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	private void indexServices(HashMap<String, String> services) {

		try {

			indexer.enterServices(path.toString());
			Iterator it = services.keySet().iterator();

			while(it.hasNext()) {

				String id = (String) it.next();
				String phpClass = services.get(id);
				
				System.err.println("index service " + id + " " + path.toString());
				indexer.addService(id, phpClass, path.toString(), timestamp);

			}
			
			indexer.exitServices();			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}