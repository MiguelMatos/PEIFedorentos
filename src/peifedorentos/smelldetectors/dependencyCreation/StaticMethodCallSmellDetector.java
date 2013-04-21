package peifedorentos.smelldetectors.dependencyCreation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

import peifedorentos.refactor.RefactorHelper;
import peifedorentos.refactor.dependencyCreator.MethodCallerAddParameterVisitor;
import peifedorentos.smelldetectors.ClassInformation;
import peifedorentos.smelldetectors.ISmellDetector;
import peifedorentos.smells.ISmell;
import peifedorentos.smells.SmellTypes;
import peifedorentos.smells.StaticCallSmell;
import peifedorentos.visitors.ClassInstanceCreationVisitor;
import peifedorentos.visitors.StaticMethodVisitor;

//Get all static methods
//Search for calls in all compilation units


public class StaticMethodCallSmellDetector implements ISmellDetector {

	private boolean isWorking = false;
	private List<ISmell> detectedSmells = new ArrayList<ISmell>();
	private List<ClassInformation> units;
	private List<IMethodBinding> staticMethods = new ArrayList<IMethodBinding>();
			

	
	@Override
	public void run() {
	this.isWorking = true;
		
		//Get all static methods
		for (ClassInformation unit : units)
		{
			StaticMethodVisitor visitor = new StaticMethodVisitor();
			unit.compilationUnit.accept(visitor);
			staticMethods.addAll(visitor.getStaticMethods());
			
		}
		
		
		final Set<SearchMatch> invocations = new HashSet<SearchMatch>();
		
		for (IMethodBinding method : staticMethods)
		{
			IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
			SearchPattern pattern = SearchPattern.createPattern(
					method.getJavaElement(), IJavaSearchConstants.REFERENCES,
					SearchPattern.R_EXACT_MATCH);
			SearchEngine engine = new SearchEngine();
			try {
				engine.search(pattern, new SearchParticipant[] { SearchEngine
						.getDefaultSearchParticipant() }, scope,
						new SearchRequestor() {

							@Override
							public void acceptSearchMatch(SearchMatch match)
									throws CoreException {
								if (match.getAccuracy() == SearchMatch.A_ACCURATE
										&& !match.isInsideDocComment())
									
									invocations.add(match);
							}
						}, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		for (SearchMatch result : invocations) {
			
			Object element = result.getElement();
			if (element instanceof IMember) {
				
				ICompilationUnit unit = ((IMember) element).getCompilationUnit();
				//CompilationUnit cUnit = parse(unit);
				//int lineNumber = unit.getLineNumber(node.getStartPosition());
				
				StaticCallSmell smell = new StaticCallSmell(SmellTypes.StaticMethodCall,
						unit.findPrimaryType().getElementName(), 
						((IMember) element).getElementName(), 
						((IMember) element).getElementName(), 
						0, 
						null, 
						unit, 
						null);
				
				detectedSmells.add(smell);
			}
			
			
		}
		
		this.isWorking = false;
		
	}



	@Override
	public void snifCode(List<ClassInformation> unit) {
		this.units = unit;
		run();
		
	}

	@Override
	public List<ISmell> getSmells() {
		if (!isWorking)
			return detectedSmells;
		
		return null;
	}

	@Override
	public boolean isComplete() {
		return !isWorking;
	}
	
	

}
