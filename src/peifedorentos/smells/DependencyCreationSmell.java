package peifedorentos.smells;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;

public class DependencyCreationSmell extends Smell {

	private String classDependencyName;
	private Name classDependencyNamespace;
	
	public DependencyCreationSmell(SmellTypes smellType, String className,
			String methodName, String fileName, String classDependencyName, Name namespace,
			int line, CompilationUnit cu, ICompilationUnit iCompilationUnit, ASTNode nodeWithSmell) {
		super(smellType, className, methodName, fileName, line, cu, iCompilationUnit, nodeWithSmell);

		this.classDependencyName = classDependencyName;
		this.classDependencyNamespace = namespace;
		
	}
	
	public String getClassDependencyName() {
		return this.classDependencyName;
	}
	
	public Name getClassDependencyNamespace() {
		return this.classDependencyNamespace;
	}
	

}
