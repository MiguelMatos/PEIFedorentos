package peifedorentos.refactor.staticCall;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import peifedorentos.util.ActiveEditor;
import peifedorentos.visitors.AdapterClassVisitor;
import peifedorentos.visitors.FactoryClassVisitor;

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
