package peifedorentos.refactor.dependencyCreator;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import peifedorentos.refactor.RefactorHelper;

public class MethodCallerAddParameterVisitor extends ASTVisitor {

	
	private String methodName;
	private ASTRewrite astRw;
	private RefactorHelper helper;
	private boolean firstOccurrence = true;
	private boolean outOfMethod = false;
	private String varName = "InstanceFactory";
	private String factoryName;
	
	public MethodCallerAddParameterVisitor(String methodName, String factoryName, ASTRewrite astRw, RefactorHelper helper) {
		this.methodName = methodName;
		this.astRw = astRw;
		this.helper = helper;
		this.factoryName = factoryName;
		this.varName = factoryName + this.varName;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		
		if (firstOccurrence)
		{
			System.out.println("Create instance");
			firstOccurrence = false;
		}
		
		
		
		if (node.getName().toString().equals(methodName)) {
		
			MethodInvocation newMethod = helper.CopyMethodInvocation(node, varName);
			astRw.replace(node, newMethod, null);
			//Instanciate a new factory instance and pass it as parameter
			//Change input params
			//helper.CreateMethodInvocation(node.getName().toString(), methodName)
//			DependencyCreationRefactoring ref = new DependencyCreationRefactoring(null);
//			node.accept(ref);
		}
		
		return true;
		
	}
	
	@Override 
	public boolean visit(ClassInstanceCreation node) {
		if (firstOccurrence)
		{
			System.out.println("Create instance");
			firstOccurrence = false;
		}
		
		if (node.getType().toString().equals(methodName)) {
			
			ClassInstanceCreation newMethod = helper.CopyClassInstanceCreation(node, varName);
			astRw.replace(node, newMethod, null);
		}
		
		return true;
	}
	
	
	
	@Override
	public boolean visit(MethodDeclaration node) {
		firstOccurrence = true;
		return true;
	}
}