package org.eclipse.symfony.core.codeassist.strategies;

import java.util.List;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.ICompletionReporter;
import org.eclipse.php.internal.core.codeassist.contexts.AbstractCompletionContext;
import org.eclipse.php.internal.core.codeassist.strategies.MethodParameterKeywordStrategy;
import org.eclipse.symfony.core.codeassist.contexts.RouteCompletionContext;
import org.eclipse.symfony.core.model.RouteSource;
import org.eclipse.symfony.core.model.SymfonyModelAccess;
import org.eclipse.symfony.index.dao.Route;


/**
 * 
 * Completes route names inside a {@link RouteCompletionContext}
 * 
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class RouteCompletionStrategy extends MethodParameterKeywordStrategy {

	public static int workaroundCount = 0;
	
	public RouteCompletionStrategy(ICompletionContext context) {
		super(context);
		
		

	}
	
	@Override
	public void apply(ICompletionReporter reporter) throws BadLocationException {
		
		// FIXME: this is a VERY dirty hack to report the route completions
		// only to the SymfonyCompletionProposalCollector which
		// shows the correct popup information.
		// otherwise each route will shown twice.
		//
		// unfortunately there's no other way using the DLTK mechanism at the moment
		if (workaroundCount == 0) {
			workaroundCount++;
			return;
		} else if (workaroundCount == 1) {
			workaroundCount = 0;
		}
		
		//TODO: this needs caching!!!
		AbstractCompletionContext context = (AbstractCompletionContext) getContext();		
		ISourceModule module = context.getSourceModule();		
		List<Route> routes = SymfonyModelAccess.getDefault().findRoutes(module.getScriptProject());		
		SourceRange range = getReplacementRange(context);
		
		SymfonyModelAccess model = SymfonyModelAccess.getDefault();
		
		for (Route route : routes) {

			IType controller = model.findController(route.bundle, route.controller, context.getSourceModule().getScriptProject());
			
			if (controller == null)
				continue;
			
			RouteSource rs = new RouteSource((ModelElement) controller, route.name, route);
			reporter.reportType(rs, "", range);

		}	
	}
}