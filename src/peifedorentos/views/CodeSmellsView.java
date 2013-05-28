package peifedorentos.views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import peifedorentos.refactor.Refactorer;
import peifedorentos.refactor.dependencyCreator.DependencyCreationDataWizard;
import peifedorentos.refactor.dependencyCreator.DependencyCreationRefactoring;
import peifedorentos.refactor.staticCall.StaticCallDataWizard;
import peifedorentos.refactor.staticCall.StaticCallRefactoring;
import peifedorentos.refactor.structures.FactoryCreator;
import peifedorentos.refactor.ui.RefactorDataWizzard;
import peifedorentos.smelldetectors.ISmellDetector;
import peifedorentos.smelldetectors.SmellDetectorEngine;
import peifedorentos.smelldetectors.dependencyCreation.NewClassSmellDetector;
import peifedorentos.smelldetectors.dependencyCreation.StaticMethodCallSmellDetector;
import peifedorentos.smells.DependencyCreationSmell;
import peifedorentos.smells.ISmell;
import peifedorentos.smells.StaticCallSmell;
import peifedorentos.util.ActiveEditor;
/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class CodeSmellsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "peifedorentos.views.CodeSmellsView";

	private static TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public CodeSmellsView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new SmellsContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public static void update() {
		viewer.refresh(true);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CodeSmellsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				ISmell smell = (ISmell) obj;

				if (smell instanceof DependencyCreationSmell) {
					
					DependencyCreationRefactoring ref = new DependencyCreationRefactoring(smell);
					//RefactorPreview ref = new RefactorPreview(smell);
					DependencyCreationDataWizard wizard = new DependencyCreationDataWizard(ref, "PeiFedorentos");
					RefactoringWizardOpenOperation operation= new RefactoringWizardOpenOperation(wizard);
					try {
						operation.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Refactor");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				
				}
				else if (smell instanceof StaticCallSmell) {
					StaticCallRefactoring ref = new StaticCallRefactoring(smell);
					StaticCallDataWizard wizard = new StaticCallDataWizard(ref, "PeiFedorentos");
					RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
					try {
						operation.run(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Refactor");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				
				
				SmellsContentProvider.clear();
				
				ISmellDetector d1 = new NewClassSmellDetector();
				ISmellDetector d2 = new StaticMethodCallSmellDetector();
				ArrayList<ISmellDetector> detectors = new ArrayList<ISmellDetector>();
				detectors.add(d1);
				detectors.add(d2);
				
				ActiveEditor editor = new ActiveEditor();
				
				
				SmellDetectorEngine eng = new SmellDetectorEngine(detectors);
				eng.StartSmellDetection(editor.getCompilationUnitsFromProject());
				
				CodeSmellsView.update();
				
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Smells View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}