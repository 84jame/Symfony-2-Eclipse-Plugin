/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.ui.wizards.project;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.php.internal.ui.wizards.CompositeData;
import org.eclipse.php.internal.ui.wizards.NameGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.composer.ui.wizard.project.template.PackageProjectWizardFirstPage;
import com.dubture.composer.ui.wizard.project.template.Validator;
import com.dubture.getcomposer.core.ComposerPackage;
import com.dubture.symfony.ui.SymfonyUiPlugin;

@SuppressWarnings("restriction")
public class SymfonyProjectWizardFirstPage extends PackageProjectWizardFirstPage implements IShellProvider {

	private Validator projectTemplateValidator;
	
	public SymfonyProjectWizardFirstPage() {
		setPageComplete(false);
		setTitle("Basic Symfony settings");
		setDescription("Create a new Symfony project and select the target location");
	}
	
	
	@Override
	public void createControl(Composite parent) {
		
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		initialName = "";
		nameGroup = new NameGroup(composite, initialName, getShell());
		nameGroup.addObserver(this);
		PHPLocationGroup = new SymfonyLocationGroup(composite, nameGroup, getShell());
		
		CompositeData data = new CompositeData();
		data.setParetnt(composite);
		data.setSettings(getDialogSettings());
		data.setObserver(PHPLocationGroup);

		versionGroup = new SymfonyVersionGroup(this, composite);
		
		nameGroup.addObserver(PHPLocationGroup);
		nameGroup.notifyObservers();
		projectTemplateValidator = new Validator(this);
		
		nameGroup.addObserver(projectTemplateValidator);
		PHPLocationGroup.addObserver(projectTemplateValidator);

		Dialog.applyDialogFont(composite);
		
		setControl(composite);
		composerPackage = new ComposerPackage();
		keywordConverter = new String2KeywordsConverter(composerPackage);
		
		setHelpContext(composite);
		setPageComplete(false);
		
	}

	@Override
	protected void setHelpContext(Control container) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, SymfonyUiPlugin.PLUGIN_ID + "." + "newproject_firstpage");		
	}
	
	@Override
	public boolean doesOverrideComposer() {
		return true;
	}

	public String getSymfonyVersion() {
		return ((SymfonyVersionGroup)versionGroup).getSymfonyVersion();
	}
	
	public String getVirtualHost() {
		return ((SymfonyLocationGroup)PHPLocationGroup).getVirtualHost();
	}
}
