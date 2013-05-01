package peifedorentos.refactor.staticCall;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import peifedorentos.refactor.dependencyCreator.DependencyCreationInputPage;

public class StaticCallDataWizard extends RefactoringWizard {

	public StaticCallDataWizard(Refactoring refactoring, String pageTitle) {
		
	super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		
		setDefaultPageTitle(pageTitle);
	}

	@Override
	protected void addUserInputPages() {
		//addPage(new StaticCallInputPage("StaticCallInputPage"));
	}

}
