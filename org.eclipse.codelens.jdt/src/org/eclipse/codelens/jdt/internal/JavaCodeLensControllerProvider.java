package org.eclipse.codelens.jdt.internal;

import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.codelens.editors.ICodeLensControllerFactory;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.ui.texteditor.ITextEditor;

public class JavaCodeLensControllerProvider implements ICodeLensControllerFactory {

	@Override
	public boolean isRelevant(ITextEditor textEditor) {
		return textEditor instanceof CompilationUnitEditor;
	}

	@Override
	public ICodeLensController create(ITextEditor textEditor) {
		return new JavaCodeLensController((CompilationUnitEditor) textEditor);
	}

}
