package peifedorentos.util;

import java.util.Observable;


import org.eclipse.jdt.core.ICompilationUnit;




public class EditorController extends Observable {
	
	private ActiveEditor editor;

	public EditorController() {
		this.editor = new ActiveEditor();
	}
	
	public void setEditor(ActiveEditor editor) {
		this.editor = editor;
	}
	
	public ActiveEditor getActiveEditor() {
		return editor;
	}
	
	public boolean isInMethod() {
		return editor.isInMethod();
	}
	
	public String getSelectedMethod() {
		return editor.getSelectedMethod();
	}
	
	public ICompilationUnit getCompilationUnit() {
		return editor.getCompilationUnit();
	}
	
	

}
