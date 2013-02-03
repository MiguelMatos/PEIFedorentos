package peifedorentos.smells;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

public interface ISmell {

	public String getClassName();

	public String getMethodName();

	public String getFileName();

	public int getLineNumber();

	public List<ISmell> getRelatedSmells();
	
	public void addRelatedSmell(ISmell smell);
	
	public SmellTypes getSmellType();
	
	public CompilationUnit getCompilationUnit();

}