package org.eclipse.symfony.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.symfony.ui.PreferenceConstants;

public class SymfonyPreferenceInitializer extends AbstractPreferenceInitializer {



	@Override
	public void initializeDefaultPreferences() {

		PreferenceConstants.initializeDefaultValues();

	}

}
