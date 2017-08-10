package org.eclipse.codelens.editors;

import org.eclipse.ui.texteditor.ITextEditor;

public interface ICodeLensControllerFactory {

	boolean isRelevant(ITextEditor textEditor);
	
	ICodeLensController create(ITextEditor textEditor);
	
}
