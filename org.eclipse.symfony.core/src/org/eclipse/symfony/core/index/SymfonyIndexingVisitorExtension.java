package org.eclipse.symfony.core.index;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.TokenBuffer;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.index2.IIndexingRequestor.ReferenceInfo;
import org.eclipse.php.core.index.PhpIndexingVisitorExtension;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPMethodDeclaration;
import org.eclipse.php.internal.core.typeinference.PHPModelUtils;
import org.eclipse.symfony.core.SymfonyCoreConstants;
import org.eclipse.symfony.index.SymfonyIndexer;


/**
 * 
 * {@link SymfonyIndexingVisitorExtension} contributes model elements
 * to the index.
 * 
 *
 * TODO: This indexer is currently called on any PHP Project, regardless
 * of a Symfony nature: Find a way to check if the Indexer is indexing
 * a Sourcemodule of a project with the SymfonyNature.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class SymfonyIndexingVisitorExtension extends
PhpIndexingVisitorExtension {


	private boolean inController = false;
	private ClassDeclaration currentClass;
	private NamespaceDeclaration namespace;
	private ControllerIndexingVisitor controllerIndexer;
	private SymfonyIndexer indexer;
	

	
	
	@Override
	public boolean visit(ModuleDeclaration s) throws Exception {
	
		return true;
	}

	@Override
	public boolean visit(Statement s) throws Exception {

		//System.err.println(s.getClass());
		return true;

	}
	

	@Override
	public boolean visit(Expression s) throws Exception {

		//System.err.println("expression:  " + s.getClass());
		return super.visit(s);
	}

	@Override
	public boolean visit(TypeDeclaration s) throws Exception {

		if (indexer == null)
			indexer = SymfonyIndexer.getInstance();

		if (s instanceof ClassDeclaration) {

			currentClass = (ClassDeclaration) s;

			for (Object o : currentClass.getSuperClasses().getChilds()) {

				if (o instanceof FullyQualifiedReference) {

					FullyQualifiedReference superReference = (FullyQualifiedReference) o;					

					//TODO: find a way to check against the FQCN
					// via the UseStatement
					if (/*superReference.getNamespace().equals(SymfonyCoreConstants.CONTROLLER_NS) 
							&& */superReference.getName().equals(SymfonyCoreConstants.CONTROLLER_CLASS)) {

						inController = true;						
						controllerIndexer = new ControllerIndexingVisitor();
						currentClass.traverse(controllerIndexer);
						


					} 
				} 
			}
		} else if (s instanceof NamespaceDeclaration) {
			namespace = (NamespaceDeclaration) s;
		}

		return true; 
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean endvisit(TypeDeclaration s) throws Exception {

		if (controllerIndexer != null) {

			Map<ASTNode, String> variables = controllerIndexer.getTemplateVariables();			
			Iterator it = variables.keySet().iterator();
			
			while(it.hasNext()) {
				
				ASTNode variable = (ASTNode) it.next();
				int start = variable.sourceStart();
				int length = variable.sourceEnd() - variable.sourceStart();
				String fqcn = variables.get(variable);
				
				String name = null;				
				
				if (variable instanceof SimpleReference) {
					name = ((SimpleReference) variable).getName().replaceAll("['\"]", "");

					String phpClass = PHPModelUtils.extractElementName(fqcn);
					String namespace = PHPModelUtils.extractNameSapceName(fqcn);
					
					JsonFactory factory = new JsonFactory();
					StringWriter writer = new StringWriter();
					JsonGenerator jg = factory.createJsonGenerator(writer);
					
					jg.writeString(phpClass);
					jg.writeString(namespace);					
					jg.flush();
					jg.close();
					
					
					ReferenceInfo info = new ReferenceInfo(IModelElement.USER_ELEMENT, start, length, name, writer.getBuffer().toString(), namespace);
					requestor.addReference(info);
					
					System.err.println("added reference info to requestor " + writer.getBuffer().toString());
					
//					indexer.addTemplateVariable(name, phpClass, namespace, "");						
//					System.err.println("added variable " + name + " to indexer");
					
				} else if (variable instanceof Literal) {
					name = ((Literal) variable).getValue().replaceAll("['\"]", "");	
				}
				
				
				
				
//				DeclarationInfo info = new DeclarationInfo(IModelElement.FIELD, 
//						Modifiers.AccPublic, start, length, start, length, name, 
//						null, null, namespace.getName(), currentClass.getName());		
//				
//				if (requestor != null && name != null) {					
//					System.err.println("added field declaration in " + namespace.getName() + " "
//							+ currentClass.getName() + " for " + name + " from " + start + " to " + (start + length));				
//					
//					requestor.addDeclaration(info);
//				}
			}
			
//			indexer.commitVariables();
		}
		
		inController = false;
		currentClass = null;
		namespace = null;
		controllerIndexer = null;
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration s) throws Exception {

		if (s instanceof PHPMethodDeclaration) {

			PHPMethodDeclaration method = (PHPMethodDeclaration) s;
			if (inController) {
				
//				System.err.println("method in controller " + s.getName() + " " + namespace.getName() + " " + currentClass.getName());
			}


		}
		return true;
	}
		


	
}