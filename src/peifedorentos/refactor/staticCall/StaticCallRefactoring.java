package peifedorentos.refactor.staticCall;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;

import peifedorentos.refactor.RefactorHelper;
import peifedorentos.refactor.dependencyCreator.DependencyCreationRefactoring;
import peifedorentos.smells.DependencyCreationSmell;
import peifedorentos.smells.ISmell;
import peifedorentos.smells.StaticCallSmell;

public class StaticCallRefactoring extends Refactoring {

	private Map<ICompilationUnit, TextFileChange> changes = null;
	private StaticCallSmell smell;
	private StaticCallRefactoring instance;
	private RefactorHelper helper;
	
	public StaticCallRefactoring(ISmell smell) {
		this.smell = (StaticCallSmell) smell;
		this.changes = new LinkedHashMap<ICompilationUnit, TextFileChange>();
		this.instance = this;
		this.helper = new RefactorHelper(smell.getCompilationUnit().getAST());
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

}
