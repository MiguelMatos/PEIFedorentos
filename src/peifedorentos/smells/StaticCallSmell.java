package peifedorentos.smells;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class StaticCallSmell extends Smell {

	private CompilationUnit staticClassUnit;
	private String staticMethodName;
	
	
	public StaticCallSmell(SmellTypes smellType, String className,
			String methodName, String fileName, int line, CompilationUnit cu,
			ICompilationUnit icu, ASTNode node, CompilationUnit staticClassUnit, String staticMethodName) {
		super(smellType, className, methodName, fileName, line, cu, icu, node);
		// TODO Auto-generated constructor stub
		this.setStaticClassUnit(staticClassUnit);
		this.staticMethodName = staticMethodName;
	}


	public CompilationUnit getStaticClassUnit() {
		return staticClassUnit;
	}


	public void setStaticClassUnit(CompilationUnit staticClassUnit) {
		this.staticClassUnit = staticClassUnit;
	}
	
	public String getStaticMethodName() {
		return staticMethodName;
	}

}
