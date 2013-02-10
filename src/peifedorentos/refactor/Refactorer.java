package peifedorentos.refactor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;

public abstract class Refactorer {

	private CompilationUnit compilationUnit;
	
	public Refactorer(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}
	
	public abstract Change doRefactor(IProgressMonitor monitor);
}
