package peifedorentos.smells;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class StaticCallSmell extends Smell {

	public StaticCallSmell(SmellTypes smellType, String className,
			String methodName, String fileName, int line, CompilationUnit cu,
			ICompilationUnit icu, ASTNode node) {
		super(smellType, className, methodName, fileName, line, cu, icu, node);
		// TODO Auto-generated constructor stub
	}

}
