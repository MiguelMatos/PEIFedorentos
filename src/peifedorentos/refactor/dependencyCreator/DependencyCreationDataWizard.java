package peifedorentos.refactor.dependencyCreator;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class DependencyCreationDataWizard extends RefactoringWizard {

	public DependencyCreationDataWizard(DependencyCreationRefactoring refactoring, String pageTitle) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		
		setDefaultPageTitle(pageTitle);
	}
	
	@Override
	protected void addUserInputPages() {
		addPage(new DependencyCreationInputPage("DependencyCreationInputPage"));
	}

}
