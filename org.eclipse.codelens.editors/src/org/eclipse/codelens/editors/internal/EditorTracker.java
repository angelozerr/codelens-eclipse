package org.eclipse.codelens.editors.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorTracker implements IWindowListener, IPageListener, IPartListener {

	private static EditorTracker INSTANCE;

	private Map<IEditorPart, ICodeLensController> codeLensControllers = new HashMap<>();

	private EditorTracker() {
		init();
	}

	public static EditorTracker getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EditorTracker();
		}
		return INSTANCE;
	}

	private void init() {
		if (PlatformUI.isWorkbenchRunning()) {
			IWorkbench workbench = CodeLensEditorPlugin.getDefault().getWorkbench();
			if (workbench != null) {
				IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
				for (IWorkbenchWindow window : windows) {
					windowOpened(window);
				}
				CodeLensEditorPlugin.getDefault().getWorkbench().addWindowListener(this);
			}
		}
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		IWorkbenchPage[] pages = window.getPages();
		for (IWorkbenchPage page : pages) {
			pageClosed(page);
		}
		window.removePageListener(this);
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		if (window.getShell() != null) {
			IWorkbenchPage[] pages = window.getPages();
			for (IWorkbenchPage page : pages) {
				pageOpened(page);
			}
			window.addPageListener(this);
		}
	}

	@Override
	public void pageActivated(IWorkbenchPage page) {
	}

	@Override
	public void pageClosed(IWorkbenchPage page) {
		IEditorReference[] rs = page.getEditorReferences();
		for (IEditorReference r : rs) {
			IEditorPart part = r.getEditor(false);
			if (part != null) {
				editorClosed(part);
			}
		}
		page.removePartListener(this);
	}

	@Override
	public void pageOpened(IWorkbenchPage page) {
		IEditorReference[] rs = page.getEditorReferences();
		for (IEditorReference r : rs) {
			IEditorPart part = r.getEditor(false);
			if (part != null) {
				editorOpened(part);
			}
		}
		page.addPartListener(this);
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextViewer textViewer = (ITextViewer) part.getAdapter(ITextOperationTarget.class);
			if (textViewer != null) {
				ICodeLensController controller = codeLensControllers.get(part);
				if (controller != null) {
					controller.refresh();
				}
			}
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			editorClosed((IEditorPart) part);
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			editorOpened((IEditorPart) part);
		}
	}

	private void editorOpened(IEditorPart part) {
		if (part instanceof ITextEditor) {
			ITextViewer textViewer = (ITextViewer) part.getAdapter(ITextOperationTarget.class);
			if (textViewer != null) {
				ICodeLensController controller = codeLensControllers.get(part);
				if (controller == null) {
					ITextEditor textEditor = (ITextEditor) part;
					controller = CodeLensControllerRegistry.getInstance().create(textEditor);
					if (controller != null) {
						controller.setProgressMonitor(new NullProgressMonitor());
						codeLensControllers.put(textEditor, controller);
						//controller.install();
					}
				}
			}
		}
	}

	private void editorClosed(IEditorPart part) {
		if (part instanceof ITextEditor) {
			ICodeLensController controller = codeLensControllers.remove(part);
			if (controller != null) {
				controller.uninstall();
				Assert.isTrue(null == codeLensControllers.get(part),
						"An old ICodeLensController is not un-installed on Text Editor instance");
			}
		}
	}

}
