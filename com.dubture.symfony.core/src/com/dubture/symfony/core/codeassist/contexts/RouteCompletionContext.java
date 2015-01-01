/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.core.codeassist.contexts;

import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.internal.core.util.text.TextSequence;

import com.dubture.symfony.core.log.Logger;
import com.dubture.symfony.core.util.text.SymfonyTextSequenceUtilities;


/**
 * 
 * Detects if we're staying in a route context:
 * 
 *  
 *  <pre>
 *  
 *  $view['router']->generate('| <-- valid route context
 *  
 *  </pre>
 * 
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class RouteCompletionContext extends QuoteIdentifierContext{

	@Override
	public boolean isValid(ISourceModule sourceModule, int offset,
			CompletionRequestor requestor) {


		if (super.isValid(sourceModule, offset, requestor)) {

			try {

				if (requestor == null || !requestor.getClass().toString().contains("Symfony")) {
					return false;
				}
				TextSequence statement = getStatementText();
				IScriptProject project = getSourceModule().getScriptProject();

				if (SymfonyTextSequenceUtilities.isInRouteFunctionParameter(statement, project) == false) {
					return false;
				}

				return true;

			} catch (Exception e) {
				Logger.logException(e);
			}
		}

		return false;
	}
}
