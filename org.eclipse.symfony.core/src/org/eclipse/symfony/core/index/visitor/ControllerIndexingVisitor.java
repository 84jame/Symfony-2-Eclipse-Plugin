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

	public ControllerIndexingVisitor(List<UseStatement> useStatements) {

		this.useStatements = useStatements;
	}

	public Map<TemplateVariable, String> getTemplateVariables() {
		return templateVariables;
	}

	private boolean inAction = false;


	@Override
	public boolean visit(UseStatement s) throws Exception {

		return true;
	}

	@Override
	public boolean visit(PHPMethodDeclaration method) throws Exception {

		currentMethod = method;
		deferredVariables = new Stack<TemplateVariable>();

		if (method.getName().endsWith(SymfonyCoreConstants.ACTION_SUFFIX)) {

			System.err.println("visit method " + method.getName());

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

		System.err.println();

		deferredVariables = null;
		currentMethod = null;
		inAction = false;
		return true;
	}

	@Override
	public boolean visit(ReturnStatement statement) throws Exception {

		//TODO: Only parse ARRAY_CREATION return types when
		// the Template() annotation is set

		if (statement.getExpr().getKind() == ASTNodeKinds.ARRAY_CREATION) {

			System.err.println("visit return");

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

					} else if(value.getClass() == PHPCallExpression.class) {


						//						Iterator it = templateVariables.keySet().iterator();
						//						
						//						while(it.hasNext()) {								
						//							String var = (String) it.next();
						//							var = var.replace("$", "");
						//							TemplateVariable variable = templateVariables.get(var);								
						//							action.addTemplateVariable(variable);								
						//							
						//						}							
					} else if (value.getClass() == ClassInstanceCreation.class) {

						ClassInstanceCreation instance = (ClassInstanceCreation) value;

						if (instance.getClassName().getClass() == FullyQualifiedReference.class) {

							FullyQualifiedReference fqcn = (FullyQualifiedReference) instance.getClassName();

							boolean found = false;

							for (UseStatement use : useStatements) {
								for (UsePart part : use.getParts()) {					
									if (part.getNamespace().getName().equals(fqcn.getName())) {

										String name = fqcn.getName();
										String qualifier = part.getNamespace().getNamespace().getName();

										TemplateVariable variable = new TemplateVariable(currentMethod, varName.getValue(), 
												varName.sourceStart(), varName.sourceEnd(), qualifier, name);
										templateVariables.put(variable, "");
										found = true;
										break;
									}
								}								

								if (found)
									break;
							}

							//							TemplateVariable variable = new TemplateVariable(currentMethod, varName.getValue(), 
							//									varName.sourceStart(), varName.sourceEnd(), fqcn.getNamespace().getName(), fqcn.getName());
							//							
							//							templateVariables.put(variable, "");							

						}
					} else {



						SymfonyCorePlugin.debug(this.getClass(), "array value: " + value.getClass());
					}
				}
			}

			//			controller.addAction(action);
		}	
		return true;
	}		

	@Override
	public boolean endvisit(ReturnStatement s) throws Exception {


		return true;
	}

	@Override
	public boolean visit(Assignment s) throws Exception {

		if (inAction) {

			System.err.println("visit assignment");

			Service service = null;
			if (s.getVariable().getClass() == VariableReference.class) {

				VariableReference var = (VariableReference) s.getVariable();			
				if (s.getValue().getClass() == PHPCallExpression.class) {

					PHPCallExpression exp = (PHPCallExpression) s.getValue();

					// are we calling a method named "get" ?
					if (exp.getName().equals("get")) {


						service = ModelUtils.extractServiceFromCall(exp);

						if (service != null) {
							TemplateVariable tempVar= new TemplateVariable(currentMethod, var.getName(), exp.sourceStart(), exp.sourceEnd(), service.getNamespace(), service.getClassName());							
							deferredVariables.push(tempVar);
//							templateVariables.put(varName, tempVar);
						}

					} else {

						if (exp.getReceiver().getClass() == PHPCallExpression.class) {

							service = ModelUtils.extractServiceFromCall((PHPCallExpression) exp.getReceiver());

							if (service == null || exp.getCallName() == null) {
								return true;
							}

							SimpleReference callName = exp.getCallName();

							TemplateVariable tempVar = SymfonyModelAccess.getDefault()
									.createTemplateVariableByReturnType(currentMethod, callName, 
											service.getClassName(), service.getNamespace(), var.getName());

							if (tempVar != null) {								

								//								templateVariables.put(tempVar, tempVar.getClassName());
								deferredVariables.push(tempVar);
							}
						}
					}
				}
			}
		}
		return true;
	}
}