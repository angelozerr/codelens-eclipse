package org.eclipse.codelens.editors;

import org.eclipse.jface.text.provisional.codelens.CodeLensStrategy;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class AbstractCodeLensController implements ICodeLensController {

	private final ITextEditor textEditor;
	private CodeLensStrategy strategy;

	public AbstractCodeLensController(ITextEditor textEditor) {
		this.textEditor = textEditor;
		strategy = new CodeLensStrategy(createContext(textEditor));
	}

	public void addTarget(String target) {
		strategy.addTarget(target);
	}

	protected EditorCodeLensContext createContext(ITextEditor textEditor) {
		return new EditorCodeLensContext(textEditor);
	}

	protected CodeLensStrategy getStrategy() {
		return strategy;
	}

	protected ITextEditor getTextEditor() {
		return textEditor;
	}
}
