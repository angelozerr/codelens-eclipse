package org.eclipse.codelens.editors;

import org.eclipse.jface.text.provisional.codelens.ICodeLensContext;
import org.eclipse.ui.texteditor.ITextEditor;

public interface IEditorCodeLensContext extends ICodeLensContext {

	ITextEditor getTextEditor();
}
