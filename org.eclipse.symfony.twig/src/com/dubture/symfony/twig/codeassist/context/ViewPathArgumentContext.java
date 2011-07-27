package com.dubture.symfony.twig.codeassist.context;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.internal.core.util.text.TextSequence;
import org.eclipse.twig.core.codeassist.context.QuotesContext;
import org.eclipse.twig.core.util.text.TwigTextSequenceUtilities;

import com.dubture.symfony.core.log.Logger;
import com.dubture.symfony.core.model.ViewPath;
import com.dubture.symfony.core.util.text.SymfonyTextSequenceUtilities;
import com.dubture.symfony.twig.codeassist.strategies.ViewPathCompletionStrategy;

/**
 * 
 * Checks if a {@link ViewPathCompletionStrategy} can be applied.
 * 
 * This is true if the curser is inside a single string literal,
 * and not in a function parameter.
 *
 * 
 * I haven't found a way yet for a better validation mechanism here.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class ViewPathArgumentContext extends QuotesContext {
	
	
	private ViewPath viewPath;
	
	@Override
	public boolean isValid(ISourceModule sourceModule, int offset,
			CompletionRequestor requestor) {

		if(super.isValid(sourceModule, offset, requestor)) {
			
			 try {				 
				 				 
				 TextSequence statement = getStatementText();

				 if (TwigTextSequenceUtilities.isInFunction(statement)) {
					 return false;
				 }
				 
				 int startOffset = SymfonyTextSequenceUtilities.readViewPathStartIndex(statement);
				 String path = getDocument().getText().substring(statement.getOriginalOffset(startOffset), offset);
				 
				 if (path != null) {								
					 viewPath = new ViewPath(path);
					 return true;
				 }
				 
			} catch (Exception e) {
				Logger.logException(e);
			}
		}
		
		return false;
	}
	
	public ViewPath getViewPath() {
		return viewPath;
	}	

}