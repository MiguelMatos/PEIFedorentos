package peifedorentos.visitors;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

import peifedorentos.smells.ISmell;
import peifedorentos.smells.Smell;
import peifedorentos.smells.SmellTypes;

public class ClassInstanceCreationVisitor extends ASTVisitor {

	private CompilationUnit unit;
	private ArrayList<ISmell> detectedSmells;
	private List<String> projectObjects;
	private String currentMethod;
	private String className;
	private String fileName;
	
	public ClassInstanceCreationVisitor(CompilationUnit unit, String className, String fileName, List<String> projectObjects) {
		this.unit = unit;
		this.detectedSmells = new ArrayList<ISmell>();
		this.projectObjects = projectObjects;
		this.className = className;
		this.fileName = fileName;
	}
	

	
	@Override  
	public boolean visit(ClassInstanceCreation node) {
		
		//Detecta news
		//Excluir os que são new de classes do java
		
		Type type = node.getType();
		SimpleType simpleType = getSimpleType(type);
		
		String st = simpleType.getName().getFullyQualifiedName();
		
		if (this.projectObjects.contains(st)) {
			int lineNumber = this.unit.getLineNumber(node.getStartPosition());
			ISmell smell = new Smell(SmellTypes.InstanceCreation, this.className, 
					this.currentMethod, this.fileName, lineNumber, unit);
			this.detectedSmells.add(smell);
		}
		
		return true;
		
	}
	@Override
	public boolean visit(MethodDeclaration node) {
		this.currentMethod = node.getName().getFullyQualifiedName();
		return true;
	}
	
	public void endVisit(MethodDeclaration node) {
		this.currentMethod = "";
	}
	
	
	
	private SimpleType getSimpleType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType)type;
			return getSimpleType(paramType.getType());
		}
		else if (type instanceof SimpleType) {
			return (SimpleType)type;
		}
		else
		{
			return null;
		}
	}
	
	public List<ISmell> getDetectedSmells() {
		return this.detectedSmells;
	}
	
}
