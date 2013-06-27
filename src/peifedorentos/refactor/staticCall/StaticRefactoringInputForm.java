package peifedorentos.refactor.staticCall;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

public class StaticRefactoringInputForm extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public StaticRefactoringInputForm(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 187);
		shell.setText(getText());
		
		Combo comboAdapters = new Combo(shell, SWT.NONE);
		comboAdapters.setBounds(177, 41, 209, 23);
		
		Label lbl = new Label(shell, SWT.NONE);
		lbl.setBounds(93, 44, 78, 15);
		lbl.setText("Adapter name:");
		
		Button btnCreateNew = new Button(shell, SWT.CHECK);
		btnCreateNew.setBounds(177, 82, 93, 16);
		btnCreateNew.setText("Create new");

	}
}
