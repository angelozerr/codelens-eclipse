package org.eclipse.codelens.lsp4e.internal;

import java.util.Collection;

import org.eclipse.codelens.editors.DefaultCodeLensController;
import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.codelens.editors.ICodeLensControllerFactory;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4e.LanguageServiceAccessor;
import org.eclipse.lsp4e.LanguageServiceAccessor.LSPDocumentInfo;
import org.eclipse.ui.texteditor.ITextEditor;

public class LSPCodeLensControllerProvider implements ICodeLensControllerFactory {

	@Override
	public boolean isRelevant(ITextEditor textEditor) {
		Collection<LSPDocumentInfo> infos = LanguageServiceAccessor.getLSPDocumentInfosFor(
				LSPEclipseUtils.getDocument(textEditor),
				capabilities -> capabilities.getCodeLensProvider() != null);
		return (!infos.isEmpty());
	}

	@Override
	public ICodeLensController create(ITextEditor textEditor) {
		DefaultCodeLensController controller = new DefaultCodeLensController(textEditor);
		controller.addTarget("lsp4e.codelens");
		return controller;
	}

}
