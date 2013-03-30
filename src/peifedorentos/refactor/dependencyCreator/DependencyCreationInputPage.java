package peifedorentos.refactor.dependencyCreator;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DependencyCreationInputPage extends UserInputWizardPage {

	private Text textNameFactory;
	private Combo comboFactories;
	private Button chkUpdateAllReferences;
	private Button chkCreateFactory;
	
	
	public DependencyCreationInputPage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	
	@Override
	public void createControl(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);

		setControl(result);

		Label lblNewFactoryName = new Label(parent, SWT.NONE);
		lblNewFactoryName.setBounds(10, 22, 115, 15);
		lblNewFactoryName.setText("New Factory Name:");
		
		Label lblExistingFactory = new Label(parent, SWT.NONE);
		lblExistingFactory.setBounds(10, 58, 115, 15);
		lblExistingFactory.setText("Existing Factory:");
		
		textNameFactory = new Text(parent, SWT.BORDER);
		textNameFactory.setBounds(131, 22, 273, 21);
		
		comboFactories = new Combo(parent, SWT.NONE);
		comboFactories.setBounds(131, 58, 273, 23);
		
		chkUpdateAllReferences = new Button(parent, SWT.CHECK);
		chkUpdateAllReferences.setBounds(131, 99, 178, 16);
		chkUpdateAllReferences.setText("Update all references");
		
		chkCreateFactory = new Button(parent, SWT.CHECK);
		chkCreateFactory.setBounds(131, 125, 93, 16);
		chkCreateFactory.setText("Create factory");
	}
	
	

}
