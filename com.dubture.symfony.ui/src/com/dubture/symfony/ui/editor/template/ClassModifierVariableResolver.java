/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.ui.editor.template;

import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

public class ClassModifierVariableResolver extends TemplateVariableResolver {

	
	public ClassModifierVariableResolver(String type, String description) {

		super(type, description);				
	}


	@Override
	public void resolve(TemplateVariable variable, TemplateContext context) {
		
		if (context instanceof SymfonyTemplateContext) {
			
			try {
							
				SymfonyTemplateContext symfonyContext = (SymfonyTemplateContext) context;
				String modifiers = (String) symfonyContext.getTemplateVariable("class_modifiers");
				
				if (modifiers != null) {
					variable.setValue(modifiers);
					variable.setResolved(true);
				}
				
				
			} catch (Exception e) {

				e.printStackTrace();
			}			
		}			
	}	
}
