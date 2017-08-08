package org.eclipse.codelens.editors.internal;

import org.eclipse.codelens.editors.IEditorCodeLensContext;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorCodeLensContext implements IEditorCodeLensContext {

	private final ITextEditor textEditor;

	public EditorCodeLensContext(ITextEditor textEditor) {
		this.textEditor = textEditor;
	}

	@Override
	public ITextEditor getTextEditor() {
		return textEditor;
	}

	@Override
	public ITextViewer getViewer() {
		return (ITextViewer) textEditor.getAdapter(ITextOperationTarget.class);
	}

}
