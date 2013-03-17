package peifedorentos.refactor.dependencyCreator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import peifedorentos.refactor.RefactorHelper;

public class DependencyCreationVisitor extends ASTVisitor {
	private String objectName;
	private ASTRewrite astRw;
	private RefactorHelper helper;
	private String factoryVarName = "factory";
	
	public DependencyCreationVisitor(String objectName, ASTRewrite rw, RefactorHelper helper)
	{
		this.objectName = objectName;
		this.astRw = rw;
		this.helper = helper;
	
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {
		
		if (node.getType().toString().equals(objectName)) 
		{
			VariableDeclarationFragment newVarDecFrag = changeVariableDeclarationFragment(node);
			astRw.replace(helper.GetVariableDeclariationFragmentParent(node), newVarDecFrag, null);
		}
		return true;
	}
	
	private VariableDeclarationFragment changeVariableDeclarationFragment(ASTNode node) 
	{
		VariableDeclarationFragment oldVarDecFrag = helper.GetVariableDeclariationFragmentParent(node);
		VariableDeclarationFragment newVarDecFrag = helper.CreateVariableDeclarationFragment(oldVarDecFrag);
		MethodInvocation methodInv = helper.CreateMethodInvocation(factoryVarName + objectName, "CreateInstance");
		newVarDecFrag.setInitializer(methodInv);
		return newVarDecFrag;
	}
	

}
