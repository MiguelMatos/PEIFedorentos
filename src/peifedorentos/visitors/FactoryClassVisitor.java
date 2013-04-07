package peifedorentos.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;

public class FactoryClassVisitor extends ASTVisitor {

	private List<String> factories;
	
	public FactoryClassVisitor(List<String> factories)
	{
		this.factories = factories;
	}
	
	public boolean visit(MarkerAnnotation node) {
		
		if (node.getTypeName().toString().equals("Factory"))
			factories.add(node.getTypeName().toString());
		return true;
	}
	
}
