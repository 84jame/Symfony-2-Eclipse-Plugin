package com.dubture.symfony.ui.wizards.classes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.internal.ui.dialogs.OpenTypeSelectionDialog2;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.php.internal.core.documentModel.provisional.contenttype.ContentTypeIdForPHP;
import org.eclipse.php.internal.ui.PHPUILanguageToolkit;
import org.eclipse.php.internal.ui.PHPUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.dubture.symfony.ui.SymfonyPluginImages;
import com.dubture.symfony.ui.wizards.CodeTemplateWizardPage;

@SuppressWarnings("restriction")
public class ClassCreationWizardPage extends CodeTemplateWizardPage {


	protected Text fileText;
	protected Text superClassText;
	
	protected ISelection selection;

	protected Label targetResourceLabel;
	protected Label superClassLabel;
	
	private List<String> interfaces = new ArrayList<String>();
	
	private TableViewer interfaceTable;
	
	private Button abstractCheckbox;
	private Button finalCheckbox;
	
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ClassCreationWizardPage(final ISelection selection, String initialFileName) {
		super("wizardPage", initialFileName); //$NON-NLS-1$
		setTitle("New Class"); //$NON-NLS-1$
		setDescription("Create a new class"); //$NON-NLS-1$
		setImageDescriptor(SymfonyPluginImages.DESC_WIZBAN_ADD_SYMFONY_FILE);
		this.selection = selection;
	}

	
	private OpenTypeSelectionDialog2 getDialog(int type, String title, String message) {
		
		final Shell p = DLTKUIPlugin.getActiveWorkbenchShell();
		OpenTypeSelectionDialog2 dialog = new OpenTypeSelectionDialog2(p,
				true, PlatformUI.getWorkbench().getProgressService(), null,
				type, PHPUILanguageToolkit.getInstance());

		dialog.setTitle(title);
		dialog.setMessage(message);

		return dialog;
		
	}
	
	
	
