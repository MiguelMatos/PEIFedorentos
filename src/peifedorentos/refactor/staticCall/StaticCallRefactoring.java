package peifedorentos.refactor.staticCall;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;

import peifedorentos.refactor.dependencyCreator.DependencyCreationRefactoring;
import peifedorentos.smells.DependencyCreationSmell;
import peifedorentos.smells.ISmell;

public class StaticCallRefactoring extends Refactoring {

	private Map<ICompilationUnit, TextFileChange> changes = null;
	private DependencyCreationSmell smell;
	private DependencyCreationRefactoring instance;
	
	public StaticCallRefactoring(ISmell smell) {
		this.smell = (DependencyCreationSmell) smell;
	}
	
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor arg0)
			throws CoreException, OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor arg0)
			throws CoreException, OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor arg0) throws CoreException,
			OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
