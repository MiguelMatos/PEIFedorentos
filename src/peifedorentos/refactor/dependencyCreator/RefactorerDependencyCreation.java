package peifedorentos.refactor.dependencyCreator;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import peifedorentos.refactor.FileChangeHelper;
import peifedorentos.refactor.RefactorHelper;
import peifedorentos.refactor.Refactorer;
import peifedorentos.refactor.structures.FactoryCreator;
import peifedorentos.smells.DependencyCreationSmell;
import peifedorentos.smells.ISmell;
import peifedorentos.util.MethodCallerFinder;

public class RefactorerDependencyCreation extends Refactorer {

	public RefactorerDependencyCreation(ISmell smell) {
		super(smell);
		
	}

	//Passo 1 OUT (Descobrir todas as chamadas ao m�todo a ser refabricado)
	//Passo 2 OUT (Criar f�brica)
	//Passo 3 IN (Alterar a lista de parametros do m�todo para receber a f�brica por parametro
	//Passo 4 OUT (Alterar as chamadas ao m�todo para enviar uma nova f�brica)
	//Passo 5 IN (Alterar a cria��o da depend�ncia para usar a fabrica)
			
	@Override
	public Change doRefactor(IProgressMonitor monitor) {
		ISmell smell = super.getSmell();
		ASTRewrite refactoredAST = super.getRewritedAST();
	//	RefactorHelper helper = new RefactorHelper(refactoredAST.getAST());
		String source;
		try {
			
			source = smell.getICompilationUnit().getSource();
		
		Document document = new Document(source);
		
		if (!(smell instanceof DependencyCreationSmell))
			return null;
		
		
		//ASTNode node = smell.getNodeWithSmell();
		
		
		//MethodDeclaration method = helper.GetMethodDeclarationParent(node);
		
		
	//	IMethodBinding binding = method.resolveBinding();
	//	IMethod javaMethod = (IMethod) binding.getJavaElement();
		
//		MethodCallerFinder mcf = new MethodCallerFinder();
//		HashSet<IMethod> callers = mcf.getCallersOf(javaMethod);

//		FactoryCreator fc = new FactoryCreator();
//		
//		
//		//Create factory
	//DependencyCreationSmell dcSmell = (DependencyCreationSmell) smell; 
	//	String factoryName = dcSmell.getClassDependencyName() + "Factory";
//		fc.CreateFactory(
//				"Factories",
//				factoryName,
//				dcSmell.getClassDependencyName(),
//				dcSmell.getClassDependencyNamespace());
		
		
		
		//method.parameters().add(helper.CreateSingleVariableDeclaration("factory" + factoryName, factoryName));
		
		
		
		//VariableDeclarationFragment frag = helper.GetVariableDeclariationFragmentParent(node);
		//MethodInvocation methodInv = helper.CreateMethodInvocation("factory" + factoryName, "CreateInstance");
		//refactoredAST.replace(frag.getInitializer(), methodInv, null);

		
		ICompilationUnit unit = smell.getICompilationUnit();
		TextEdit res = refactoredAST.rewriteAST(document, unit
				.getJavaProject().getOptions(true));

		res.apply(document);
		
		String newSource = document.get();

			
		   // update of the compilation unit
		//unit.getBuffer().setContents(newSource);

		//unit.reconcile(ICompilationUnit.NO_AST,false,null, null);
		IFile file=(IFile)unit.getResource();
	
		//FileChangeHelper fch = new FileChangeHelper();
		
		TextFileChange change= new TextFileChange(unit.getElementName(), file);		
		return change;
		//return fch.newCu("method1", source, monitor, unit);
		//return fch.createTextFileChange(document, smell.getCompilationUnit(), unit);
		} catch (MalformedTreeException | BadLocationException | OperationCanceledException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}

}
