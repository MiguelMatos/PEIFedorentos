package peifedorentos.refactor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;

public class RefactorerDependencyCreation extends Refactorer {

	public RefactorerDependencyCreation(CompilationUnit compilationUnit) {
		super(compilationUnit);
		
	}

	@Override
	public Change doRefactor(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

}
