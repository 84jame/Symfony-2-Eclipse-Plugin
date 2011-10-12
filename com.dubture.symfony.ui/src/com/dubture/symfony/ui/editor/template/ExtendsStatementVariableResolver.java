package com.dubture.symfony.ui.editor.template;

import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;


/**
 * 
 * Resolves ${extends} variables in code templates.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ExtendsStatementVariableResolver extends TemplateVariableResolver {

	public ExtendsStatementVariableResolver(String type, String description) {

		super(type, description);
	}
	
	
	@Override
	public void resolve(TemplateVariable variable, TemplateContext context) {
		
		
		if (context instanceof SymfonyTemplateContext) {
			
			SymfonyTemplateContext symfonyContext = (SymfonyTemplateContext) context;
			
			try {
				
				String value = (String) symfonyContext.getTemplateVariable("extends");
				
				if (value != null && value.length() > 0) {
					
					String statement = "extends " + value;					
					variable.setValue(statement);
				} else {
					variable.setValue("");
				}
				
				variable.setResolved(true);				
				
				
			} catch (Exception e) {

				e.printStackTrace();
			}			
		}			
	}
}