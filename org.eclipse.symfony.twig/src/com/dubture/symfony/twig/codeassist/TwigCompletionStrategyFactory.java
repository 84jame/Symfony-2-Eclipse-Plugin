package com.dubture.symfony.twig.codeassist;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.internal.core.codeassist.strategies.AbstractCompletionStrategy;
import org.eclipse.twig.core.codeassist.ITwigCompletionStrategyFactory;

import com.dubture.symfony.core.codeassist.strategies.RouteCompletionStrategy;
import com.dubture.symfony.twig.codeassist.context.RouteCompletionContext;
import com.dubture.symfony.twig.codeassist.context.TemplateVariableCompletionContext;
import com.dubture.symfony.twig.codeassist.context.TemplateVariableFieldCompletionContext;
import com.dubture.symfony.twig.codeassist.context.ViewPathArgumentContext;
import com.dubture.symfony.twig.codeassist.strategies.TemplateVariableCompletionStrategy;
import com.dubture.symfony.twig.codeassist.strategies.TemplateVariableFieldCompletionStrategy;
import com.dubture.symfony.twig.codeassist.strategies.ViewPathCompletionStrategy;

/**
 * 
 * {@link TwigCompletionStrategyFactory} provides Symfony2 completion
 * strategies for the Twig plugin.
 *  
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class TwigCompletionStrategyFactory implements
		ITwigCompletionStrategyFactory {

	public TwigCompletionStrategyFactory() {

	}


	@Override
	public AbstractCompletionStrategy[] create(ICompletionContext[] contexts) {


		List<AbstractCompletionStrategy> result = new LinkedList<AbstractCompletionStrategy>();
		
		for (ICompletionContext context : contexts) {
			if (context.getClass() == TemplateVariableCompletionContext.class) {
				
				result.add(new TemplateVariableCompletionStrategy(context));
				
			} else if (context.getClass() == TemplateVariableFieldCompletionContext.class) {
				
				result.add(new TemplateVariableFieldCompletionStrategy(context));
				
			} else if (context.getClass() == RouteCompletionContext.class) {
				
				result.add(new RouteCompletionStrategy(context));
				
			} else if (context.getClass() == ViewPathArgumentContext.class) {
				
				result.add(new ViewPathCompletionStrategy(context));
			}
		}
		
		return (AbstractCompletionStrategy[]) result
		        .toArray(new AbstractCompletionStrategy[result.size()]);
				
	}
}
