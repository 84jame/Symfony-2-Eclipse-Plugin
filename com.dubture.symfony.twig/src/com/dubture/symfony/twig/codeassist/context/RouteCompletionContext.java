/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.twig.codeassist.context;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.php.internal.core.util.text.TextSequence;

import com.dubture.twig.core.codeassist.context.QuotesContext;
import com.dubture.twig.core.util.text.TwigTextSequenceUtilities;

/**
 *
 * Checks for a valid context to complete route names.
 *
 * Right now the only check is to validate we're staying inside a method call
 * and not a single string literal.
 *
 *
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class RouteCompletionContext extends QuotesContext {

	@Override
	public boolean isValid(IDocument template, int offset, IProgressMonitor monitor) {
		if (!super.isValid(template, offset, monitor)) {
			return false;
		}

		TextSequence statement = getStatementText();

		if (!TwigTextSequenceUtilities.isInFunction(statement)) {
			return false;
		}

		return true;
	}
}
