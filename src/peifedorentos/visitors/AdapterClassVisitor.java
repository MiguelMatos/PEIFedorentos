package peifedorentos.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class AdapterClassVisitor extends ASTVisitor {

	private List<String> adapter;
	private String typeName = "";
	
	public AdapterClassVisitor(List<String> adapters) {
		this.adapter = adapters;
	}
	
	public boolean visit(TypeDeclaration node) {
		typeName = node.getName().toString();
		return true;
	}
	
	public void endVisit(TypeDeclaration node) {
		typeName = "";
	}
	
	public boolean visit(MarkerAnnotation node) {
		
		if (node.getTypeName().toString().equals("Adapter"))
			adapter.add(typeName);
		return true;
	}

	public String getTypeName() {
		return typeName;
	}
	
}
