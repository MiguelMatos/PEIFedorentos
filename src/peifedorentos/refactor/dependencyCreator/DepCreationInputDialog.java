package peifedorentos.refactor.dependencyCreator;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class DepCreationInputDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text textNameFactory;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DepCreationInputDialog(Shell parent, int style) {
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
		shell.setSize(450, 199);
		shell.setText(getText());
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(10, 10, 424, 151);
		
		Label lblNewFactoryName = new Label(composite, SWT.NONE);
		lblNewFactoryName.setBounds(10, 22, 115, 15);
		lblNewFactoryName.setText("New Factory Name:");
		
		Label lblExistingFactory = new Label(composite, SWT.NONE);
		lblExistingFactory.setBounds(10, 58, 115, 15);
		lblExistingFactory.setText("Existing Factory:");
		
		textNameFactory = new Text(composite, SWT.BORDER);
		textNameFactory.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
			}
		});
		textNameFactory.setBounds(131, 22, 273, 21);
		
		Combo comboFactories = new Combo(composite, SWT.NONE);
		comboFactories.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
			}
		});
	
		comboFactories.setBounds(131, 58, 273, 23);
		
		Button chkUpdateAllReferences = new Button(composite, SWT.CHECK);
		chkUpdateAllReferences.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		chkUpdateAllReferences.setBounds(131, 99, 178, 16);
		chkUpdateAllReferences.setText("Update all references");
		
		
		Button chkCreateFactory = new Button(composite, SWT.CHECK);
		chkCreateFactory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		chkCreateFactory.setBounds(131, 125, 93, 16);
		chkCreateFactory.setText("Create factory");

	}
}
