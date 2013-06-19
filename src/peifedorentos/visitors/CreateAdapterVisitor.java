package peifedorentos.visitors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.dom.ASTFlattener;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class CreateAdapterVisitor extends ASTVisitor {

	// Package declaration (adapters)
	// Imports declaration (importar a class estática)
	// Type declaration (public class {className}Adapter)
	// Body declaration (copy all that have static modifier)

	private AST adapterAST;
	private CompilationUnit adapterCompilationUnit;
	private Name packageName;
	private Name typeName;
	private List<MethodDeclaration> methodDeclarations;

	public CreateAdapterVisitor() {
		adapterAST = AST.newAST(AST.JLS3);
		adapterCompilationUnit = adapterAST.newCompilationUnit();
		methodDeclarations = new ArrayList<MethodDeclaration>();
	}

	public boolean visit(MethodDeclaration node) {
		for (Object modifier : node.modifiers()) {
			if (modifier.toString().equals("static")) {
				methodDeclarations.add(node);
				break;
			}
		}
		return true;
	}

	public boolean visit(PackageDeclaration node) {
		this.packageName = node.getName();
		return true;
	}

	public boolean visit(TypeDeclaration node) {
		this.typeName = node.getName();
		return true;
	}

	public void endVisit(TypeDeclaration node) {
		CreateAdapter();
	}

	private void CreateAdapter() {

		// Package
		PackageDeclaration packageDeclaration = adapterAST
				.newPackageDeclaration();
		packageDeclaration.setName(adapterAST.newName("adapters"));
		adapterCompilationUnit.setPackage(packageDeclaration);

		// Imports
		ImportDeclaration importDeclaration = adapterAST.newImportDeclaration();
		String[] names = packageName.getFullyQualifiedName().split(".");
		String[] namesType = new String[names.length + 1];
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

		// Type and body
		TypeDeclaration classDec = adapterAST.newTypeDeclaration();
		classDec.setInterface(false);
		classDec.setName(adapterAST.newSimpleName(typeName
				.getFullyQualifiedName()));

		// body
		for (MethodDeclaration oldMethodDeclaration : methodDeclarations) {
			MethodDeclaration newMethodDeclaration = adapterAST
					.newMethodDeclaration();

			newMethodDeclaration.setName(adapterAST
					.newSimpleName(oldMethodDeclaration.getName()
							.getFullyQualifiedName()));
			// newMethodDeclaration.modifiers().add("public");
			newMethodDeclaration.parameters().addAll(
					oldMethodDeclaration.parameters());

			Type returnType = oldMethodDeclaration.getReturnType2();

			// ArrayType, ParameterizedType, PrimitiveType, QualifiedType,
			// SimpleType, UnionType, WildcardType
			boolean isVoid = false;
			Type t;
			if (returnType instanceof PrimitiveType) {
				if (((PrimitiveType) returnType).getPrimitiveTypeCode() == PrimitiveType.VOID) {
					isVoid = true;

				} else {
					isVoid = false;
				}

				t = adapterAST.newPrimitiveType(((PrimitiveType) returnType)
						.getPrimitiveTypeCode());
			} else {

				t = adapterAST.newSimpleType(((SimpleType) returnType)
						.getName());
			}

			newMethodDeclaration.setReturnType2(t);

			Block b = adapterAST.newBlock();

			ReturnStatement returnStatement = adapterAST.newReturnStatement();
			MethodInvocation methodInvocation = adapterAST
					.newMethodInvocation();
			SimpleName className = adapterAST.newSimpleName(typeName
					.getFullyQualifiedName());
			SimpleName methodName = adapterAST
					.newSimpleName(oldMethodDeclaration.getName()
							.getFullyQualifiedName());
			methodInvocation.setExpression(className);
			methodInvocation.setName(methodName);

			List<SimpleName> arguments = new ArrayList<SimpleName>();
			for (int a = 0; a < oldMethodDeclaration.parameters().size(); a++) {
				if (oldMethodDeclaration.parameters().get(a) instanceof SingleVariableDeclaration) {
					SingleVariableDeclaration dec = (SingleVariableDeclaration) oldMethodDeclaration
							.parameters().get(a);
					arguments.add(adapterAST.newSimpleName(dec.getName()
							.getFullyQualifiedName()));

				}
			}

			methodInvocation.arguments().addAll(arguments);
			returnStatement.setExpression(methodInvocation);
			b.statements().add(returnStatement);
			newMethodDeclaration.setBody(b);

			classDec.bodyDeclarations().add(newMethodDeclaration);

	
		}
		
		adapterCompilationUnit.types().add(classDec);

		ASTFlattener fl = new ASTFlattener();
		adapterCompilationUnit.accept(fl);

		String code = fl.getResult();
		CodeFormatter cf = new DefaultCodeFormatter();
		TextEdit te = cf.format(CodeFormatter.K_UNKNOWN, code, 0,
				code.length(), 0, null);

		Document doc = new Document(code);
		try {
			te.apply(doc);
		} catch (MalformedTreeException | BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
