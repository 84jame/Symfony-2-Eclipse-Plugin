package org.eclipse.symfony.core.index.visitor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.ASTNodeKinds;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.ArrayElement;
import org.eclipse.php.internal.core.compiler.ast.nodes.Assignment;
import org.eclipse.php.internal.core.compiler.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.internal.core.compiler.ast.nodes.FullyQualifiedReference;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPCallExpression;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPDocBlock;
import org.eclipse.php.internal.core.compiler.ast.nodes.PHPMethodDeclaration;
import org.eclipse.php.internal.core.compiler.ast.nodes.ReturnStatement;
import org.eclipse.php.internal.core.compiler.ast.nodes.Scalar;
import org.eclipse.php.internal.core.compiler.ast.nodes.UsePart;
import org.eclipse.php.internal.core.compiler.ast.nodes.UseStatement;
import org.eclipse.php.internal.core.compiler.ast.visitor.PHPASTVisitor;
import org.eclipse.symfony.core.SymfonyCoreConstants;
import org.eclipse.symfony.core.SymfonyCorePlugin;
import org.eclipse.symfony.core.model.Service;
import org.eclipse.symfony.core.model.SymfonyModelAccess;
import org.eclipse.symfony.core.model.TemplateVariable;
import org.eclipse.symfony.core.util.ModelUtils;


