package com.dubture.symfony.ui.wizards.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.ui.PHPUIMessages;
import org.eclipse.php.internal.ui.PHPUiPlugin;
import org.eclipse.php.internal.ui.preferences.PHPProjectLayoutPreferencePage;
import org.eclipse.php.internal.ui.preferences.PreferenceConstants;
import org.eclipse.php.internal.ui.wizards.CompositeData;
import org.eclipse.php.internal.ui.wizards.DetectGroup;
import org.eclipse.php.internal.ui.wizards.LocationGroup;
import org.eclipse.php.internal.ui.wizards.NameGroup;
import org.eclipse.php.internal.ui.wizards.PHPProjectWizardFirstPage;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.internal.ui.wizards.fields.ComboDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.dubture.symfony.core.SymfonyVersion;
import com.dubture.symfony.core.log.Logger;
import com.dubture.symfony.ui.wizards.ISymfonyProjectWizardExtension;

@SuppressWarnings("restriction")
public class SymfonyProjectWizardFirstPage extends PHPProjectWizardFirstPage {
	
	
	public static final String WIZARDEXTENSION_ID = "com.dubture.symfony.ui.projectWizardExtension";
	
	private SymfonySupportGroup symfonySupportGroup;
	private SymfonyLayoutGroup fSymfonyLayoutGroup;
	
	private List<ISymfonyProjectWizardExtension> extensions = new ArrayList<ISymfonyProjectWizardExtension>();
	
	public SymfonyProjectWizardFirstPage() {
				
		setPageComplete(false);
		setTitle("New Symfony project");
		setDescription("Create a Symfony project in the workspace or in an external location.");
		fInitialName = ""; //$NON-NLS-1$$
	}
	
		

	public void createControl(Composite parent) {
		
		initializeDialogUnits(parent);
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		// create UI elements
		fNameGroup = new NameGroup(composite, fInitialName, getShell());
		fPHPLocationGroup = new LocationGroup(composite, fNameGroup, getShell());
		
		fPHPLocationGroup.addObserver(new Observer() {
			
			@Override
			public void update(Observable arg0, Object arg1) {
				fSymfonyLayoutGroup.setEnabled(!fPHPLocationGroup.isExistingLocation());
			}
		});

		CompositeData data = new CompositeData();
		data.setParetnt(composite);
		data.setSettings(getDialogSettings());
		data.setObserver(fPHPLocationGroup);
		fragment = (WizardFragment) Platform.getAdapterManager().loadAdapter(
				data, PHPProjectWizardFirstPage.class.getName());

		// don't really have a choice with Symfony2 ;)
//		fVersionGroup = new VersionGroup(composite);		
		
		fSymfonyLayoutGroup = new SymfonyLayoutGroup(composite);
		fJavaScriptSupportGroup = new JavaScriptSupportGroup(composite, this);
		
		symfonySupportGroup = new SymfonySupportGroup(composite, this);		
		fDetectGroup = new DetectGroup(composite, fPHPLocationGroup, fNameGroup);

		// establish connections
		fNameGroup.addObserver(fPHPLocationGroup);
		fDetectGroup.addObserver(fSymfonyLayoutGroup);

		fPHPLocationGroup.addObserver(fDetectGroup);
		// initialize all elements
		fNameGroup.notifyObservers();
		// create and connect validator
		fPdtValidator = new Validator();

		fNameGroup.addObserver(fPdtValidator);
		fPHPLocationGroup.addObserver(fPdtValidator);

		setControl(composite);
		Dialog.applyDialogFont(composite);

		// set the focus to the project name
		fNameGroup.postSetFocus();

		setHelpContext(composite);
	}
	
	public boolean hasTwigSupport() {
		
		return symfonySupportGroup.getSelection();
	}
	
	public List<ISymfonyProjectWizardExtension> getExtensions() {
		
		return extensions;
		
	}
	
	
	public class SymfonySupportGroup implements SelectionListener {

		private final Group fGroup;
		protected Button fEnableJavaScriptSupport;

		public boolean shouldSupportJavaScript() {
			return PHPUiPlugin.getDefault().getPreferenceStore()
					.getBoolean((PreferenceConstants.JavaScriptSupportEnable));
		}
		
		

		public SymfonySupportGroup(Composite composite,
				WizardPage projectWizardFirstPage) {
			final int numColumns = 3;
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());

			fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fGroup.setLayout(initGridLayout(new GridLayout(numColumns, false),
					true));
			fGroup.setText("Symfony"); //$NON-NLS-1$
			
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(WIZARDEXTENSION_ID);			
			extensions = new ArrayList<ISymfonyProjectWizardExtension>();
			
			try {				
				for (IConfigurationElement e : config) {				
					final Object object = e.createExecutableExtension("class");					
					if (object instanceof ISymfonyProjectWizardExtension) {										
						ISymfonyProjectWizardExtension extension = (ISymfonyProjectWizardExtension) object;						
						extension.addElements(fGroup);
						extensions.add(extension);
					}
				}				
			} catch (Exception e) {
				Logger.logException(e);		
			}					
			
