package peifedorentos.smelldetectors;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import peifedorentos.smells.ISmell;
import peifedorentos.views.CodeSmellsView;
import peifedorentos.views.SmellsContentProvider;

public class SmellDetectorEngine {
	
	private List<ISmellDetector> smellDetectors;
	private SmellsContentProvider viewProvider = new SmellsContentProvider();
	
	public SmellDetectorEngine(List<ISmellDetector> smellDetectors) {
		this.smellDetectors = smellDetectors;
	}
	
	public void StartSmellDetection(List<ICompilationUnit> cus) {
		
		
		
		List<CompilationUnit> compilationUnits = parse(cus);
		
		for(ISmellDetector smellDetector : smellDetectors) {
				smellDetector.snifCode(compilationUnits);
		}
		
		for(ISmellDetector smellDetector : smellDetectors) {
			viewProvider.addSmells(smellDetector.getSmells());
			
		}
		
		CodeSmellsView.update();
	
	}
	
	private List<CompilationUnit> parse(List<ICompilationUnit> units) {
		
		ArrayList<CompilationUnit> cus = new ArrayList<CompilationUnit>();
		for (ICompilationUnit unit : units)  {
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		cus.add((CompilationUnit) parser.createAST(null));
		
		}
		
		return cus;
	}
	
}
