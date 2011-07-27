package com.dubture.symfony.core.preferences;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.dubture.symfony.core.log.Logger;
import com.dubture.symfony.core.preferences.CorePreferenceConstants.Keys;


/**
 * 
 * Provides acces to the {@link IEclipsePreferences} on a
 * ProjectScope, falls back to the Workspace preferences
 * if not project scoped value is there.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ProjectOptions {
	
	private ProjectOptions() {

	}
	
	/**
	 * 
	 * Retrieve the synthetic services of a project.
	 * 
	 * 
	 * @param project
	 * @return
	 */
	public static final JSONArray getSyntheticServices(IProject project) {
		
		JSONArray defaultSynthetics = null;
		
		try {
			String synths = CorePreferencesSupport.getInstance()
			.getPreferencesValue(Keys.SYNTHETIC_SERVICES, null, project);		
			
			Logger.debugMSG("LOADED DEFAULTS: " + synths);
			
			JSONParser parser = new JSONParser();
			defaultSynthetics = (JSONArray) parser.parse(synths);
			
		} catch (Exception e) {
			Logger.logException(e);			
		}		
		
		return defaultSynthetics;
	}	
	
	
	public static final String getDefaultSyntheticServices() {
	
		return CorePreferencesSupport.getInstance().getWorkspacePreferencesValue(Keys.SYNTHETIC_SERVICES);
		
	}
}
