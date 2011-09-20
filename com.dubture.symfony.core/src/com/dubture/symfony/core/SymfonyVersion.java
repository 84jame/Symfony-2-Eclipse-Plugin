package com.dubture.symfony.core;

import java.util.HashMap;
import java.util.Map;




public enum SymfonyVersion {

	Symfony2("Symfony2.0"), //$NON-NLS-1$
	Symfony2_1("Symfony2.1"); //$NON-NLS-1$

	private String alias;

	private static class Aliases {
		private static Map<String, SymfonyVersion> map = new HashMap<String, SymfonyVersion>();
	}

	SymfonyVersion(String alias) {
		this.alias = alias;
		Aliases.map.put(alias, this);
	}

	public String getAlias() {
		return alias;
	}

	public static SymfonyVersion byAlias(String alias) {
		return Aliases.map.get(alias);
	}

	public boolean isLessThan(SymfonyVersion SymfonyVersion) {
		return ordinal() < SymfonyVersion.ordinal();
	}

	public boolean isGreaterThan(SymfonyVersion SymfonyVersion) {
		return ordinal() > SymfonyVersion.ordinal();
	}
}
