package peifedorentos.smelldetectors;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import peifedorentos.smells.ISmell;

public interface ISmellDetector extends Runnable  {
	
	void snifCode(List<ClassInformation> unit);
	List<ISmell> getSmells();
	boolean isComplete();
}
