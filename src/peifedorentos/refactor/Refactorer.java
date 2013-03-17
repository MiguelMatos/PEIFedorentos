package peifedorentos.refactor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.text.edits.TextEdit;

import peifedorentos.smells.ISmell;


public abstract class Refactorer {

	private CompilationUnit compilationUnit;
	private ISmell smell;
	private ASTRewrite unitRefactored;
	
	public Refactorer(ISmell smell) {
		this.compilationUnit = smell.getCompilationUnit();
		this.smell = smell;
		this.unitRefactored = ASTRewrite.create(this.compilationUnit.getAST());
	}
	
	public abstract Change doRefactor(IProgressMonitor monitor);
	
	protected CompilationUnit getCompilationUnit() {
		return this.compilationUnit;
	}
	
	protected ISmell getSmell() {
		return this.smell;
	}
	
	protected ASTRewrite getRewritedAST() {
		return this.unitRefactored;
	}
	

}
