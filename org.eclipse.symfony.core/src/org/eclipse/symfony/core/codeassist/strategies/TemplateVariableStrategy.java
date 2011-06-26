package org.eclipse.symfony.core.codeassist.strategies;


import java.util.List;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.CodeAssistUtils;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.codeassist.strategies.GlobalElementStrategy;
import org.eclipse.symfony.core.model.SymfonyModelAccess;

/**
 * 
 * Completes variables in templates declared in 
 * Symfony2 controllers.
 * 
 * 
 * @author "Robert Gruendler <r.gruendler@gmail.com>"
 *
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class TemplateVariableStrategy extends GlobalElementStrategy {

	public TemplateVariableStrategy(ICompletionContext context) {
		super(context);

	}

	@Override
	public void apply(ICompletionReporter reporter) throws Exception {

		AbstractCompletionContext ctxt = (AbstractCompletionContext) getContext();
		ISourceModule module = ctxt.getSourceModule();
		SymfonyModelAccess model = SymfonyModelAccess.getDefault();
		
		IType controller = model.findControllerByTemplate(module);		
		List<IModelElement> variables = model.findTemplateVariables(controller);
		
		SourceRange range = getReplacementRange(getContext());
		String prefix = ctxt.getPrefix();

		for(IModelElement element : variables) {
			
			if (CodeAssistUtils.startsWithIgnoreCase(element.getElementName(), prefix))
				reporter.reportField((IField) element, "", range, false);
		}
	}
}