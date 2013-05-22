package peifedorentos;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import peifedorentos.smelldetectors.ISmellDetector;
import peifedorentos.smelldetectors.SmellDetectorEngine;
import peifedorentos.smelldetectors.dependencyCreation.NewClassSmellDetector;
import peifedorentos.smelldetectors.dependencyCreation.StaticMethodCallSmellDetector;
import peifedorentos.util.ActiveEditor;
import peifedorentos.util.ClassEnumerator;

public class StartProcessAction implements IWorkbenchWindowActionDelegate {

	@SuppressWarnings("rawtypes")
	@Override
	public void run(IAction arg0) {

		
		ISmellDetector d1 = new NewClassSmellDetector();
		ISmellDetector d2 = new StaticMethodCallSmellDetector();
		ArrayList<ISmellDetector> detectors = new ArrayList<ISmellDetector>();
		detectors.add(d1);
		detectors.add(d2);
		
		ActiveEditor editor = new ActiveEditor();
		
		
		SmellDetectorEngine eng = new SmellDetectorEngine(detectors);
		eng.StartSmellDetection(editor.getCompilationUnitsFromProject());

	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub
		
	}

}
