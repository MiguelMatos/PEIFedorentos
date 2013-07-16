package peifedorentos.refactor.staticCall;

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

import peifedorentos.util.ActiveEditor;
import peifedorentos.visitors.AdapterClassVisitor;

public class StaticCallInputPage extends UserInputWizardPage {

	ArrayList<String> adapters; 
	
	public StaticCallInputPage(String name) {
		super(name);

		// TODO Auto-generated constructor stub
		this.adapters = new ArrayList<String>();

		ActiveEditor editor = new ActiveEditor();
		List<ICompilationUnit> cus = editor.getCompilationUnitsFromProject();

		for (ICompilationUnit iCompilationUnit : cus) {
			AdapterClassVisitor f = new AdapterClassVisitor(adapters);
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(iCompilationUnit);
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			cu.accept(f);

		}
	}
	
	private StaticCallRefactoring getStatRefactoring() {
		return (StaticCallRefactoring) getRefactoring();
	}

	@Override
	public void createControl(Composite arg0) {
		
		Composite result= new Composite(arg0, SWT.NONE);
		
		setControl(result);
		final Button btnCreateNew = new Button(result, SWT.CHECK);
		final Combo comboAdapters = new Combo(result, SWT.NONE);
		
		
		for (String s : adapters) {
			comboAdapters.add(s);
		}
		
		comboAdapters.setBounds(177, 41, 209, 23);
		comboAdapters.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				handleInputChanged(btnCreateNew.getSelection(), comboAdapters.getText());
			}
		});
		
		Label lbl = new Label(result, SWT.NONE);
		lbl.setBounds(93, 44, 78, 15);
		lbl.setText("Adapter name:");
		
		
		btnCreateNew.setBounds(177, 82, 93, 16);
		btnCreateNew.setText("Create new");
		btnCreateNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleInputChanged(btnCreateNew.getSelection(), comboAdapters.getText());
			}
		});
		
	}
	
	void handleInputChanged(boolean selection, String adapterName) {
		RefactoringStatus status= new RefactoringStatus();
		StaticCallRefactoring refactoring= getStatRefactoring();	
		status.merge(refactoring.setNewAdapter(selection));
		status.merge(refactoring.setAdapterName(adapterName));
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
