package peifedorentos.refactor.dependencyCreator;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import peifedorentos.smelldetectors.ClassInformation;
import peifedorentos.util.ActiveEditor;
import peifedorentos.visitors.FactoryClassVisitor;

public class DependencyCreationInputPage extends UserInputWizardPage {

	private Text textNameFactory;
	private Combo comboFactories;
	private Button chkUpdateAllReferences;
	private Button chkCreateFactory;
	
	private ArrayList<String> factories;
	
	
	
	public DependencyCreationInputPage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		this.factories = new ArrayList<String>();
		
		ActiveEditor editor = new ActiveEditor();
		List<ICompilationUnit> cus = editor.getCompilationUnitsFromProject();
		
		for (ICompilationUnit iCompilationUnit : cus) {
			FactoryClassVisitor f = new FactoryClassVisitor(factories);
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(iCompilationUnit);
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			cu.accept(f);
			
		}
		
		
		
		
	}


	
	@Override
	public void createControl(Composite composite) {
		Composite result= new Composite(composite, SWT.NONE);
		
		setControl(result);
		
		Label lblNewFactoryName = new Label(result, SWT.NONE);
		lblNewFactoryName.setBounds(10, 22, 115, 15);
		lblNewFactoryName.setText("New Factory Name:");
		
		Label lblExistingFactory = new Label(result, SWT.NONE);
		lblExistingFactory.setBounds(10, 58, 115, 15);
		lblExistingFactory.setText("Existing Factory:");
		
		textNameFactory = new Text(result, SWT.BORDER);
		
		final DependencyCreationRefactoring refactoring = getDepRefactoring();
		
		textNameFactory.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				handleInputChanged();
				
			}
		});
		textNameFactory.setBounds(131, 22, 273, 21);
		
		 comboFactories = new Combo(result, SWT.NONE);
		 
		 for (String s : factories)
			{
			 comboFactories.add(s);
			}
		 
		comboFactories.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				handleInputChanged();
			}
		});
	
		comboFactories.setBounds(131, 58, 273, 23);
		
		chkUpdateAllReferences = new Button(result, SWT.CHECK);
		chkUpdateAllReferences.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refactoring.setUpdateAllReferences(chkUpdateAllReferences.getSelection());
			}
		});
		chkUpdateAllReferences.setBounds(131, 99, 178, 16);
		chkUpdateAllReferences.setText("Update all references");
		
		
		chkCreateFactory = new Button(result, SWT.CHECK);
		chkCreateFactory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refactoring.setNewFactory(chkCreateFactory.getSelection());
				
			}
		});
		chkCreateFactory.setBounds(131, 125, 93, 16);
		chkCreateFactory.setText("Create factory");
	}
	
	private DependencyCreationRefactoring getDepRefactoring() {
		return (DependencyCreationRefactoring) getRefactoring();
	}
	
	void handleInputChanged() {
		RefactoringStatus status= new RefactoringStatus();
		DependencyCreationRefactoring refactoring= getDepRefactoring();
		
		if (refactoring.isNewFactory()) {
			status.merge(refactoring.setFactoryTypeName(textNameFactory.getText()));
		}
		else
		{
			status.merge(refactoring.setFactoryName(comboFactories.getText()));
		}

		setPageComplete(!status.hasError());
		int severity= status.getSeverity();
		String message= status.getMessageMatchingSeverity(severity);
		if (severity >= RefactoringStatus.INFO) {
			setMessage(message, severity);
		} else {
			setMessage("", NONE); //$NON-NLS-1$
		}
	}
	
	

}
