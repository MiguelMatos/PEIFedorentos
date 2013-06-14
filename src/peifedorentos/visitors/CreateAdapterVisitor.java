package peifedorentos.visitors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CreateAdapterVisitor extends ASTVisitor {

	
	//Package declaration (adapters) 
	//Imports declaration (importar a class estática)
	//Type declaration (public class {className}Adapter)
	//Body declaration (copy all that have static modifier)
	
	private AST adapterAST;
	private CompilationUnit adapterCompilationUnit;
	private Name packageName;
	private Name typeName;
	
	public CreateAdapterVisitor() {
		adapterAST = AST.newAST(AST.JLS3);
		adapterCompilationUnit = adapterAST.newCompilationUnit();
		
		
	}
	
	public boolean visit(PackageDeclaration node) {
		this.packageName = node.getName();
		return true;
	}
	
	public boolean visit(TypeDeclaration node) {
		this.typeName = node.getName();
		return true;
		
	}
	
		
	private void CreateAdapter() {
		
		//Package
		PackageDeclaration packageDeclaration = adapterAST.newPackageDeclaration();
		packageDeclaration.setName(adapterAST.newName("adapters"));
		adapterCompilationUnit.setPackage(packageDeclaration);
		
		
		//Imports
		ImportDeclaration importDeclaration = adapterAST.newImportDeclaration();
		String[] names = packageName.getFullyQualifiedName().split(".");
		String[] namesType = new String[names.length +1];
		int i = 0;
		for (String s : names) {
			namesType[i] = s;
			i++;
		}
		namesType[i] = this.typeName.getFullyQualifiedName();
		Name completeName = adapterAST.newName(namesType);
		importDeclaration.setName(completeName);
		importDeclaration.setOnDemand(false);
		adapterCompilationUnit.imports().add(importDeclaration);
		
		//Type and body
		
		
		
	}
	
	
	

	
	
}
