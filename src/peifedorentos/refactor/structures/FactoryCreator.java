package peifedorentos.refactor.structures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;


public class FactoryCreator {
	

	private AST ast;
	
	public void CreateFactory(String packageName, String className, String typeName) {
		ast = AST.newAST(AST.JLS3);
		CompilationUnit unit = ast.newCompilationUnit();
	
		//Set package
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(composeName(packageName));
		unit.setPackage(packageDeclaration);
		
		//Imports
//		ImportDeclaration importDeclaration = ast.newImportDeclaration();
//		QualifiedName name = 
//		    ast.newQualifiedName(
//		    ast.newSimpleName("java"),
//		    ast.newSimpleName("util"));
//		importDeclaration.setName(name);
//		importDeclaration.setOnDemand(true);
//		unit.imports().add(importDeclaration);
		
		//Type dec
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setInterface(false);
		type.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		type.setName(ast.newSimpleName(className));
		
		unit.types().add(type);
		
		MethodDeclaration method = ast.newMethodDeclaration();
		method.setConstructor(false);
		method.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		method.setName(ast.newSimpleName("create" + typeName));
		method.setReturnType2(ast.newSimpleType(ast.newSimpleName(typeName)));
		
		Block body = ast.newBlock();
		ReturnStatement ret = ast.newReturnStatement();

		ClassInstanceCreation instance = ast.newClassInstanceCreation();
		instance.setType(ast.newSimpleType(ast.newSimpleName(typeName)));
		
		ret.setExpression(instance);
		body.statements().add(ret);
		
		method.setBody(body);
		
		
		
		
		//body.statements().add(body);
		
		saveFile(className + ".java");
		
	}
	
	private void saveFile(String fileName) {
	
		
	
		try {
		ASTRewrite rewriter = ASTRewrite.create(ast);
		// get the current document source
		 Document document = new Document();
		// compute the edits you have made to the compilation unit
		 TextEdit edits;

			edits = rewriter.rewriteAST();
		
		// apply the edits to the document
		edits.apply(document);
		// get the new source
		String newSource = document.get();
		
		newSource.toString();
//		
//		File file = new File(fileName);
//		FileWriter fw = new FileWriter(file);
//		fw.write(newSource);
//		fw.close();
//		FileUtils.writeStringToFile(File file, String newSource) 
		
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Name composeName(String name) {
		if (!name.isEmpty()) {
			String[] names = name.split("\\.", 2);
			if (names.length > 1) {
				ast.newQualifiedName(composeName(names[1]), ast.newSimpleName(names[0])); 
			}
			else
			{
				return ast.newSimpleName(names[0]);
			}
			
		}
		return null;
	}
	
}
