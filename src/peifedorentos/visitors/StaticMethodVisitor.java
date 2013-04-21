package peifedorentos.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class StaticMethodVisitor extends ASTVisitor {

	private ArrayList<IMethodBinding> staticMethods = new ArrayList<>();
	
	public List<IMethodBinding> getStaticMethods() {
		return staticMethods;
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
	
}
