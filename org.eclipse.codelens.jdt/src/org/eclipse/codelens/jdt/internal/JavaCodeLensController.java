package org.eclipse.codelens.jdt.internal;

import java.lang.reflect.Method;

import org.eclipse.codelens.editors.AbstractCodeLensController;
import org.eclipse.codelens.editors.EditorCodeLensContext;
import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener;
import org.eclipse.jface.text.provisional.codelens.CodeLensStrategy;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaCodeLensController extends AbstractCodeLensController implements IJavaReconcilingListener {

	private IProgressMonitor monitor;
	private boolean listenerAdded;

	public JavaCodeLensController(CompilationUnitEditor textEditor) {
		super((ITextEditor )textEditor);
		super.addTarget("java.codelens");
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
		getStrategy().setProgressMonitor(monitor);
	}

	@Override
	public void uninstall() {
		if (monitor != null) {
			monitor.setCanceled(true);
		}
		removeReconcileListener((CompilationUnitEditor) getTextEditor());
		getStrategy().dispose();
	}

	@Override
	public void refresh() {
		getStrategy().initialReconcile();
		if (!listenerAdded) {
			addReconcileListener((CompilationUnitEditor) getTextEditor());
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

	@Override
	public void install() {
		// TODO Auto-generated method stub
		
	}
}
