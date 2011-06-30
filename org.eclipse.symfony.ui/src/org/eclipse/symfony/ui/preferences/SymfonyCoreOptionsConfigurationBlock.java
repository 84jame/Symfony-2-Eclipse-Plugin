package org.eclipse.symfony.ui.preferences;


import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.symfony.core.SymfonyCorePlugin;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

@SuppressWarnings("restriction")
public abstract class SymfonyCoreOptionsConfigurationBlock extends
		OptionsConfigurationBlock {

	public SymfonyCoreOptionsConfigurationBlock(IStatusChangeListener context,
			IProject project, Key[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);

	}

	@Override
	protected abstract Control createContents(Composite parent);

	@Override
	protected abstract void validateSettings(Key changedKey, String oldValue,
			String newValue);

	@Override
	protected abstract String[] getFullBuildDialogStrings(boolean workspaceSettings);
	
	protected final static Key getSymfonyCoreKey(String key) {
		return getKey(SymfonyCorePlugin.ID, key);
	}	

}
