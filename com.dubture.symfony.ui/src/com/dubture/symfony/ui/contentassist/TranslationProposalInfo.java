package com.dubture.symfony.ui.contentassist;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.text.completion.MemberProposalInfo;

public class TranslationProposalInfo extends MemberProposalInfo {

	public TranslationProposalInfo(IScriptProject project,
			CompletionProposal proposal) {
		super(project, proposal);

	}

}
