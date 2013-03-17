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

	Combo factoriesAvailable;
	Text newFactoryName;
	
	public DependencyCreationInputPage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);

		setControl(result);

		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);

		Label label= new Label(result, SWT.NONE);
		label.setText("&Factory name:");
		
		newFactoryName = createNameField(result);
		
		label= new Label(result, SWT.NONE);
		label.setText("&Declaring class:");

		Composite composite= new Composite(result, SWT.NONE);
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		factoriesAvailable= createTypeCombo(composite);
		factoriesAvailable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridData data= new GridData();
		data.horizontalAlignment= GridData.END;
		final Button createNewFactory= new Button(result, SWT.CHECK);
		createNewFactory.setText("&Create new Factory");
		data= new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan= 2;
		data.verticalIndent= 2;
		createNewFactory.setLayoutData(data);
		
		
		createNewFactory.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				//Selection changed in create new factory check box
			}
		});
		
		newFactoryName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				inputChanged();
			}
		});
		
		
		factoriesAvailable.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				inputChanged();
			}
		});
	}
	
	public void inputChanged() {
		
	}
	
	
	private Text createNameField(Composite result) {
		Text field= new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return field;
	}

	private Combo createTypeCombo(Composite composite) {
		Combo combo= new Combo(composite, SWT.SINGLE | SWT.BORDER);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setVisibleItemCount(4);
		return combo;
	}
	
	private String[] getComboItems() {
		return null;
	}

}
