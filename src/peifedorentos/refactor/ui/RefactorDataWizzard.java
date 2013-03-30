package peifedorentos.refactor.ui;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;






public class RefactorDataWizzard extends RefactoringWizard {

	
	public RefactorDataWizzard(Refactoring ref, String pageTitle) {
		super(ref, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(pageTitle);
	}
	
	@Override
	protected void addUserInputPages() {
		// TODO Auto-generated method stub
		addPage(new RefactorDataInputPage("IntroduceIndirectionInputPage"));
	}

}
