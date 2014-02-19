/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.index.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import com.dubture.symfony.index.SymfonyIndex;

public class SymfonyIndexPreferences extends AbstractPreferenceInitializer {

	public static final String DB_LOCK_MODE = "db_lock_mode";
	public static final String DB_CACHE_TYPE = "db_cache_type";
	public static final String DB_CACHE_SIZE = "db_cache_size";
	public static final String SCHEMA_VERSION = "0.2";


	@Override
	@SuppressWarnings("deprecation")
	public void initializeDefaultPreferences() {

		IEclipsePreferences p = ((IScopeContext) new DefaultScope())
				.getNode(SymfonyIndex.PLUGIN_ID);

		p.putInt(DB_CACHE_SIZE, 32000); // 32Mb
		p.put(DB_CACHE_TYPE, "LRU");
		p.putInt(DB_LOCK_MODE, 0); // no transaction isolation


	}

}
