package org.eclipse.symfony.ui.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.symfony.core.SymfonyCoreConstants;
import org.eclipse.symfony.ui.SymfonyUiPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class SymfonyPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public SymfonyPreferencePage() {
		super(GRID);
		
		setPreferenceStore(SymfonyUiPlugin.getDefault().getPreferenceStore());
		setDescription("Symfony2 preferences");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		
		
		String[][] options = new String[][] 
		{ 
				{ SymfonyCoreConstants.ANNOTATION_ERROR, SymfonyCoreConstants.ANNOTATION_ERROR }, 
				{ SymfonyCoreConstants.ANNOTATION_WARNING, SymfonyCoreConstants.ANNOTATION_WARNING },
				{ SymfonyCoreConstants.ANNOTATION_IGNORE, SymfonyCoreConstants.ANNOTATION_IGNORE },				
		};
		
		addField(new ComboFieldEditor(SymfonyCoreConstants.ANNOTATION_PROBLEM_SEVERITY, 
				"Annotation Problems", options, getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		
	}
	
}