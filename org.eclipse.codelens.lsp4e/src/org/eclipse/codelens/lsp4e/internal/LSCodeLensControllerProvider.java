package org.eclipse.codelens.lsp4e.internal;

import org.eclipse.codelens.editors.DefaultCodeLensController;
import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.codelens.editors.ICodeLensControllerFactory;
import org.eclipse.ui.texteditor.ITextEditor;

public class LSCodeLensControllerProvider implements ICodeLensControllerFactory {

	@Override
	public boolean isRelevant(ITextEditor textEditor) {
		return true;
	}

	@Override
	public ICodeLensController create(ITextEditor textEditor) {
		DefaultCodeLensController controller = new DefaultCodeLensController(textEditor);
		controller.addTarget("lsp4e.codelens");
		return controller;
	}

}
