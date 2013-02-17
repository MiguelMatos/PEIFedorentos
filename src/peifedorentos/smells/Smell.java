package peifedorentos.smells;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Smell implements ISmell  {
	
	
	private String className;
	private String methodName;
	private String fileName;
	private int lineNumber;
	private List<ISmell> relatedSmells;
	private SmellTypes smellType;
	private CompilationUnit compilationUnit;
	private ICompilationUnit iCompilationUnit;
	private ASTNode nodeWithSmell;
	
	public Smell(SmellTypes smellType, String className, String methodName, String fileName, int line, CompilationUnit cu, ICompilationUnit icu, ASTNode node)
	{
		this.smellType = smellType;
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = line;
		this.compilationUnit = cu;
		this.iCompilationUnit = icu;
		this.nodeWithSmell = node;
	}

	/* (non-Javadoc)
	 * @see peifedorentos.ISmell#getClassName()
	 */
	@Override
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	/* (non-Javadoc)
	 * @see peifedorentos.ISmell#getMethodName()
	 */
	@Override
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	/* (non-Javadoc)
	 * @see peifedorentos.ISmell#getFileName()
	 */
	@Override
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/* (non-Javadoc)
	 * @see peifedorentos.ISmell#getLineNumber()
	 */
	@Override
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	@Override
	public List<ISmell> getRelatedSmells() {
		return relatedSmells;
	}
	
	
	
	public void setRelatedSmells(List<ISmell> relatedSmells) {
		this.relatedSmells = relatedSmells;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(smellType);
		sb.append(" in class ");
		sb.append(className);
		sb.append(" in method ");
		sb.append(methodName);
		sb.append(" file ");
		sb.append(fileName);
		sb.append(" line ");
		sb.append(lineNumber);
		
		return sb.toString();
	
		
	}
	
	
	
	
	@Override
	public void addRelatedSmell(ISmell smell) {
		if (this.relatedSmells == null)
			this.relatedSmells = new ArrayList<ISmell>();
		
		this.relatedSmells.add(smell);
		
	}

	public SmellTypes getSmellType() {
		return smellType;
	}

	public void setSmellType(SmellTypes smellType) {
		this.smellType = smellType;
	}
	
	public CompilationUnit getCompilationUnit() {
		
		return this.compilationUnit;
	}
	
	public ASTNode getNodeWithSmell() {
		return this.nodeWithSmell;
	}

	@Override
	public ICompilationUnit getICompilationUnit() {
		return this.iCompilationUnit;
	}
	
	
	

}
