package peifedorentos.refactor.dependencyCreator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import peifedorentos.refactor.RefactorHelper;

public class DependencyCreationVisitor extends ASTVisitor {
	private String objectName;
	private ASTRewrite astRw;
	private RefactorHelper helper;
	private String factoryVarName = "factory";
	
	public DependencyCreationVisitor(String objectName, ASTRewrite rw, RefactorHelper helper, String factoryVarName)
	{
		this.objectName = objectName;
		this.astRw = rw;
		this.helper = helper;
		this.factoryVarName = factoryVarName;
	
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {
		
		if (node.getType().toString().equals(objectName)) 
		{
			VariableDeclarationFragment newVarDecFrag = changeVariableDeclarationFragment(node);
			if (newVarDecFrag == null) {
				Assignment ass = helper.GetAssignmentParent(node);
				MethodInvocation methodInv = helper.CreateMethodInvocation(factoryVarName, "CreateInstance");
				
				ListRewrite lrw = astRw.getListRewrite(methodInv, MethodInvocation.ARGUMENTS_PROPERTY);
				
				ASTNode lastNode = null; 
				for (Object o : node.arguments()) {
					if (o instanceof Name) {
						Name name = astRw.getAST().newName(((Name) o).getFullyQualifiedName());
						
						if (lastNode == null)
						{
							lrw.insertFirst(name, null);
						}
						else
						{
							lrw.insertAfter(lastNode, name, null);
						}
						
						lastNode = name;
					}
					
				}
				
				astRw.replace(ass.getRightHandSide(), methodInv, null);
				
			}
			else
			{		
				astRw.replace(helper.GetVariableDeclariationFragmentParent(node), newVarDecFrag, null);
			}
		}
		return true;
	}
	
	private VariableDeclarationFragment changeVariableDeclarationFragment(ASTNode node) 
	{
		//ERRO AQUI!!!
		
		
		
		VariableDeclarationFragment oldVarDecFrag = helper.GetVariableDeclariationFragmentParent(node);
		
		if (oldVarDecFrag == null) {
			
			return null;
			
		}
		else
		{
			VariableDeclarationFragment newVarDecFrag = helper.CreateVariableDeclarationFragment(oldVarDecFrag);
			MethodInvocation methodInv = helper.CreateMethodInvocation(factoryVarName, "CreateInstance");
			newVarDecFrag.setInitializer(methodInv);
			return newVarDecFrag;
		}
		
		
		
	}
	

}
