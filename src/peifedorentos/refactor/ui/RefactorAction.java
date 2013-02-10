package peifedorentos.refactor.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;



public class RefactorAction implements IWorkbenchWindowActionDelegate {

	private static final String WIZARD_NAME = "Deodorizer";
	private IWorkbenchWindow window;
	
	@Override
	public void run(IAction arg0) {
		RefactorPreview ref = new RefactorPreview();
		run(new RefactorDataWizzard(ref, WIZARD_NAME), window.getShell(), WIZARD_NAME);
		
	}
	
	
	public void run(RefactoringWizard wizard, Shell parent, String dialogTitle) {
		try {
			RefactoringWizardOpenOperation operation= new RefactoringWizardOpenOperation(wizard);
			operation.run(parent, dialogTitle);
		} catch (InterruptedException exception) {
			// Do nothing
		}
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		this.window = arg0;
		
	}

}
