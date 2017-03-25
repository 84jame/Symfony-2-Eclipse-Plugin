/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.php.core.compiler.ast.nodes.PHPModuleDeclaration;

import com.dubture.symfony.core.log.Logger;

/**
 *
 * Not used yet.
 *
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class SymfonyBuildParticipant implements IBuildParticipant {
	private PHPModuleDeclaration getModuleDeclaration(IBuildContext context) {
		if (context.get(IBuildContext.ATTR_MODULE_DECLARATION) instanceof PHPModuleDeclaration) {
			return (PHPModuleDeclaration) context.get(IBuildContext.ATTR_MODULE_DECLARATION);
		}

		return null;
	}

	@Override
	public void build(IBuildContext context) throws CoreException {
		try {
			PHPModuleDeclaration module = getModuleDeclaration(context);

		} catch (Exception e) {
			Logger.logException(e);
		}
	}
}
