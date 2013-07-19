package peifedorentos.visitors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.swt.events.HelpEvent;

import peifedorentos.smells.StaticCallSmell;

public class StaticMethodCallRefactoringVisitor extends ASTVisitor  {

	StaticCallSmell smell;
	boolean changedAnything = false;
	String adapterName;
	String importName;
	String varName;
	ASTRewrite rewrite;
	
	
	public StaticMethodCallRefactoringVisitor(StaticCallSmell smell, String adapterName, String importName, ASTRewrite rw) {
		this.smell = smell;
		this.adapterName = adapterName;
		this.importName = importName;
		this.varName = smell.getStaticMethodName() + "Adapter";
		this.rewrite = rw;
	}

	public boolean visit(MethodDeclaration node) {
		changedAnything = false;
		return true;
	}
	
	public void endVisit(MethodDeclaration node) {
		if (changedAnything) 
		{
			ClassInstanceCreation nca = smell.getCompilationUnit().getAST().newClassInstanceCreation();
			nca.setType(smell.getCompilationUnit().getAST().newSimpleType(
					smell.getCompilationUnit().getAST().newSimpleName(this.adapterName)));
			
			VariableDeclarationFragment vdf = smell.getCompilationUnit().getAST().newVariableDeclarationFragment();
			vdf.setName(smell.getCompilationUnit().getAST().newSimpleName(this.varName));
			
			vdf.setInitializer(nca);
			
			VariableDeclarationStatement vds = smell.getCompilationUnit().getAST().newVariableDeclarationStatement(vdf);
			vds.setType(smell.getCompilationUnit().getAST().newSimpleType(
					smell.getCompilationUnit().getAST().newSimpleName(this.adapterName)));
			
			
			ListRewrite lrw = rewrite.getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
			lrw.insertFirst(vds, null);
		
		}
	}
	
	public boolean visit(MethodInvocation node) {
		
		if (node.getName().getFullyQualifiedName().equals(smell.getStaticMethodName()))
		{
			changedAnything = true;
			//Change call to a var
			MethodInvocation newInv = smell.getCompilationUnit().getAST().newMethodInvocation();
			Expression exp = smell.getCompilationUnit().getAST().newSimpleName(this.varName);
			newInv.setName(smell.getCompilationUnit().getAST().newSimpleName(node.getName().getFullyQualifiedName()));
			newInv.setExpression(exp);
			rewrite.replace(node, newInv, null);
			
		}
		
		return true;
	}
	
	
	
}
