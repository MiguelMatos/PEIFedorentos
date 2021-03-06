package peifedorentos.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;




public class ActiveEditor {

	private IEditorPart part;
	private ITextEditor editor;
	private ITextSelection textSelect; // text selected in editor.
	private IFile file; // the current open file.
	private ICompilationUnit compilationUnit;
	private IJavaProject javaProject;
	private IProject project;


	public ActiveEditor() {


		part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		editor = (ITextEditor) part; // obtain the text editor.

		ISelection select = editor.getSelectionProvider().getSelection(); // the selected text.
		textSelect = (ITextSelection) select; // get the text selected.
		file = (IFile) part.getEditorInput().getAdapter(IFile.class); // get the file
		project = file.getProject();
		try {
			if(project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				javaProject = JavaCore.create(project);
				compilationUnit = JavaCore.createCompilationUnitFrom(file);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public IProject getProject() {
		return project;
	}
	

	public String getProjectName() {
		return javaProject.getElementName();
	}

	public String getPackageName() {
		IPackageDeclaration[] pd;
		try {
			pd = compilationUnit.getPackageDeclarations();
			if(pd.length != 0)
				return pd[0].getElementName();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}
	

	public List<String> getMethodNames() {
		List<String> methodNames = new LinkedList<String>();
		try {
			for(IType type : compilationUnit.getAllTypes())
				for (IMethod method : type.getMethods())
					methodNames.add(method.getElementName());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return methodNames;
	}

	public String getSelectedMethod() {
		try {
			for(IType type : compilationUnit.getAllTypes())
				for(IMethod method : type.getMethods()) {
					int cursorPosition = textSelect.getOffset();
					int methodStart = method.getSourceRange().getOffset();
					int methodEnd = method.getSourceRange().getOffset() + method.getSourceRange().getLength();
					if(methodStart <= cursorPosition && cursorPosition <= methodEnd)
						return method.getElementName();
				}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isInMethod() {
		return getSelectedMethod() != null ? true : false;
	}

	public String getClassName() {
		return compilationUnit.getElementName().substring(0, compilationUnit.getElementName().length() - 5);
	}


	public String getClassFilePath() {
		try {
			String outputFolder = javaProject.getOutputLocation().toOSString().substring(getProjectName().length() + 2, javaProject.getOutputLocation().toOSString().length());
			return javaProject.getResource().getLocation().toOSString() + IPath.SEPARATOR + outputFolder + IPath.SEPARATOR + getPackageName() + IPath.SEPARATOR + getClassName() + ".class";
		} catch (JavaModelException e) {
			e.printStackTrace();
			return "";
		}
	}

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}
	
	public List<ICompilationUnit> getCompilationUnitsFromProject() {
		
		ArrayList<ICompilationUnit> cus = new ArrayList<ICompilationUnit>();
		try {
			IPackageFragment[] packages = javaProject.getPackageFragments();
			
			for (IPackageFragment frag : packages)
			{
				cus.addAll(Arrays.asList(frag.getCompilationUnits()));
			}
			
			return cus;
			
		} catch (JavaModelException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
				
		
	}

}