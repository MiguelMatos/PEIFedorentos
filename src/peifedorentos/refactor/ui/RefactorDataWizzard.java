package peifedorentos.refactor.ui;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;





public class RefactorDataWizzard extends RefactoringWizard {

	
	public RefactorDataWizzard(RefactorPreview refactoring, String pageTitle) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(pageTitle);
	}
	
	@Override
	protected void addUserInputPages() {
		// TODO Auto-generated method stub
		addPage(new RefactorDataInputPage("IntroduceIndirectionInputPage"));
	}

}
