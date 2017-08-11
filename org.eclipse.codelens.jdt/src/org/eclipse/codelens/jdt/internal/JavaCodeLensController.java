package org.eclipse.codelens.jdt.internal;

import java.lang.reflect.Method;

import org.eclipse.codelens.editors.EditorCodeLensContext;
import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener;
import org.eclipse.jface.text.provisional.codelens.CodeLensStrategy;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaCodeLensController implements ICodeLensController, IJavaReconcilingListener {

	private final CompilationUnitEditor textEditor;
	private final CodeLensStrategy strategy;
	private IProgressMonitor monitor;
	private boolean listenerAdded;

	public JavaCodeLensController(CompilationUnitEditor textEditor) {
		this.textEditor = textEditor;
		strategy = new CodeLensStrategy(new EditorCodeLensContext((ITextEditor) textEditor));
		strategy.addTarget("java.codelens");
	}

	@Override
	public void install() {

		// refresh();
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
		strategy.setProgressMonitor(monitor);
	}

	@Override
	public void uninstall() {
		if (monitor != null) {
			monitor.setCanceled(true);
		}
		removeReconcileListener(textEditor);
		strategy.dispose();
	}

	@Override
	public void refresh() {
		strategy.initialReconcile();
		if (!listenerAdded) {
			addReconcileListener(textEditor);
			listenerAdded = true;
		}
	}

	@Override
	public void aboutToBeReconciled() {

	}

	@Override
	public void reconciled(CompilationUnit ast, boolean forced, IProgressMonitor progressMonitor) {
		refresh();
	}

	private void addReconcileListener(CompilationUnitEditor textEditor) {
		try {
			Method m = CompilationUnitEditor.class.getDeclaredMethod("addReconcileListener",
					IJavaReconcilingListener.class);
			m.setAccessible(true);
			m.invoke(textEditor, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void removeReconcileListener(CompilationUnitEditor textEditor) {
		try {
			Method m = CompilationUnitEditor.class.getDeclaredMethod("removeReconcileListener",
					IJavaReconcilingListener.class);
			m.setAccessible(true);
			m.invoke(textEditor, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
