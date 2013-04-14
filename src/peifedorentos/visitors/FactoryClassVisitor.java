package peifedorentos.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class FactoryClassVisitor extends ASTVisitor {

	private List<String> factories;
	private String typeName = "";
	
	
	public FactoryClassVisitor(List<String> factories)
	{
		this.factories = factories;
	}
	
	public boolean visit(TypeDeclaration node) {
		typeName = node.getName().toString();
		return true;
	}
	
	public void endVisit(TypeDeclaration node) {
		typeName = "";
	}
	
	public boolean visit(MarkerAnnotation node) {
		
		if (node.getTypeName().toString().equals("Factory"))
			factories.add(typeName);
		return true;
	}

	public String getTypeName() {
		return typeName;
	}


	
}
