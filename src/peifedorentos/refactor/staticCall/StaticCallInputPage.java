package peifedorentos.refactor.staticCall;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StaticCallInputPage extends UserInputWizardPage {

	public StaticCallInputPage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite arg0) {
		Composite result= new Composite(arg0, SWT.NONE);
		
		setControl(result);
		
		Combo comboAdapters = new Combo(result, SWT.NONE);
		comboAdapters.setBounds(177, 41, 209, 23);
		
		Label lbl = new Label(result, SWT.NONE);
		lbl.setBounds(93, 44, 78, 15);
		lbl.setText("Adapter name:");
		
		Button btnCreateNew = new Button(result, SWT.CHECK);
		btnCreateNew.setBounds(177, 82, 93, 16);
		btnCreateNew.setText("Create new");
		
	}

}
