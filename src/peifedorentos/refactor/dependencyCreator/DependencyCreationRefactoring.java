package peifedorentos.refactor.dependencyCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import peifedorentos.interfaces.Factory;
import peifedorentos.refactor.RefactorHelper;
import peifedorentos.refactor.structures.FactoryCreator;
import peifedorentos.smells.DependencyCreationSmell;
import peifedorentos.smells.ISmell;
import peifedorentos.util.ActiveEditor;

public class DependencyCreationRefactoring extends Refactoring {

	private Map<ICompilationUnit, TextFileChange> changes = null;
	private DependencyCreationSmell smell;
	private DependencyCreationRefactoring instance;

	private String factoryTypeName = "Roda11";
	private final String factoryVarName = "factory";
	private boolean updateAllReferences = false;
	private boolean newFactory = false;
	private ASTRewrite rewrite;
	private RefactorHelper helper;

	public DependencyCreationRefactoring(ISmell smell) {
		this.smell = (DependencyCreationSmell) smell;
		this.changes = new LinkedHashMap<ICompilationUnit, TextFileChange>();
		this.rewrite = ASTRewrite.create(this.smell.getCompilationUnit()
				.getAST());

		this.helper = new RefactorHelper(smell.getCompilationUnit().getAST());
		this.instance = this;
	}
	
	

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		// TODO Auto-generated method stub
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

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {

		// if (!(smell instanceof DependencyCreationSmell)) //Não podemos
		// continuar
		// return null;

		RefactoringStatus status = new RefactoringStatus();
		try {
			monitor.beginTask("Checking preconditions...", 1);
			// Check the name

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
									DependencyCreationRefactoring ref = null;
						
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
		// TODO Auto-generated method stub
		return "DependencyCreationRefactoring";
	}

	private void refactor(IProgressMonitor monitor) {

		if (isNewFactory()) {
			IJavaElement el = smell.getICompilationUnit().getAncestor(
					IJavaElement.PACKAGE_FRAGMENT_ROOT);

	
			
		ICompilationUnit factory = createFactory(
						smell.getClassDependencyName(),
						smell.getClassDependencyNamespace(), factoryTypeName, ((IPackageFragmentRoot) el));


		}

		ASTNode node = smell.getNodeWithSmell();
		
		ImportRewrite importRewrite = ImportRewrite.create(
				smell.getCompilationUnit(), true);
		
		importRewrite.addImport("factories." + factoryTypeName);
		
		// Get method declation where the smell is
		// MethodDeclaration method = helper.GetMethodDeclarationParent(node);
		MethodDeclaration method = helper.GetMethodDeclarationParent(node);
		final Set<SearchMatch> invocations = new HashSet<SearchMatch>();
		if (isUpdateAllReferences()) {
			updateReferences(monitor, method, invocations);

		}

		
	    
	    
	    
		MethodDeclaration newMethod = changeMethodDeclaration(method);
		rewrite.replace(method, newMethod, null);

		DependencyCreationVisitor visitor = new DependencyCreationVisitor(
				smell.getClassDependencyName(), rewrite, helper, "factory" + factoryTypeName);

		newMethod.accept(visitor);

		VariableDeclarationFragment newVarDecFrag = changeVariableDeclarationFragment(node);
		rewrite.replace(helper.GetVariableDeclariationFragmentParent(node),
				newVarDecFrag, null);

		// rewriteAST(smell.getICompilationUnit(), rewrite, importRewrite);

		rewriteAST(smell.getICompilationUnit(), rewrite, importRewrite);

	}

	private void updateReferences(IProgressMonitor monitor,
			MethodDeclaration method, final Set<SearchMatch> invocations) {
		IMethodBinding mbind = method.resolveBinding();

		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		SearchPattern pattern = SearchPattern.createPattern(
				mbind.getJavaElement(), IJavaSearchConstants.REFERENCES,
				SearchPattern.R_EXACT_MATCH);
		SearchEngine engine = new SearchEngine();
		try {
			engine.search(pattern, new SearchParticipant[] { SearchEngine
					.getDefaultSearchParticipant() }, scope,
					new SearchRequestor() {

						@Override
						public void acceptSearchMatch(SearchMatch match)
								throws CoreException {
							if (match.getAccuracy() == SearchMatch.A_ACCURATE
									&& !match.isInsideDocComment())
								
								invocations.add(match);
						}
					}, new SubProgressMonitor(monitor, 1,
							SubProgressMonitor.SUPPRESS_SUBTASK_LABEL));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			for (SearchMatch match : invocations) {
				Object element = match.getElement();
				if (element instanceof IMember) {
					
					ICompilationUnit unit = ((IMember) element).getCompilationUnit();
					CompilationUnit cUnit = parse(unit);
					ASTRewrite astRw = ASTRewrite.create(cUnit.getAST());
					RefactorHelper refHelper = new RefactorHelper(cUnit.getAST());
					
					
					ImportRewrite importRewrite = ImportRewrite.create(
							cUnit, true);
					
					importRewrite.addImport("factories." + factoryTypeName);
					
							MethodCallerAddParameterVisitor visitor = new MethodCallerAddParameterVisitor(
									method.getName().toString(), factoryTypeName, astRw, refHelper);
							
					cUnit.accept(visitor);
					rewriteAST(unit, astRw, importRewrite);
				}
			}
	}

	private ICompilationUnit createFactory(String objName, Name importName, String factoryName,
			IPackageFragmentRoot project) {
		FactoryCreator fact = new FactoryCreator();
		try {
			return fact.CreateFactory("factories", factoryName,
					objName, importName, project);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private MethodDeclaration changeMethodDeclaration(MethodDeclaration method) {
		MethodDeclaration newMethodDeclaration = helper
				.CreateMethodDeclaration(method);
		newMethodDeclaration.parameters().add(
				helper.CreateSingleVariableDeclaration(factoryVarName
						+ getFactoryTypeName(), getFactoryTypeName()));
		return newMethodDeclaration;

	}

	private VariableDeclarationFragment changeVariableDeclarationFragment(
			ASTNode node) {
		VariableDeclarationFragment oldVarDecFrag = helper
				.GetVariableDeclariationFragmentParent(node);
		VariableDeclarationFragment newVarDecFrag = helper
				.CreateVariableDeclarationFragment(oldVarDecFrag);
		MethodInvocation methodInv = helper.CreateMethodInvocation(
				factoryVarName + getFactoryTypeName(), "CreateInstance");
		newVarDecFrag.setInitializer(methodInv);
		return newVarDecFrag;
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
	
private CompilationUnit parse(ICompilationUnit unit) {
		
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return ((CompilationUnit) parser.createAST(null));
		
		
	}

	public RefactoringStatus setFactoryName(String name) {
		setFactoryTypeName(name);
		RefactoringStatus status = new RefactoringStatus();
		return status;
	}
	
	public RefactoringStatus changeUpdateRefFlag(boolean flag) {
		setUpdateAllReferences(flag);
		RefactoringStatus status = new RefactoringStatus();
		return status;
	}
	
	public RefactoringStatus isNewFactory(boolean flag) {
		newFactory = flag;
		RefactoringStatus status = new RefactoringStatus();
		return status;
	}

	public String getFactoryTypeName() {
		return factoryTypeName;
	}

	public RefactoringStatus setFactoryTypeName(String factoryTypeName) {
		this.factoryTypeName = factoryTypeName;
		return new RefactoringStatus();
	}

	public boolean isUpdateAllReferences() {
		return updateAllReferences;
	}

	public RefactoringStatus setUpdateAllReferences(boolean updateAllReferences) {
		this.updateAllReferences = updateAllReferences;
		return new RefactoringStatus();
	}

	public boolean isNewFactory() {
		return newFactory;
	}

	public RefactoringStatus setNewFactory(boolean newFactory) {
		this.newFactory = newFactory;
		return new RefactoringStatus();
	}

}
