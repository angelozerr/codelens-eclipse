package org.eclipse.codelens.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.ui.texteditor.ITextEditor;

public class DefaultCodeLensController extends AbstractCodeLensController {

	private MonoReconciler reconciler;
	private ITextViewer textViewer;

	public DefaultCodeLensController(ITextEditor textEditor) {
		super(textEditor);
		reconciler = new MonoReconciler(super.getStrategy(), false);
		reconciler.setDelay(500);
	}

	@Override
	public void install() {

	}

	@Override
	public void uninstall() {
		reconciler.uninstall();
	}

	@Override
	public void refresh() {
		if (textViewer == null) {
			this.textViewer = (ITextViewer) getTextEditor().getAdapter(ITextOperationTarget.class);
			reconciler.install(textViewer);
		}
		getStrategy().initialReconcile();
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		reconciler.setProgressMonitor(monitor);
	}
}
