package org.eclipse.symfony.core.codeassist.strategies;


import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.strategies.GlobalElementStrategy;
import org.eclipse.php.internal.core.typeinference.FakeField;
import org.eclipse.symfony.core.codeassist.contexts.TemplateVariableContext;
import org.eclipse.symfony.core.index.SymfonyElementResolver.TemplateField;

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

		TemplateVariableContext ctxt = (TemplateVariableContext) getContext();
		SourceRange range = getReplacementRange(getContext());
		String viewPath = ctxt.getViewPath();
		
		for(TemplateField element : ctxt.getVariables()) {

			if (viewPath.equals(element.getViewPath())) {
				reporter.reportField(new FakeField(element, element.getElementName(), Modifiers.AccPublic), "", range, false);
			}
		}
	}
}