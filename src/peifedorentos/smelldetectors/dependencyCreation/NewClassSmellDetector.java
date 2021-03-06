package peifedorentos.smelldetectors.dependencyCreation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;

import peifedorentos.smelldetectors.ClassInformation;
import peifedorentos.smelldetectors.ISmellDetector;
import peifedorentos.smells.ISmell;
import peifedorentos.util.ActiveEditor;
import peifedorentos.visitors.ClassInstanceCreationVisitor;

public class NewClassSmellDetector implements ISmellDetector {

	
	private boolean isWorking = false;
	private List<ISmell> detectedSmells = new ArrayList<ISmell>();
	private List<ClassInformation> units;


	@Override
	public void run() {
		
		this.isWorking = true;
		
		List<String> projectObjects = getProjectObjects();
		
		for (ClassInformation unit : units)
		{
			ClassInstanceCreationVisitor visitor = new ClassInstanceCreationVisitor(unit,
					projectObjects);
			
			unit.compilationUnit.accept(visitor);
			this.detectedSmells.addAll(visitor.getDetectedSmells());
		}
		
		this.isWorking = false;
	}

	@Override
	public void snifCode(List<ClassInformation> units) {
		this.units = units;
		run();
	}

	@Override
	public List<ISmell> getSmells() {
		if (!isWorking)
			return detectedSmells;
		
		return null;
	}

	@Override
	public boolean isComplete() {
		
		return !isWorking;
	}
	
	private List<String> getProjectObjects() {
		ActiveEditor editor = new ActiveEditor();
			List<ICompilationUnit> cus = editor.getCompilationUnitsFromProject();
	
		ArrayList<String> projectClasses = new ArrayList<String>();
		for (ICompilationUnit cu : cus) {
			String element = cu.getElementName();
			
			
			projectClasses.add(element.substring(0, element.indexOf('.')));
		}	
		
		return projectClasses;
		
		
	}

}
