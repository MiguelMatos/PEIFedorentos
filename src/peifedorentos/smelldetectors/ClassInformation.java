package peifedorentos.smelldetectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ClassInformation {
	public ICompilationUnit iCompilationUnit;
	public CompilationUnit compilationUnit;
	
	public ClassInformation(ICompilationUnit iUnit, CompilationUnit unit) {
		this.iCompilationUnit = iUnit;
		this.compilationUnit = unit;
	}
}