/**
 * 
 * The {@link ControllerIndexingVisitor} indexes the following
 * ModelElements in Symfony2 controllers:
 * 
 * 
 * 1. Template variables
 * 2. Annotations
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class ControllerIndexingVisitor extends PHPASTVisitor {

	private Map<TemplateVariable, String> templateVariables = new HashMap<TemplateVariable, String>();
	private Stack<TemplateVariable> deferredVariables = new Stack<TemplateVariable>();

	private PHPMethodDeclaration currentMethod;

	final private List<UseStatement> useStatements;

	private boolean inAction = false;


	public ControllerIndexingVisitor(List<UseStatement> useStatements) {

		this.useStatements = useStatements;
	}


	public Map<TemplateVariable, String> getTemplateVariables() {
		return templateVariables;
	}


	@Override
	public boolean visit(PHPMethodDeclaration method) throws Exception {

		currentMethod = method;
		deferredVariables = new Stack<TemplateVariable>();

		if (method.getName().endsWith(SymfonyCoreConstants.ACTION_SUFFIX)) {

			inAction = true;
			boolean foundAnnotation = false;
			PHPDocBlock docs = method.getPHPDoc();

			if (docs != null) {

				BufferedReader buffer = new BufferedReader(new StringReader(docs.getShortDescription()));

				try {
					String line;
					while((line = buffer.readLine()) != null) {

						//TODO: parse @Template() parameters
						if (line.startsWith(SymfonyCoreConstants.TEMPLATE_ANNOTATION)) {

							foundAnnotation = true;
							break;

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	@Override
	public boolean endvisit(PHPMethodDeclaration s) throws Exception {

		deferredVariables = null;
		currentMethod = null;
		inAction = false;
		return true;
	}

	
	/**
	 * Parse {@link ReturnStatement}s and try to evaluate
	 * the variables.
	 * 
	 */
	@Override
	public boolean visit(ReturnStatement statement) throws Exception {

		//TODO: Only parse ARRAY_CREATION return types when
		// the Template() annotation is set
		if (statement.getExpr().getKind() == ASTNodeKinds.ARRAY_CREATION) {

			//Action action = new Action(controller, method);
			ArrayCreation array = (ArrayCreation) statement.getExpr();

			for (ArrayElement element : array.getElements()) {

				Expression key = element.getKey();
				Expression value = element.getValue();


				if (key.getClass() == Scalar.class) {

					Scalar varName = (Scalar) key;

					// something in the form:  return array ('foo' => $bar);
					// check the type of $bar:
					if (value.getClass() == VariableReference.class) {

						VariableReference ref = (VariableReference) value;

						for (TemplateVariable variable : deferredVariables) {

							// we got the variable, add it the the templateVariables
							if (ref.getName().equals(variable.getName())) {								
								// alter the variable name
								variable.setName(varName.getValue());
								templateVariables.put(variable, "");
								break;
							}							
						}

						// this is more complicated, something like:
						// return array('form' => $form->createView());
						// we need to infer $form and then check the returntype of createView()
					} else if(value.getClass() == PHPCallExpression.class) {

						PHPCallExpression callExp = (PHPCallExpression) value;
						VariableReference varRef = (VariableReference) callExp.getReceiver();

						if (varRef == null) {
							continue;
						}

						SimpleReference callName = callExp.getCallName();

						// we got the variable name (in this case $form)
						// now search for the defferedVariable:						
						for (TemplateVariable deferred : deferredVariables) {

							// we got it, find the returntype of the
							// callExpression
							if (deferred.getName().equals(varRef.getName())) {

								TemplateVariable tempVar = SymfonyModelAccess.getDefault()
										.createTemplateVariableByReturnType(currentMethod, 
												callName, deferred.getClassName(), deferred.getNamespace(), 
												varRef.getName());

								templateVariables.put(tempVar, "");
								break;
							}
						}

						// this is a direct ClassInstanceCreation, ie:
						// return array('user' => new User());
					} else if (value.getClass() == ClassInstanceCreation.class) {

						ClassInstanceCreation instance = (ClassInstanceCreation) value;

						if (instance.getClassName().getClass() == FullyQualifiedReference.class) {

							FullyQualifiedReference fqcn = (FullyQualifiedReference) instance.getClassName();
							NamespaceReference nsRef = createFromFQCN(fqcn);

							if (nsRef != null) {
								TemplateVariable variable = new TemplateVariable(currentMethod, varName.getValue(), 
										varName.sourceStart(), varName.sourceEnd(), nsRef.getNamespace(), nsRef.getClassName());
								templateVariables.put(variable, "");
							}
						}
					} else {

						SymfonyCorePlugin.debug(this.getClass(), "array value: " + value.getClass());
					}
				}
			}
		}	
		return true;
	}		


	/**
	 * 
	 * Collect all Assignments inside a {@link PHPMethodDeclaration}
	 * to infer them in the ReturnStatements and add it to the
	 * templateVariables.
	 * 
	 */
	@Override
	public boolean visit(Assignment s) throws Exception {

		if (inAction) {

			Service service = null;
			if (s.getVariable().getClass() == VariableReference.class) {

				VariableReference var = (VariableReference) s.getVariable();		

				// A call expression like $foo = $this->get('bar');
				//
				if (s.getValue().getClass() == PHPCallExpression.class) {

					PHPCallExpression exp = (PHPCallExpression) s.getValue();

					// are we requesting a Service?
					if (exp.getName().equals("get")) {

						service = ModelUtils.extractServiceFromCall(exp);

						if (service != null) {
							TemplateVariable tempVar= new TemplateVariable(currentMethod, var.getName(), exp.sourceStart(), exp.sourceEnd(), service.getNamespace(), service.getClassName());							
							deferredVariables.push(tempVar);
						}

					// a more complex expression like
					// $form = $this->get('form.factory')->create(new ContactType());
					} else if (exp.getReceiver().getClass() == PHPCallExpression.class) {

						// try to extract a service if it's a Servicecontainer call
						service = ModelUtils.extractServiceFromCall((PHPCallExpression) exp.getReceiver());
						
						// nothing found, return
						if (service == null || exp.getCallName() == null) {
							
							System.err.println("nothing found");
							return true;
						}

						SimpleReference callName = exp.getCallName();

						TemplateVariable tempVar = SymfonyModelAccess.getDefault()
								.createTemplateVariableByReturnType(currentMethod, callName, 
										service.getClassName(), service.getNamespace(), var.getName());

						if (tempVar != null) {								
							deferredVariables.push(tempVar);
						}

					// something like $formView = $form->createView(); 
					} else if (exp.getReceiver().getClass() == VariableReference.class) {
						
						VariableReference varRef = (VariableReference) exp.getReceiver();
						SimpleReference ref = exp.getCallName();
						
						// check for a previosly declared variable
						for (TemplateVariable tempVar : deferredVariables) {							
							if (tempVar.getName().equals(varRef.getName())) {
								
								TemplateVariable tVar = SymfonyModelAccess.getDefault()
										.createTemplateVariableByReturnType(currentMethod, ref, tempVar.getClassName(), tempVar.getNamespace(), var.getName());
								
								if (tVar != null) {
									deferredVariables.push(tVar);
									break;
								}								
							}							
						}
					}
				// a simple ClassInstanceCreation, ie. $contact = new ContactType();
				} else if (s.getValue().getClass() == ClassInstanceCreation.class) {

					ClassInstanceCreation instance = (ClassInstanceCreation) s.getValue();

					if (instance.getClassName().getClass() == FullyQualifiedReference.class) {

						FullyQualifiedReference fqcn = (FullyQualifiedReference) instance.getClassName();
						NamespaceReference nsRef = createFromFQCN(fqcn);

						if (nsRef != null) {
							TemplateVariable variable = new TemplateVariable(currentMethod, var.getName(), 
									var.sourceStart(), var.sourceEnd(), nsRef.getNamespace(), nsRef.getClassName());
							deferredVariables.push(variable);
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * Get the ClassName and Namespace from a {@link FullyQualifiedReference}
	 * 
	 * @param fqcn
	 * @return
	 */
	private NamespaceReference createFromFQCN(FullyQualifiedReference fqcn) {

		for (UseStatement use : useStatements) {
			for (UsePart part : use.getParts()) {					
				if (part.getNamespace().getName().equals(fqcn.getName())) {

					String name = fqcn.getName();
					String qualifier = part.getNamespace().getNamespace().getName();

					return new NamespaceReference(qualifier, name);
				}
			}								
		}

		return null;
	}


	/**
	 * 
	 * Simple helper class to pass around namespaces.
	 * 
	 * @author "Robert Gruendler <r.gruendler@gmail.com>"
	 *
	 */
	private class NamespaceReference {		

		private String namespace;
		private String className;

		public NamespaceReference(String qualifier, String name) {

			this.namespace = qualifier;
			this.className = name;
		}

		public String getNamespace() {
			return namespace;
		}

		public String getClassName() {
			return className;
		}

	}
}