package peifedorentos.visitors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;

import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import peifedorentos.smelldetectors.ClassInformation;
import peifedorentos.smells.DependencyCreationSmell;
import peifedorentos.smells.SmellTypes;

public class ClassInstanceCreationVisitor extends ASTVisitor {

	private ArrayList<DependencyCreationSmell> detectedSmells;
	private List<String> projectObjects;
	private String currentMethod;
	private Map<String, Name> imports;
	private Name packageDec;
	private ClassInformation classInformation;
	
	public ClassInstanceCreationVisitor(ClassInformation classInformation, List<String> projectObjects) {
		this.classInformation =classInformation;
		this.projectObjects = projectObjects;
		this.detectedSmells = new ArrayList<DependencyCreationSmell>();
		this.imports = new HashMap<String, Name>();
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		
		
		for (Object mod : node.modifiers())
		{
			if (mod instanceof MarkerAnnotation)
			{
				if (((MarkerAnnotation)mod).getTypeName().toString().equals("Factory"))
					return false;
				
			}
		}
		return true;
	}
	
	
	@Override  
	public boolean visit(PackageDeclaration node) {
		this.packageDec = node.getName();
		return true;
	}
	

	@Override  
	public boolean visit(ImportDeclaration node) {
		
		Name importName = node.getName();
		
		String key;
		if (importName instanceof QualifiedName)
			key = ((QualifiedName)importName).getName().toString();
		else
			key = ((SimpleName)importName).toString();
		
		this.imports.put(key, importName);
		return true;
	}
	
	@Override  
	public boolean visit(ClassInstanceCreation node) {
		
		//Detecta news
		//Excluir os que são new de classes do java
		
		Type type = node.getType();
		SimpleType simpleType = getSimpleType(type);
		
		String st = simpleType.getName().getFullyQualifiedName();
		
		Name importName = this.imports.get(st);
		
		if (importName == null) {
			
			AST ast = AST.newAST(AST.JLS3);
			
				importName = ast.newQualifiedName(ast.newSimpleName(this.packageDec.getFullyQualifiedName()), ast.newSimpleName(st));

		}
		
		if (this.projectObjects.contains(st)) {
			int lineNumber = this.classInformation.compilationUnit.getLineNumber(node.getStartPosition());
			DependencyCreationSmell smell = new DependencyCreationSmell(SmellTypes.InstanceCreation, this.classInformation.iCompilationUnit.getElementName(), 
					this.currentMethod, this.classInformation.iCompilationUnit.getElementName() + ".java", st, importName, 
					lineNumber, this.classInformation.compilationUnit, this.classInformation.iCompilationUnit, node);
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
	
	public List<DependencyCreationSmell> getDetectedSmells() {
		return this.detectedSmells;
	}
	
	public Name getPackage() {
		return packageDec;
	}
}
