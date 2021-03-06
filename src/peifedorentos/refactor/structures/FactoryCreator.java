package peifedorentos.refactor.structures;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.dom.ASTFlattener;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;

import peifedorentos.Activator;
import peifedorentos.refactor.RefactorHelper;
import peifedorentos.util.ActiveEditor;

@SuppressWarnings("restriction")
public class FactoryCreator {

	private AST ast;
	CompilationUnit unit;

	@SuppressWarnings("unchecked")
	public ICompilationUnit CreateFactory(String packageName, String className,
			String typeName, Name importName, IPackageFragmentRoot project, ASTNode originalCall, List<ASTNode> params) throws JavaModelException {
		ast = AST.newAST(AST.JLS3);
		unit = ast.newCompilationUnit();

		// Set package
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(composeName(packageName));
		unit.setPackage(packageDeclaration);

		// Imports
		 ImportDeclaration importDeclaration = ast.newImportDeclaration();
		
		// ast.newQualifiedName(
		// ast.newSimpleName("java"),
		// ast.newSimpleName("util"));
		 Name newName = null;
		 newName =  (Name) importName.copySubtree(ast, importName);

		 
		 importDeclaration.setName(newName);
		 importDeclaration.setOnDemand(false);

		 unit.imports().add(importDeclaration);
		// Type dec
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setInterface(false);
		
		MarkerAnnotation annot = ast.newMarkerAnnotation();
		annot.setTypeName(ast.newSimpleName("Factory"));
		
		type.modifiers().add(annot);
		type.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		type.setName(ast.newSimpleName(className));
		
		

		MethodDeclaration method = ast.newMethodDeclaration();
		method.setConstructor(false);
		method.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		method.setName(ast.newSimpleName("CreateInstance"));
		method.setReturnType2(ast.newSimpleType(ast.newSimpleName(typeName)));
		
		ClassInstanceCreation instance = ast.newClassInstanceCreation();
		instance.setType(ast.newSimpleType(ast.newSimpleName(typeName)));
		
		RefactorHelper helper = new RefactorHelper(ast);
		
		for (ASTNode o : params) {
			if (o instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration newVarDec = 
						helper.CreateSingleVariableDeclaration(((SingleVariableDeclaration) o).getName().toString(), ((SingleVariableDeclaration) o).getType());
				
					
				method.parameters().add(newVarDec);
				
				instance.arguments().add(ast.newSimpleName(((SingleVariableDeclaration) o).getName().toString()));
			}
			
			
		}

		Block body = ast.newBlock();
		ReturnStatement ret = ast.newReturnStatement();

		
		

		ret.setExpression(instance);
		body.statements().add(ret);

		method.setBody(body);

		type.bodyDeclarations().add(method);

		unit.types().add(type);

		ASTFlattener fl = new ASTFlattener();
		unit.accept(fl);
	
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
//		ActiveEditor ae = new ActiveEditor();
//		IJavaProject proj = (IJavaProject) ae.getProject();
//		IPackageFragmentRoot[] frags = proj.getAllPackageFragmentRoots();
//		
//		IPackageFragmentRoot root = null;
//		for(IPackageFragmentRoot frag : frags) {
//			if (frag.getKind() == IPackageFragmentRoot.K_SOURCE) {
//				root = frag;
//				break;
//			}
//		}
//		
		
//		IPackageFragment packFrag = root.createPackageFragment(packageName, true, null);
		
		ICompilationUnit cu = project.createPackageFragment("factories", true, null).createCompilationUnit(className + ".java", doc.get(), false, null);
		// body.statements().add(body);

		//saveFile(className + ".java");
		return cu;
	}

	
	
	@SuppressWarnings("restriction")
	private void saveFile(String fileName) {
		try {
			ASTFlattener fl = new ASTFlattener();
			unit.accept(fl);
		
			String code = fl.getResult();
			CodeFormatter cf = new DefaultCodeFormatter();
			TextEdit te = cf.format(CodeFormatter.K_UNKNOWN, code, 0,
					code.length(), 0, null);
			Document doc = new Document(code);
			te.apply(doc);
			
			ActiveEditor editor = new ActiveEditor();
			IProject proj = editor.getProject();
			
			//InputStream inputStream = Activator.getDefault().getBundle().getEntry(fileName).openStream();
			InputStream inputStream = new ByteArrayInputStream(doc.get().getBytes());
					
			IFolder folder = proj.getFolder("src/Factories");
			
			try {
				folder.create(true, true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			IFile file = folder.getFile(fileName);
			file.create(inputStream, false, null);

		} catch (MalformedTreeException | BadLocationException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	

	private Name composeName(String name) {
		if (!name.isEmpty()) {
			String[] names = name.split("\\.", 2);
			if (names.length > 1) {
				ast.newQualifiedName(composeName(names[1]),
						ast.newSimpleName(names[0]));
			} else {
				return ast.newSimpleName(names[0]);
			}

		}
		return null;
	}

}
