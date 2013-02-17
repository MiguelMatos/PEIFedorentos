package peifedorentos.refactor.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MalformedTreeException;

import peifedorentos.refactor.Refactorer;
import peifedorentos.refactor.dependencyCreator.RefactorerDependencyCreation;
import peifedorentos.smells.ISmell;



public class RefactorPreview extends Refactoring {

	private ISmell smell;
	
	public RefactorPreview(ISmell smell) {
		this.smell = smell;
	}
	
	protected Change change;
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status= new RefactoringStatus();
		try {
			monitor.beginTask("Checking checkFinalConditions...", 2);
			
						
			Refactorer ref =  new RefactorerDependencyCreation(smell);
			change = ref.doRefactor(monitor);
			
			
			
						
		//	change=ref.doRefactor(monitor);
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		} finally {
			monitor.done();
		}
		return status;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
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
	public Change createChange(IProgressMonitor arg0) throws CoreException,
			OperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RefactoringService";
	}

}
