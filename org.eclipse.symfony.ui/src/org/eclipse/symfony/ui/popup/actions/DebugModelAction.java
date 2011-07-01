package org.eclipse.symfony.ui.popup.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.symfony.core.model.Annotation;
import org.eclipse.symfony.core.model.AnnotationParameter;
import org.eclipse.symfony.core.model.Bundle;
import org.eclipse.symfony.core.model.Controller;
import org.eclipse.symfony.core.model.ModelManager;
import org.eclipse.symfony.core.model.Project;
import org.eclipse.symfony.core.model.Service;
import org.eclipse.symfony.core.preferences.SymfonyCoreConstants;
import org.eclipse.symfony.ui.SymfonyUiPlugin;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class DebugModelAction implements IObjectActionDelegate {

	@Override
	@SuppressWarnings("rawtypes")	
	public void run(IAction action) {
		
		IPreferenceStore store = SymfonyUiPlugin.getDefault().getPreferenceStore();
		String warnType = store.getString(SymfonyCoreConstants.ANNOTATION_PROBLEM_SEVERITY);
		
		System.err.println(warnType);
				
		
		List<Project> projects = ModelManager.getInstance().getProjects();
		
		System.out.println("The current workspace contains " + projects.size() + " projects");
			
		for (Project project : ModelManager.getInstance().getProjects()) {
			
			List<Service> projectServices = project.getProjectScopedServices();
			
			if (projectServices.size() > 0) {
				
				System.err.println("The project " + project.toDebugString() + " contains " + projectServices.size() + " project-scoped services");
				for(Service service : projectServices) {
					System.out.println("-- " + service.getId() + " => " + service.getFullyQualifiedName());
				}
			}
			
			List<Bundle> bundles = project.getBundles();			
			System.out.println(project.toDebugString() + " contains " + bundles.size() + " bundles)");		
			
			for (Bundle bundle : bundles) {
				
				List<Service> services = bundle.getServices();
				System.err.println("-- " + bundle.getName() + " (contains " + services.size() + " services)");
				
				for (Service service : services) {					
					System.out.println("---- " + service.getFullyQualifiedName());					
				}
				
				List<Controller> controllers = bundle.getControllers();
				
				System.err.println("-- " + bundle.getName() + " contains " + controllers.size() + " controllers: ");
				
				for (Controller controller : controllers) {
					System.out.println("---- " + controller.getName());
				}
				
				
			}
			
			List<Annotation> annotations = project.getAnnotations();
			
			System.err.println(project.toDebugString() + " contains " + annotations.size() +  " annotations:");
			
			
			for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {
				Annotation annotation = (Annotation) iterator.next();
				
				List<AnnotationParameter> params = annotation.getParameters();
				System.out.println("-- " + annotation.getName() + " (" + params.size() + " parameters)");
				
				for (Iterator iterator2 = params.iterator(); iterator2
						.hasNext();) {
					AnnotationParameter annotationParameter = (AnnotationParameter) iterator2
							.next();
					
					System.out.println("---- " + annotationParameter.getName());
					
				}				
			}		
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

}
