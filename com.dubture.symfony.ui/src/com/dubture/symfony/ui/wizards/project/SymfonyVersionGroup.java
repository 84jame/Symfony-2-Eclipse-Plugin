package com.dubture.symfony.ui.wizards.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.dltk.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.ui.wizard.AbstractVersionGroup;
import com.dubture.composer.ui.wizard.AbstractWizardFirstPage;
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.getcomposer.core.RepositoryPackage;
import com.dubture.getcomposer.core.collection.Versions;
import com.dubture.getcomposer.packages.AsyncPackagistDownloader;
import com.dubture.getcomposer.packages.PackageListenerInterface;
import com.dubture.symfony.core.preferences.SymfonyCoreConstants;

@SuppressWarnings("restriction")
public class SymfonyVersionGroup extends AbstractVersionGroup {

	protected ComboDialogField symfonyVersionSelector;
	
	public SymfonyVersionGroup(AbstractWizardFirstPage composerProjectWizardFirstPage, Composite composite) {
		super(composerProjectWizardFirstPage, composite, 4, PHPVersion.PHP5_3);
	}
	
	@Override
	protected void createSubComponents(Group group) {
		symfonyVersionSelector = new ComboDialogField(SWT.READ_ONLY);
		symfonyVersionSelector.setLabelText("Symfony Version:");
		symfonyVersionSelector.doFillIntoGrid(group, 2);
		symfonyVersionSelector.setDialogFieldListener(this);
		fConfigurationBlock.setMinimumVersion(PHPVersion.PHP5_3);
		loadVersionCombo();
	}
	
	protected void loadVersionCombo() {

		symfonyVersionSelector.setItems(new String[]{"Loading versions..."});
		symfonyVersionSelector.selectItem(0);
		
		AsyncPackagistDownloader dl = new AsyncPackagistDownloader();
		dl.addPackageListener(new PackageListenerInterface() {

			@Override
			public void errorOccured(Exception e) {
				Logger.logException(e);
			}

			@Override
			public void aborted(String url) {
				
			}

			@Override
			public void packageLoaded(RepositoryPackage repositoryPackage) {
				Versions versions = repositoryPackage.getVersions();
				final List<String> versionNames = new ArrayList<String>();
				for (Entry<String, ComposerPackage> version : versions) {
					versionNames.add(version.getValue().getVersion());
				}

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						symfonyVersionSelector.setItems(versionNames.toArray(new String[versionNames.size()]));
						symfonyVersionSelector.selectItem(0);
					}
				});
			}
		});

		dl.loadPackage(SymfonyCoreConstants.SYMFONY_STANDARD_EDITION);
	}
	
	public String getSymfonyVersion() {
		return symfonyVersionSelector.getText();
	}
}
