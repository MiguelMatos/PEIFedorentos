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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceMethod;

import peifedorentos.refactor.RefactorHelper;
import peifedorentos.refactor.dependencyCreator.MethodCallerAddParameterVisitor;
import peifedorentos.smelldetectors.ClassInformation;
import peifedorentos.smelldetectors.ISmellDetector;
import peifedorentos.smells.ISmell;
import peifedorentos.smells.SmellTypes;
import peifedorentos.smells.StaticCallSmell;
import peifedorentos.util.ASTUtils;
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
				IMember member = (IMember) element;
				
				ASTNode nodeWithSmell = getNodeWithSmell(member);
				ICompilationUnit unit = member.getCompilationUnit();
				//CompilationUnit cUnit = parse(unit);
				//int lineNumber = unit.getLineNumber(node.getStartPosition());
				if (isSmell(nodeWithSmell)) {
				StaticCallSmell smell = new StaticCallSmell(SmellTypes.StaticMethodCall,
						unit.findPrimaryType().getElementName(), 
						member.getElementName(), 
						member.getElementName(), 
						0, 
						null, 
						unit, 
						nodeWithSmell);
				
				detectedSmells.add(smell);
				}
			}
			
			
		}
		
		this.isWorking = false;
		
	}


	private ASTNode getNodeWithSmell(IMember member) {
		// TODO Auto-generated method stub
		ICompilationUnit unit = member.getCompilationUnit();
		CompilationUnit cu = ASTUtils.parse(unit);
		ASTNode node = cu.findDeclaringNode(((ResolvedSourceMethod)member).getKey());

		return node;
	}
	
	private boolean isSmell(ASTNode node) {
		
		if (findParentVariableDeclaration(node))
			return true;
		
		if (findParentAssignment(node))
			return true;
		
		return false;
	}
	
	private boolean findParentVariableDeclaration(ASTNode node) {
		if (node instanceof VariableDeclaration)
		{
			return true;
		}
		else
		{
			if (node.getParent() != null)
				return findParentVariableDeclaration(node.getParent());
			else
				return false;
			
			
		}
	}
	
	private boolean findParentAssignment(ASTNode node) {
		if (node instanceof Assignment)
		{
			return true;
		}
		else
		{
			if (node.getParent() != null)
				return findParentVariableDeclaration(node.getParent());
			else
				return false;
			
			
		}
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
