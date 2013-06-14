package peifedorentos.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import peifedorentos.smelldetectors.ClassInformation;

public class StaticMethodVisitor extends ASTVisitor {

	private ArrayList<IMethodBinding> staticMethods = new ArrayList<>();
	private ClassInformation cu = null;
	
	public List<IMethodBinding> getStaticMethods() {
		return staticMethods;
	}
	
	public StaticMethodVisitor(ClassInformation cu)
	{
		this.setCompilationUnit(cu);
	}
	
	public boolean visit(MethodDeclaration node) {
	
		for (Object o : node.modifiers())
		{
			if (o.toString().equals("static"))
			{
				IMethodBinding method = (IMethodBinding) node.resolveBinding();
				staticMethods.add(method);
			}
		
		}
		
		
		return true;
		
	}

	public ClassInformation getCompilationUnit() {
		return cu;
	}

	private void setCompilationUnit(ClassInformation cu) {
		this.cu = cu;
	}
	
}
