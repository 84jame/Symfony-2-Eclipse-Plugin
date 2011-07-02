package org.eclipse.symfony.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.symfony.core.Logger;
import org.eclipse.symfony.core.visitor.AnnotationVisitor;

/**
 * 
 * Not used yet.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class SymfonyBuildParticipant implements IBuildParticipant {

	
	
	
	private ModuleDeclaration getModuleDeclaration(IBuildContext context) {
		
		ISourceModule sourceModule = context.getSourceModule();		
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule);
		
		return moduleDeclaration;		
		
	}
	
	@Override
	public void build(IBuildContext context) throws CoreException {
			
		try {
			
			IFile file = context.getFile();
			
			ModuleDeclaration module = getModuleDeclaration(context);									
			
			if (file.getFileExtension().equals("php")) {				
				module.traverse(new AnnotationVisitor(context));								
			}
			
		} catch (Exception e) {
			
			Logger.logException(e);
		}
	}
}