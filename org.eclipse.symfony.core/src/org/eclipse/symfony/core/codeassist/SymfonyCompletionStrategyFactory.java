package org.eclipse.symfony.core.codeassist;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.php.core.codeassist.ICompletionContext;
import org.eclipse.php.core.codeassist.ICompletionStrategy;
import org.eclipse.php.core.codeassist.ICompletionStrategyFactory;
import org.eclipse.symfony.core.codeassist.contexts.AnnotationCompletionContext;
import org.eclipse.symfony.core.codeassist.contexts.ServiceContainerContext;
import org.eclipse.symfony.core.codeassist.contexts.ServiceReturnTypeContext;
import org.eclipse.symfony.core.codeassist.contexts.TemplateVariableContext;
import org.eclipse.symfony.core.codeassist.strategies.AnnotationCompletionStrategy;
import org.eclipse.symfony.core.codeassist.strategies.ServiceContainerCompletionStrategy;
import org.eclipse.symfony.core.codeassist.strategies.ServiceReturnTypeCompletionStrategy;
import org.eclipse.symfony.core.codeassist.strategies.TemplateVariableStrategy;

/**
 * Factory class for CompletionStrategies. 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class SymfonyCompletionStrategyFactory implements ICompletionStrategyFactory {


	@Override
	public ICompletionStrategy[] create(ICompletionContext[] contexts) {

		List<ICompletionStrategy> result = new LinkedList<ICompletionStrategy>();
		
		for (ICompletionContext context : contexts) {
			if (context.getClass() == AnnotationCompletionContext.class) {
				result.add(new AnnotationCompletionStrategy(context));
			} else if (context.getClass() == ServiceContainerContext.class) {				
				result.add(new ServiceContainerCompletionStrategy(context));
			} else if (context.getClass() == ServiceReturnTypeContext.class) {				
				result.add(new ServiceReturnTypeCompletionStrategy(context));
			} else if (context.getClass() == TemplateVariableContext.class) {
				result.add(new TemplateVariableStrategy(context));
			}
		}
		
		return (ICompletionStrategy[]) result
		        .toArray(new ICompletionStrategy[result.size()]);

	}
}
