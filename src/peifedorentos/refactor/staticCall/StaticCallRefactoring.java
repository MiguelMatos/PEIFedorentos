package peifedorentos.refactor.staticCall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
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
import peifedorentos.smells.ISmell;
import peifedorentos.smells.StaticCallSmell;

public class StaticCallRefactoring extends Refactoring {

	private Map<ICompilationUnit, TextFileChange> changes = null;
	private StaticCallSmell smell;
	private StaticCallRefactoring instance;
	private RefactorHelper helper;
	private static int varCount = 1;
	private ASTRewrite rewrite;
	
	private boolean multipleCalls = false;
	
	
	public StaticCallRefactoring(ISmell smell) {
		this.smell = (StaticCallSmell) smell;
		this.changes = new LinkedHashMap<ICompilationUnit, TextFileChange>();
		this.instance = this;
		this.helper = new RefactorHelper(smell.getCompilationUnit().getAST());
		this.rewrite = ASTRewrite.create(this.smell.getCompilationUnit()
				.getAST());
	}
	
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		final RefactoringStatus status = new RefactoringStatus();
		monitor.beginTask("Checking preconditions...", 2);
		try {
			refactor(monitor);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			monitor.done();
		}
		return status;
	}
	private void refactor(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		ASTNode node = smell.getNodeWithSmell();
		
		ImportRewrite importRewrite = ImportRewrite.create(
				smell.getCompilationUnit(), true);
		
		//Primeiro encontrar o pai seja ele i = static ou int i = static
		//Segundo ver o o tipo de i
		//Terceiro adicionar um parametro ao método do tipo i
		//Quarto substituir a chamada ao static pelo parametro
		//Quinto corrigir todas as chamadas para passarem a chamar o static na chamada
		VariableDeclaration dec = findParentVariableDeclaration(node);
		Assignment assig = findParentAssignment(node);
		Type typeName = null; 
		
		
		if (dec == null) {
			if (assig == null)
			{
				return;
			}
			else
			{
				typeName = ((VariableDeclarationStatement)dec.getParent()).getType();
				addParameterToMethod(node, typeName);
				processAssignment(assig);
			}
		}
		else
		{
			typeName = ((VariableDeclarationStatement)dec.getParent()).getType();
			processVariableDeclaration(dec);
			addParameterToMethod(dec, typeName);
			
		}
		
		
		updateAllReferences(node);
		
		rewriteAST(smell.getICompilationUnit(), rewrite, importRewrite);

		
	}

	private void processAssignment(Assignment node) {
		// i = static;
		MethodInvocation dec = findMethodInvocation(node.getRightHandSide());
		varCount++;
		SimpleName s = helper.CreateSimpleName("var" + varCount);
		
		rewrite.replace(dec, s, null);
		
	}

	private MethodInvocation findMethodInvocation(Expression exp) {
		
		if (exp instanceof MethodInvocation) {
			return (MethodInvocation) exp;
		}
		
		return null;
	}

	private void processVariableDeclaration(VariableDeclaration node) {
		// int i = static;
		MethodInvocation dec = findMethodInvocation(node.getInitializer());
		varCount++;
		SimpleName s = helper.CreateSimpleName("var" + varCount);
		
		rewrite.replace(dec, s, null);
	
	}

	private void addParameterToMethod(ASTNode node, Type parameterType) {
		// TODO Auto-generated method stub
		String varname = "var" + varCount;
		
		MethodDeclaration md = findParentMethodDeclaration(node);
		MethodDeclaration copyMd = helper.CreateMethodDeclaration(md);
	
	
		
		SingleVariableDeclaration param = helper.CreateSingleVariableDeclaration(varname, parameterType);
		//copyMd.parameters().add(param);

		List params = new ArrayList();
		
		
		
		if (copyMd.parameters() != null)
		{
			params.addAll(copyMd.parameters());
		}
		
		
		params.add(param);
		
		copyMd.parameters().clear();
		copyMd.parameters().addAll(params);

		rewrite.replace(md, copyMd, null);
		//return copyMd;
		
		
	}

	private MethodDeclaration findParentMethodDeclaration(ASTNode node) {
		if (node instanceof MethodDeclaration)
			return (MethodDeclaration) node;
		else if (node.getParent() != null)
			return findParentMethodDeclaration(node.getParent());
		else
			return null;
	}

	private void updateAllReferences(ASTNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		try {
			monitor.beginTask("Checking preconditions...", 1);
		} finally {
			monitor.done();
		}
		return status;
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		try {
			monitor.beginTask("Creating change...", 1);
			final Collection<TextFileChange> localChanges = changes.values();
			CompositeChange change = new CompositeChange(getName(),
					localChanges.toArray(new Change[localChanges.size()])) {

				@Override
				public ChangeDescriptor getDescriptor() {
					return new RefactoringChangeDescriptor(
							new RefactoringDescriptor("Id", "Project", "Desc",
									"comp", 0) {

								@Override
								public Refactoring createRefactoring(
										RefactoringStatus arg0)
										throws CoreException {
									
									StaticCallRefactoring ref = null;
									ref = instance;
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
		return "StaticCallRefactoring";
	}
	
	
	private VariableDeclaration findParentVariableDeclaration(ASTNode node) {
		
		if (node instanceof VariableDeclaration)
		{
			return (VariableDeclaration) node;
		}
		else
		{
			if (node.getParent() != null)
				return findParentVariableDeclaration(node.getParent());
			else
				return null;
			
			
		}
	}
	
	private Assignment findParentAssignment(ASTNode node) {
		System.out.println(node.getClass().toString());
		if (node instanceof Assignment)
		{
			return (Assignment) node;
		}
		else
		{
			if (node.getParent() != null)
				return findParentAssignment(node.getParent());
			else
				return null;
			
			
		}
	}
	
	private void rewriteAST(ICompilationUnit unit, ASTRewrite astRewrite,
			ImportRewrite importRewrite) {
		try {
			MultiTextEdit edit = new MultiTextEdit();
			TextEdit astEdit = astRewrite.rewriteAST();

			if (!isEmptyEdit(astEdit))
				edit.addChild(astEdit);
			TextEdit importEdit = importRewrite
					.rewriteImports(new NullProgressMonitor());
			if (!isEmptyEdit(importEdit))
				edit.addChild(importEdit);
			if (isEmptyEdit(edit))
				return;

			TextFileChange change = changes.get(unit);
			// if (change instanceof TextFileChange || change == null) {
			// TextFileChange tch;
			if (change == null) {
				change = new TextFileChange(unit.getElementName(),
						(IFile) unit.getResource());
				change.setTextType("java");
				change.setEdit(edit);
			} else
				((TextFileChange) change).getEdit().addChild(edit);

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

	private boolean isMultipleCalls() {
		return multipleCalls;
	}

	private void setMultipleCalls(boolean multipleCalls) {
		this.multipleCalls = multipleCalls;
	}


}
