package peifedorentos.refactor.dependencyCreator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import peifedorentos.refactor.RefactorHelper;
import peifedorentos.refactor.structures.FactoryCreator;
import peifedorentos.smells.DependencyCreationSmell;
import peifedorentos.smells.ISmell;
import peifedorentos.smells.SmellTypes;
import peifedorentos.util.ActiveEditor;
import peifedorentos.visitors.ClassInstanceCreationVisitor;

public class DependencyCreationRefactoring extends Refactoring {

	private Map<ICompilationUnit, TextFileChange> changes= null;
	private DependencyCreationSmell smell;
	
	private String factoryTypeName = "Roda";
	private final String factoryVarName = "factory";
	private boolean updateAllReferences = true;
	private boolean newFactory = false;
	
	private ASTRewrite rewrite;
	private RefactorHelper helper;
	
	public DependencyCreationRefactoring(ISmell smell) {
		this.smell = (DependencyCreationSmell) smell;
		this.changes = new LinkedHashMap<ICompilationUnit, TextFileChange>();
		this.rewrite = ASTRewrite.create(this.smell.getCompilationUnit().getAST());
		
		this.helper = new RefactorHelper(smell.getCompilationUnit().getAST());
	
	}
	
	
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		// TODO Auto-generated method stub
		final RefactoringStatus status= new RefactoringStatus();
		monitor.beginTask("Checking preconditions...", 2);
		try {
			refactor();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally {
			monitor.done();
		}
		return status;
		
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		
//		if (!(smell instanceof DependencyCreationSmell)) //Não podemos continuar
//			return null;
		
		RefactoringStatus status= new RefactoringStatus();
		try {
			monitor.beginTask("Checking preconditions...", 1);
			//Check the name
		
		} finally {
			monitor.done();
		}
		return status;
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		try {
			monitor.beginTask("Creating change...", 1);
			final Collection<TextFileChange> localChanges= changes.values();
			CompositeChange change= new CompositeChange(getName(), localChanges.toArray(new Change[localChanges.size()])) {

				@Override
				public ChangeDescriptor getDescriptor() {
					return new RefactoringChangeDescriptor(new RefactoringDescriptor("Id", "Project", "Desc", "comp", 0) {
						
						@Override
						public Refactoring createRefactoring(RefactoringStatus arg0)
								throws CoreException {
							DependencyCreationRefactoring ref = new DependencyCreationRefactoring(smell);
							
							return ref;
						}
					});
				}
			};
			return change;
		} finally {
			monitor.done();
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "DependencyCreationRefactoring";
	}
	
	private void refactor() {
		
		
		if (newFactory)
		{
			ICompilationUnit factory = createFactory(smell.getClassDependencyName(), smell.getClassDependencyNamespace());
			changes.put(factory, null);
		}
		
		
		ASTNode node = smell.getNodeWithSmell();
		ImportRewrite importRewrite= ImportRewrite.create(smell.getCompilationUnit(), true);
		//Get method declation where the smell is
		//MethodDeclaration method = helper.GetMethodDeclarationParent(node);

		if (updateAllReferences)
			System.out.println("Update all references not implmented");
		
		
	
		
		MethodDeclaration method = helper.GetMethodDeclarationParent(node);
		MethodDeclaration newMethod = changeMethodDeclaration(method);
		rewrite.replace(method, newMethod, null);

		DependencyCreationVisitor visitor =new DependencyCreationVisitor(smell.getClassDependencyName(), rewrite, helper);

		newMethod.accept(visitor);
		
		VariableDeclarationFragment newVarDecFrag = changeVariableDeclarationFragment(node);
		rewrite.replace(helper.GetVariableDeclariationFragmentParent(node), newVarDecFrag, null);
		
	
		//rewriteAST(smell.getICompilationUnit(), rewrite, importRewrite);
		
		rewriteAST(smell.getICompilationUnit(), rewrite, importRewrite);
		
	}
	
	private ICompilationUnit createFactory(String objName, Name importName) {
		FactoryCreator fact = new FactoryCreator();
		try {
			return fact.CreateFactory("factories", objName + "Factory", objName, importName);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private MethodDeclaration changeMethodDeclaration(MethodDeclaration method)
	{
		MethodDeclaration newMethodDeclaration = helper.CreateMethodDeclaration(method);
		newMethodDeclaration.parameters().add(helper.CreateSingleVariableDeclaration(factoryVarName + factoryTypeName, factoryTypeName));
		return newMethodDeclaration;
		
	}
	
	private VariableDeclarationFragment changeVariableDeclarationFragment(ASTNode node) 
	{
		VariableDeclarationFragment oldVarDecFrag = helper.GetVariableDeclariationFragmentParent(node);
		VariableDeclarationFragment newVarDecFrag = helper.CreateVariableDeclarationFragment(oldVarDecFrag);
		MethodInvocation methodInv = helper.CreateMethodInvocation(factoryVarName + factoryTypeName, "CreateInstance");
		newVarDecFrag.setInitializer(methodInv);
		return newVarDecFrag;
	}
	
	private void rewriteAST(ICompilationUnit unit, ASTRewrite astRewrite, ImportRewrite importRewrite) {
		try {
			MultiTextEdit edit= new MultiTextEdit();
			TextEdit astEdit= astRewrite.rewriteAST();

			if (!isEmptyEdit(astEdit))
				edit.addChild(astEdit);
			TextEdit importEdit= importRewrite.rewriteImports(new NullProgressMonitor());
			if (!isEmptyEdit(importEdit))
				edit.addChild(importEdit);
			if (isEmptyEdit(edit))
				return;

			TextFileChange change= changes.get(unit);
			if (change == null) {
				change= new TextFileChange(unit.getElementName(), (IFile) unit.getResource());
				change.setTextType("java");
				change.setEdit(edit);
			} else
				change.getEdit().addChild(edit);

			changes.put(unit, change);
		} catch (MalformedTreeException exception) {
			System.out.println(exception.getMessage());
		} catch (IllegalArgumentException exception) {
			System.out.println(exception.getMessage());
		} catch (CoreException exception) {
			System.out.println(exception.getMessage());
		}
	}
	
	private boolean isEmptyEdit(TextEdit edit) {
		return edit.getClass() == MultiTextEdit.class && !edit.hasChildren();
	}
	
	private List<String> getProjectObjects() {
		ActiveEditor editor = new ActiveEditor();
			List<ICompilationUnit> cus = editor.getCompilationUnitsFromProject();
	
		ArrayList<String> projectClasses = new ArrayList<String>();
		for (ICompilationUnit cu : cus) {
			String element = cu.getElementName();
			
			
			projectClasses.add(element.substring(0, element.indexOf('.')));
		}	
		
		return projectClasses;
		
		
	}

}
