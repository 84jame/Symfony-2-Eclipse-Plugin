package org.eclipse.symfony.ui.contentassist;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.text.completion.MemberProposalInfo;
import org.eclipse.symfony.core.model.Bundle;
import org.eclipse.symfony.ui.utils.HTMLUtils;

/**
 * 
 * 
 * 
 * @author "Robert Gruendler <r.gruendler@gmail.com>"
 *
 */
public class BundleProposalInfo extends MemberProposalInfo {

	public BundleProposalInfo(IScriptProject project,
			CompletionProposal proposal) {
		super(project, proposal);

	}
	
	@Override
	public String getInfo(IProgressMonitor monitor) {
 
		try {
			
			return HTMLUtils.bundle2Html((Bundle) getModelElement());			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}	
}