	private SelectionListener superClassSelectionListener  = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {

			OpenTypeSelectionDialog2 dialog = getDialog(IDLTKSearchConstants.TYPE, "Superclass selection", "Select superclass");

			int result = dialog.open();
			if (result != IDialogConstants.OK_ID)
				return;
			
			Object[] types = dialog.getResult();
			if (types != null && types.length > 0) {
				IModelElement type = null;
				for (int i = 0; i < types.length; i++) {
					type = (IModelElement) types[i];
					try {
													
						String superclass = "";
						
						if (type.getParent() == null)
							return;
						
						superclass += type.getParent().getElementName() + "\\";
						superclass += type.getElementName();
						
						superClassText.setText(superclass);

					} catch (Exception x) {

					}
				}
			}				
			
			
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	private SelectionListener interfaceSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			OpenTypeSelectionDialog2 dialog = getDialog(IDLTKSearchConstants.TYPE, "Interface selection", "Select interface");

			int result = dialog.open();
			if (result != IDialogConstants.OK_ID)
				return;
			
			Object[] types = dialog.getResult();
			if (types != null && types.length > 0) {
				IModelElement type = null;
				for (int i = 0; i < types.length; i++) {
					type = (IModelElement) types[i];
					try {
													
						String _interface = "";
						
						if (type.getParent() == null)
							return;
						
						_interface += type.getParent().getElementName() + "\\";
						_interface += type.getElementName();
						
						interfaces.add(_interface);						
						interfaceTable.setInput(interfaces);

					} catch (Exception x) {

					}
				}
			}				
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}
	};
	

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(final Composite parent) {
		
		final Composite container = new Composite(parent, SWT.NULL);
		
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 400;
		
		superClassLabel = new Label(container, SWT.NULL);
		superClassLabel.setText("Superclass");
		
		superClassText = new Text(container, SWT.BORDER | SWT.SINGLE);
		superClassText.setLayoutData(gd);
		
		Button button = new Button(container, SWT.NULL);
		button.setText("Browse");
				
		button.addSelectionListener(superClassSelectionListener);	

		targetResourceLabel = new Label(container, SWT.NULL);
		targetResourceLabel.setText("Controller name");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		fileText.setFocus();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		// gd.widthHint = 300;
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				dialogChanged();
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		
		abstractCheckbox = new Button(container, SWT.CHECK);	
		abstractCheckbox.setText("abstract");
		
	    finalCheckbox = new Button(container, SWT.CHECK);
	    finalCheckbox.setText("final");		
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;		
		
		interfaceTable = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		
		
				
		interfaceTable.setContentProvider(ArrayContentProvider.getInstance());
		interfaceTable.setInput(interfaces);
		
		
		interfaceTable.getControl().setLayoutData(gridData);
		
		Button addInterface = new Button(container, SWT.NULL);
		addInterface.setText("Add");
		
		addInterface.addSelectionListener(interfaceSelectionListener);

		initialize();
		dialogChanged();
		setControl(container);
//		PlatformUI
//				.getWorkbench()
//				.getHelpSystem()
//				.setHelp(parent,
//						IPHPHelpContextIds.CREATING_A_PHP_FILE_WITHIN_A_PROJECT);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1) {
				return;
			}

			Object obj = ssel.getFirstElement();
			if (obj instanceof IAdaptable) {
				obj = ((IAdaptable) obj).getAdapter(IResource.class);
			}

			IContainer container = null;
			if (obj instanceof IResource) {
				if (obj instanceof IContainer) {
					container = (IContainer) obj;
				} else {
					container = ((IResource) obj).getParent();
				}
			}

			if (container != null) {
				containerName = container.getFullPath().toString();
			}
		}
		setInitialFileName(initialFileName); //$NON-NLS-1$
	}

	protected void setInitialFileName(final String fileName) {
		
		fileText.setFocus();
		fileText.setText(fileName);
		fileText.setSelection(0, fileName.length());
	}


	/**
	 * Ensures that both text fields are set.
	 */
	protected void dialogChanged() {
		final String container = getContainerName();
		final String fileName = getFileName();

		if (container.length() == 0) {
			updateStatus(PHPUIMessages.PHPFileCreationWizardPage_10); //$NON-NLS-1$
			return;
		}
		final IContainer containerFolder = getContainer(container);
		if (containerFolder == null || !containerFolder.exists()) {
			updateStatus(PHPUIMessages.PHPFileCreationWizardPage_11); //$NON-NLS-1$
			return;
		}
		if (!containerFolder.getProject().isOpen()) {
			updateStatus(PHPUIMessages.PHPFileCreationWizardPage_12); //$NON-NLS-1$
			return;
		}
		if (fileName != null
				&& !fileName.equals("") && containerFolder.getFile(new Path(fileName)).exists()) { //$NON-NLS-1$
			updateStatus(PHPUIMessages.PHPFileCreationWizardPage_14); //$NON-NLS-1$
			return;
		}

		int dotIndex = fileName.lastIndexOf('.');
		if (fileName.length() == 0 || dotIndex == 0) {
			updateStatus(PHPUIMessages.PHPFileCreationWizardPage_15); //$NON-NLS-1$
			return;
		}

		if (dotIndex != -1) {
			String fileNameWithoutExtention = fileName.substring(0, dotIndex);
			for (int i = 0; i < fileNameWithoutExtention.length(); i++) {
				char ch = fileNameWithoutExtention.charAt(i);
				if (!(Character.isJavaIdentifierPart(ch) || ch == '.' || ch == '-')) {
					updateStatus(PHPUIMessages.PHPFileCreationWizardPage_16); //$NON-NLS-1$
					return;
				}
			}
		}

		final IContentType contentType = Platform.getContentTypeManager()
				.getContentType(ContentTypeIdForPHP.ContentTypeID_PHP);
		
//		if (!contentType.isAssociatedWith(fileName)) {
//			// fixed bug 195274
//			// get the extensions from content type
//			final String[] fileExtensions = contentType
//					.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
//			StringBuffer buffer = new StringBuffer(
//					PHPUIMessages.PHPFileCreationWizardPage_17); //$NON-NLS-1$
//			buffer.append(fileExtensions[0]);
//			for (String extension : fileExtensions) {
//				buffer.append(", ").append(extension); //$NON-NLS-1$
//			}
//			buffer.append("]"); //$NON-NLS-1$
//			updateStatus(buffer.toString());
//			return;
//		}

		updateStatus(null);
	}

	protected void updateStatus(final String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}


	public String getFileName() {
		return fileText.getText();
	}

	public String getSuperclass() {

		return superClassText.getText();

	}
	
	public List<String> getInterfaces() {
		
		return interfaces;
		
	}


	public String getModifiers() {

		String modifiers = "";
		
		if (abstractCheckbox.isEnabled())
			modifiers = "abstract ";

		if (finalCheckbox.isEnabled())
			modifiers += "final ";
			
		return modifiers;
		
	}
}
