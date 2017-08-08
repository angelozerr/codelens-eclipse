package org.eclipse.codelens.editors.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.provisional.codelens.CodeLensStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
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

	private Map<IEditorPart, MonoReconciler> fAsYouTypeValidators = new HashMap<>();

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
				CodeLensStrategy strategy = new CodeLensStrategy(new EditorCodeLensContext((ITextEditor) part));
				// TODO: manage target with extension point.
				strategy.addTarget("java.codelens");
				MonoReconciler reconciler = new MonoReconciler(strategy, false);
				reconciler.setProgressMonitor(new NullProgressMonitor());
				reconciler.setDelay(500);
				reconciler.install(textViewer);
				fAsYouTypeValidators.put(part, reconciler);
			}
		}
	}

	private void editorClosed(IEditorPart part) {
		if (part instanceof ITextEditor) {
			MonoReconciler processor = fAsYouTypeValidators.remove(part);
			if (processor != null) {
				processor.uninstall();
				Assert.isTrue(null == fAsYouTypeValidators.get(part),
						"An old TypeScriptDocumentRegionProcessor is not un-installed on Java Editor instance");
			}
		}
	}

}