			// hide the symfony group if no extensions is filling it up
			if (config.length == 0)
				fGroup.setVisible(false);
			
			
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			 PHPUiPlugin.getDefault().getPreferenceStore().setValue(
			 (PreferenceConstants.JavaScriptSupportEnable),
			 fEnableJavaScriptSupport.getSelection());
		}

		public boolean getSelection() {
			return fEnableJavaScriptSupport.getSelection();
		}

	}	
	
	/**
	 * Request a project layout.
	 */
	public class SymfonyLayoutGroup implements Observer, SelectionListener,
			IDialogFieldListener {

		private SelectionButtonDialogField fStdRadio, fSrcBinRadio;
		private Group fGroup;
		private Link fPreferenceLink;
		private SelectionButtonDialogField fStdRadio2;
		private ComboDialogField projectLayout;
		private ComboDialogField dialog;

		public SymfonyLayoutGroup(Composite composite) {
			final int numColumns = 4;

			String[] layouts = new String[] {
					"Standard Edition (vendors)",
					"Standard Edition (no vendors)",
					"Custom project layout"
					};
			
			final Map<String, String[]> available = new HashMap<String, String[]>();
			
			
			available.put(layouts[0], new String[]
					{SymfonyVersion.Symfony2_1.getAlias(),
					SymfonyVersion.Symfony2.getAlias()}
					);

			available.put(layouts[1], new String[]
					{SymfonyVersion.Symfony2_1.getAlias(),
					SymfonyVersion.Symfony2.getAlias()}
					);

			available.put(layouts[2], new String[]
					{"Standard Edition (vendors)",
					"Standard Edition (no vendors)",
					"Custom project layout"});
			
			projectLayout = new ComboDialogField(SWT.READ_ONLY);
			projectLayout.setLabelText("");
			projectLayout.setItems(layouts);			
			
			
			dialog = new ComboDialogField(SWT.READ_ONLY);
			dialog.setLabelText("Select layout:");
			dialog.setItems(layouts);			
			
			dialog.setDialogFieldListener(new org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener() {
				
				@Override
				public void dialogFieldChanged(
						org.eclipse.php.internal.ui.wizards.fields.DialogField field) {
					
					String[] items = dialog.getItems();
					
					String index = items[dialog.getSelectionIndex()];
					String[] selection = available.get(index);
					
					projectLayout.setItems(selection);
					projectLayout.selectItem(0);					
					
				}
			});
			
			// createContent
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fGroup.setLayout(initGridLayout(new GridLayout(numColumns, false),
					true));
			fGroup.setText(PHPUIMessages.LayoutGroup_OptionBlock_Title); //$NON-NLS-1$

			dialog.doFillIntoGrid(fGroup, 2);
			
			projectLayout.doFillIntoGrid(fGroup, 2);
			dialog.selectItem(0);
			projectLayout.selectItem(0);

			
//			fPreferenceLink = new Link(fGroup, SWT.NONE);
//			fPreferenceLink
//					.setText(PHPUIMessages.ToggleLinkingAction_link_description); //$NON-NLS-1$
//			fPreferenceLink.setLayoutData(new GridData(SWT.END, SWT.BEGINNING,
//					true, false));
//			fPreferenceLink.addSelectionListener(this);
//			fPreferenceLink.setEnabled(true);

			updateEnableState();
		}

		public void setEnabled(boolean b) {

			dialog.setEnabled(b);
			projectLayout.setEnabled(b);
			
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			updateEnableState();
		}

		private void updateEnableState() {
			
//			if (fDetectGroup == null)
//				return;
//
//			final boolean detect = fDetectGroup.mustDetect();
//			fStdRadio.setEnabled(!detect);
//			fSrcBinRadio.setEnabled(!detect);
//
//			if (fGroup != null) {
//				fGroup.setEnabled(!detect);
//			}
		}

		/**
		 * Return <code>true</code> if the user specified to create
		 * 'application' and 'public' folders.
		 * 
		 * @return returns <code>true</code> if the user specified to create
		 *         'source' and 'bin' folders.
		 */
		public boolean isDetailedLayout() {
			return fSrcBinRadio.isSelected();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener
		 * #dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.
		 * DialogField)
		 * 
		 * @since 3.5
		 */
		public void dialogFieldChanged(DialogField field) {
			updateEnableState();
		}

		public void widgetDefaultSelected(SelectionEvent e) {

			String prefID = PHPProjectLayoutPreferencePage.PREF_ID;

			Map data = null;
			PreferencesUtil.createPreferenceDialogOn(getShell(), prefID,
					new String[] { prefID }, data).open();
		}
		
		
	}	
	
	
	public boolean shouldSupportJavascript() {
		
		return fJavaScriptSupportGroup.shouldSupportJavaScript();
		
	}
	
	// Symfony requires php 5.3
	public PHPVersion getPHPVersionValue() {
		
		return PHPVersion.PHP5_3;
		
	}
	
	

}
