
package peifedorentos.smelldetectors.dependencyCreation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

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



public class StaticMethodCallSmellDetector implements ISmellDetector {

	private boolean isWorking = false;
	private List<ISmell> detectedSmells = new ArrayList<ISmell>();
	private List<ClassInformation> units;
	private List<MethodInformation> staticMethods = new ArrayList<MethodInformation>();
	
	private class SearchInformation {
		SearchMatch match;
		ClassInformation staticInformation;
		
		public SearchInformation(SearchMatch sm, ClassInformation ci) {
			this.match = sm;
			this.staticInformation = ci;
		}
	}
	
	private class MethodInformation {
		IMethodBinding methodBinding;
		ClassInformation staticInformation;
		
		public MethodInformation(IMethodBinding methodBinding, ClassInformation staticInformation) {
			this.methodBinding = methodBinding;
			this.staticInformation = staticInformation;
		}
	}

	
	@Override
	public void run() {
	this.isWorking = true;
		
		//Get all static methods
		for (ClassInformation unit : units)
		{
			StaticMethodVisitor visitor = new StaticMethodVisitor(unit);
			unit.compilationUnit.accept(visitor);
			
			for(IMethodBinding b : visitor.getStaticMethods())
				staticMethods.add(new MethodInformation(b, unit));
			
			
		}
		
		
		final Set<SearchInformation> invocations = new HashSet<SearchInformation>();
		
		for (final MethodInformation method : staticMethods)
		{
			IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
			SearchPattern pattern = SearchPattern.createPattern(
					method.methodBinding.getJavaElement(), IJavaSearchConstants.REFERENCES,
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
									
									invocations.add(new SearchInformation(match, method.staticInformation));
							}
						}, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		for (SearchInformation result : invocations) {
			
			
		
			
			
			Object element = result.match.getElement();
			
			
			if (element instanceof IMember) {
				IMember member = (IMember) element;
				ICompilationUnit unit = member.getCompilationUnit();
				CompilationUnit cUnit = ASTUtils.parse(unit);
				//ISourceRange range = null;
				try {
					//range = result..getSourceRange();
					System.out.println(result.match.getOffset());
					
					
					IBuffer buf = null;
					
					buf = unit.getBuffer();
					final int start = result.match.getOffset();
					String contents = buf.getContents();
					Document doc = new Document(contents);
					
					int line = doc.getLineOfOffset(start);
					System.out.println("- " + line);
					
				
			
				ASTNode nodeWithSmell = getNodeWithSmell(result.match, cUnit);
				
				//int lineNumber = unit.getLineNumber(node.getStartPosition());
				if (isSmell(nodeWithSmell)) {
				StaticCallSmell smell = new StaticCallSmell(SmellTypes.StaticMethodCall,
						unit.findPrimaryType().getElementName(), 
						member.getElementName(), 
						member.getElementName(), 
						line-1, 
						cUnit, 
						unit, 
						nodeWithSmell, result.staticInformation.compilationUnit);
				
				detectedSmells.add(smell);
				
				
				
				}
				
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
		this.isWorking = false;
		
	}


	private ASTNode getNodeWithSmell(SearchMatch member, CompilationUnit cu) {
		ASTNode node =  null;
		node = NodeFinder.perform(cu, member.getOffset(), 0);//member.getSourceRange().getLength());
	
		return node;
	}
	
	private boolean isSmell(ASTNode node) {
		System.out.println("New smell check!");
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
		System.out.println(node.getClass().toString());
		if (node instanceof Assignment)
		{
			return true;
		}
		else
		{
			if (node.getParent() != null)
				return findParentAssignment(node.getParent());
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

