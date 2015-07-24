/*******************************************************************************
 * This file is part of the Symfony eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.symfony.ui.editor.hyperlink;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ModelElementHyperlink;
import org.eclipse.dltk.ui.actions.OpenAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;

import com.dubture.symfony.core.builder.SymfonyNature;
import com.dubture.symfony.core.log.Logger;
import com.dubture.symfony.core.model.SymfonyModelAccess;
import com.dubture.symfony.core.model.ViewPath;

/**
 * 
 * {@link ViewpathHyperlinkDetector} links viewPaths to the corresponding
 * template / action / controller.
 * 
 * 
 * 
 * @author "Robert Gruendler <r.gruendler@gmail.com>"
 *
 */
@SuppressWarnings("restriction")
public class ViewpathHyperlinkDetector extends StringHyperlinkDetector {

	private ISourceModule input;
	private PHPStructuredEditor editor;

	private IHyperlink getControllerlink(ViewPath viewPath, IRegion wordRegion) {
		
		IType controller = SymfonyModelAccess.getDefault().findController(viewPath.getBundle(), viewPath.getController(), input.getScriptProject());

		if (controller != null) {			
			
			String tpl = viewPath.getTemplate();
			
			// try to open a corresponding action
			try {
				String action = tpl.substring(0, tpl.indexOf("."));
				
				if (action.length() > 0) {					
					IMethod method = controller.getMethod(action + "Action");					
					return new ModelElementHyperlink(wordRegion, method, new OpenAction(editor));
				}
			} catch (Exception e) {
				// ignore and open the controller
			}
			
			return new ModelElementHyperlink(wordRegion, controller, new OpenAction(editor));
			
		}		
		
		return null;
	}
	
	private IHyperlink getTemplateLink(ViewPath viewPath, IRegion wordRegion) {
						
		IScriptFolder folder = SymfonyModelAccess.getDefault().findBundleFolder(viewPath.getBundle(), input.getScriptProject());
		
		if (folder == null) {
			Logger.debugMSG("Unable to resolve template link: " + viewPath);
			return null;
		}
		
		String path = "Resources/views/";
		
		if (viewPath.getController() != null) {			
			path += viewPath.getController() + "/";
		}
		
		path += viewPath.getTemplate();		
		ISourceModule module = folder.getSourceModule(path);
		
		if (module != null) {			
			return new ModelElementHyperlink(wordRegion, module, new OpenAction(editor));		
		}
				
		return null;
		
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {

		editor = org.eclipse.php.internal.ui.util.EditorUtility
				.getPHPEditor(textViewer);

		if (editor == null || region == null) {
			return null;
		}
		
		

		input = EditorUtility.getEditorInputModelElement(editor, false);
		
		if (input == null) {
			editor = null;
			return null;
		}
		
		try {
			if (input.getScriptProject() == null || !input.getScriptProject().getProject().hasNature(SymfonyNature.NATURE_ID)) {
				return null;
			}
		} catch (CoreException e1) {
			return null;
		}

		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		try {

			IRegion wordRegion = findWord(document, offset);
			
			if (wordRegion == null) {
				return null;
			}

			String path = document.get(wordRegion.getOffset(), wordRegion.getLength());
			if (wordRegion.getOffset() > 0) {
				String start = document.get(wordRegion.getOffset() - 1, 1);
				if (!start.equals("'") && !start.equals("\"") && !start.equals(",")) {
					// this is not a string. Might be static member access
					return null;
				}
			}
			ViewPath viewPath = new ViewPath(path);
			
			// 	"AcmeDemoBundle::layout.html.twig" 2 part viewpath
			// link the whole thing to the template
			if (path.contains("::")) {

				String[] parts = path.split("::");
				
				if (parts.length == 2) {					
					IHyperlink link = getTemplateLink(viewPath, wordRegion);					
					if (link != null) {						
						return new IHyperlink[] { link };
					}
				}

			// "AcmeDemoBundle:Demo:index.html.twig" 3 part viewpath
			// link the controller and the template separately
			} else if (path.contains(":")) {

				String[] parts = path.split(":");

				if (parts.length == 3) {
					
					int controllerLength = parts[0].length() + parts[1].length() + 1;		
					int deltaOffset = offset - wordRegion.getOffset();

					if (deltaOffset < controllerLength) {
						
						Region controllerRegion =  new Region(wordRegion.getOffset(), controllerLength);						
						IHyperlink link = getControllerlink(viewPath, controllerRegion);
						
						if (link != null) {
							return new IHyperlink[] { link };
						}
						
					} else {
						
						Region templateRegion = new Region(wordRegion.getOffset() + controllerLength + 1, parts[2].length());
						IHyperlink link = getTemplateLink(viewPath, templateRegion);
						
						if (link != null) {							
							return new IHyperlink[] { link };
						}
					}
					
				}
			}
						
		} catch (Exception e) {			
			Logger.logException(e);
		} finally {
			input = null;
			editor = null;
		}

		return null;
	}
}
