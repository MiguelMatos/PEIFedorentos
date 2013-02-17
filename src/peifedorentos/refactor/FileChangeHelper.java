package peifedorentos.refactor;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.changes.CreateCompilationUnitChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.CreatePackageChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.DeleteSourceManipulationChange;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

@SuppressWarnings({"restriction","rawtypes"})
/**
 * 
 * @author alain
 *	
 */
public class FileChangeHelper {

	/**
	 * Way to create a new package during the refactoring process.
	 * This package will be propose to the user.
	 * If the user validate the refactoring => The package will be created
	 * If the user cancel the refactoring => The package will be not created
	 */
	public Change createNewPackageChange(IPackageFragmentRoot packageRoot,
										 final String packageName){
		//
		IPackageFragment fragment=packageRoot.getPackageFragment(packageName);
		Change newPackageChange=new CreatePackageChange(fragment){
			@Override
			public String getName() {
				return super.getName()+" - "+packageName;
			}
		};
		return newPackageChange;
	}
		
	/**
	 * Way to create a new Java class during the refactoring process.
	 * This file will be propose to the user.  
	 * If the user validate the refactoring => The file will be created 
	 * If the user cancel the refactoring => The file will be not created 
	 */
	public CreateCompilationUnitChange newCu(String name, 
											 String code, 
											 IProgressMonitor monitor,
											 ICompilationUnit cu
											// WorkingCopyOwner workingCopyOwner 
											 ) 
												throws 	CoreException,	
														OperationCanceledException{
	
		CreateCompilationUnitChange newCu=null;
		ICompilationUnit copy=cu.getWorkingCopy(monitor);
//		ICompilationUnit copy=cu.findWorkingCopy(workingCopyOwner);
//		if(copy==null){
//			copy=cu.getWorkingCopy(workingCopyOwner, monitor);
//		}
		copy.getBuffer().setContents(code);
		copy.reconcile(ICompilationUnit.NO_AST,false,null, null);
		IFile file=(IFile)copy.getResource();
		newCu=new CreateCompilationUnitChange(copy, code, file.getCharset(false));
		return newCu;
	}

	/**
	 * Way to delete a file during a refactoring process
	 */
	public DeleteSourceManipulationChange deleteSource(	IPackageFragment fragment, 
														String fileName){
		ICompilationUnit unit=fragment.getCompilationUnit(fileName+".java");
		return new DeleteSourceManipulationChange(unit, true);
	}
	
	/**
	 * Way to apply some changes into a already existing docuement.
	 */
	public TextFileChange createTextFileChange(	Document document,
												CompilationUnit cu, 
												ICompilationUnit compilationUnit) 
														throws JavaModelException{
		MultiTextEdit edit= new MultiTextEdit();
		Map options=compilationUnit.getJavaProject().getOptions(true);
		TextEdit cuEdit = cu.rewrite(document, options);
		// Apply the new source code to the document 
		edit.addChild(cuEdit);
		//Get the source code.
		String newSource = document.get();
		//set the code source to the IcompilationUnit.
		compilationUnit.getBuffer().setContents(newSource);
		//Create a change
		String name=compilationUnit.getElementName();
		IFile file=(IFile) compilationUnit.getResource();
		TextFileChange change= new TextFileChange(name, file);
		change.setTextType("java");
		change.setEdit(edit);
		return change;
	}
	
}
