package peifedorentos.smelldetectors;

import java.util.ArrayList;
import org.eclipse.jdt.core.dom.CompilationUnit;
import peifedorentos.ISmell;

public interface ISmellDetector extends Runnable  {
	
	void snifCode(CompilationUnit unit);
	ArrayList<ISmell> getSmells();
	boolean isComplete();
}
