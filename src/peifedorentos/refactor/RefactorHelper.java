package peifedorentos.refactor;

import java.beans.Expression;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class RefactorHelper {

	private AST ast;
	
	public RefactorHelper(AST ast) {
		this.ast = ast;
	}
	
	
	
	
	public SingleVariableDeclaration CreateSingleVariableDeclaration(String parameterName, String parameterType) {
		
		SingleVariableDeclaration var = ast.newSingleVariableDeclaration();
		var.setName((SimpleName) CreateName(parameterName));
		var.setType(CreateType(parameterType));
		
		return var;
	}
	
	public Type CreateType(Type qualifier, String typeName) {
		QualifiedType qType = ast.newQualifiedType(qualifier, (SimpleName) CreateName(typeName));
		return qType;
	}
	
	public Type CreateType(String typeName) {
		Type type = ast.newSimpleType(CreateName(typeName));
		return type;
	}
	
	public MethodInvocation CreateMethodInvocation(String instanceVariableName, String methodName) {
		
		MethodInvocation method = ast.newMethodInvocation();
		SimpleName instanceSName = ast.newSimpleName(instanceVariableName);
		SimpleName methodSName = ast.newSimpleName(methodName);
		method.setName(methodSName);
		method.setExpression(instanceSName);
		
		return method;
		
	}
	
	public MethodInvocation CopyMethodInvocation(MethodInvocation node, String varName) {
		MethodInvocation newMethod = ast.newMethodInvocation();
		
		//newMethod.setExpression(node.getExpression());
		newMethod.arguments().addAll(node.arguments());
		newMethod.arguments().add(ast.newSimpleName(varName));
		newMethod.setFlags(node.getFlags());
		newMethod.setName(node.getName());
		return newMethod;
	}
	
	public ClassInstanceCreation CopyClassInstanceCreation(ClassInstanceCreation node, String varName) {
		ClassInstanceCreation newClass = ast.newClassInstanceCreation();
		//newClass.copySubtree(ast, node);
		newClass.setExpression(node.getExpression());
		newClass.setFlags(node.getFlags());
		newClass.arguments().addAll(node.arguments());
		newClass.arguments().add(ast.newSimpleName(varName));
		newClass.setType(CreateType(node.getType().toString()));
	    newClass.typeArguments().addAll(node.typeArguments());
		return newClass;
	}
	
	public Name CreateName(String name) {
		Name simpleName = ast.newName(name);
		return simpleName;
	}
	
	public MethodDeclaration GetMethodDeclarationParent(ASTNode node)
	{
		if (node.getParent() != null) {
			if (!(node.getParent() instanceof MethodDeclaration))
				return GetMethodDeclarationParent(node.getParent());
			else
				return (MethodDeclaration) node.getParent();
		}
		return null;
	}
	
	public VariableDeclarationFragment GetVariableDeclariationFragmentParent(ASTNode node) 
	{
		if (node.getParent() != null) {
			if (!(node.getParent() instanceof VariableDeclarationFragment))
				return GetVariableDeclariationFragmentParent(node.getParent());
			else
				return (VariableDeclarationFragment) node.getParent();
		}
		
		return null;
	}




	public MethodDeclaration CreateMethodDeclaration(MethodDeclaration method) {
		
		return (MethodDeclaration) ASTNode.copySubtree(ast, method);
	}




	public VariableDeclarationFragment CreateVariableDeclarationFragment(
			VariableDeclarationFragment oldVarDecFrag) {
		return (VariableDeclarationFragment) ASTNode.copySubtree(ast, oldVarDecFrag);
		
	}
	
	public Block GetBlock(ASTNode node) {
		
		if (node instanceof Block)
		{
			return (Block)node;
		}
		else
		{
			if (node.getParent() == null) {
				return null;
			}
			else
			{
				return GetBlock(node);
			}
		}
	}
	
	
	
}
